package gov.idiger.rcontractual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Clase principal del módulo Reportes Contractuales.
 * Excluye UserDetailsServiceAutoConfiguration para evitar que
 * Spring Boot registre un segundo UserDetailsService automático
 * que entra en conflicto con RcontractualUserDetailsService.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class RcontractualApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder application) {
        return application.sources(RcontractualApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(RcontractualApplication.class, args);
    }
}