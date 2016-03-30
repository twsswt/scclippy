package uk.ac.glasgow.scclippy.uicomponents.main;

import org.jetbrains.annotations.NotNull;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import uk.ac.glasgow.scclippy.plugin.search.SearchController;
import uk.ac.glasgow.scclippy.uicomponents.search.QueryInputPane;

/**
 * Handles user key input
 */
public class KeyInput extends TypedHandlerDelegate {
	
    // Special characters that trigger search
    private static char[] triggerSearchChars = new char[] {
      ' ', '.', ';'
    };
	
	private final QueryInputPane queryInputPane;

	private final SearchController searchController;
	
	public KeyInput (QueryInputPane queryInputPane, SearchController searchController){
		this.queryInputPane = queryInputPane;
		this.searchController = searchController;
	}

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
                    
                    String queryText =
                    	file.getText().substring(last + 1, offset).trim();
                    
                    queryInputPane.setQueryText(queryText);
                }
                break;
            }
        }
        return Result.CONTINUE;
    }

}
