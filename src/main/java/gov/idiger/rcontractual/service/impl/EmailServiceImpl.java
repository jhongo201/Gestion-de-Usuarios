package gov.idiger.rcontractual.service.impl;

import gov.idiger.rcontractual.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Implementacion de envio de correos del sistema.
 *
 * El envio se controla con app.mail.enabled para permitir trabajar sin correo
 * cuando no exista configuracion SMTP disponible.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:}")
    private String mailFrom;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarNotificacionUsuarioCreadoPorAdministrador(
            String destinatario,
            String nombreCompleto,
            String username) {

        enviarCorreoHtml(
                destinatario,
                "Usuario registrado - Gestor de Reportes Contractuales",
                "Usuario registrado",
                nombreSeguro(nombreCompleto),
                "Su usuario ha sido registrado por el administrador del aplicativo Gestor de Reportes Contractuales.",
                "Usuario: " + username,
                "Por seguridad, en el primer ingreso el sistema le solicitará cambiar la contraseña.",
                "registro de usuario por administrador");
    }

    @Override
    public void enviarNotificacionSolicitudRegistrada(
            String destinatario,
            String nombreCompleto,
            String username) {

        enviarCorreoHtml(
                destinatario,
                "Solicitud de usuario registrada - Gestor de Reportes Contractuales",
                "Solicitud registrada",
                nombreSeguro(nombreCompleto),
                "Su solicitud de usuario fue registrada correctamente en el Gestor de Reportes Contractuales.",
                "Usuario solicitado: " + username,
                "La cuenta quedó pendiente de activación por parte del Administrador del sistema. Cuando sea activada, recibirá una notificación por correo.",
                "solicitud de usuario registrada");
    }

    @Override
    public void enviarNotificacionUsuarioActivado(
            String destinatario,
            String nombreCompleto,
            String username) {

        enviarCorreoHtml(
                destinatario,
                "Usuario activado - Gestor de Reportes Contractuales",
                "Usuario activado",
                nombreSeguro(nombreCompleto),
                "Su usuario en el Gestor de Reportes Contractuales ha sido activado.",
                "Usuario: " + username,
                "Ya puede ingresar al sistema. Si el sistema lo solicita, deberá cambiar su contraseña en el primer ingreso.",
                "activacion de usuario");
    }

    @Override
    public void enviarNotificacionUsuarioInactivado(
            String destinatario,
            String nombreCompleto,
            String username) {

        enviarCorreoHtml(
                destinatario,
                "Usuario inactivado - Gestor de Reportes Contractuales",
                "Usuario inactivado",
                nombreSeguro(nombreCompleto),
                "Su usuario en el Gestor de Reportes Contractuales ha sido inactivado por el administrador del sistema.",
                "Usuario: " + username,
                "Para solicitar la activación nuevamente, por favor contacte al Administrador del sistema.",
                "inactivacion de usuario");
    }

    @Override
    public void enviarCodigoRecuperacionClave(
            String destinatario,
            String nombreCompleto,
            String username,
            String codigo) {

        enviarCorreoHtml(
                destinatario,
                "Recuperación de contraseña - Gestor de Reportes Contractuales",
                "Código de recuperación",
                nombreSeguro(nombreCompleto),
                "Se ha solicitado la recuperación de contraseña para su usuario en el Gestor de Reportes Contractuales.",
                "Usuario: " + username,
                "Código de recuperación: " + codigo + ". Este código tiene una vigencia de 10 minutos.",
                "recuperacion de contraseña");
    }

    @Override
    public void enviarNotificacionCambioClave(
            String destinatario,
            String nombreCompleto,
            String username) {

        enviarCorreoHtml(
                destinatario,
                "Contraseña actualizada - Gestor de Reportes Contractuales",
                "Contraseña actualizada",
                nombreSeguro(nombreCompleto),
                "La contraseña de su usuario fue actualizada correctamente.",
                "Usuario: " + username,
                "Si usted no realizó este cambio, por favor contacte al Administrador del sistema.",
                "cambio de contraseña");
    }

    private void enviarCorreoHtml(
            String destinatario,
            String asunto,
            String titulo,
            String nombre,
            String mensajePrincipal,
            String detalle,
            String nota,
            String contexto) {

        if (!mailEnabled) {
            log.info("Envio de correo desactivado. No se envia correo de {} a {}",
                    contexto, destinatario);
            return;
        }

        if (destinatario == null || destinatario.trim().isEmpty()) {
            log.info("No se envia correo de {} porque el destinatario esta vacio",
                    contexto);
            return;
        }

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(construirHtml(titulo, nombre, mensajePrincipal, detalle, nota), true);
            helper.addInline("logoIdiger", new ClassPathResource("static/images/logo-idiger.png"));

            mailSender.send(mensaje);

            log.info("Correo de {} enviado a {}", contexto, destinatario);
        } catch (MessagingException ex) {
            log.warn("No fue posible construir correo de {} a {}: {}",
                    contexto, destinatario, ex.getMessage());
        } catch (MailException ex) {
            log.warn("No fue posible enviar correo de {} a {}: {}",
                    contexto, destinatario, ex.getMessage());
        }
    }

    private String construirHtml(
            String titulo,
            String nombre,
            String mensajePrincipal,
            String detalle,
            String nota) {

        return "<!DOCTYPE html>"
                + "<html lang='es'>"
                + "<head><meta charset='UTF-8'></head>"
                + "<body style='margin:0;padding:0;background:#f5f7f4;font-family:Segoe UI,Arial,sans-serif;color:#1f2a1f;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f5f7f4;padding:24px 0;'>"
                + "<tr><td align='center'>"
                + "<table width='620' cellpadding='0' cellspacing='0' style='max-width:620px;background:#ffffff;border:1px solid #d9e2d4;border-radius:6px;overflow:hidden;'>"
                + "<tr>"
                + "<td style='padding:24px 28px;text-align:center;border-bottom:1px solid #edf0ea;'>"
                + "<img src='cid:logoIdiger' alt='IDIGER - Bogotá' style='max-width:280px;height:auto;' />"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='padding:28px;'>"
                + "<h1 style='margin:0 0 18px 0;font-size:22px;line-height:1.25;color:#1f2a1f;text-align:center;'>"
                + escapar(titulo)
                + "</h1>"
                + "<p style='margin:0 0 14px 0;font-size:15px;line-height:1.55;'>Hola "
                + escapar(nombre)
                + ",</p>"
                + "<p style='margin:0 0 14px 0;font-size:15px;line-height:1.55;'>"
                + escapar(mensajePrincipal)
                + "</p>"
                + "<div style='margin:18px 0;padding:14px 16px;background:#f2f7ee;border-left:4px solid #3d682c;font-size:15px;line-height:1.5;font-weight:600;'>"
                + escapar(detalle)
                + "</div>"
                + "<p style='margin:0 0 22px 0;font-size:15px;line-height:1.55;color:#3f4c3f;'>"
                + escapar(nota)
                + "</p>"
                + "<p style='margin:0 0 10px 0;font-size:15px;line-height:1.5;'>Cordialmente,</p>"
                + "<p style='margin:0;font-size:15px;line-height:1.5;font-weight:700;color:#3d682c;'>"
                + "Gestor de Reportes Contractuales"
                + "</p>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='padding:16px 28px;background:#f8faf7;border-top:1px solid #edf0ea;text-align:center;font-size:12px;color:#6f7a6f;'>"
                + "Instituto Distrital de Gestión de Riesgos y Cambio Climático - IDIGER<br/>"
                + "Mensaje generado automáticamente. Por favor no responda este correo."
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</td></tr>"
                + "</table>"
                + "</body>"
                + "</html>";
    }

    private String nombreSeguro(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            return "usuario";
        }

        return nombreCompleto.trim();
    }

    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
