package gov.idiger.rcontractual.repository;

import gov.idiger.rcontractual.entity.Entidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Entidad}.
 * Gestiona el acceso a CONTRATOS.ENTIDADES.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Repository
public interface EntidadRepository extends JpaRepository<Entidad, Long> {

    /**
     * Verifica si existe una entidad con la descripción dada.
     *
     * @param descripcion descripción a verificar
     * @return true si ya existe
     */
    boolean existsByDescripcion(String descripcion);
}