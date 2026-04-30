package gov.idiger.rcontractual.controller;

import gov.idiger.rcontractual.dto.LogAuditoriaDTO;
import gov.idiger.rcontractual.service.AuditoriaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Controlador REST para la consulta del historial de auditoría.
 * Expone los endpoints del CU-08 bajo la ruta /api/auditoria.
 * Todos los endpoints requieren el permiso RC_ADMIN.
 *
 * @author IDIGER – Equipo de Desarrollo
 */

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuditoriaController.class);

    /** Servicio de consulta del log de auditoría */
    private final AuditoriaService auditoriaService;
    

    /**
     * Constructor del controlador.
     *
     * @param auditoriaService servicio de consulta del log de auditoría
     */
    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    /**
     * Retorna el historial de ejecuciones de un usuario específico.
     *
     * GET /api/auditoria/usuario/{idUsuario}
     *
     * @param idUsuario ID del usuario a consultar
     * @return HTTP 200 con la lista de logs del usuario
     */
    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<LogAuditoriaDTO>> porUsuario(
            @PathVariable Long idUsuario) {
        log.debug("GET /api/auditoria/usuario/{}", idUsuario);
        return ResponseEntity.ok(
                auditoriaService.consultarPorUsuario(idUsuario));
    }

    /**
     * Retorna el historial de ejecuciones de una entidad específica.
     *
     * GET /api/auditoria/entidad/{idEntidad}
     *
     * @param idEntidad ID de la entidad a auditar
     * @return HTTP 200 con la lista de logs de esa entidad
     */
    @GetMapping("/entidad/{idEntidad}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<LogAuditoriaDTO>> porEntidad(
            @PathVariable Long idEntidad) {
        log.debug("GET /api/auditoria/entidad/{}", idEntidad);
        return ResponseEntity.ok(
                auditoriaService.consultarPorEntidad(idEntidad));
    }

    /**
     * Retorna el historial de ejecuciones de un reporte en un rango de fechas.
     *
     * GET /api/auditoria/reporte/{idReporte}?desde=...&hasta=...
     *
     * Las fechas deben enviarse en formato ISO-8601:
     * Ejemplo: 2026-01-01T00:00:00-05:00
     *
     * @param idReporte ID del reporte a auditar
     * @param desde     fecha de inicio del rango (inclusive)
     * @param hasta     fecha de fin del rango (inclusive)
     * @return HTTP 200 con la lista de logs en el rango dado
     */
    @GetMapping("/reporte/{idReporte}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<LogAuditoriaDTO>> porReporteYFecha(
            @PathVariable Long idReporte,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                OffsetDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                OffsetDateTime hasta) {
        log.debug("GET /api/auditoria/reporte/{} entre {} y {}",
                idReporte, desde, hasta);
        return ResponseEntity.ok(
                auditoriaService.consultarPorReporteYFecha(
                        idReporte, desde, hasta));
    }
}