package gov.idiger.rcontractual.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO de respuesta tras ejecutar un reporte (CU-03).
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class EjecucionResponseDTO {

    /** ID del log de auditoría generado */
    private Long idLog;

    /** Nombre del reporte ejecutado */
    private String nombreReporte;

    /** Timestamp de la ejecución */
    private OffsetDateTime fechaEjecucion;

    /** Estado: OK o ERROR */
    private String estado;

    /** Mensaje de error si aplica */
    private String mensajeError;

    /** Total de filas retornadas */
    private Integer totalFilas;

    /**
     * Resultados como lista de mapas columna → valor.
     * Cada elemento representa una fila del resultado.
     */
    private List<Map<String, Object>> resultados;

    /**
     * Builder estático para construir el DTO de forma fluida.
     * Usado en EjecucionReporteServiceImpl.
     */
    public static Builder builder() { return new Builder(); }

    /** Clase interna Builder para EjecucionResponseDTO */
    public static class Builder {
        private final EjecucionResponseDTO dto = new EjecucionResponseDTO();

        /** @param idLog ID del log generado */
        public Builder idLog(Long idLog) {
            dto.idLog = idLog; return this;
        }
        /** @param nombreReporte nombre del reporte */
        public Builder nombreReporte(String nombreReporte) {
            dto.nombreReporte = nombreReporte; return this;
        }
        /** @param fechaEjecucion timestamp de ejecución */
        public Builder fechaEjecucion(OffsetDateTime fechaEjecucion) {
            dto.fechaEjecucion = fechaEjecucion; return this;
        }
        /** @param estado OK o ERROR */
        public Builder estado(String estado) {
            dto.estado = estado; return this;
        }
        /** @param mensajeError mensaje si hubo error */
        public Builder mensajeError(String mensajeError) {
            dto.mensajeError = mensajeError; return this;
        }
        /** @param totalFilas cantidad de filas */
        public Builder totalFilas(Integer totalFilas) {
            dto.totalFilas = totalFilas; return this;
        }
        /** @param resultados lista de filas */
        public Builder resultados(List<Map<String, Object>> resultados) {
            dto.resultados = resultados; return this;
        }
        /** @return DTO construido */
        public EjecucionResponseDTO build() { return dto; }
    }

    public Long getIdLog() { return idLog; }
    public String getNombreReporte() { return nombreReporte; }
    public OffsetDateTime getFechaEjecucion() { return fechaEjecucion; }
    public String getEstado() { return estado; }
    public String getMensajeError() { return mensajeError; }
    public Integer getTotalFilas() { return totalFilas; }
    public List<Map<String, Object>> getResultados() { return resultados; }
}