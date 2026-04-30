package gov.idiger.rcontractual.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO de entrada para crear o editar un permiso.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class PermisoRequestDTO {

    /**
     * Descripción del permiso.
     * Obligatorio, máximo 200 caracteres.
     */
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200, message = "La descripción no puede superar 200 caracteres")
    private String descripcion;

    /**
     * ID del permiso padre.
     * Null si es un nodo raíz.
     */
    private Long idPermisoPadre;

    /**
     * Tipo del permiso: "menu" o "accion".
     */
    private String tipoEstilo;

    /**
     * Función JS de carga para permisos tipo "menu".
     */
    private String funcionCarga;

    /**
     * Orden de aparición en el menú.
     */
    private Integer orden;

    // ── Getters y Setters ────────────────────────────────────────────────────

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getIdPermisoPadre() { return idPermisoPadre; }
    public void setIdPermisoPadre(Long idPermisoPadre) { this.idPermisoPadre = idPermisoPadre; }

    public String getTipoEstilo() { return tipoEstilo; }
    public void setTipoEstilo(String tipoEstilo) { this.tipoEstilo = tipoEstilo; }

    public String getFuncionCarga() { return funcionCarga; }
    public void setFuncionCarga(String funcionCarga) { this.funcionCarga = funcionCarga; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}