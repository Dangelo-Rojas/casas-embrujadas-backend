package cl.duoc.casasembrujadas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

// Mensaje enviado desde el formulario de Contacto (no exige iniciar sesión)
@Entity
@Table(name = "mensaje_contacto")
public class MensajeContacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // mismas reglas que validarContacto() del frontend
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    @Pattern(regexp = ".*@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$",
             message = "Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String correo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500, message = "El mensaje admite máximo 500 caracteres")
    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // el admin lo marca como leído desde su panel
    @Column(nullable = false)
    private boolean leido = false;

    @PrePersist
    void alCrear() {
        fecha = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public LocalDateTime getFecha() { return fecha; }
    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
}
