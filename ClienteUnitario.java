package sdis.broker.client.unit;

import sdis.broker.common.MensajeProtocolo;
import sdis.broker.common.MalMensajeProtocoloException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import sdis.broker.common.Primitiva;

/**
 * Clase abstracta que representa a un cliente unitario el cual, ejecuta una
 * o varias primitivas para comunicarse con el servidor.
 */
public abstract class ClienteUnitario {
    /**
     * Puerto del usuario para la conexion con el servidor
     */
    static final private int PUERTO = 2000;
    public static final String ROJO = "\u001B[31m";
    public static final String RESET = "\u001B[0m";

    /**
     * Metodo que ejecuta una sola primitiva.
     * Establece una conexión con el servidor, envía el mensaje y recibe la respuesta.
     * Si la respuesta no es un error, se realiza una prueba de petición-respuesta con el servidor.
     *
     * @param host El host del servidor al que se conecta el cliente.
     * @param mensaje El mensaje a enviar al servidor encapsulado en un objeto MensajeProtocolo.
     * @throws java.io.IOException Si ocurre un error de conexión o entrada/salida.
     */
    public static void ejecutar(String host, MensajeProtocolo mensaje) throws java.io.IOException {
        try (Socket sock = new Socket(host, PUERTO);
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {
            MensajeProtocolo m = (MensajeProtocolo) ois.readObject(); // Puede ser INFO o ERROR.
            System.out.println(m);
            if (m.getPrimitiva() != Primitiva.ERROR) {
                pruebaPeticionRespuesta(oos, ois, mensaje);
            }


        } catch (java.io.EOFException e) {
            System.err.println(ROJO +"Cliente: Fin de conexión." +RESET);
        } catch (java.io.IOException e) {
            System.err.println(ROJO +"Cliente: Error de apertura o E/S sobre objetos: " + e+RESET);
        } catch (Exception e) {
            System.err.println(ROJO +"Cliente: Excepción. Cerrando Sockets: " + e+RESET);
        }
    }

    /**
     * Metodo que ejecuta varias primitivas.
     * Establece una conexión con el servidor, envía los mensajes uno por uno y recibe las respuestas.
     * Si algún mensaje no es autorizado o hay un error, la ejecución se detiene.
     *
     * @param host El host del servidor al que se conecta el cliente.
     * @param mensajes Una lista de objetos que contienen los mensajes a enviar.
     * @throws java.io.IOException Si ocurre un error de conexión o entrada/salida.
     */
    public static void ejecutar(String host, List<MensajeProtocolo> mensajes) throws java.io.IOException {
        try (Socket sock = new Socket(host, PUERTO);
             ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {

            MensajeProtocolo m = (MensajeProtocolo) ois.readObject(); // Puede ser INFO o ERROR.
            System.out.println(m);

            if (m.getPrimitiva() != Primitiva.ERROR) {
                for (MensajeProtocolo mensaje : mensajes) {
                    m= pruebaPeticionRespuesta(oos, ois, mensaje);
                    if (m.getPrimitiva() == Primitiva.NOTAUTH || m.getPrimitiva() == Primitiva.ERROR) {
                        return;
                    }
                    System.in.read();
                }
            }


        } catch (java.io.EOFException e) {
            System.err.println(ROJO+"Cliente: Fin de conexión."+RESET);
        } catch (java.io.IOException e) {
            System.err.println(ROJO+"Cliente: Error de apertura o E/S sobre objetos: " + e+RESET);
        } catch (Exception e) {
            System.err.println(ROJO+"Cliente: Excepción. Cerrando Sockets: " + e+RESET);
        }
    }

    /**
     * Realiza una prueba de petición-respuesta con el servidor.
     * El mensaje se envía al servidor y se espera la respuesta.
     * La respuesta se imprime en la consola.
     *
     * @param oos para enviar el mensaje al servidor.
     * @param ois para recibir el mensaje de respuesta del servidor.
     * @param mp  Mensaje que se va a enviar.
     * @return Respuesta del servidor.
     * @throws java.io.IOException Si ocurre un error de entrada o salida al enviar el mensaje.
     * @throws MalMensajeProtocoloException Si el mensaje no tiene el formato adecuado.
     * @throws ClassNotFoundException Si no se puede deserializar el mesaje recibido.
     */

    private static MensajeProtocolo pruebaPeticionRespuesta(ObjectOutputStream oos, ObjectInputStream ois, MensajeProtocolo mp)
            throws java.io.IOException, MalMensajeProtocoloException, ClassNotFoundException {
        System.out.println("> " + mp);
        oos.writeObject(mp);
        mp = (MensajeProtocolo) ois.readObject();
        System.out.println("< " + mp);
        return mp;
    }


}

