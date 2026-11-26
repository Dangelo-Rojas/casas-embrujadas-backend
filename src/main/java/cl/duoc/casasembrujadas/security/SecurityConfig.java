package cl.duoc.casasembrujadas.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

// CONFIGURACIÓN DE SEGURIDAD: qué endpoints son públicos, cuáles necesitan sesión y cómo se responde
// cuando falta permiso. Los endpoints solo para ADMIN se marcan en cada controlador con @PreAuthorize
@Configuration
@EnableMethodSecurity // activa @PreAuthorize("hasRole('ADMIN')") en los controladores
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${cors.origenes-permitidos}")
    private String origenesPermitidos;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF protege formularios con cookies de sesión; con JWT en el header no aplica
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // STATELESS: el servidor no guarda sesiones; cada petición se identifica sola con su token
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // documentación Swagger
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // login, registro y renovación del token
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/registro", "/api/auth/refresh").permitAll()
                // lo que ve cualquier visitante: catálogo, blog, regiones
                .requestMatchers(HttpMethod.GET, "/api/propiedades", "/api/propiedades/destacadas",
                        "/api/propiedades/slug/**", "/api/blogs", "/api/blogs/**", "/api/regiones", "/api/regiones/**").permitAll()
                // el formulario de contacto no exige cuenta
                .requestMatchers(HttpMethod.POST, "/api/contacto").permitAll()
                .requestMatchers("/error").permitAll()
                // Solo ADMIN. Se revisa AQUÍ, en el filtro, antes de llegar al controlador (y además con
                // @PreAuthorize en cada método: doble protección). Así un cliente recibe 403 de inmediato
                .requestMatchers(HttpMethod.GET, "/api/propiedades/*", "/api/pedidos", "/api/contacto").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/propiedades", "/api/blogs").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/propiedades/**", "/api/blogs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/pedidos/**", "/api/contacto/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/propiedades/**", "/api/blogs/**", "/api/contacto/**").hasRole("ADMIN")
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                // TODO lo demás necesita un token válido (cualquier rol): comprar, mis pedidos, /api/auth/me
                .anyRequest().authenticated())
            // respuestas JSON para 401 (sin sesión / token malo) y 403 (sesión, pero sin el rol necesario)
            .exceptionHandling(e -> e
                .authenticationEntryPoint((request, response, ex) -> {
                    Object detalle = request.getAttribute("errorJwt");
                    escribirError(response, 401, "No autenticado",
                            detalle != null ? detalle.toString() : "Debes iniciar sesión para acceder a este recurso");
                })
                .accessDeniedHandler((request, response, ex) ->
                    escribirError(response, 403, "Acceso denegado", "No tienes permisos para realizar esta acción")))
            // nuestro filtro JWT va ANTES del filtro estándar de usuario/contraseña de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCrypt: algoritmo de hash lento y con "sal" aleatoria, pensado para contraseñas.
    // La misma contraseña da un hash distinto cada vez, y no se puede revertir
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Spring Boot crea un usuario de prueba con contraseña aleatoria si no definimos este bean.
    // Como el login lo hacemos nosotros con JWT, lo dejamos vacío para desactivar ese usuario por defecto
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("El login se hace en /api/auth/login");
        };
    }

    // CORS: el navegador bloquea las llamadas de http://localhost:5173 (React) a http://localhost:8080 (API)
    // porque son "orígenes" distintos, a menos que el backend los autorice explícitamente aquí
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(origenesPermitidos.split(",")).map(String::trim).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    private static void escribirError(jakarta.servlet.http.HttpServletResponse response, int estado,
                                      String error, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"estado\":%d,\"error\":\"%s\",\"mensaje\":\"%s\",\"campos\":null,\"fecha\":\"%s\"}",
                estado, error, mensaje, LocalDateTime.now()));
    }
}
