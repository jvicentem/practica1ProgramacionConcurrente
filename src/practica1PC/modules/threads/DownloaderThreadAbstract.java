package practica1PC.modules.threads;

import practica1PC.modules.Downloader;

public abstract class DownloaderThreadAbstract extends Thread {
	private Downloader downloader;
	
	public DownloaderThreadAbstract(Downloader downloader, String threadName) {
		super(threadName);
		this.downloader = downloader;
	}
	
	protected Downloader getDownloader() {
		return downloader;
	}
	
	@Override
	public abstract void run();
}
