package uk.ac.glasgow.main;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import uk.ac.glasgow.lucene.File;
import uk.ac.glasgow.lucene.SearchFiles;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * Main Tool Window class
 */
public class MainWindow implements ToolWindowFactory {

    private static int MAIN_SCROLL_STEP = 10;
    private static int INPUT_TEXT_AREA_ROWS = 5; //TODO make this auto resizable

    static int queryNumber = 5; // TODO make input for this

    private static String indexPath = "D:/index";

    public static JTextArea input = new JTextArea();
    public static JEditorPane[] output = new JEditorPane[queryNumber];
    JScrollPane[] outputScrollPane = new JBScrollPane[output.length];

    public static JButton searchButton = new JButton("Search");

    public static Editor currentEditor;

    JPanel resultPanel = new JPanel();
    JScrollPane resultPanelScroll = new JBScrollPane(resultPanel);

    File[] files = null;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        // TODO add select and create index options
        // IndexFiles.index(new String[] {"-index", "D:/sccindex/index", "-docs", "D:/sccdata", "-update"});

        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.PAGE_AXIS));
        resultPanelScroll.getVerticalScrollBar().setUnitIncrement(MAIN_SCROLL_STEP);

        Component component = toolWindow.getComponent();
        component.getParent().add(resultPanelScroll);

        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.CYAN));
        input.setRows(INPUT_TEXT_AREA_ROWS);

        JScrollPane inputScrollPane = new JBScrollPane(input);
        resultPanel.add(inputScrollPane);

        searchButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        try {
                            files = SearchFiles.search(new String[]{
                                    indexPath, "contents", input.getText(), String.valueOf(queryNumber)
                            });
                        } catch (Exception e) {
                            /* TODO: Show intellij notification for failure */
                            System.err.println(e.getMessage());
                        }

                        if (files == null)
                            return;

                        for (int i = 0; i < files.length; i++) {
                            String text = files[i].getContent();
                            String url = "<a href=\"http://stackoverflow.com/questions/"
                                    + files[i].getFileName()
                                    + "\">Link to Stackoverflow</a>";
                            output[i].setText(text + url);
                            output[i].setEnabled(true);
                            output[i].addHyperlinkListener(new HyperlinkListener() {
                                public void hyperlinkUpdate(HyperlinkEvent e) {
                                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                        if (Desktop.isDesktopSupported()) {
                                            try {
                                                Desktop.getDesktop().browse(e.getURL().toURI());
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            } catch (URISyntaxException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            });
                            outputScrollPane[i].repaint();
                        }
                        for (int i = files.length; i < output.length; i++) {
                            output[i].setText("");
                        }
                    }
                }
        );
        resultPanel.add(searchButton);

        for (int i = 0; i < output.length; i++) {
            output[i] = new JEditorPane("text/html", "");
            output[i].setEditable(false);
            output[i].setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.YELLOW));
            HTMLEditorKit kit = new HTMLEditorKit();
            output[i].setEditorKit(kit);
            kit.getStyleSheet().addRule("code {background-color: olive;}");

            output[i].addMouseListener(new SelectedSnippetListener(i, toolWindow));

            outputScrollPane[i] = new JBScrollPane(output[i]);
            outputScrollPane[i].setPreferredSize(new Dimension(1000, 100));
            resultPanel.add(outputScrollPane[i]);
        }
    }

    private class SelectedSnippetListener extends MouseAdapter {
        int id;
        ToolWindow toolWindow;

        public SelectedSnippetListener(int id, ToolWindow toolWindow) {
            this.id = id;
            this.toolWindow = toolWindow;
        }

        @Override
        public void mouseClicked(MouseEvent e) {

            // double click
            if (e.getClickCount() == 2 && files != null && files[id] != null) {
                String text = files[id].getContent();
                int start, end = 0;
                String startText = "<code>";
                String endText = "</code>";

                List<String> snippets = new LinkedList<>();
                while ((start = text.indexOf(startText, end)) != -1 &&
                        (end = text.indexOf(endText, start)) != -1) {
                    snippets.add(text.substring(start + startText.length(), end));
                }

                if (snippets.size() == 1) {
                    // insert directly
                    ApplicationManager.getApplication().runWriteAction(new Runnable() { //TODO
                        @Override
                        public void run() {

                            Document doc = currentEditor.getDocument();
                            int offset = currentEditor.getCaretModel().getOffset();
                            doc.setText(
                                    doc.getText(new TextRange(0, offset)) +
                                            snippets.get(0) +
                                            doc.getText(new TextRange(offset, doc.getText().length()))
                            );
                        }
                    });
                } else if (snippets.size() > 1) {
                    // ask user for input
                    JPanel panel = new JPanel();

                    JLabel snippetsLabel = new JLabel();
                    panel.add(snippetsLabel);

                    Object[] possibilities = new Object[snippets.size()];
                    for (int i = 0; i < snippets.size(); i++) {
                        possibilities[i] = snippets.get(i);
                    }

                    String chosenSnippet = (String)JOptionPane.showInputDialog(
                            resultPanel,
                            "Choose which code snippet:\n",
                            "Code snippet",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            possibilities,
                            possibilities[0]);

                    if ((chosenSnippet != null) && (chosenSnippet.length() > 0)) {
                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
                            @Override
                            public void run() {

                                Document doc = currentEditor.getDocument();
                                int offset = currentEditor.getCaretModel().getOffset();
                                doc.setText(
                                        doc.getText(new TextRange(0, offset)) +
                                                chosenSnippet +
                                                doc.getText(new TextRange(offset, doc.getText().length()))
                                );
                            }
                        });
                    }

                }
            }
        }
    }
}
