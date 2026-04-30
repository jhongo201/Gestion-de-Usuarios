package gov.idiger.rcontractual.service.impl;

import gov.idiger.rcontractual.dto.PermisoDTO;
import gov.idiger.rcontractual.dto.RolDTO;
import gov.idiger.rcontractual.dto.RolRequestDTO;
import gov.idiger.rcontractual.entity.PermisoRol;
import gov.idiger.rcontractual.entity.Rol;
import gov.idiger.rcontractual.exception.RecursoNoEncontradoException;
import gov.idiger.rcontractual.exception.ReglaDeNegocioException;
import gov.idiger.rcontractual.repository.PermisoRepository;
import gov.idiger.rcontractual.repository.PermisoRolRepository;
import gov.idiger.rcontractual.repository.RolRepository;
import gov.idiger.rcontractual.service.RolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación de {@link RolService}.
 * Gestiona roles y la asignación de permisos a roles.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Service
public class RolServiceImpl implements RolService {

    private static final Logger log =
        LoggerFactory.getLogger(RolServiceImpl.class);

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final PermisoRolRepository permisoRolRepository;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param rolRepository        repositorio de roles
     * @param permisoRepository    repositorio de permisos
     * @param permisoRolRepository repositorio de asignaciones
     */
    public RolServiceImpl(RolRepository rolRepository,
                           PermisoRepository permisoRepository,
                           PermisoRolRepository permisoRolRepository) {
        this.rolRepository        = rolRepository;
        this.permisoRepository    = permisoRepository;
        this.permisoRolRepository = permisoRolRepository;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listarTodos() {
        log.debug("Listando todos los roles");

        /* Construir mapa de conteo de usuarios por rol */
        Map<Long, Long> conteo = new HashMap<>();
        rolRepository.contarUsuariosPorRol()
                .forEach(row -> conteo.put((Long) row[0], (Long) row[1]));

        return rolRepository.findAll()
                .stream()
                .map(r -> {
                    RolDTO dto = new RolDTO();
                    dto.setIdRol(r.getIdRol());
                    dto.setNombreRol(r.getNombreRol());
                    dto.setTotalUsuarios(conteo.getOrDefault(r.getIdRol(), 0L));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public RolDTO buscarPorId(Long idRol) {
        log.debug("Buscando rol ID: {}", idRol);
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Rol no encontrado con ID: " + idRol));
        RolDTO dto = new RolDTO();
        dto.setIdRol(rol.getIdRol());
        dto.setNombreRol(rol.getNombreRol());
        return dto;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public RolDTO crear(RolRequestDTO request) {
        log.info("Creando rol: {}", request.getNombreRol());

        if (rolRepository.existsByNombreRol(request.getNombreRol())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un rol con el nombre: " + request.getNombreRol());
        }

        Rol rol = new Rol();
        rol.setNombreRol(request.getNombreRol().toUpperCase().trim());

        Rol guardado = rolRepository.save(rol);
        log.info("Rol creado con ID: {}", guardado.getIdRol());

        RolDTO dto = new RolDTO();
        dto.setIdRol(guardado.getIdRol());
        dto.setNombreRol(guardado.getNombreRol());
        dto.setTotalUsuarios(0L);
        return dto;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public RolDTO actualizar(Long idRol, RolRequestDTO request) {
        log.info("Actualizando rol ID: {}", idRol);

        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Rol no encontrado con ID: " + idRol));

        if (!rol.getNombreRol().equals(request.getNombreRol())
                && rolRepository.existsByNombreRol(request.getNombreRol())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un rol con el nombre: " + request.getNombreRol());
        }

        rol.setNombreRol(request.getNombreRol().toUpperCase().trim());
        rolRepository.save(rol);

        RolDTO dto = new RolDTO();
        dto.setIdRol(rol.getIdRol());
        dto.setNombreRol(rol.getNombreRol());
        return dto;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void asignarPermiso(Long idRol, Long idPermiso) {
        log.info("Asignando permiso {} al rol {}", idPermiso, idRol);

        /* Verificar que el rol existe */
        if (!rolRepository.existsById(idRol)) {
            throw new RecursoNoEncontradoException(
                    "Rol no encontrado con ID: " + idRol);
        }

        /* Verificar que el permiso existe */
        if (!permisoRepository.existsById(idPermiso)) {
            throw new RecursoNoEncontradoException(
                    "Permiso no encontrado con ID: " + idPermiso);
        }

        /* Idempotente: no duplicar si ya existe */
        if (permisoRolRepository.existsByIdRolAndIdPermiso(idRol, idPermiso)) {
            log.debug("El permiso {} ya está asignado al rol {}", idPermiso, idRol);
            return;
        }

        PermisoRol permisoRol = new PermisoRol();
        permisoRol.setIdRol(idRol);
        permisoRol.setIdPermiso(idPermiso);
        permisoRol.setFecha(new Date());
        permisoRolRepository.save(permisoRol);

        log.info("Permiso {} asignado al rol {}", idPermiso, idRol);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void quitarPermiso(Long idRol, Long idPermiso) {
        log.info("Quitando permiso {} del rol {}", idPermiso, idRol);

        if (!permisoRolRepository.existsByIdRolAndIdPermiso(idRol, idPermiso)) {
            throw new RecursoNoEncontradoException(
                    "El permiso " + idPermiso
                    + " no está asignado al rol " + idRol);
        }

        permisoRolRepository.deleteById(
                new gov.idiger.rcontractual.entity.PermisoRolId(idRol, idPermiso));

        log.info("Permiso {} quitado del rol {}", idPermiso, idRol);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<PermisoDTO> listarPermisosPorRol(Long idRol) {
        log.debug("Listando permisos del rol ID: {}", idRol);
        return permisoRepository.findByRol(idRol)
                .stream()
                .map(p -> {
                    PermisoDTO dto = new PermisoDTO();
                    dto.setIdPermiso(p.getIdPermiso());
                    dto.setDescripcion(p.getDescripcion());
                    dto.setTipoEstilo(p.getTipoEstilo());
                    dto.setFuncionCarga(p.getFuncionCarga());
                    dto.setOrden(p.getOrden());
                    if (p.getPermisoPadre() != null) {
                        dto.setIdPermisoPadre(p.getPermisoPadre().getIdPermiso());
                        dto.setDescripcionPadre(p.getPermisoPadre().getDescripcion());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}