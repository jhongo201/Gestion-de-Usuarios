package gov.idiger.rcontractual.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad JPA mapeada a CONTRATOS.REPORTE_CONTRACTUAL_ENTIDAD.
 * Controla qué reportes están habilitados para cada entidad.
 * Usa clave primaria compuesta definida en {@link ReporteContractualEntidadId}.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Entity
@Table(name = "REPORTE_CONTRACTUAL_ENTIDAD", schema = "CONTRATOS")
@IdClass(ReporteContractualEntidadId.class)
public class ReporteContractualEntidad {

    /** ID del reporte — parte de la PK compuesta */
    @Id
    @Column(name = "ID_REPORTE", nullable = false)
    private Long idReporte;

    /** ID de la entidad — parte de la PK compuesta */
    @Id
    @Column(name = "ID_ENTIDAD", nullable = false)
    private Long idEntidad;

    /** Estado: 1 = habilitado para la entidad, 0 = deshabilitado */
    @Column(name = "ACTIVO", nullable = false)
    private Integer activo;

    /** Fecha en que se asoció el reporte a la entidad */
    @Column(name = "FECHA_ASIGNACION", nullable = false)
    private OffsetDateTime fechaAsignacion;

    /**
     * Relación de solo lectura al catálogo de reportes.
     * insertable/updatable = false porque la FK se gestiona
     * directamente a través del campo idReporte.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REPORTE", insertable = false, updatable = false)
    private ReporteContractual reporte;

    /**
     * Asigna valores por defecto antes de insertar el registro.
     */
    @PrePersist
    public void prePersist() {
        if (this.fechaAsignacion == null) {
            this.fechaAsignacion = OffsetDateTime.now();
        }
        if (this.activo == null) {
            this.activo = 1;
        }
    }

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getIdReporte() { return idReporte; }
    public void setIdReporte(Long idReporte) { this.idReporte = idReporte; }

    public Long getIdEntidad() { return idEntidad; }
    public void setIdEntidad(Long idEntidad) { this.idEntidad = idEntidad; }

    public Integer getActivo() { return activo; }
    public void setActivo(Integer activo) { this.activo = activo; }

    public OffsetDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(OffsetDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public ReporteContractual getReporte() { return reporte; }
    public void setReporte(ReporteContractual reporte) { this.reporte = reporte; }
}