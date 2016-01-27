package uk.ac.glasgow.scclippy.uicomponents.main;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import uk.ac.glasgow.scclippy.uicomponents.search.SearchTab;

/**
 * Handles user key input
 */
public class KeyInput extends TypedHandlerDelegate {

    // Special characters that trigger search
    private static char[] triggerSearchChars = new char[] {
      ' ', '.', ';'
    };

    @Override
    public Result charTyped(char c, Project project, @NotNull Editor editor, @NotNull PsiFile file) {

        for (char trigger : triggerSearchChars) {
            if (c == trigger) {
                int offset = editor.getCaretModel().getCurrentCaret().getOffset();
                if (offset >= 1) {
                    int lastCommand = file.getText().lastIndexOf(';', offset - 1);
                    int lastBrace = file.getText().lastIndexOf('{', offset - 1);

                    // set scope from last command or brace
                    int last;
                    if (lastCommand > lastBrace) {
                        last = lastCommand;
                    } else if (lastBrace > lastCommand) {
                        last = lastBrace;
                    } else {
                        break;
                    }
                    // update query input
                    SearchTab.inputPane.inputArea.setText(file.getText().substring(last + 1, offset).trim());
                }
                break;
            }
        }
        return Result.CONTINUE;
    }

}
