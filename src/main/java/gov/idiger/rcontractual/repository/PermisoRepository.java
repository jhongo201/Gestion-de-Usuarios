package gov.idiger.rcontractual.repository;

import gov.idiger.rcontractual.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Permiso}.
 * Gestiona el acceso a CONTRATOS.PERMISOS.
 * Usado para cargar el árbol de permisos de un rol
 * durante la autenticación.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    /**
     * Retorna todos los permisos asignados a un rol específico.
     * Usado por el UserDetailsService para construir las
     * authorities de Spring Security al autenticar.
     *
     * @param idRol ID del rol del usuario autenticado
     * @return lista de permisos del rol
     */
    @Query("SELECT p FROM Permiso p " +
           "JOIN PermisoRol pr ON pr.idPermiso = p.idPermiso " +
           "WHERE pr.idRol = :idRol " +
           "ORDER BY p.orden ASC")
    List<Permiso> findByRol(@Param("idRol") Long idRol);
}