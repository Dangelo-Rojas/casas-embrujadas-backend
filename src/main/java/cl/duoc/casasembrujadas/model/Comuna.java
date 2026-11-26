package cl.duoc.casasembrujadas.model;

import jakarta.persistence.*;

// Comuna: pertenece a UNA región y una región tiene MUCHAS comunas (relación N a 1)
@Entity
@Table(name = "comuna")
public class Comuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    // @ManyToOne crea la columna region_id como clave foránea hacia la tabla region
    @ManyToOne(optional = false)
    @JoinColumn(name = "region_id")
    private Region region;

    public Comuna() {
    }

    public Comuna(String nombre, Region region) {
        this.nombre = nombre;
        this.region = region;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }
}
