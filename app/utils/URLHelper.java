package utils;


import errors.FetchUrlException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import siena.Json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLHelper {
	private static Logger logger = Logger.getLogger(URLHelper.class);

	public static Json fetchJson(String url) throws Exception {
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			try{
			conn.setRequestProperty("user-agent", "TinyBrowser/2.0 (TinyBrowser Comment; rv:1.9.1a2pre) Gecko/20201231");
			if (conn.getResponseCode() == 200) {
				InputStream in = conn.getInputStream();

				byte[] bytes = IOUtils.toByteArray(in);
				Json b =  Json.loads(new String(bytes, "UTF-8"));
				return b;
			}

			if (conn.getResponseCode() >=500) {
				logger.info("code " + conn.getResponseCode());
				throw new FetchUrlException("banned or downtime", conn.getResponseCode());
			}
			if (conn.getResponseCode() == 404) {
				throw new FetchUrlException("404", conn.getResponseCode());
			}
			if (conn.getResponseCode() != 200) {
				throw new Exception("GitHub code was: "+conn.getResponseCode()+" while fetching URL: "+url);
			}
			return null;
			}finally{
			}
		}

	public static TagNode fetchUrl(String url, int timeout) throws Exception {
			HtmlCleaner cleaner = new HtmlCleaner();
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestProperty("user-agent", "TinyBrowser/2.0 (TinyBrowser Comment; rv:1.9.1a2pre) Gecko/20201231");
			if(timeout > 0) {
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout);
			}
			if (conn.getResponseCode() == 503) {
				logger.info("Response code 503. Going to sleep for a while");
				Thread.sleep(60000);
				logger.info("Wake up!");
			}

			if (conn.getResponseCode() == 404 || conn.getResponseCode() == 302){
				throw new FetchUrlException("Link Doesn't exists or moved", conn.getResponseCode());
			}

			if (conn.getResponseCode() != 200)
				throw new Exception("Response code was: "+conn.getResponseCode()+" while fetching URL: "+url);

			String contentType = conn.getHeaderField("content-type");
			if(!contentType.contains("text/html"))
				return null;

			//String charset = "iso-8859-1";
			String charset = "UTF-8";
			if(contentType != null) {
				int i = contentType.indexOf("charset=");
				if(i > 0) {
					charset = contentType.substring(i+"charset=".length()).trim();
				}
			}
		InputStream in = conn.getInputStream();
		InputStreamReader reader = new InputStreamReader(in, charset);
		TagNode tag = cleaner.clean(reader);
		return tag;
	}
}
