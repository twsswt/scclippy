package uk.ac.glasgow.scclippy.actions;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import uk.ac.glasgow.scclippy.uicomponents.MainWindow;
import org.jetbrains.annotations.NotNull;
import uk.ac.glasgow.scclippy.uicomponents.Search;

public class CustomTypedHandlerDelegate extends TypedHandlerDelegate {

    private static char[] triggerSearchChars = new char[] {
      ' ', '.', ';'
    };

    @Override
    public Result charTyped(char c, Project project, @NotNull Editor editor, @NotNull PsiFile file) {

        for (char trigger : triggerSearchChars) {
            if (c == trigger) {
                MainWindow.currentEditor = editor;
                int offset = editor.getCaretModel().getCurrentCaret().getOffset();
                if (offset >= 1) {
                    int lastCommand = file.getText().lastIndexOf(';', offset - 1);

                    int lastBrace = file.getText().lastIndexOf('{', offset - 1);

                    int last;
                    if (lastCommand > lastBrace) {
                        last = lastCommand;
                    } else if (lastBrace > lastCommand) {
                        last = lastBrace;
                    } else {
                        break;
                    }
                    Search.inputPane.inputArea.setText(file.getText().substring(last + 1, offset).trim());
                }
                break;
            }
        }
        return Result.CONTINUE;
    }

}
