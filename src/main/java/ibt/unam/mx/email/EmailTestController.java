package ibt.unam.mx.email;

import org.springframework.web.bind.annotation.*;


//Este endpoint solo es para probar el envio de correos
@RestController
@RequestMapping("/api/email")
public class EmailTestController {
    private final SendGridEmailService emailService;

    public EmailTestController(SendGridEmailService emailService) {
        this.emailService = emailService;
    }

    // DTO para recibir el contenido del POST
    public static class EmailRequest {
        public String to;
        public String subject;
        public String body;
    }

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequest request) {
        boolean result = emailService.sendEmail(
                request.to,
                request.subject != null ? request.subject : "Correo de prueba",
                request.body != null ? request.body : "<p>Este es un mensaje de prueba enviado desde Spring Boot.</p>"
        );
        return result ? "Correo enviado exitosamente" : "Fallo al enviar correo";
    }

}
