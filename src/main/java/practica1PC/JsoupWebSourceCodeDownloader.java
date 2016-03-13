package practica1PC;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

public class JsoupWebSourceCodeDownloader {
	private String url;
	
	public JsoupWebSourceCodeDownloader(String url) {
		this.url = url;
	}
	
	private String getUrl() {
		return this.url;
	}
	
	public String downloadSourceCode() throws IOException {
		Connection conn = Jsoup.connect(this.getUrl());

		String html = "<html></html>";
		
		Response resp = conn.execute();

		if (resp.statusCode() != 200) {
			throw new IOException(Integer.toString(resp.statusCode()));
		} 

		html = conn.get().html();

		return html;
	}

}
