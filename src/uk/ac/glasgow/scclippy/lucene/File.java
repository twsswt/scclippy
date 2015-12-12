package uk.ac.glasgow.scclippy.lucene;

import java.nio.file.Paths;

public class File {

    private String filepath;
    private String content;
    private String fileType = ".txt";

    File(String filepath, String content) {
        this.filepath = filepath;
        this.content = content;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getFileName() {
        if (filepath == null) {
            return null;
        }
        String filename = Paths.get(filepath).getFileName().toString();
        String[] questionAndAnswer = filename.split("-");
        String questionOrAnswer = questionAndAnswer[questionAndAnswer.length - 1];

        return questionOrAnswer.substring(0, questionOrAnswer.length() - fileType.length());
    }

    public String getContent() {
        return content;
    }

}
