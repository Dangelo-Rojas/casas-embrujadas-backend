package cl.duoc.casasembrujadas.security;

import cl.duoc.casasembrujadas.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

// GENERACIÓN y VERIFICACIÓN de tokens JWT.
// Un JWT tiene 3 partes separadas por puntos: header.payload.firma
//  - payload ("claims"): datos del usuario (correo, id, rol) y la fecha de expiración
//  - firma: HMAC-SHA256 del header + payload con nuestra clave secreta. Si alguien modifica el
//    payload (por ejemplo para cambiarse el rol a ADMIN), la firma ya no calza y el token se rechaza
@Service
public class JwtService {

    // distinguimos los dos tipos de token para que uno no se pueda usar en lugar del otro
    public static final String TIPO_ACCESO = "access";
    public static final String TIPO_REFRESCO = "refresh";

    private final SecretKey clave;
    private final long duracionAccesoMs;
    private final long duracionRefrescoMs;

    // @Value lee los valores de application.yml
    public JwtService(@Value("${jwt.secret}") String secretoBase64,
                      @Value("${jwt.access-expiration-ms}") long duracionAccesoMs,
                      @Value("${jwt.refresh-expiration-ms}") long duracionRefrescoMs) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
        this.duracionAccesoMs = duracionAccesoMs;
        this.duracionRefrescoMs = duracionRefrescoMs;
    }

    // token corto (30 min) que se manda en cada petición: Authorization: Bearer <token>
    public String generarTokenAcceso(Usuario usuario) {
        return generar(usuario, TIPO_ACCESO, duracionAccesoMs);
    }

    // token largo (7 días) que SOLO sirve para pedir un token de acceso nuevo en /api/auth/refresh
    public String generarTokenRefresco(Usuario usuario) {
        return generar(usuario, TIPO_REFRESCO, duracionRefrescoMs);
    }

    private String generar(Usuario usuario, String tipo, long duracionMs) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario.getCorreo())             // a quién pertenece
                .claim("id", usuario.getId())
                .claim("nombre", usuario.getNombre())
                .claim("rol", usuario.getRol().name())    // CLIENTE o ADMIN
                .claim("tipo", tipo)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + duracionMs))
                .signWith(clave)                          // firma HMAC-SHA256
                .compact();
    }

    // Verifica firma y expiración. Si el token fue alterado o venció, lanza una excepción (JwtException)
    public Claims validar(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // arma el UsuarioAutenticado a partir de los datos del token (sin ir a la base de datos)
    public UsuarioAutenticado aUsuario(Claims claims) {
        return new UsuarioAutenticado(
                ((Number) claims.get("id")).longValue(),
                claims.getSubject(),
                claims.get("nombre", String.class),
                claims.get("rol", String.class));
    }

    public long getDuracionAccesoMs() {
        return duracionAccesoMs;
    }
}
