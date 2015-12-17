package uk.ac.glasgow.scclippy.main;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import uk.ac.glasgow.scclippy.lucene.File;

import javax.swing.*;
import java.awt.*;

/**
 * Main Tool Window class
 */
public class MainWindow implements ToolWindowFactory {

    private static int MAIN_SCROLL_STEP = 10;
    static int queryNumber = 5; // TODO make inputPane for this
    static String indexPath = "D:/index";

    public static Editor currentEditor;

    static JPanel mainPanel = new JPanel();
    static JScrollPane mainPanelScroll = new JBScrollPane(mainPanel);
    public static Posts posts = new Posts(queryNumber);

    static File[] files = null;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        // TODO add select and create index options
        // IndexFiles.index(new String[] {"-index", "D:/sccindex/index", "-docs", "D:/sccdata", "-update"});

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanelScroll.getVerticalScrollBar().setUnitIncrement(MAIN_SCROLL_STEP);

        JButton button = new JButton("Search Stackoverflow (online)");
        button.addActionListener(new StackoverflowSearchActionListener(posts));

        Component component = toolWindow.getComponent();
        component.getParent().add(mainPanelScroll);

        mainPanel.add(button);
        mainPanel.add(InputPane.inputScrollPane);
        posts.addTo(mainPanel);
    }
}
