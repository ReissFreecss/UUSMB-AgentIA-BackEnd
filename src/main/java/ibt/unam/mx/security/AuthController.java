package ibt.unam.mx.security;

import ibt.unam.mx.user.model.AppUser;
import ibt.unam.mx.user.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AppUserRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Optional<AppUser> usuarioOptional = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (usuarioOptional.isPresent()) {
            AppUser usuario = usuarioOptional.get();

            // Verifica la contraseña
            if (passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                // Generar el token incluyendo el id y el status
                String token = jwtUtil.generateToken(
                        usuario.getEmail(),
                        usuario.getRole().name(),
                        usuario.getId(),  // El ID del usuario
                        usuario.isStatus()  // El estado del usuario
                );
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(401).body("INVALID CREDENTIALS: INCORRECT PASSWORD");
            }
        } else {
            return ResponseEntity.status(401).body("INVALID CREDENTIALS: INCORRECT PASSWORD");
        }
    }
}
