package gov.idiger.rcontractual.dto;

/**
 * DTO de respuesta para la consulta de roles.
 *
 * Se usa en el modulo de administracion de roles y en los select
 * donde se asigna un rol a un usuario.
 */
public class RolDTO {

    /** ID unico del rol. */
    private Long idRol;

    /** Nombre del rol. Ejemplo: ADMIN_RC, USUARIO_RC. */
    private String nombreRol;

    /** Cantidad de usuarios asignados a este rol. */
    private Long totalUsuarios;

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public Long getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(Long totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }
}
