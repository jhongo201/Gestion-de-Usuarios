package gov.idiger.rcontractual.config;

import gov.idiger.rcontractual.security.UsuarioSesionVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor que obliga a cambiar la contraseña antes de usar el sistema.
 *
 * Si el usuario autenticado tiene CAMBIO_CLAVE_REQUERIDO = 1, solo puede
 * acceder a /cambiar-clave, /logout y recursos estaticos.
 */
public class CambioClaveInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return true;
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof UsuarioSesionVO)) {
            return true;
        }

        UsuarioSesionVO usuario = (UsuarioSesionVO) principal;

        if (!usuario.debeCambiarClave()) {
            return true;
        }

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        String path = uri.substring(contextPath.length());

        if (path.equals("/cambiar-clave")
                || path.equals("/logout")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/webjars/")
                || path.equals("/favicon.ico")) {
            return true;
        }

        response.sendRedirect(contextPath + "/cambiar-clave");
        return false;
    }
}
