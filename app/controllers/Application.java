package controllers;

import play.Play;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        render();
    }
	
	public static void askTopicsAndArticles(String twitterUser){
		String apiKey=Play.configuration.getProperty("klout.key");









	}

}