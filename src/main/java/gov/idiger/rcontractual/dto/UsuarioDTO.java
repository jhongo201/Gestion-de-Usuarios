package gov.idiger.rcontractual.dto;

import java.time.OffsetDateTime;

/**
 * DTO de respuesta para la consulta de usuarios.
 * No expone la clave ni datos sensibles internos.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class UsuarioDTO {

    /** ID único del usuario */
    private Long idUsuario;

    /** Login del usuario */
    private String username;

    /** Estado: 1 = activo, 0 = inactivo */
    private Integer estadoUsuario;

    /** ID del rol asignado */
    private Long idRol;

    /** Nombre del rol asignado */
    private String nombreRol;

    /** ID de la entidad a la que pertenece */
    private Long idEntidad;

    /** Nombre de la entidad */
    private String nombreEntidad;

    /** Nombre del usuario */
    private String nombreUsuario;

    /** Apellido del usuario */
    private String apellidoUsuario;

    /** Número de documento */
    private String numDocUsu;

    /** Tipo de documento */
    private String tipoDocUsu;

    /** Correo electrónico */
    private String correoUsuario;

    /** Fecha de creación */
    private OffsetDateTime fechaCreacion;

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getEstadoUsuario() { return estadoUsuario; }
    public void setEstadoUsuario(Integer estadoUsuario) { this.estadoUsuario = estadoUsuario; }

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }

    public Long getIdEntidad() { return idEntidad; }
    public void setIdEntidad(Long idEntidad) { this.idEntidad = idEntidad; }

    public String getNombreEntidad() { return nombreEntidad; }
    public void setNombreEntidad(String nombreEntidad) { this.nombreEntidad = nombreEntidad; }

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

    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}