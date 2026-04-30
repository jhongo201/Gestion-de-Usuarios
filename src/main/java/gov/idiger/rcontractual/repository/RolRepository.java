package gov.idiger.rcontractual.repository;

import gov.idiger.rcontractual.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Rol}.
 * Gestiona el acceso a CONTRATOS.ROL.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Verifica si existe un rol con el nombre dado.
     * Usado para validar duplicados al crear o editar.
     *
     * @param nombreRol nombre a verificar
     * @return true si ya existe
     */
    boolean existsByNombreRol(String nombreRol);

    /**
     * Busca un rol por su nombre exacto.
     *
     * @param nombreRol nombre del rol
     * @return Optional con el rol si existe
     */
    Optional<Rol> findByNombreRol(String nombreRol);

    /**
     * Cuenta cuántos usuarios tienen asignado cada rol.
     * Retorna pares [ID_ROL, total] para armar el DTO.
     *
     * @return lista de arrays con idRol y total de usuarios
     */
    @Query("SELECT u.rol.idRol, COUNT(u) " +
           "FROM Usuario u " +
           "GROUP BY u.rol.idRol")
    List<Object[]> contarUsuariosPorRol();
}