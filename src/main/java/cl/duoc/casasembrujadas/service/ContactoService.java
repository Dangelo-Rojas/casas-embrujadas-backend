package cl.duoc.casasembrujadas.service;

import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.model.MensajeContacto;
import cl.duoc.casasembrujadas.repository.MensajeContactoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ContactoService {

    private final MensajeContactoRepository mensajeRepository;

    public ContactoService(MensajeContactoRepository mensajeRepository) {
        this.mensajeRepository = mensajeRepository;
    }

    @Transactional
    public MensajeContacto enviar(MensajeContacto mensaje) {
        mensaje.setId(null);
        mensaje.setLeido(false); // un mensaje nuevo siempre llega sin leer
        mensaje.setNombre(mensaje.getNombre().trim());
        mensaje.setMensaje(mensaje.getMensaje().trim());
        return mensajeRepository.save(mensaje);
    }

    public List<MensajeContacto> listar() {
        return mensajeRepository.findAllByOrderByFechaDesc();
    }

    @Transactional
    public MensajeContacto marcarLeido(Long id, boolean leido) {
        MensajeContacto mensaje = buscar(id);
        mensaje.setLeido(leido);
        return mensajeRepository.save(mensaje);
    }

    @Transactional
    public void eliminar(Long id) {
        mensajeRepository.delete(buscar(id));
    }

    private MensajeContacto buscar(Long id) {
        return mensajeRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el mensaje con id " + id));
    }
}
