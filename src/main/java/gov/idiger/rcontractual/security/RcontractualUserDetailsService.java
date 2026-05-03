package gov.idiger.rcontractual.security;

import gov.idiger.rcontractual.entity.Permiso;
import gov.idiger.rcontractual.entity.Usuario;
import gov.idiger.rcontractual.repository.PermisoRepository;
import gov.idiger.rcontractual.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementacion de UserDetailsService que consulta usuarios en Oracle.
 *
 * Primero busca el usuario sin filtrar estado para poder diferenciar si
 * no existe, esta inactivo o esta pendiente de aprobacion.
 */
@Component
public class RcontractualUserDetailsService implements UserDetailsService {

    private static final Logger log =
            LoggerFactory.getLogger(RcontractualUserDetailsService.class);

    private final UsuarioRepository usuarioRepository;
    private final PermisoRepository permisoRepository;

    public RcontractualUserDetailsService(
            UsuarioRepository usuarioRepository,
            PermisoRepository permisoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.permisoRepository = permisoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        log.debug("Intentando autenticar usuario: {}", username);

        Usuario usuario = usuarioRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", username);
                    return new UsernameNotFoundException(
                            "Usuario o contraseña incorrectos");
                });

        validarEstadoParaLogin(usuario);

        List<Permiso> permisos = permisoRepository
                .findByRol(usuario.getRol().getIdRol());

        List<String> nombresPermisos = permisos.stream()
                .map(p -> extraerCodigoPermiso(p.getDescripcion()))
                .collect(Collectors.toList());

        log.debug("Usuario {} cargado con {} permisos: {}",
                username, nombresPermisos.size(), nombresPermisos);

        return new UsuarioSesionVO(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getClave(),
                usuario.estaActivo(),
                usuario.getEstadoUsuario(),
                usuario.debeCambiarClave(),
                usuario.getEntidad().getIdEntidad(),
                usuario.getEntidad().getDescripcion(),
                usuario.getRol().getIdRol(),
                usuario.getRol().getNombreRol(),
                construirNombreCompleto(usuario),
                usuario.getCorreoUsuario(),
                nombresPermisos
        );
    }

    /**
     * Bloquea login cuando el usuario no esta activo.
     */
    private void validarEstadoParaLogin(Usuario usuario) {
        if (usuario.estaPendiente()) {
            log.warn("Usuario pendiente de aprobacion: {}", usuario.getUsername());
            throw new DisabledException(
                    "Su solicitud de usuario está pendiente de aprobación por un administrador.");
        }

        if (usuario.estaInactivo()) {
            log.warn("Usuario inactivo: {}", usuario.getUsername());
            throw new DisabledException(
                    "El usuario está inactivo. Por favor, contacte al Administrador del sistema para solicitar la activación.");
        }
    }

    /**
     * Convierte la descripcion del permiso en la authority usada por Spring.
     */
    private String extraerCodigoPermiso(String descripcion) {
        if (descripcion == null) {
            return "";
        }
        if (descripcion.contains("Consultar")) {
            return "RC_LISTAR";
        }
        if (descripcion.contains("Exportar")) {
            return "RC_EXPORTAR";
        }
        if (descripcion.contains("Administrar")) {
            return "RC_ADMIN";
        }
        if (descripcion.equals("Reportes Contractuales")) {
            return "RC_MENU";
        }

        return descripcion.trim().toUpperCase().replace(" ", "_");
    }

    /**
     * Construye el nombre completo evitando valores null.
     */
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
