package gov.idiger.rcontractual.service;

import gov.idiger.rcontractual.dto.ReporteContractualDTO;
import gov.idiger.rcontractual.dto.ReporteContractualRequestDTO;

import java.util.List;

/**
 * Interfaz de servicio para la gestión del catálogo de reportes.
 * Define las operaciones disponibles para consultar, crear,
 * actualizar y desactivar reportes del módulo (CU-02, CU-06).
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public interface ReporteContractualService {

    /**
     * Retorna todos los reportes activos habilitados para una entidad.
     * Aplica el filtro de segmentación por entidad obligatorio (CU-02).
     *
     * @param idEntidad ID de la entidad del usuario en sesión
     * @return lista de reportes disponibles para esa entidad
     */
    List<ReporteContractualDTO> listarPorEntidad(Long idEntidad);

    /**
     * Retorna todos los reportes activos sin filtro de entidad.
     * Solo accesible por el administrador RC (CU-06).
     *
     * @return lista completa de reportes activos
     */
    List<ReporteContractualDTO> listarTodos();

    /**
     * Busca un reporte por su ID.
     *
     * @param idReporte ID del reporte a buscar
     * @return DTO del reporte encontrado
     * @throws gov.idiger.rcontractual.exception.RecursoNoEncontradoException
     *         si no existe un reporte con ese ID
     */
    ReporteContractualDTO buscarPorId(Long idReporte);

    /**
     * Crea un nuevo reporte en el catálogo (CU-06).
     * Valida que no exista otro reporte activo con el mismo nombre.
     *
     * @param request DTO con los datos del nuevo reporte
     * @return DTO del reporte creado con su ID asignado
     */
    ReporteContractualDTO crear(ReporteContractualRequestDTO request);

    /**
     * Actualiza los datos de un reporte existente (CU-06).
     *
     * @param idReporte ID del reporte a actualizar
     * @param request   DTO con los nuevos datos
     * @return DTO del reporte actualizado
     */
    ReporteContractualDTO actualizar(Long idReporte, ReporteContractualRequestDTO request);

    /**
     * Desactiva un reporte del catálogo (ACTIVO = 0).
     * No elimina el registro para preservar la trazabilidad
     * de los logs históricos asociados.
     *
     * @param idReporte ID del reporte a desactivar
     */
    void desactivar(Long idReporte);
}