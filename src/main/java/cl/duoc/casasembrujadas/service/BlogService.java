package cl.duoc.casasembrujadas.service;

import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.model.Blog;
import cl.duoc.casasembrujadas.repository.BlogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public List<Blog> listar() {
        return blogRepository.findAllByOrderByFechaPublicacionDesc();
    }

    public Blog buscarPorSlug(String slug) {
        return blogRepository.findBySlug(slug)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el caso " + slug));
    }

    public Blog buscarPorId(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el caso con id " + id));
    }

    @Transactional
    public Blog crear(Blog datos) {
        if (blogRepository.findBySlug(datos.getSlug()).isPresent()) {
            throw new ReglaNegocioException("Ya existe un caso con el slug " + datos.getSlug());
        }
        datos.setId(null);
        return blogRepository.save(datos);
    }

    @Transactional
    public Blog actualizar(Long id, Blog datos) {
        Blog actual = buscarPorId(id);
        actual.setSlug(datos.getSlug());
        actual.setTitulo(datos.getTitulo());
        actual.setResumen(datos.getResumen());
        actual.setImagen(datos.getImagen());
        actual.setImagenGrande(datos.getImagenGrande());
        actual.setFechaPublicacion(datos.getFechaPublicacion());
        actual.getParrafos().clear();
        actual.getParrafos().addAll(datos.getParrafos());
        return blogRepository.save(actual);
    }

    @Transactional
    public void eliminar(Long id) {
        blogRepository.delete(buscarPorId(id));
    }
}
