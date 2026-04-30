package gov.idiger.rcontractual.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Entidad JPA mapeada a CONTRATOS.PERMISOS.
 * Representa el árbol jerárquico de permisos del sistema.
 * Los permisos tipo "menu" generan entradas en el menú lateral.
 * Los permisos tipo "accion" controlan operaciones específicas.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Entity
@Table(name = "PERMISOS", schema = "CONTRATOS")
public class Permiso {

    /** Identificador único del permiso */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "seq_permisos")
    @SequenceGenerator(
        name         = "seq_permisos",
        sequenceName = "CONTRATOS.SEQ_PERMISOS",
        allocationSize = 1
    )
    @Column(name = "ID_PERMISO", nullable = false)
    private Long idPermiso;

    /** Descripción legible del permiso */
    @Column(name = "DESCRIPCION", nullable = false, length = 200)
    private String descripcion;

    /**
     * Permiso padre en la jerarquía.
     * NULL indica que es un nodo raíz.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PERMISOPADRE")
    private Permiso permisoPadre;

    /**
     * Permisos hijos en la jerarquía.
     * Cargados con LAZY para evitar consultas innecesarias.
     */
    @OneToMany(mappedBy = "permisoPadre", fetch = FetchType.LAZY)
    @OrderBy("orden ASC")
    private List<Permiso> hijos;

    /** Tipo del permiso: "menu" o "accion" */
    @Column(name = "TIPO_ESTILO", length = 20)
    private String tipoEstilo;

    /** Función JS que carga la pantalla al seleccionar el menú */
    @Column(name = "FUNCION_CARGA", length = 500)
    private String funcionCarga;

    /** Orden de aparición dentro del nivel jerárquico */
    @Column(name = "ORDEN")
    private Integer orden;

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getIdPermiso() { return idPermiso; }
    public void setIdPermiso(Long idPermiso) { this.idPermiso = idPermiso; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Permiso getPermisoPadre() { return permisoPadre; }
    public void setPermisoPadre(Permiso permisoPadre) { this.permisoPadre = permisoPadre; }

    public List<Permiso> getHijos() { return hijos; }
    public void setHijos(List<Permiso> hijos) { this.hijos = hijos; }

    public String getTipoEstilo() { return tipoEstilo; }
    public void setTipoEstilo(String tipoEstilo) { this.tipoEstilo = tipoEstilo; }

    public String getFuncionCarga() { return funcionCarga; }
    public void setFuncionCarga(String funcionCarga) { this.funcionCarga = funcionCarga; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}