package gov.idiger.rcontractual.service;

import gov.idiger.rcontractual.dto.SolicitudUsuarioRequestDTO;
import gov.idiger.rcontractual.dto.UsuarioDTO;
import gov.idiger.rcontractual.dto.UsuarioRequestDTO;

import java.util.List;

/**
 * Servicio para la gestion de usuarios.
 *
 * Cubre el CRUD administrativo, la solicitud publica de usuarios y las
 * operaciones de activacion, desactivacion y consulta de usuarios pendientes.
 */
public interface UsuarioService {

    /**
     * Lista todos los usuarios activos.
     */
    List<UsuarioDTO> listarActivos();

    /**
     * Lista todos los usuarios sin filtrar estado.
     */
    List<UsuarioDTO> listarTodos();

    /**
     * Lista usuarios por estado.
     * 0 = Inactivo, 1 = Activo, 2 = Pendiente.
     */
    List<UsuarioDTO> listarPorEstado(Integer estadoUsuario);

    /**
     * Lista usuarios de una entidad sin filtrar estado.
     */
    List<UsuarioDTO> listarPorEntidad(Long idEntidad);

    /**
     * Busca un usuario por su ID.
     */
    UsuarioDTO buscarPorId(Long idUsuario);

    /**
     * Cuenta usuarios pendientes de aprobacion.
     */
    long contarPendientes();

    /**
     * Crea un usuario desde el modulo administrativo.
     */
    UsuarioDTO crear(UsuarioRequestDTO request);

    /**
     * Registra una solicitud publica de usuario.
     *
     * El usuario queda en estado pendiente para revision del administrador.
     */
    UsuarioDTO solicitarUsuario(SolicitudUsuarioRequestDTO request);

    /**
     * Actualiza datos de un usuario.
     */
    UsuarioDTO actualizar(Long idUsuario, UsuarioRequestDTO request);

    /**
     * Activa un usuario pendiente o inactivo.
     *
     * @param idUsuario ID del usuario a activar
     * @param idAdmin   ID del administrador que realiza la activacion
     */
    UsuarioDTO activar(Long idUsuario, Long idAdmin);

    /**
     * Cambia la contraseña del usuario autenticado y desactiva
     * la marca de cambio obligatorio.
     *
     * @param idUsuario      ID del usuario autenticado
     * @param nuevaClave     nueva contraseña en texto plano
     * @param confirmarClave confirmación de la nueva contraseña
     */
    void cambiarClaveObligatoria(Long idUsuario, String nuevaClave, String confirmarClave);

    /**
     * Desactiva un usuario.
     */
    void desactivar(Long idUsuario);
}
