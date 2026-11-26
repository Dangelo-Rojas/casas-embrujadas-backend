package cl.duoc.casasembrujadas.security;

// Lo que sabemos del usuario que hizo la petición, sacado de su token JWT.
// Los controladores lo reciben con @AuthenticationPrincipal (ej: para saber de quién es un pedido)
public record UsuarioAutenticado(Long id, String correo, String nombre, String rol) {
}
