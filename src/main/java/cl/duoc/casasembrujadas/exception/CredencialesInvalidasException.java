package cl.duoc.casasembrujadas.exception;

// Correo o contraseña incorrectos, o refresh token inválido. Se responde 401
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
