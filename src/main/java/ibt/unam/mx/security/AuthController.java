package ibt.unam.mx.security;

import ibt.unam.mx.user.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // Inyectamos el AuthenticationManager de Spring Security
    private final AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Delegamos la autenticación al AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            // Si la autenticación es exitosa, obtenemos los detalles del usuario
            AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
            AppUser user = userDetails.getAppUser(); // Obtenemos el objeto AppUser
            // Generamos el token con los datos del usuario autenticado
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId(), user.isStatus());
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            // Si las credenciales son incorrectas, el AuthenticationManager lanza esta excepción
            return ResponseEntity.status(401).body("INVALID CREDENTIALS: INCORRECT PASSWORD");
        }
    }
}
