package gov.idiger.rcontractual.controller;

import gov.idiger.rcontractual.dto.UsuarioDTO;
import gov.idiger.rcontractual.dto.UsuarioRequestDTO;
import gov.idiger.rcontractual.security.UsuarioSesionVO;
import gov.idiger.rcontractual.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controlador REST para la gestion administrativa de usuarios.
 *
 * Todos los endpoints requieren authority RC_ADMIN.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final Logger log =
            LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Lista usuarios.
     *
     * Si no se envia estado, retorna todos.
     * Si se envia estado, filtra:
     * 0 = Inactivo, 1 = Activo, 2 = Pendiente.
     *
     * Ejemplos:
     * GET /api/usuarios
     * GET /api/usuarios?estado=2
     */
    @GetMapping
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listar(
            @RequestParam(required = false) Integer estado) {
        log.debug("GET /api/usuarios estado={}", estado);

        if (estado == null) {
            return ResponseEntity.ok(usuarioService.listarTodos());
        }

        return ResponseEntity.ok(usuarioService.listarPorEstado(estado));
    }

    /**
     * Cuenta usuarios pendientes de aprobacion.
     * GET /api/usuarios/pendientes/count
     */
    @GetMapping("/pendientes/count")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<Long> contarPendientes() {
        log.debug("GET /api/usuarios/pendientes/count");
        return ResponseEntity.ok(usuarioService.contarPendientes());
    }

    /**
     * Lista usuarios de una entidad.
     * GET /api/usuarios/entidad/{idEntidad}
     */
    @GetMapping("/entidad/{idEntidad}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarPorEntidad(
            @PathVariable Long idEntidad) {
        log.debug("GET /api/usuarios/entidad/{}", idEntidad);
        return ResponseEntity.ok(usuarioService.listarPorEntidad(idEntidad));
    }

    /**
     * Busca un usuario por ID.
     * GET /api/usuarios/{idUsuario}
     */
    @GetMapping("/{idUsuario}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<UsuarioDTO> buscarPorId(
            @PathVariable Long idUsuario) {
        log.debug("GET /api/usuarios/{}", idUsuario);
        return ResponseEntity.ok(usuarioService.buscarPorId(idUsuario));
    }

    /**
     * Crea un usuario desde el modulo administrativo.
     * POST /api/usuarios
     */
    @PostMapping
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<UsuarioDTO> crear(
            @Valid @RequestBody UsuarioRequestDTO request) {
        log.info("POST /api/usuarios - username: {}", request.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.crear(request));
    }

    /**
     * Actualiza un usuario existente.
     * PUT /api/usuarios/{idUsuario}
     */
    @PutMapping("/{idUsuario}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<UsuarioDTO> actualizar(
            @PathVariable Long idUsuario,
            @Valid @RequestBody UsuarioRequestDTO request) {
        log.info("PUT /api/usuarios/{}", idUsuario);
        return ResponseEntity.ok(usuarioService.actualizar(idUsuario, request));
    }

    /**
     * Activa un usuario pendiente o inactivo.
     * PATCH /api/usuarios/{idUsuario}/activar
     */
    @PatchMapping("/{idUsuario}/activar")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<UsuarioDTO> activar(
            @PathVariable Long idUsuario,
            @AuthenticationPrincipal UsuarioSesionVO admin) {
        log.info("PATCH /api/usuarios/{}/activar", idUsuario);
        return ResponseEntity.ok(
                usuarioService.activar(idUsuario, admin.getIdUsuario()));
    }

    /**
     * Desactiva un usuario.
     * DELETE /api/usuarios/{idUsuario}
     */
    @DeleteMapping("/{idUsuario}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<Void> desactivar(@PathVariable Long idUsuario) {
        log.info("DELETE /api/usuarios/{}", idUsuario);
        usuarioService.desactivar(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
