package gov.idiger.rcontractual.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO de entrada para crear o actualizar un reporte en el catálogo.
 * Solo el administrador RC (permiso RC_ADMIN) puede usar este DTO (CU-06).
 * Incluye validaciones para garantizar integridad de los datos recibidos.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class ReporteContractualRequestDTO {

    /**
     * Nombre visible del reporte.
     * Obligatorio, máximo 150 caracteres.
     */
    @NotBlank(message = "El nombre del reporte es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombre;

    /**
     * Descripción del reporte.
     * Opcional, máximo 500 caracteres.
     */
    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;

    /**
     * Consulta SQL base del reporte.
     * Opcional — si no se provee, la lógica va en el DAO/Service.
     */
    private String sqlBase;

    /**
     * ID del usuario que crea el reporte.
     * Se toma de la sesión en el servicio.
     */
    @NotNull(message = "El ID del usuario creador es obligatorio")
    private Long idUsuarioCrea;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getSqlBase() { return sqlBase; }
    public void setSqlBase(String sqlBase) { this.sqlBase = sqlBase; }

    public Long getIdUsuarioCrea() { return idUsuarioCrea; }
    public void setIdUsuarioCrea(Long idUsuarioCrea) { this.idUsuarioCrea = idUsuarioCrea; }
}