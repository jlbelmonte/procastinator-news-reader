package controllers;

import play.Play;
import play.mvc.Controller;
import siena.Json;
import utils.Constants;
import utils.RedditHelper;
import utils.URLHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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


		Map<String, List<String>> finalLinks = new HashMap<String, List<String>>();
		for (String topic : topicList){
			topic = topic.replace(" ", "-");
			finalLinks.putAll(RedditHelper.searchDelicious(topic));
		}
		render("Application/index.html", finalLinks);

	}


}