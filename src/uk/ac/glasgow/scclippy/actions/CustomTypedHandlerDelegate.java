package uk.ac.glasgow.scclippy.actions;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import uk.ac.glasgow.scclippy.main.InputPane;
import uk.ac.glasgow.scclippy.main.MainWindow;
import org.jetbrains.annotations.NotNull;

public class CustomTypedHandlerDelegate extends TypedHandlerDelegate {

    char[] triggerSearchChars = new char[] {
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
                    InputPane.inputPane.setText(file.getText().substring(last + 1, offset).trim());
//                    MainWindow.searchButton.doClick();
                }
                break;
            }
        }
        return Result.CONTINUE;
    }

}
