package gov.idiger.rcontractual.dto;

/**
 * DTO de respuesta para la consulta de roles.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class RolDTO {

    /** ID único del rol */
    private Long idRol;

    /** Nombre del rol */
    private String nombreRol;

    /** Cantidad de usuarios asignados a este rol */
    private Long totalUsuarios;

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }

    public Long getTotalUsuarios() { return totalUsuarios; }
    public void setTotalUsuarios(Long totalUsuarios) { this.totalUsuarios = totalUsuarios; }
}