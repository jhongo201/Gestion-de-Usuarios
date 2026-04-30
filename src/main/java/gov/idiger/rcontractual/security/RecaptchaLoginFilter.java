package gov.idiger.rcontractual.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro que valida el reCAPTCHA antes de que Spring Security
 * procese el formulario de login (/login POST).
 * Si el captcha es inválido, redirige a /login?captcha=error.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class RecaptchaLoginFilter extends OncePerRequestFilter {

    private static final Logger log =
        LoggerFactory.getLogger(RecaptchaLoginFilter.class);

    private final RecaptchaService recaptchaService;

    /**
     * Constructor con inyección del servicio de validación.
     *
     * @param recaptchaService servicio que consulta la API de Google
     */
    public RecaptchaLoginFilter(RecaptchaService recaptchaService) {
        this.recaptchaService = recaptchaService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        /* Solo interceptar POST /login */
        if ("POST".equalsIgnoreCase(request.getMethod())
                && request.getServletPath().equals("/login")) {

            String token = request.getParameter("g-recaptcha-response");
            log.debug("Validando reCAPTCHA en login, token presente: {}",
                      token != null && !token.isEmpty());

            if (!recaptchaService.validar(token)) {
                log.warn("reCAPTCHA inválido para intento de login");
                response.sendRedirect(
                    request.getContextPath() + "/login?captcha=error");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}