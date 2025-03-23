package sdis.broker.client.unit;

import java.io.IOException;
import java.net.Socket;
import sdis.broker.common.MensajeProtocolo;
import sdis.broker.common.MalMensajeProtocoloException;
import sdis.broker.common.Primitiva;

/**
 * Clase que representa a la primitiva Info
 */
public class Info extends ClienteUnitario {
    /**
     * Metodo principal que recibe el host del servidor y envía una solicitud de tipo INFO al servidor.
     * Si no se pasa el parámetro correcto, el programa terminará y mostrará un mensaje de uso.
     *
     * @param args Los parámetros de la línea de comandos: el {@code host} del servidor.
     * @throws java.io.IOException Si ocurre un error de entrada/salida al conectarse al servidor o durante la comunicación.
     */
    public static void main(String[] args) throws java.io.IOException {
        if (args.length != 1) {
            System.out.println("\u001B[33mUso: java sdis.broker.client.unit.INFO host\u001B[0m");
            System.exit(-1);
        }

        String host = args[0];

        try {
            ejecutar(host, new MensajeProtocolo(Primitiva.INFO));
        } catch (sdis.broker.common.MalMensajeProtocoloException e) {
            System.err.println("\u001B[31mCliente: Error mensaje Protocolo: " + e + "\u001B[0m");
        }

    }

}

