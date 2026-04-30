package gov.idiger.rcontractual.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Servicio para validar el token de Google reCAPTCHA v2
 * contra la API de verificación de Google.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Service
public class RecaptchaService {

    private static final Logger log =
        LoggerFactory.getLogger(RecaptchaService.class);

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @Value("${recaptcha.verify-url}")
    private String verifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Valida el token reCAPTCHA enviado desde el formulario de login.
     *
     * @param token valor del campo g-recaptcha-response del formulario
     * @return true si Google confirma que el captcha es válido
     */
    @SuppressWarnings("unchecked")
    public boolean validar(String token) {
        if (token == null || token.isEmpty()) {
            log.warn("Token reCAPTCHA vacío o nulo");
            return false;
        }

        try {
            MultiValueMap<String, String> params =
                new LinkedMultiValueMap<>();
            params.add("secret",   secretKey);
            params.add("response", token);

            ResponseEntity<Map> response =
                restTemplate.postForEntity(verifyUrl, params, Map.class);

            Map<String, Object> body = response.getBody();
            if (body == null) return false;

            boolean success = Boolean.TRUE.equals(body.get("success"));
            log.debug("reCAPTCHA validación: success={}", success);
            return success;

        } catch (Exception e) {
            log.error("Error validando reCAPTCHA: {}", e.getMessage());
            return false;
        }
    }
}