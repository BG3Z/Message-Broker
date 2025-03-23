package sdis.broker.client.unit;

import sdis.broker.common.MensajeProtocolo;
import sdis.broker.common.Primitiva;

/**
 * Clase que representa a la primitiva Echo.
 */
public class Echo extends ClienteUnitario {

    /**
     * Metodo principal que recibe los parámetros desde la línea de comandos y ejecuta la operación
     * de eco de una palabra.
     * Si no se pasan los parámetros correctos, el programa terminará y mostrará un mensaje de uso.
     *
     * @param args Los parámetros de la línea de comandos: {@code host} y {@code palabra}.
     * @throws java.io.IOException Si ocurre un error de entrada/salida al ejecutar la operación.
     */
    public static void main(String[] args) throws java.io.IOException {
        if (args.length != 2) {
            System.out.println("\u001B[33mUso: java sdis.broker.client.unit.Echo host palabra\u001B[0m");
            System.exit(-1);
        }

        String host = args[0];
        String palabra = args[1];

        try {
            // ejecuta el metodo ejecutar de cliente unitario para la primitiva Echo
            ejecutar(host, new MensajeProtocolo(Primitiva.ECHO, palabra));
        } catch (sdis.broker.common.MalMensajeProtocoloException e) {
            // Maneja errores relacionados con el protocolo de mensajes
            System.err.println("\u001B[31mCliente: Error mensaje Protocolo: " + e + "\u001B[0m");
        }
    }
}
