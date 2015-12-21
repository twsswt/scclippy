package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;
import uk.ac.glasgow.scclippy.actions.SearchAction;
import uk.ac.glasgow.scclippy.lucene.File;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Main Tool Window class
 */
public class MainWindow implements ToolWindowFactory {

    public static Editor currentEditor;

    static Search search;
    static Settings settings;

    static File[] files = null;

    static JTabbedPane tabbedPane = new JBTabbedPane();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JTabbedPane tabbedPane = createTabbedPane();
        Component component = toolWindow.getComponent();
        component.getParent().add(tabbedPane);

        Search.initSearchPanel();
        Settings.initSettingsPanel();
    }

    private static JTabbedPane createTabbedPane() {

        tabbedPane.addTab("Search", null, Search.searchPanelScroll, "Searching by index or using Stackoverflow API");
        tabbedPane.addTab("Settings", null, Settings.settingsPanelScroll, "Change settings");

        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        return tabbedPane;
    }

}
