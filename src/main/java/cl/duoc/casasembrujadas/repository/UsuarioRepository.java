package cl.duoc.casasembrujadas.repository;

import cl.duoc.casasembrujadas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // para el login: buscar la cuenta por correo
    Optional<Usuario> findByCorreo(String correo);

    // para el registro: no permitir correos repetidos
    boolean existsByCorreo(String correo);
}
