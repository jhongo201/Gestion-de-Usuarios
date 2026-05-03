package gov.idiger.rcontractual.service.impl;

import gov.idiger.rcontractual.dto.SolicitudUsuarioRequestDTO;
import gov.idiger.rcontractual.dto.UsuarioDTO;
import gov.idiger.rcontractual.dto.UsuarioRequestDTO;
import gov.idiger.rcontractual.entity.Entidad;
import gov.idiger.rcontractual.entity.Rol;
import gov.idiger.rcontractual.entity.Usuario;
import gov.idiger.rcontractual.exception.RecursoNoEncontradoException;
import gov.idiger.rcontractual.exception.ReglaDeNegocioException;
import gov.idiger.rcontractual.repository.EntidadRepository;
import gov.idiger.rcontractual.repository.RolRepository;
import gov.idiger.rcontractual.repository.UsuarioRepository;
import gov.idiger.rcontractual.service.EmailService;
import gov.idiger.rcontractual.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementacion de UsuarioService.
 *
 * Gestiona el CRUD administrativo, solicitudes publicas, estados de usuario y
 * validaciones basicas de documento/contrasena antes de persistir en Oracle.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    /** Rol por defecto para solicitudes publicas. */
    private static final String ROL_USUARIO_RC = "USUARIO_RC";

    /** Entidad por defecto IDIGER. */
    private static final Long ID_ENTIDAD_IDIGER = 1L;

    /** Politica minima: mayuscula, minuscula, numero, especial y 8 caracteres. */
    private static final Pattern CLAVE_SEGURA = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final EntidadRepository entidadRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            EntidadRepository entidadRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.entidadRepository = entidadRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarActivos() {
        log.debug("Listando usuarios activos");
        return usuarioRepository.findByEstadoUsuario(Usuario.ESTADO_ACTIVO)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        log.debug("Listando todos los usuarios");
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarPorEstado(Integer estadoUsuario) {
        validarEstado(estadoUsuario);
        log.debug("Listando usuarios por estado: {}", estadoUsuario);
        return usuarioRepository.findByEstadoUsuario(estadoUsuario)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarPorEntidad(Long idEntidad) {
        log.debug("Listando usuarios de entidad ID: {}", idEntidad);
        return usuarioRepository.findByEntidad_IdEntidad(idEntidad)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long idUsuario) {
        log.debug("Buscando usuario ID: {}", idUsuario);
        Usuario usuario = buscarEntidadUsuario(idUsuario);
        return toDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPendientes() {
        return usuarioRepository.countByEstadoUsuario(Usuario.ESTADO_PENDIENTE);
    }

    @Override
    @Transactional
    public UsuarioDTO crear(UsuarioRequestDTO request) {
        log.info("Creando usuario desde administracion: {}", request.getUsername());

        validarDuplicadosCreacion(request);
        validarClaveObligatoriaYSegura(request.getClave());
        validarDocumento(request.getTipoDocUsu(), request.getNumDocUsu());

        Rol rol = buscarRol(request.getIdRol());
        Entidad entidad = buscarEntidad(request.getIdEntidad());

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setClave(passwordEncoder.encode(request.getClave()));
        usuario.setRol(rol);
        usuario.setEntidad(entidad);
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setApellidoUsuario(request.getApellidoUsuario());
        usuario.setNumDocUsu(request.getNumDocUsu());
        usuario.setTipoDocUsu(request.getTipoDocUsu());
        usuario.setCorreoUsuario(request.getCorreoUsuario());

        usuario.setEstadoUsuario(
                request.getEstadoUsuario() != null
                        ? request.getEstadoUsuario()
                        : Usuario.ESTADO_ACTIVO);
        usuario.setCambioClaveRequerido(
                request.getCambioClaveRequerido() != null
                        ? request.getCambioClaveRequerido()
                        : 1);

        validarEstado(usuario.getEstadoUsuario());
        validarBanderaCambioClave(usuario.getCambioClaveRequerido());

        Usuario guardado = usuarioRepository.save(usuario);

        emailService.enviarNotificacionUsuarioCreadoPorAdministrador(
                guardado.getCorreoUsuario(),
                construirNombreCompleto(guardado),
                guardado.getUsername());

        log.info("Usuario creado con ID: {}", guardado.getIdUsuario());
        return toDTO(guardado);
    }

    @Override
    @Transactional
    public UsuarioDTO solicitarUsuario(SolicitudUsuarioRequestDTO request) {
        log.info("Registrando solicitud publica de usuario: {}", request.getUsername());

        if (!request.getClave().equals(request.getConfirmarClave())) {
            throw new ReglaDeNegocioException(
                    "La contraseña y la confirmación no coinciden");
        }

        validarDuplicadosSolicitud(request);
        validarClaveObligatoriaYSegura(request.getClave());
        validarDocumento(request.getTipoDocUsu(), request.getNumDocUsu());

        Rol rol = buscarRolUsuarioRc();
        Entidad entidad = buscarEntidad(ID_ENTIDAD_IDIGER);

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setClave(passwordEncoder.encode(request.getClave()));
        usuario.setRol(rol);
        usuario.setEntidad(entidad);
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setApellidoUsuario(request.getApellidoUsuario());
        usuario.setTipoDocUsu(request.getTipoDocUsu());
        usuario.setNumDocUsu(request.getNumDocUsu());
        usuario.setCorreoUsuario(request.getCorreoUsuario());
        usuario.setEstadoUsuario(Usuario.ESTADO_PENDIENTE);
        usuario.setCambioClaveRequerido(1);

        Usuario guardado = usuarioRepository.save(usuario);

        emailService.enviarNotificacionSolicitudRegistrada(
                guardado.getCorreoUsuario(),
                construirNombreCompleto(guardado),
                guardado.getUsername());

        log.info("Solicitud publica registrada con usuario ID: {}", guardado.getIdUsuario());
        return toDTO(guardado);
    }

    @Override
    @Transactional
    public UsuarioDTO actualizar(Long idUsuario, UsuarioRequestDTO request) {
        log.info("Actualizando usuario ID: {}", idUsuario);

        Usuario usuario = buscarEntidadUsuario(idUsuario);

        validarDuplicadosActualizacion(usuario, request);
        validarDocumento(request.getTipoDocUsu(), request.getNumDocUsu());

        if (!usuario.getUsername().equalsIgnoreCase(request.getUsername())) {
            usuario.setUsername(request.getUsername());
        }

        if (request.getClave() != null && !request.getClave().trim().isEmpty()) {
            validarClaveSegura(request.getClave());
            usuario.setClave(passwordEncoder.encode(request.getClave()));
            usuario.setFechaUltimoCambioClave(OffsetDateTime.now());
            log.info("Contrasena actualizada para usuario ID: {}", idUsuario);
        }

        actualizarRolSiCambio(usuario, request.getIdRol());
        actualizarEntidadSiCambio(usuario, request.getIdEntidad());

        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setApellidoUsuario(request.getApellidoUsuario());
        usuario.setNumDocUsu(request.getNumDocUsu());
        usuario.setTipoDocUsu(request.getTipoDocUsu());
        usuario.setCorreoUsuario(request.getCorreoUsuario());

        if (request.getEstadoUsuario() != null) {
            validarEstado(request.getEstadoUsuario());
            usuario.setEstadoUsuario(request.getEstadoUsuario());
        }

        if (request.getCambioClaveRequerido() != null) {
            validarBanderaCambioClave(request.getCambioClaveRequerido());
            usuario.setCambioClaveRequerido(request.getCambioClaveRequerido());
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado correctamente: {}", idUsuario);
        return toDTO(actualizado);
    }

    @Override
    @Transactional
    public UsuarioDTO activar(Long idUsuario, Long idAdmin) {
        log.info("Activando usuario ID: {} por admin ID: {}", idUsuario, idAdmin);

        Usuario usuario = buscarEntidadUsuario(idUsuario);
        usuario.setEstadoUsuario(Usuario.ESTADO_ACTIVO);
        usuario.setFechaActivacion(OffsetDateTime.now());
        usuario.setIdUsuActivador(idAdmin);
        usuario.setCambioClaveRequerido(1);

        Usuario activado = usuarioRepository.save(usuario);

        emailService.enviarNotificacionUsuarioActivado(
                activado.getCorreoUsuario(),
                construirNombreCompleto(activado),
                activado.getUsername());

        return toDTO(activado);
    }

    @Override
    @Transactional
    public void cambiarClaveObligatoria(
            Long idUsuario,
            String nuevaClave,
            String confirmarClave) {
        log.info("Cambiando clave obligatoria para usuario ID: {}", idUsuario);

        Usuario usuario = buscarEntidadUsuario(idUsuario);

        if (!usuario.estaActivo()) {
            throw new ReglaDeNegocioException(
                    "Solo los usuarios activos pueden cambiar su contraseña");
        }

        if (nuevaClave == null || nuevaClave.trim().isEmpty()) {
            throw new ReglaDeNegocioException(
                    "La nueva contraseña es obligatoria");
        }

        if (!nuevaClave.equals(confirmarClave)) {
            throw new ReglaDeNegocioException(
                    "La nueva contraseña y la confirmación no coinciden");
        }

        validarClaveSegura(nuevaClave);

        usuario.setClave(passwordEncoder.encode(nuevaClave));
        usuario.setCambioClaveRequerido(0);
        usuario.setFechaUltimoCambioClave(OffsetDateTime.now());

        Usuario actualizado = usuarioRepository.save(usuario);

        emailService.enviarNotificacionCambioClave(
                actualizado.getCorreoUsuario(),
                construirNombreCompleto(actualizado),
                actualizado.getUsername());

        log.info("Cambio obligatorio de clave completado para usuario ID: {}", idUsuario);
    }

    @Override
    @Transactional
    public void desactivar(Long idUsuario) {
        log.info("Desactivando usuario ID: {}", idUsuario);

        Usuario usuario = buscarEntidadUsuario(idUsuario);
        usuario.setEstadoUsuario(Usuario.ESTADO_INACTIVO);

        Usuario inactivado = usuarioRepository.save(usuario);

        emailService.enviarNotificacionUsuarioInactivado(
                inactivado.getCorreoUsuario(),
                construirNombreCompleto(inactivado),
                inactivado.getUsername());

        log.info("Usuario desactivado: {}", idUsuario);
    }

    private Usuario buscarEntidadUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con ID: " + idUsuario));
    }

    private Rol buscarRol(Long idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Rol no encontrado con ID: " + idRol));
    }

    private Rol buscarRolUsuarioRc() {
        return rolRepository.findAll()
                .stream()
                .filter(rol -> ROL_USUARIO_RC.equalsIgnoreCase(rol.getNombreRol()))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el rol requerido: " + ROL_USUARIO_RC));
    }

    private Entidad buscarEntidad(Long idEntidad) {
        return entidadRepository.findById(idEntidad)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Entidad no encontrada con ID: " + idEntidad));
    }

    private void actualizarRolSiCambio(Usuario usuario, Long idRol) {
        if (!usuario.getRol().getIdRol().equals(idRol)) {
            usuario.setRol(buscarRol(idRol));
        }
    }

    private void actualizarEntidadSiCambio(Usuario usuario, Long idEntidad) {
        if (!usuario.getEntidad().getIdEntidad().equals(idEntidad)) {
            usuario.setEntidad(buscarEntidad(idEntidad));
        }
    }

    private void validarDuplicadosCreacion(UsuarioRequestDTO request) {
        if (usuarioRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un usuario con el username: " + request.getUsername());
        }

        if (request.getCorreoUsuario() != null
                && !request.getCorreoUsuario().trim().isEmpty()
                && usuarioRepository.existsByCorreoUsuarioIgnoreCase(
                        request.getCorreoUsuario())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un usuario con el correo: " + request.getCorreoUsuario());
        }

        if (request.getTipoDocUsu() != null
                && request.getNumDocUsu() != null
                && usuarioRepository.existsByTipoDocUsuAndNumDocUsu(
                        request.getTipoDocUsu(), request.getNumDocUsu())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un usuario con el documento indicado");
        }
    }

    private void validarDuplicadosSolicitud(SolicitudUsuarioRequestDTO request) {
        if (usuarioRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un usuario con el username: " + request.getUsername());
        }

        if (usuarioRepository.existsByCorreoUsuarioIgnoreCase(request.getCorreoUsuario())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un usuario con el correo: " + request.getCorreoUsuario());
        }

        if (usuarioRepository.existsByTipoDocUsuAndNumDocUsu(
                request.getTipoDocUsu(), request.getNumDocUsu())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un usuario con el documento indicado");
        }
    }

    private void validarDuplicadosActualizacion(
            Usuario usuario,
            UsuarioRequestDTO request) {

        if (!usuario.getUsername().equalsIgnoreCase(request.getUsername())
                && usuarioRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un usuario con el username: " + request.getUsername());
        }

        if (request.getCorreoUsuario() != null
                && !request.getCorreoUsuario().trim().isEmpty()
                && !request.getCorreoUsuario().equalsIgnoreCase(
                        usuario.getCorreoUsuario())
                && usuarioRepository.existsByCorreoUsuarioIgnoreCase(
                        request.getCorreoUsuario())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un usuario con el correo: " + request.getCorreoUsuario());
        }

        if (request.getTipoDocUsu() != null
                && request.getNumDocUsu() != null
                && documentoCambio(usuario, request)
                && usuarioRepository.existsByTipoDocUsuAndNumDocUsu(
                        request.getTipoDocUsu(), request.getNumDocUsu())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un usuario con el documento indicado");
        }
    }

    private boolean documentoCambio(Usuario usuario, UsuarioRequestDTO request) {
        String tipoActual = usuario.getTipoDocUsu();
        String numeroActual = usuario.getNumDocUsu();

        return !equalsIgnoreCase(tipoActual, request.getTipoDocUsu())
                || !equalsIgnoreCase(numeroActual, request.getNumDocUsu());
    }

    private boolean equalsIgnoreCase(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return b != null && a.equalsIgnoreCase(b);
    }

    private void validarClaveObligatoriaYSegura(String clave) {
        if (clave == null || clave.trim().isEmpty()) {
            throw new ReglaDeNegocioException(
                    "La contraseña es obligatoria al crear un usuario");
        }
        validarClaveSegura(clave);
    }

    private void validarClaveSegura(String clave) {
        if (!CLAVE_SEGURA.matcher(clave).matches()) {
            throw new ReglaDeNegocioException(
                    "La contraseña debe tener mínimo 8 caracteres, una mayúscula, "
                            + "una minúscula, un número y un carácter especial");
        }
    }

    private void validarDocumento(String tipoDocumento, String numeroDocumento) {
        boolean tipoVacio = tipoDocumento == null || tipoDocumento.trim().isEmpty();
        boolean numeroVacio = numeroDocumento == null || numeroDocumento.trim().isEmpty();

        if (tipoVacio && numeroVacio) {
            return;
        }

        if (tipoVacio || numeroVacio) {
            throw new ReglaDeNegocioException(
                    "Debe indicar tipo y número de documento");
        }

        switch (tipoDocumento) {
            case "CC":
                validarRegex(numeroDocumento, "^[0-9]{6,10}$",
                        "La cédula de ciudadanía debe tener entre 6 y 10 dígitos");
                break;
            case "CE":
                validarRegex(numeroDocumento, "^[A-Za-z0-9]{6,12}$",
                        "La cédula de extranjería debe tener entre 6 y 12 caracteres alfanuméricos");
                break;
            case "PA":
                validarRegex(numeroDocumento, "^[A-Za-z0-9]{6,12}$",
                        "El pasaporte debe tener entre 6 y 12 caracteres alfanuméricos");
                break;
            case "TI":
                validarRegex(numeroDocumento, "^[0-9]{10,11}$",
                        "La tarjeta de identidad debe tener entre 10 y 11 dígitos");
                break;
            case "PT":
                validarRegex(numeroDocumento, "^[0-9]{6,15}$",
                        "El PPT debe tener entre 6 y 15 dígitos");
                break;
            default:
                throw new ReglaDeNegocioException(
                        "El tipo de documento debe ser CC, CE, PA, TI o PT");
        }
    }

    private void validarRegex(String valor, String regex, String mensaje) {
        if (!Pattern.matches(regex, valor)) {
            throw new ReglaDeNegocioException(mensaje);
        }
    }

    private void validarEstado(Integer estadoUsuario) {
        if (!Integer.valueOf(Usuario.ESTADO_INACTIVO).equals(estadoUsuario)
                && !Integer.valueOf(Usuario.ESTADO_ACTIVO).equals(estadoUsuario)
                && !Integer.valueOf(Usuario.ESTADO_PENDIENTE).equals(estadoUsuario)) {
            throw new ReglaDeNegocioException(
                    "El estado del usuario debe ser 0, 1 o 2");
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

    private void validarBanderaCambioClave(Integer cambioClaveRequerido) {
        if (!Integer.valueOf(0).equals(cambioClaveRequerido)
                && !Integer.valueOf(1).equals(cambioClaveRequerido)) {
            throw new ReglaDeNegocioException(
                    "El campo cambioClaveRequerido debe ser 0 o 1");
        }
    }

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setUsername(u.getUsername());
        dto.setEstadoUsuario(u.getEstadoUsuario());
        dto.setIdRol(u.getRol().getIdRol());
        dto.setNombreRol(u.getRol().getNombreRol());
        dto.setIdEntidad(u.getEntidad().getIdEntidad());
        dto.setNombreEntidad(u.getEntidad().getDescripcion());
        dto.setNombreUsuario(u.getNombreUsuario());
        dto.setApellidoUsuario(u.getApellidoUsuario());
        dto.setNumDocUsu(u.getNumDocUsu());
        dto.setTipoDocUsu(u.getTipoDocUsu());
        dto.setCorreoUsuario(u.getCorreoUsuario());
        dto.setFechaCreacion(u.getFechaCreacion());
        dto.setCambioClaveRequerido(u.getCambioClaveRequerido());
        dto.setFechaUltimoCambioClave(u.getFechaUltimoCambioClave());
        dto.setFechaActivacion(u.getFechaActivacion());
        dto.setIdUsuActivador(u.getIdUsuActivador());
        dto.setPuedeIngresar(u.estaActivo());
        return dto;
    }
}
