package gov.idiger.rcontractual.controller.mvc;

import gov.idiger.rcontractual.dto.CambioClaveRequestDTO;
import gov.idiger.rcontractual.exception.ReglaDeNegocioException;
import gov.idiger.rcontractual.security.UsuarioSesionVO;
import gov.idiger.rcontractual.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * Controlador MVC para cambio obligatorio de contraseña.
 *
 * Cuando CAMBIO_CLAVE_REQUERIDO = 1, el usuario debe cambiar la clave
 * antes de continuar usando el sistema.
 */
@Controller
public class CambioClaveController {

    private final UsuarioService usuarioService;

    public CambioClaveController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Muestra formulario de cambio obligatorio de contraseña.
     */
    @GetMapping("/cambiar-clave")
    public String cambiarClave(Model model,
            @AuthenticationPrincipal UsuarioSesionVO usuario) {

        if (usuario == null) {
            return "redirect:/login?claveActualizada=true";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("cambioClaveRequest", new CambioClaveRequestDTO());
        return "cambiar-clave";
    }

    /**
     * Procesa el cambio obligatorio de contraseña.
     *
     * Al cambiar la clave se invalida la sesion para obligar a iniciar
     * nuevamente con la contraseña nueva.
     */
    @PostMapping("/cambiar-clave")
    public String guardarCambioClave(
            @Valid CambioClaveRequestDTO request,
            @AuthenticationPrincipal UsuarioSesionVO usuario,
            Model model) {

        if (usuario == null) {
            return "redirect:/login?claveActualizada=true";
        }

        try {
            usuarioService.cambiarClaveObligatoria(
                    usuario.getIdUsuario(),
                    request.getNuevaClave(),
                    request.getConfirmarClave());

            /*
             * Se limpia la autenticacion actual para que el usuario ingrese
             * nuevamente y se cargue la sesion con CAMBIO_CLAVE_REQUERIDO = 0.
             */
            SecurityContextHolder.clearContext();
            return "redirect:/login?claveActualizada=true";
        } catch (ReglaDeNegocioException ex) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("cambioClaveRequest", request);
            model.addAttribute("error", ex.getMessage());
            return "cambiar-clave";
        }
    }
}
