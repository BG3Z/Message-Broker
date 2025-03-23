package sdis.broker.common;

public enum Primitiva {
    INFO,       /*Mensaje de bienvenida del servidor*/
    XAUTH,      /*Usuario- password*/
    ADDMSG,     /* Añadir mensaje a la cola*/
    ADDED,      /*Servidor confirma el mensaje añadido*/
    READQ,      /*El cliente quiere consumir el siguiente elemento de la cola key*/
    MSG,        /*El servidor devuelve el elemento de la cola*/
    EMPTY,      /* El servidor indica que no es posible entregar ningun valor*/
    STATE,      /*Obtener el estado del servidor, solo para ADMIN*/
    DELETEQ,    /*Borrar una cola, solo para ADMIN*/
    DELETED,    /*Cola borrada con exito, solo para ADMIN*/
    FLUSHQ,     /*Borrar todos los mensajes de una cola, solo para ADMIN*/
    FLUSHED,    /*Cola vaciada con exito, solo para ADMIN*/
    PING,       /*Ping del cliente al servidor*/
    PONG,       /*Pong del servidor al cliente*/
    ECHO,       /*Echo del cliente al servidor*/
    ECHO_REPLY, /*Echo del servidor al cliente*/
    LISTQ,      /*Listar las colas del servidor*/
    LISTQ_CONTENT, /*Listar los mensajes de todas las cola*/
    NOTAUTH,    /*Usuario no autorizado / no logueado / sin permisos */
    ERROR,      /*Error: max conexiones o max num de intentos*/
    BADCODE;    /*Mala primitiva*/
}
