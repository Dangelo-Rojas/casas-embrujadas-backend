package cl.duoc.casasembrujadas.repository;

import cl.duoc.casasembrujadas.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // "Mis pedidos": los del usuario que inició sesión, del más nuevo al más antiguo
    List<Pedido> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    // panel del admin: todos, del más nuevo al más antiguo
    List<Pedido> findAllByOrderByFechaDesc();
}
