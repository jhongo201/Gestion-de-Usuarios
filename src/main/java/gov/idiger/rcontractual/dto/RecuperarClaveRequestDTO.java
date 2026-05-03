package gov.idiger.rcontractual.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO para iniciar recuperacion de contraseña.
 *
 * El usuario puede indicar su nombre de usuario o correo registrado.
 */
public class RecuperarClaveRequestDTO {

    /** Usuario o correo registrado en el sistema. */
    @NotBlank(message = "Debe indicar usuario o correo")
    @Size(max = 200, message = "El usuario o correo no puede superar 200 caracteres")
    private String usuarioOCorreo;

    public String getUsuarioOCorreo() {
        return usuarioOCorreo;
    }

    public void setUsuarioOCorreo(String usuarioOCorreo) {
        this.usuarioOCorreo = usuarioOCorreo;
    }
}
