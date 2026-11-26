package cl.duoc.casasembrujadas.repository;

import cl.duoc.casasembrujadas.model.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComunaRepository extends JpaRepository<Comuna, Long> {

    // SELECT c.* FROM comuna c JOIN region r ... WHERE r.codigo = ? ORDER BY c.nombre
    List<Comuna> findByRegionCodigoOrderByNombre(String codigo);
}
