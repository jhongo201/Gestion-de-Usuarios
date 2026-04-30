package gov.idiger.rcontractual.service.impl;

import gov.idiger.rcontractual.dto.LogAuditoriaDTO;
import gov.idiger.rcontractual.entity.ReporteContractual;
import gov.idiger.rcontractual.entity.ReporteContractualLog;
import gov.idiger.rcontractual.repository.ReporteContractualLogRepository;
import gov.idiger.rcontractual.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de {@link AuditoriaService}.
 * Centraliza el registro y consulta de todos los eventos
 * de ejecución y exportación del módulo (CU-05, CU-08).
 *
 * @author IDIGER – Equipo de Desarrollo
 */

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuditoriaServiceImpl.class);

    /** Repositorio JPA para acceso a REPORTE_CONTRACTUAL_LOG */
    private final ReporteContractualLogRepository logRepository;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param logRepository repositorio del log de auditoría
     */
    public AuditoriaServiceImpl(
            ReporteContractualLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * {@inheritDoc}
     * Persiste un log con ESTADO = 'OK'.
     * Se invoca siempre que una ejecución o exportación termina exitosamente.
     */
    @Override
    @Transactional
    public Long registrarEjecucionOk(ReporteContractual reporte,
                                      Long idUsuario,
                                      Long idEntidad,
                                      String filtrosJson,
                                      String formato,
                                      String ipCliente,
                                      String userAgent) {
        log.debug("Registrando ejecución OK para reporte ID: {}, usuario: {}",
                reporte.getIdReporte(), idUsuario);

        ReporteContractualLog logEntry = construirLog(
                reporte, idUsuario, idEntidad,
                filtrosJson, formato, ipCliente, userAgent,
                "OK", null);

        return logRepository.save(logEntry).getIdLog();
    }

    /**
     * {@inheritDoc}
     * Persiste un log con ESTADO = 'ERROR' y el mensaje capturado.
     * Se invoca cuando una ejecución o exportación falla.
     */
    @Override
    @Transactional
    public Long registrarEjecucionError(ReporteContractual reporte,
                                         Long idUsuario,
                                         Long idEntidad,
                                         String filtrosJson,
                                         String formato,
                                         String ipCliente,
                                         String userAgent,
                                         String mensajeError) {
        log.warn("Registrando ejecución ERROR para reporte ID: {}, usuario: {}, error: {}",
                reporte.getIdReporte(), idUsuario, mensajeError);

        ReporteContractualLog logEntry = construirLog(
                reporte, idUsuario, idEntidad,
                filtrosJson, formato, ipCliente, userAgent,
                "ERROR", mensajeError);

        return logRepository.save(logEntry).getIdLog();
    }

    /**
     * {@inheritDoc}
     * Retorna el historial personal del usuario ordenado por fecha.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LogAuditoriaDTO> consultarPorUsuario(Long idUsuario) {
        log.debug("Consultando auditoría del usuario ID: {}", idUsuario);
        return logRepository
                .findByIdUsuarioOrderByFechaEjecucionDesc(idUsuario)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Retorna el historial completo de una entidad.
     * Solo accesible por el administrador RC.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LogAuditoriaDTO> consultarPorEntidad(Long idEntidad) {
        log.debug("Consultando auditoría de la entidad ID: {}", idEntidad);
        return logRepository
                .findByIdEntidadOrderByFechaEjecucionDesc(idEntidad)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Retorna logs de un reporte en un rango de fechas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LogAuditoriaDTO> consultarPorReporteYFecha(Long idReporte,
                                                            OffsetDateTime desde,
                                                            OffsetDateTime hasta) {
        log.debug("Consultando auditoría reporte ID: {} entre {} y {}",
                idReporte, desde, hasta);
        return logRepository
                .findByReporteYRangoFecha(idReporte, desde, hasta)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Construye un objeto {@link ReporteContractualLog} con los datos comunes
     * tanto para ejecuciones exitosas como fallidas.
     * Método auxiliar privado para evitar duplicación de código.
     *
     * @param reporte      entidad del reporte ejecutado
     * @param idUsuario    ID del usuario
     * @param idEntidad    ID de la entidad
     * @param filtrosJson  filtros aplicados
     * @param formato      formato de exportación o null
     * @param ipCliente    IP del cliente
     * @param userAgent    User-Agent del navegador
     * @param estado       "OK" o "ERROR"
     * @param mensajeError mensaje de error o null si fue exitoso
     * @return entidad log lista para persistir
     */
    private ReporteContractualLog construirLog(ReporteContractual reporte,
                                                Long idUsuario,
                                                Long idEntidad,
                                                String filtrosJson,
                                                String formato,
                                                String ipCliente,
                                                String userAgent,
                                                String estado,
                                                String mensajeError) {
        ReporteContractualLog log = new ReporteContractualLog();
        log.setReporte(reporte);
        log.setIdUsuario(idUsuario);
        log.setIdEntidad(idEntidad);
        log.setFiltrosJson(filtrosJson);
        log.setFormato(formato);
        log.setFechaEjecucion(OffsetDateTime.now());
        log.setEstado(estado);
        log.setMensajeError(mensajeError);
        log.setIpCliente(ipCliente);
        log.setUserAgent(userAgent);
        return log;
    }

    /**
     * Convierte una entidad {@link ReporteContractualLog} a su DTO de respuesta.
     *
     * @param entidad entidad JPA a convertir
     * @return DTO con los datos del log
     */
    private LogAuditoriaDTO toDTO(ReporteContractualLog entidad) {
        return LogAuditoriaDTO.builder()
                .idLog(entidad.getIdLog())
                .idReporte(entidad.getReporte().getIdReporte())
                .nombreReporte(entidad.getReporte().getNombre())
                .idUsuario(entidad.getIdUsuario())
                .idEntidad(entidad.getIdEntidad())
                .filtrosJson(entidad.getFiltrosJson())
                .formato(entidad.getFormato())
                .fechaEjecucion(entidad.getFechaEjecucion())
                .estado(entidad.getEstado())
                .mensajeError(entidad.getMensajeError())
                .build();
    }
}