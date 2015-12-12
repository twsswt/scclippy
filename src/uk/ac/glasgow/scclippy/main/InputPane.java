package uk.ac.glasgow.scclippy.main;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import uk.ac.glasgow.scclippy.lucene.SearchFiles;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InputPane {

    private static int INPUT_TEXT_AREA_ROWS = 5; //TODO make this auto resizable

    public static JTextArea inputPane = new JTextArea();
    static JScrollPane inputScrollPane = new JBScrollPane(inputPane);

    InputPane() {
        inputPane.setLineWrap(true);
        inputPane.setWrapStyleWord(true);
        inputPane.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.CYAN));
        inputPane.setRows(INPUT_TEXT_AREA_ROWS);
        inputPane.getDocument().addDocumentListener(new InputPaneListener());
    }

    private static class InputPaneListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            searchAction();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            searchAction();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // not fired
        }

        private void searchAction() {
            String text = inputPane.getText();
            if (text.equals(""))
                return;

            try {
                MainWindow.files = SearchFiles.search(
                        MainWindow.indexPath,
                        "contents",
                        text,
                        MainWindow.queryNumber
                );
            } catch (Exception e2) {
                /* TODO: Show intellij notification for failure */
                System.err.println(e2.getMessage());
            }
            MainWindow.posts.update(MainWindow.files);
        }
    }
}
