package gov.idiger.rcontractual.service;

import gov.idiger.rcontractual.dto.LogAuditoriaDTO;
import gov.idiger.rcontractual.entity.ReporteContractual;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Interfaz de servicio para el registro y consulta de auditoría.
 * Centraliza toda la lógica de trazabilidad del módulo (CU-05, CU-08).
 * Es invocado internamente por {@link EjecucionReporteService}
 * y expuesto al administrador RC para consultas históricas.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public interface AuditoriaService {

    /**
     * Registra una ejecución exitosa de reporte en el log de auditoría.
     * Llamado automáticamente por EjecucionReporteService al completar
     * una consulta o exportación (CU-05).
     *
     * @param reporte    entidad del reporte ejecutado
     * @param idUsuario  ID del usuario que ejecutó
     * @param idEntidad  ID de la entidad del usuario
     * @param filtrosJson filtros aplicados en formato JSON
     * @param formato    formato de exportación o null si fue consulta
     * @param ipCliente  IP del cliente
     * @param userAgent  User-Agent del navegador
     * @return ID del log generado
     */
    Long registrarEjecucionOk(ReporteContractual reporte,
                               Long idUsuario,
                               Long idEntidad,
                               String filtrosJson,
                               String formato,
                               String ipCliente,
                               String userAgent);

    /**
     * Registra una ejecución fallida en el log de auditoría.
     * Llamado automáticamente por EjecucionReporteService cuando
     * ocurre un error durante la consulta o exportación (CU-05).
     *
     * @param reporte       entidad del reporte que falló
     * @param idUsuario     ID del usuario que ejecutó
     * @param idEntidad     ID de la entidad del usuario
     * @param filtrosJson   filtros aplicados en formato JSON
     * @param formato       formato solicitado o null
     * @param ipCliente     IP del cliente
     * @param userAgent     User-Agent del navegador
     * @param mensajeError  mensaje del error capturado
     * @return ID del log generado
     */
    Long registrarEjecucionError(ReporteContractual reporte,
                                  Long idUsuario,
                                  Long idEntidad,
                                  String filtrosJson,
                                  String formato,
                                  String ipCliente,
                                  String userAgent,
                                  String mensajeError);

    /**
     * Retorna el historial de auditoría de un usuario específico (CU-08).
     *
     * @param idUsuario ID del usuario a consultar
     * @return lista de logs ordenada por fecha descendente
     */
    List<LogAuditoriaDTO> consultarPorUsuario(Long idUsuario);

    /**
     * Retorna el historial de auditoría de una entidad específica (CU-08).
     * Solo accesible por el administrador RC.
     *
     * @param idEntidad ID de la entidad a auditar
     * @return lista de logs ordenada por fecha descendente
     */
    List<LogAuditoriaDTO> consultarPorEntidad(Long idEntidad);

    /**
     * Retorna el historial de auditoría de un reporte en un rango de fechas.
     * Permite al administrador identificar picos de uso o errores
     * recurrentes en un período determinado (CU-08).
     *
     * @param idReporte ID del reporte a auditar
     * @param desde     fecha de inicio del rango
     * @param hasta     fecha de fin del rango
     * @return lista de logs en el rango dado
     */
    List<LogAuditoriaDTO> consultarPorReporteYFecha(Long idReporte,
                                                     OffsetDateTime desde,
                                                     OffsetDateTime hasta);
}