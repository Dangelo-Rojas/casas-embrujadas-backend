package cl.duoc.casasembrujadas.repository;

import cl.duoc.casasembrujadas.model.Propiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {

    Optional<Propiedad> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // catálogo público: solo las activas, de la más barata a la más cara
    List<Propiedad> findByActivaTrueOrderByPrecioAsc();

    // destacadas del Home
    List<Propiedad> findByDestacadaTrueAndActivaTrue();
}
