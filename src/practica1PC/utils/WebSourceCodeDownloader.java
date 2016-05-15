package practica1PC.utils;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

public class WebSourceCodeDownloader {
	private WebSourceCodeDownloader() {}
	
	public static String downloadSourceCode(String url) throws IOException {
		Connection conn = Jsoup.connect(url);
			
		Response resp; 
		
		resp = conn.execute();

		if (resp.statusCode() != 200) {
			throw new IOException(Integer.toString(resp.statusCode()));
		} 

		return conn.get().html();			
	}

}
