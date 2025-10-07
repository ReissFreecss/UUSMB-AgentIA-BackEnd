package ibt.unam.mx.user.service;

import ibt.unam.mx.email.EmailService;
import ibt.unam.mx.resource.Role;
import ibt.unam.mx.user.model.AppUser;
import ibt.unam.mx.user.model.ChangePasswordDTO;
import ibt.unam.mx.user.model.RecoveryDTO;
import ibt.unam.mx.user.model.UserDTO;
import ibt.unam.mx.user.repository.AppUserRepository;
import ibt.unam.mx.utils.Message;
import ibt.unam.mx.utils.TypesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class UserService {

    private final EmailService emailService;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    //Obtener todos los usuarios
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<AppUser> users = userRepository.findAllOrderedById();
        return new ResponseEntity<>(new Message(users, "Lista de usuarios obtenida con éxito.", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Buscar usuario por ID
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findById(Long id) {
        return userRepository.findById(id)
                .map(user -> new ResponseEntity<>(new Message(user, "Usuario encontrado.", TypesResponse.SUCCESS), HttpStatus.OK))
                .orElse(new ResponseEntity<>(new Message("Usuario no encontrado.", TypesResponse.ERROR), HttpStatus.NOT_FOUND));
    }

    //Crear usuario
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Message> save(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            return new ResponseEntity<>(new Message("El correo ya está en uso.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPassword().length() < 8 || dto.getPassword().length() > 255) {
            return new ResponseEntity<>(new Message("La contraseña debe tener entre 8 y 255 caracteres.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        // El rol al registrarse siempre sera externo (despues el admin lo podra administrar)
        Role rol = Role.EXTERNO;

        AppUser user = new AppUser(
                dto.getFullName(),
                dto.getFirstLastName(),
                dto.getSecondLastName(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getEmail(),
                dto.getPhone(),
                rol
        );
        System.out.println(rol);
        user.setStatus(true);
        userRepository.save(user);
        return new ResponseEntity<>(new Message(user, "Usuario creado con éxito.", TypesResponse.SUCCESS), HttpStatus.CREATED);
    }

    //Actualizar
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Message> update(UserDTO dto) {
        Optional<AppUser> optionalUser = userRepository.findById(dto.getId());
        if (!optionalUser.isPresent()) {
            return  new ResponseEntity<>(new Message("Usuario no encontrado.", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        AppUser user = optionalUser.get();

        //Verificar si el email ya existe en otro usuario
        if(!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            return new ResponseEntity<>(new Message("El correo ya está en uso.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        //Validar longitudes
        if(dto.getFullName().length() >100){
            return new ResponseEntity<>(new Message("El nombre completo no puede exceder los 100 caracteres.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getFirstLastName().length() >100){
            return new ResponseEntity<>(new Message("El apellido no puede exceder los 100 caracteres.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getSecondLastName().length() >100){
            return new ResponseEntity<>(new Message("El apellido no puede exceder los 100 caracteres.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getPhone().length() > 12){
            return new ResponseEntity<>(new Message("El numero de telefono no puede exceder los 12 caracteres.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);

        }
        if (dto.getEmail().length() > 100) {
            return new ResponseEntity<>(new Message("El correo no puede exceder los 100 caracteres.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        //Actualizar los datos
        user.setFullName(dto.getFullName());
        user.setFirstLastName(dto.getFirstLastName());
        user.setSecondLastName(dto.getSecondLastName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        userRepository.save(user);
        return new ResponseEntity<>(new Message(user, "Usuario actualizado con éxito.", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Enviar codigo de recuperacion
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Message> sendCode(String email) {
        Optional<AppUser> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado.", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }

        AppUser user = optionalUser.get();
        String code = String.format("%06d", new Random().nextInt(100000));
        user.setRecoveryCode(code);
        user.setCodeExpiration(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);
        String mensajeHtml = "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\" />\n" +
                "  <title>Recuperación de Contraseña</title>\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f6f6f6;\">\n" +
                "  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "    <tr>\n" +
                "      <td style=\"padding: 20px 0;\">\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"background-color: #ffffff; border-radius: 6px;\">\n" +
                "          <!-- Encabezado -->\n" +
                "          <tr>\n" +
                "            <td align=\"center\" style=\"padding: 20px;\">\n" +
                "              <h1 style=\"color: #2A004E; margin-bottom: 0; font-size: 28px;\">\n" +
                "                Recuperación de Contraseña\n" +
                "              </h1>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- Contenido principal -->\n" +
                "          <tr>\n" +
                "            <td style=\"padding: 20px; color: #333333; font-size: 16px; line-height: 1.5;\">\n" +
                "              <p style=\"margin-top: 0;\">\n" +
                "                ¡Hola <strong>{{nombreUsuario}}</strong>!\n" +
                "              </p>\n" +
                "              <p>\n" +
                "                Recibimos una solicitud para restablecer la contraseña de tu cuenta. Tu código de\n" +
                "                recuperación es:\n" +
                "              </p>\n" +
                "              <div style=\"text-align: center; margin: 30px 0;\">\n" +
                "                <span style=\"font-size: 24px; font-weight: bold; color: #2A004E;\">\n" +
                "                  {{codigo}}\n" +
                "                </span>\n" +
                "              </div>\n" +
                "              <p>\n" +
                "                Este código expirará en <strong>10 minutos</strong>. Si no realizaste esta solicitud, simplemente\n" +
                "                ignora este correo.\n" +
                "              </p>\n" +
                "              <p style=\"margin-bottom: 0;\">\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- Pie de página -->\n" +
                "          <tr>\n" +
                "            <td align=\"center\" style=\"background-color: #f6f6f6; padding: 10px; border-radius: 0 0 6px 6px;\">\n" +
                "              <p style=\"color: #888888; font-size: 14px; margin: 0;\">\n" +
                "                © 2025 UUSMB. Todos los derechos reservados.\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "</body>\n" +
                "</html>\n";

        String finalHtml = mensajeHtml
                .replace("{{nombreUsuario}}", user.getFullName())
                .replace("{{codigo}}", code);

        boolean enviado = emailService.sendEmail(
                user.getEmail(),
                "Recuperación de contraseña",
                finalHtml
        );

        if (enviado) {
            return new ResponseEntity<>(new Message("Código de recuperación enviado con éxito.", TypesResponse.SUCCESS), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Message("Error al enviar el código de recuperación.", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Verificar código de recuperación
    @Transactional(readOnly = true)
    public ResponseEntity<Message> verifyRecoveryCode(RecoveryDTO dto) {
        Optional<AppUser> optionalUser = userRepository.findFirstByRecoveryCode(dto.getRecoveryCode());
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("El código de recuperación es inválido o ha expirado.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        AppUser user = optionalUser.get();
        if (user.getCodeExpiration().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(new Message("El código de recuperación ha expirado.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new Message("Código de recuperación verificado con éxito.", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Cambiar contra despues de verficacion de codigo
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Message> resetPassword(String email, String newPassword) {
        Optional<AppUser> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado.", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }

        AppUser user = optionalUser.get();
        if (user.getRecoveryCode() == null || user.getCodeExpiration().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(new Message("El código de recuperación es inválido o ha expirado.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setRecoveryCode(null);
        user.setCodeExpiration(null);

        userRepository.save(user);
        return new ResponseEntity<>(new Message("Contraseña restablecida con éxito.", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Cambair contrasena desde perfil
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Message> changePasswordUser(ChangePasswordDTO dto) {
        Optional<AppUser> optionalUser = userRepository.findById(dto.getUserId());
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado.", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }

        AppUser user = optionalUser.get();

        // Verificar la contraseña actual
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            return new ResponseEntity<>(new Message("La contraseña actual es incorrecta.", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }

        // Validar la nueva contraseña
        if (dto.getNewPassword().length() < 8 || dto.getNewPassword().length() > 255) {
            return new ResponseEntity<>(new Message("La contraseña debe tener entre 8 y 255 caracteres.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        // Verificar que la nueva contraseña no sea igual a la actual
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            return new ResponseEntity<>(new Message("La nueva contraseña debe ser diferente a la contraseña actual.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        // Actualizar la contraseña
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return new ResponseEntity<>(new Message("Contraseña actualizada con éxito.", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Cambiar el estado del usuario (Activar/Desactivar)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Message> changeStatus(Long id) {
        Optional<AppUser> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado.", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        AppUser user = optionalUser.get();
        user.setStatus(!user.isStatus()); //Alternar estado (activo/inactivo)

        userRepository.save(user);
        String statusMessage = user.isStatus() ? "Usuario activado con exito" : "Usuario desactivado con exito";
        return new ResponseEntity<>(new Message(user, statusMessage, TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Obtener usuarios activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findActiveUsers() {
        return new ResponseEntity<>(
                new Message(userRepository.findByStatus(true), "Lista de usuarios activos obtenida con éxito.", TypesResponse.SUCCESS),
                HttpStatus.OK
        );
    }

    // Obtener usuarios inactivos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findInactiveUsers() {
        return new ResponseEntity<>(
                new Message(userRepository.findByStatus(false), "Lista de usuarios inactivos obtenida con éxito.", TypesResponse.SUCCESS),
                HttpStatus.OK
        );
    }


    // Cambiar rol de INTERNO a EXTERNO o viceversa
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Message> changeRole(Long id) {
        Optional<AppUser> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado.", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }

        AppUser user = optionalUser.get();

        // Verificar el rol actual y cambiarlo
        if (user.getRole() == Role.INTERNO) {
            user.setRole(Role.EXTERNO);
        } else if (user.getRole() == Role.EXTERNO) {
            user.setRole(Role.INTERNO);
        } else {
            return new ResponseEntity<>(new Message("El usuario no tiene un rol que pueda ser alternado.", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }

        userRepository.save(user);
        return new ResponseEntity<>(new Message("Rol cambiado exitosamente.", TypesResponse.SUCCESS), HttpStatus.OK);
    }


}
