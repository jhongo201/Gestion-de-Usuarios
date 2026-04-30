package gov.idiger.rcontractual.service;

import gov.idiger.rcontractual.dto.PermisoDTO;
import gov.idiger.rcontractual.dto.PermisoRequestDTO;

import java.util.List;

/**
 * Interfaz de servicio para la gestión del árbol de permisos.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public interface PermisoService {

    /**
     * Retorna el árbol completo de permisos con sus hijos anidados.
     * @return lista de permisos raíz con sus hijos
     */
    List<PermisoDTO> listarArbol();

    /**
     * Retorna la lista plana de todos los permisos.
     * @return lista de permisos sin anidar
     */
    List<PermisoDTO> listarPlano();

    /**
     * Busca un permiso por su ID.
     * @param idPermiso ID del permiso
     * @return DTO del permiso encontrado
     */
    PermisoDTO buscarPorId(Long idPermiso);

    /**
     * Crea un nuevo permiso en el árbol.
     * @param request DTO con los datos del permiso
     * @return DTO del permiso creado
     */
    PermisoDTO crear(PermisoRequestDTO request);

    /**
     * Actualiza un permiso existente.
     * @param idPermiso ID del permiso a actualizar
     * @param request   DTO con los nuevos datos
     * @return DTO del permiso actualizado
     */
    PermisoDTO actualizar(Long idPermiso, PermisoRequestDTO request);
}