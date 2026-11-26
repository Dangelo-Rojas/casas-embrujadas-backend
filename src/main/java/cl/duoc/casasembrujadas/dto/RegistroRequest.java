package cl.duoc.casasembrujadas.dto;

import jakarta.validation.constraints.*;

// DTO (Data Transfer Object): solo los datos que el frontend manda al registrarse.
// No usamos la entidad Usuario directamente para que nadie pueda mandar "rol": "ADMIN" al registrarse.
// Mismas reglas que validarRegistro() del frontend
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Size(max = 100, message = "El correo admite máximo 100 caracteres")
        @Pattern(regexp = "^[^\\s@]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$",
                 message = "Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 4, max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres")
        String contrasena,

        @NotNull(message = "La comuna es obligatoria")
        Long comunaId) {
}
