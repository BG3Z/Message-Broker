package sdis.broker.server;

import sdis.broker.common.MensajeProtocolo;
import sdis.broker.common.Primitiva;
import sdis.utils.MultiMap;
import sdis.broker.common.MalMensajeProtocoloException;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import sdis.broker.common.Strings;

/**
 * Clase que representa un hilo que maneja una conexión de cliente.
 */
class Sirviente implements Runnable {
    public static final String ROJO = "\u001B[31m";
    public static final String RESET = "\u001B[0m";

    private final java.net.Socket socket;
    private final MultiMap<String, String> mapa;
    private final ConcurrentHashMap<String, String> credenciales;
    private final java.io.ObjectOutputStream oos;
    private final java.io.ObjectInputStream ois;
    private final sdis.broker.server.InformacionServidor informacion;
    private final BlacklistManager conexiones;
    private final BlacklistManager intentos;
    private final int ns;
    private static java.util.concurrent.atomic.AtomicInteger nInstance = new java.util.concurrent.atomic.AtomicInteger();
    private boolean logged, admin;

    /**
     * Constructor de la clase Sirviente.
     *
     * @param s El socket de la conexión con el cliente.
     * @param c El mapa de colas de mensajes.
     * @param credenciales El mapa de credenciales de los usuarios.
     * @param informacionServidor La información del servidor que incluye el número de hilos ocupados.
     * @param conexiones El gestor de bloqueos de conexiones.
     * @param intentos El gestor de bloqueos por intentos fallidos de autenticación.
     * @throws java.io.IOException Si ocurre un error al establecer los flujos de entrada/salida.
     */

    Sirviente(java.net.Socket s, MultiMap<String, String> c, ConcurrentHashMap<String, String> credenciales,
              sdis.broker.server.InformacionServidor informacionServidor,
              BlacklistManager conexiones, BlacklistManager intentos ) throws java.io.IOException {
        this.socket = s;
        this.mapa = c;
        this.credenciales = credenciales;
        this.ns = nInstance.getAndIncrement();
        this.oos = new java.io.ObjectOutputStream(socket.getOutputStream());
        this.ois = new java.io.ObjectInputStream(socket.getInputStream());
        this.informacion = informacionServidor;
        this.conexiones = conexiones;
        this.intentos = intentos;
        logged = admin = false;
    }
    /**
     * Metodo principal que maneja la ejecucion del hilo del sirviente.
     * Este metodo escucha continuamente las solicitudes del cliente, las procesa y responde
     * según la primitiva solicitada.
     */
    public void run() {
        try {
            //incrementar hilos usados
            informacion.incrementarHilosOcupados();
            MensajeProtocolo bienvenida = new MensajeProtocolo(Primitiva.INFO);
            oos.writeObject(bienvenida);

            // Procesar las solicitudes del cliente
            while (true) {

                MensajeProtocolo me = (MensajeProtocolo) ois.readObject();
                MensajeProtocolo ms;
                switch (me.getPrimitiva()) {
                    case XAUTH:
                        ms = ejecutar_XAUTH(me);
                        break;

                    case ADDMSG:
                        ms = ejecutar_ADDMSG(me);
                        break;

                    case STATE:
                        ms = ejecutar_STATE(me);
                        break;

                    case DELETEQ:
                        ms = ejecutar_DELETEQ(me);
                        break;

                    case READQ:
                        ms = ejecutar_READQ(me);
                        break;

                    case PING:
                        ms = new MensajeProtocolo(Primitiva.PONG);
                        break;

                    case ECHO:
                        ms =  new MensajeProtocolo(Primitiva.ECHO_REPLY, me.getParametro1());
                        break;

                    case FLUSHQ:
                        ms = ejecutar_FLUSHQ(me);
                        break;

                    case LISTQ:
                        ms = ejecutar_LISTQ(me);
                        break;

                    case LISTQ_CONTENT:
                        ms = ejecutar_LISTQ_CONTENT(me);
                        break;
                    default:
                        ms = new MensajeProtocolo(Primitiva.BADCODE, Strings.primitivaNoReconocida());
                        break;
                }
                oos.writeObject(ms);
            }
        } catch (java.io.IOException e) {
            System.err.println(ROJO + "Sirviente: "+ns+": [FIN]" + RESET);
        } catch (ClassNotFoundException ex) {
            System.err.println(ROJO + "Sirviente: "+ns+": [ERR Class not found]" + RESET);
        } catch (MalMensajeProtocoloException ex) {
            System.err.println(ROJO + "Sirviente: "+ns+": [ERR MalMensajeProtocolo]" + RESET);
        } finally {
            try {
                informacion.decrementarHilosOcupados();
                conexiones.decrementarContador(socket.getInetAddress().getHostAddress());
                ois.close();
                oos.close();
                socket.close();
            } catch (Exception e) {
                System.err.println(ROJO + "Sirviente: " + ns + ": [ERROR] Cerrando sockets" + RESET);
            }
        }
    }

