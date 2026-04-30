package gov.idiger.rcontractual.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio del módulo.
 * Resulta en HTTP 400 cuando es manejada por el GlobalExceptionHandler.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class ReglaDeNegocioException extends RuntimeException {
    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}