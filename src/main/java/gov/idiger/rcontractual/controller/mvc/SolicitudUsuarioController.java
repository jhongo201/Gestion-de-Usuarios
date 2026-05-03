package gov.idiger.rcontractual.controller.mvc;

import gov.idiger.rcontractual.dto.SolicitudUsuarioRequestDTO;
import gov.idiger.rcontractual.exception.ReglaDeNegocioException;
import gov.idiger.rcontractual.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * Controlador MVC para solicitud publica de usuarios.
 *
 * Permite que una persona solicite acceso. La cuenta queda en estado pendiente
 * hasta que un administrador la active.
 */
@Controller
public class SolicitudUsuarioController {

    private final UsuarioService usuarioService;

    public SolicitudUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Muestra el formulario publico de solicitud.
     */
    @GetMapping("/solicitar-usuario")
    public String mostrarFormulario(Model model) {
        model.addAttribute("solicitudUsuario", new SolicitudUsuarioRequestDTO());
        return "solicitar-usuario";
    }

    /**
     * Procesa la solicitud publica de usuario.
     */
    @PostMapping("/solicitar-usuario")
    public String registrarSolicitud(
            @Valid SolicitudUsuarioRequestDTO solicitudUsuario,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("solicitudUsuario", solicitudUsuario);
            return "solicitar-usuario";
        }

        try {
            usuarioService.solicitarUsuario(solicitudUsuario);
            model.addAttribute("solicitudUsuario", new SolicitudUsuarioRequestDTO());
            model.addAttribute("mensaje",
                    "Solicitud registrada correctamente. Revise su correo y espere la activación del administrador.");
            return "solicitar-usuario";
        } catch (ReglaDeNegocioException ex) {
            model.addAttribute("solicitudUsuario", solicitudUsuario);
            model.addAttribute("error", ex.getMessage());
            return "solicitar-usuario";
        }
    }
}
