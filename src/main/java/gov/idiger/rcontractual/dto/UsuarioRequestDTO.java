package gov.idiger.rcontractual.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO de entrada para crear o editar un usuario.
 * La contraseña solo se recibe en creación o cambio explícito.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class UsuarioRequestDTO {

    /**
     * Login único del usuario.
     * Obligatorio, entre 4 y 100 caracteres.
     */
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max = 100, message = "El username debe tener entre 4 y 100 caracteres")
    private String username;

    /**
     * Contraseña en texto plano.
     * Obligatorio solo en creación. En edición puede ser null
     * para indicar que no se cambia.
     */
    @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
    private String clave;

    /** ID del rol a asignar. Obligatorio. */
    @NotNull(message = "El ID del rol es obligatorio")
    private Long idRol;

    /** ID de la entidad a la que pertenece. Obligatorio. */
    @NotNull(message = "El ID de la entidad es obligatorio")
    private Long idEntidad;

    /** Nombre del usuario */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombreUsuario;

    /** Apellido del usuario */
    @NotBlank(message = "El apellido es obligatorio")
    private String apellidoUsuario;

    /** Número de documento */
    private String numDocUsu;

    /** Tipo de documento (CC, CE, NIT, etc.) */
    private String tipoDocUsu;

    /** Correo electrónico */
    @Email(message = "El correo no tiene un formato válido")
    private String correoUsuario;

    // ── Getters y Setters ────────────────────────────────────────────────────

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public Long getIdEntidad() { return idEntidad; }
    public void setIdEntidad(Long idEntidad) { this.idEntidad = idEntidad; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getApellidoUsuario() { return apellidoUsuario; }
    public void setApellidoUsuario(String apellidoUsuario) { this.apellidoUsuario = apellidoUsuario; }

    public String getNumDocUsu() { return numDocUsu; }
    public void setNumDocUsu(String numDocUsu) { this.numDocUsu = numDocUsu; }

    public String getTipoDocUsu() { return tipoDocUsu; }
    public void setTipoDocUsu(String tipoDocUsu) { this.tipoDocUsu = tipoDocUsu; }

    public String getCorreoUsuario() { return correoUsuario; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }
}