    /**
     * Metodo para validar las credenciales del usuario.
     *
     * @param usuario El nombre de usuario a verificar.
     * @param password La contraseña del usuario.
     * @return {@code true} si las credenciales son válidas, {@code false} en caso contrario.
     */
    private boolean validarCredenciales(String usuario, String password) {
        return credenciales.containsKey(usuario) && credenciales.get(usuario).equals(password);
    }

    /**
     * Metodo que maneja la primitiva Xauth
     *
     * Este metodo valida las credenciales del usuario (nombre de usuario y contraseña).
     * Si las credenciales son correctas, el usuario se autentica, si la autenticación falla, se registra el intento fallido
     * y, si se superan los intentos fallidos, la IP es bloqueada.
     *
     * @param me El mensaje que contiene las credenciales del usuario.
     * @return El mensaje de respuesta que indica si la autenticación fue exitosa o no.
     * @throws MalMensajeProtocoloException Si el mensaje no es válido o tiene un formato incorrecto.
     */

    private MensajeProtocolo ejecutar_XAUTH(MensajeProtocolo me) throws MalMensajeProtocoloException {
        String usuario = me.getParametro1();
        String password = me.getParametro2();
        if (validarCredenciales(usuario, password)) {
            logged = true;
            admin = usuario.equals("admin"); // Asumiendo que "admin" tiene privilegios especiales
            return new MensajeProtocolo(Primitiva.XAUTH, Strings.logueado());
        } else {
            logged = false;
            intentos.evento(socket.getInetAddress().getHostAddress());
            System.out.println(ROJO + "Sirviente: "+ns+": Invalid credentials from " +
                    socket.getInetAddress().getHostAddress());

            if(intentos.estaBloqueado(socket.getInetAddress().getHostAddress())) {
                return new MensajeProtocolo(Primitiva.ERROR, Strings.maxNumFallosAccesos());
            }

            return new MensajeProtocolo(Primitiva.NOTAUTH, Strings.fallologin());

        }

    }
    /**
     * Metodo que maneja la primitiva AddMsg
     *
     * Este metodo permite agregar un mensaje a la cola asociada a una clave. Si el usuario no está
     * autenticado, se devuelve un mensaje de error. Si el usuario es admin puede agregar una nueva cola,
     * si no es admin unicamente puede agregar mensajes a una cola ya existente.
     *
     * @param me El mensaje que contiene la clave y el valor a agregar a la cola.
     * @return El mensaje de respuesta que indica si la operación fue exitosa o si hubo un error.
     * @throws MalMensajeProtocoloException Si el mensaje no es válido o tiene un formato incorrecto.
     */
    private MensajeProtocolo ejecutar_ADDMSG(MensajeProtocolo me) throws MalMensajeProtocoloException {
        if (!logged) {
            return new MensajeProtocolo(Primitiva.NOTAUTH, Strings.noLogueado());
        }
        String key = me.getParametro1();
        String val = me.getParametro2();
        MensajeProtocolo ms;
        if (!mapa.existe(key)) {
            if (admin) {
                mapa.push(key, val);
                ms = new MensajeProtocolo(Primitiva.ADDED);
                informacion.incrementarMensajesPushed();
            } else {
                ms = new MensajeProtocolo(Primitiva.NOTAUTH, Strings.noAdmin());
            }
        } else {
            mapa.push(key, val);
            ms = new MensajeProtocolo(Primitiva.ADDED);
            informacion.incrementarMensajesPushed();
        }
        return ms;
    }
    /**
     * Metodo que maneja la primitiva State
     * .
     * Este metodo solo puede ser ejecutado por usuarios autenticados y como admin.
     * Devuelve información sobre el estado actual del servidor.
     *
     * @param me El mensaje de la solicitud, que no requiere parámetros adicionales.
     * @return El mensaje de respuesta con el estado del servidor o un error si el usuario no tiene privilegios.
     * @throws MalMensajeProtocoloException Si el mensaje no es válido o tiene un formato incorrecto.
     */
    private MensajeProtocolo ejecutar_STATE(MensajeProtocolo me) throws MalMensajeProtocoloException {
        if (!logged) {
            return new MensajeProtocolo(Primitiva.NOTAUTH, Strings.noLogueado());
        }
        if (!admin) {
            return new MensajeProtocolo(Primitiva.NOTAUTH, Strings.noAdmin());
        }
        String estadoServidor = informacion.getInformacionServidor();
        return new MensajeProtocolo(Primitiva.STATE, estadoServidor);
    }


    /**
     * Metodo que maneja la primitiva DeleteQ.
     *
     * Este metodo solo puede ser ejecutado por usuarios autenticados como admin.
     * Elimina la cola asociada a una clave si existe.
     *
     * @param me El mensaje que contiene la clave de la cola que se desea eliminar.
     * @return El mensaje de respuesta que indica si la operación fue exitosa o si la cola estaba vacía.
     * @throws MalMensajeProtocoloException Si el mensaje no es válido o tiene un formato incorrecto.
     */
    private MensajeProtocolo ejecutar_DELETEQ(MensajeProtocolo me) throws MalMensajeProtocoloException{
        if (!logged) {
            return new MensajeProtocolo(Primitiva.NOTAUTH, Strings.noLogueado());
        }
        if (!admin) {
            return new MensajeProtocolo(Primitiva.NOTAUTH, Strings.noAdmin());

        }

        String queueKey = me.getParametro1();
        MensajeProtocolo ms;
        if (mapa.existe(queueKey)) {
            mapa.delete(queueKey);
            ms = new MensajeProtocolo(Primitiva.DELETED);
        } else {
            ms = new MensajeProtocolo(Primitiva.EMPTY);
        }
        return ms;
    }

