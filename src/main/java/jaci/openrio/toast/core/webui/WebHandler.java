package jaci.openrio.toast.core.webui;

import java.util.HashMap;

public interface WebHandler {

    /**
     * Should this handler act for the URL provided?
     */
    public boolean handleURL(String url);

    /**
     * Handles actions in the given URL. POST and GET requests are handled already
     * @return The url to navigate to
     */
    public String handle(String url, HashMap<String, String> post, HashMap<String, String> get, HashMap<String, String> headers);

}
