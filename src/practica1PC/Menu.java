package practica1PC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Menu {
	public static final String LOG_FILE_NAME = "error_log.txt";
	
	private static final int NUMBER_OF_THREATS = 5;
	
	private static final int MAX_CONCURRENT_THREATS = 2;
	
	private Menu() {}
	
	public static void execute() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		String urlsFilePath = requestUrlsFilePath(reader);
		
		String folderPath = requestFolderPath(reader);
		
	    Thread th = startWebProcessorThread(folderPath, urlsFilePath);
	    
	    Thread stopThread = new Thread(() -> {
	    										System.out.println("> Presiona ENTER para cancelar las descargas");
	    										try {
	    											reader.readLine(); 		
	    										} catch(Exception e) {
	    											e.printStackTrace();
	    										} 
    											th.interrupt();
    											System.out.println("Descargas canceladas por usuario"); 
	    									 }
	    							  , "Stop thread");
	    stopThread.start();

	    try {
			th.join();
			reader.close();
			System.in.close();
			stopThread.interrupt();
		} catch (InterruptedException e) { 
			e.printStackTrace();
		} 
	    
	}
	
	private static String requestUrlsFilePath(BufferedReader reader) throws IOException {
		System.out.println("> Introduce el fichero con las páginas web que quieres descargar");
		String urlsFilePath = reader.readLine();
		
		while(! FileAndFolderUtils.validFilePath(urlsFilePath)) {
			System.out.println(">> Ruta de archivo no válida. Por favor, vuelva a introducir una ruta correcta:");
			urlsFilePath = reader.readLine();
		}
		
		return urlsFilePath;		
	}
	
	private static String requestFolderPath(BufferedReader reader) throws IOException {
	    System.out.println("> Introduce el directorio donde quieres guardar las descargas");
	    String folderPath = reader.readLine();
	    FileAndFolderUtils.createFolder(folderPath);
	    
	    return folderPath;
	}
	
	private static Thread startWebProcessorThread(String folderPath, String urlsFilePath) {
		WebProcessor wp = new WebProcessor(folderPath, Menu.NUMBER_OF_THREATS, Menu.MAX_CONCURRENT_THREATS);
		
		Thread th = new Thread(() -> wp.process(urlsFilePath), "Process tread");
		FileAndFolderUtils.deleteFileIfExists(Menu.LOG_FILE_NAME);
		
	    th.start();		
	    
	    return th;
	}
	
}
