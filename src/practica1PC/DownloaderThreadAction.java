package practica1PC;

public class DownloaderThreadAction extends DownloaderThreadAbstract {
	
	public DownloaderThreadAction(Downloader downloader, String threadName) {
		super(downloader, threadName);
	}
	
	@Override
	public void run() {
		getDownloader().threadAction();
	}
}
