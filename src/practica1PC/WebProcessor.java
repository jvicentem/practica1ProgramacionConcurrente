package practica1PC;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebProcessor {
	private static volatile BufferedReader br = null;

	private static Semaphore smFile = null;
	private static Semaphore smLog = null;
	private static Semaphore smConcurrentThreadsLimit = null;
	
	private String path;
	private int nDown;
	private int maxDown;
	
	public WebProcessor(String path, int nDown, int maxDown) {
		this.path = path;
		
		this.nDown = nDown;
		
		/* Por seguridad, no se permite que haya más procesos
		 * paralelos que el número de CPUs que hay disponibles */
		int cores = Runtime.getRuntime().availableProcessors();
		if(maxDown > cores) {
			maxDown = cores;
		}
		
		this.maxDown = maxDown;
	}
	
	private String getPath() {
		return this.path;
	}
	
	private int getNDown() {
		return this.nDown;
	}
	
	private int getMaxDown() {
		return this.maxDown;
	}
	
	private void setPath(String path) {
		this.path = path;
	}
	
	private void setNDown(int nDown) {
		this.nDown = nDown;
	}
	
	private void setMaxDown(int maxDown) {
		int cores = Runtime.getRuntime().availableProcessors();
		if(maxDown > cores) {
			maxDown = cores;
		}
		
		this.maxDown = maxDown;
	}
	
	private String extractNameFromUrl(String url) {
	     String pattern = "http://www.([^.]*).";
	     Pattern r = Pattern.compile(pattern);
	     Matcher m = r.matcher(url);
	     m.find();
	     return m.group(1);
	}
	
	private void threadAction() {		
		while(true) {
			//Intento adquirir el semáforo para leer del fichero de urls
			try { WebProcessor.smFile.acquire(); } catch (InterruptedException e) {e.printStackTrace();}
			//Llegados a este punto, he adquirido el semáforo para leer del fichero de urls
			String url = "";
			try { 
				//Leo la siguiente url del fichero
				url = WebProcessor.br.readLine(); 
				//Si url es null, eso quiere decir que he llegado al final del fichero
				if(url == null){
					//Libero el semáforo para leer del fichero de urls
					WebProcessor.smFile.release();
					return;
				}
			} 
			catch (IOException e) { 
				//Si ha ocurrido una excepción, libero el semáforo para leer del fichero de urls
				WebProcessor.smFile.release();
				return;
			}
			//Si se llega a este punto es que se ha leído la url correctamente y
			//se libera el semáforo para leer del fichero de urls
			WebProcessor.smFile.release();
			//Se intenta adquirir el semáforo que regula el número de threads que están
			//al mismo tiempo descargando una página web
			try { WebProcessor.smConcurrentThreadsLimit.acquire(); } catch (InterruptedException e) {e.printStackTrace();}
			JsoupWebSourceCodeDownloader downloader = new JsoupWebSourceCodeDownloader(url);
			try {
				//Se descarga el código de la página web y se guarda en un archivo
				String source = downloader.downloadSourceCode();
				File file = new File(getPath()+File.separator+extractNameFromUrl(url)+".html");
				PrintWriter out = new PrintWriter(file);
				out.println(source);
				out.close();
			} catch(IOException e){
				//Escribir en log
				//Intenta adquirir el semáforo para escribir en el archivo de log de errores
				try { WebProcessor.smLog.acquire(); } catch(InterruptedException ie) {ie.printStackTrace();}
				try {
					//Escribe la url que ha fallado en el log
					FileWriter logfile = new FileWriter("error_log.txt",true);
					BufferedWriter logbw = new BufferedWriter(logfile);
					logbw.write(url);
					logbw.newLine();
					logbw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					//Libero el semáforo que regula el número de threads que están
					//al mismo tiempo descargando una página web tanto si sólo se ha hecho 
					//el bloque try como si se ha hecho el bloque catch también
					WebProcessor.smLog.release();
				}
			} finally {
				//Libero el semáforo del log tanto si sólo se ha hecho 
				//el bloque try como si se ha hecho el bloque catch también
				WebProcessor.smConcurrentThreadsLimit.release();
			}			
		}
	}
	
	
	public void process(String fileName) throws FileNotFoundException, NotDirectoryException {
		/* Intentamos abrir el archivo. Si no se abre, se lanzará una exepción
		 * al exterior, que la clase Menu se encargará de recoger y deberá 
		 * volver a pedir que se introduzca una ruta de un fichero
		 * con urls*/
		WebProcessor.br = new BufferedReader(new FileReader(fileName));
		
		boolean successCreatingDirectory = new File(getPath()).mkdirs();
		
		if(!successCreatingDirectory) 
			throw new NotDirectoryException(getPath());
		
		WebProcessor.smFile = new Semaphore(1);
		WebProcessor.smLog = new Semaphore(1);
		WebProcessor.smConcurrentThreadsLimit = new Semaphore(getMaxDown());
		
		List<Thread> thds = new ArrayList<Thread>(getNDown());

		for (int i = 0; i < getNDown(); i++) {
			thds.add(new Thread(() -> this.threadAction(), "Thread " + i));
		}

		for (Thread th : thds)
			th.start();

		for (Thread th : thds)
			try {
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		try {
			WebProcessor.br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
