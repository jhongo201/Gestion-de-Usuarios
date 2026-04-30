package gov.idiger.rcontractual.dto;

import java.time.OffsetDateTime;

/**
 * DTO de respuesta para el catálogo de reportes.
 * Se usa para listar y mostrar los reportes disponibles
 * en la interfaz del módulo (CU-02, CU-03).
 * No expone campos internos como SQL_BASE por seguridad.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class ReporteContractualDTO {

    /** Identificador único del reporte */
    private Long idReporte;

    /** Nombre visible del reporte en la interfaz */
    private String nombre;

    /** Descripción del propósito del reporte */
    private String descripcion;

    /** Estado del reporte: 1 = activo, 0 = inactivo */
    private Integer activo;

    /** Fecha de creación del reporte en el catálogo */
    private OffsetDateTime fechaCreacion;

    public Long getIdReporte() { return idReporte; }
    public void setIdReporte(Long idReporte) { this.idReporte = idReporte; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getActivo() { return activo; }
    public void setActivo(Integer activo) { this.activo = activo; }

    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}