package cl.duoc.casasembrujadas.controller;

import cl.duoc.casasembrujadas.model.Propiedad;
import cl.duoc.casasembrujadas.service.PropiedadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// CONTROLADOR REST: recibe las peticiones HTTP y responde JSON.
// @RestController = @Controller + @ResponseBody (lo que retorna cada método va directo al cuerpo como JSON)
// @RequestMapping define la ruta base de todos los endpoints de esta clase
@RestController
@RequestMapping("/api/propiedades")
@Tag(name = "Propiedades", description = "Catálogo de propiedades embrujadas (CRUD)")
public class PropiedadController {

    private final PropiedadService propiedadService;

    public PropiedadController(PropiedadService propiedadService) {
        this.propiedadService = propiedadService;
    }

    // READ: GET /api/propiedades
    @GetMapping
    @Operation(summary = "Listar propiedades activas", description = "Catálogo público, ordenado por precio")
    public List<Propiedad> listarActivas() {
        return propiedadService.listarActivas();
    }

    @GetMapping("/todas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las propiedades", description = "Incluye las desactivadas. Solo ADMIN")
    public List<Propiedad> listarTodas() {
        return propiedadService.listarTodas();
    }

    @GetMapping("/destacadas")
    @Operation(summary = "Listar propiedades destacadas", description = "Las que aparecen en el Home")
    public List<Propiedad> listarDestacadas() {
        return propiedadService.listarDestacadas();
    }

    // @PathVariable toma el valor que viene en la URL: /api/propiedades/slug/faro-ahogado
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Buscar propiedad por slug", description = "La usa la página de detalle del frontend")
    public Propiedad buscarPorSlug(@PathVariable String slug) {
        return propiedadService.buscarActivaPorSlug(slug);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar propiedad por id")
    public Propiedad buscarPorId(@PathVariable Long id) {
        return propiedadService.buscarPorId(id);
    }

    // CREATE: POST /api/propiedades
    // @RequestBody convierte el JSON recibido en un objeto Propiedad; @Valid aplica las validaciones de la entidad
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear propiedad", description = "Solo ADMIN. Responde 201 Created")
    public ResponseEntity<Propiedad> crear(@Valid @RequestBody Propiedad propiedad) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propiedadService.crear(propiedad));
    }

    // UPDATE: PUT /api/propiedades/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar propiedad", description = "Solo ADMIN. Reemplaza todos los datos de la propiedad")
    public Propiedad actualizar(@PathVariable Long id, @Valid @RequestBody Propiedad propiedad) {
        return propiedadService.actualizar(id, propiedad);
    }

    // DELETE: DELETE /api/propiedades/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar propiedad", description = "Solo ADMIN. Responde 204; 409 si ya está en pedidos (en ese caso desactívala)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        propiedadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
