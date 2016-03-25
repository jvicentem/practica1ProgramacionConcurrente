package practica1PC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

public class Menu {
	public static final String LOG_FILE_NAME = "error_log.txt";
	
	private static final int NUMBER_OF_THREATS = 5;
	
	private static final int MAX_CONCURRENT_THREATS = 2;
	
	public static void execute() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("> Introduce el fichero con las páginas web que quieres descargar");
		String urlsFilePath = reader.readLine();
		while(! FileAndFolderUtils.validFilePath(urlsFilePath)) {
			System.out.println(">> Ruta de archivo no válida. Por favor, vuelva a introducir una ruta correcta:");
			urlsFilePath = reader.readLine();
		}
		String urlsFilePathToUse = urlsFilePath;
		
	    System.out.println("> Introduce el directorio donde quieres guardar las descargas");
	    String folderPath = reader.readLine();
	    FileAndFolderUtils.createFolder(folderPath);
		
		WebProcessor wp = new WebProcessor(folderPath, Menu.NUMBER_OF_THREATS, Menu.MAX_CONCURRENT_THREATS);
		Thread th = new Thread(() -> wp.process(urlsFilePathToUse), "Process tread");
		    
		FileAndFolderUtils.deleteFileIfExists(Menu.LOG_FILE_NAME);
		
	    th.start();

	    System.out.println(">> Presiona ENTER para cancelar las descargas.");
	    
	    Thread stopThread = new Thread(() -> {
	    										try {
	    											IOUtils.readLines(System.in, "UTF-8");
	    											th.interrupt();
	    											System.out.println("Descargas canceladas por usuario");
	    										} catch(IOException e) { return; }
	    									 }
	    							  , "Stop thread");
	    
	    reader.close();
	    stopThread.start();
	    
	    try {
			th.join();
		} catch (InterruptedException e) {
			IOUtils.closeQuietly(System.in);
		} 
	    
	}

}
