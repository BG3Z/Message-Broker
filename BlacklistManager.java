package sdis.broker.server;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Clase usado para limitar el numero de conexiones e intentos del servidor.
 *
 */

public class BlacklistManager {

    // Mapa de conexiones, almacenando la IP y el número de conexiones actuales
    private final ConcurrentHashMap<String, Integer> conexiones = new ConcurrentHashMap<>();

    // Número máximo de conexiones permitidas
    private final int conexionesMaximas;

    /**
     * Metodo para recibir las conexiones maximas.
     * @param conexionesMaximas Numero de conexiones maximas.
     */
    public BlacklistManager(int conexionesMaximas){
        this.conexionesMaximas = conexionesMaximas;
    }

    /**
     * Registra un evento de conexión para una IP, incrementando el contador de conexiones de esa IP.
     * Si la IP no existe, se inicializa con 1.
     * @param ip Ip del usuario.
     */
    public void evento(String ip) {
        conexiones.merge(ip, 1, Integer::sum);
    }

    /**
     * Si se ha superado el numero limite se bloquea la conexion del cliente.
     * @param ip Ip del usuario.
     * @return Nos devuelve true o false.
     */
    public boolean estaBloqueado(String ip){
        return (conexiones.getOrDefault(ip, 0) >= conexionesMaximas);
    }

    /**
     * Si un cliente se desconecta, decrementamos los logins.
     * Si el contador de conexiones llega a 1, se elimina la IP del mapa de conexiones.
     * @param ip Ip del usuario que se desconecta.
     */
    public void decrementarContador(String ip){
        conexiones.computeIfPresent(ip, (key, val) -> (val > 1) ? val - 1 : 0);
    }

    /**
     * Recibir el numero de conexiones
     * Si la IP no tiene conexiones registradas, se devuelve 0
     * @param ip Ip del usuario.
     * @return Returnea el numero de conexiones.
     */
    public int getConexiones(String ip){
        return conexiones.getOrDefault(ip, 0);
    }
}
