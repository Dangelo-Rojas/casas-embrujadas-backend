package cl.duoc.casasembrujadas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

// Usuario registrado (Registro / Login)
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    // unique: no pueden existir dos cuentas con el mismo correo
    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    // Guardamos el HASH de la contraseña (BCrypt), nunca la contraseña real.
    // @JsonIgnore: este campo jamás se envía en las respuestas JSON de la API
    @JsonIgnore
    @Column(name = "contrasena_hash", nullable = false)
    private String contrasenaHash;

    @ManyToOne(optional = false)
    @JoinColumn(name = "comuna_id")
    private Comuna comuna;

    // EnumType.STRING guarda el texto "ADMIN"/"CLIENTE" (y no un número 0/1, que es más frágil)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.CLIENTE;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    // se ejecuta justo antes del INSERT: así la fecha la pone el sistema y no el usuario
    @PrePersist
    void alCrear() {
        fechaRegistro = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }
    public Comuna getComuna() { return comuna; }
    public void setComuna(Comuna comuna) { this.comuna = comuna; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}
