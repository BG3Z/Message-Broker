package sdis.utils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

/**
 * Clase que implementa un mapa concurrente donde cada clave está asociada a una cola (queue) de valores.
 *
 * @param <K> Tipo de la clave asociada a cada cola.
 * @param <T> Tipo de los valores almacenados en la cola asociada a cada clave.
 */

public class MultiMap<K,T> {
    private final ConcurrentMap<K, ConcurrentLinkedQueue<T>> map =
            new ConcurrentHashMap<K,ConcurrentLinkedQueue<T>>();

    /**
     * Agrega un valor a la cola asociada a una clave. Si la cola no existe, la crea.
     *
     * @param clave La clave a la que se asociará el valor.
     * @param valor El valor que se agregará a la cola.
     */
    public void push(K clave, T valor){
        Queue<T> cola = map.get(clave);

        if(cola == null){
            ConcurrentLinkedQueue<T> nueva = new ConcurrentLinkedQueue<T>();
            ConcurrentLinkedQueue<T> previa = map.putIfAbsent(clave, nueva);
            cola = (previa == null) ? nueva : previa;
        }
        cola.add(valor);
    }

    /**
     * Metodo que te devuelve el ultimo elemento de la cola BORRANDO ESE ULTIMO
     * @param clave
     * @return
     */
    public T pop(K clave){
        ConcurrentLinkedQueue<T> cola = map.get(clave);
        return (cola != null) ? cola.poll() : null ;
    }

    /**
     * Metodo que elimina una cola
     * @param clave
     */
     public void delete(K clave) {map.remove(clave);}


    /**
     * Metodo que elimina todos los elementos de una cola pero no la cola
     * @param clave
     */
    public void flush(K clave){
        ConcurrentLinkedQueue<T> cola = map.get(clave);
        if(cola != null){
            cola.clear();
        }
    }

    /**
     * Metodo que te devuelve el ultimo elemento de la cola SIN BORRARLO
     * @param clave
     * @return
     */
    public T read(K clave){
        ConcurrentLinkedQueue<T> cola = map.get(clave);
        return (cola != null) ? cola.peek() : null ;
    }

    /**
     * Verifica si una clave existe en el mapa.
     * @param clave La clave que se desea verificar.
     * @return {@code true} si la clave existe en el mapa, {@code false} en caso contrario.
     */
    public boolean existe(K clave){
        return map.containsKey(clave);
    }

    /**
     * Metodo que devuelve un conjunto con todas las claves del mapa
     * @return conjunto con las claves
     */
    public Set<K> keySet(){
        return map.keySet();
    }

    /**
     * Metodo que devuelve la cola asociada a una clave.
     * @param clave La clave de la cola que se desea obtener.
     * @return La cola asociada a la clave, o {@code null} si la clave no existe.
     */
    public Queue<T> getQueue(K clave) {
        return map.get(clave);
    }

    /**
     * Metodo que devuelve el numero de claves del mapa
     * @return el numero de claves
     */
    public int size(){ return map.size();}

}
