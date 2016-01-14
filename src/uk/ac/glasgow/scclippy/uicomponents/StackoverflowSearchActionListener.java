package uk.ac.glasgow.scclippy.uicomponents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

public class StackoverflowSearchActionListener implements ActionListener {

    String EXCERPTS_URL  = "http://api.stackexchange.com/2.2/search/excerpts?";
    String PARAMS = "&sort=relevance&tagged=java&site=stackoverflow";

    private Posts posts;
    private InputPane inputPane;

    StackoverflowSearchActionListener(Posts posts, InputPane inputPane) {
        this.posts = posts;
        this.inputPane = inputPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MainWindow.files = null;

        String body = "";
        try {
            body = URLEncoder.encode(inputPane.inputArea.getText(), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        JSONObject json = readJsonFromUrl(EXCERPTS_URL + "body=" + body + PARAMS);

        if (json == null) {
            posts.update("Query failed");
            return;
        }

        if (json.getInt("quota_remaining") == 0) {
            posts.update("Cannot make more requests today");
            return;
        }

        JSONArray items = json.getJSONArray("items");
        if (items.length() == 0) {
            posts.update(
                    "No results. Consider changing the query " +
                    "(e.g. removing variable names) or using another option"
            );
            return;
        }

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = (JSONObject) items.get(i);
            posts.update(i, item.getString("excerpt"), item.getInt(item.getString("item_type") + "_id"));
        }
        Search.currentSearchType = Search.SearchType.API;
    }

    private static String readAll(Reader rd) throws IOException {

        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }

        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url) {

        JSONObject json = null;
        try (InputStream is = new URL(url).openStream()) {

            GZIPInputStream gzis = new GZIPInputStream(is);
            InputStreamReader reader = new InputStreamReader(gzis);
            BufferedReader rd = new BufferedReader(reader);

            String jsonText = readAll(rd);
            json = new JSONObject(jsonText);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }
}
