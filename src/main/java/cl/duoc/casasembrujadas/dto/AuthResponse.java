package cl.duoc.casasembrujadas.dto;

import cl.duoc.casasembrujadas.model.Usuario;

// Lo que devuelve el login / registro / refresh:
// - token: el JWT de acceso (va en el header Authorization de cada petición)
// - refreshToken: sirve para pedir un token nuevo cuando el de acceso vence
// - expiraEn: segundos que dura el token de acceso
// - usuario: datos públicos del usuario (sin contraseña)
public record AuthResponse(String token, String refreshToken, String tipo, long expiraEn, Usuario usuario) {
}
