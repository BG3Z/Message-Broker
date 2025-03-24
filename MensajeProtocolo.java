package sdis.broker.common;

import java.io.Serializable;
import java.sql.SQLOutput;

public class MensajeProtocolo implements Serializable {
    private final Primitiva primitiva;
    private final String parametro1;
    private final String parametro2;

    // Reset para volver al color normal
    public static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";

    /**
     * Metodo para crear un mensaje protocolo para las primitivas INFO,
     * ADDED, EMPTY, STATE, DELETED, STATE, PING,
     * PONG, FLUSHED, LISTQ o LISTQ_CONTENT
     *
     * @param p la primitiva a enviar
     * @throws MalMensajeProtocoloException si p no es ninguna de las primitivas
     *                                      admitidas
     */
    public MensajeProtocolo(Primitiva p)
            throws MalMensajeProtocoloException {
        if (p == Primitiva.INFO || p == Primitiva.ADDED || p == Primitiva.STATE ||
                p == Primitiva.EMPTY || p == Primitiva.DELETED ||
                p == Primitiva.STATE || p == Primitiva.PING || p == Primitiva.PONG ||
                p == Primitiva.FLUSHED || p == Primitiva.LISTQ || p == Primitiva.LISTQ_CONTENT) {
            this.primitiva = p;
            this.parametro1 = this.parametro2 = null;
        } else
            throw new MalMensajeProtocoloException();
    }

    /**
     * Metodo para crear un mensaje protocolo para las primitivas XAUTH, READQ, MSG,
     * STATE, DELETEQ, NOTAUTH, ERROR, BADCODE, ECHO, ECHO_REPLY o FLUSH_Q
     *
     * @param p       la primitiva a enviar
     * @param mensaje el mensaje para las prinitivas XAUTH, STATE, NOTAUTH, ERROR, BADGATE,
     *                ECHO, y ECHO_REPLY
     *                el valor para la primitiva MSG
     *                la key para las primitivas READQ y DELETEQ, FLUSHQ
     * @throws MalMensajeProtocoloException si p no es ninguna de las primitivas
     *                                      admitidas
     */
    public MensajeProtocolo(Primitiva p, String mensaje)
            throws MalMensajeProtocoloException {
        if (p == Primitiva.XAUTH || p == Primitiva.READQ || p == Primitiva.MSG ||
                p == Primitiva.STATE || p == Primitiva.DELETEQ || p == Primitiva.NOTAUTH ||
                p == Primitiva.ERROR || p == Primitiva.BADCODE || p == Primitiva.ECHO ||
                p == Primitiva.ECHO_REPLY || p == Primitiva.FLUSHQ) {
            this.primitiva = p;
            this.parametro1 = mensaje;
            this.parametro2 = null;
        } else
            throw new MalMensajeProtocoloException();
    }


    /**
     * Metodo para crear un mensaje protocolo para las primitivas ADDMSG o XAUTH
     *
     * @param p            la primitiva a enviar
     * @param claveUsuario el usuario para la primitiva XAUTH o la clave de la cola
     *                     para la primitiva ADDMSG
     * @param valor        la contraseña para la primitiva XAUTH o el valor a añadir
     *                     para la primitiva ADDMSG
     * @throws MalMensajeProtocoloException si p no es ninguna de las primitivas
     *                                      admitidas
     */
    public MensajeProtocolo(Primitiva p, String claveUsuario, String valor)
            throws MalMensajeProtocoloException {
        if (p == Primitiva.ADDMSG || p == Primitiva.XAUTH) {
            this.primitiva = p;
            this.parametro1 = claveUsuario;
            this.parametro2 = valor;
        } else
            throw new MalMensajeProtocoloException();
    }


    public Primitiva getPrimitiva() {
        return this.primitiva;
    }

    public String getParametro1() {
        return this.parametro1;
    }

    public String getParametro2() {
        return this.parametro2;
    }

    @Override
    public String toString() { /* prettyPrinter de la clase */
        switch (this.primitiva) {
            case INFO:
                return AZUL + Strings.getBienvenida() + RESET;
            case ADDED:
                return VERDE +"ADDED" + RESET;
            case EMPTY:
                return AMARILLO + "EMPTY" + RESET;
            case STATE:
                if (this.parametro1 == null)
                    return AZUL + "STATE de cliente a servidor" + RESET;
                return VERDE + "STATE de servidor a cliente con mensaje: " + this.parametro1 + "." + RESET;
            case DELETED:
                return VERDE + "DELETED" + RESET;
            case FLUSHED:
                return VERDE + "FLUSHED" + RESET;
            case XAUTH:
                if(this.parametro2 == null)
                    return VERDE + this.parametro1 + RESET;
                return AZUL + "XAUTH con usuario: " + this.parametro1 + " password: " + this.parametro2 + "." + RESET;
            case ADDMSG:
                return AZUL + "ADDMSG con key: " + this.parametro1 + " y valor: " + this.parametro2 + "." + RESET;
            case READQ:
                return AZUL + "READQ con key: " + this.parametro1 + "." + RESET;
            case LISTQ:
                return AZUL + "LISTQ" + RESET;
            case LISTQ_CONTENT:
                return AZUL + "LISTQ_CONTENT" + RESET;
            case MSG:
                return VERDE + "MSG con mensaje: " + this.parametro1 + "." + RESET;
            case DELETEQ:
                return AZUL + "DELETEQ con key: " + this.parametro1 + "." + RESET;
            case FLUSHQ:
                return AZUL + "FLUSHQ con key: " + this.parametro1 + "." + RESET;
            case PING:
                return AZUL + "PING" + RESET;
            case PONG:
                return VERDE + "PONG" + RESET;
            case ECHO:
                return AZUL + "ECHO: " + this.parametro1 + "." + RESET;
            case ECHO_REPLY:
                return VERDE + "ECHO_REPLY: " + this.parametro1 + "." + RESET;
            case NOTAUTH:
                return ROJO + "NOTAUTH: " + this.parametro1 + "." + RESET;
            case ERROR:
                return ROJO + "ERROR: " + this.parametro1 + "."+RESET;
            case BADCODE:
                return ROJO +"BADCODE: " + this.parametro1 + "."+RESET;
            default:
                return ROJO + "NOTHING." + RESET;
        }

    }
}
