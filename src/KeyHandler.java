import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by User on 7.10.2015 ?..
 */
public class KeyHandler implements TypedActionHandler {

    @Override
    public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
//        final Document document = editor.getDocument();
//        Project project = editor.getProject();
//
//        final char ch = c;
//        final Editor finalEditor = editor;
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                MainWindow.keyListenerOutcome.setText(MainWindow.keyListenerOutcome.getText() + ch);
//                int offset = finalEditor.getCaretModel().getOffset();
//                document.insertString(offset, "" + ch);
//                finalEditor.getCaretModel().moveToOffset(offset + 1);
//            }
//        };
//        WriteCommandAction.runWriteCommandAction(project, runnable);
    }
}
