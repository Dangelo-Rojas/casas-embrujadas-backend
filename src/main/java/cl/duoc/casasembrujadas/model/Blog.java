package cl.duoc.casasembrujadas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Caso curioso del blog (antes en frontend/src/data/blogs.js)
@Entity
@Table(name = "blog")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // va en la URL del frontend: /blogs/dama-de-blanco
    @NotBlank
    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank
    @Column(nullable = false, length = 400)
    private String resumen;

    @NotBlank
    @Column(nullable = false)
    private String imagen;

    @NotBlank
    @Column(name = "imagen_grande", nullable = false)
    private String imagenGrande;

    @NotNull
    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDate fechaPublicacion;

    // cada párrafo del caso, en orden (tabla blog_parrafo)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "blog_parrafo", joinColumns = @JoinColumn(name = "blog_id"))
    @OrderColumn(name = "orden")
    @Column(name = "texto", nullable = false, length = 2000)
    private List<String> parrafos = new ArrayList<>();

    public Blog() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public String getImagenGrande() { return imagenGrande; }
    public void setImagenGrande(String imagenGrande) { this.imagenGrande = imagenGrande; }
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
    public List<String> getParrafos() { return parrafos; }
    public void setParrafos(List<String> parrafos) { this.parrafos = parrafos; }
}
