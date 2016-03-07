package uk.ac.glasgow.scclippy.uicomponents.search;

import static java.util.Arrays.asList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

import uk.ac.glasgow.scclippy.plugin.editor.IntelijFacade;


/**
 * Double Click Mouse Listener for a Post (JEditorPane)
 */
public class DoubleClickOnPostListener extends MouseAdapter {

    private final static String CODE_START_TAG = "<code>"; // marks snippet's start
    private final static String CODE_END_TAG = "</code>"; // marks snippet's end
    private final static int INPUT_DIALOG_MAX_SNIPPET_LENGTH = 100; // length of snippets in the JOptionPane

    private JEditorPane jEditorPane;

    public DoubleClickOnPostListener(JEditorPane jEditorPane) {
        this.jEditorPane = jEditorPane;
    }

    /**
     * Event handler for double click
     * Inserts selected code into the user's current editor
     * @param e the event
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        // double click
        if (e.getClickCount() < 2) return;

        String text = jEditorPane.getText();
        List<String> codeSnippets = getCodeSnippetsFromText(text);
        if (codeSnippets.size() < 1) return;

        String chosenSnippet = null;
        
        if (codeSnippets.size() == 1) 
        	chosenSnippet = codeSnippets.get(0);
        else if (codeSnippets.size() > 1) 
            chosenSnippet = getUserToChooseASnippet(codeSnippets);
        
        if ((chosenSnippet != null) && (chosenSnippet.length() > 0)) {
            String textToInsert = HTMLtoText(chosenSnippet);
            insertTextIntoEditor(textToInsert);
        }
        
    }

    /**
     * Returns a list of snippets from a string based on start and end tags
     * @param text the input string
     * @return the list of snippets
     */
    private List<String> getCodeSnippetsFromText(String text) {
        List<String> snippets = new LinkedList<>();
        int start, end = 0;
        while ((start = text.indexOf(CODE_START_TAG, end)) != -1 &&
                (end = text.indexOf(CODE_END_TAG, start)) != -1) {
            snippets.add(text.substring(start + CODE_START_TAG.length(), end));
        }

        return snippets;
    }

    private void insertTextIntoEditor(String text) {
    	
        Editor editor = IntelijFacade.getEditor();
        if (editor == null) return;

        Project project = editor.getProject();
        if (project == null)return;

        Document doc = editor.getDocument();
        int offset = editor.getCaretModel().getOffset();

        // Write to file
        ApplicationManager.getApplication().runWriteAction(() -> {
            doc.setText(
                    doc.getText(new TextRange(0, offset)) +
                    "\n" + text +
                    doc.getText(new TextRange(offset, doc.getText().length()))
            );
        });

        // Reformat code
        PsiFile psi = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        int snippetLength = text.length();
        TextRange range = new TextRange(offset, offset + snippetLength);
        ReformatCodeProcessor rfp = new ReformatCodeProcessor(project, psi, range, false);
        rfp.run();
    }


    /**
     * Displays a JOptionPane and asks the user to choose a snippet.
     * @param codeSnippets the available snippet options
     * @return the user's choice of snippet or null if the user cancels.
     */
    private String getUserToChooseASnippet(List<String> codeSnippets) {
    	
    	Object[] possibilities = getSnippetOptions(codeSnippets);
    	
        String result = (String) JOptionPane.showInputDialog(
                jEditorPane,
                "Choose which code snippet:\n",
                "Code snippet",
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                possibilities[0]);
        
        Integer index = asList(possibilities).indexOf(result);
        return codeSnippets.get(index);
    }

    /**
     * Takes a list of snippets and returns a formatted array of options.
     * @param snippets the input snippets
     * @return the options
     */
    private Object[] getSnippetOptions(List<String> snippets) {
        List<Object> snippetOptions = new ArrayList<Object>();
        
        int i = 0;
        for (String snippet: snippets) {
            String snippetPreview = snippet;

            if (snippetPreview.length() > 100)
                snippetPreview = snippetPreview.substring(0, INPUT_DIALOG_MAX_SNIPPET_LENGTH);

        	snippetPreview = (++i) +":"+ snippetPreview;
        	snippetOptions.add(snippetPreview);
        }

        return snippetOptions.toArray();
    }

    /**
     * Performs simple convertion of HTML to text
     * @param s html text to be converted
     * @return the text as a result
     */
    private static String HTMLtoText(String s) {
        s = s.replaceAll("&lt;", "<");
        s = s.replaceAll("&gt;", ">");
        return s;
    }
}