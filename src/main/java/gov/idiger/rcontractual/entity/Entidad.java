package gov.idiger.rcontractual.entity;

import javax.persistence.*;

/**
 * Entidad JPA mapeada a CONTRATOS.ENTIDADES.
 * Representa las instituciones/entidades del sistema.
 * Cada usuario pertenece a una entidad y solo ve
 * los datos correspondientes a la suya.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Entity
@Table(name = "ENTIDADES", schema = "CONTRATOS")
public class Entidad {

    /** Identificador único de la entidad */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "seq_entidades")
    @SequenceGenerator(
        name         = "seq_entidades",
        sequenceName = "CONTRATOS.SEQ_ENTIDADES",
        allocationSize = 1
    )
    @Column(name = "ID_ENTIDAD", nullable = false)
    private Long idEntidad;

    /** Nombre o descripción de la entidad */
    @Column(name = "DESCRIPCION", nullable = false, length = 200)
    private String descripcion;

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getIdEntidad() { return idEntidad; }
    public void setIdEntidad(Long idEntidad) { this.idEntidad = idEntidad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}