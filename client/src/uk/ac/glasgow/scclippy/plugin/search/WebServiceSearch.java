package uk.ac.glasgow.scclippy.plugin.search;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.glasgow.scclippy.plugin.lucene.File;
import uk.ac.glasgow.scclippy.plugin.settings.Settings;
import uk.ac.glasgow.scclippy.plugin.util.URLProcessing;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Class for searching with a web service
 */
public class WebServiceSearch extends Search {

    /**
     * Performs search by querying app server using RESTful services
     * @param query query string
     * @param posts number of posts to return
     * @return error message or null if successful
     */
    public String search(String query, int posts) {
        query = query.trim();
        if (query.equals(""))
            return "";

        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        JSONObject json = URLProcessing.readJsonFromUrl(Settings.webServiceURI[0] + query + "?posts=" + posts);

        if (json == null) {
            return "Connection problems / Query failed";
        }

        JSONArray items = json.getJSONArray("results");
        if (items.length() == 0) {
            return "No results";
        }

        files = new File[items.length()];
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = (JSONObject) items.get(i);
            files[i] = new File("" + item.getString("id"), item.getString("content"), (int) item.get("score"));
        }
        Search.currentSearchType = SearchType.WEB_SERVICE;

        return null;
    }
}
