package cl.duoc.casasembrujadas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Pedido: se crea cuando el cliente presiona "Finalizar compra" en el carrito
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // quién compró (sale del token del usuario que inició sesión, no de lo que mande el navegador)
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // el total lo calcula el BACKEND con los precios de la base de datos (no se confía en el frontend)
    @Column(nullable = false)
    private Long total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    // Un pedido tiene muchas líneas de detalle (1 a N).
    // cascade = ALL: al guardar el pedido se guardan también sus detalles
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetallePedido> detalles = new ArrayList<>();

    @PrePersist
    void alCrear() {
        fecha = LocalDateTime.now();
    }

    // agrega una línea y deja enlazados los dos lados de la relación
    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        detalles.add(detalle);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getFecha() { return fecha; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public List<DetallePedido> getDetalles() { return detalles; }
}
