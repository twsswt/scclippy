package uk.ac.glasgow.scclippy.main;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import uk.ac.glasgow.scclippy.lucene.File;
import uk.ac.glasgow.scclippy.lucene.SearchFiles;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Main Tool Window class
 */
public class MainWindow implements ToolWindowFactory {

    private static int MAIN_SCROLL_STEP = 10;
    private static int INPUT_TEXT_AREA_ROWS = 5; //TODO make this auto resizable
    static int queryNumber = 5; // TODO make inputPane for this
    private static String indexPath = "D:/index";

    public static Editor currentEditor;

    static JPanel resultsPanel = new JPanel();
    static JScrollPane resultsPanelScroll = new JBScrollPane(resultsPanel);
    public static JTextArea inputPane = new JTextArea();
    public static Posts posts;
    public static JButton searchButton = new JButton("Search");

    static File[] files = null;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        // TODO add select and create index options
        // IndexFiles.index(new String[] {"-index", "D:/sccindex/index", "-docs", "D:/sccdata", "-update"});

        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.PAGE_AXIS));
        resultsPanelScroll.getVerticalScrollBar().setUnitIncrement(MAIN_SCROLL_STEP);

        Component component = toolWindow.getComponent();
        component.getParent().add(resultsPanelScroll);

        inputPane.setLineWrap(true);
        inputPane.setWrapStyleWord(true);
        inputPane.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.CYAN));
        inputPane.setRows(INPUT_TEXT_AREA_ROWS);

        JScrollPane inputScrollPane = new JBScrollPane(inputPane);
        resultsPanel.add(inputScrollPane);

        posts = new Posts(queryNumber, toolWindow);

        searchButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        try {
                            files = SearchFiles.search(new String[]{
                                    indexPath, "contents", inputPane.getText(), String.valueOf(queryNumber)
                            });
                        } catch (Exception e) {
                            /* TODO: Show intellij notification for failure */
                            System.err.println(e.getMessage());
                        }

                        if (files == null)
                            return;

                        posts.update(files);
                    }
                }
        );
        resultsPanel.add(searchButton);
        posts.addTo(resultsPanel);
    }


}
