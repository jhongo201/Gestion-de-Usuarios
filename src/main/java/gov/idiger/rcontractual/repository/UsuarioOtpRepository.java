package gov.idiger.rcontractual.repository;

import gov.idiger.rcontractual.entity.UsuarioOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para codigos OTP de recuperacion de contraseña.
 */
public interface UsuarioOtpRepository extends JpaRepository<UsuarioOtp, Long> {

    /**
     * Busca un codigo no usado para un usuario.
     *
     * @param idUsuario ID del usuario
     * @param codigo codigo OTP
     * @param usado bandera de uso: 0 no usado, 1 usado
     * @return OTP encontrado
     */
    Optional<UsuarioOtp> findTopByIdUsuarioAndCodigoAndUsadoOrderByFechaCreacionDesc(
            Long idUsuario,
            String codigo,
            Integer usado);

    /**
     * Busca el ultimo codigo no usado de un usuario.
     *
     * Se usa para contabilizar intentos cuando el codigo digitado es incorrecto.
     *
     * @param idUsuario ID del usuario
     * @param usado bandera de uso
     * @return ultimo OTP no usado
     */
    Optional<UsuarioOtp> findTopByIdUsuarioAndUsadoOrderByFechaCreacionDesc(
            Long idUsuario,
            Integer usado);

    /**
     * Lista codigos no usados de un usuario.
     *
     * @param idUsuario ID del usuario
     * @param usado bandera de uso
     * @return codigos encontrados
     */
    List<UsuarioOtp> findByIdUsuarioAndUsado(Long idUsuario, Integer usado);
}
