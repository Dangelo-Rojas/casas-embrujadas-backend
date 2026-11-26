package cl.duoc.casasembrujadas.service;

import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.model.Rol;
import cl.duoc.casasembrujadas.model.Usuario;
import cl.duoc.casasembrujadas.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// Administración de usuarios (solo ADMIN)
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    @Transactional
    public Usuario cambiarRol(Long id, Rol nuevoRol, Long idAdminQueSolicita) {
        // regla: un admin no puede quitarse a sí mismo el rol (se quedaría sin acceso al panel)
        if (id.equals(idAdminQueSolicita)) {
            throw new ReglaNegocioException("No puedes cambiar tu propio rol");
        }
        Usuario usuario = buscar(id);
        usuario.setRol(nuevoRol);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id, Long idAdminQueSolicita) {
        if (id.equals(idAdminQueSolicita)) {
            throw new ReglaNegocioException("No puedes eliminar tu propia cuenta");
        }
        usuarioRepository.delete(buscar(id));
        usuarioRepository.flush(); // si tiene pedidos, MySQL lo impide y respondemos 409
    }
}
