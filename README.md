# Práctica 1: Sincronización condicional y exclusión mutua

 El objetivo de esta práctica es utilizar los conocimientos adquiridos en la asignatura para construir un gestor de descargas de páginas web multiproceso. El programa recibirá como entrada un fichero con las páginas web que deseamos descargar y la salida será un nuevo directorio con todas las páginas web descargadas.

 Al ejecutar el programa se mostrará una interfaz de usuario textual donde se solicitará el nombre del fichero que contiene las páginas web que queremos descargar. Si no se puede abrir  el fichero se deberá notificar mediante un mensaje y solicitar un nuevo fichero. A continuación  se  solicitará  el  directorio  de  salida  donde  se  almacenarán  las  descargas realizadas (si ese directorio no existe, deberá crearlo el programa).

<table align="center">
 <tr align="center">
  <td>path/to/file/webpages.txt > Introduce el directorio donde quieres guardar las descargas path/to/dir</td>
 </tr>
</table>

 Una vez elegido el fichero de entrada y el directorio de salida, la aplicación deberá comenzar con la descarga de las páginas web contenidas en el fichero.

- Para descargar las páginas web vamos a utilizar la librería Jsoup. Se trata de una librería Java que permite establecer conexiones HTTP para analizar el documento HTML contenido en la página. El siguiente código indica cómo establecer una conexión:

```// Creates a connection to a given url 
Connection conn = Jsoup.connect(url); 
try {      
	// Performs the connection and retrieves the response Response 
    resp = conn.execute(); // If the response is different from 200 OK, 
    						// the website is not reachable 
    if (resp.statusCode() != 200) { 
    	System.out.println("Todo bien"); 
    } 
    else {         
    	System.out.println("Error: "+resp.statusCode());
    } 
    
} catch (IOException e) { 
	System.out.println("No se puede conectar"); 
}```


 Una vez nos hemos asegurado de que la conexión es correcta y la web es alcanzable, podemos utilizar Jsoup para descargar el contenido html asociado:

- String html = conn.get().html();

 Para guardar la web en un fichero, bastará con escribir el html recibido en un fichero.

- La clase WebProcessor debe encargarse de toda la lógica del programa. Para ello, creará y destruirá los threads que consideres oportunos. El constructor de la clase tendrá la siguiente cabecera:

```public WebProcessor(String path, int nDown, int maxDown);```

donde  path  será  la  ruta  hacia  el  directorio  donde  se  almacenarán  las  páginas  web descargadas, nDown será  el  número  de  procesos  de  descarga  que  lanzará  la  aplicación,  y maxDown ndicará  el  máximo  número  de  procesos  que  pueden  descargar  webs simultáneamente.

Además, la clase deberá presentar un método con la siguiente cabecera:

```public void process(String fileName);```

que recibirá como parámetro el nombre del fichero que queremos procesar. Dicho fichero contendrá una URL completa en cada línea. El método process abrirá el fichero de entrada de  manera  que  cada  proceso  de  descarga  irá  leyendo  líneas  del  mismo  para  conocer  la siguiente web a descargar.

 Para proporcionar una mayor usabilidad, se desea informar al usuario cada 3 segundos sobre el número de ficheros descargados hasta el momento. Además, el usuario debe ser capaz de detener las descargas y salir de la aplicación con solo pulsar una tecla. En este caso, se deben finalizar  las  descargas  en  proceso,  dejando  todos  los  ficheros  descargados  en  un estado consistente.

 La aplicación debe cumplir los siguientes requisitos:

- Siempre deben estar descargando páginas web el máximo número de procesos de descarga permitidos.

- Todos los procesos que quieran descargar un fichero deberán poder hacerlo en algún momento.

- Cada  vez  que  se  deba  informar  del  progreso  de  la  descarga,  se  deberá  hacer minimizando los retrasos.

- Si se produce un error asociado a una URL (no es una URL correcta, no es posible conectarse a esa web, etc.), se continuará con la siguiente web, sin crear el fichero asociado.  La  web  que  no  se  ha  podido  descargar  se  escribirá  en  un  fichero error_log.txt.

- Si se llega al final del fichero, los procesos de descarga deben finalizar.

- El nombre de cada fichero descargado se corresponderá con el nombre de la web, eliminando http://www. y el dominio (.com, .es, etc.), añadiendo la extensión html. Por  ejemplo,  el  nombre  del  fichero  asociado  a  la  web  http://www.urjc.es  sería urjc.html.
