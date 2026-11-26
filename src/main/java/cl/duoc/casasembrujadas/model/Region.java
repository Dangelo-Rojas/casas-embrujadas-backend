package cl.duoc.casasembrujadas.model;

import jakarta.persistence.*;

// Una región de Chile. Se usa en el select del Registro (al elegirla se cargan sus comunas)
@Entity
@Table(name = "region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT de MySQL
    private Long id;

    // el "value" que usa el select del frontend: metropolitana, araucania, nuble
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 60)
    private String nombre;

    public Region() {
    }

    public Region(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
