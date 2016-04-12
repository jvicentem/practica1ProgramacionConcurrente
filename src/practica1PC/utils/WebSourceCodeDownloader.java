package practica1PC.utils;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

public class WebSourceCodeDownloader {
	private String url;
	
	public WebSourceCodeDownloader(String url) {
		this.url = url;
	}
	
	private String getUrl() {
		return this.url;
	}
	
	public String downloadSourceCode() throws IOException {
		Connection conn = Jsoup.connect(this.getUrl());
		
		Response resp = conn.execute();

		if (resp.statusCode() != 200) {
			throw new IOException(Integer.toString(resp.statusCode()));
		} 

		return conn.get().html();
	}

}
