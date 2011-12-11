package controllers;

import org.htmlcleaner.TagNode;
import play.Play;
import play.mvc.Controller;
import siena.Json;
import utils.Constants;
import utils.URLHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Application extends Controller {

    public static void index() {
        render();
    }

	public static void showResults(){

	}
	
	public static void askTopicsAndArticles(String twitterUser){
		String  apiKey = Play.configuration.getProperty("klout.key");
		Json userInfo = Json.map();
		try {
			userInfo = URLHelper.fetchJson(Constants.influencersUrl+twitterUser+Constants.keyParam+apiKey);
		} catch (Exception e){
			e.printStackTrace();
			//I know this is not a proper handling of an error, but I've already drunk 4 beers and I slept 3.5 hours
			// last  night. so...
			index();
		}
		Json userSection = userInfo.get("users");
		if (userSection == null)  index();
		Json influencers = userSection.at(0).get("influencers");
		if ( influencers == null) index();
		String influencersString = "";
		for ( Json influencer : influencers){
			influencersString += influencer.get("twitter_screen_name").str()+",";
		}

		influencersString = influencersString.substring(0, influencersString.length() -1);

		Map<String, Integer> topics = new HashMap<String, Integer>();
		String urlTopics = Constants.topicsUrl+influencersString+Constants.keyParam+apiKey;
		Json topicsJson  = Json.map();
		try {
			topicsJson = URLHelper.fetchJson(urlTopics).get("users");
		} catch ( Exception e){
				index();
				e.printStackTrace();
		}

		Set<String> topicList = new HashSet<String>();
		for (Json userTopics : topicsJson){
			Json userTops = userTopics.get("topics");
			for (Json topic : userTops){
				topicList.add(topic.str());
			}
		}

		Map<String, String> finalLinks = new HashMap<String, String>();
		System.out.println(topicList);
		for (String topic : topicList){
			topic = topic.replace(" ", "-");
			String url ="http://delicious.com/search?p="+topic;
			System.out.println(url);
			try{
				TagNode html = URLHelper.fetchUrl(url, -1);
				////div[@id='profile']/img[@class='avatar']"
				Object[] links = html.evaluateXPath("ul[@id='srch1-bookmarklist']/li/div[@class='data']/h4/a");
				if (links == null ) continue;
				TagNode link1 = (TagNode) links[0];

				finalLinks.put(link1.getAttributeByName("title"), link1.getAttributeByName("href"));


				TagNode link2 = (TagNode) links[1];
				finalLinks.put(link2.getAttributeByName("title"), link2.getAttributeByName("href"));


				TagNode link3 = (TagNode) links[2];
				finalLinks.put(link3.getAttributeByName("title"), link3.getAttributeByName("href"));
			} catch (Exception e){
				index();
				e.printStackTrace();
			}
			System.out.println(url);
		}
		render("Application/index.html", finalLinks);
		}
}