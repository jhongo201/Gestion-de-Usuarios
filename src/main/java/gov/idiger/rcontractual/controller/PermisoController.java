package gov.idiger.rcontractual.controller;

import gov.idiger.rcontractual.dto.PermisoDTO;
import gov.idiger.rcontractual.dto.PermisoRequestDTO;
import gov.idiger.rcontractual.service.PermisoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controlador REST para la gestión del árbol de permisos.
 * Expone el CRUD bajo /api/permisos.
 * Todos los endpoints requieren RC_ADMIN.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private static final Logger log =
        LoggerFactory.getLogger(PermisoController.class);

    private final PermisoService permisoService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param permisoService servicio de gestión de permisos
     */
    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    /**
     * Retorna el árbol jerárquico completo de permisos.
     * GET /api/permisos/arbol
     *
     * @return HTTP 200 con árbol de permisos anidado
     */
    @GetMapping("/arbol")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<PermisoDTO>> listarArbol() {
        log.debug("GET /api/permisos/arbol");
        return ResponseEntity.ok(permisoService.listarArbol());
    }

    /**
     * Retorna la lista plana de todos los permisos.
     * Útil para poblar selectores en el frontend.
     * GET /api/permisos
     *
     * @return HTTP 200 con lista plana de permisos
     */
    @GetMapping
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<PermisoDTO>> listarPlano() {
        log.debug("GET /api/permisos");
        return ResponseEntity.ok(permisoService.listarPlano());
    }

    /**
     * Busca un permiso por su ID.
     * GET /api/permisos/{idPermiso}
     *
     * @param idPermiso ID del permiso
     * @return HTTP 200 con el permiso encontrado
     */
    @GetMapping("/{idPermiso}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<PermisoDTO> buscarPorId(
            @PathVariable Long idPermiso) {
        log.debug("GET /api/permisos/{}", idPermiso);
        return ResponseEntity.ok(permisoService.buscarPorId(idPermiso));
    }

    /**
     * Crea un nuevo permiso en el árbol.
     * POST /api/permisos
     *
     * @param request DTO con los datos del nuevo permiso
     * @return HTTP 201 con el permiso creado
     */
    @PostMapping
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<PermisoDTO> crear(
            @Valid @RequestBody PermisoRequestDTO request) {
        log.info("POST /api/permisos - desc: {}", request.getDescripcion());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(permisoService.crear(request));
    }

    /**
     * Actualiza un permiso existente.
     * PUT /api/permisos/{idPermiso}
     *
     * @param idPermiso ID del permiso a actualizar
     * @param request   DTO con los nuevos datos
     * @return HTTP 200 con el permiso actualizado
     */
    @PutMapping("/{idPermiso}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<PermisoDTO> actualizar(
            @PathVariable Long idPermiso,
            @Valid @RequestBody PermisoRequestDTO request) {
        log.info("PUT /api/permisos/{}", idPermiso);
        return ResponseEntity.ok(
                permisoService.actualizar(idPermiso, request));
    }
}