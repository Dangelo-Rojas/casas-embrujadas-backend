package cl.duoc.casasembrujadas.service;

import cl.duoc.casasembrujadas.dto.AuthResponse;
import cl.duoc.casasembrujadas.dto.LoginRequest;
import cl.duoc.casasembrujadas.dto.RegistroRequest;
import cl.duoc.casasembrujadas.exception.CredencialesInvalidasException;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.model.Comuna;
import cl.duoc.casasembrujadas.model.Rol;
import cl.duoc.casasembrujadas.model.Usuario;
import cl.duoc.casasembrujadas.repository.ComunaRepository;
import cl.duoc.casasembrujadas.repository.UsuarioRepository;
import cl.duoc.casasembrujadas.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Registro, inicio de sesión y renovación de tokens
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ComunaRepository comunaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, ComunaRepository comunaRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.comunaRepository = comunaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registrar(RegistroRequest datos) {
        String correo = datos.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo");
        }
        Comuna comuna = comunaRepository.findById(datos.comunaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la comuna seleccionada"));

        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(passwordEncoder.encode(datos.contrasena())); // se guarda el hash, no la contraseña
        usuario.setComuna(comuna);
        usuario.setRol(Rol.CLIENTE); // quien se registra desde la página SIEMPRE es cliente

        return respuestaCon(usuarioRepository.save(usuario));
    }

    public AuthResponse login(LoginRequest datos) {
        Usuario usuario = usuarioRepository.findByCorreo(datos.correo().trim().toLowerCase())
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos"));

        // matches() aplica BCrypt a la contraseña recibida y la compara con el hash guardado
        if (!passwordEncoder.matches(datos.contrasena(), usuario.getContrasenaHash())) {
            // mismo mensaje en los dos casos: así no se revela si el correo existe o no
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }
        return respuestaCon(usuario);
    }

    // RENOVACIÓN: con un refresh token válido se entrega un par de tokens nuevo.
    // Se vuelve a leer el usuario de la BD: si lo borraron o le cambiaron el rol, el token nuevo lo refleja
    public AuthResponse refrescar(String refreshToken) {
        Claims claims;
        try {
            claims = jwtService.validar(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CredencialesInvalidasException("El refresh token es inválido o expiró. Inicia sesión de nuevo");
        }
        if (!JwtService.TIPO_REFRESCO.equals(claims.get("tipo"))) {
            throw new CredencialesInvalidasException("El token enviado no es un refresh token");
        }
        Usuario usuario = usuarioRepository.findByCorreo(claims.getSubject())
                .orElseThrow(() -> new CredencialesInvalidasException("La cuenta ya no existe"));
        return respuestaCon(usuario);
    }

    public Usuario usuarioActual(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La cuenta ya no existe"));
    }

    private AuthResponse respuestaCon(Usuario usuario) {
        return new AuthResponse(
                jwtService.generarTokenAcceso(usuario),
                jwtService.generarTokenRefresco(usuario),
                "Bearer",
                jwtService.getDuracionAccesoMs() / 1000,
                usuario);
    }
}
