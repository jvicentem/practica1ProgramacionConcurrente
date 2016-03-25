package practica1PC;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Downloader{
	private BufferedReader reader;
	private Semaphore readUrlSm;
	private Semaphore writeLogSm;
	private Semaphore concurrentThreadsSm;
	private Semaphore countSm;
	private Semaphore threadsRunningSm;
	private Semaphore availablePermitsSm;
	private int count;
	private String path;

	private final int SHOW_STATUS_INTERVAL = 3; //En segundos
	
	public Downloader(BufferedReader reader, 
					    Semaphore readUrlSm, 
					   Semaphore writeLogSm, 
			  Semaphore concurrentThreadsSm, 
						  Semaphore countSm,
				 Semaphore threadsRunningSm,
			   Semaphore availablePermitsSm,
							    String path) {
		this.reader = reader;
		this.readUrlSm = readUrlSm;
		this.writeLogSm = writeLogSm;
		this.concurrentThreadsSm = concurrentThreadsSm;
		this.countSm = countSm;
		this.threadsRunningSm = threadsRunningSm;
		this.availablePermitsSm = availablePermitsSm;
		this.count = 0;
		this.path = path;
	}
	
	public void downloadsStatus() {
		while(threadsRunning()) {
			try { 
				Thread.sleep(SHOW_STATUS_INTERVAL*1000); 
				
				getCountSm().acquire(); 
			} catch(InterruptedException e) {
				break;
			}
			
			System.out.println(getCount()+" archivos descargados. "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
			
			getCountSm().release();			
		}
	}
	
	private boolean threadsRunning() {
		return getThreadsRunningSm().availablePermits() > 0;
	}

	public void threadAction() {
		getThreadsRunningSm().release();
		
		String url = readUrl();
		
		while(thereIsAWebsiteToDownload(url)) {
			try{
				String code = downloadSourceCode(url);
				saveCode(url, code);
			} catch(IOException e) {
				writeInLog(url);
			}
			
			url = readUrl();
		}
		
		closeReader();
		
		try{ 
			getThreadsRunningSm().acquire(); 
		} catch(InterruptedException e) {
			//System.out.println("InterruptedException threadAction "+Thread.currentThread().getName());
			getThreadsRunningSm().acquireUninterruptibly();
		}
		
	}
	
	private String readUrl() {
		String url = null;
		
		try {
			getReadUrlSm().acquire();
		} catch(InterruptedException e) {
			//System.out.println("InterruptedException readUrl "+Thread.currentThread().getName());
			Thread.currentThread().interrupt();
			return url;
		}
		
		try {
			url = getReader().readLine();
		} catch(IOException e) {
			return url;
			//Error o que el reader se ha cerrado
		} finally {
			getReadUrlSm().release();
			
			if(Thread.currentThread().isInterrupted()) {
				//System.out.println("isInterrupted readUrl "+Thread.currentThread().getName());
				Thread.currentThread().interrupt();
				url = null;
			}
		}		
		
		return url;
	}
	
	private boolean thereIsAWebsiteToDownload(String url) {
		if(Thread.currentThread().isInterrupted()) {
			Thread.currentThread().interrupt();
				if(url != null) {
					writeInLog(url);
					return false;
				}			
		}

		return url != null;
	}
	
	private String downloadSourceCode(String url) throws IOException {
		String code = "";
		
		//System.out.println("downloadSourceCode "+Thread.currentThread().interrupted()+" "+Thread.currentThread().getName());

		try {
			getConcurrentThreadsSm().acquire();
		} catch(InterruptedException e) {
			//System.out.println("InterruptedException downloadSourceCode "+Thread.currentThread().getName());
			
			Thread.currentThread().interrupt();
			
			getAvailablePermitsSm().acquireUninterruptibly();
			
			if(getConcurrentThreadsSm().availablePermits() >= 1) {
				getConcurrentThreadsSm().acquireUninterruptibly();
				getAvailablePermitsSm().release();
			}
			else {
				getAvailablePermitsSm().release();
				throw new IOException();
			}
		} 
		
		WebSourceCodeDownloader realDownloader = new WebSourceCodeDownloader(url);
		
		try{
			code = realDownloader.downloadSourceCode();
		} catch(IOException e) {
			throw e;
		} finally {
			getAvailablePermitsSm().acquireUninterruptibly();
			
			getConcurrentThreadsSm().release();
			
			getAvailablePermitsSm().release();
		}			

		//System.out.println("downloadSourceCode end "+Thread.currentThread().interrupted()+" "+Thread.currentThread().getName());
		return code;
	}
	
	private void writeInLog(String url) {
		try {
			getWriteLogSm().acquire();
		} catch(InterruptedException e) {
			//System.out.println("InterruptedException writeInLog "+Thread.currentThread().getName());
			Thread.currentThread().interrupt();
			getWriteLogSm().acquireUninterruptibly();
		} finally {
			try{
				FileAndFolderUtils.writeAtEndOfFile(Menu.LOG_FILE_NAME, url);
			} catch(IOException e) {

			} finally {
				getWriteLogSm().release();
			}			
		}
	}
	
	private void saveCode(String url, String code) throws IOException {
		//System.out.println("saveCode "+Thread.currentThread().interrupted()+" "+Thread.currentThread().getName());
		FileAndFolderUtils.writeFile(getPath()+File.separator+extractNameFromUrl(url)+".html", code);
		increaseCount();
		//System.out.println("saveCode end "+Thread.currentThread().interrupted()+" "+Thread.currentThread().getName());
	}
	
	private void increaseCount() {
		//System.out.println("increaseCount "+Thread.currentThread().interrupted()+" "+Thread.currentThread().getName());
		try{
			getCountSm().acquire();
		} catch(InterruptedException e) {
			//System.out.println("InterruptedException increaseCount "+Thread.currentThread().getName());
			Thread.currentThread().interrupt();
			getCountSm().acquireUninterruptibly();
		} finally {
			setCount(getCount() + 1);
			getCountSm().release();
		}
		//System.out.println("increaseCount end "+Thread.currentThread().interrupted()+" "+Thread.currentThread().getName());
	}
	
	private String extractNameFromUrl(String url) {
	     String pattern = "http://www.(.*)/";
	     Pattern r = Pattern.compile(pattern);
	     Matcher m = r.matcher(url);
	     m.find();
	     return m.group(1);
	}
	
	private void closeReader(){
		try{
			getReadUrlSm().acquire();
		} catch(InterruptedException e) {
			//System.out.println("InterruptedException closeReader "+Thread.currentThread().getName());
			Thread.currentThread().interrupt();
			getReadUrlSm().acquireUninterruptibly();
		} 
		finally {
			try{
				getReader().close();
			} catch(IOException e) {
				
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
	
	private Semaphore getThreadsRunningSm() {
		return threadsRunningSm;
	}
	
	private Semaphore getAvailablePermitsSm() {
		return availablePermitsSm;
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
