package uk.ac.glasgow.scclippy.plugin;

import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Contains utility methods for processing URLs
 */
public class URLProcessing {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }

        return sb.toString();
    }

    public static JSONObject readJsonFromUrlUsingGZIP(String url) {

        JSONObject json = null;
        try (InputStream is = new URL(url).openStream()) {
            GZIPInputStream gzis = new GZIPInputStream(is);
            String jsonText = readAll(new BufferedReader(new InputStreamReader(gzis)));
            json = new JSONObject(jsonText);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    static JSONObject readJsonFromUrl(String url) {

        JSONObject json = null;
        try (InputStream is = new URL(url).openStream()) {
            String jsonText = readAll(new BufferedReader(new InputStreamReader(is)));
            json = new JSONObject(jsonText);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }
}
