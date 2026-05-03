package gov.idiger.rcontractual.service.impl;

import gov.idiger.rcontractual.dto.RecuperarClaveRequestDTO;
import gov.idiger.rcontractual.dto.RestablecerClaveRequestDTO;
import gov.idiger.rcontractual.entity.Usuario;
import gov.idiger.rcontractual.entity.UsuarioOtp;
import gov.idiger.rcontractual.exception.ReglaDeNegocioException;
import gov.idiger.rcontractual.repository.UsuarioOtpRepository;
import gov.idiger.rcontractual.repository.UsuarioRepository;
import gov.idiger.rcontractual.service.EmailService;
import gov.idiger.rcontractual.service.RecuperacionClaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Implementacion de recuperacion de contraseña mediante OTP.
 *
 * Por seguridad, la solicitud inicial no revela si el usuario/correo existe.
 * Si existe, esta activo y tiene correo registrado, se envia un codigo
 * temporal.
 */
@Service
public class RecuperacionClaveServiceImpl implements RecuperacionClaveService {

    private static final Logger log = LoggerFactory.getLogger(RecuperacionClaveServiceImpl.class);

    /** Tiempo de vigencia del codigo OTP en minutos. */
    private static final int MINUTOS_VIGENCIA_OTP = 10;

    /** Maximo de intentos permitidos por codigo OTP. */
    private static final int MAX_INTENTOS_OTP = 5;

    /** Bandera de OTP no usado. */
    private static final Integer OTP_NO_USADO = 0;

    /** Bandera de OTP usado. */
    private static final Integer OTP_USADO = 1;

