package cl.duoc.casasembrujadas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Una línea del carrito: el slug de la propiedad (el mismo id que usa el carrito del frontend) y la cantidad
public record ItemPedidoRequest(
        @NotBlank(message = "Falta la propiedad") String slug,
        @Min(value = 1, message = "La cantidad mínima es 1")
        @Max(value = 3, message = "La cantidad máxima es 3") int cantidad) {
}
