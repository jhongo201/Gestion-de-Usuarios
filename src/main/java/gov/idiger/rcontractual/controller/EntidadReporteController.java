package gov.idiger.rcontractual.controller;

import gov.idiger.rcontractual.dto.ReporteContractualDTO;
import gov.idiger.rcontractual.service.EntidadReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de la asignación de reportes por entidad.
 * Expone los endpoints del CU-07 bajo la ruta /api/entidad-reporte.
 * Todos los endpoints requieren el permiso RC_ADMIN.
 *
 * @author IDIGER – Equipo de Desarrollo
 */

@RestController
@RequestMapping("/api/entidad-reporte")
public class EntidadReporteController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EntidadReporteController.class);

    /** Servicio de gestión de asignación de reportes por entidad */
    private final EntidadReporteService entidadReporteService;

    /**
     * Constructor del controlador.
     *
     * @param entidadReporteService servicio de gestión de asignación de reportes por entidad
     */
    public EntidadReporteController(EntidadReporteService entidadReporteService) {
        this.entidadReporteService = entidadReporteService;
    }

    /**
     * Retorna los reportes habilitados para una entidad específica.
     *
     * GET /api/entidad-reporte/{idEntidad}
     *
     * @param idEntidad ID de la entidad a consultar
     * @return HTTP 200 con la lista de reportes habilitados
     */
    @GetMapping("/{idEntidad}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<ReporteContractualDTO>> listar(
            @PathVariable Long idEntidad) {
        log.debug("GET /api/entidad-reporte/{}", idEntidad);
        return ResponseEntity.ok(
                entidadReporteService.listarReportesPorEntidad(idEntidad));
    }

    /**
     * Habilita un reporte para una entidad específica (CU-07).
     * Si ya existe la relación deshabilitada, la reactiva.
     *
     * POST /api/entidad-reporte/{idEntidad}/habilitar/{idReporte}
     *
     * @param idEntidad ID de la entidad destino
     * @param idReporte ID del reporte a habilitar
     * @return HTTP 204 sin contenido
     */
    @PostMapping("/{idEntidad}/habilitar/{idReporte}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<Void> habilitar(
            @PathVariable Long idEntidad,
            @PathVariable Long idReporte) {
        log.info("POST habilitar reporte {} para entidad {}", idReporte, idEntidad);
        entidadReporteService.habilitar(idReporte, idEntidad);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deshabilita un reporte para una entidad específica (CU-07).
     * Desactivación lógica — no elimina el registro.
     *
     * POST /api/entidad-reporte/{idEntidad}/deshabilitar/{idReporte}
     *
     * @param idEntidad ID de la entidad destino
     * @param idReporte ID del reporte a deshabilitar
     * @return HTTP 204 sin contenido
     */
    @PostMapping("/{idEntidad}/deshabilitar/{idReporte}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<Void> deshabilitar(
            @PathVariable Long idEntidad,
            @PathVariable Long idReporte) {
        log.info("POST deshabilitar reporte {} para entidad {}", idReporte, idEntidad);
        entidadReporteService.deshabilitar(idReporte, idEntidad);
        return ResponseEntity.noContent().build();
    }
}