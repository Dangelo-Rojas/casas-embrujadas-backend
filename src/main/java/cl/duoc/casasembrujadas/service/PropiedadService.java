package cl.duoc.casasembrujadas.service;

import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.model.Propiedad;
import cl.duoc.casasembrujadas.repository.PropiedadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

// CAPA DE SERVICIO: aquí vive la lógica de negocio.
// El controlador solo recibe la petición HTTP y llama a estos métodos; el repositorio solo habla con la BD
@Service
public class PropiedadService {

    private final PropiedadRepository propiedadRepository;

    public PropiedadService(PropiedadRepository propiedadRepository) {
        this.propiedadRepository = propiedadRepository;
    }

    // catálogo público: solo propiedades activas
    public List<Propiedad> listarActivas() {
        return propiedadRepository.findByActivaTrueOrderByPrecioAsc();
    }

    // panel del admin: todas, incluso las desactivadas
    public List<Propiedad> listarTodas() {
        return propiedadRepository.findAll();
    }

    public List<Propiedad> listarDestacadas() {
        return propiedadRepository.findByDestacadaTrueAndActivaTrue();
    }

    public Propiedad buscarPorId(Long id) {
        return propiedadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la propiedad con id " + id));
    }

    // el detalle del frontend busca por slug; una propiedad desactivada no se muestra al público
    public Propiedad buscarActivaPorSlug(String slug) {
        return propiedadRepository.findBySlug(slug)
                .filter(Propiedad::isActiva)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la propiedad " + slug));
    }

    @Transactional
    public Propiedad crear(Propiedad datos) {
        if (propiedadRepository.existsBySlug(datos.getSlug())) {
            throw new ReglaNegocioException("Ya existe una propiedad con el slug " + datos.getSlug());
        }
        datos.setId(null); // el id lo genera la BD, aunque el cliente mande uno
        completarGaleria(datos);
        return propiedadRepository.save(datos);
    }

    @Transactional
    public Propiedad actualizar(Long id, Propiedad datos) {
        Propiedad actual = buscarPorId(id);

        // si cambia el slug, el nuevo no puede estar usado por OTRA propiedad
        if (!actual.getSlug().equals(datos.getSlug()) && propiedadRepository.existsBySlug(datos.getSlug())) {
            throw new ReglaNegocioException("Ya existe una propiedad con el slug " + datos.getSlug());
        }

        actual.setSlug(datos.getSlug());
        actual.setNombre(datos.getNombre());
        actual.setUbicacion(datos.getUbicacion());
        actual.setPrecio(datos.getPrecio());
        actual.setDescripcion(datos.getDescripcion());
        actual.setImagen(datos.getImagen());
        actual.setImagenGrande(datos.getImagenGrande());
        actual.setDestacada(datos.isDestacada());
        actual.setActiva(datos.isActiva());
        actual.getImagenes().clear();
        actual.getImagenes().addAll(datos.getImagenes() == null ? List.of() : datos.getImagenes());
        completarGaleria(actual);
        return propiedadRepository.save(actual);
    }

    @Transactional
    public void eliminar(Long id) {
        Propiedad propiedad = buscarPorId(id);
        // Si la propiedad ya está en pedidos, MySQL no deja borrarla (clave foránea) y el manejador
        // global responde 409. En ese caso el admin la puede desactivar con PUT (activa = false)
        propiedadRepository.delete(propiedad);
        propiedadRepository.flush();
    }

    // regla: si no mandan galería, se usa la imagen grande para que el detalle siempre tenga algo que mostrar
    private void completarGaleria(Propiedad p) {
        if (p.getImagenes() == null || p.getImagenes().isEmpty()) {
            p.setImagenes(new ArrayList<>(List.of(p.getImagenGrande())));
        }
    }
}
