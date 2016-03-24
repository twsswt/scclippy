package uk.ac.glasgow.scclippy.plugin.search;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;
import uk.ac.glasgow.scclippy.plugin.util.URLProcessing;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for searching with a Stackoverflow API v2.2
 */
public class StackoverflowJSONAPISearch implements StackoverflowSearch {

    private final static String EXCERPTS_URL = "http://api.stackexchange.com/2.2/search/excerpts?";
    private final static String EXCERPTS_PARAMS = "&sort=relevance&tagged=java&site=stackoverflow";

    private int remainingCalls;

    @Override
    public List<StackoverflowEntry> searchIndex(@NotNull String query, int posts) throws SearchException  {

        String body;
		try {
			body = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new SearchException(e);
		}
        JSONObject json = URLProcessing.readJsonFromUrlUsingGZIP(EXCERPTS_URL + "body=" + body + EXCERPTS_PARAMS);

        if (json == null)
            throw new SearchException("Query failed. Check connection to server.");
        

        if (json.getInt("quota_remaining") == 0) {
            throw new SearchException("Cannot make more requests today");
        }

        remainingCalls = json.getInt("quota_remaining");
        
        JSONArray items = json.getJSONArray("items");

        List<StackoverflowEntry> result = new ArrayList<StackoverflowEntry>();

        for (int i = 0; i < items.length() && i < posts; i++) {
            JSONObject item = (JSONObject) items.get(i);
            
            String id = "" + item.getInt(item.getString("item_type") + "_id");
            String content = textToHTML(item.getString("excerpt")) + "<br/>";
            int score = item.getInt("score");

            StackoverflowEntry entry = new StackoverflowEntry(id, null ,score, content);
            result.add(entry);
        }
        
        return result;
    }

    public int getRemainingCalls() {
        return remainingCalls;
    }

    /**
     * Converts text to HTML by adding breaks and nbsp
     *
     * @param snippetText the text
     * @return the html variant of the text
     */
    public static String textToHTML(String snippetText) {
        snippetText = snippetText.replaceAll("\n", "<br/>");
        snippetText = snippetText.replaceAll(" ", "&nbsp ");

        return snippetText;
    }
}
