package sdis.broker.server;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import java.sql.SQLOutput;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase principal que representa el servidor del broker de mensajería.
 */
public class Servidor {

    /**
     * Metodo principal que inicia el servidor y configura la escucha de conexiones.
     * El servidor se inicia en el puerto especificado y maneja múltiples hilos utilizando un pool de hilos.
     * Además, gestiona la autenticación de usuarios y limita el número de conexiones simultáneas y el número
     * de intentos fallidos de inicio de sesión por dirección IP.
     */
    public static void main(String args[]) {
        int PUERTO = 2000; // Puerto en el que el servidor escuchará las conexiones
        int NThreads = 5; // Número de hilos máximo que el servidor puede manejar

        //Creamos las instancias de la clase BlacklistManager para el numero de conexiones e intentos
        BlacklistManager conexiones = new BlacklistManager(3);
        BlacklistManager intentos = new BlacklistManager(3);

        //Creamos la instancia de la clase InformacionServidor que proporciona la informacion actual del servidor
        InformacionServidor informacion = new InformacionServidor(NThreads);

        // Mapa que almacena los mensajes relacionados con las colas
        sdis.utils.MultiMap<String, String> mapa = new sdis.utils.MultiMap<>();

        // Cargar las credenciales de los usuarios desde un archivo JSON
        ConcurrentHashMap<String, String> credenciales = cargarJson("Usuarios");

        // Crear un pool de hilos para manejar las conexiones de clientes
        java.util.concurrent.ExecutorService exec = java.util.concurrent.Executors.newFixedThreadPool(NThreads);

        try {
            // Crear el servidor que escucha en el puerto especificado
            java.net.ServerSocket sock = new java.net.ServerSocket(PUERTO);
            System.err.println("\u001B[31mServidor: WHILE [INICIANDO]\u001B[0m");

            // Hilo principal que acepta conexiones y maneja el proceso de autenticación y bloqueo
            Thread mainServer = new Thread(() -> {
                try {
                    while (true) {
                        // Aceptar una conexión entrante
                        java.net.Socket socket = sock.accept();
                        String ip = socket.getInetAddress().getHostAddress();

                        // Verificar si la IP está bloqueada por superar el límite de conexiones o intentos fallidos
                        if(conexiones.estaBloqueado(ip)){
                            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(socket.getOutputStream());
                            oos.writeObject(new sdis.broker.common.MensajeProtocolo(sdis.broker.common.Primitiva.ERROR, sdis.broker.common.Strings.maxNumConexiones()));
                        } else if(intentos.estaBloqueado(ip)){
                            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(socket.getOutputStream());
                            oos.writeObject(new sdis.broker.common.MensajeProtocolo(sdis.broker.common.Primitiva.ERROR, sdis.broker.common.Strings.maxNumFallosAccesos()));
                        } else {
                            try {
                                // Registrar la conexión entrante
                                conexiones.evento(ip);
                                System.out.println("\u001B[35mServidor: Numero de conexiones: " + conexiones.getConexiones(ip) + "\u001B[0m");

                                // Crear un nuevo hilo para manejar la conexión del cliente
                                sdis.broker.server.Sirviente serv = new sdis.broker.server.Sirviente(socket, mapa, credenciales, informacion, conexiones, intentos);
                                exec.execute(serv); // Ejecutar el hilo del trabajador (Sirviente)
                            } catch (java.io.IOException ioe) {
                                System.err.println("\u001B[31mServidor: WHILE [ERR ObjectStreams]\u001B[0m");
                            }
                        }
                    }
                } catch (java.io.IOException ioe) {
                    System.err.println("\u001B[31mServidor: WHILE [Error.E/S]\u001B[0m");
                } catch (Exception e) {
                    System.err.println("\u001B[31mServidor: WHILE [Error.execute]\u001B[0m");
                }
            }, "RUN(WHILE)");

            mainServer.start(); // Iniciar el hilo principal del servidor
            System.out.println("\u001B[32mServidor: [CORRIENDO]\u001B[0m");
        } catch (java.io.IOException ioe) {
            System.err.println("\u001B[31mServidor: [ERR SSOCKET]\u001B[0m");
        }
    }

    /**
     * Funcion que lee un archivo JSON y la almacena en un Hashmap
     * @param archivo JSON
     * @return mapa
     */
    private static ConcurrentHashMap<String, String> cargarJson(String archivo){
        ConcurrentHashMap<String, String> mapa = new ConcurrentHashMap<>();
        try {
            java.io.InputStreamReader input_archivo2 =
                    new java.io.InputStreamReader(new java.io.FileInputStream(archivo));
            mapa = new Gson().fromJson(
                    input_archivo2, new TypeToken<ConcurrentHashMap<String, String>>() {}.getType() );

            input_archivo2.close();
        } catch (java.io.IOException ioe) { }
        return mapa;
    }
}
