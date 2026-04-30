package gov.idiger.rcontractual.controller;

import gov.idiger.rcontractual.dto.PermisoDTO;
import gov.idiger.rcontractual.dto.RolDTO;
import gov.idiger.rcontractual.dto.RolRequestDTO;
import gov.idiger.rcontractual.service.RolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de roles y
 * asignación de permisos a roles.
 * Expone el CRUD bajo /api/roles.
 * Todos los endpoints requieren RC_ADMIN.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private static final Logger log =
        LoggerFactory.getLogger(RolController.class);

    private final RolService rolService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param rolService servicio de gestión de roles
     */
    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    /**
     * Lista todos los roles con el conteo de usuarios.
     * GET /api/roles
     *
     * @return HTTP 200 con lista de roles
     */
    @GetMapping
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<RolDTO>> listarTodos() {
        log.debug("GET /api/roles");
        return ResponseEntity.ok(rolService.listarTodos());
    }

    /**
     * Busca un rol por su ID.
     * GET /api/roles/{idRol}
     *
     * @param idRol ID del rol
     * @return HTTP 200 con el rol encontrado
     */
    @GetMapping("/{idRol}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<RolDTO> buscarPorId(@PathVariable Long idRol) {
        log.debug("GET /api/roles/{}", idRol);
        return ResponseEntity.ok(rolService.buscarPorId(idRol));
    }

    /**
     * Crea un nuevo rol.
     * POST /api/roles
     *
     * @param request DTO con el nombre del rol
     * @return HTTP 201 con el rol creado
     */
    @PostMapping
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<RolDTO> crear(
            @Valid @RequestBody RolRequestDTO request) {
        log.info("POST /api/roles - nombre: {}", request.getNombreRol());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rolService.crear(request));
    }

    /**
     * Actualiza el nombre de un rol existente.
     * PUT /api/roles/{idRol}
     *
     * @param idRol   ID del rol
     * @param request DTO con el nuevo nombre
     * @return HTTP 200 con el rol actualizado
     */
    @PutMapping("/{idRol}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<RolDTO> actualizar(
            @PathVariable Long idRol,
            @Valid @RequestBody RolRequestDTO request) {
        log.info("PUT /api/roles/{}", idRol);
        return ResponseEntity.ok(rolService.actualizar(idRol, request));
    }

    /**
     * Retorna los permisos asignados a un rol.
     * GET /api/roles/{idRol}/permisos
     *
     * @param idRol ID del rol
     * @return HTTP 200 con lista de permisos del rol
     */
    @GetMapping("/{idRol}/permisos")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<PermisoDTO>> listarPermisos(
            @PathVariable Long idRol) {
        log.debug("GET /api/roles/{}/permisos", idRol);
        return ResponseEntity.ok(rolService.listarPermisosPorRol(idRol));
    }

    /**
     * Asigna un permiso a un rol.
     * POST /api/roles/{idRol}/permisos/{idPermiso}
     *
     * @param idRol     ID del rol
     * @param idPermiso ID del permiso a asignar
     * @return HTTP 204 sin contenido
     */
    @PostMapping("/{idRol}/permisos/{idPermiso}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<Void> asignarPermiso(
            @PathVariable Long idRol,
            @PathVariable Long idPermiso) {
        log.info("POST /api/roles/{}/permisos/{}", idRol, idPermiso);
        rolService.asignarPermiso(idRol, idPermiso);
        return ResponseEntity.noContent().build();
    }

    /**
     * Quita un permiso de un rol.
     * DELETE /api/roles/{idRol}/permisos/{idPermiso}
     *
     * @param idRol     ID del rol
     * @param idPermiso ID del permiso a quitar
     * @return HTTP 204 sin contenido
     */
    @DeleteMapping("/{idRol}/permisos/{idPermiso}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<Void> quitarPermiso(
            @PathVariable Long idRol,
            @PathVariable Long idPermiso) {
        log.info("DELETE /api/roles/{}/permisos/{}", idRol, idPermiso);
        rolService.quitarPermiso(idRol, idPermiso);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint de diagnóstico temporal — muestra las authorities
     * del usuario autenticado. Eliminar antes de producción.
     *
     * GET /api/roles/mi-sesion
     */
    @GetMapping("/mi-sesion")
    public ResponseEntity<Map<String, Object>> miSesion(
            org.springframework.security.core.Authentication auth) {
        Map<String, Object> info = new java.util.HashMap<>();
        if (auth != null) {
            info.put("usuario",     auth.getName());
            info.put("autenticado", auth.isAuthenticated());
            info.put("authorities", auth.getAuthorities().toString());
            info.put("principal",   auth.getPrincipal().getClass().getSimpleName());
        } else {
            info.put("error", "Sin autenticación");
        }
        return ResponseEntity.ok(info);
    }

    /**
     * Endpoint temporal para generar hash BCrypt.
     * ELIMINAR antes de producción.
     * GET /api/roles/hash?password=tuPassword
     */
    @GetMapping("/hash")
    public ResponseEntity<String> generarHash(
            @RequestParam String password,
            org.springframework.security.crypto.password.PasswordEncoder encoder) {
        return ResponseEntity.ok(encoder.encode(password));
    }
}