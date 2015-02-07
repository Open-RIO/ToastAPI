package jaci.openrio.toast.core.webui;

import java.util.HashMap;

/**
 * An interface used for handling web requests. This allows for replacement of content of a web-page without using JS or
 * god-forbid PHP. This should be implemented if you plan to make your own custom web pages for the WebUI, or modify
 * existing one. {@link jaci.openrio.toast.core.webui.handlers.HandlerPower} is an example of this interface.
 *
 * These classes are registered in {@link jaci.openrio.toast.core.webui.WebRegistry}
 *
 * @author Jaci
 */
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

    /**
     * Should this handler completely rewrite the contents of this page?
     */
    public boolean overrideData(String url, HashMap<String, String> post, HashMap<String, String> get, HashMap<String, String> headers);

    /**
     * Override the content of the page requested
     * @return The new content of the page
     */
    public String doOverride(String url, HashMap<String, String> post, HashMap<String, String> get, HashMap<String, String> headers);

}
