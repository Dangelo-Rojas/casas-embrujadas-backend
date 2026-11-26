package cl.duoc.casasembrujadas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada del backend.
// @SpringBootApplication activa la configuración automática de Spring Boot y hace que
// busque nuestras clases (@RestController, @Service, @Repository, @Entity...) en este paquete y sus subpaquetes
@SpringBootApplication
public class CasasEmbrujadasApplication {

	public static void main(String[] args) {
		SpringApplication.run(CasasEmbrujadasApplication.class, args);
	}
}
