package gov.idiger.rcontractual.service;

import gov.idiger.rcontractual.dto.EjecucionRequestDTO;
import gov.idiger.rcontractual.dto.EjecucionResponseDTO;

/**
 * Interfaz de servicio para la ejecución y exportación de reportes.
 * Gestiona el ciclo completo: validación de acceso, ejecución
 * de la consulta, registro de auditoría y generación del archivo
 * de exportación si aplica (CU-03, CU-04, CU-05).
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public interface EjecucionReporteService {

    /**
     * Ejecuta un reporte y retorna los resultados en memoria.
     * Flujo completo:
     * 1. Valida que el reporte esté activo y habilitado para la entidad
     * 2. Construye y ejecuta la consulta con los filtros recibidos
     * 3. Registra la ejecución en el log de auditoría (CU-05)
     * 4. Retorna los resultados como lista de mapas
     *
     * @param request   DTO con el ID del reporte y los filtros opcionales
     * @param idUsuario ID del usuario que ejecuta (tomado de la sesión)
     * @param idEntidad ID de la entidad del usuario (tomado de la sesión)
     * @param ipCliente IP del cliente para auditoría
     * @return DTO con los resultados y metadatos de la ejecución
     */
    EjecucionResponseDTO ejecutar(EjecucionRequestDTO request,
                                  Long idUsuario,
                                  Long idEntidad,
                                  String ipCliente);

    /**
     * Ejecuta un reporte y genera el archivo de exportación.
     * Extiende el flujo de {@link #ejecutar} agregando la generación
     * del archivo en el formato solicitado (CSV, XLSX, PDF, HTML).
     * Registra el formato en el log de auditoría (CU-04, CU-05).
     *
     * @param request   DTO con el ID del reporte, filtros y formato
     * @param idUsuario ID del usuario exportador (tomado de la sesión)
     * @param idEntidad ID de la entidad del usuario (tomado de la sesión)
     * @param ipCliente IP del cliente para auditoría
     * @return arreglo de bytes con el contenido del archivo generado
     */
    byte[] exportar(EjecucionRequestDTO request,
                    Long idUsuario,
                    Long idEntidad,
                    String ipCliente);
}