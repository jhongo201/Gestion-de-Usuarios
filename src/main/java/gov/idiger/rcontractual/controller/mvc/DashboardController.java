package gov.idiger.rcontractual.controller.mvc;

import gov.idiger.rcontractual.security.UsuarioSesionVO;
import gov.idiger.rcontractual.service.PermisoService;
import gov.idiger.rcontractual.service.RolService;
import gov.idiger.rcontractual.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador MVC para las vistas principales del sistema.
 *
 * Carga dashboard y pantallas del modulo de seguridad.
 */
@Controller
public class DashboardController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PermisoService permisoService;

    public DashboardController(UsuarioService usuarioService,
                               RolService rolService,
                               PermisoService permisoService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.permisoService = permisoService;
    }

    /**
     * Dashboard principal.
     * GET /dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model,
            @AuthenticationPrincipal UsuarioSesionVO usuario) {
        model.addAttribute("totalUsuarios",
                usuarioService.listarActivos().size());
        model.addAttribute("totalRoles",
                rolService.listarTodos().size());
        model.addAttribute("totalPermisos",
                permisoService.listarPlano().size());
        model.addAttribute("usuariosPendientes",
                usuarioService.contarPendientes());
        model.addAttribute("usuario", usuario);
        return "dashboard";
    }

    /**
     * Administracion de usuarios.
     * Ahora carga todos los usuarios, no solo activos, para que ADMIN_RC
     * pueda ver activos, inactivos y pendientes de aprobacion.
     */
    @GetMapping("/seguridad/usuarios")
    public String usuarios(Model model,
            @AuthenticationPrincipal UsuarioSesionVO usuario) {
        model.addAttribute("usuarios",
                usuarioService.listarTodos());
        model.addAttribute("roles",
                rolService.listarTodos());
        model.addAttribute("usuariosPendientes",
                usuarioService.contarPendientes());
        model.addAttribute("usuario", usuario);
        return "seguridad/usuarios";
    }

    /**
     * Administracion de roles.
     * GET /seguridad/roles
     */
    @GetMapping("/seguridad/roles")
    public String roles(Model model,
            @AuthenticationPrincipal UsuarioSesionVO usuario) {
        model.addAttribute("roles", rolService.listarTodos());
        model.addAttribute("usuario", usuario);
        return "seguridad/roles";
    }

    /**
     * Administracion de permisos.
     * GET /seguridad/permisos
     */
    @GetMapping("/seguridad/permisos")
    public String permisos(Model model,
            @AuthenticationPrincipal UsuarioSesionVO usuario) {
        model.addAttribute("permisos",
                permisoService.listarPlano());
        model.addAttribute("usuario", usuario);
        return "seguridad/permisos";
    }
}
