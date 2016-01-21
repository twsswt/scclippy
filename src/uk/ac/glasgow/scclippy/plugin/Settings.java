package uk.ac.glasgow.scclippy.plugin;

import uk.ac.glasgow.scclippy.uicomponents.Posts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

/**
 * Settings functionality
 */
public class Settings {

    final static String settingsPath = "D:/scc_settings.txt";
    public static String indexPath;
    public static boolean resizable = true;
    public static String[] webServiceURI = new String[]{"http://localhost:8080/scc/rest/search/"};

    /**
     * Saves the current settings
     */
    public static void saveSettings() {
        java.io.File f = new java.io.File(settingsPath);

        try {
            PrintWriter pw = new PrintWriter(f);
            pw.write(indexPath);
            pw.write("\n");
            pw.write(String.valueOf(resizable));
            pw.write("\n");
            pw.write(webServiceURI[0]);
            pw.write("\n");
            pw.write(String.valueOf(Posts.defaultPostCount[0]));
            pw.write("\n");
            pw.write(String.valueOf(Posts.maxPostCount[0]));
            pw.write("\n");
            pw.write(String.valueOf(Posts.textColour));
            pw.write("\n");
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads settings from a file
     */
    public static void loadSettings() {
        java.io.File f = new java.io.File(settingsPath);

        if (!f.exists()) {
            try {
                if (!f.createNewFile())
                    return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            Scanner sc = new Scanner(f);
            if (sc.hasNextLine()) {
                indexPath = sc.nextLine();
            }
            if (sc.hasNextLine()) {
                resizable = Boolean.parseBoolean(sc.nextLine());
            }
            if (sc.hasNextLine()) {
                webServiceURI[0] = sc.nextLine();
            }
            if (sc.hasNextLine()) {
                Posts.defaultPostCount[0] = Integer.parseInt(sc.nextLine());
            }
            if (sc.hasNextLine()) {
                Posts.maxPostCount[0] = Integer.parseInt(sc.nextLine());
            }
            if (sc.hasNextLine()) {
                Posts.textColour = sc.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
