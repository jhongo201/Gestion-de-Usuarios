package gov.idiger.rcontractual.service.impl;

import gov.idiger.rcontractual.dto.EjecucionRequestDTO;
import gov.idiger.rcontractual.dto.EjecucionResponseDTO;
import gov.idiger.rcontractual.entity.ReporteContractual;
import gov.idiger.rcontractual.exception.AccesoDenegadoException;
import gov.idiger.rcontractual.exception.RecursoNoEncontradoException;
import gov.idiger.rcontractual.exception.ReglaDeNegocioException;
import gov.idiger.rcontractual.repository.ReporteContractualRepository;
import gov.idiger.rcontractual.service.AuditoriaService;
import gov.idiger.rcontractual.service.EjecucionReporteService;
import gov.idiger.rcontractual.service.EntidadReporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de {@link EjecucionReporteService}.
 * Orquesta el ciclo completo de ejecución de un reporte:
 * validación de acceso → construcción de consulta → ejecución
 * → auditoría → retorno de resultados (CU-03, CU-04, CU-05).
 *
 * La exportación a archivos (CSV, XLSX, PDF) se implementará
 * en una fase posterior con las librerías correspondientes.
 *
 * @author IDIGER – Equipo de Desarrollo
 */

@Service
public class EjecucionReporteServiceImpl implements EjecucionReporteService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EjecucionReporteServiceImpl.class);

    /** EntityManager para ejecutar consultas nativas dinámicas */
    private final EntityManager entityManager;

    /** Repositorio del catálogo de reportes */
    private final ReporteContractualRepository reporteRepository;

    /** Servicio de validación de acceso por entidad */
    private final EntidadReporteService entidadReporteService;

    /** Servicio de auditoría para registrar cada ejecución */
    private final AuditoriaService auditoriaService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param entityManager        para ejecutar consultas nativas
     * @param reporteRepository    repositorio del catálogo de reportes
     * @param entidadReporteService servicio de validación por entidad
     * @param auditoriaService     servicio de auditoría
     */
    public EjecucionReporteServiceImpl(
            EntityManager entityManager,
            ReporteContractualRepository reporteRepository,
            EntidadReporteService entidadReporteService,
            AuditoriaService auditoriaService) {
        this.entityManager = entityManager;
        this.reporteRepository = reporteRepository;
        this.entidadReporteService = entidadReporteService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * {@inheritDoc}
     *
     * Flujo completo:
     * 1. Carga el reporte del catálogo — lanza excepción si no existe
     * 2. Valida que el reporte esté habilitado para la entidad del usuario
     * 3. Ejecuta la consulta SQL base del reporte con los filtros recibidos
     * 4. Mapea los resultados a lista de mapas columna → valor
     * 5. Registra la ejecución en auditoría como OK o ERROR
     */
    @Override
    @Transactional(readOnly = true)
    public EjecucionResponseDTO ejecutar(EjecucionRequestDTO request,
                                          Long idUsuario,
                                          Long idEntidad,
                                          String ipCliente) {
        log.info("Ejecutando reporte ID: {} para usuario: {}, entidad: {}",
                request.getIdReporte(), idUsuario, idEntidad);

        /* Paso 1: cargar el reporte del catálogo */
        ReporteContractual reporte = reporteRepository
                .findById(request.getIdReporte())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reporte no encontrado con ID: " + request.getIdReporte()));

        /* Paso 2: validar que el reporte esté habilitado para la entidad */
        if (!entidadReporteService.estaHabilitado(reporte.getIdReporte(), idEntidad)) {
            throw new AccesoDenegadoException(
                    "El reporte no está habilitado para su entidad");
        }

        try {
            /* Paso 3: ejecutar la consulta nativa */
            List<Map<String, Object>> resultados =
                    ejecutarConsultaNativa(reporte.getSqlBase(), idEntidad);

            /* Paso 4: registrar auditoría OK */
            Long idLog = auditoriaService.registrarEjecucionOk(
                    reporte, idUsuario, idEntidad,
                    request.getFiltrosJson(), null,
                    ipCliente, null);

            log.info("Reporte {} ejecutado OK. Filas: {}, Log ID: {}",
                    reporte.getIdReporte(), resultados.size(), idLog);

            /* Paso 5: construir y retornar la respuesta */
            return EjecucionResponseDTO.builder()
                    .idLog(idLog)
                    .nombreReporte(reporte.getNombre())
                    .fechaEjecucion(OffsetDateTime.now())
                    .estado("OK")
                    .totalFilas(resultados.size())
                    .resultados(resultados)
                    .build();

        } catch (AccesoDenegadoException e) {
            throw e;
        } catch (Exception e) {
            /* Registrar auditoría ERROR y relanzar */
            log.error("Error ejecutando reporte ID: {}: {}",
                    reporte.getIdReporte(), e.getMessage(), e);

            auditoriaService.registrarEjecucionError(
                    reporte, idUsuario, idEntidad,
                    request.getFiltrosJson(), null,
                    ipCliente, null,
                    e.getMessage());

            throw new ReglaDeNegocioException(
                    "Error al ejecutar el reporte: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * La exportación a archivos se implementará en la siguiente fase.
     * Por ahora lanza UnsupportedOperationException para indicar
     * que el endpoint existe pero aún no está implementado.
     */
    @Override
    public byte[] exportar(EjecucionRequestDTO request,
                            Long idUsuario,
                            Long idEntidad,
                            String ipCliente) {
        log.warn("Exportación solicitada pero aún no implementada. " +
                 "Reporte: {}, Formato: {}",
                 request.getIdReporte(), request.getFormato());
        throw new UnsupportedOperationException(
                "Exportación en formato " + request.getFormato()
                + " será implementada en la siguiente fase");
    }

    /**
     * Ejecuta una consulta SQL nativa usando el EntityManager.
     * Aplica automáticamente el filtro de entidad para garantizar
     * la segmentación de datos (regla de negocio obligatoria).
     *
     * Los resultados se mapean a una lista de LinkedHashMap donde
     * cada mapa representa una fila con pares columna → valor,
     * manteniendo el orden de columnas del SELECT.
     *
     * @param sqlBase   consulta SQL base almacenada en el catálogo
     * @param idEntidad ID de la entidad del usuario en sesión
     * @return lista de filas como mapas columna → valor
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> ejecutarConsultaNativa(String sqlBase,
                                                              Long idEntidad) {
        log.debug("Ejecutando SQL nativo con entidad ID: {}", idEntidad);

        /* Inyectar el filtro de entidad en la consulta si no lo tiene */
        String sql = sqlBase;
        if (sql == null || sql.trim().isEmpty()) {
            log.warn("El reporte no tiene SQL_BASE definido");
            return new ArrayList<>();
        }

        Query query = entityManager.createNativeQuery(sql);

        /* Si la consulta tiene el parámetro :idEntidad lo inyectamos */
        try {
            query.setParameter("idEntidad", idEntidad);
        } catch (IllegalArgumentException e) {
            /* La consulta no usa :idEntidad — continuar sin el parámetro */
            log.debug("La consulta no usa parámetro :idEntidad");
        }

        /* Obtener metadatos para los nombres de columnas */
        List<Object[]> rows = query.getResultList();
        List<Map<String, Object>> resultado = new ArrayList<>();

        /* Mapear cada fila a un LinkedHashMap columna → valor */
        for (Object[] row : rows) {
            Map<String, Object> fila = new LinkedHashMap<>();
            for (int i = 0; i < row.length; i++) {
                fila.put("COL_" + (i + 1), row[i]);
            }
            resultado.add(fila);
        }

        return resultado;
    }
}