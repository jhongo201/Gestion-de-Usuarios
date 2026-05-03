package gov.idiger.rcontractual.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Value Object de sesion que implementa UserDetails de Spring Security.
 *
 * Encapsula los datos del usuario autenticado que se necesitan durante
 * la sesion: credenciales, permisos, entidad, rol y datos personales.
 */
public class UsuarioSesionVO implements UserDetails {

    private static final long serialVersionUID = 1L;

    /** ID del usuario en BD. */
    private final Long idUsuario;

    /** Login del usuario. */
    private final String username;

    /** Clave hasheada. No se debe exponer en vistas ni respuestas. */
    private final String clave;

    /** Indica si Spring Security considera habilitada la cuenta. */
    private final boolean activo;

    /**
     * Estado numerico del usuario.
     * 0 = Inactivo, 1 = Activo, 2 = Pendiente aprobacion.
     */
    private final Integer estadoUsuario;

    /** Indica si el usuario debe cambiar clave al ingresar. */
    private final boolean cambioClaveRequerido;

    /** ID de la entidad del usuario. */
    private final Long idEntidad;

    /** Nombre de la entidad del usuario. */
    private final String nombreEntidad;

    /** ID del rol del usuario. */
    private final Long idRol;

    /** Nombre del rol del usuario. */
    private final String nombreRol;

    /** Nombre completo para mostrar en la interfaz. */
    private final String nombreCompleto;

    /** Correo electronico del usuario. */
    private final String correo;

    /** Authorities construidas desde los permisos del rol. */
    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioSesionVO(Long idUsuario,
                            String username,
                            String clave,
                            boolean activo,
                            Integer estadoUsuario,
                            boolean cambioClaveRequerido,
                            Long idEntidad,
                            String nombreEntidad,
                            Long idRol,
                            String nombreRol,
                            String nombreCompleto,
                            String correo,
                            List<String> permisos) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.clave = clave;
        this.activo = activo;
        this.estadoUsuario = estadoUsuario;
        this.cambioClaveRequerido = cambioClaveRequerido;
        this.idEntidad = idEntidad;
        this.nombreEntidad = nombreEntidad;
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;

        this.authorities = permisos.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /** Spring Security usa estas authorities para @PreAuthorize. */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /** Retorna la clave hasheada para validacion interna de Spring Security. */
    @Override
    public String getPassword() {
        return clave;
    }

    /** Retorna el login del usuario. */
    @Override
    public String getUsername() {
        return username;
    }

    /** La cuenta no expira en esta implementacion. */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** La cuenta no se bloquea por intentos en esta implementacion inicial. */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** Las credenciales no expiran automaticamente en esta implementacion. */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** Solo usuarios activos quedan habilitados para Spring Security. */
    @Override
    public boolean isEnabled() {
        return activo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdEntidad() {
        return idEntidad;
    }

    public String getNombreEntidad() {
        return nombreEntidad;
    }

    public Long getIdRol() {
        return idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public Integer getEstadoUsuario() {
        return estadoUsuario;
    }

    public boolean isCambioClaveRequerido() {
        return cambioClaveRequerido;
    }

    /** Alias legible para usar desde Thymeleaf si hace falta. */
    public boolean debeCambiarClave() {
        return cambioClaveRequerido;
    }
}
