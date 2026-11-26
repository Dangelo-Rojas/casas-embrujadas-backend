package cl.duoc.casasembrujadas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

// Una línea del pedido: qué propiedad, cuántas y a qué precio.
// Es la tabla intermedia de la relación N a N entre pedido y propiedad
@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @JsonIgnore evita un ciclo infinito al convertir a JSON (pedido -> detalle -> pedido -> ...)
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "propiedad_id")
    private Propiedad propiedad;

    @Column(nullable = false)
    private int cantidad;

    // se copia el precio del momento de la compra, por si después la propiedad cambia de precio
    @Column(name = "precio_unitario", nullable = false)
    private Long precioUnitario;

    public DetallePedido() {
    }

    public DetallePedido(Propiedad propiedad, int cantidad) {
        this.propiedad = propiedad;
        this.cantidad = cantidad;
        this.precioUnitario = propiedad.getPrecio();
    }

    // no es columna: se calcula y sale en el JSON como "subtotal"
    public Long getSubtotal() {
        return precioUnitario * cantidad;
    }

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Propiedad getPropiedad() { return propiedad; }
    public int getCantidad() { return cantidad; }
    public Long getPrecioUnitario() { return precioUnitario; }
}
