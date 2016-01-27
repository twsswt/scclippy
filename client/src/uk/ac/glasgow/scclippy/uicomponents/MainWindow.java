package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;
import uk.ac.glasgow.scclippy.actions.TextSelectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Main Tool Window class
 */
public class MainWindow implements ToolWindowFactory {

    static JTabbedPane tabsPanel;

    /**
     * Creates tool window content
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        initTabsPanel();
        Component component = toolWindow.getComponent();
        component.getParent().add(tabsPanel);

        // init panels
        SearchTab.initSearchPanel();
        SearchHistoryTab.initHistoryPanel();
        SettingsTab.initSettingsPanel();

        // init mouse selection listener
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null)
            return;

        editor.addEditorMouseListener(new TextSelectionListener());
    }

    /**
     * Creates the tabs panel and the tabs
     * for search, history and settings
     * (and sets a mnemonic for each)
     */
    private static void initTabsPanel() {
        tabsPanel = new JBTabbedPane();

        tabsPanel.addTab("Search", null, SearchTab.searchPanelScroll, "Searching by index or using Stackoverflow API");
        tabsPanel.addTab("History", null, SearchHistoryTab.historyPanelScroll, "View input history");
        tabsPanel.addTab("Settings", null, SettingsTab.settingsPanelScroll, "Change settings");

        tabsPanel.setMnemonicAt(0, KeyEvent.VK_1);
        tabsPanel.setMnemonicAt(1, KeyEvent.VK_2);
        tabsPanel.setMnemonicAt(2, KeyEvent.VK_3);
    }

}
