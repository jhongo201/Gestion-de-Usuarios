package gov.idiger.rcontractual.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Entidad para codigos OTP de recuperacion de contraseña.
 *
 * Cada registro representa un codigo temporal asociado a un usuario.
 */
@Entity
@Table(name = "USUARIO_OTP")
public class UsuarioOtp {

    /** Identificador del OTP. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario_otp")
    @SequenceGenerator(
            name = "seq_usuario_otp",
            sequenceName = "SEQ_USUARIO_OTP",
            allocationSize = 1)
    @Column(name = "ID_OTP")
    private Long idOtp;

    /** Usuario asociado al codigo. */
    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    /** Codigo temporal enviado al correo. */
    @Column(name = "CODIGO_OTP", nullable = false, length = 10)
    private String codigo;

    /** Fecha de generacion del codigo. */
    @Column(name = "FECHA_CREACION", nullable = false)
    private OffsetDateTime fechaCreacion;

    /** Fecha de expiracion del codigo. */
    @Column(name = "FECHA_EXPIRACION", nullable = false)
    private OffsetDateTime fechaExpiracion;

    /** Indica si el codigo ya fue usado. 0 = No usado, 1 = Usado. */
    @Column(name = "USADO", nullable = false)
    private Integer usado;

    /** Numero de intentos de uso del codigo. */
    @Column(name = "INTENTOS", nullable = false)
    private Integer intentos;

    public Long getIdOtp() {
        return idOtp;
    }

    public void setIdOtp(Long idOtp) {
        this.idOtp = idOtp;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public OffsetDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(OffsetDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Integer getUsado() {
        return usado;
    }

    public void setUsado(Integer usado) {
        this.usado = usado;
    }

    public Integer getIntentos() {
        return intentos;
    }

    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }

    /**
     * Valida si el codigo sigue vigente.
     */
    public boolean estaVigente() {
        return Integer.valueOf(0).equals(usado)
                && fechaExpiracion != null
                && fechaExpiracion.isAfter(OffsetDateTime.now());
    }
}
