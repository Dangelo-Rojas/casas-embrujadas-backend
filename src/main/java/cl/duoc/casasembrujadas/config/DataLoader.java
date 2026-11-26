package cl.duoc.casasembrujadas.config;

import cl.duoc.casasembrujadas.model.*;
import cl.duoc.casasembrujadas.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

// Carga los datos iniciales la PRIMERA vez que arranca el backend (si las tablas están vacías).
// Son los mismos datos que el frontend tenía escritos en productos.js y blogs.js.
// CommandLineRunner: Spring ejecuta run() automáticamente apenas termina de arrancar
@Component
public class DataLoader implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
    private final PropiedadRepository propiedadRepository;
    private final BlogRepository blogRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Inyección por constructor: Spring nos entrega los repositorios ya creados
    public DataLoader(RegionRepository regionRepository, ComunaRepository comunaRepository,
                      PropiedadRepository propiedadRepository, BlogRepository blogRepository,
                      UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.regionRepository = regionRepository;
        this.comunaRepository = comunaRepository;
        this.propiedadRepository = propiedadRepository;
        this.blogRepository = blogRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (regionRepository.count() == 0) {
            cargarRegionesYComunas();
        }
        if (propiedadRepository.count() == 0) {
            cargarPropiedades();
        }
        if (blogRepository.count() == 0) {
            cargarBlogs();
        }
        if (usuarioRepository.count() == 0) {
            cargarUsuarios();
        }
    }

    // Usuarios de prueba (aparecen en el manual de usuario):
    //   ADMIN   -> admin@duoc.cl     / admin123
    //   CLIENTE -> cliente@gmail.com / cliente123
    private void cargarUsuarios() {
        Comuna santiago = comunaRepository.findByRegionCodigoOrderByNombre("metropolitana").stream()
                .filter(c -> c.getNombre().equals("Santiago")).findFirst().orElseThrow();
        Comuna chillan = comunaRepository.findByRegionCodigoOrderByNombre("nuble").stream()
                .filter(c -> c.getNombre().equals("Chillán")).findFirst().orElseThrow();

        Usuario admin = new Usuario();
        admin.setNombre("Administrador");
        admin.setCorreo("admin@duoc.cl");
        admin.setContrasenaHash(passwordEncoder.encode("admin123"));
        admin.setComuna(santiago);
        admin.setRol(Rol.ADMIN);
        usuarioRepository.save(admin);

        Usuario cliente = new Usuario();
        cliente.setNombre("Cliente de Prueba");
        cliente.setCorreo("cliente@gmail.com");
        cliente.setContrasenaHash(passwordEncoder.encode("cliente123"));
        cliente.setComuna(chillan);
        cliente.setRol(Rol.CLIENTE);
        usuarioRepository.save(cliente);
    }

    // mismas regiones y comunas que regionesComunas en utils/validaciones.js
    private void cargarRegionesYComunas() {
        Region rm = regionRepository.save(new Region("metropolitana", "Región Metropolitana"));
        Region araucania = regionRepository.save(new Region("araucania", "Región de la Araucanía"));
        Region nuble = regionRepository.save(new Region("nuble", "Región de Ñuble"));

        for (String c : List.of("Santiago", "Puente Alto", "Maipú", "Providencia")) {
            comunaRepository.save(new Comuna(c, rm));
        }
        for (String c : List.of("Temuco", "Villarrica", "Pucón")) {
            comunaRepository.save(new Comuna(c, araucania));
        }
        for (String c : List.of("Chillán", "San Carlos", "Bulnes")) {
            comunaRepository.save(new Comuna(c, nuble));
        }
    }

    private void cargarPropiedades() {
        propiedadRepository.save(propiedad("mansion-victoriana", "Mansión Victoriana", "Valparaíso", 185_000_000L,
                "Impresionante mansión de estilo victoriano construida en 1899. Cuenta con 8 habitaciones, una escalera principal que cruje exactamente a las 3:00 AM, y un retrato en el pasillo cuyos ojos siguen a las visitas. Ideal para quienes buscan tranquilidad... eterna.",
                "/img/mansion-victoriana.jpg", "/img/mansion-victoriana-grande.jpg",
                List.of("/img/mansion-victoriana-grande.jpg", "/img/mansion-victoriana.jpg", "/img/mansion-victoriana-grande.jpg"), true));

        propiedadRepository.save(propiedad("cabana-bosque", "Cabaña del Bosque Maldito", "Región de la Araucanía", 62_500_000L,
                "Acogedora cabaña de madera rodeada de un bosque nativo donde los árboles susurran nombres que nadie reconoce. Perfecta para escapadas de fin de semana... si logras encontrar el camino de vuelta.",
                "/img/cabana-bosque.jpg", "/img/cabana-bosque.jpg",
                List.of("/img/cabana-bosque.jpg", "/img/cabana-bosque.jpg", "/img/cabana-bosque.jpg"), true));

        propiedadRepository.save(propiedad("faro-ahogado", "Faro del Ahogado", "Costa de Ñuble", 99_900_000L,
                "Faro centenario junto al mar que, según los vecinos, se enciende solo en noches de tormenta. Vista privilegiada al océano y a algo más que prefiere quedarse sin nombre.",
                "/img/faro-abandonado.jpg", "/img/caso-curioso-2-grande.jpg",
                List.of("/img/faro-abandonado.jpg", "/img/faro-abandonado.jpg", "/img/faro-abandonado.jpg"), true));

        propiedadRepository.save(propiedad("casona-colonial", "Casona Colonial", "Región Metropolitana", 140_000_000L,
                "Casona de adobe con patio central, más de 150 años de historia y una dama de blanco que pasea entre los rosales pasada la medianoche.",
                "/img/casona-colonial.jpg", "/img/casona-colonial.jpg",
                List.of("/img/casona-colonial.jpg", "/img/casona-colonial.jpg", "/img/casona-colonial.jpg"), false));

        propiedadRepository.save(propiedad("hospital-san-judas", "Ex Hospital San Judas", "Concepción", 210_000_000L,
                "Antiguo recinto hospitalario, cerrado desde 1978. Pasillos amplios, buena ventilación, y llamadas de enfermería que a veces suenan solas.",
                "/img/hospital-abandonado.jpg", "/img/hospital-abandonado.jpg",
                List.of("/img/hospital-abandonado.jpg", "/img/hospital-abandonado.jpg", "/img/hospital-abandonado.jpg"), false));

        propiedadRepository.save(propiedad("castillo-niebla", "Castillo de la Niebla", "Región de Los Lagos", 320_000_000L,
                "Castillo de piedra envuelto en niebla casi todo el año, con torreones, foso seco y un eco que responde preguntas que nadie hizo.",
                "/img/castillo-niebla.jpg", "/img/castillo-niebla.jpg",
                List.of("/img/castillo-niebla.jpg", "/img/castillo-niebla.jpg", "/img/castillo-niebla.jpg"), true));
    }

    private void cargarBlogs() {
        Blog dama = new Blog();
        dama.setSlug("dama-de-blanco");
        dama.setTitulo("Caso curioso #1: La dama de blanco");
        dama.setResumen("Vecinos de la Mansión Victoriana reportan haber visto a una mujer vestida de blanco caminando por el jardín cada medianoche. Descubre la historia completa detrás del avistamiento.");
        dama.setImagen("/img/caso-curioso-1.jpg");
        dama.setImagenGrande("/img/caso-curioso-1-grande.jpg");
        dama.setFechaPublicacion(LocalDate.of(2026, 9, 15));
        dama.setParrafos(List.of(
                "Desde hace más de cuarenta años, distintos vecinos de la Mansión Victoriana aseguran haber visto a una mujer vestida de blanco caminando lentamente entre los rosales del jardín trasero, siempre pasada la medianoche.",
                "Según los registros de la propia mansión, la figura coincidiría con la esposa del primer dueño de la propiedad, quien según se cuenta nunca llegó a mudarse oficialmente... porque nunca se fue.",
                "Los actuales interesados en comprar la propiedad destacan este detalle como un plus: \"no todos los días se compra una casa con compañía incluida\", comentó un futuro comprador durante la visita guiada."));
        blogRepository.save(dama);

        Blog faro = new Blog();
        faro.setSlug("faro-se-enciende-solo");
        faro.setTitulo("Caso curioso #2: El faro que se enciende solo");
        faro.setResumen("A pesar de llevar décadas desconectado, el Faro del Ahogado se enciende algunas noches de tormenta. Te contamos qué dicen los pescadores de la zona.");
        faro.setImagen("/img/caso-curioso-2.jpg");
        faro.setImagenGrande("/img/caso-curioso-2-grande.jpg");
        faro.setFechaPublicacion(LocalDate.of(2026, 10, 1));
        faro.setParrafos(List.of(
                "El Faro del Ahogado fue desconectado de la red eléctrica hace más de veinte años, cuando dejó de operar oficialmente. Sin embargo, pescadores de la zona insisten en que, durante las noches de tormenta más fuertes, su luz vuelve a encenderse.",
                "Algunos lo atribuyen a un guardafaros que se negó a abandonar su puesto incluso después de su fallecimiento. Otros, simplemente, prefieren no acercarse demasiado a comprobarlo.",
                "Sea como sea, la propiedad se vende con la particularidad de que \"la luz se enciende sola, sin necesidad de pagar cuenta de luz\", según destaca el aviso de venta."));
        blogRepository.save(faro);
    }

    // ayudante para no repetir 10 setters por cada propiedad
    private Propiedad propiedad(String slug, String nombre, String ubicacion, Long precio, String descripcion,
                                String imagen, String imagenGrande, List<String> imagenes, boolean destacada) {
        Propiedad p = new Propiedad();
        p.setSlug(slug);
        p.setNombre(nombre);
        p.setUbicacion(ubicacion);
        p.setPrecio(precio);
        p.setDescripcion(descripcion);
        p.setImagen(imagen);
        p.setImagenGrande(imagenGrande);
        p.setImagenes(new java.util.ArrayList<>(imagenes));
        p.setDestacada(destacada);
        return p;
    }
}
