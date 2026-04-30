package gov.idiger.rcontractual.repository;

import gov.idiger.rcontractual.entity.PermisoRol;
import gov.idiger.rcontractual.entity.PermisoRolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link PermisoRol}.
 * Gestiona la asignación de permisos a roles.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Repository
public interface PermisoRolRepository
        extends JpaRepository<PermisoRol, PermisoRolId> {

    /**
     * Retorna todos los permisos asignados a un rol.
     *
     * @param idRol ID del rol
     * @return lista de relaciones permiso-rol
     */
    List<PermisoRol> findByIdRol(Long idRol);

    /**
     * Elimina todos los permisos asignados a un rol.
     * Usado al reasignar permisos completos de un rol.
     *
     * @param idRol ID del rol a limpiar
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PermisoRol pr WHERE pr.idRol = :idRol")
    void deleteByIdRol(@Param("idRol") Long idRol);

    /**
     * Verifica si un permiso ya está asignado a un rol.
     *
     * @param idRol     ID del rol
     * @param idPermiso ID del permiso
     * @return true si ya existe la asignación
     */
    boolean existsByIdRolAndIdPermiso(Long idRol, Long idPermiso);
}