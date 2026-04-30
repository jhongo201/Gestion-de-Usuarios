package gov.idiger.rcontractual.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad JPA mapeada a CONTRATOS.REPORTE_CONTRACTUAL_LOG.
 * Registra cada ejecución y exportación realizada en el módulo
 * para trazabilidad completa (CU-05).
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Entity
@Table(name = "REPORTE_CONTRACTUAL_LOG", schema = "CONTRATOS")
public class ReporteContractualLog {

    /** Identificador único — generado por SEQ_RC_LOG */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "seq_rc_log")
    @SequenceGenerator(
        name         = "seq_rc_log",
        sequenceName = "CONTRATOS.SEQ_RC_LOG",
        allocationSize = 1
    )
    @Column(name = "ID_LOG", nullable = false)
    private Long idLog;

    /** Reporte que fue ejecutado — FK a REPORTE_CONTRACTUAL */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REPORTE", nullable = false)
    private ReporteContractual reporte;

    /** ID del usuario que ejecutó el reporte */
    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    /** ID de la entidad del usuario al momento de la ejecución */
    @Column(name = "ID_ENTIDAD")
    private Long idEntidad;

    /** Filtros aplicados en formato JSON */
    @Column(name = "FILTROS_JSON", length = 4000)
    private String filtrosJson;

    /** Formato de exportación: CSV, XLSX, PDF, HTML o null si fue consulta */
    @Column(name = "FORMATO", length = 10)
    private String formato;

    /** Timestamp exacto de la ejecución con zona horaria */
    @Column(name = "FECHA_EJECUCION", nullable = false)
    private OffsetDateTime fechaEjecucion;

    /** Resultado: OK o ERROR */
    @Column(name = "ESTADO", nullable = false, length = 10)
    private String estado;

    /** Mensaje del error capturado si ESTADO = ERROR */
    @Column(name = "MENSAJE_ERROR", length = 2000)
    private String mensajeError;

    /** IP del cliente que realizó la solicitud (IPv4 o IPv6) */
    @Column(name = "IP_CLIENTE", length = 45)
    private String ipCliente;

    /** User-Agent del navegador del cliente */
    @Column(name = "USER_AGENT", length = 500)
    private String userAgent;

    /**
     * Asigna valores por defecto antes de insertar el registro.
     */
    @PrePersist
    public void prePersist() {
        if (this.fechaEjecucion == null) {
            this.fechaEjecucion = OffsetDateTime.now();
        }
        if (this.estado == null) {
            this.estado = "OK";
        }
    }

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getIdLog() { return idLog; }
    public void setIdLog(Long idLog) { this.idLog = idLog; }

    public ReporteContractual getReporte() { return reporte; }
    public void setReporte(ReporteContractual reporte) { this.reporte = reporte; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Long getIdEntidad() { return idEntidad; }
    public void setIdEntidad(Long idEntidad) { this.idEntidad = idEntidad; }

    public String getFiltrosJson() { return filtrosJson; }
    public void setFiltrosJson(String filtrosJson) { this.filtrosJson = filtrosJson; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public OffsetDateTime getFechaEjecucion() { return fechaEjecucion; }
    public void setFechaEjecucion(OffsetDateTime fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMensajeError() { return mensajeError; }
    public void setMensajeError(String mensajeError) { this.mensajeError = mensajeError; }

    public String getIpCliente() { return ipCliente; }
    public void setIpCliente(String ipCliente) { this.ipCliente = ipCliente; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}