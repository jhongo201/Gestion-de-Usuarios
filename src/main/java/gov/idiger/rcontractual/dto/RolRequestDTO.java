package gov.idiger.rcontractual.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO de entrada para crear o editar un rol.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
public class RolRequestDTO {

    /**
     * Nombre del rol.
     * Obligatorio, máximo 100 caracteres.
     * Convención: mayúsculas con guión bajo (ej: ADMIN_RC).
     */
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombreRol;

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}