package ibt.unam.mx.security;

// Importaciones necesarias para trabajar con JWT y otras utilidades

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {

    // Inyecta el valor de la duración de expiración del token desde las propiedades de la aplicación
    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    // Clave secreta para firmar los tokens JWT
    private final SecretKey secretKey;

    // Constructor que inicializa la clave secreta
    public JWTUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Método para generar un token JWT
    public String generateToken(String correoElectronico, String rol, Long id, boolean status) {
        return Jwts.builder().setSubject(correoElectronico)  // Establece el correo electrónico como el sujeto del token
                .claim("role", rol)  // Añade el rol del usuario como un claim
                .claim("id", id)  // Añade el ID del usuario como un claim
                .claim("status", status)  // Añade el estado del usuario como un claim
                .setIssuedAt(new Date())  // Establece la fecha de emisión del token
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))  // Establece la fecha de expiración
                .signWith(secretKey)  // Firma el token con la clave secreta
                .compact();  // Construye el token como una cadena compacta
    }

    // Método para validar un token JWT
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Método para verificar si un token ha expirado
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Método para extraer la fecha de expiración de un token
    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // Método para extraer todos los claims de un token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    // Método para extraer el nombre de usuario (correo electrónico) de un token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Método para extraer el rol de un token
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        String role = claims.get("role", String.class);
        System.out.println(role);
        System.out.println("Rol extraído del token: " + role); // Línea de depuración
        return role;
    }

    // Método para extraer el ID de un token
    public Long extractId(String token) {
        return extractAllClaims(token).get("id", Long.class);
    }

    // Método para extraer el estado de un token
    public boolean extractStatus(String token) {
        return extractAllClaims(token).get("status", Boolean.class);
    }
}
