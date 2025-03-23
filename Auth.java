package sdis.broker.client.unit;

import sdis.broker.common.MensajeProtocolo;
import sdis.broker.common.Primitiva;

/**
 * Clase que representa la primitiva XAuth
 */
public class Auth extends ClienteUnitario {

    /**
     * Metodo principal que recibe los parametros desde la linea de comandos.
     * @param args Los parámetros de la línea de comandos: {@code host}, {@code usuario}, {@code password}.
     * @throws java.io.IOException Si surge un error de entrada/salida, nos manda el error.
     */
    public static void main(String[] args) throws java.io.IOException {
        if (args.length != 3) {
            System.out.println("\u001B[33mUso: java sdis.broker.client.unit.Auth host usuario contraseña\u001B[0m");
            System.exit(-1);
        }


        String host = args[0];
        String usuario = args[1];
        String password = args[2];


        try {
            // ejecuta el metodo ejecutar de cliente unitario para la primitiva Xauth
            ejecutar(host, new MensajeProtocolo(Primitiva.XAUTH, usuario, password));
        } catch (sdis.broker.common.MalMensajeProtocoloException e) {
            System.err.println("\u001B[31mCliente: Error mensaje Protocolo: " + e + "\u001B[0m");
        }

    }
}
