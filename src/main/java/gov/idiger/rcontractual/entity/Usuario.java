package gov.idiger.rcontractual.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad JPA mapeada a CONTRATOS.USUARIO.
 *
 * Estados de usuario:
 * 0 = Inactivo
 * 1 = Activo
 * 2 = Pendiente de aprobacion
 *
 * La clave se almacena hasheada con BCrypt.
 */
@Entity
@Table(name = "USUARIO", schema = "CONTRATOS")
public class Usuario {

    /** Usuario inactivo: no puede ingresar al sistema. */
    public static final int ESTADO_INACTIVO = 0;

    /** Usuario activo: puede ingresar si no tiene otras restricciones. */
    public static final int ESTADO_ACTIVO = 1;

    /** Usuario solicitado desde formulario publico, pendiente de aprobacion. */
    public static final int ESTADO_PENDIENTE = 2;

    /** Identificador unico del usuario. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario")
    @SequenceGenerator(
        name = "seq_usuario",
        sequenceName = "CONTRATOS.SEQ_USUARIO",
        allocationSize = 1
    )
    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    /** Login unico del usuario. */
    @Column(name = "USUARIO", nullable = false, length = 100, unique = true)
    private String username;

    /** Contrasena hasheada con BCrypt. */
    @Column(name = "CLAVE", nullable = false, length = 200)
    private String clave;

    /**
     * Estado del usuario.
     * 0 = Inactivo, 1 = Activo, 2 = Pendiente aprobacion.
     */
    @Column(name = "ESTADO_USUARIO", nullable = false)
    private Integer estadoUsuario;

    /** Rol asignado al usuario. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ROL", nullable = false)
    private Rol rol;

    /** Entidad a la que pertenece el usuario. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ENTIDAD", nullable = false)
    private Entidad entidad;

    /** Nombre del usuario. */
    @Column(name = "NOMBRE_USUARIO", length = 100)
    private String nombreUsuario;

    /** Apellido del usuario. */
    @Column(name = "APELLIDO_USUARIO", length = 100)
    private String apellidoUsuario;

    /** Numero de documento de identidad. */
    @Column(name = "NUM_DOC_USU", length = 20)
    private String numDocUsu;

    /** Correo electronico del usuario. */
    @Column(name = "CORREO_USUARIO", length = 200)
    private String correoUsuario;

    /** Fecha y hora de creacion del registro. */
    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    /** ID del usuario administrador que creo el registro, si aplica. */
    @Column(name = "ID_USU_CREADOR")
    private Long idUsuCreador;

    /**
     * Tipo de documento.
     * Valores permitidos por BD: CC, CE, PA, TI, PT.
     */
    @Column(name = "TIPO_DOC_USU", length = 10)
    private String tipoDocUsu;

    /**
     * Indica si el usuario debe cambiar clave al ingresar.
     * 0 = No requerido, 1 = Requerido.
     */
    @Column(name = "CAMBIO_CLAVE_REQUERIDO", nullable = false)
    private Integer cambioClaveRequerido;

    /** Fecha del ultimo cambio de clave. */
    @Column(name = "FECHA_ULTIMO_CAMBIO_CLAVE")
    private OffsetDateTime fechaUltimoCambioClave;

    /** Fecha en la que un administrador activo la cuenta. */
    @Column(name = "FECHA_ACTIVACION")
    private OffsetDateTime fechaActivacion;

    /** ID del administrador que activo la cuenta. */
    @Column(name = "ID_USU_ACTIVADOR")
    private Long idUsuActivador;

    /** Valores por defecto antes de insertar. */
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = OffsetDateTime.now();
        }
        if (this.estadoUsuario == null) {
            this.estadoUsuario = ESTADO_ACTIVO;
        }
        if (this.cambioClaveRequerido == null) {
            this.cambioClaveRequerido = 0;
        }
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Integer getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(Integer estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Entidad getEntidad() {
        return entidad;
    }

    public void setEntidad(Entidad entidad) {
        this.entidad = entidad;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    public String getNumDocUsu() {
        return numDocUsu;
    }

    public void setNumDocUsu(String numDocUsu) {
        this.numDocUsu = numDocUsu;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getIdUsuCreador() {
        return idUsuCreador;
    }

    public void setIdUsuCreador(Long idUsuCreador) {
        this.idUsuCreador = idUsuCreador;
    }

    public String getTipoDocUsu() {
        return tipoDocUsu;
    }

    public void setTipoDocUsu(String tipoDocUsu) {
        this.tipoDocUsu = tipoDocUsu;
    }

    public Integer getCambioClaveRequerido() {
        return cambioClaveRequerido;
    }

    public void setCambioClaveRequerido(Integer cambioClaveRequerido) {
        this.cambioClaveRequerido = cambioClaveRequerido;
    }

    public OffsetDateTime getFechaUltimoCambioClave() {
        return fechaUltimoCambioClave;
    }

    public void setFechaUltimoCambioClave(OffsetDateTime fechaUltimoCambioClave) {
        this.fechaUltimoCambioClave = fechaUltimoCambioClave;
    }

    public OffsetDateTime getFechaActivacion() {
        return fechaActivacion;
    }

    public void setFechaActivacion(OffsetDateTime fechaActivacion) {
        this.fechaActivacion = fechaActivacion;
    }

    public Long getIdUsuActivador() {
        return idUsuActivador;
    }

    public void setIdUsuActivador(Long idUsuActivador) {
        this.idUsuActivador = idUsuActivador;
    }

    /** Indica si el usuario esta activo. */
    public boolean estaActivo() {
        return Integer.valueOf(ESTADO_ACTIVO).equals(this.estadoUsuario);
    }

    /** Indica si el usuario esta pendiente de aprobacion. */
    public boolean estaPendiente() {
        return Integer.valueOf(ESTADO_PENDIENTE).equals(this.estadoUsuario);
    }

    /** Indica si el usuario esta inactivo. */
    public boolean estaInactivo() {
        return Integer.valueOf(ESTADO_INACTIVO).equals(this.estadoUsuario);
    }

    /** Indica si debe cambiar clave antes de usar el sistema. */
    public boolean debeCambiarClave() {
        return Integer.valueOf(1).equals(this.cambioClaveRequerido);
    }
}
