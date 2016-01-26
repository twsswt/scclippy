package uk.ac.glasgow.scclippy.plugin;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.glasgow.scclippy.lucene.File;
import uk.ac.glasgow.scclippy.lucene.SearchFiles;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

/**
 * Search functionality
 */
public class Search {

    public static File[] files = null;

    public enum SearchType { LOCAL_INDEX, WEB_SERVICE, STACKEXCHANGE_API }
    public static SearchType currentSearchType = SearchType.WEB_SERVICE;

    public enum SortType { RELEVANCE, BY_SCORE }
    public static SortType currentSortOption = SortType.RELEVANCE;

    public static int[] minimumScore = new int[]{0};

    private final static String LOCAL_INDEX_DEFAULT_FIELD = "contents";
    private final static String STACKEXCHANGE_EXCERPTS_URL = "http://api.stackexchange.com/2.2/search/excerpts?";

    private final static String STACKEXCHANGE_PARAMS = "&sort=relevance&tagged=java&site=stackoverflow";

    /**
     * Performs a search using StackExchange API v2.2
     * @param query query string
     * @return error message or null if successful
     */
    public static String stackExchangeSearch(String query) {
        query = query.trim();
        if (query.equals(""))
            return "";

        Search.files = null;

        String body = "";
        try {
            body = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        JSONObject json = URLProcessing.readJsonFromUrlUsingGZIP(STACKEXCHANGE_EXCERPTS_URL + "body=" + body + STACKEXCHANGE_PARAMS);

        if (json == null) {
            return "Connection problems / Query failed";
        }

        if (json.getInt("quota_remaining") == 0) {
            return "Cannot make more requests today";
        }

        JSONArray items = json.getJSONArray("items");
        if (items.length() == 0) {
            return "No results. Consider changing the query " +
                   "(e.g. removing variable names) or using another option";
        }

        files = new File[items.length()];
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = (JSONObject) items.get(i);
            String id = "" + item.getInt(item.getString("item_type") + "_id");
            String content = StringProcessing.textToHTML(item.getString("excerpt")) + "<br/>";
            int score = item.getInt("score");

            files[i] = new File(id, content, score);
        }
        Search.currentSearchType = SearchType.STACKEXCHANGE_API;

        return null;
    }
    /**
     * Performs search by querying app server using RESTful services
     * @param query query string
     * @param posts number of posts to return
     * @return error message or null if successful
     */
    public static String webAppSearch(String query, int posts) {
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

    /**
     * Performs search using a local index
     * @param query query string
     * @param posts number of returned results/posts
     * @return error message or null if successful
     */
    public static String localIndexSearch(String query, int posts) {
        query = query.trim();
        if (query.equals(""))
            return "";

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
        Search.currentSearchType = SearchType.LOCAL_INDEX;

        return null;
    }

    /**
     * Sorts results/files by score
     */
    public static void sortResultsByScore() {
        Arrays.sort(files);
    }

}
