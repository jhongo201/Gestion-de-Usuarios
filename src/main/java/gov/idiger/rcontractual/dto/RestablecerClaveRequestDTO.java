package gov.idiger.rcontractual.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO para restablecer contraseña usando codigo OTP.
 */
public class RestablecerClaveRequestDTO {

    /** Usuario o correo usado para solicitar la recuperacion. */
    @NotBlank(message = "Debe indicar usuario o correo")
    @Size(max = 200, message = "El usuario o correo no puede superar 200 caracteres")
    private String usuarioOCorreo;

    /** Codigo OTP enviado al correo registrado. */
    @NotBlank(message = "El código de recuperación es obligatorio")
    @Size(min = 6, max = 6, message = "El código debe tener 6 dígitos")
    private String codigo;

    /** Nueva contraseña. */
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, max = 200, message = "La contraseña debe tener mínimo 8 caracteres")
    private String nuevaClave;

    /** Confirmacion de nueva contraseña. */
    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmarClave;

    public String getUsuarioOCorreo() {
        return usuarioOCorreo;
    }

    public void setUsuarioOCorreo(String usuarioOCorreo) {
        this.usuarioOCorreo = usuarioOCorreo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNuevaClave() {
        return nuevaClave;
    }

    public void setNuevaClave(String nuevaClave) {
        this.nuevaClave = nuevaClave;
    }

    public String getConfirmarClave() {
        return confirmarClave;
    }

    public void setConfirmarClave(String confirmarClave) {
        this.confirmarClave = confirmarClave;
    }
}
