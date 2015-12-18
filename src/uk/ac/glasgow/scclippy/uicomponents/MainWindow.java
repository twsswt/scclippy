package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;
import uk.ac.glasgow.scclippy.lucene.File;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Main Tool Window class
 */
public class MainWindow implements ToolWindowFactory {

    private static int MAIN_SCROLL_STEP = 10;
    static String indexPath = "D:/index";

    public static Editor currentEditor;

    static JComponent searchPanel = new JPanel();
    static JBScrollPane searchPanelScroll = new JBScrollPane(searchPanel);
    public static Posts posts = new Posts();

    static JComponent settingsPanel = new JPanel();

    static File[] files = null;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JTabbedPane tabbedPane = createTabbedPane();
        Component component = toolWindow.getComponent();
        component.getParent().add(tabbedPane);

        initSearchPanel();
    }

    private static JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JBTabbedPane();

        tabbedPane.addTab("Search", null, searchPanelScroll, "Searching by index or using Stackoverflow API");
        tabbedPane.addTab("Settings", null, settingsPanel, "Change settings");

        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        return tabbedPane;
    }

    private static void initSearchPanel() {
        MainWindow.searchPanel.setLayout(new BoxLayout(MainWindow.searchPanel, BoxLayout.PAGE_AXIS));
        searchPanelScroll.getVerticalScrollBar().setUnitIncrement(MAIN_SCROLL_STEP);

        // buttons
        JButton searchStackoverflowButton = new JButton("Search for excerpts in Stackoverflow");
        searchStackoverflowButton.addActionListener(new StackoverflowSearchActionListener(posts));
        JButton searchWithGoogleButton = new JButton("Open browser to search for Stackoverflow posts");
        searchWithGoogleButton.addActionListener(new GoogleSearchActionListener());

        // button panel
        JComponent buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.add(searchStackoverflowButton);
        buttonPanel.add(searchWithGoogleButton);

        // add components to search panel
        MainWindow.searchPanel.add(buttonPanel);
        MainWindow.searchPanel.add(InputPane.inputScrollPane);
        posts.addTo(MainWindow.searchPanel);
    }

}
