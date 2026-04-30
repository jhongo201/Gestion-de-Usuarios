package gov.idiger.rcontractual.service;

import gov.idiger.rcontractual.dto.PermisoDTO;
import gov.idiger.rcontractual.dto.RolDTO;
import gov.idiger.rcontractual.dto.RolRequestDTO;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de roles y
 * asignación de permisos a roles.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public interface RolService {

    /**
     * Lista todos los roles con el conteo de usuarios.
     * @return lista de roles
     */
    List<RolDTO> listarTodos();

    /**
     * Busca un rol por su ID.
     * @param idRol ID del rol
     * @return DTO del rol encontrado
     */
    RolDTO buscarPorId(Long idRol);

    /**
     * Crea un nuevo rol.
     * @param request DTO con los datos del rol
     * @return DTO del rol creado
     */
    RolDTO crear(RolRequestDTO request);

    /**
     * Actualiza el nombre de un rol.
     * @param idRol   ID del rol a actualizar
     * @param request DTO con el nuevo nombre
     * @return DTO del rol actualizado
     */
    RolDTO actualizar(Long idRol, RolRequestDTO request);

    /**
     * Asigna un permiso a un rol.
     * Si ya existe la asignación no hace nada (idempotente).
     * @param idRol     ID del rol
     * @param idPermiso ID del permiso a asignar
     */
    void asignarPermiso(Long idRol, Long idPermiso);

    /**
     * Quita un permiso de un rol.
     * @param idRol     ID del rol
     * @param idPermiso ID del permiso a quitar
     */
    void quitarPermiso(Long idRol, Long idPermiso);

    /**
     * Retorna todos los permisos asignados a un rol.
     * @param idRol ID del rol
     * @return lista de permisos del rol
     */
    List<PermisoDTO> listarPermisosPorRol(Long idRol);
}