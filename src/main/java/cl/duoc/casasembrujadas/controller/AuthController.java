package cl.duoc.casasembrujadas.controller;

import cl.duoc.casasembrujadas.dto.AuthResponse;
import cl.duoc.casasembrujadas.dto.LoginRequest;
import cl.duoc.casasembrujadas.dto.RefreshRequest;
import cl.duoc.casasembrujadas.dto.RegistroRequest;
import cl.duoc.casasembrujadas.model.Usuario;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;
import cl.duoc.casasembrujadas.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y renovación de tokens JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    @Operation(summary = "Crear cuenta", description = "Crea un usuario CLIENTE y devuelve sus tokens (queda con la sesión iniciada)")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(datos));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve el token de acceso (30 min) y el refresh token (7 días)")
    public AuthResponse login(@Valid @RequestBody LoginRequest datos) {
        return authService.login(datos);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Con un refresh token válido entrega tokens nuevos sin volver a pedir la contraseña")
    public AuthResponse refrescar(@Valid @RequestBody RefreshRequest datos) {
        return authService.refrescar(datos.refreshToken());
    }

    // @AuthenticationPrincipal entrega el usuario que armó el filtro JWT a partir del token
    @GetMapping("/me")
    @Operation(summary = "Usuario de la sesión actual", description = "Requiere token")
    public Usuario yo(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return authService.usuarioActual(usuario.id());
    }
}
