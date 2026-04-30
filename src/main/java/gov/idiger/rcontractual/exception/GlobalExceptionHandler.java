package gov.idiger.rcontractual.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones del módulo Reportes Contractuales.
 * Intercepta todas las excepciones lanzadas desde los controllers
 * y las convierte en respuestas HTTP estructuradas con el formato:
 * { timestamp, status, error, mensaje, path }.
 *
 * Mapeo de excepciones a códigos HTTP:
 * - RecursoNoEncontradoException → 404 NOT FOUND
 * - AccesoDenegadoException      → 403 FORBIDDEN
 * - ReglaDeNegocioException      → 400 BAD REQUEST
 * - MethodArgumentNotValidException → 400 con detalle por campo
 * - Exception (genérica)         → 500 INTERNAL SERVER ERROR
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
        LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja recursos no encontrados (CU-03, CU-04, CU-06).
     * Lanzada cuando se busca un reporte por ID y no existe.
     *
     * @param ex      excepción capturada
     * @param request petición HTTP para obtener la ruta
     * @return HTTP 404 con mensaje descriptivo
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoNoEncontrado(
            RecursoNoEncontradoException ex,
            HttpServletRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /**
     * Maneja accesos denegados por entidad (CU-03, CU-04).
     * Lanzada cuando un usuario intenta ejecutar un reporte
     * que no está habilitado para su entidad.
     *
     * @param ex      excepción capturada
     * @param request petición HTTP para obtener la ruta
     * @return HTTP 403 con mensaje descriptivo
     */
    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<Map<String, Object>> handleAccesoDenegado(
            AccesoDenegadoException ex,
            HttpServletRequest request) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    /**
     * Maneja violaciones de reglas de negocio (CU-06).
     * Lanzada cuando se intenta crear un reporte con nombre duplicado
     * o cualquier otra validación de negocio falla.
     *
     * @param ex      excepción capturada
     * @param request petición HTTP para obtener la ruta
     * @return HTTP 400 con mensaje descriptivo
     */
    @ExceptionHandler(ReglaDeNegocioException.class)
    public ResponseEntity<Map<String, Object>> handleReglaDeNegocio(
            ReglaDeNegocioException ex,
            HttpServletRequest request) {
        log.warn("Regla de negocio violada: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * Maneja errores de validación de DTOs con @Valid.
     * Retorna el detalle de cada campo inválido para que el frontend
     * pueda mostrar los mensajes de error por campo.
     *
     * @param ex      excepción con el detalle de campos inválidos
     * @param request petición HTTP para obtener la ruta
     * @return HTTP 400 con mapa de campo → mensaje de error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        log.warn("Error de validación en request: {}", request.getRequestURI());

        /* Construir mapa de errores por campo */
        Map<String, String> erroresPorCampo = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erroresPorCampo.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp",   OffsetDateTime.now().toString());
        body.put("status",      HttpStatus.BAD_REQUEST.value());
        body.put("error",       "Error de validación");
        body.put("errores",     erroresPorCampo);
        body.put("path",        request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Maneja operaciones no implementadas aún.
     * Usada por el endpoint de exportación mientras se desarrolla
     * la generación de archivos (fase siguiente).
     *
     * @param ex      excepción capturada
     * @param request petición HTTP para obtener la ruta
     * @return HTTP 501 NOT IMPLEMENTED
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Map<String, Object>> handleNoImplementado(
            UnsupportedOperationException ex,
            HttpServletRequest request) {
        log.info("Operación no implementada: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_IMPLEMENTED, ex.getMessage(), request);
    }

    /**
     * Manejador genérico para cualquier excepción no contemplada.
     * Evita exponer stack traces al cliente en producción.
     *
     * @param ex      excepción capturada
     * @param request petición HTTP para obtener la ruta
     * @return HTTP 500 con mensaje genérico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenerico(
            Exception ex,
            HttpServletRequest request) {
        log.error("Error interno no controlado: {}", ex.getMessage(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor. Contacte al administrador.",
                request);
    }

    /**
     * Construye el cuerpo estándar de respuesta de error.
     * Formato consistente para todos los errores del módulo:
     * { timestamp, status, error, mensaje, path }
     *
     * @param status  código HTTP de la respuesta
     * @param mensaje descripción del error
     * @param request petición HTTP para obtener la ruta
     * @return ResponseEntity con el cuerpo estructurado
     */
    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String mensaje,
            HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("mensaje",   mensaje);
        body.put("path",      request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}