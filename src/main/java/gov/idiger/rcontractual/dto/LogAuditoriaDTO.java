package gov.idiger.rcontractual.dto;

import java.time.OffsetDateTime;

/**
 * DTO de respuesta para el historial de auditoría (CU-08).
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class LogAuditoriaDTO {

    private Long idLog;
    private Long idReporte;
    private String nombreReporte;
    private Long idUsuario;
    private Long idEntidad;
    private String filtrosJson;
    private String formato;
    private OffsetDateTime fechaEjecucion;
    private String estado;
    private String mensajeError;

    /** Builder estático para LogAuditoriaDTO */
    public static Builder builder() { return new Builder(); }

    /** Clase interna Builder */
    public static class Builder {
        private final LogAuditoriaDTO dto = new LogAuditoriaDTO();

        public Builder idLog(Long v)              { dto.idLog = v; return this; }
        public Builder idReporte(Long v)          { dto.idReporte = v; return this; }
        public Builder nombreReporte(String v)    { dto.nombreReporte = v; return this; }
        public Builder idUsuario(Long v)          { dto.idUsuario = v; return this; }
        public Builder idEntidad(Long v)          { dto.idEntidad = v; return this; }
        public Builder filtrosJson(String v)      { dto.filtrosJson = v; return this; }
        public Builder formato(String v)          { dto.formato = v; return this; }
        public Builder fechaEjecucion(OffsetDateTime v) { dto.fechaEjecucion = v; return this; }
        public Builder estado(String v)           { dto.estado = v; return this; }
        public Builder mensajeError(String v)     { dto.mensajeError = v; return this; }
        public LogAuditoriaDTO build()            { return dto; }
    }

    public Long getIdLog() { return idLog; }
    public Long getIdReporte() { return idReporte; }
    public String getNombreReporte() { return nombreReporte; }
    public Long getIdUsuario() { return idUsuario; }
    public Long getIdEntidad() { return idEntidad; }
    public String getFiltrosJson() { return filtrosJson; }
    public String getFormato() { return formato; }
    public OffsetDateTime getFechaEjecucion() { return fechaEjecucion; }
    public String getEstado() { return estado; }
    public String getMensajeError() { return mensajeError; }
}