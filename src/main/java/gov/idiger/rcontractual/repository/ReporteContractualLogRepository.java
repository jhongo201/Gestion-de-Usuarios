package gov.idiger.rcontractual.repository;

import gov.idiger.rcontractual.entity.ReporteContractualLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link ReporteContractualLog}.
 * Gestiona el acceso a la tabla CONTRATOS.REPORTE_CONTRACTUAL_LOG.
 * Se usa principalmente para registrar y consultar la auditoría
 * de ejecuciones y exportaciones del módulo.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Repository
public interface ReporteContractualLogRepository extends JpaRepository<ReporteContractualLog, Long> {

    /**
     * Retorna el historial de ejecuciones de un usuario específico,
     * ordenado de más reciente a más antiguo.
     *
     * @param idUsuario ID del usuario cuyo historial se consulta
     * @return lista de logs del usuario ordenada por fecha descendente
     */
    List<ReporteContractualLog> findByIdUsuarioOrderByFechaEjecucionDesc(Long idUsuario);

    /**
     * Retorna el historial de ejecuciones filtrado por entidad,
     * ordenado de más reciente a más antiguo.
     * Usado por el administrador para auditar por entidad.
     *
     * @param idEntidad ID de la entidad a auditar
     * @return lista de logs de esa entidad
     */
    List<ReporteContractualLog> findByIdEntidadOrderByFechaEjecucionDesc(Long idEntidad);

    /**
     * Retorna los logs de ejecución de un reporte específico
     * dentro de un rango de fechas.
     * Útil para el reporte de auditoría filtrado por período.
     *
     * @param idReporte ID del reporte consultado
     * @param desde     fecha de inicio del rango (inclusive)
     * @param hasta     fecha de fin del rango (inclusive)
     * @return lista de logs en el rango de fechas dado
     */
    @Query("SELECT l FROM ReporteContractualLog l " +
           "WHERE l.reporte.idReporte = :idReporte " +
           "AND l.fechaEjecucion BETWEEN :desde AND :hasta " +
           "ORDER BY l.fechaEjecucion DESC")
    List<ReporteContractualLog> findByReporteYRangoFecha(
            @Param("idReporte") Long idReporte,
            @Param("desde")     OffsetDateTime desde,
            @Param("hasta")     OffsetDateTime hasta);

    /**
     * Cuenta la cantidad de ejecuciones con error de un reporte específico.
     * Útil para monitoreo y detección de reportes con problemas recurrentes.
     *
     * @param idReporte ID del reporte a evaluar
     * @return número de ejecuciones con ESTADO = 'ERROR'
     */
    @Query("SELECT COUNT(l) FROM ReporteContractualLog l " +
           "WHERE l.reporte.idReporte = :idReporte AND l.estado = 'ERROR'")
    Long contarErroresPorReporte(@Param("idReporte") Long idReporte);
}