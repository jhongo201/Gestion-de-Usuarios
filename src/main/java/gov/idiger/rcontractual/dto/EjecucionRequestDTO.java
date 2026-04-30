package gov.idiger.rcontractual.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * DTO de entrada para ejecutar o exportar un reporte (CU-03, CU-04).
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class EjecucionRequestDTO {

    /** ID del reporte a ejecutar. Obligatorio. */
    @NotNull(message = "El ID del reporte es obligatorio")
    private Long idReporte;

    /**
     * Filtros opcionales en formato JSON.
     * Ejemplo: {"fechaDesde":"2026-01-01","fechaHasta":"2026-04-30"}
     */
    private String filtrosJson;

    /**
     * Formato de exportación: CSV, XLSX, PDF o HTML.
     * Null si es consulta en pantalla sin descarga.
     */
    @Pattern(regexp = "^(CSV|XLSX|PDF|HTML)$",
             message = "El formato debe ser CSV, XLSX, PDF o HTML")
    private String formato;

    public Long getIdReporte() { return idReporte; }
    public void setIdReporte(Long idReporte) { this.idReporte = idReporte; }

    public String getFiltrosJson() { return filtrosJson; }
    public void setFiltrosJson(String filtrosJson) { this.filtrosJson = filtrosJson; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
}