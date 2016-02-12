import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CSVSplitter {

    public static void main(String[] args) throws Exception {

        String delimiter = "%&#@";
        int cols = 4;
        String inputPath = "";
        String outputPath = "";

        try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
            String line;
            StringBuilder lines = new StringBuilder();
            List<String> list = new ArrayList<>(cols);

            while ((line = br.readLine()) != null) {
                lines.append(line);
                lines.append("\n");

                int index;
                while ((index = lines.indexOf(delimiter)) < 0) {

                    list.add(lines.substring(0, index));
                    lines.delete(0, index + delimiter.length());

                    if (list.size() != cols) {
                        continue;
                    }

                    String qid = list.get(1);
                    String aid = list.get(2);
                    String body = list.get(3);
                    String score = list.get(0);

                    list.clear();

                    File f = new File(outputPath, qid.trim() + "#" + aid.trim() + "#" + score.trim() + ".txt");
                    if (f.exists() || f.createNewFile()) {
                        FileOutputStream stream = new FileOutputStream(f, false);
                        stream.write(body.getBytes());
                        stream.close();
                    }
                }
            }
        }

        File f = new File(outputPath);
        File[] files = f.listFiles();
        if (files != null) {
            System.out.println("\n======\n" + files.length);
        }
    }
}
