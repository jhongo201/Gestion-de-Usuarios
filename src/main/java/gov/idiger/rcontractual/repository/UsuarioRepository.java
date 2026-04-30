package gov.idiger.rcontractual.repository;

import gov.idiger.rcontractual.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Usuario}.
 * Gestiona el acceso a CONTRATOS.USUARIO.
 * Es usado principalmente por el proceso de autenticación
 * de Spring Security.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario activo por su username.
     * Usado por Spring Security en el proceso de autenticación.
     * Solo retorna usuarios con ESTADO_USUARIO = 1.
     *
     * @param username login del usuario
     * @return Optional con el usuario si existe y está activo
     */
    @Query("SELECT u FROM Usuario u " +
           "WHERE u.username = :username " +
           "AND u.estadoUsuario = 1")
    Optional<Usuario> findActivoByUsername(@Param("username") String username);

    /**
     * Retorna todos los usuarios activos de una entidad específica.
     * Usado por el administrador RC para gestionar usuarios.
     *
     * @param idEntidad ID de la entidad a filtrar
     * @return lista de usuarios activos de esa entidad
     */
    List<Usuario> findByEntidad_IdEntidadAndEstadoUsuario(
            Long idEntidad, Integer estadoUsuario);
}