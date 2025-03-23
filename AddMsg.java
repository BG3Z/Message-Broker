package sdis.broker.client.unit;

import sdis.broker.common.MensajeProtocolo;
import sdis.broker.common.Primitiva;

/**
 * Clase que representa a la primitiva AddMsg
 */
public class AddMsg extends ClienteUnitario{

    /**
     * Metodo principal que recibe los parametros desde la linea de comandos.
     * @param args Los parámetros de la línea de comandos: {@code host}, {@code clave}, {@code cadena}.
     * @throws java.io.IOException Si surge un error de entrada/salida, nos manda el error.
     */
    public static void main(String[] args) throws java.io.IOException {
        if (args.length != 3) {
            System.out.println("\u001B[33mUso: java sdis.broker.client.unit.AddMsg host clave cadena\u001B[0m");
            System.exit(-1);
        }
        String host = args[0];
        String clave = args[1];
        String cadena = args[2];

        try {
            //ejecutamos el metodo ejecutar de cliente unitario para la primitiva AddMsg
            ejecutar(host, new MensajeProtocolo(Primitiva.ADDMSG, clave, cadena));
        } catch (sdis.broker.common.MalMensajeProtocoloException e) {
            System.err.println("\u001B[31m Cliente: Error mensaje Protocolo: " + e + "\u001B[0m");
        }

    }
}
