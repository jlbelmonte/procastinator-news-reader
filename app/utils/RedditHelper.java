package utils;


import org.htmlcleaner.TagNode;

import java.util.HashMap;
import java.util.Map;

public class RedditHelper {
	public static Map<String, String> searchDelicious(String searchItem) {
		Map<String, String> searchResults = new HashMap<String, String>();
		try {
			String url ="http://www.reddit.com/search?q="+searchItem;
			TagNode html = URLHelper.fetchUrl(url, -1);
			Object[] links = html.evaluateXPath("//p[@class='title']/a");


			if (links == null) return searchResults;
			TagNode link1 = (TagNode) links[0];
			if (link1 == null) return  searchResults;
			String title = new String(link1.getText());
			String href = reditty(link1.getAttributeByName("href"));
			
			searchResults.put(title, href);

			TagNode link2 = (TagNode) links[1];
			if (link2 == null) return  searchResults;
			title = new String(link2.getText());
			href = reditty(link2.getAttributeByName("href"));
			searchResults.put(title, href);

			TagNode link3 = (TagNode) links[2];
			if (link3 == null) return  searchResults;
			title = new String(link3.getText());
			href = reditty(link3.getAttributeByName("href"));
			searchResults.put(title, href);

		} catch (Exception e) {
			e.printStackTrace();
		}
//
//		return searchResults;
		return searchResults;
	}
	
	public static String reditty(String uri){
		if( uri.startsWith("http")) return uri;
		return "http://www.reddit.com"+uri;
	}
}
