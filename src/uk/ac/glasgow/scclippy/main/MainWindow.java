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

    static JPanel resultsPanel = new JPanel();
    static JScrollPane resultsPanelScroll = new JBScrollPane(resultsPanel);
    public static Posts posts;

    static File[] files = null;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        // TODO add select and create index options
        // IndexFiles.index(new String[] {"-index", "D:/sccindex/index", "-docs", "D:/sccdata", "-update"});

        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.PAGE_AXIS));
        resultsPanelScroll.getVerticalScrollBar().setUnitIncrement(MAIN_SCROLL_STEP);

        Component component = toolWindow.getComponent();
        component.getParent().add(resultsPanelScroll);

        resultsPanel.add(InputPane.inputScrollPane);

        posts = new Posts(queryNumber, toolWindow);
        posts.addTo(resultsPanel);
    }
}
