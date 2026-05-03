package gov.idiger.rcontractual.controller.mvc;

import gov.idiger.rcontractual.dto.RecuperarClaveRequestDTO;
import gov.idiger.rcontractual.dto.RestablecerClaveRequestDTO;
import gov.idiger.rcontractual.exception.ReglaDeNegocioException;
import gov.idiger.rcontractual.service.RecuperacionClaveService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * Controlador MVC para recuperacion de contraseña mediante OTP.
 */
@Controller
public class RecuperacionClaveController {

    private final RecuperacionClaveService recuperacionClaveService;

    public RecuperacionClaveController(RecuperacionClaveService recuperacionClaveService) {
        this.recuperacionClaveService = recuperacionClaveService;
    }

    /**
     * Muestra formulario para solicitar codigo de recuperacion.
     */
    @GetMapping("/recuperar-clave")
    public String mostrarRecuperarClave(Model model) {
        model.addAttribute("recuperarClave", new RecuperarClaveRequestDTO());
        return "recuperar-clave";
    }

    /**
     * Procesa solicitud de codigo de recuperacion.
     *
     * La respuesta es generica para no revelar si el usuario/correo existe.
     */
    @PostMapping("/recuperar-clave")
    public String solicitarCodigo(
            @Valid RecuperarClaveRequestDTO recuperarClave,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("recuperarClave", recuperarClave);
            return "recuperar-clave";
        }

        recuperacionClaveService.solicitarCodigo(recuperarClave);

        model.addAttribute("restablecerClave", prepararRestablecer(recuperarClave.getUsuarioOCorreo()));
        model.addAttribute("mensaje",
                "Si el usuario o correo existe y está activo, recibirá un código de recuperación.");
        return "restablecer-clave";
    }

    /**
     * Muestra formulario para restablecer contraseña.
     */
    @GetMapping("/restablecer-clave")
    public String mostrarRestablecerClave(
            @RequestParam(value = "usuario", required = false) String usuario,
            Model model) {

        model.addAttribute("restablecerClave", prepararRestablecer(usuario));
        return "restablecer-clave";
    }

    /**
     * Procesa cambio de contraseña con codigo OTP.
     */
    @PostMapping("/restablecer-clave")
    public String restablecerClave(
            @Valid RestablecerClaveRequestDTO restablecerClave,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("restablecerClave", restablecerClave);
            return "restablecer-clave";
        }

        try {
            recuperacionClaveService.restablecerClave(restablecerClave);
            return "redirect:/login?claveActualizada=true";
        } catch (ReglaDeNegocioException ex) {
            model.addAttribute("restablecerClave", restablecerClave);
            model.addAttribute("error", ex.getMessage());
            return "restablecer-clave";
        }
    }

    private RestablecerClaveRequestDTO prepararRestablecer(String usuarioOCorreo) {
        RestablecerClaveRequestDTO dto = new RestablecerClaveRequestDTO();
        dto.setUsuarioOCorreo(usuarioOCorreo);
        return dto;
    }
}
