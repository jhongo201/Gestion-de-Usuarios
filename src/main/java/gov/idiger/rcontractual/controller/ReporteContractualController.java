package gov.idiger.rcontractual.controller;

import gov.idiger.rcontractual.dto.ReporteContractualDTO;
import gov.idiger.rcontractual.dto.ReporteContractualRequestDTO;
import gov.idiger.rcontractual.service.ReporteContractualService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controlador REST para la gestión del catálogo de reportes.
 * Expone los endpoints de consulta (CU-02) y administración
 * del catálogo (CU-06) bajo la ruta base /api/reportes.
 *
 * Seguridad:
 * - GET /api/reportes/entidad/{id} → requiere permiso RC_LISTAR
 * - GET /api/reportes              → requiere permiso RC_ADMIN
 * - POST, PUT, DELETE              → requiere permiso RC_ADMIN
 *
 * @author IDIGER – Equipo de Desarrollo
 */

@RestController
@RequestMapping("/api/reportes")
public class ReporteContractualController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReporteContractualController.class);

    /** Servicio de gestión del catálogo de reportes */
    private final ReporteContractualService reporteService;
    

    /**
     * Constructor del controlador.
     *
     * @param reporteService servicio de gestión del catálogo de reportes
     */
    public ReporteContractualController(ReporteContractualService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * Retorna los reportes activos habilitados para una entidad específica.
     * Usado por el frontend para cargar el listado del módulo (CU-02).
     *
     * GET /api/reportes/entidad/{idEntidad}
     *
     * @param idEntidad ID de la entidad cuyos reportes se listan
     * @return HTTP 200 con la lista de reportes disponibles
     */
    @GetMapping("/entidad/{idEntidad}")
    @PreAuthorize("hasAuthority('RC_LISTAR')")
    public ResponseEntity<List<ReporteContractualDTO>> listarPorEntidad(
            @PathVariable Long idEntidad) {
        log.debug("GET /api/reportes/entidad/{}", idEntidad);
        List<ReporteContractualDTO> reportes =
                reporteService.listarPorEntidad(idEntidad);
        return ResponseEntity.ok(reportes);
    }

    /**
     * Retorna todos los reportes activos sin filtro de entidad.
     * Solo accesible por el administrador RC (CU-06).
     *
     * GET /api/reportes
     *
     * @return HTTP 200 con la lista completa de reportes activos
     */
    @GetMapping
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<ReporteContractualDTO>> listarTodos() {
        log.debug("GET /api/reportes");
        return ResponseEntity.ok(reporteService.listarTodos());
    }

    /**
     * Busca un reporte por su ID.
     *
     * GET /api/reportes/{idReporte}
     *
     * @param idReporte ID del reporte a buscar
     * @return HTTP 200 con el reporte encontrado
     *         HTTP 404 si no existe (manejado por GlobalExceptionHandler)
     */
    @GetMapping("/{idReporte}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<ReporteContractualDTO> buscarPorId(
            @PathVariable Long idReporte) {
        log.debug("GET /api/reportes/{}", idReporte);
        return ResponseEntity.ok(reporteService.buscarPorId(idReporte));
    }

    /**
     * Crea un nuevo reporte en el catálogo (CU-06).
     * Valida los datos de entrada con @Valid.
     *
     * POST /api/reportes
     *
     * @param request DTO con los datos del nuevo reporte
     * @return HTTP 201 con el reporte creado
     */
    @PostMapping
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<ReporteContractualDTO> crear(
            @Valid @RequestBody ReporteContractualRequestDTO request) {
        log.info("POST /api/reportes - nombre: {}", request.getNombre());
        ReporteContractualDTO creado = reporteService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Actualiza los datos de un reporte existente (CU-06).
     *
     * PUT /api/reportes/{idReporte}
     *
     * @param idReporte ID del reporte a actualizar
     * @param request   DTO con los nuevos datos
     * @return HTTP 200 con el reporte actualizado
     */
    @PutMapping("/{idReporte}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<ReporteContractualDTO> actualizar(
            @PathVariable Long idReporte,
            @Valid @RequestBody ReporteContractualRequestDTO request) {
        log.info("PUT /api/reportes/{}", idReporte);
        return ResponseEntity.ok(reporteService.actualizar(idReporte, request));
    }

    /**
     * Desactiva un reporte del catálogo (CU-06).
     * Desactivación lógica — no elimina el registro.
     *
     * DELETE /api/reportes/{idReporte}
     *
     * @param idReporte ID del reporte a desactivar
     * @return HTTP 204 sin contenido
     */
    @DeleteMapping("/{idReporte}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<Void> desactivar(@PathVariable Long idReporte) {
        log.info("DELETE /api/reportes/{}", idReporte);
        reporteService.desactivar(idReporte);
        return ResponseEntity.noContent().build();
    }
}