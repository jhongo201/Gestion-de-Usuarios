package gov.idiger.rcontractual.service.impl;

import gov.idiger.rcontractual.dto.ReporteContractualDTO;
import gov.idiger.rcontractual.dto.ReporteContractualRequestDTO;
import gov.idiger.rcontractual.entity.ReporteContractual;
import gov.idiger.rcontractual.exception.RecursoNoEncontradoException;
import gov.idiger.rcontractual.exception.ReglaDeNegocioException;
import gov.idiger.rcontractual.repository.ReporteContractualRepository;
import gov.idiger.rcontractual.service.ReporteContractualService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de {@link ReporteContractualService}.
 * Gestiona el catálogo de reportes: consulta, creación,
 * actualización y desactivación (CU-02, CU-06).
 *
 * @author IDIGER – Equipo de Desarrollo
 */

@Service
public class ReporteContractualServiceImpl implements ReporteContractualService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReporteContractualServiceImpl.class);

    /** Repositorio JPA para acceso a REPORTE_CONTRACTUAL */
    private final ReporteContractualRepository reporteRepository;
    

    /**
     * Constructor para inyección de dependencias.
     *
     * @param reporteRepository repositorio del catálogo de reportes
     */
    public ReporteContractualServiceImpl(
            ReporteContractualRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    /**
     * {@inheritDoc}
     * Filtra por entidad usando la vista lógica del repositorio.
     * Garantiza que cada usuario solo vea los reportes de su entidad.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReporteContractualDTO> listarPorEntidad(Long idEntidad) {
        log.debug("Listando reportes activos para entidad ID: {}", idEntidad);
        return reporteRepository.findActivosPorEntidad(idEntidad)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Sin filtro de entidad — uso exclusivo del administrador RC.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReporteContractualDTO> listarTodos() {
        log.debug("Listando todos los reportes activos");
        return reporteRepository.findByActivo(1)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Lanza {@link RecursoNoEncontradoException} si el ID no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public ReporteContractualDTO buscarPorId(Long idReporte) {
        log.debug("Buscando reporte con ID: {}", idReporte);
        ReporteContractual entidad = reporteRepository.findById(idReporte)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reporte no encontrado con ID: " + idReporte));
        return toDTO(entidad);
    }

    /**
     * {@inheritDoc}
     * Valida que no exista otro reporte activo con el mismo nombre
     * antes de persistir para evitar duplicados en el catálogo.
     */
    @Override
    @Transactional
    public ReporteContractualDTO crear(ReporteContractualRequestDTO request) {
        log.info("Creando nuevo reporte: {}", request.getNombre());

        /* Validar nombre duplicado */
        if (reporteRepository.existsByNombreAndActivo(request.getNombre(), 1)) {
            throw new ReglaDeNegocioException(
                    "Ya existe un reporte activo con el nombre: " + request.getNombre());
        }

        /* Construir y guardar la entidad */
        ReporteContractual entidad = new ReporteContractual();
        entidad.setNombre(request.getNombre());
        entidad.setDescripcion(request.getDescripcion());
        entidad.setSqlBase(request.getSqlBase());
        entidad.setIdUsuarioCrea(request.getIdUsuarioCrea());
        entidad.setActivo(1);

        ReporteContractual guardado = reporteRepository.save(entidad);
        log.info("Reporte creado con ID: {}", guardado.getIdReporte());
        return toDTO(guardado);
    }

    /**
     * {@inheritDoc}
     * Solo actualiza los campos editables: nombre, descripción y sqlBase.
     * No modifica fecha de creación ni usuario creador.
     */
    @Override
    @Transactional
    public ReporteContractualDTO actualizar(Long idReporte,
                                             ReporteContractualRequestDTO request) {
        log.info("Actualizando reporte ID: {}", idReporte);

        ReporteContractual entidad = reporteRepository.findById(idReporte)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reporte no encontrado con ID: " + idReporte));

        /* Validar nombre duplicado solo si cambió */
        if (!entidad.getNombre().equals(request.getNombre())
                && reporteRepository.existsByNombreAndActivo(request.getNombre(), 1)) {
            throw new ReglaDeNegocioException(
                    "Ya existe un reporte activo con el nombre: " + request.getNombre());
        }

        entidad.setNombre(request.getNombre());
        entidad.setDescripcion(request.getDescripcion());
        entidad.setSqlBase(request.getSqlBase());

        ReporteContractual actualizado = reporteRepository.save(entidad);
        log.info("Reporte actualizado correctamente: {}", idReporte);
        return toDTO(actualizado);
    }

    /**
     * {@inheritDoc}
     * Desactivación lógica: pone ACTIVO = 0 en lugar de eliminar
     * para preservar la trazabilidad de los logs históricos.
     */
    @Override
    @Transactional
    public void desactivar(Long idReporte) {
        log.info("Desactivando reporte ID: {}", idReporte);

        ReporteContractual entidad = reporteRepository.findById(idReporte)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reporte no encontrado con ID: " + idReporte));

        entidad.setActivo(0);
        reporteRepository.save(entidad);
        log.info("Reporte desactivado correctamente: {}", idReporte);
    }

    /**
     * Convierte una entidad {@link ReporteContractual} a su DTO de respuesta.
     * Método auxiliar usado internamente en todos los métodos del servicio.
     *
     * @param entidad entidad JPA a convertir
     * @return DTO con los datos del reporte
     */
    private ReporteContractualDTO toDTO(ReporteContractual entidad) {
        ReporteContractualDTO dto = new ReporteContractualDTO();
        dto.setIdReporte(entidad.getIdReporte());
        dto.setNombre(entidad.getNombre());
        dto.setDescripcion(entidad.getDescripcion());
        dto.setActivo(entidad.getActivo());
        dto.setFechaCreacion(entidad.getFechaCreacion());
        return dto;
    }
}