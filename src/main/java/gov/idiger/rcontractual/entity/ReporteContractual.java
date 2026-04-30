package gov.idiger.rcontractual.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad JPA mapeada a la tabla CONTRATOS.REPORTE_CONTRACTUAL.
 * Representa el catálogo de reportes disponibles en el módulo.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Entity
@Table(name = "REPORTE_CONTRACTUAL", schema = "CONTRATOS")
public class ReporteContractual {

    /** Identificador único — generado por SEQ_REPORTE_CONTRACTUAL */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "seq_reporte_contractual")
    @SequenceGenerator(
        name         = "seq_reporte_contractual",
        sequenceName = "CONTRATOS.SEQ_REPORTE_CONTRACTUAL",
        allocationSize = 1
    )
    @Column(name = "ID_REPORTE", nullable = false)
    private Long idReporte;

    /** Nombre visible del reporte en la interfaz */
    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    /** Descripción del propósito del reporte */
    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    /** Estado: 1 = activo, 0 = inactivo */
    @Column(name = "ACTIVO", nullable = false)
    private Integer activo;

    /** Consulta SQL base opcional para reportes parametrizados */
    @Lob
    @Column(name = "SQL_BASE")
    private String sqlBase;

    /** Fecha y hora de creación con zona horaria */
    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    /** ID del usuario que creó el reporte en el catálogo */
    @Column(name = "ID_USUARIO_CREA", nullable = false)
    private Long idUsuarioCrea;

    /**
     * Se ejecuta antes de insertar — asigna valores por defecto
     * si no fueron provistos por el código que instanció la entidad.
     */
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = OffsetDateTime.now();
        }
        if (this.activo == null) {
            this.activo = 1;
        }
    }

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getIdReporte() { return idReporte; }
    public void setIdReporte(Long idReporte) { this.idReporte = idReporte; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getActivo() { return activo; }
    public void setActivo(Integer activo) { this.activo = activo; }

    public String getSqlBase() { return sqlBase; }
    public void setSqlBase(String sqlBase) { this.sqlBase = sqlBase; }

    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getIdUsuarioCrea() { return idUsuarioCrea; }
    public void setIdUsuarioCrea(Long idUsuarioCrea) {
        this.idUsuarioCrea = idUsuarioCrea;
    }
}