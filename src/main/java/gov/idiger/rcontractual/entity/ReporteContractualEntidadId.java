package gov.idiger.rcontractual.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clase que representa la clave primaria compuesta de la tabla
 * REPORTE_CONTRACTUAL_ENTIDAD (ID_REPORTE + ID_ENTIDAD).
 * Debe implementar Serializable y sobrescribir equals/hashCode
 * para que JPA pueda identificar correctamente los registros.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class ReporteContractualEntidadId implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID del reporte — parte de la PK compuesta */
    private Long idReporte;

    /** ID de la entidad — parte de la PK compuesta */
    private Long idEntidad;

    /**
     * Constructor vacío requerido por JPA para la clave compuesta.
     */
    public ReporteContractualEntidadId() {}

    /**
     * Constructor con argumentos para crear la PK desde el código.
     *
     * @param idReporte ID del reporte
     * @param idEntidad ID de la entidad
     */
    public ReporteContractualEntidadId(Long idReporte, Long idEntidad) {
        this.idReporte = idReporte;
        this.idEntidad = idEntidad;
    }

    /** @return ID del reporte */
    public Long getIdReporte() { return idReporte; }

    /** @param idReporte ID del reporte */
    public void setIdReporte(Long idReporte) { this.idReporte = idReporte; }

    /** @return ID de la entidad */
    public Long getIdEntidad() { return idEntidad; }

    /** @param idEntidad ID de la entidad */
    public void setIdEntidad(Long idEntidad) { this.idEntidad = idEntidad; }

    /**
     * Comparación por valor de ambos campos de la PK.
     * Requerido por JPA para identificar entidades únicas.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReporteContractualEntidadId that = (ReporteContractualEntidadId) o;
        return Objects.equals(idReporte, that.idReporte)
            && Objects.equals(idEntidad, that.idEntidad);
    }

    /**
     * Hash basado en ambos campos de la PK.
     * Requerido por JPA para uso en colecciones y caché.
     */
    @Override
    public int hashCode() {
        return Objects.hash(idReporte, idEntidad);
    }
}