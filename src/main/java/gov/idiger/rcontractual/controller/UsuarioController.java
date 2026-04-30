package gov.idiger.rcontractual.controller;

import gov.idiger.rcontractual.dto.UsuarioDTO;
import gov.idiger.rcontractual.dto.UsuarioRequestDTO;
import gov.idiger.rcontractual.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controlador REST para la gestión de usuarios.
 * Expone el CRUD bajo /api/usuarios.
 * Todos los endpoints requieren RC_ADMIN.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final Logger log =
        LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param usuarioService servicio de gestión de usuarios
     */
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todos los usuarios activos del sistema.
     * GET /api/usuarios
     *
     * @return HTTP 200 con lista de usuarios activos
     */
    @GetMapping
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarActivos() {
        log.debug("GET /api/usuarios");
        return ResponseEntity.ok(usuarioService.listarActivos());
    }

    /**
     * Lista los usuarios activos de una entidad específica.
     * GET /api/usuarios/entidad/{idEntidad}
     *
     * @param idEntidad ID de la entidad a filtrar
     * @return HTTP 200 con lista de usuarios de esa entidad
     */
    @GetMapping("/entidad/{idEntidad}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarPorEntidad(
            @PathVariable Long idEntidad) {
        log.debug("GET /api/usuarios/entidad/{}", idEntidad);
        return ResponseEntity.ok(usuarioService.listarPorEntidad(idEntidad));
    }

    /**
     * Busca un usuario por su ID.
     * GET /api/usuarios/{idUsuario}
     *
     * @param idUsuario ID del usuario
     * @return HTTP 200 con el usuario encontrado
     */
    @GetMapping("/{idUsuario}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<UsuarioDTO> buscarPorId(
            @PathVariable Long idUsuario) {
        log.debug("GET /api/usuarios/{}", idUsuario);
        return ResponseEntity.ok(usuarioService.buscarPorId(idUsuario));
    }

    /**
     * Crea un nuevo usuario.
     * POST /api/usuarios
     *
     * @param request DTO con los datos del nuevo usuario
     * @return HTTP 201 con el usuario creado
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
     * Actualiza los datos de un usuario existente.
     * PUT /api/usuarios/{idUsuario}
     *
     * @param idUsuario ID del usuario a actualizar
     * @param request   DTO con los nuevos datos
     * @return HTTP 200 con el usuario actualizado
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
     * Desactiva un usuario (ESTADO_USUARIO = 0).
     * DELETE /api/usuarios/{idUsuario}
     *
     * @param idUsuario ID del usuario a desactivar
     * @return HTTP 204 sin contenido
     */
    @DeleteMapping("/{idUsuario}")
    @PreAuthorize("hasAuthority('RC_ADMIN')")
    public ResponseEntity<Void> desactivar(@PathVariable Long idUsuario) {
        log.info("DELETE /api/usuarios/{}", idUsuario);
        usuarioService.desactivar(idUsuario);
        return ResponseEntity.noContent().build();
    }
}