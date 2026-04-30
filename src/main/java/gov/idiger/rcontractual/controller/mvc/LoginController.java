package gov.idiger.rcontractual.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador MVC para la página de login.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Controller
public class LoginController {

    /**
     * Muestra la página de login.
     * GET /login
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Redirige la raíz al dashboard.
     * GET /
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
}