package cl.duoc.casasembrujadas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

// Lo que manda el frontend al finalizar la compra. Fíjate que NO trae precios ni total:
// eso lo calcula el backend con los datos de la BD, así nadie puede comprar "un castillo a $1"
public record PedidoRequest(
        @NotEmpty(message = "El carrito está vacío")
        List<@Valid ItemPedidoRequest> items) {
}
