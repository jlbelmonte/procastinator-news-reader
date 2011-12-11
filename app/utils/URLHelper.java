package utils;


import errors.FetchUrlException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import siena.Json;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLHelper {
	private static Logger logger = Logger.getLogger(URLHelper.class);

	public static Json fetchJson(String url) throws Exception {
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			try{
			conn.setRequestProperty("user-agent", "want-to-read");
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
}
