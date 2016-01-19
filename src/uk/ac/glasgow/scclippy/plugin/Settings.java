package uk.ac.glasgow.scclippy.plugin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Settings functionality
 */
public class Settings {

    final static String settingsPath = "D:/scc_settings.txt"; //TODO
    public static String indexPath;
    public static boolean resizable = true;

    public static void saveSettings() {
        java.io.File f = new java.io.File(settingsPath);

        try {
            PrintWriter pw = new PrintWriter(f);
            pw.write(indexPath);
            pw.write("\n");
            pw.write(String.valueOf(resizable));
            pw.write("\n");
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
