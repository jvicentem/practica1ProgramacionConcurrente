package practica1PC.modules.threads;

import practica1PC.modules.Downloader;

public class DownloaderThreadAction extends DownloaderThreadAbstract {
	
	public DownloaderThreadAction(Downloader downloader, String threadName) {
		super(downloader, threadName);
	}
	
	@Override
	public void run() {
		getDownloader().threadAction();
	}
}
