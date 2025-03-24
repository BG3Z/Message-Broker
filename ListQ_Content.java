package sdis.broker.client.unit;

import sdis.broker.common.MensajeProtocolo;
import sdis.broker.common.Primitiva;

/**
 * Clase que representa a la primitiva ListQ_Content
 */
public class ListQ_Content extends ClienteUnitario {

    /**
     * Metodo principal que recibe el parámetro desde la línea de comandos y ejecuta la operación
     * de consulta del estado del servidor.
     * Si no se pasa el parámetro correcto, el programa terminará y mostrará un mensaje de uso.
     *
     * @param args Los parámetros de la línea de comandos: el {@code host} del servidor.
     * @throws java.io.IOException Si ocurre un error de entrada/salida al ejecutar la operación.
     */
    public static void main(String[] args) throws java.io.IOException {
        if (args.length != 1) {
            System.out.println("\u001B[33mUso: java sdis.broker.client.unit.ListQ_Content host\u001B[0m");
            System.exit(-1);
        }

        String host = args[0];

        try {
            // ejecuta el metodo ejecutar de cliente unitario para la primitiva State
            ejecutar(host, new MensajeProtocolo(Primitiva.LISTQ_CONTENT));
        } catch (sdis.broker.common.MalMensajeProtocoloException e) {
            System.err.println("\u001B[31mCliente: Error mensaje Protocolo: " + e + "\u001B[0m");
        }
    }
}