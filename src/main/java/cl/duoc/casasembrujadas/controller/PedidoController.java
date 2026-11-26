package cl.duoc.casasembrujadas.controller;

import cl.duoc.casasembrujadas.dto.PedidoRequest;
import cl.duoc.casasembrujadas.model.EstadoPedido;
import cl.duoc.casasembrujadas.model.Pedido;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;
import cl.duoc.casasembrujadas.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Compras de los clientes")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // cualquier usuario con sesión puede comprar; el dueño del pedido sale de SU token
    @PostMapping
    @Operation(summary = "Finalizar compra", description = "Requiere sesión. El total lo calcula el servidor")
    public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoRequest datos,
                                        @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(usuario.id(), datos));
    }

    @GetMapping("/mis-pedidos")
    @Operation(summary = "Mis pedidos", description = "Los pedidos del usuario de la sesión")
    public List<Pedido> misPedidos(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return pedidoService.misPedidos(usuario.id());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los pedidos", description = "Solo ADMIN")
    public List<Pedido> listarTodos() {
        return pedidoService.listarTodos();
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar estado del pedido", description = "Solo ADMIN. Ejemplo: /api/pedidos/1/estado?valor=PAGADO")
    public Pedido cambiarEstado(@PathVariable Long id, @RequestParam EstadoPedido valor) {
        return pedidoService.cambiarEstado(id, valor);
    }
}
