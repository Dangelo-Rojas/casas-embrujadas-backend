package cl.duoc.casasembrujadas.controller;

import cl.duoc.casasembrujadas.model.Comuna;
import cl.duoc.casasembrujadas.model.Region;
import cl.duoc.casasembrujadas.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/regiones")
@Tag(name = "Regiones", description = "Regiones y comunas para el formulario de registro")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    @Operation(summary = "Listar regiones")
    public List<Region> listar() {
        return regionService.listarRegiones();
    }

    @GetMapping("/{codigo}/comunas")
    @Operation(summary = "Comunas de una región", description = "Ejemplo: /api/regiones/nuble/comunas")
    public List<Comuna> comunas(@PathVariable String codigo) {
        return regionService.comunasDeRegion(codigo);
    }
}
