package gov.idiger.rcontractual.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Value Object de sesión que implementa {@link UserDetails} de Spring Security.
 * Encapsula todos los datos del usuario autenticado que se necesitan
 * durante la sesión: credenciales, permisos, entidad y datos personales.
 *
 * Este VO es el principal en sesión — se obtiene con:
 * (UsuarioSesionVO) SecurityContextHolder.getContext()
 *                   .getAuthentication().getPrincipal()
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class UsuarioSesionVO implements UserDetails {

    private static final long serialVersionUID = 1L;

    /** ID del usuario en BD */
    private final Long idUsuario;

    /** Login del usuario */
    private final String username;

    /** Contraseña hasheada (no exponer en respuestas) */
    private final String clave;

    /** Estado del usuario: true = activo */
    private final boolean activo;

    /** ID de la entidad del usuario — controla el alcance de datos */
    private final Long idEntidad;

    /** Nombre de la entidad para mostrar en UI */
    private final String nombreEntidad;

    /** ID del rol del usuario */
    private final Long idRol;

    /** Nombre del rol */
    private final String nombreRol;

    /** Nombre completo del usuario */
    private final String nombreCompleto;

    /** Correo electrónico */
    private final String correo;

    /**
     * Authorities de Spring Security construidas desde los permisos
     * de la tabla PERMISO_ROL + PERMISOS.
     * Cada descripción de permiso se convierte en una authority.
     */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Constructor principal — construido por {@link RcontractualUserDetailsService}.
     *
     * @param idUsuario     ID del usuario en BD
     * @param username      login del usuario
     * @param clave         contraseña hasheada
     * @param activo        true si ESTADO_USUARIO = 1
     * @param idEntidad     ID de la entidad
     * @param nombreEntidad descripción de la entidad
     * @param idRol         ID del rol
     * @param nombreRol     nombre del rol
     * @param nombreCompleto nombre y apellido concatenados
     * @param correo        correo electrónico
     * @param permisos      lista de descripciones de permisos del rol
     */
    public UsuarioSesionVO(Long idUsuario,
                            String username,
                            String clave,
                            boolean activo,
                            Long idEntidad,
                            String nombreEntidad,
                            Long idRol,
                            String nombreRol,
                            String nombreCompleto,
                            String correo,
                            List<String> permisos) {
        this.idUsuario     = idUsuario;
        this.username      = username;
        this.clave         = clave;
        this.activo        = activo;
        this.idEntidad     = idEntidad;
        this.nombreEntidad = nombreEntidad;
        this.idRol         = idRol;
        this.nombreRol     = nombreRol;
        this.nombreCompleto = nombreCompleto;
        this.correo        = correo;
        /* Cada permiso se convierte en una SimpleGrantedAuthority */
        this.authorities   = permisos.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // ── Implementación de UserDetails ────────────────────────────────────────

    /**
     * Retorna las authorities del usuario.
     * Spring Security usa esto para evaluar @PreAuthorize.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /** Retorna la contraseña hasheada para que Spring Security la valide */
    @Override
    public String getPassword() { return clave; }

    /** Retorna el login del usuario */
    @Override
    public String getUsername() { return username; }

    /** La cuenta nunca expira en esta implementación */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /** La cuenta nunca se bloquea por intentos en esta implementación */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /** Las credenciales nunca expiran en esta implementación */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /** El usuario está habilitado si ESTADO_USUARIO = 1 */
    @Override
    public boolean isEnabled() { return activo; }

    // ── Getters del contexto de sesión ───────────────────────────────────────

    /** @return ID del usuario en BD */
    public Long getIdUsuario() { return idUsuario; }

    /** @return ID de la entidad del usuario */
    public Long getIdEntidad() { return idEntidad; }

    /** @return nombre de la entidad del usuario */
    public String getNombreEntidad() { return nombreEntidad; }

    /** @return ID del rol del usuario */
    public Long getIdRol() { return idRol; }

    /** @return nombre del rol del usuario */
    public String getNombreRol() { return nombreRol; }

    /** @return nombre completo del usuario */
    public String getNombreCompleto() { return nombreCompleto; }

    /** @return correo electrónico del usuario */
    public String getCorreo() { return correo; }
}