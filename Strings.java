package sdis.broker.common;

/**
 * Clase utilizada para almacenar los diferentes mensajes que el servidor puede enviar
 * en respuesta a diversas primitivas o eventos del sistema
 */

public class Strings {
    /**
     * Mensaje de bienvenida al arancar el servidor.
     *
     * @return El mensaje de bienvenida.
     */
    public static String getBienvenida() {
        return "Bienvenido al Broker de Mensajeria";
    }

    /**
     * Mensaje de exito al realizar la conexion del cliente
     *
     * @return Mensaje de exito.
     */
    public static String logueado() {
        return "User successfully logged";
    }

    /**
     * Mensaje para el black list si se llega al nr maximo de conexiones.
     *
     * @return Mensaje de error.
     */
    public static String maxNumConexiones() {
        return "Err Max Number of connections reached.";
    }

    /**
     * Mensaje que se devuelve cuando se alcanza el número máximo de intentos fallidos de inicio de sesión.
     *
     * @return Mensaje de error indicando que se ha alcanzado el límite de intentos fallidos.
     */
    public static String maxNumFallosAccesos() {
        return "Err Max Number of login attempts reached.";
    }

    /**
     * Mensaje que se devuelve cuando un usuario intenta realizar una acción sin ser administrador.
     *
     * @return Mensaje de error indicando que el usuario no tiene privilegios de administrador.
     */
    public static String noAdmin() {
        return "NO ADMIN";
    }

    /**
     * Mensaje que se devuelve cuando el usuario no ha iniciado sesión y realiza una acción que requiere autenticación.
     *
     * @return Mensaje de error indicando que se requiere iniciar sesión.
     */
    public static String noLogueado() {
        return "User login is required";
    }

    /**
     * Mensaje que se devuelve cuando las credenciales del usuario no coinciden.
     *
     * @return Mensaje de error indicando que las credenciales no coinciden.
     */
    public static String fallologin() {
        return "Err 401 ~ Credentials DO NOT MATCH. Try again";
    }

    /**
     * Mensaje que se devuelve cuando la primitiva solicitada no es reconocida.
     *
     * @return Mensaje de error indicando que la primitiva no es reconocida.
     */
    public static String primitivaNoReconocida() {
        return "Primitiva no reconocida";
    }
}