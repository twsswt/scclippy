package uk.ac.glasgow.scclippy.plugin;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;

/**
 * Editor
 */
public class Editor {

    public static com.intellij.openapi.editor.Editor getEditor() {
        DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
        if (dataContext == null)
            return null;

        Project project = DataKeys.PROJECT.getData(dataContext);
        if (project == null)
            return null;

        com.intellij.openapi.editor.Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null)
            return null;

        return editor;
    }

}
