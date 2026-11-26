package cl.duoc.casasembrujadas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

// FILTRO que se ejecuta UNA vez en cada petición, antes de llegar al controlador.
// Lee el header "Authorization: Bearer <token>", lo verifica y, si es válido, le dice a Spring Security
// quién es el usuario y qué rol tiene. Si no hay token (o es inválido) la petición sigue como anónima,
// y Spring Security la rechaza con 401 si el endpoint necesitaba sesión
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtService.validar(token);

                // un token de refresco NO sirve para llamar a la API, solo para renovar
                if (JwtService.TIPO_ACCESO.equals(claims.get("tipo"))) {
                    UsuarioAutenticado usuario = jwtService.aUsuario(claims);
                    // Spring Security trabaja con "ROLE_" + rol: ROLE_ADMIN, ROLE_CLIENTE
                    var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol()));
                    var autenticacion = new UsernamePasswordAuthenticationToken(usuario, null, autoridades);
                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                }
            } catch (ExpiredJwtException e) {
                // lo guardamos para que el 401 diga "token expirado" y el frontend sepa que debe renovarlo
                request.setAttribute("errorJwt", "El token expiró");
            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute("errorJwt", "Token inválido");
            }
        }

        chain.doFilter(request, response);
    }
}
