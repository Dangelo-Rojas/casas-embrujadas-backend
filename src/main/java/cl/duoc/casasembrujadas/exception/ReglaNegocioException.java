package cl.duoc.casasembrujadas.exception;

// Se lanza cuando una operación rompe una regla del negocio
// (ej: correo ya registrado, slug repetido, propiedad inactiva en un pedido). Se convierte en HTTP 400
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
