package uk.ac.glasgow.scclippy.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import uk.ac.glasgow.scclippy.uicomponents.InputPane;
import uk.ac.glasgow.scclippy.uicomponents.MainWindow;

public class SearchAction extends AnAction {

    @Override
    public void actionPerformed(final AnActionEvent anActionEvent) {

        // Get all the required data from data keys
        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        MainWindow.currentEditor = editor;

        // Access document, caret, and selection
        final Document document = editor.getDocument();

        final SelectionModel selectionModel = editor.getSelectionModel();

        final int start = selectionModel.getSelectionStart();
        final int end = selectionModel.getSelectionEnd();

        // New instance of Runnable to make a replacement
        Runnable runnable = () -> {
            String newText = document.getText(new TextRange(start, end));
            InputPane.inputPane.setText(newText);
        };

        // Making the replacement
        WriteCommandAction.runWriteCommandAction(project, runnable);
        selectionModel.removeSelection();
    }

    @Override
    public void update(final AnActionEvent e) {

        // Get required data keys
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);

        // Set visibility only in case of existing project and editor and if some text in the editor is selected
        e.getPresentation().setVisible((project != null && editor != null && editor.getSelectionModel().hasSelection()));
    }
}