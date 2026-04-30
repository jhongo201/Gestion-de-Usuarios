package gov.idiger.rcontractual.service.impl;

import gov.idiger.rcontractual.dto.PermisoDTO;
import gov.idiger.rcontractual.dto.PermisoRequestDTO;
import gov.idiger.rcontractual.entity.Permiso;
import gov.idiger.rcontractual.exception.RecursoNoEncontradoException;
import gov.idiger.rcontractual.repository.PermisoRepository;
import gov.idiger.rcontractual.service.PermisoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de {@link PermisoService}.
 * Gestiona el árbol jerárquico de permisos.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Service
public class PermisoServiceImpl implements PermisoService {

    private static final Logger log =
        LoggerFactory.getLogger(PermisoServiceImpl.class);

    private final PermisoRepository permisoRepository;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param permisoRepository repositorio de permisos
     */
    public PermisoServiceImpl(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    /**
     * {@inheritDoc}
     * Retorna solo los nodos raíz con sus hijos anidados.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PermisoDTO> listarArbol() {
        log.debug("Construyendo árbol de permisos");
        return permisoRepository.findAll()
                .stream()
                .filter(p -> p.getPermisoPadre() == null)
                .map(this::toDTOConHijos)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<PermisoDTO> listarPlano() {
        log.debug("Listando todos los permisos (plano)");
        return permisoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public PermisoDTO buscarPorId(Long idPermiso) {
        log.debug("Buscando permiso ID: {}", idPermiso);
        Permiso permiso = permisoRepository.findById(idPermiso)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Permiso no encontrado con ID: " + idPermiso));
        return toDTO(permiso);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public PermisoDTO crear(PermisoRequestDTO request) {
        log.info("Creando permiso: {}", request.getDescripcion());

        Permiso permiso = new Permiso();
        permiso.setDescripcion(request.getDescripcion());
        permiso.setTipoEstilo(request.getTipoEstilo());
        permiso.setFuncionCarga(request.getFuncionCarga());
        permiso.setOrden(request.getOrden() != null ? request.getOrden() : 0);

        /* Asignar padre si fue especificado */
        if (request.getIdPermisoPadre() != null) {
            Permiso padre = permisoRepository
                    .findById(request.getIdPermisoPadre())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Permiso padre no encontrado con ID: "
                            + request.getIdPermisoPadre()));
            permiso.setPermisoPadre(padre);
        }

        Permiso guardado = permisoRepository.save(permiso);
        log.info("Permiso creado con ID: {}", guardado.getIdPermiso());
        return toDTO(guardado);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public PermisoDTO actualizar(Long idPermiso, PermisoRequestDTO request) {
        log.info("Actualizando permiso ID: {}", idPermiso);

        Permiso permiso = permisoRepository.findById(idPermiso)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Permiso no encontrado con ID: " + idPermiso));

        permiso.setDescripcion(request.getDescripcion());
        permiso.setTipoEstilo(request.getTipoEstilo());
        permiso.setFuncionCarga(request.getFuncionCarga());
        if (request.getOrden() != null) {
            permiso.setOrden(request.getOrden());
        }

        /* Actualizar padre si cambió */
        if (request.getIdPermisoPadre() != null) {
            Permiso padre = permisoRepository
                    .findById(request.getIdPermisoPadre())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Permiso padre no encontrado con ID: "
                            + request.getIdPermisoPadre()));
            permiso.setPermisoPadre(padre);
        } else {
            permiso.setPermisoPadre(null);
        }

        permisoRepository.save(permiso);
        log.info("Permiso actualizado: {}", idPermiso);
        return toDTO(permiso);
    }

    /**
     * Convierte una entidad Permiso a DTO sin hijos anidados.
     *
     * @param p entidad a convertir
     * @return DTO plano del permiso
     */
    private PermisoDTO toDTO(Permiso p) {
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
    }

    /**
     * Convierte un nodo raíz a DTO incluyendo sus hijos recursivamente.
     * Usado para construir el árbol completo de permisos.
     *
     * @param p entidad raíz a convertir
     * @return DTO con hijos anidados
     */
    private PermisoDTO toDTOConHijos(Permiso p) {
        PermisoDTO dto = toDTO(p);
        if (p.getHijos() != null && !p.getHijos().isEmpty()) {
            List<PermisoDTO> hijos = p.getHijos()
                    .stream()
                    .map(this::toDTOConHijos)
                    .collect(Collectors.toList());
            dto.setHijos(hijos);
        } else {
            dto.setHijos(new ArrayList<>());
        }
        return dto;
    }
}