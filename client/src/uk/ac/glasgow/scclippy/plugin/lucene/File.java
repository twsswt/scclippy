package uk.ac.glasgow.scclippy.plugin.lucene;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

public class File implements Comparable<File> {

    private String content;
    private String fileName;
    private String path;
    private int score;

    /**
     * Constructor
     *
     * @param fileName name of the file
     * @param content  content of the file
     * @param score    the score/upvotes of the file
     */
    public File(String fileName, String content, Integer score) {
        this.fileName = fileName;
        this.content = content;
        this.score = score;
    }

    public File(String fileName, String content, String path) {
        this.fileName = fileName;
        this.content = content;
        this.path = path;
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
        return new File(filename, content, path);
    }

    @Override
    public int compareTo(@NotNull File otherFile) {
        return otherFile.getScore() - score;
    }

    public String getPath() {
        return path;
    }
}
