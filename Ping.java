package sdis.broker.client.unit;

import sdis.broker.common.MensajeProtocolo;
import sdis.broker.common.Primitiva;

/**
 * Clase que representa a la primitva Ping
 */
public class Ping extends ClienteUnitario {

    /**
     * Metodo principal que recibe el parámetro desde la línea de comandos y ejecuta la operación
     * de ping al servidor para comprobar la conexion.
     * Si no se pasa el parámetro correcto, el programa terminará y mostrará un mensaje de uso.
     *
     * @param args Los parámetros de la línea de comandos: el host del servidor.
     * @throws java.io.IOException Si ocurre un error de entrada/salida al ejecutar la operación.
     */
    public static void main(String[] args) throws java.io.IOException {
        if (args.length != 1) {
            System.out.println("\u001B[33mUso: java sdis.broker.client.unit.Ping host\u001B[0m");
            System.exit(-1);
        }

        String host = args[0];

        try {
            // ejecuta el metodo ejecutar de cliente unitario para la primitiva Ping
            ejecutar(host, new MensajeProtocolo(Primitiva.PING));
        } catch (sdis.broker.common.MalMensajeProtocoloException e) {
            System.err.println("\u001B[31mCliente: Error mensaje Protocolo: " + e + "\u001B[0m");
        }
    }
}
