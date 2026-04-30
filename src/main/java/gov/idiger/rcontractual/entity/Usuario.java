package gov.idiger.rcontractual.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad JPA mapeada a CONTRATOS.USUARIO.
 * Representa los usuarios del sistema.
 * La clave se almacena hasheada con BCrypt —
 * nunca se almacena ni se expone en texto plano.
 *
 * @author IDIGER – Equipo de Desarrollo
 */
@Entity
@Table(name = "USUARIO", schema = "CONTRATOS")
public class Usuario {

    /** Identificador único del usuario */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "seq_usuario")
    @SequenceGenerator(
        name         = "seq_usuario",
        sequenceName = "CONTRATOS.SEQ_USUARIO",
        allocationSize = 1
    )
    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    /** Login único del usuario */
    @Column(name = "USUARIO", nullable = false, length = 100, unique = true)
    private String username;

    /** Contraseña hasheada con BCrypt */
    @Column(name = "CLAVE", nullable = false, length = 200)
    private String clave;

    /** Estado: 1 = activo, 0 = inactivo */
    @Column(name = "ESTADO_USUARIO", nullable = false)
    private Integer estadoUsuario;

    /**
     * Rol del usuario — determina sus permisos.
     * Cargado de forma EAGER porque siempre se necesita
     * en el proceso de autenticación.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ROL", nullable = false)
    private Rol rol;

    /**
     * Entidad a la que pertenece el usuario.
     * Define el alcance de datos que puede ver.
     * Cargado EAGER porque se necesita en la sesión.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ENTIDAD", nullable = false)
    private Entidad entidad;

    /** Nombre del usuario */
    @Column(name = "NOMBRE_USUARIO", length = 100)
    private String nombreUsuario;

    /** Apellido del usuario */
    @Column(name = "APELLIDO_USUARIO", length = 100)
    private String apellidoUsuario;

    /** Número de documento de identidad */
    @Column(name = "NUM_DOC_USU", length = 20)
    private String numDocUsu;

    /** Correo electrónico */
    @Column(name = "CORREO_USUARIO", length = 200)
    private String correoUsuario;

    /** Fecha y hora de creación con zona horaria */
    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    /** ID del usuario que creó este registro */
    @Column(name = "ID_USU_CREADOR")
    private Long idUsuCreador;

    /** Tipo de documento (CC, CE, NIT, etc.) */
    @Column(name = "TIPO_DOC_USU", length = 10)
    private String tipoDocUsu;

    /** Asigna la fecha de creación antes de insertar */
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = OffsetDateTime.now();
        }
        if (this.estadoUsuario == null) {
            this.estadoUsuario = 1;
        }
    }

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public Integer getEstadoUsuario() { return estadoUsuario; }
    public void setEstadoUsuario(Integer estadoUsuario) { this.estadoUsuario = estadoUsuario; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Entidad getEntidad() { return entidad; }
    public void setEntidad(Entidad entidad) { this.entidad = entidad; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getApellidoUsuario() { return apellidoUsuario; }
    public void setApellidoUsuario(String apellidoUsuario) { this.apellidoUsuario = apellidoUsuario; }

    public String getNumDocUsu() { return numDocUsu; }
    public void setNumDocUsu(String numDocUsu) { this.numDocUsu = numDocUsu; }

    public String getCorreoUsuario() { return correoUsuario; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }

    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Long getIdUsuCreador() { return idUsuCreador; }
    public void setIdUsuCreador(Long idUsuCreador) { this.idUsuCreador = idUsuCreador; }

    public String getTipoDocUsu() { return tipoDocUsu; }
    public void setTipoDocUsu(String tipoDocUsu) { this.tipoDocUsu = tipoDocUsu; }
}