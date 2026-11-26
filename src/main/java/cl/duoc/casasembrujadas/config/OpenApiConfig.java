package cl.duoc.casasembrujadas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Información general que muestra Swagger en http://localhost:8080/swagger-ui.html
// También agrega el botón "Authorize": se pega el token del login y Swagger lo envía
// en el header Authorization: Bearer <token> en cada prueba
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA = "bearerAuth";

    @Bean
    public OpenAPI apiCasasEmbrujadas() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Casas Embrujadas")
                        .version("1.0.0")
                        .description("Backend de la tienda Casas Embrujadas (DSY1104 - Desarrollo FullStack II). "
                                + "Para probar endpoints protegidos: 1) POST /api/auth/login, 2) copia el token, "
                                + "3) botón Authorize. Usuarios de prueba: admin@duoc.cl / admin123 y cliente@gmail.com / cliente123"))
                .components(new Components().addSecuritySchemes(ESQUEMA, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}
