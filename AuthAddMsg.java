package sdis.broker.client.unit;

import sdis.broker.common.MensajeProtocolo;
import sdis.broker.common.Primitiva;
import java.util.Arrays;

/**
 * Clase que representa al conjunto de primitivas Xauth y AddMsg.
 */
public class AuthAddMsg extends ClienteUnitario {
    static final private int PUERTO = 2000;
    static java.io.ObjectInputStream ois = null;
    static java.io.ObjectOutputStream oos = null;

    /**
     * Metodo principal que recibe los parametros desde la linea de comandos.
     * @param args Los parámetros de la línea de comandos: {@code host}, {@code usuario}, {@code password}, {@code clave}, {@code mensaje}.
     * @throws java.io.IOException Si surge un error de entrada/salida, nos manda el error.
     */
    public static void main(String[] args) throws java.io.IOException {
        if (args.length != 5) {
            System.out.println("\u001B[33mUso: java sdis.broker.client.unit.AuthAddMsg host usuario contraseña clave mensaje\u001B[0m");
            System.exit(-1);
        }
        String host = args[0];
        String usuario = args[1];
        String password = args[2];
        String clave = args[3];
        String mensaje = args[4];

        try {
            // ejecuta el metodo ejecutar de ClienteUnitario para las primitivas Xauth y AddMsg
            ejecutar(host, Arrays.asList(
                    new MensajeProtocolo(Primitiva.XAUTH, usuario, password), // Autenticación
                    new MensajeProtocolo(Primitiva.ADDMSG, clave, mensaje) // Añadir mensaje
            ));
        } catch (sdis.broker.common.MalMensajeProtocoloException e) {
            System.err.println("\u001B[31mCliente: Error mensaje Protocolo: " + e + "\u001B[0m");
        }


    }
}