    /** Politica minima: mayuscula, minuscula, numero, especial y 8 caracteres. */
    private static final Pattern CLAVE_SEGURA = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    private final UsuarioRepository usuarioRepository;
    private final UsuarioOtpRepository usuarioOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public RecuperacionClaveServiceImpl(
            UsuarioRepository usuarioRepository,
            UsuarioOtpRepository usuarioOtpRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioOtpRepository = usuarioOtpRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void solicitarCodigo(RecuperarClaveRequestDTO request) {
        String usuarioOCorreo = normalizar(request.getUsuarioOCorreo());
        log.info("Solicitud de recuperacion de clave recibida para: {}", usuarioOCorreo);

        Optional<Usuario> usuarioOpt = buscarPorUsuarioOCorreo(usuarioOCorreo);

        if (!usuarioOpt.isPresent()) {
            log.warn("Solicitud de recuperacion para usuario/correo no encontrado: {}", usuarioOCorreo);
            return;
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.estaActivo()) {
            log.warn("Solicitud de recuperacion para usuario no activo: {}", usuario.getUsername());
            return;
        }

        if (usuario.getCorreoUsuario() == null || usuario.getCorreoUsuario().trim().isEmpty()) {
            log.warn("Usuario {} no tiene correo registrado para recuperacion", usuario.getUsername());
            return;
        }

        invalidarCodigosPendientes(usuario.getIdUsuario());

        String codigo = generarCodigoOtp();

        UsuarioOtp otp = new UsuarioOtp();
        otp.setIdUsuario(usuario.getIdUsuario());
        otp.setCodigo(codigo);
        otp.setFechaCreacion(OffsetDateTime.now());
        otp.setFechaExpiracion(OffsetDateTime.now().plusMinutes(MINUTOS_VIGENCIA_OTP));
        otp.setUsado(OTP_NO_USADO);
        otp.setIntentos(0);

        usuarioOtpRepository.save(otp);

        emailService.enviarCodigoRecuperacionClave(
                usuario.getCorreoUsuario(),
                construirNombreCompleto(usuario),
                usuario.getUsername(),
                codigo);

        log.info("Codigo de recuperacion generado para usuario ID: {}", usuario.getIdUsuario());
    }

    @Override
    @Transactional(noRollbackFor = ReglaDeNegocioException.class)
    public void restablecerClave(RestablecerClaveRequestDTO request) {

        String usuarioOCorreo = normalizar(request.getUsuarioOCorreo());
        String codigo = normalizar(request.getCodigo());

        log.info("Intento de restablecimiento de clave para: {}", usuarioOCorreo);

        if (!request.getNuevaClave().equals(request.getConfirmarClave())) {
            throw new ReglaDeNegocioException(
                    "La nueva contraseña y la confirmación no coinciden");
        }

        validarClaveSegura(request.getNuevaClave());

        Usuario usuario = buscarPorUsuarioOCorreo(usuarioOCorreo)
                .orElseThrow(() -> new ReglaDeNegocioException(
                        "No fue posible validar la solicitud de recuperación"));

        if (!usuario.estaActivo()) {
            throw new ReglaDeNegocioException(
                    "Solo los usuarios activos pueden recuperar la contraseña");
        }

        UsuarioOtp otp = usuarioOtpRepository
                .findTopByIdUsuarioAndCodigoAndUsadoOrderByFechaCreacionDesc(
                        usuario.getIdUsuario(),
                        codigo,
                        OTP_NO_USADO)
                .orElse(null);

        if (otp == null) {
            registrarIntentoFallido(usuario.getIdUsuario());
            return;
        }

        if (!otp.estaVigente()) {
            otp.setUsado(OTP_USADO);
            usuarioOtpRepository.save(otp);
            throw new ReglaDeNegocioException(
                    "El código de recuperación está vencido. Solicite uno nuevo.");
        }

        if (otp.getIntentos() != null && otp.getIntentos() >= MAX_INTENTOS_OTP) {
            otp.setUsado(OTP_USADO);
            usuarioOtpRepository.save(otp);
            throw new ReglaDeNegocioException(
                    "Se superó el número máximo de intentos. Solicite un nuevo código.");
        }

        usuario.setClave(passwordEncoder.encode(request.getNuevaClave()));
        usuario.setCambioClaveRequerido(0);
        usuario.setFechaUltimoCambioClave(OffsetDateTime.now());

        otp.setUsado(OTP_USADO);

        usuarioRepository.save(usuario);
        usuarioOtpRepository.save(otp);

        emailService.enviarNotificacionCambioClave(
                usuario.getCorreoUsuario(),
                construirNombreCompleto(usuario),
                usuario.getUsername());

        log.info("Clave restablecida correctamente para usuario ID: {}", usuario.getIdUsuario());
    }

    /**
     * Registra intento fallido sobre el ultimo codigo vigente del usuario.
     */
    private void registrarIntentoFallido(Long idUsuario) {
        Optional<UsuarioOtp> otpOpt = usuarioOtpRepository
                .findTopByIdUsuarioAndUsadoOrderByFechaCreacionDesc(
                        idUsuario,
                        OTP_NO_USADO);

        if (!otpOpt.isPresent()) {
            throw new ReglaDeNegocioException(
                    "El código de recuperación no es válido. Solicite uno nuevo.");
        }

        UsuarioOtp otp = otpOpt.get();

        if (!otp.estaVigente()) {
            otp.setUsado(OTP_USADO);
            usuarioOtpRepository.save(otp);
            throw new ReglaDeNegocioException(
                    "El código de recuperación está vencido. Solicite uno nuevo.");
        }

        int intentosActuales = otp.getIntentos() != null ? otp.getIntentos() : 0;
        int intentosNuevos = intentosActuales + 1;
        otp.setIntentos(intentosNuevos);

        if (intentosNuevos >= MAX_INTENTOS_OTP) {
            otp.setUsado(OTP_USADO);
            usuarioOtpRepository.save(otp);
            throw new ReglaDeNegocioException(
                    "Se superó el número máximo de intentos. Solicite un nuevo código.");
        }

        usuarioOtpRepository.save(otp);

        throw new ReglaDeNegocioException(
                "El código de recuperación no es válido. Intento "
                        + intentosNuevos + " de " + MAX_INTENTOS_OTP + ".");
    }

    /**
     * Busca usuario por username o correo sin revelar datos al usuario final.
     */
    private Optional<Usuario> buscarPorUsuarioOCorreo(String usuarioOCorreo) {
        if (usuarioOCorreo == null || usuarioOCorreo.isEmpty()) {
            return Optional.empty();
        }

        return usuarioRepository.findAll()
                .stream()
                .filter(usuario -> coincide(usuario.getUsername(), usuarioOCorreo)
                        || coincide(usuario.getCorreoUsuario(), usuarioOCorreo))
                .findFirst();
    }

    /**
     * Invalida codigos anteriores para que solo quede vigente el ultimo codigo.
     */
    private void invalidarCodigosPendientes(Long idUsuario) {
        List<UsuarioOtp> pendientes = usuarioOtpRepository.findByIdUsuarioAndUsado(
                idUsuario,
                OTP_NO_USADO);

        for (UsuarioOtp otp : pendientes) {
            otp.setUsado(OTP_USADO);
        }

        usuarioOtpRepository.saveAll(pendientes);
    }

    private String generarCodigoOtp() {
        return String.format("%06d", secureRandom.nextInt(1000000));
    }

    private boolean coincide(String valor, String buscado) {
        return valor != null && valor.trim().equalsIgnoreCase(buscado);
    }

    private String normalizar(String valor) {
        return valor != null ? valor.trim() : "";
    }

    private void validarClaveSegura(String clave) {
        if (clave == null || !CLAVE_SEGURA.matcher(clave).matches()) {
            throw new ReglaDeNegocioException(
                    "La contraseña debe tener mínimo 8 caracteres, una mayúscula, "
                            + "una minúscula, un número y un carácter especial");
        }
    }

    private String construirNombreCompleto(Usuario usuario) {
        String nombre = usuario.getNombreUsuario() != null
                ? usuario.getNombreUsuario()
                : "";
        String apellido = usuario.getApellidoUsuario() != null
                ? usuario.getApellidoUsuario()
                : "";

        return (nombre + " " + apellido).trim();
    }
}
