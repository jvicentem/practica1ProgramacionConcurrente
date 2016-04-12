package practica1PC;

public class DownloaderThreadStatus extends DownloaderThreadAbstract {
	
	public DownloaderThreadStatus(Downloader downloader, String threadName) {
		super(downloader, threadName);
	}
	
	@Override
	public void run() {
		getDownloader().downloadsStatus();
	}
}
