package gov.idiger.rcontractual.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO para solicitud publica de usuario.
 *
 * Este formulario crea usuarios en estado pendiente para que un administrador
 * revise y active la cuenta.
 */
public class SolicitudUsuarioRequestDTO {

    /** Nombre de usuario solicitado para iniciar sesion. */
    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 100, message = "El usuario no puede superar 100 caracteres")
    private String username;

    /** Contrasena inicial digitada por el solicitante. */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 200, message = "La contraseña debe tener minimo 8 caracteres")
    private String clave;

    /** Confirmacion de la contrasena inicial. */
    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmarClave;

    /** Nombre del solicitante. */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombreUsuario;

    /** Apellido del solicitante. */
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
    private String apellidoUsuario;

    /** Tipo de documento: CC, CE, PA, TI o PT. */
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 10, message = "El tipo de documento no puede superar 10 caracteres")
    private String tipoDocUsu;

    /** Numero de documento del solicitante. */
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El número de documento no puede superar 20 caracteres")
    private String numDocUsu;

    /** Correo electronico del solicitante. */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe indicar un correo válido")
    @Size(max = 200, message = "El correo no puede superar 200 caracteres")
    private String correoUsuario;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getConfirmarClave() {
        return confirmarClave;
    }

    public void setConfirmarClave(String confirmarClave) {
        this.confirmarClave = confirmarClave;
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

    public String getTipoDocUsu() {
        return tipoDocUsu;
    }

    public void setTipoDocUsu(String tipoDocUsu) {
        this.tipoDocUsu = tipoDocUsu;
    }

    public String getNumDocUsu() {
        return numDocUsu;
    }

    public void setNumDocUsu(String numDocUsu) {
        this.numDocUsu = numDocUsu;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }
}
