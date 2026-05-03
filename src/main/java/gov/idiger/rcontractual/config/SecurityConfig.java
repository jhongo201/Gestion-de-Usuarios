package gov.idiger.rcontractual.config;

import gov.idiger.rcontractual.security.RcontractualUserDetailsService;
import gov.idiger.rcontractual.security.RecaptchaLoginFilter;
import gov.idiger.rcontractual.security.RecaptchaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuracion de seguridad usando WebSecurityConfigurerAdapter.
 *
 * Centraliza autenticacion con Oracle, reCAPTCHA en login y rutas publicas.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final RcontractualUserDetailsService userDetailsService;
    private final RecaptchaService recaptchaService;

    public SecurityConfig(
            RcontractualUserDetailsService userDetailsService,
            RecaptchaService recaptchaService) {
        this.userDetailsService = userDetailsService;
        this.recaptchaService = recaptchaService;
    }

    /**
     * Bean del filtro de reCAPTCHA.
     */
    @Bean
    public RecaptchaLoginFilter recaptchaLoginFilter() {
        return new RecaptchaLoginFilter(recaptchaService);
    }

    /**
     * Encoder BCrypt expuesto como @Bean para hashear contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Excluye recursos estaticos del filtro de seguridad.
     */
    @Override
    public void configure(
            org.springframework.security.config.annotation.web.builders.WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/fonts/**",
                        "/favicon.ico",
                        "/webjars/**");
    }

    /**
     * Registra autenticacion contra el UserDetailsService propio.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    /**
     * Configura rutas publicas, login, logout y filtros HTTP.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .and()
                .anonymous()
                .and()
                .requestCache()
                .and()
                .securityContext()
                .and()
                .exceptionHandling()
                .and()
                .headers()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .addFilterBefore(
                        recaptchaLoginFilter(),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(
                        "/login", "/login/**",
                        "/solicitar-usuario", "/solicitar-usuario/**",
                        "/recuperar-clave", "/recuperar-clave/**",
                        "/restablecer-clave", "/restablecer-clave/**",
                        "/css/**", "/js/**", "/images/**", "/fonts/**",
                        "/favicon.ico", "/webjars/**",
                        "/actuator/health", "/error")
                .permitAll()
                .anyRequest().authenticated()
                .and()

                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                /*
                 * Guarda un mensaje especifico de autenticacion para que login.html
                 * pueda mostrar si el usuario esta inactivo o pendiente.
                 */
                .failureHandler((request, response, exception) -> {
                    String mensaje = "Usuario o contraseña incorrectos.";

                    Throwable causa = exception;
                    while (causa != null) {
                        if (causa instanceof org.springframework.security.authentication.DisabledException) {
                            mensaje = causa.getMessage();
                            break;
                        }

                        String texto = causa.getMessage();
                        if (texto != null && texto.toLowerCase().contains("inactivo")) {
                            mensaje = texto;
                            break;
                        }

                        if (texto != null && texto.toLowerCase().contains("pendiente")) {
                            mensaje = texto;
                            break;
                        }

                        causa = causa.getCause();
                    }

                    request.getSession().setAttribute("LOGIN_ERROR", mensaje);
                    response.sendRedirect(request.getContextPath() + "/login?error=true");
                })
                .permitAll()

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
                .and()
                .httpBasic();

        log.info("Seguridad configurada - formLogin + BasicAuth + Oracle");
    }
}