    /**
     * Metodo que maneja la primitiva ReadQ.
     *
     * Este metodo solo puede ser ejecutado por usuarios autenticados. Si la cola asociada a la clave
     * está vacía, se devuelve un mensaje de error.
     *
     * @param me El mensaje que contiene la clave de la cola de la que se desea leer el siguiente mensaje.
     * @return El mensaje de respuesta con el valor extraído de la cola, o un error si la cola está vacía.
     * @throws MalMensajeProtocoloException Si el mensaje no es válido o tiene un formato incorrecto.
     */
    private MensajeProtocolo ejecutar_READQ(MensajeProtocolo me) throws MalMensajeProtocoloException {
        if (!logged) {
            return new MensajeProtocolo(Primitiva.NOTAUTH, Strings.noLogueado());
        }
        MensajeProtocolo ms;
        String sgteElemQueue = me.getParametro1();
        if (mapa.existe(sgteElemQueue)) {
            String elem = mapa.pop(sgteElemQueue);
            if (elem == null){
                ms = new MensajeProtocolo(Primitiva.EMPTY);
            }else{
                ms = new MensajeProtocolo(Primitiva.MSG, elem);
                informacion.incrementarMensajesPulled();
            }
        } else {
            ms = new MensajeProtocolo(Primitiva.EMPTY);
        }

        return ms;
    }

    /**
     * Metodo que maneja la primitiva FlushQ
     *
     * Este metodo solo puede ser ejecutado por usuarios autenticados como admin.
     * Este metodo vacia una cola de mensajes asociada a una clave
     *
     * @param me El mensaje que contiene la clave de la cola de la que se desea eliminar
     * @return El mensaje de respuesta con la cola vacia, o un error si la cola está vacía.
     * @throws MalMensajeProtocoloException Si el mensaje no es válido o tiene un formato incorrecto.
     */
    private MensajeProtocolo ejecutar_FLUSHQ(MensajeProtocolo me) throws MalMensajeProtocoloException{
        if (!logged) {
            return new MensajeProtocolo(Primitiva.NOTAUTH, Strings.noLogueado());
        }
        if (!admin) {
            return new MensajeProtocolo(Primitiva.NOTAUTH, Strings.noAdmin());

        }

        String queueKey = me.getParametro1();
        MensajeProtocolo ms;
        if (mapa.existe(queueKey)) {
            mapa.flush(queueKey);
            ms = new MensajeProtocolo(Primitiva.FLUSHED);
        } else {
            ms = new MensajeProtocolo(Primitiva.EMPTY);
        }
        return ms;
    }

    /**
     * Metodo que maneja la primitiva ListQ
     *
     * Este metodo lista las claves que se encuentran en un instante en el broker de mensajeria
     *
     * @param me El mensaje de la solicitud, que no requiere parámetros adicionales.
     * @return El mensaje de respuesta con las claves
     * @throws MalMensajeProtocoloException Si el mensaje no es válido o tiene un formato incorrecto.
     */
    private MensajeProtocolo ejecutar_LISTQ (MensajeProtocolo me) throws MalMensajeProtocoloException{
        String mensaje = "";
        int num_keys = mapa.size();
        if (num_keys == 0) {
            return new MensajeProtocolo(Primitiva.EMPTY);
        }
        int contador = 0;
        for (String key : mapa.keySet()){
                mensaje += key;
                contador++;
                if(contador < num_keys){
                    mensaje+=" : ";
                }
        }
        return new MensajeProtocolo(Primitiva.MSG, mensaje);
    }

    /**
     * Metodo que maneja la primitiva ListQ_Content
     *
     * Este metodo lista las claves de las colas y sus mensajes asociados que se encuentran
     * en un instante en el broker de mensajeria
     *
     * @param me El mensaje de la solicitud, que no requiere parámetros adicionales.
     * @return El mensaje de respuesta con las claves y su mensajes asociados a esa cola
     * @throws MalMensajeProtocoloException Si el mensaje no es válido o tiene un formato incorrecto.
     */
    private MensajeProtocolo ejecutar_LISTQ_CONTENT(MensajeProtocolo me) throws MalMensajeProtocoloException{
        String mensaje = "";
        int num_keys = mapa.size();
        if (num_keys == 0) {
            return new MensajeProtocolo(Primitiva.EMPTY);
        }
        int contador = 0;
        for (String key : mapa.keySet()){
            mensaje += key + ":";
            for (String elem : mapa.getQueue(key)){
                        mensaje += elem + ",";
            }

            contador++;
            if(contador < num_keys) {
                mensaje += "\n";
            }

        }
        return new MensajeProtocolo(Primitiva.MSG, mensaje);

    }
}
