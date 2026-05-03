package gov.idiger.rcontractual.dto;

import java.time.OffsetDateTime;

/**
 * DTO de respuesta para la consulta de usuarios.
 *
 * No expone la clave. Incluye campos nuevos para administrar estados,
 * solicitudes pendientes y cambio obligatorio de contraseña.
 */
public class UsuarioDTO {

    private Long idUsuario;
    private String username;

    /** 0 = Inactivo, 1 = Activo, 2 = Pendiente. */
    private Integer estadoUsuario;
    private String nombreEstadoUsuario;

    private Long idRol;
    private String nombreRol;

    private Long idEntidad;
    private String nombreEntidad;

    private String nombreUsuario;
    private String apellidoUsuario;

    private String numDocUsu;
    private String tipoDocUsu;

    private String correoUsuario;
    private OffsetDateTime fechaCreacion;

    /** 0 = No requerido, 1 = Requerido. */
    private Integer cambioClaveRequerido;

    private OffsetDateTime fechaUltimoCambioClave;
    private OffsetDateTime fechaActivacion;
    private Long idUsuActivador;

    /** True cuando el usuario esta activo. */
    private Boolean puedeIngresar;

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(Integer estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
        this.nombreEstadoUsuario = resolverNombreEstado(estadoUsuario);
        this.puedeIngresar = Integer.valueOf(1).equals(estadoUsuario);
    }

    public String getNombreEstadoUsuario() {
        return nombreEstadoUsuario;
    }

    public void setNombreEstadoUsuario(String nombreEstadoUsuario) {
        this.nombreEstadoUsuario = nombreEstadoUsuario;
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public Long getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(Long idEntidad) {
        this.idEntidad = idEntidad;
    }

    public String getNombreEntidad() {
        return nombreEntidad;
    }

    public void setNombreEntidad(String nombreEntidad) {
        this.nombreEntidad = nombreEntidad;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    public String getNumDocUsu() {
        return numDocUsu;
    }

    public void setNumDocUsu(String numDocUsu) {
        this.numDocUsu = numDocUsu;
    }

    public String getTipoDocUsu() {
        return tipoDocUsu;
    }

    public void setTipoDocUsu(String tipoDocUsu) {
        this.tipoDocUsu = tipoDocUsu;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getCambioClaveRequerido() {
        return cambioClaveRequerido;
    }

    public void setCambioClaveRequerido(Integer cambioClaveRequerido) {
        this.cambioClaveRequerido = cambioClaveRequerido;
    }

    public OffsetDateTime getFechaUltimoCambioClave() {
        return fechaUltimoCambioClave;
    }

    public void setFechaUltimoCambioClave(OffsetDateTime fechaUltimoCambioClave) {
        this.fechaUltimoCambioClave = fechaUltimoCambioClave;
    }

    public OffsetDateTime getFechaActivacion() {
        return fechaActivacion;
    }

    public void setFechaActivacion(OffsetDateTime fechaActivacion) {
        this.fechaActivacion = fechaActivacion;
    }

    public Long getIdUsuActivador() {
        return idUsuActivador;
    }

    public void setIdUsuActivador(Long idUsuActivador) {
        this.idUsuActivador = idUsuActivador;
    }

    public Boolean getPuedeIngresar() {
        return puedeIngresar;
    }

    public void setPuedeIngresar(Boolean puedeIngresar) {
        this.puedeIngresar = puedeIngresar;
    }

    /** Texto amigable para mostrar el estado en la interfaz. */
    private String resolverNombreEstado(Integer estado) {
        if (estado == null) {
            return "Desconocido";
        }

        switch (estado) {
            case 0:
                return "Inactivo";
            case 1:
                return "Activo";
            case 2:
                return "Pendiente";
            default:
                return "Desconocido";
        }
    }
}
