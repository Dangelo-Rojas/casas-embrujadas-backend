package cl.duoc.casasembrujadas.controller;

import cl.duoc.casasembrujadas.model.Blog;
import cl.duoc.casasembrujadas.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@Tag(name = "Blog", description = "Casos curiosos (CRUD)")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    @Operation(summary = "Listar casos", description = "Del más reciente al más antiguo")
    public List<Blog> listar() {
        return blogService.listar();
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Buscar caso por slug")
    public Blog buscar(@PathVariable String slug) {
        return blogService.buscarPorSlug(slug);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear caso", description = "Solo ADMIN")
    public ResponseEntity<Blog> crear(@Valid @RequestBody Blog blog) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blogService.crear(blog));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar caso", description = "Solo ADMIN")
    public Blog actualizar(@PathVariable Long id, @Valid @RequestBody Blog blog) {
        return blogService.actualizar(id, blog);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar caso", description = "Solo ADMIN")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        blogService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
