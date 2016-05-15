package practica1PC.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import practica1PC.utils.FileAndFolderUtils;

public class Menu {
	public static final String LOG_FILE_NAME = "error_log.txt";
	
	private static final int NUMBER_OF_THREATS = 6;
	
	private static final int MAX_CONCURRENT_THREATS = 4;
	
	private Menu() {}
	
	public static final void execute() throws IOException {
		BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));

		String urlsFilePath = requestUrlsFilePath(keyboardReader);
		
		String folderPath = requestFolderPath(keyboardReader);
		
	    Thread stopThread = createStopThread(Thread.currentThread(), keyboardReader);
	    
	    stopThread.start();
		
	    startWebProcessor(folderPath, urlsFilePath);
	    
		stopThread.interrupt();
		
		System.out.println("Programa finalizado");
	}
	
	private static String requestUrlsFilePath(BufferedReader keyboardReader) throws IOException {
		System.out.println("> Introduce el fichero con las páginas web que quieres descargar");
		String urlsFilePath = keyboardReader.readLine();
		
		while (! FileAndFolderUtils.validFilePath(urlsFilePath)) {
			System.out.println(">> Ruta de archivo no válida. Por favor, vuelva a introducir una ruta correcta:");
			urlsFilePath = keyboardReader.readLine();
		}
		
		return urlsFilePath;		
	}
	
	private static String requestFolderPath(BufferedReader keyboardReader) throws IOException {
	    System.out.println("> Introduce el directorio donde quieres guardar las descargas");
	    String folderPath = keyboardReader.readLine();
	    FileAndFolderUtils.createFolder(folderPath);
	    
	    return folderPath;
	}
	
	private static void startWebProcessor(String folderPath, String urlsFilePath) {
		WebProcessor wp = new WebProcessor(folderPath, Menu.NUMBER_OF_THREATS, Menu.MAX_CONCURRENT_THREATS);
		FileAndFolderUtils.deleteFileIfExists(Menu.LOG_FILE_NAME);
		wp.process(urlsFilePath);
	}
	
	private static Thread createStopThread(Thread threadToStop, BufferedReader keyboardReader) {
		return new Thread(() -> {
			System.out.println("> Presiona ENTER para cancelar las descargas");
			
			try {
				while (System.in.available() == 0) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						//Este thread se interrumpe, es decir, el programa
						//ha acabado correctamente
						keyboardReader.close();
						return;
					}
				}
				
				keyboardReader.close();
				threadToStop.interrupt();
				System.out.println("Descargas canceladas por usuario"); 													
			} catch (IOException e) {
				e.printStackTrace();
			}	    									
		 }
		, "Stop thread");		
	}
	
}
