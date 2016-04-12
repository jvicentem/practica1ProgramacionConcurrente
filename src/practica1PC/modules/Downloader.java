package practica1PC.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import practica1PC.utils.FileAndFolderUtils;
import practica1PC.utils.WebSourceCodeDownloader;


public class Downloader{
	private BufferedReader reader;
	private Semaphore readUrlSm;
	private Semaphore writeLogSm;
	private Semaphore concurrentThreadsSm;
	private Semaphore countSm;
	private int count;
	private String path;

	private final static int SHOW_STATUS_INTERVAL = 3; //En segundos
	
	public Downloader(BufferedReader reader, 
					    Semaphore readUrlSm, 
					   Semaphore writeLogSm, 
			  Semaphore concurrentThreadsSm, 
						  Semaphore countSm,
							    String path) {
		this.reader = reader;
		this.readUrlSm = readUrlSm;
		this.writeLogSm = writeLogSm;
		this.concurrentThreadsSm = concurrentThreadsSm;
		this.countSm = countSm;
		this.count = 0;
		this.path = path;
	}
	
	public void downloadsStatus() {
		while(true) {
			try { 
				Thread.sleep((long) SHOW_STATUS_INTERVAL*1000); 
				getCountSm().acquire();
				System.out.println(getCount()+" archivos descargados. "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
				getCountSm().release();	
			} catch(InterruptedException e) {
				return;
			} 
		}
	}

	public void threadAction() {
		InterruptedException exception = null;
		String url = readUrl(exception);
		
		while(thereIsAWebsiteToDownload(url)) {
			try{
				String code = downloadSourceCode(url);
				saveCode(url, code);
			} catch(IOException e) {
				writeInLog(url);
			} catch(InterruptedException e) {
				exception = e;
			}
			finally {
				url = readUrl(exception);
			}
		}
		
		closeReader();
	}
	
	private String readUrl(InterruptedException exception) {
		String url = null;
		
		if(exception == null) {
			try {
				getReadUrlSm().acquire();
			} catch(InterruptedException e) {
				return url;
			}
			
			try {
				url = getReader().readLine();
			} catch(IOException e) {
				//Error de I/O o que el reader se ha cerrado
				return null;
			} finally {
				getReadUrlSm().release();
			}					
		}	
		
		return url;
	}
	
	private boolean thereIsAWebsiteToDownload(String url) {
		return url != null;
	}
	
	private String downloadSourceCode(String url) throws IOException {
		String code;

		try {
			getConcurrentThreadsSm().acquire();
		} catch(InterruptedException e) {
			throw new IOException();
		} 
		
		WebSourceCodeDownloader realDownloader = new WebSourceCodeDownloader(url);
		
		try{
			code = realDownloader.downloadSourceCode();
		} catch(IOException e) {
			throw e;
		} finally {
			getConcurrentThreadsSm().release();
		}			
		
		return code;
	}
	
	private void writeInLog(String url) {
		try {
			getWriteLogSm().acquire();
		} catch(InterruptedException e) {
			return;
		} 
		
		try{
			FileAndFolderUtils.writeAtEndOfFile(Menu.LOG_FILE_NAME, url);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			getWriteLogSm().release();
		}			
	}
	
	private void saveCode(String url, String code) throws IOException, InterruptedException {
		String hostName = "";
		
		try {
			hostName = extractNameFromUrl(url);
		}
		catch(MalformedURLException e) {
			throw new IOException();
		}
		
		FileAndFolderUtils.writeFile(getPath()+File.separator+hostName+".html", code);
		
		try{
			increaseCount();
		} catch(InterruptedException e) {
			throw e;
		}
		
	}
	
	private void increaseCount() throws InterruptedException {
		try{
			getCountSm().acquire();
		} catch(InterruptedException e) {
			throw e;
		} finally {
			setCount(getCount() + 1);
			getCountSm().release();	
		}		
	}
	
	private static String extractNameFromUrl(String url) throws MalformedURLException {
		 String pattern = "http://www.(.*)/?";
	     Pattern r = Pattern.compile(pattern);
	     Matcher m = r.matcher(url);
	     
    	 if(m.find()) {
    		 return m.group(1);
    	 } else {
	    	 throw new MalformedURLException("No es una URL válida");
    	 }
	}
	
	private void closeReader(){
		try{
			getReadUrlSm().acquire();
		} catch(InterruptedException e) {
			//No manejo la interrupción para que el reader se cierre siempre
		} finally {
			try{
				getReader().close();
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				getReadUrlSm().release();
			}	
		}
	}
	
	private BufferedReader getReader() {
		return reader;
	}


	private Semaphore getReadUrlSm() {
		return readUrlSm;
	}


	private Semaphore getWriteLogSm() {
		return writeLogSm;
	}
	

	private Semaphore getConcurrentThreadsSm() {
		return concurrentThreadsSm;
	}
	
	
	private Semaphore getCountSm() {
		return countSm;
	}

	private int getCount() {
		return count;
	}
	

	private void setCount(int count) {
		this.count = count;
	}
	
	
	private String getPath() {
		return path;
	}
}
