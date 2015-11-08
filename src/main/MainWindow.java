package main;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import lucene.SearchFiles;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main Tool Window class
 */
public class MainWindow implements ToolWindowFactory {

    public static int queryNumber = 5; // TODO make input for this
    public static JTextArea input = new JTextArea("", 5, 50);
    public static JTextArea[] output = new JTextArea[queryNumber];
    private static String indexPath = "D:/sccindex/index";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        // TODO add select index path & data option
        // IndexFiles.index(new String[] {"-index", "D:/sccindex/index", "-docs", "D:/sccdata", "-update"});

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JScrollPane panelScroll = new JBScrollPane(panel);
        panelScroll.getVerticalScrollBar().setUnitIncrement(10);

        Component component = toolWindow.getComponent();
        component.getParent().add(panelScroll);

        input.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.CYAN));
        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JBScrollPane(input);
        panel.add(inputScrollPane);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String[] filesContent = null;
                        try {
                            filesContent = SearchFiles.search(new String[]{
                                    indexPath, "contents", input.getText(), "5"
                            });
                        } catch (Exception e) { /* TODO: Show intellij notification for failure */ }

                        if (filesContent == null)
                            return;

                        for (int i = 0; i < filesContent.length; i++) {
                            String text = filesContent[i];
                            output[i].setText(text);
                            try {
                                int start, end = 0;
                                String startText = "<code>";
                                String endText = "</code>";
                                while ((start = text.indexOf(startText, end)) != -1 &&
                                        (end = text.indexOf(endText, start)) != -1) {
                                    output[i].getHighlighter().addHighlight(start + startText.length(), end, DefaultHighlighter.DefaultPainter);
                                }

                            } catch (BadLocationException e2) {
                                //TODO WHAT EXACTLY?
                            }
                        }
                        for (int i = filesContent.length; i < output.length; i++) {
                            output[i].setText("");
                        }
                    }

                }
        );
        panel.add(searchButton);

        JScrollPane[] outputScrollPane = new JBScrollPane[output.length];

        for (int i = 0; i < output.length; i++) {
            output[i] = new JTextArea("", 5, 100);
            output[i].setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.YELLOW));
            output[i].setLineWrap(true);
            output[i].setWrapStyleWord(true);
            outputScrollPane[i] = new JBScrollPane(output[i]);
            panel.add(outputScrollPane[i]);
        }
    }


}
