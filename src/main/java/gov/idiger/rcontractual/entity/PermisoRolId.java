package gov.idiger.rcontractual.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clase de clave primaria compuesta para {@link PermisoRol}.
 * Requerida por JPA cuando se usa @IdClass con PK compuesta.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class PermisoRolId implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID del rol */
    private Long idRol;

    /** ID del permiso */
    private Long idPermiso;

    /** Constructor vacío requerido por JPA */
    public PermisoRolId() {}

    /**
     * Constructor con argumentos.
     *
     * @param idRol     ID del rol
     * @param idPermiso ID del permiso
     */
    public PermisoRolId(Long idRol, Long idPermiso) {
        this.idRol = idRol;
        this.idPermiso = idPermiso;
    }

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public Long getIdPermiso() { return idPermiso; }
    public void setIdPermiso(Long idPermiso) { this.idPermiso = idPermiso; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermisoRolId that = (PermisoRolId) o;
        return Objects.equals(idRol, that.idRol)
            && Objects.equals(idPermiso, that.idPermiso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRol, idPermiso);
    }
}