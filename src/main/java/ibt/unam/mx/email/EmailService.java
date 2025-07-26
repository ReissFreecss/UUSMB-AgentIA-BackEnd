package ibt.unam.mx.email;

public interface EmailService {
    /**
     * Envía un correo electrónico.
     * @param to Dirección de destino.
     * @param subject Asunto del correo.
     * @param message Cuerpo o mensaje del correo.
     * @return true si se envió correctamente, false si hubo un error.
     */
    boolean sendEmail(String to, String subject, String message);
}