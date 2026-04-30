package gov.idiger.rcontractual.entity;

import javax.persistence.*;

import java.util.Date;

/**
 * Entidad JPA mapeada a CONTRATOS.PERMISO_ROL.
 * Relación N:M entre roles y permisos.
 * Usa clave primaria compuesta (ID_ROL, ID_PERMISO).
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Entity
@Table(name = "PERMISO_ROL", schema = "CONTRATOS")
@IdClass(PermisoRolId.class)
public class PermisoRol {

    /** ID del rol — parte de la PK compuesta */
    @Id
    @Column(name = "ID_ROL", nullable = false)
    private Long idRol;

    /** ID del permiso — parte de la PK compuesta */
    @Id
    @Column(name = "ID_PERMISO", nullable = false)
    private Long idPermiso;

    /** Fecha en que se asignó el permiso al rol */
    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public Long getIdPermiso() { return idPermiso; }
    public void setIdPermiso(Long idPermiso) { this.idPermiso = idPermiso; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}