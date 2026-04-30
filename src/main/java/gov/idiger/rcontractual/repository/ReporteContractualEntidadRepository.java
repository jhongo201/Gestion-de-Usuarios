package gov.idiger.rcontractual.repository;

import gov.idiger.rcontractual.entity.ReporteContractualEntidad;
import gov.idiger.rcontractual.entity.ReporteContractualEntidadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link ReporteContractualEntidad}.
 * Gestiona el acceso a la tabla CONTRATOS.REPORTE_CONTRACTUAL_ENTIDAD.
 * Controla qué reportes están habilitados para cada entidad.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Repository
public interface ReporteContractualEntidadRepository
        extends JpaRepository<ReporteContractualEntidad, ReporteContractualEntidadId> {

    /**
     * Retorna todas las asignaciones activas de una entidad específica.
     * Permite saber qué reportes tiene habilitados una entidad.
     *
     * @param idEntidad ID de la entidad a consultar
     * @return lista de asignaciones activas para esa entidad
     */
    List<ReporteContractualEntidad> findByIdEntidadAndActivo(Long idEntidad, Integer activo);

    /**
     * Retorna todas las entidades que tienen habilitado un reporte específico.
     * Usado por el administrador para ver el alcance de un reporte.
     *
     * @param idReporte ID del reporte a consultar
     * @return lista de asignaciones activas para ese reporte
     */
    List<ReporteContractualEntidad> findByIdReporteAndActivo(Long idReporte, Integer activo);

    /**
     * Verifica si una entidad tiene habilitado un reporte específico.
     * Se usa antes de ejecutar un reporte para validar que el usuario
     * tiene acceso desde su entidad (control de seguridad en capa de servicio).
     *
     * @param idReporte ID del reporte a verificar
     * @param idEntidad ID de la entidad del usuario en sesión
     * @return true si el reporte está activo para esa entidad
     */
    boolean existsByIdReporteAndIdEntidadAndActivo(Long idReporte, Long idEntidad, Integer activo);

    /**
     * Activa o desactiva un reporte para una entidad específica.
     * Usado por el administrador RC (CU-07).
     *
     * @param idReporte ID del reporte a modificar
     * @param idEntidad ID de la entidad a modificar
     * @param activo    1 para habilitar, 0 para deshabilitar
     */
    @Modifying
    @Transactional
    @Query("UPDATE ReporteContractualEntidad e " +
           "SET e.activo = :activo " +
           "WHERE e.idReporte = :idReporte AND e.idEntidad = :idEntidad")
    void actualizarEstado(
            @Param("idReporte") Long idReporte,
            @Param("idEntidad") Long idEntidad,
            @Param("activo")    Integer activo);
}