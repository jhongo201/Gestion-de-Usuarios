package gov.idiger.rcontractual.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO para cambio obligatorio de contraseña.
 *
 * Se usa cuando un usuario ingresa por primera vez o cuando el sistema
 * marca CAMBIO_CLAVE_REQUERIDO = 1.
 */
public class CambioClaveRequestDTO {

    /** Nueva contraseña digitada por el usuario. */
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String nuevaClave;

    /** Confirmación de la nueva contraseña. */
    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmarClave;

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
