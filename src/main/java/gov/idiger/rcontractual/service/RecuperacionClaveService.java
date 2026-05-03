package gov.idiger.rcontractual.service;

import gov.idiger.rcontractual.dto.RecuperarClaveRequestDTO;
import gov.idiger.rcontractual.dto.RestablecerClaveRequestDTO;

/**
 * Servicio para recuperacion de contraseña mediante codigo OTP.
 */
public interface RecuperacionClaveService {

    /**
     * Genera un codigo OTP y lo envia al correo registrado del usuario.
     *
     * @param request datos para identificar usuario o correo
     */
    void solicitarCodigo(RecuperarClaveRequestDTO request);

    /**
     * Valida el codigo OTP y actualiza la contraseña.
     *
     * @param request datos del codigo y nueva contraseña
     */
    void restablecerClave(RestablecerClaveRequestDTO request);
}
