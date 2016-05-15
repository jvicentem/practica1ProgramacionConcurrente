package practica1PC.modules.threads;

import practica1PC.modules.Downloader;

public class DownloaderThreadStatus extends DownloaderThreadAbstract {
	
	public DownloaderThreadStatus(Downloader downloader, String threadName) {
		super(downloader, threadName);
	}
	
	@Override
	public void run() {
		getDownloader().downloadsStatus();
	}
}
