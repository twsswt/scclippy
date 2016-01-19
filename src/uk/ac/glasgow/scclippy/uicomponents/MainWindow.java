package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;
import uk.ac.glasgow.scclippy.actions.TextSelectionListener;
import uk.ac.glasgow.scclippy.lucene.File;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Main Tool Window class
 */
public class MainWindow implements ToolWindowFactory {

    static SearchTab search;
    static SearchHistoryTab history;
    static SettingsTab settings;

    static JTabbedPane tabbedPane = new JBTabbedPane();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JTabbedPane tabbedPane = createTabbedPane();
        Component component = toolWindow.getComponent();
        component.getParent().add(tabbedPane);

        SearchTab.initSearchPanel();
        SearchHistoryTab.initHistoryPanel();
        SettingsTab.initSettingsPanel();

        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null)
            return;
        editor.getSelectionModel().addSelectionListener(new TextSelectionListener());
    }

    private static JTabbedPane createTabbedPane() {
        tabbedPane.addTab("Search", null, SearchTab.searchPanelScroll, "Searching by index or using Stackoverflow API");
        tabbedPane.addTab("History", null, SearchHistoryTab.historyPanelScroll, "View input history");
        tabbedPane.addTab("Settings", null, SettingsTab.settingsPanelScroll, "Change settings");

        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        return tabbedPane;
    }

}
