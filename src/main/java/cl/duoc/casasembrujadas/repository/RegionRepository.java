package cl.duoc.casasembrujadas.repository;

import cl.duoc.casasembrujadas.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<Entidad, TipoDelId> ya trae findAll, findById, save, deleteById, count...
// Solo declaramos las consultas extra; Spring arma el SQL a partir del nombre del método
public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByCodigo(String codigo);
}
