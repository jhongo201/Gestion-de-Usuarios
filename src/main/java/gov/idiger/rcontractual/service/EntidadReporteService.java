package gov.idiger.rcontractual.service;

import gov.idiger.rcontractual.dto.ReporteContractualDTO;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de la asignación
 * de reportes por entidad (CU-07).
 * Solo accesible por el administrador RC (permiso RC_ADMIN).
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public interface EntidadReporteService {

    /**
     * Habilita un reporte para una entidad específica.
     * Si ya existe la relación y está inactiva, la reactiva.
     * Si no existe, crea el registro en REPORTE_CONTRACTUAL_ENTIDAD.
     *
     * @param idReporte ID del reporte a habilitar
     * @param idEntidad ID de la entidad destino
     */
    void habilitar(Long idReporte, Long idEntidad);

    /**
     * Deshabilita un reporte para una entidad específica (ACTIVO = 0).
     * No elimina el registro para preservar historial.
     *
     * @param idReporte ID del reporte a deshabilitar
     * @param idEntidad ID de la entidad destino
     */
    void deshabilitar(Long idReporte, Long idEntidad);

    /**
     * Retorna la lista de reportes activos habilitados para una entidad.
     *
     * @param idEntidad ID de la entidad a consultar
     * @return lista de DTOs de reportes habilitados
     */
    List<ReporteContractualDTO> listarReportesPorEntidad(Long idEntidad);

    /**
     * Verifica si un reporte está habilitado para una entidad.
     * Usado como validación de seguridad antes de ejecutar un reporte.
     *
     * @param idReporte ID del reporte
     * @param idEntidad ID de la entidad
     * @return true si el reporte está activo para esa entidad
     */
    boolean estaHabilitado(Long idReporte, Long idEntidad);
}