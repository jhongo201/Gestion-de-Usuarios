package gov.idiger.rcontractual.controller.mvc;

import gov.idiger.rcontractual.security.UsuarioSesionVO;
import gov.idiger.rcontractual.service.RolService;
import gov.idiger.rcontractual.service.UsuarioService;
import gov.idiger.rcontractual.service.PermisoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador MVC para el dashboard principal.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Controller
public class DashboardController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PermisoService permisoService;

    /**
     * Constructor para inyección de dependencias.
     */
    public DashboardController(UsuarioService usuarioService,
                                RolService rolService,
                                PermisoService permisoService) {
        this.usuarioService  = usuarioService;
        this.rolService      = rolService;
        this.permisoService  = permisoService;
    }

    /**
     * Muestra el dashboard con estadísticas del módulo.
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
        model.addAttribute("usuario", usuario);
        return "dashboard";
    }

    /** GET /seguridad/usuarios */
    @GetMapping("/seguridad/usuarios")
    public String usuarios(Model model,
            @AuthenticationPrincipal UsuarioSesionVO usuario) {
        model.addAttribute("usuarios",
                usuarioService.listarActivos());
        model.addAttribute("roles", rolService.listarTodos());
        model.addAttribute("usuario", usuario);
        return "seguridad/usuarios";
    }

    /** GET /seguridad/roles */
    @GetMapping("/seguridad/roles")
    public String roles(Model model,
            @AuthenticationPrincipal UsuarioSesionVO usuario) {
        model.addAttribute("roles", rolService.listarTodos());
        model.addAttribute("usuario", usuario);
        return "seguridad/roles";
    }

    /** GET /seguridad/permisos */
    @GetMapping("/seguridad/permisos")
    public String permisos(Model model,
            @AuthenticationPrincipal UsuarioSesionVO usuario) {
        model.addAttribute("permisos",
                permisoService.listarPlano());
        model.addAttribute("usuario", usuario);
        return "seguridad/permisos";
    }
}