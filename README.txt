COMPILACION Y EJECUCION:

Compilacion:
javac -d. -cp ".;gson-2.12.1.jar" *.java 

Ejecucion de Servidor:
java -cp ".;gson-2.12.1.jar" sdis.broker.server.Servidor

COMPROBACION DE PRIMITIVAS
java sdis.broker.client.unit.Auth host usuario contraseña
java sdis.broker.client.unit.AddMsg host clave cadena
java sdis.broker.client.unit.ReadQ host clave
java sdis.broker.client.unit.State host
java sdis.broker.client.unit.DeleteQ host clave
java sdis.broker.client.unit.FlushQ host clave
java sdis.broker.client.unit.Ping host
java sdis.broker.client.unit.Echo host palabra
java sdis.broker.client.unit.ListQ host
java sdis.broker.client.unit.ListQ_Content host

java sdis.broker.client.unit.AuthAddMsg host usuario contraseña clave mensaje
java sdis.broker.client.unit.AuthDeleteQ host usuario contraseña clave
java sdis.broker.client.unit.AuthReadQ usuario password clave
java sdis.broker.client.unit.AuthState host usuario contraseña
java sdis.broker.client.unit.AuthFlushQ host usuario password clave

Ejecución de cliente
java sdis.broker.client.Cliente host
