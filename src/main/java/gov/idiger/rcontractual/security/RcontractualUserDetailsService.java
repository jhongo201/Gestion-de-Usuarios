package gov.idiger.rcontractual.security;

import gov.idiger.rcontractual.entity.Permiso;
import gov.idiger.rcontractual.entity.Usuario;
import gov.idiger.rcontractual.repository.PermisoRepository;
import gov.idiger.rcontractual.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de {@link UserDetailsService} que consulta
 * la BD Oracle real (CONTRATOS.USUARIO, CONTRATOS.PERMISO_ROL,
 * CONTRATOS.PERMISOS) para autenticar usuarios.
 *
 * Flujo de autenticación:
 * 1. Spring Security llama a loadUserByUsername con el login
 * 2. Se busca el usuario activo en CONTRATOS.USUARIO
 * 3. Se cargan sus permisos desde CONTRATOS.PERMISO_ROL + PERMISOS
 * 4. Se construye un UsuarioSesionVO con todos los datos de sesión
 * 5. Spring Security valida la contraseña con BCryptPasswordEncoder
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Component
public class RcontractualUserDetailsService implements UserDetailsService {

    private static final Logger log =
        LoggerFactory.getLogger(RcontractualUserDetailsService.class);

    /** Repositorio para buscar el usuario por username */
    private final UsuarioRepository usuarioRepository;

    /** Repositorio para cargar los permisos del rol del usuario */
    private final PermisoRepository permisoRepository;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param usuarioRepository repositorio de usuarios
     * @param permisoRepository repositorio de permisos
     */
    public RcontractualUserDetailsService(
            UsuarioRepository usuarioRepository,
            PermisoRepository permisoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.permisoRepository = permisoRepository;
    }

    /**
     * Carga el usuario por su login y construye el VO de sesión.
     * Es llamado automáticamente por Spring Security en cada
     * intento de autenticación.
     *
     * @param username login ingresado por el usuario
     * @return UsuarioSesionVO con credenciales y permisos
     * @throws UsernameNotFoundException si el usuario no existe
     *         o está inactivo en la BD
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        log.debug("Intentando autenticar usuario: {}", username);

        /* Paso 1: buscar usuario activo en BD */
        Usuario usuario = usuarioRepository
                .findActivoByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado o inactivo: {}", username);
                    return new UsernameNotFoundException(
                            "Usuario no encontrado o inactivo: " + username);
                });

        /* Paso 2: cargar permisos del rol del usuario */
        List<Permiso> permisos = permisoRepository
                .findByRol(usuario.getRol().getIdRol());

        /*
         * Paso 3: extraer las descripciones de los permisos como Strings.
         * Estas se convierten en SimpleGrantedAuthority en UsuarioSesionVO.
         * Ejemplo: "RC_LISTAR", "RC_EXPORTAR", "RC_ADMIN"
         */
        List<String> nombresPermisos = permisos.stream()
                .map(p -> extraerCodigoPermiso(p.getDescripcion()))
                .collect(Collectors.toList());

        log.debug("Usuario {} autenticado con {} permisos: {}",
                username, nombresPermisos.size(), nombresPermisos);

        /* Paso 4: construir y retornar el VO de sesión */
        return new UsuarioSesionVO(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getClave(),
                usuario.getEstadoUsuario() == 1,
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
     * Extrae el código del permiso desde su descripción.
     * La descripción en BD puede ser "Reportes Contractuales - Consultar"
     * pero la authority que usa @PreAuthorize es "RC_LISTAR".
     *
     * Regla de mapeo:
     * - Si la descripción contiene "Consultar"  → RC_LISTAR
     * - Si la descripción contiene "Exportar"   → RC_EXPORTAR
     * - Si la descripción contiene "Administrar"→ RC_ADMIN
     * - Si la descripción es "Reportes Contractuales" → RC_MENU
     * - En cualquier otro caso → usa la descripción tal cual
     *
     * @param descripcion descripción del permiso en BD
     * @return código de authority para Spring Security
     */
    private String extraerCodigoPermiso(String descripcion) {
        if (descripcion == null) return "";
        if (descripcion.contains("Consultar"))   return "RC_LISTAR";
        if (descripcion.contains("Exportar"))    return "RC_EXPORTAR";
        if (descripcion.contains("Administrar")) return "RC_ADMIN";
        if (descripcion.equals("Reportes Contractuales")) return "RC_MENU";
        /* Para permisos futuros devuelve la descripción sin espacios */
        return descripcion.trim().toUpperCase().replace(" ", "_");
    }

    /**
     * Construye el nombre completo del usuario concatenando
     * nombre y apellido. Si alguno es null usa cadena vacía.
     *
     * @param usuario entidad del usuario
     * @return nombre completo formateado
     */
    private String construirNombreCompleto(Usuario usuario) {
        String nombre   = usuario.getNombreUsuario()   != null
                          ? usuario.getNombreUsuario()   : "";
        String apellido = usuario.getApellidoUsuario() != null
                          ? usuario.getApellidoUsuario() : "";
        return (nombre + " " + apellido).trim();
    }
}