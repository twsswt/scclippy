package uk.ac.glasgow.scclippy.lucene;

import java.nio.file.Paths;

public class File {

    private String content;
    private String filename;

    private final static String FILE_TYPE = ".txt";

    public File(String content) {
        this.content = content;
    }

    public File(String filename, String content) {
        this(content);
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Returns the real filename from a filepath
     * "x/y/z/123-456.txt" returns '456' which is the answer id
     * "x/y/z/123.txt" returns '123' which is the question id
     * @param filepath the filepath
     * @return the filename
     */
    public static String getFilenameFromFilepath(String filepath) {
        String filename = Paths.get(filepath).getFileName().toString();
        String[] questionAndAnswer = filename.split("-");
        String questionOrAnswer = questionAndAnswer[questionAndAnswer.length - 1];

        filename = questionOrAnswer.substring(0, questionOrAnswer.length() - FILE_TYPE.length());
        return filename;
    }

}
