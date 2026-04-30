package gov.idiger.rcontractual.repository;

import gov.idiger.rcontractual.entity.ReporteContractual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link ReporteContractual}.
 * Gestiona el acceso a la tabla CONTRATOS.REPORTE_CONTRACTUAL.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Repository
public interface ReporteContractualRepository extends JpaRepository<ReporteContractual, Long> {

    /**
     * Retorna todos los reportes que están activos (ACTIVO = 1).
     *
     * @return lista de reportes activos
     */
    List<ReporteContractual> findByActivo(Integer activo);

    /**
     * Retorna los reportes activos habilitados para una entidad específica.
     * Hace join con REPORTE_CONTRACTUAL_ENTIDAD para filtrar por entidad
     * y garantizar que el usuario solo vea los reportes de su entidad.
     *
     * @param idEntidad ID de la entidad del usuario en sesión
     * @return lista de reportes activos para esa entidad
     */
    @Query("SELECT r FROM ReporteContractual r " +
           "JOIN ReporteContractualEntidad e ON e.idReporte = r.idReporte " +
           "WHERE r.activo = 1 AND e.activo = 1 AND e.idEntidad = :idEntidad")
    List<ReporteContractual> findActivosPorEntidad(@Param("idEntidad") Long idEntidad);

    /**
     * Verifica si existe un reporte activo con el nombre dado.
     * Útil para validar duplicados al crear o editar reportes.
     *
     * @param nombre nombre del reporte a verificar
     * @return true si ya existe un reporte activo con ese nombre
     */
    boolean existsByNombreAndActivo(String nombre, Integer activo);
}