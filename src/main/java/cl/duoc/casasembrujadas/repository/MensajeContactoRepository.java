package cl.duoc.casasembrujadas.repository;

import cl.duoc.casasembrujadas.model.MensajeContacto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MensajeContactoRepository extends JpaRepository<MensajeContacto, Long> {

    List<MensajeContacto> findAllByOrderByFechaDesc();
}
