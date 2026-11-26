package cl.duoc.casasembrujadas.controller;

import cl.duoc.casasembrujadas.model.MensajeContacto;
import cl.duoc.casasembrujadas.service.ContactoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contacto")
@Tag(name = "Contacto", description = "Mensajes del formulario de contacto")
public class ContactoController {

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @PostMapping
    @Operation(summary = "Enviar mensaje", description = "Público, no requiere iniciar sesión")
    public ResponseEntity<MensajeContacto> enviar(@Valid @RequestBody MensajeContacto mensaje) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactoService.enviar(mensaje));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar mensajes", description = "Solo ADMIN")
    public List<MensajeContacto> listar() {
        return contactoService.listar();
    }

    // PATCH = actualización parcial: solo cambia el campo "leido"
    @PatchMapping("/{id}/leido")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Marcar como leído / no leído", description = "Solo ADMIN. Ejemplo: /api/contacto/1/leido?valor=true")
    public MensajeContacto marcarLeido(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean valor) {
        return contactoService.marcarLeido(id, valor);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar mensaje", description = "Solo ADMIN")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        contactoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
