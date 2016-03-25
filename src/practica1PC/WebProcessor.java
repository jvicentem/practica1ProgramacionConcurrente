package practica1PC;

import java.util.ArrayList;
import java.util.List;

public class WebProcessor {
	private String path;
	private int nDown;
	private int maxDown;
	
	public WebProcessor(String path, int nDown, int maxDown) {
		this.path = path;
		
		this.nDown = nDown;
		
		/* Por seguridad, no se permite que haya más procesos
		 * paralelos que el número de CPUs que hay disponibles */
		int cores = Runtime.getRuntime().availableProcessors();
		
		this.maxDown = (maxDown > cores) ? cores : ((maxDown <= 0) ? 1 : maxDown);
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
	
	public void process(String fileName) {
		ThreadFactory threadFactory = new ThreadFactory(fileName, getPath(), getMaxDown());
		
		List<DownloaderThreadAbstract> thds = new ArrayList<DownloaderThreadAbstract>(getNDown()+1);
		
		for (int i = 0; i < getNDown(); i++) 
			thds.add(threadFactory.createActionThread("Thread "+i));
		
		DownloaderThreadStatus statusThd = threadFactory.createStatusThread("Status thread");
		thds.add(statusThd);

		for (DownloaderThreadAbstract th : thds)
			th.start();

		for (DownloaderThreadAbstract th : thds)
			try {
				th.join();
			} catch (InterruptedException e) {
				for (DownloaderThreadAbstract thd : thds){
					thd.interrupt();
				}
				break;
			}
	}
}
