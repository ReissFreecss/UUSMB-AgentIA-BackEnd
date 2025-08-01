package ibt.unam.mx.email;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


//Este endpoint solo es para probar el envio de correos
@RestController
@RequestMapping("/api/email")
public class EmailController {
    private final SendGridEmailService emailService;

    public EmailController(SendGridEmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendEmail(@Validated @RequestBody EmailDTO req) {
        String to = req.getTo() != null ? req.getTo() : "correo@default.com";
        String subject = req.getSubject() != null ? req.getSubject() : "Correo de prueba";
        String body = req.getBody() != null ? req.getBody() : "<p>Este es un mensaje de prueba enviado desde Spring Boot.</p>";

        boolean result = emailService.sendEmail(to, subject, body);

        return result ? "Correo enviado exitosamente" : "Fallo al enviar correo";
    }
    }
