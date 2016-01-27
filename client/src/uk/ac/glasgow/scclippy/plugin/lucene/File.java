package uk.ac.glasgow.scclippy.plugin.lucene;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

public class File implements Comparable<File> {

    private String content;
    private String fileName;
    private int score;

    /**
     * Constructor
     *
     * @param fileName name of the file
     * @param content  content of the file
     * @param score    the score/upvotes of the file
     */
    public File(String fileName, String content, int score) {
        this.fileName = fileName;
        this.content = content;
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public String getFileName() {
        return fileName;
    }

    public int getScore() {
        return score;
    }

    /**
     * Initializes the fileName from a filepath
     * @param path    the filepath
     * @param content the content of the file
     */
    public static File getNewFileFromPath(String path, String content) {
        String filename = Paths.get(path).getFileName().toString();
        String[] field = filename.substring(0, filename.length() - ".txt".length()).split("#");

        try {
            if (field.length == 2) {
                return new File(field[0], content, Integer.parseInt(field[1]));
            } else if (field.length == 3) {
                return new File(field[0] + "#" + field[1], content, Integer.parseInt(field[2]));
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return new File("", "", 0);
    }

    @Override
    public int compareTo(@NotNull File otherFile) {
        return otherFile.getScore() - score;
    }
}
