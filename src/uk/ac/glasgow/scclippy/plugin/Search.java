package uk.ac.glasgow.scclippy.plugin;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.glasgow.scclippy.lucene.File;
import uk.ac.glasgow.scclippy.lucene.SearchFiles;
import uk.ac.glasgow.scclippy.uicomponents.SettingsTab;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Search functionality
 */
public class Search {

    public static File[] files = null;

    public enum SearchType { INDEX, API }
    public static SearchType currentSearchType = SearchType.INDEX;

    private final static String LOCAL_INDEX_DEFAULT_FIELD = "contents";

    private final static String STACKEXCHANGE_EXCERPTS_URL = "http://api.stackexchange.com/2.2/search/excerpts?";
    private final static String STACKEXCHANGE_PARAMS = "&sort=relevance&tagged=java&site=stackoverflow";

    /**
     * Performs a search using StackExchange API v2.2
     * @param query query string
     */
    public static void stackexchangeSearch(String query) {
        query = query.trim();
        if (query.equals(""))
            return;

        Search.files = null;

        String body = "";
        try {
            body = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        JSONObject json = URLProcessing.readJsonFromUrlUsingGZIP(STACKEXCHANGE_EXCERPTS_URL + "body=" + body + STACKEXCHANGE_PARAMS);

        if (json == null) {
            files = new File[1];
            files[0] = new File("Query failed");
            return;
        }

        if (json.getInt("quota_remaining") == 0) {
            files = new File[1];
            files[0] = new File("Cannot make more requests today");
            return;
        }

        JSONArray items = json.getJSONArray("items");
        if (items.length() == 0) {
            files = new File[1];
            files[0].setContent("No results. Consider changing the query " +
                    "(e.g. removing variable names) or using another option");
            return;
        }

        files = new File[items.length()];
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = (JSONObject) items.get(i);
            String id = "" + item.getInt(item.getString("item_type") + "_id");
            String content = StringProcessing.textToHTML(item.getString("excerpt")) + "<br/>";
            files[i] = new File(id, content);
        }
        Search.currentSearchType = Search.SearchType.API;
    }

    /**
     * Performs search by querying app server using RESTful services
     * @param query query string
     * @param posts number of posts to return
     */
    public static void webAppSearch(String query, int posts) {
        query = query.trim();
        if (query.equals(""))
            return;

        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        JSONObject json = URLProcessing.readJsonFromUrl(SettingsTab.webAppURL.getText() + query + "?posts=" + posts);

        if (json == null) {
            files = new File[1];
            files[0] = new File("Query failed");
            return;
        }

        JSONArray items = json.getJSONArray("results");
        if (items.length() == 0) {
            files = new File[1];
            files[0] = new File("No results");
            return;
        }

        files = new File[items.length()];
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = (JSONObject) items.get(i);
            files[i] = new File("" + item.getInt("id"), item.getString("content"));
        }
        Search.currentSearchType = Search.SearchType.INDEX;
    }

    /**
     * Performs search using a local index
     * @param query query string
     * @param posts number of returned results/posts
     */
    public static void localIndexSearch(String query, int posts) {
        query = query.trim();
        if (query.equals(""))
            return;

        try {
            files = SearchFiles.search(
                    Settings.indexPath,
                    LOCAL_INDEX_DEFAULT_FIELD,
                    query,
                    posts
            );
        } catch (Exception e2) {
            System.err.println(e2.getMessage());
        }
        Search.currentSearchType = Search.SearchType.INDEX;
    }

}
