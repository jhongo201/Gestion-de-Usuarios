package gov.idiger.rcontractual.dto;

import java.util.List;

/**
 * DTO de respuesta para la consulta de permisos.
 * Incluye los hijos directos para representar el árbol jerárquico.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class PermisoDTO {

    /** ID único del permiso */
    private Long idPermiso;

    /** Descripción del permiso */
    private String descripcion;

    /** ID del permiso padre (null si es raíz) */
    private Long idPermisoPadre;

    /** Descripción del permiso padre */
    private String descripcionPadre;

    /** Tipo: menu o accion */
    private String tipoEstilo;

    /** Función JS de carga */
    private String funcionCarga;

    /** Orden de aparición */
    private Integer orden;

    /** Hijos directos en la jerarquía */
    private List<PermisoDTO> hijos;

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getIdPermiso() { return idPermiso; }
    public void setIdPermiso(Long idPermiso) { this.idPermiso = idPermiso; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getIdPermisoPadre() { return idPermisoPadre; }
    public void setIdPermisoPadre(Long idPermisoPadre) { this.idPermisoPadre = idPermisoPadre; }

    public String getDescripcionPadre() { return descripcionPadre; }
    public void setDescripcionPadre(String descripcionPadre) { this.descripcionPadre = descripcionPadre; }

    public String getTipoEstilo() { return tipoEstilo; }
    public void setTipoEstilo(String tipoEstilo) { this.tipoEstilo = tipoEstilo; }

    public String getFuncionCarga() { return funcionCarga; }
    public void setFuncionCarga(String funcionCarga) { this.funcionCarga = funcionCarga; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    public List<PermisoDTO> getHijos() { return hijos; }
    public void setHijos(List<PermisoDTO> hijos) { this.hijos = hijos; }
}