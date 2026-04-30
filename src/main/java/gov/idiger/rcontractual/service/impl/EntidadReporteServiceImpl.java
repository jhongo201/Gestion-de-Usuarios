package gov.idiger.rcontractual.service.impl;

import gov.idiger.rcontractual.dto.ReporteContractualDTO;
import gov.idiger.rcontractual.entity.ReporteContractualEntidad;
import gov.idiger.rcontractual.entity.ReporteContractualEntidadId;
import gov.idiger.rcontractual.exception.RecursoNoEncontradoException;
import gov.idiger.rcontractual.repository.ReporteContractualEntidadRepository;
import gov.idiger.rcontractual.repository.ReporteContractualRepository;
import gov.idiger.rcontractual.service.EntidadReporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de {@link EntidadReporteService}.
 * Gestiona la habilitación y deshabilitación de reportes
 * por entidad (CU-07), y provee la validación de acceso
 * que usa {@link EjecucionReporteServiceImpl} antes de ejecutar.
 *
 * @author IDIGER – Equipo de Desarrollo
 */

@Service
public class EntidadReporteServiceImpl implements EntidadReporteService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EntidadReporteServiceImpl.class);

    /** Repositorio de la tabla de relación reporte–entidad */
    private final ReporteContractualEntidadRepository entidadRepository;

    /** Repositorio del catálogo de reportes */
    private final ReporteContractualRepository reporteRepository;

    /**
 * Constructor para inyección de dependencias.
 * Reemplaza la anotación @RequiredArgsConstructor de Lombok.
 *
 * @param entidadRepository repositorio de la relación reporte–entidad
 * @param reporteRepository repositorio del catálogo de reportes
 */
public EntidadReporteServiceImpl(
        ReporteContractualEntidadRepository entidadRepository,
        ReporteContractualRepository reporteRepository) {
    this.entidadRepository = entidadRepository;
    this.reporteRepository = reporteRepository;
}

    /**
     * {@inheritDoc}
     * Si ya existe la relación pero está inactiva, la reactiva.
     * Si no existe, crea un nuevo registro en la tabla de relación.
     */
    @Override
    @Transactional
    public void habilitar(Long idReporte, Long idEntidad) {
        log.info("Habilitando reporte ID: {} para entidad ID: {}",
                idReporte, idEntidad);

        /* Verificar que el reporte existe */
        reporteRepository.findById(idReporte)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reporte no encontrado con ID: " + idReporte));

        ReporteContractualEntidadId pk =
                new ReporteContractualEntidadId(idReporte, idEntidad);

        Optional<ReporteContractualEntidad> existente =
                entidadRepository.findById(pk);

        if (existente.isPresent()) {
            /* Reactivar si ya existía deshabilitada */
            entidadRepository.actualizarEstado(idReporte, idEntidad, 1);
            log.info("Relación reactivada: reporte {} – entidad {}",
                    idReporte, idEntidad);
        } else {
            /* Crear nueva relación */
            ReporteContractualEntidad nueva = new ReporteContractualEntidad();
            nueva.setIdReporte(idReporte);
            nueva.setIdEntidad(idEntidad);
            nueva.setActivo(1);
            entidadRepository.save(nueva);
            log.info("Nueva relación creada: reporte {} – entidad {}",
                    idReporte, idEntidad);
        }
    }

    /**
     * {@inheritDoc}
     * Desactivación lógica: pone ACTIVO = 0.
     * Lanza excepción si la relación no existe.
     */
    @Override
    @Transactional
    public void deshabilitar(Long idReporte, Long idEntidad) {
        log.info("Deshabilitando reporte ID: {} para entidad ID: {}",
                idReporte, idEntidad);

        ReporteContractualEntidadId pk =
                new ReporteContractualEntidadId(idReporte, idEntidad);

        entidadRepository.findById(pk)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe asignación del reporte " + idReporte
                        + " para la entidad " + idEntidad));

        entidadRepository.actualizarEstado(idReporte, idEntidad, 0);
        log.info("Relación deshabilitada: reporte {} – entidad {}",
                idReporte, idEntidad);
    }

    /**
     * {@inheritDoc}
     * Retorna los reportes activos de una entidad como DTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReporteContractualDTO> listarReportesPorEntidad(Long idEntidad) {
        log.debug("Listando reportes habilitados para entidad ID: {}", idEntidad);

        return entidadRepository
                .findByIdEntidadAndActivo(idEntidad, 1)
                .stream()
                .map(rel -> {
                    /* Cargar el reporte desde el catálogo para armar el DTO */
                    return reporteRepository.findById(rel.getIdReporte())
                            .map(r -> {
                                ReporteContractualDTO dto = new ReporteContractualDTO();
                                dto.setIdReporte(r.getIdReporte());
                                dto.setNombre(r.getNombre());
                                dto.setDescripcion(r.getDescripcion());
                                dto.setActivo(r.getActivo());
                                dto.setFechaCreacion(r.getFechaCreacion());
                                return dto;
                            }).orElse(null);
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Usado como validación de seguridad en la capa de ejecución.
     * Retorna true solo si la relación existe y está activa (ACTIVO = 1).
     */
    @Override
    @Transactional(readOnly = true)
    public boolean estaHabilitado(Long idReporte, Long idEntidad) {
        boolean habilitado = entidadRepository
                .existsByIdReporteAndIdEntidadAndActivo(idReporte, idEntidad, 1);
        log.debug("Verificación acceso reporte {} entidad {}: {}",
                idReporte, idEntidad, habilitado);
        return habilitado;
    }
}