package gov.idiger.rcontractual.exception;

/**
 * Excepción lanzada cuando un usuario intenta acceder a un recurso
 * para el que no tiene permiso (reporte no habilitado para su entidad).
 * Resulta en HTTP 403 cuando es manejada por el GlobalExceptionHandler.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}