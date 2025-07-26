package ibt.unam.mx.email;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import ibt.unam.mx.config.SendGridConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SendGridEmailService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendGridEmailService.class);

    private final SendGridConfig sendGridConfig;
    private final SendGrid sendGridClient;

    public SendGridEmailService(SendGridConfig sendGridConfig) {
        this.sendGridConfig = sendGridConfig;
        this.sendGridClient = new SendGrid(sendGridConfig.getApiKey());
    }

    @Override
    public boolean sendEmail(String to, String subject, String message) {
        try {
            Email from = new Email(sendGridConfig.getSenderEmail());
            Email recipient = new Email(to);
            Content content = new Content("text/html", message); // Cambia a "text/html" si envías HTML
            Mail mail = new Mail(from, subject, recipient, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGridClient.api(request);
            if (response.getStatusCode() == 202) {
                LOGGER.info("Correo enviado con éxito a {}", to);
                return true;
            } else {
                LOGGER.error("Error al enviar correo. Código: {}. Respuesta: {}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (Exception ex) {
            LOGGER.error("Excepción al enviar correo: ", ex);
        }
        return false;
    }
}
