package sdis.broker.server;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase que almacena y gestiona la información del servidor relacionado con el número de hilos y mensajes.
 */
public class InformacionServidor {

    // Variables atómicas para contar hilos y mensajes
    private AtomicInteger nHilosMaximos;
    private AtomicInteger nHilosOcupados;
    private AtomicInteger nMensajesPushed;
    private AtomicInteger nMensajesPulled;

    /**
     * Constructor que inicializa los contadores con los valores proporcionados.
     *
     * @param nHilosMaximos El número máximo de hilos que puede tener el servidor.
     */
    public InformacionServidor(int nHilosMaximos){
        this.nHilosMaximos = new AtomicInteger(nHilosMaximos);
        this.nHilosOcupados = new AtomicInteger(0);
        this.nMensajesPushed = new AtomicInteger(0);
        this.nMensajesPulled = new AtomicInteger(0);
    }

    /**
     * Devuelve la información completa del servidor en forma de un string.
     *
     * @return Una cadena de texto que contiene las métricas del servidor: hilos máximos, hilos ocupados, mensajes empujados y mensajes extraídos.
     */
    public String getInformacionServidor(){
        return getHilosMaximos() + " : " + getHilosOcupados() + " : " + getMensajesPushed() + " : " + getMensajesPulled();
    }

    /**
     * Incrementa el número de hilos ocupados por uno.
     */
    public void incrementarHilosOcupados(){
        nHilosOcupados.incrementAndGet();
    }

    /**
     * Incrementa el número de mensajes empujados al servidor por uno.
     */
    public void incrementarMensajesPushed(){
        nMensajesPushed.incrementAndGet();
    }

    /**
     * Incrementa el número de mensajes extraídos del servidor por uno.
     */
    public void incrementarMensajesPulled(){
        nMensajesPulled.incrementAndGet();
    }

    /**
     * Obtiene el número máximo de hilos configurados para el servidor.
     *
     * @return El número máximo de hilos.
     */
    public int getHilosMaximos(){
        return nHilosMaximos.get();
    }

    /**
     * Obtiene el número de hilos actualmente ocupados por el servidor.
     *
     * @return El número de hilos ocupados.
     */
    public int getHilosOcupados(){
        return nHilosOcupados.get();
    }

    /**
     * Obtiene el número de mensajes empujados al servidor.
     *
     * @return El número de mensajes empujados.
     */
    public int getMensajesPushed(){
        return nMensajesPushed.get();
    }

    /**
     * Obtiene el número de mensajes extraídos del servidor.
     *
     * @return El número de mensajes extraídos.
     */
    public int getMensajesPulled(){
        return nMensajesPulled.get();
    }

    /**
     * Decrementa el número de hilos ocupados por uno.
     */
    public void decrementarHilosOcupados(){
        nHilosOcupados.decrementAndGet();
    }
}

