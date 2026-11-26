package cl.duoc.casasembrujadas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

// Propiedad del catálogo (antes estaba escrita a mano en frontend/src/data/productos.js)
@Entity
@Table(name = "propiedad")
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // el identificador que va en la URL del frontend: /producto/faro-ahogado
    @NotBlank(message = "El slug es obligatorio")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "El slug solo admite minúsculas, números y guiones")
    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String ubicacion;

    // precio en pesos como NÚMERO (en el frontend venía como texto "$99.900.000")
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private Long precio;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String descripcion;

    // imagen de la tarjeta del catálogo
    @NotBlank(message = "La imagen es obligatoria")
    @Column(nullable = false)
    private String imagen;

    // imagen principal del detalle
    @NotBlank(message = "La imagen grande es obligatoria")
    @Column(name = "imagen_grande", nullable = false)
    private String imagenGrande;

    // Galería del detalle. @ElementCollection guarda la lista en otra tabla (propiedad_imagen)
    // ligada a esta propiedad, y @OrderColumn recuerda el orden de las miniaturas
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "propiedad_imagen", joinColumns = @JoinColumn(name = "propiedad_id"))
    @OrderColumn(name = "orden")
    @Column(name = "url", nullable = false)
    private List<String> imagenes = new ArrayList<>();

    // aparece en "destacadas" del Home
    @Column(nullable = false)
    private boolean destacada = false;

    // En vez de borrar una propiedad vendida se puede ocultar (activa = false):
    // así los pedidos antiguos que la incluyen no se rompen
    @Column(nullable = false)
    private boolean activa = true;

    public Propiedad() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public Long getPrecio() { return precio; }
    public void setPrecio(Long precio) { this.precio = precio; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public String getImagenGrande() { return imagenGrande; }
    public void setImagenGrande(String imagenGrande) { this.imagenGrande = imagenGrande; }
    public List<String> getImagenes() { return imagenes; }
    public void setImagenes(List<String> imagenes) { this.imagenes = imagenes; }
    public boolean isDestacada() { return destacada; }
    public void setDestacada(boolean destacada) { this.destacada = destacada; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
