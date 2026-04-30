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
 * Configuración de seguridad usando WebSecurityConfigurerAdapter.
 * Se pasa disableDefaults=true al constructor padre para evitar
 * que Spring Boot registre un segundo DaoAuthenticationProvider
 * automáticamente al detectar el UserDetailsService en el contexto.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger log =
        LoggerFactory.getLogger(SecurityConfig.class);

    private final RcontractualUserDetailsService userDetailsService;

    /* Inyección del servicio de validación reCAPTCHA */
    private final RecaptchaService recaptchaService;

    /**
     * Constructor — disableDefaults=true desactiva el registro
     * automático de filtros y providers de Spring Boot Security,
     * evitando el doble DaoAuthenticationProvider.
     *
     * @param userDetailsService servicio de autenticación contra Oracle
     */
    public SecurityConfig(
            RcontractualUserDetailsService userDetailsService,
        RecaptchaService recaptchaService) {
        /* disableDefaults=true es la clave — evita el provider automático */
        //super(true);
        this.userDetailsService = userDetailsService;
        this.recaptchaService = recaptchaService;
    }

    /**
     * Bean del filtro de reCAPTCHA.
     *
     * @return filtro configurado con el servicio de validación
     */
    @Bean
    public RecaptchaLoginFilter recaptchaLoginFilter() {
        return new RecaptchaLoginFilter(recaptchaService);
    }

    /**
     * Encoder BCrypt expuesto como @Bean para que UsuarioServiceImpl
     * pueda inyectarlo al hashear contraseñas nuevas.
     *
     * @return BCryptPasswordEncoder con factor 10
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Excluye los recursos estáticos del filtro de seguridad.
     * Sin esto, Spring Security intercepta CSS/JS y los sirve
     * como JSON con tipo de contenido incorrecto.
     *
     * @param web configurador de seguridad web
     * @throws Exception si hay error
     */
    @Override
    public void configure(
            org.springframework.security.config.annotation.web
                .builders.WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(
                "/css/**",
                "/js/**",
                "/images/**",
                "/fonts/**",
                "/favicon.ico",
                "/webjars/**"
            );
    }

    /**
     * Registra el único AuthenticationProvider del módulo.
     * Al usar super(true) este es el ÚNICO provider — no hay duplicados.
     *
     * @param auth configurador del AuthenticationManager
     * @throws Exception si hay error en la configuración
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    /**
     * Configura los filtros HTTP manualmente.
     * Con super(true) los filtros no se registran automáticamente,
     * por eso se agregan explícitamente los esenciales.
     *
     * @param http configurador HTTP
     * @throws Exception si hay error
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
        org.springframework.security.web.authentication
            .UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
                .antMatchers(
                    "/login", "/login/**",
                    "/css/**", "/js/**", "/images/**",
                    "/actuator/health", "/error"
                ).permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
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

        log.info("Seguridad configurada — formLogin + BasicAuth + Oracle");
    }
}