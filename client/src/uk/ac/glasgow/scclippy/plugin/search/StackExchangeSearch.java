package uk.ac.glasgow.scclippy.plugin.search;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.glasgow.scclippy.plugin.lucene.File;
import uk.ac.glasgow.scclippy.plugin.util.StringProcessing;
import uk.ac.glasgow.scclippy.plugin.util.URLProcessing;

import java.net.URLEncoder;

/**
 * Class for searching with a Stack Exchange API for code excerpts
 */
public class StackExchangeSearch extends Search {

    private final static String EXCERPTS_URL = "http://api.stackexchange.com/2.2/search/excerpts?";
    private final static String EXCERPTS_PARAMS = "&sort=relevance&tagged=java&site=stackoverflow";

    private static int remainingCalls;

    /**
     * Performs a search using StackExchange API v2.2
     * @See Search#search
     */
    public void search(String query, int posts) throws Exception {
        query = query.trim();
        if (query.equals("")) {
            files = null;
            return;
        }

        String body = URLEncoder.encode(query, "UTF-8");
        JSONObject json = URLProcessing.readJsonFromUrlUsingGZIP(EXCERPTS_URL + "body=" + body + EXCERPTS_PARAMS);

        if (json == null) {
            throw new Exception("Query failed. Check connection to server.");
        }

        if (json.getInt("quota_remaining") == 0) {
            throw new Exception("Cannot make more requests today");
        }

        remainingCalls = json.getInt("quota_remaining");
        JSONArray items = json.getJSONArray("items");
        if (items.length() == 0) {
            throw new Exception("No results. Consider removing variable names or using another option");
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
    }

    public static int getRemainingCalls() {
        return remainingCalls;
    }

}
