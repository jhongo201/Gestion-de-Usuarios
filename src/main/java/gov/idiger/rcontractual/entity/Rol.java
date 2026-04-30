package gov.idiger.rcontractual.entity;

import javax.persistence.*;

/**
 * Entidad JPA mapeada a CONTRATOS.ROL.
 * Define los roles del sistema. Cada usuario tiene
 * exactamente un rol que determina sus permisos.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Entity
@Table(name = "ROL", schema = "CONTRATOS")
public class Rol {

    /** Identificador único del rol */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "seq_rol")
    @SequenceGenerator(
        name         = "seq_rol",
        sequenceName = "CONTRATOS.SEQ_ROL",
        allocationSize = 1
    )
    @Column(name = "ID_ROL", nullable = false)
    private Long idRol;

    /** Nombre del rol (ej: ADMIN_RC, USUARIO_RC) */
    @Column(name = "NOMBRE_ROL", nullable = false, length = 100)
    private String nombreRol;

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}