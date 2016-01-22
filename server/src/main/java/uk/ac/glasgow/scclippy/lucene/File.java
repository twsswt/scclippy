package uk.ac.glasgow.scclippy.lucene;

import java.nio.file.Paths;

public class File {

    private String content;
    private String fileName;
    
    /**
     * Constructor
     * @param fileName name of the file
     * @param content content of the file
     */
    public File(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * Initializes the fileName from a filepath
     * "x/y/z/123-456.txt" -> '456' which is the answer id
     * "x/y/z/123.txt" -> '123' which is the question id
     * @param path the filepath
     * @param content the content of the file
     */
    public static File getNewFileFromPath(String path, String content) {
        String filename = Paths.get(path).getFileName().toString();
        String[] field = filename.substring(0, filename.length() - ".txt".length()).split("-");

        if (field.length == 1) {
            return new File(field[0], content);
        } else if (field.length == 2) {
            return new File(field[0] + "#" + field[1], content);
        }
        return new File("", "");
    }
}
