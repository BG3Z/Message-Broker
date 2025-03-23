package sdis.broker.client.unit;

import sdis.broker.common.MensajeProtocolo;
import sdis.broker.common.Primitiva;
import java.util.Arrays;

/**
 * Clase que representa al conjunto de primitivas Xauth y State
 */
public class AuthState extends ClienteUnitario {

    /**
     * Metodo principal que recibe los parámetros desde la línea de comandos y ejecuta las primitivas
     * Xauth y State
     * Si los parámetros no son correctos, el programa terminará y mostrará un mensaje de uso.
     *
     * @param args Los parámetros de la línea de comandos: {@code host}, {@code usuario}, {@code password}.
     * @throws java.io.IOException Si ocurre un error de entrada/salida al ejecutar la operación.
     */
    public static void main(String[] args) throws java.io.IOException {
        if (args.length != 3) {
            System.out.println("\u001B[33mUso: java sdis.broker.client.unit.AuthState host usuario contraseña\u001B[0m");
            System.exit(-1);
        }

        String host = args[0];
        String usuario = args[1];
        String password = args[2];

        try {
            // ejecuta el metodo ejecutar de cliente unitario para las primitivas Xauth y State
            ejecutar(host, Arrays.asList(
                    new MensajeProtocolo(Primitiva.XAUTH, usuario, password), // Autenticación
                    new MensajeProtocolo(Primitiva.STATE) // Consulta de estado
            ));
        } catch (sdis.broker.common.MalMensajeProtocoloException e) {
            System.err.println("\u001B[31mCliente: Error mensaje Protocolo: " + e + "\u001B[0m");
        }
    }
}
