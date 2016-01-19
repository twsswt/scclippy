package uk.ac.glasgow.scclippy.plugin;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.glasgow.scclippy.lucene.File;
import uk.ac.glasgow.scclippy.lucene.SearchFiles;
import uk.ac.glasgow.scclippy.uicomponents.SearchTab;
import uk.ac.glasgow.scclippy.uicomponents.SettingsTab;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Search functionality
 */
public class Search {

    public static File[] files = null;

    public static SearchType currentSearchType = SearchType.INDEX;

    private final static String LOCAL_INDEX_DEFAULT_FIELD = "contents";

    private final static String STACKEXCHANGE_EXCERPTS_URL = "http://api.stackexchange.com/2.2/search/excerpts?";
    private final static String STACKEXCHANGE_PARAMS = "&sort=relevance&tagged=java&site=stackoverflow";

    public static void stackexchangeSearch(String query) {
        Search.files = null;

        String body = "";
        try {
            body = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        JSONObject json = URLProcessing.readJsonFromUrlUsingGZIP(STACKEXCHANGE_EXCERPTS_URL + "body=" + body + STACKEXCHANGE_PARAMS);

        if (json == null) {
            SearchTab.posts.update("Query failed");
            return;
        }

        if (json.getInt("quota_remaining") == 0) {
            SearchTab.posts.update("Cannot make more requests today");
            return;
        }

        JSONArray items = json.getJSONArray("items");
        if (items.length() == 0) {
            SearchTab.posts.update(
                    "No results. Consider changing the query " +
                            "(e.g. removing variable names) or using another option"
            );
            return;
        }

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = (JSONObject) items.get(i);
            SearchTab.posts.update(i, item.getString("excerpt"), item.getInt(item.getString("item_type") + "_id"));
        }
        Search.currentSearchType = Search.SearchType.API;
    }

    public enum SearchType { INDEX, API }

    public static void webAppSearch(String query) {
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        JSONObject json = URLProcessing.readJsonFromUrl(SettingsTab.webAppURL.getText() + query);

        if (json == null) {
            SearchTab.posts.update("Query failed");
            return;
        }

        JSONArray items = json.getJSONArray("results");
        if (items.length() == 0) {
            SearchTab.posts.update("No results");
            return;
        }

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = (JSONObject) items.get(i);
            SearchTab.posts.update(i, item.getString("content"), item.getInt("id"));
        }
        Search.currentSearchType = Search.SearchType.INDEX;
    }

    public static void localIndexSearch(String query, int posts) {
        try {
            files = SearchFiles.search(
                    Settings.indexPath,
                    LOCAL_INDEX_DEFAULT_FIELD,
                    query.trim(),
                    posts
            );
            SearchTab.posts.update(files);
        } catch (Exception e2) {
            /* TODO: Show intellij notification for failure */
            System.err.println(e2.getMessage());
        }
        Search.currentSearchType = Search.SearchType.INDEX;
    }

}
