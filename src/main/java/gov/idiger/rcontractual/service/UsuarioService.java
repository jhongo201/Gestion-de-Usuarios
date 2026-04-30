package gov.idiger.rcontractual.service;

import gov.idiger.rcontractual.dto.UsuarioDTO;
import gov.idiger.rcontractual.dto.UsuarioRequestDTO;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de usuarios (CRUD).
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public interface UsuarioService {

    /**
     * Lista todos los usuarios activos.
     * @return lista de usuarios activos
     */
    List<UsuarioDTO> listarActivos();

    /**
     * Lista los usuarios activos de una entidad específica.
     * @param idEntidad ID de la entidad
     * @return lista de usuarios de esa entidad
     */
    List<UsuarioDTO> listarPorEntidad(Long idEntidad);

    /**
     * Busca un usuario por su ID.
     * @param idUsuario ID del usuario
     * @return DTO del usuario encontrado
     */
    UsuarioDTO buscarPorId(Long idUsuario);

    /**
     * Crea un nuevo usuario hasheando la contraseña con BCrypt.
     * @param request DTO con los datos del nuevo usuario
     * @return DTO del usuario creado
     */
    UsuarioDTO crear(UsuarioRequestDTO request);

    /**
     * Actualiza los datos de un usuario.
     * Si request.getClave() es null no cambia la contraseña.
     * @param idUsuario ID del usuario a actualizar
     * @param request   DTO con los nuevos datos
     * @return DTO del usuario actualizado
     */
    UsuarioDTO actualizar(Long idUsuario, UsuarioRequestDTO request);

    /**
     * Desactiva un usuario (ESTADO_USUARIO = 0).
     * No elimina el registro físicamente.
     * @param idUsuario ID del usuario a desactivar
     */
    void desactivar(Long idUsuario);
}