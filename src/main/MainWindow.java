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
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main Tool Window class
 */
public class MainWindow implements ToolWindowFactory {

    public static int queryNumber = 5; // TODO make input for this
    private static String indexPath = "D:/sccindex/index";

    public static JTextArea input = new JTextArea("", 5, 50);

    public static JEditorPane[] output = new JEditorPane[queryNumber];
    public static JButton searchButton = new JButton("Search");

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        // TODO add select index path & data option?
        // IndexFiles.index(new String[] {"-index", "D:/sccindex/index", "-docs", "D:/sccdata", "-update"});

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JScrollPane panelScroll = new JBScrollPane(panel);
        panelScroll.getVerticalScrollBar().setUnitIncrement(10);

        Component component = toolWindow.getComponent();
        component.getParent().add(panelScroll);

        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.CYAN));

        JScrollPane inputScrollPane = new JBScrollPane(input);
        panel.add(inputScrollPane);

        searchButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String[] filesContent = null;
                        try {
                            filesContent = SearchFiles.search(new String[]{
                                    indexPath, "contents", input.getText(), String.valueOf(queryNumber)
                            });
                        } catch (Exception e) {
                            /* TODO: Show intellij notification for failure */
                            System.err.println(e.getMessage());
                        }

                        if (filesContent == null)
                            return;

                        for (int i = 0; i < filesContent.length; i++) {
                            String text = filesContent[i];
                            output[i].setText(text);
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
            output[i] = new JEditorPane("text/html", "<span style=\"background-color: yellow\">Result " + (i + 1) + "</span>");
            output[i].setEditable(false);
            output[i].setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.YELLOW));
            HTMLEditorKit kit = new HTMLEditorKit();
            output[i].setEditorKit(kit);
            kit.getStyleSheet().addRule("code {background-color: olive;}");
            outputScrollPane[i] = new JBScrollPane(output[i]);
            panel.add(outputScrollPane[i]);
        }
    }


}
