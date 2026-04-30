package gov.idiger.rcontractual.controller;

import gov.idiger.rcontractual.dto.EjecucionRequestDTO;
import gov.idiger.rcontractual.dto.EjecucionResponseDTO;
import gov.idiger.rcontractual.service.EjecucionReporteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controlador REST para la ejecución y exportación de reportes.
 * Expone los endpoints del flujo principal del módulo (CU-03, CU-04).
 *
 * Seguridad:
 * - POST /api/ejecucion/ejecutar  → requiere RC_LISTAR
 * - POST /api/ejecucion/exportar  → requiere RC_EXPORTAR
 *
 * El ID de usuario y entidad se toman del principal de seguridad
 * en sesión para evitar que el cliente los manipule.
 *
 * @author IDIGER – Equipo de Desarrollo
 */

@RestController
@RequestMapping("/api/ejecucion")
public class EjecucionReporteController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EjecucionReporteController.class);

    /** Servicio de ejecución y exportación de reportes */
    private final EjecucionReporteService ejecucionService;
    

    /**
     * Constructor del controlador.
     *
     * @param ejecucionService servicio de ejecución y exportación de reportes
     */
    public EjecucionReporteController(EjecucionReporteService ejecucionService) {
        this.ejecucionService = ejecucionService;
    }

    /**
     * Ejecuta un reporte y retorna los resultados en formato JSON (CU-03).
     *
     * POST /api/ejecucion/ejecutar
     *
     * El ID de entidad se extrae del header X-Entidad-Id por ahora.
     * En la siguiente fase se tomará del UserDetails de la sesión.
     *
     * @param request    DTO con el ID del reporte y filtros opcionales
     * @param entidadId  ID de la entidad del usuario (header)
     * @param usuarioId  ID del usuario (header temporal hasta integrar sesión)
     * @param httpRequest petición HTTP para obtener la IP del cliente
     * @return HTTP 200 con los resultados del reporte
     */
    @PostMapping("/ejecutar")
    @PreAuthorize("hasAuthority('RC_LISTAR')")
    public ResponseEntity<EjecucionResponseDTO> ejecutar(
            @Valid @RequestBody EjecucionRequestDTO request,
            @RequestHeader("X-Entidad-Id") Long entidadId,
            @RequestHeader("X-Usuario-Id") Long usuarioId,
            HttpServletRequest httpRequest) {

        log.info("POST /api/ejecucion/ejecutar - reporte: {}, entidad: {}, usuario: {}",
                request.getIdReporte(), entidadId, usuarioId);

        String ipCliente = obtenerIpCliente(httpRequest);

        EjecucionResponseDTO respuesta = ejecucionService.ejecutar(
                request, usuarioId, entidadId, ipCliente);

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Ejecuta un reporte y retorna el archivo para descarga (CU-04).
     * El formato de exportación debe venir en el DTO (CSV, XLSX, PDF, HTML).
     *
     * POST /api/ejecucion/exportar
     *
     * @param request    DTO con el ID del reporte, filtros y formato
     * @param entidadId  ID de la entidad del usuario (header)
     * @param usuarioId  ID del usuario (header temporal)
     * @param httpRequest petición HTTP para obtener la IP del cliente
     * @return HTTP 200 con el archivo en el Content-Type correspondiente
     */
    @PostMapping("/exportar")
    @PreAuthorize("hasAuthority('RC_EXPORTAR')")
    public ResponseEntity<byte[]> exportar(
            @Valid @RequestBody EjecucionRequestDTO request,
            @RequestHeader("X-Entidad-Id") Long entidadId,
            @RequestHeader("X-Usuario-Id") Long usuarioId,
            HttpServletRequest httpRequest) {

        log.info("POST /api/ejecucion/exportar - reporte: {}, formato: {}, entidad: {}",
                request.getIdReporte(), request.getFormato(), entidadId);

        String ipCliente = obtenerIpCliente(httpRequest);

        byte[] archivo = ejecucionService.exportar(
                request, usuarioId, entidadId, ipCliente);

        /* Determinar Content-Type y nombre del archivo según el formato */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData(
                "attachment",
                "reporte_" + request.getIdReporte()
                + "_" + System.currentTimeMillis()
                + obtenerExtension(request.getFormato()));
        headers.setContentType(obtenerMediaType(request.getFormato()));

        return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
    }

    /**
     * Obtiene la IP real del cliente considerando proxies inversos.
     * Revisa el header X-Forwarded-For antes de usar getRemoteAddr().
     *
     * @param request petición HTTP entrante
     * @return IP del cliente como String
     */
    private String obtenerIpCliente(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            /* X-Forwarded-For puede tener múltiples IPs separadas por coma
               La primera es siempre la IP original del cliente */
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Retorna el MediaType correspondiente al formato de exportación.
     *
     * @param formato CSV, XLSX, PDF o HTML
     * @return MediaType apropiado para el Content-Type del response
     */
    private MediaType obtenerMediaType(String formato) {
        if (formato == null) return MediaType.APPLICATION_OCTET_STREAM;
        switch (formato.toUpperCase()) {
            case "CSV":  return MediaType.parseMediaType("text/csv");
            case "XLSX": return MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "PDF":  return MediaType.APPLICATION_PDF;
            case "HTML": return MediaType.TEXT_HTML;
            default:     return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    /**
     * Retorna la extensión de archivo correspondiente al formato.
     *
     * @param formato CSV, XLSX, PDF o HTML
     * @return extensión con punto (ej. ".csv")
     */
    private String obtenerExtension(String formato) {
        if (formato == null) return ".bin";
        switch (formato.toUpperCase()) {
            case "CSV":  return ".csv";
            case "XLSX": return ".xlsx";
            case "PDF":  return ".pdf";
            case "HTML": return ".html";
            default:     return ".bin";
        }
    }
}