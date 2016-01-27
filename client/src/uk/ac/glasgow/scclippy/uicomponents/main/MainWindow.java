package uk.ac.glasgow.scclippy.uicomponents.main;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;
import uk.ac.glasgow.scclippy.uicomponents.history.SearchHistoryTab;
import uk.ac.glasgow.scclippy.uicomponents.search.SearchTab;
import uk.ac.glasgow.scclippy.uicomponents.settings.SettingsTab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Main Tool Window class
 */
public class MainWindow implements ToolWindowFactory {

    JTabbedPane tabsPanel = new JBTabbedPane();

    SearchTab searchTab;
    SearchHistoryTab searchHistoryTab;
    SettingsTab settingsTab;

    /**
     * Creates tool main content
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // init panels
        searchHistoryTab = new SearchHistoryTab();
        searchTab = new SearchTab(searchHistoryTab);
        settingsTab = new SettingsTab(searchTab.getPosts());

        // init tabs
        initTabsPanel();
        Component component = toolWindow.getComponent();
        component.getParent().add(tabsPanel);

        // init mouse selection listener
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null)
            return;

        editor.addEditorMouseListener(new TextSelectionMouseListener());
    }

    /**
     * Creates the tabs panel and the tabs
     * for search, history and settings
     * (and sets a mnemonic for each)
     */
    private void initTabsPanel() {
        tabsPanel = new JBTabbedPane();

        tabsPanel.addTab("Search", null, searchTab.getScroll(), "Searching by index or using Stackoverflow API");
        tabsPanel.addTab("History", null, searchHistoryTab.getScroll(), "View input history");
        tabsPanel.addTab("Settings", null, settingsTab.getScroll(), "Change settings");

        tabsPanel.setMnemonicAt(0, KeyEvent.VK_1);
        tabsPanel.setMnemonicAt(1, KeyEvent.VK_2);
        tabsPanel.setMnemonicAt(2, KeyEvent.VK_3);
    }

}
