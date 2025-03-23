package sdis.broker.client;

import java.io.*;
import java.net.Socket;
import sdis.broker.common.MensajeProtocolo;
import sdis.broker.common.Primitiva;
import sdis.broker.common.MalMensajeProtocoloException;

/**
 * Clase que representa un cliente.
 * El cliente se conecta al servidor, autentica al usuario y luego permite interactuar con el servidor
 * enviando y recibiendo mensajes relacionados con distintas primitivas.
 * El cliente soporta las siguientes primitivas: XAUTH, ADDMSG, STATE, DELETEQ y READQ.
 */
public class Cliente {

    /**
     * Puerto de conexión al servidor.
     */
    final static private int PUERTO = 2000;
    public static final String ROJO = "\u001B[31m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String RESET = "\u001B[0m";

    /**
     * Metodo principal que se conecta al servidor, autentica al usuario y permite interactuar con el servidor
     * enviando primitivas seleccionadas por el usuario.
     * @param args Los parámetros de la línea de comandos: el {@code host} del servidor.
     * @throws java.io.IOException Si ocurre un error de entrada/salida al interactuar con el servidor.
     */
    public static void main(String[] args) throws java.io.IOException {
        // Verifica que se haya pasado exactamente 1 parámetro (el host)
        if (args.length != 1) {
            System.out.println(ROJO + "Uso: java sdis.broker.client.Cliente host" + RESET);
            System.exit(-1);
        }

        String host = args[0];

        try (Socket sock = new Socket(host, PUERTO);
             ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {

            // Leer el primer mensaje del servidor (INFO o ERROR)
            MensajeProtocolo m = (MensajeProtocolo) ois.readObject();
            System.out.println(m);

            // Si el mensaje no es un error, proceder con la autenticación
            if (m.getPrimitiva() != Primitiva.ERROR) {
                BufferedReader tec = new BufferedReader(new InputStreamReader(System.in));
                System.out.print(MAGENTA + "Ingrese su usuario: " + RESET);
                String parametro1 = tec.readLine();
                System.out.print(MAGENTA + "Ingrese su clave: " + RESET);
                String parametro2 = tec.readLine();

                // Enviar mensaje de autenticación
                m = pruebaPeticionRespuesta(oos, ois, new MensajeProtocolo(Primitiva.XAUTH, parametro1, parametro2));

                // Si la autenticación fue exitosa, continuar con la selección de primitivas
                if (m.getPrimitiva() != Primitiva.NOTAUTH && m.getPrimitiva() != Primitiva.ERROR) {
                    while (true) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                        System.out.print("Introduce una primitiva (XAUTH, ADDMSG, STATE, DELETEQ, " +
                                "READQ, ECHO, PING, FLUSHQ, LISTQ, LISTQ_CONTENT): ");
                        String primitivaInput = br.readLine().toUpperCase();

                        Primitiva primitiva;
                        try {
                            primitiva = Primitiva.valueOf(primitivaInput); // Intentar convertir el string a una primitiva
                        } catch (IllegalArgumentException e) {
                            System.err.println(ROJO +"Primitiva no válida. Inténtalo de nuevo."+ RESET);
                            continue;
                        }

                        switch (primitiva) {
                            case XAUTH:
                                // Realizar autenticación nuevamente
                                System.out.print(MAGENTA + "Introduce usuario: " + RESET);
                                parametro1 = br.readLine();
                                System.out.print(MAGENTA + "Introduce contraseña: " + RESET);
                                parametro2 = br.readLine();
                                pruebaPeticionRespuesta(oos, ois, new MensajeProtocolo(Primitiva.XAUTH, parametro1, parametro2));
                                break;
                            case ADDMSG:
                                // Añadir un mensaje
                                System.out.print(MAGENTA + "Introduce clave: " + RESET);
                                parametro1 = br.readLine();
                                System.out.print(MAGENTA + "Introduce cadena: " + RESET);
                                parametro2 = br.readLine();
                                pruebaPeticionRespuesta(oos, ois, new MensajeProtocolo(Primitiva.ADDMSG, parametro1, parametro2));
                                break;
                            case STATE:
                                // Consultar el estado
                                pruebaPeticionRespuesta(oos, ois, new MensajeProtocolo(Primitiva.STATE));
                                break;
                            case DELETEQ:
                                // Eliminar una cola
                                System.out.print(MAGENTA + "Introduce clave: " + RESET);
                                parametro1 = br.readLine();
                                pruebaPeticionRespuesta(oos, ois, new MensajeProtocolo(Primitiva.DELETEQ, parametro1));
                                break;
                            case READQ:
                                // Leer una cola
                                System.out.print(MAGENTA + "Introduce clave: " + RESET);
                                parametro1 = br.readLine();
                                pruebaPeticionRespuesta(oos, ois, new MensajeProtocolo(Primitiva.READQ, parametro1));
                                break;
                            case PING:
                                // Enviar un mensaje de ping
                                pruebaPeticionRespuesta(oos, ois, new MensajeProtocolo(Primitiva.PING));
                                break;
                            case ECHO:
                                // Enviar un mensaje de eco
                                System.out.print(MAGENTA + "Introduce mensaje: " + RESET);
                                parametro1 = br.readLine();
                                pruebaPeticionRespuesta(oos, ois, new MensajeProtocolo(Primitiva.ECHO, parametro1));
                                break;
                            case FLUSHQ:
                                // Vaciar una cola
                                System.out.print(MAGENTA + "Introduce clave: " + RESET);
                                parametro1 = br.readLine();
                                pruebaPeticionRespuesta(oos, ois, new MensajeProtocolo(Primitiva.FLUSHQ, parametro1));
                                break;

                            case LISTQ_CONTENT:
                                // Listar los mensajes de todas las colas
                                pruebaPeticionRespuesta(oos, ois, new MensajeProtocolo(Primitiva.LISTQ_CONTENT));
                                break;
                            case LISTQ:
                                // Listar las colas
                                pruebaPeticionRespuesta(oos, ois, new MensajeProtocolo(Primitiva.LISTQ));
                                break;

                            default:
                                System.out.println(ROJO + "Primitiva no soportada." + RESET);
                        }
                    }
                }
            }

        } catch (java.io.EOFException e) {
            System.err.println(ROJO +"Cliente: Fin de conexión."+ RESET);
        } catch (java.io.IOException e) {
            System.err.println(ROJO +"Cliente: Error de apertura o E/S sobre objetos: "+e+ RESET);
        } catch (MalMensajeProtocoloException e) {
            System.err.println(ROJO +"Cliente: Error mensaje Protocolo: "+e+ RESET);
        } catch (Exception e) {
            System.err.println(ROJO +"Cliente: Excepción. Cerrando Sockets: "+e+ RESET);
        }
    }

    /**
     * Envia una petición al servidor y espera la respuesta.
     *
     * Este metodo escribe un mensaje en el flujo de salida y luego lee la respuesta del servidor,
     * imprimiendo tanto la petición como la respuesta en la consola.
     *
     * @param oos para enviar la petición al servidor.
     * @param ois para recibir la respuesta del servidor.
     * @param mp El mensaje que se enviará al servidor.
     *
     * @return El mensaje de respuesta recibido del servidor.
     * @throws java.io.IOException Si ocurre un error de entrada/salida.
     * @throws MalMensajeProtocoloException Si el mensaje recibido no es válido.
     * @throws ClassNotFoundException Si no se puede deserializar el mensaje.
     */
    private static MensajeProtocolo pruebaPeticionRespuesta(ObjectOutputStream oos, ObjectInputStream ois, MensajeProtocolo mp)
            throws java.io.IOException, MalMensajeProtocoloException, ClassNotFoundException {
        System.out.println("> " + mp);
        oos.writeObject(mp); // Enviar el mensaje
        mp = (MensajeProtocolo) ois.readObject(); // Leer la respuesta
        System.out.println("< " + mp);
        return mp;
    }
}

