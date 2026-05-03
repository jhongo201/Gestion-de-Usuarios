package gov.idiger.rcontractual.repository;

import gov.idiger.rcontractual.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Usuario.
 * Gestiona el acceso a CONTRATOS.USUARIO.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario activo por username.
     *
     * Se usa cuando el flujo necesita permitir ingreso solo a usuarios activos.
     */
    @Query("SELECT u FROM Usuario u " +
           "WHERE LOWER(u.username) = LOWER(:username) " +
           "AND u.estadoUsuario = 1")
    Optional<Usuario> findActivoByUsername(@Param("username") String username);

    /**
     * Busca cualquier usuario por username, sin filtrar estado.
     *
     * Se usa en autenticacion para poder detectar si existe pero esta inactivo
     * o pendiente de aprobacion y mostrar mensajes adecuados.
     */
    @Query("SELECT u FROM Usuario u " +
           "WHERE LOWER(u.username) = LOWER(:username)")
    Optional<Usuario> findByUsernameIgnoreCase(@Param("username") String username);

    /**
     * Busca usuario activo por username o correo.
     *
     * Se usara en el flujo de recuperacion de contrasena con OTP.
     */
    @Query("SELECT u FROM Usuario u " +
           "WHERE u.estadoUsuario = 1 " +
           "AND (LOWER(u.username) = LOWER(:valor) " +
           "OR LOWER(u.correoUsuario) = LOWER(:valor))")
    Optional<Usuario> findActivoByUsernameOrCorreo(@Param("valor") String valor);

    /**
     * Valida si ya existe un username, sin importar estado.
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * Valida si ya existe un correo, sin importar estado.
     */
    boolean existsByCorreoUsuarioIgnoreCase(String correoUsuario);

    /**
     * Valida si ya existe un documento registrado.
     */
    boolean existsByTipoDocUsuAndNumDocUsu(String tipoDocUsu, String numDocUsu);

    /**
     * Retorna usuarios por estado.
     * Estados: 0 = Inactivo, 1 = Activo, 2 = Pendiente.
     */
    List<Usuario> findByEstadoUsuario(Integer estadoUsuario);

    /**
     * Cuenta usuarios pendientes de aprobacion.
     */
    long countByEstadoUsuario(Integer estadoUsuario);

    /**
     * Retorna todos los usuarios de una entidad filtrados por estado.
     */
    List<Usuario> findByEntidad_IdEntidadAndEstadoUsuario(
            Long idEntidad, Integer estadoUsuario);

    /**
     * Retorna todos los usuarios de una entidad, sin filtrar estado.
     */
    List<Usuario> findByEntidad_IdEntidad(Long idEntidad);
}
