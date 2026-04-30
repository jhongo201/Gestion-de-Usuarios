package gov.idiger.rcontractual.service.impl;

import gov.idiger.rcontractual.dto.UsuarioDTO;
import gov.idiger.rcontractual.dto.UsuarioRequestDTO;
import gov.idiger.rcontractual.entity.Entidad;
import gov.idiger.rcontractual.entity.Rol;
import gov.idiger.rcontractual.entity.Usuario;
import gov.idiger.rcontractual.exception.RecursoNoEncontradoException;
import gov.idiger.rcontractual.exception.ReglaDeNegocioException;
import gov.idiger.rcontractual.repository.EntidadRepository;
import gov.idiger.rcontractual.repository.RolRepository;
import gov.idiger.rcontractual.repository.UsuarioRepository;
import gov.idiger.rcontractual.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de {@link UsuarioService}.
 * Gestiona el CRUD de usuarios hasheando contraseñas con BCrypt.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log =
        LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final EntidadRepository entidadRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param usuarioRepository repositorio de usuarios
     * @param rolRepository     repositorio de roles
     * @param entidadRepository repositorio de entidades
     * @param passwordEncoder   encoder BCrypt para contraseñas
     */
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                               RolRepository rolRepository,
                               EntidadRepository entidadRepository,
                               PasswordEncoder passwordEncoder) {
        this.usuarioRepository  = usuarioRepository;
        this.rolRepository      = rolRepository;
        this.entidadRepository  = entidadRepository;
        this.passwordEncoder    = passwordEncoder;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarActivos() {
        log.debug("Listando todos los usuarios activos");
        return usuarioRepository.findAll()
                .stream()
                .filter(u -> u.getEstadoUsuario() == 1)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarPorEntidad(Long idEntidad) {
        log.debug("Listando usuarios activos de entidad ID: {}", idEntidad);
        return usuarioRepository
                .findByEntidad_IdEntidadAndEstadoUsuario(idEntidad, 1)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long idUsuario) {
        log.debug("Buscando usuario ID: {}", idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con ID: " + idUsuario));
        return toDTO(usuario);
    }

    /**
     * {@inheritDoc}
     * Valida username único y hashea la contraseña antes de persistir.
     */
    @Override
    @Transactional
    public UsuarioDTO crear(UsuarioRequestDTO request) {
        log.info("Creando usuario: {}", request.getUsername());

        /* Validar username único */
        if (usuarioRepository.findActivoByUsername(request.getUsername()).isPresent()) {
            throw new ReglaDeNegocioException(
                    "Ya existe un usuario activo con el username: "
                    + request.getUsername());
        }

        /* Validar que la clave fue enviada en creación */
        if (request.getClave() == null || request.getClave().trim().isEmpty()) {
            throw new ReglaDeNegocioException(
                    "La contraseña es obligatoria al crear un usuario");
        }

        /* Cargar rol y entidad */
        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Rol no encontrado con ID: " + request.getIdRol()));

        Entidad entidad = entidadRepository.findById(request.getIdEntidad())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Entidad no encontrada con ID: " + request.getIdEntidad()));

        /* Construir entidad */
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setClave(passwordEncoder.encode(request.getClave()));
        usuario.setEstadoUsuario(1);
        usuario.setRol(rol);
        usuario.setEntidad(entidad);
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setApellidoUsuario(request.getApellidoUsuario());
        usuario.setNumDocUsu(request.getNumDocUsu());
        usuario.setTipoDocUsu(request.getTipoDocUsu());
        usuario.setCorreoUsuario(request.getCorreoUsuario());

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado con ID: {}", guardado.getIdUsuario());
        return toDTO(guardado);
    }

    /**
     * {@inheritDoc}
     * Si request.getClave() es null no modifica la contraseña actual.
     */
    @Override
    @Transactional
    public UsuarioDTO actualizar(Long idUsuario, UsuarioRequestDTO request) {
        log.info("Actualizando usuario ID: {}", idUsuario);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con ID: " + idUsuario));

        /* Validar username único si cambió */
        if (!usuario.getUsername().equals(request.getUsername())) {
            if (usuarioRepository.findActivoByUsername(
                    request.getUsername()).isPresent()) {
                throw new ReglaDeNegocioException(
                        "Ya existe un usuario con el username: "
                        + request.getUsername());
            }
            usuario.setUsername(request.getUsername());
        }

        /* Actualizar clave solo si fue enviada */
        if (request.getClave() != null && !request.getClave().trim().isEmpty()) {
            usuario.setClave(passwordEncoder.encode(request.getClave()));
            log.info("Contraseña actualizada para usuario ID: {}", idUsuario);
        }

        /* Actualizar rol si cambió */
        if (!usuario.getRol().getIdRol().equals(request.getIdRol())) {
            Rol rol = rolRepository.findById(request.getIdRol())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Rol no encontrado con ID: " + request.getIdRol()));
            usuario.setRol(rol);
        }

        /* Actualizar entidad si cambió */
        if (!usuario.getEntidad().getIdEntidad().equals(request.getIdEntidad())) {
            Entidad entidad = entidadRepository.findById(request.getIdEntidad())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Entidad no encontrada con ID: " + request.getIdEntidad()));
            usuario.setEntidad(entidad);
        }

        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setApellidoUsuario(request.getApellidoUsuario());
        usuario.setNumDocUsu(request.getNumDocUsu());
        usuario.setTipoDocUsu(request.getTipoDocUsu());
        usuario.setCorreoUsuario(request.getCorreoUsuario());

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado correctamente: {}", idUsuario);
        return toDTO(actualizado);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void desactivar(Long idUsuario) {
        log.info("Desactivando usuario ID: {}", idUsuario);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con ID: " + idUsuario));

        usuario.setEstadoUsuario(0);
        usuarioRepository.save(usuario);
        log.info("Usuario desactivado: {}", idUsuario);
    }

    /**
     * Convierte una entidad {@link Usuario} a su DTO de respuesta.
     *
     * @param u entidad a convertir
     * @return DTO con los datos del usuario (sin clave)
     */
    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setUsername(u.getUsername());
        dto.setEstadoUsuario(u.getEstadoUsuario());
        dto.setIdRol(u.getRol().getIdRol());
        dto.setNombreRol(u.getRol().getNombreRol());
        dto.setIdEntidad(u.getEntidad().getIdEntidad());
        dto.setNombreEntidad(u.getEntidad().getDescripcion());
        dto.setNombreUsuario(u.getNombreUsuario());
        dto.setApellidoUsuario(u.getApellidoUsuario());
        dto.setNumDocUsu(u.getNumDocUsu());
        dto.setTipoDocUsu(u.getTipoDocUsu());
        dto.setCorreoUsuario(u.getCorreoUsuario());
        dto.setFechaCreacion(u.getFechaCreacion());
        return dto;
    }
}