package cl.duoc.casasembrujadas.service;

import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.model.Comuna;
import cl.duoc.casasembrujadas.model.Region;
import cl.duoc.casasembrujadas.repository.ComunaRepository;
import cl.duoc.casasembrujadas.repository.RegionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RegionService {

    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;

    public RegionService(RegionRepository regionRepository, ComunaRepository comunaRepository) {
        this.regionRepository = regionRepository;
        this.comunaRepository = comunaRepository;
    }

    public List<Region> listarRegiones() {
        return regionRepository.findAll();
    }

    // las comunas del select dependiente del Registro
    public List<Comuna> comunasDeRegion(String codigoRegion) {
        if (regionRepository.findByCodigo(codigoRegion).isEmpty()) {
            throw new RecursoNoEncontradoException("No existe la región " + codigoRegion);
        }
        return comunaRepository.findByRegionCodigoOrderByNombre(codigoRegion);
    }
}
