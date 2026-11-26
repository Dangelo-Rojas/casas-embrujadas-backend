package cl.duoc.casasembrujadas.controller;

import cl.duoc.casasembrujadas.model.Rol;
import cl.duoc.casasembrujadas.model.Usuario;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;
import cl.duoc.casasembrujadas.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Toda la clase es solo para ADMIN: @PreAuthorize a nivel de clase aplica a todos sus métodos
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Usuarios", description = "Administración de usuarios. Solo ADMIN")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios")
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por id")
    public Usuario buscar(@PathVariable Long id) {
        return usuarioService.buscar(id);
    }

    @PatchMapping("/{id}/rol")
    @Operation(summary = "Cambiar rol", description = "Ejemplo: /api/usuarios/3/rol?valor=ADMIN")
    public Usuario cambiarRol(@PathVariable Long id, @RequestParam Rol valor,
                              @AuthenticationPrincipal UsuarioAutenticado admin) {
        return usuarioService.cambiarRol(id, valor, admin.id());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "No se puede eliminar la propia cuenta ni un usuario con pedidos (409)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado admin) {
        usuarioService.eliminar(id, admin.id());
        return ResponseEntity.noContent().build();
    }
}
