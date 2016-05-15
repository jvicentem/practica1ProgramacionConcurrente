package practica1PC.modules.threads;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.concurrent.Semaphore;

import practica1PC.modules.Downloader;
import practica1PC.utils.FileAndFolderUtils;

public class ThreadFactory {
	private String fileName;
	private BufferedReader file;
	private Semaphore fileSm;
	private Semaphore logSm;
	private Semaphore concurrentSm;
	private Semaphore countSm;
	private String path;
	private Downloader downloader;

	public ThreadFactory(String fileName, String path, int maxConcurrent) {
		this.fileName = fileName;
		this.fileSm = new Semaphore(1);
		this.logSm = new Semaphore(1);
		this.concurrentSm = new Semaphore(maxConcurrent);
		this.countSm = new Semaphore(1);
		this.path = path;
		this.downloader = null;
	}
	
	public DownloaderThreadAction createActionThread(String threadName) {
		try {
			createDownloaderObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return new DownloaderThreadAction(getDownloader(), threadName);
	}

	public DownloaderThreadStatus createStatusThread(String threadName) {
		try {
			createDownloaderObject();
		} catch (FileNotFoundException e) {		
			e.printStackTrace();
		}
		
		return new DownloaderThreadStatus(getDownloader(), threadName);
	}
	
	private void createDownloaderObject() throws FileNotFoundException {
		if (getFile() == null) {
			try {
				setFile(FileAndFolderUtils.openFile(getfileName()));
			} catch(FileNotFoundException e) {
				throw e;
			}
			
			setDownloader(new Downloader(getFile(), getFileSm(), getLogSm(), getConcurrentSm(), getCountSm(), getPath()));
		}		
	}

	private BufferedReader getFile() {
		return file;
	}
	
	private void setFile(BufferedReader file) {
		this.file = file;
	}

	private Semaphore getFileSm() {
		return fileSm;
	}

	private Semaphore getLogSm() {
		return logSm;
	}

	private Semaphore getConcurrentSm() {
		return concurrentSm;
	}
	
	private Semaphore getCountSm() {
		return countSm;
	}

	private String getPath() {
		return path;
	}
	
	private Downloader getDownloader() {
		return downloader;
	}
	
	private void setDownloader(Downloader downloader) {
		this.downloader = downloader;
	}
	
	private String getfileName() {
		return fileName;
	}
	
}
