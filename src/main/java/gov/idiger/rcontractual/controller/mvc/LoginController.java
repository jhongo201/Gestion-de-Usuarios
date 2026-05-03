package gov.idiger.rcontractual.controller.mvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/**
 * Controlador MVC para la pagina de login.
 */
@Controller
public class LoginController {

    @Value("${recaptcha.site-key}")
    private String recaptchaSiteKey;

    /**
     * Muestra la pagina de login.
     *
     * Si Spring Security dejo un mensaje de error en sesion, lo pasa
     * a la vista y luego lo elimina para que no quede pegado.
     */
    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        Object loginError = session.getAttribute("LOGIN_ERROR");

        if (loginError != null) {
            model.addAttribute("loginError", loginError.toString());
            session.removeAttribute("LOGIN_ERROR");
        }

        model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
        return "login";
    }

    /**
     * Redirige la raiz al dashboard.
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
}
