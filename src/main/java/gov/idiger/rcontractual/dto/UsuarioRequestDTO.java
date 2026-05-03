package gov.idiger.rcontractual.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DTO de entrada para crear o editar un usuario desde el modulo admin.
 *
 * Este DTO tambien acepta datos de documento para mantener un solo contrato
 * de entrada en el CRUD administrativo.
 */
public class UsuarioRequestDTO {

    /** Login unico del usuario. */
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max = 100, message = "El username debe tener entre 4 y 100 caracteres")
    private String username;

    /**
     * Contrasena en texto plano.
     * En creacion es obligatoria desde la capa de servicio.
     * En edicion puede venir vacia para conservar la clave actual.
     */
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String clave;

    /** ID del rol a asignar. */
    @NotNull(message = "El ID del rol es obligatorio")
    private Long idRol;

    /** ID de la entidad a la que pertenece. */
    @NotNull(message = "El ID de la entidad es obligatorio")
    private Long idEntidad;

    /** Nombre del usuario. */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombreUsuario;

    /** Apellido del usuario. */
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
    private String apellidoUsuario;

    /** Numero de documento. */
    @Size(max = 20, message = "El número de documento no puede superar 20 caracteres")
    private String numDocUsu;

    /**
     * Tipo de documento permitido por BD.
     * CC = Cedula de ciudadania
     * CE = Cedula de extranjeria
     * PA = Pasaporte
     * TI = Tarjeta de identidad
     * PT = PPT
     */
    @Pattern(
        regexp = "^$|CC|CE|PA|TI|PT",
        message = "El tipo de documento debe ser CC, CE, PA, TI o PT"
    )
    private String tipoDocUsu;

    /** Correo electronico. */
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 200, message = "El correo no puede superar 200 caracteres")
    private String correoUsuario;

    /**
     * Estado solicitado desde administracion.
     * 0 = Inactivo, 1 = Activo, 2 = Pendiente.
     * Si no se envia, el servicio conserva su comportamiento por defecto.
     */
    private Integer estadoUsuario;

    /**
     * Indica si debe cambiar clave al ingresar.
     * 0 = No requerido, 1 = Requerido.
     */
    private Integer cambioClaveRequerido;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = limpiar(username);
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public Long getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(Long idEntidad) {
        this.idEntidad = idEntidad;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = limpiar(nombreUsuario);
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = limpiar(apellidoUsuario);
    }

    public String getNumDocUsu() {
        return numDocUsu;
    }

    public void setNumDocUsu(String numDocUsu) {
        this.numDocUsu = limpiar(numDocUsu);
    }

    public String getTipoDocUsu() {
        return tipoDocUsu;
    }

    public void setTipoDocUsu(String tipoDocUsu) {
        this.tipoDocUsu = limpiarMayuscula(tipoDocUsu);
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = limpiarMinuscula(correoUsuario);
    }

    public Integer getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(Integer estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public Integer getCambioClaveRequerido() {
        return cambioClaveRequerido;
    }

    public void setCambioClaveRequerido(Integer cambioClaveRequerido) {
        this.cambioClaveRequerido = cambioClaveRequerido;
    }

    /** Limpia espacios sobrantes sin romper valores nulos. */
    private String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }

    /** Limpia y normaliza a mayusculas codigos cortos como tipo de documento. */
    private String limpiarMayuscula(String valor) {
        return valor == null ? null : valor.trim().toUpperCase();
    }

    /** Limpia y normaliza correo a minusculas. */
    private String limpiarMinuscula(String valor) {
        return valor == null ? null : valor.trim().toLowerCase();
    }
}
