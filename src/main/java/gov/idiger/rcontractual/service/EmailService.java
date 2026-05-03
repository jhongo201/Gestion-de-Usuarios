package gov.idiger.rcontractual.service;

/**
 * Servicio para envio de correos del sistema.
 *
 * La implementacion debe respetar la propiedad app.mail.enabled para permitir
 * activar o desactivar el envio sin afectar los flujos principales.
 */
public interface EmailService {

    /**
     * Notifica a un usuario creado desde el panel administrativo.
     *
     * @param destinatario correo del usuario creado
     * @param nombreCompleto nombre completo del usuario
     * @param username nombre de usuario para iniciar sesion
     */
    void enviarNotificacionUsuarioCreadoPorAdministrador(
            String destinatario,
            String nombreCompleto,
            String username);

    /**
     * Notifica al solicitante que su registro fue recibido y quedo pendiente
     * de activacion por parte del administrador.
     *
     * @param destinatario correo del solicitante
     * @param nombreCompleto nombre completo del solicitante
     * @param username nombre de usuario solicitado
     */
    void enviarNotificacionSolicitudRegistrada(
            String destinatario,
            String nombreCompleto,
            String username);

    /**
     * Notifica a un usuario cuando su cuenta fue activada por un administrador.
     *
     * @param destinatario correo del usuario activado
     * @param nombreCompleto nombre completo del usuario
     * @param username nombre de usuario para iniciar sesion
     */
    void enviarNotificacionUsuarioActivado(
            String destinatario,
            String nombreCompleto,
            String username);

    /**
     * Notifica a un usuario cuando su cuenta fue inactivada por un administrador.
     *
     * @param destinatario correo del usuario inactivado
     * @param nombreCompleto nombre completo del usuario
     * @param username nombre de usuario inactivado
     */
    void enviarNotificacionUsuarioInactivado(
            String destinatario,
            String nombreCompleto,
            String username);

    /**
     * Envia un codigo o instruccion para recuperacion de contraseña.
     *
     * @param destinatario correo del usuario
     * @param nombreCompleto nombre completo del usuario
     * @param username nombre de usuario
     * @param codigo codigo temporal de recuperacion
     */
    void enviarCodigoRecuperacionClave(
            String destinatario,
            String nombreCompleto,
            String username,
            String codigo);

    /**
     * Notifica que la contraseña fue cambiada correctamente.
     *
     * Aplica para cambio obligatorio, recuperacion o cambio manual.
     *
     * @param destinatario correo del usuario
     * @param nombreCompleto nombre completo del usuario
     * @param username nombre de usuario
     */
    void enviarNotificacionCambioClave(
            String destinatario,
            String nombreCompleto,
            String username);
}
