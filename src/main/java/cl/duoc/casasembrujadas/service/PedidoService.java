package cl.duoc.casasembrujadas.service;

import cl.duoc.casasembrujadas.dto.ItemPedidoRequest;
import cl.duoc.casasembrujadas.dto.PedidoRequest;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.model.*;
import cl.duoc.casasembrujadas.repository.PedidoRepository;
import cl.duoc.casasembrujadas.repository.PropiedadRepository;
import cl.duoc.casasembrujadas.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// La lógica de negocio más importante de la tienda: convertir un carrito en un pedido
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository, PropiedadRepository propiedadRepository,
                         UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.propiedadRepository = propiedadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // @Transactional: o se guarda TODO el pedido con sus detalles, o no se guarda nada
    @Transactional
    public Pedido crear(Long usuarioId, PedidoRequest datos) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La cuenta ya no existe"));

        // si el carrito trae la misma propiedad en dos líneas, las juntamos
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (ItemPedidoRequest item : datos.items()) {
            cantidades.merge(item.slug(), item.cantidad(), Integer::sum);
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        long total = 0;

        for (Map.Entry<String, Integer> linea : cantidades.entrySet()) {
            Propiedad propiedad = propiedadRepository.findBySlug(linea.getKey())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe la propiedad " + linea.getKey()));
            if (!propiedad.isActiva()) {
                throw new ReglaNegocioException(propiedad.getNombre() + " ya no está disponible");
            }
            if (linea.getValue() > 3) {
                throw new ReglaNegocioException("Máximo 3 unidades de " + propiedad.getNombre() + " por pedido");
            }
            DetallePedido detalle = new DetallePedido(propiedad, linea.getValue());
            pedido.agregarDetalle(detalle);
            total += detalle.getSubtotal(); // el total se calcula con el precio de la BD
        }

        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> misPedidos(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaDesc();
    }

    @Transactional
    public Pedido cambiarEstado(Long id, EstadoPedido estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el pedido con id " + id));
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new ReglaNegocioException("Un pedido cancelado no se puede modificar");
        }
        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }
}
