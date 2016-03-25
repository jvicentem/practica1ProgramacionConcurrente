package practica1PC;

import java.io.BufferedReader;
import java.util.concurrent.Semaphore;

public class ThreadFactory {
	private BufferedReader file;
	private Semaphore fileSm;
	private Semaphore logSm;
	private Semaphore concurrentSm;
	private Semaphore countSm;
	private Semaphore runningThdsSm;
	private Semaphore availablePermitsSm;
	private String path;
	private Downloader downloader;

	public ThreadFactory(String fileName, String path, int maxConcurrent) {
		this.file = FileAndFolderUtils.openFileNoException(fileName);
		this.fileSm = new Semaphore(1);
		this.logSm = new Semaphore(1);
		this.concurrentSm = new Semaphore(maxConcurrent);
		this.countSm = new Semaphore(1);
		this.runningThdsSm = new Semaphore(0);
		this.availablePermitsSm = new Semaphore(1);
		this.path = path;
		this.downloader = new Downloader(getFile(), getFileSm(), getLogSm(), getConcurrentSm(), getCountSm(), getRunningThdsSm(), getAvailablePermitsSm(), getPath());
	}
	
	public DownloaderThreadAction createActionThread(String threadName) {
		return new DownloaderThreadAction(getDownloader(), threadName);
	}
	
	public DownloaderThreadStatus createStatusThread(String threadName) {
		return new DownloaderThreadStatus(getDownloader(), threadName);
	}

	private BufferedReader getFile() {
		return file;
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
	
	private Semaphore getRunningThdsSm() {
		return runningThdsSm;
	}
	
	private Semaphore getAvailablePermitsSm() {
		return availablePermitsSm;
	}

	private String getPath() {
		return path;
	}
	
	private Downloader getDownloader() {
		return downloader;
	}
	
}
