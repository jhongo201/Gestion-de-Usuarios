package gov.idiger.rcontractual.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso por su ID.
 * Resulta en HTTP 404 cuando es manejada por el GlobalExceptionHandler.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}