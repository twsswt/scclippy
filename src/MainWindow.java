import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Created by User on 7.10.2015 ?..
 */
public class MainWindow implements ToolWindowFactory {

    public static JTextArea selectionOutcome = new JTextArea("Selection");
//    public static JTextArea keyListenerOutcome = new JTextArea("Keys");


    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        Component component = toolWindow.getComponent();

        selectionOutcome.setAlignmentX(JLabel.LEFT);
        selectionOutcome.setAlignmentY(JLabel.TOP);
        selectionOutcome.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.green));

        component.getParent().add(selectionOutcome);

//        keyListenerOutcome.setAlignmentX(JLabel.RIGHT);
//        keyListenerOutcome.setAlignmentY(JLabel.TOP);
//        keyListenerOutcome.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.green));
//
//        component.getParent().add(keyListenerOutcome);

    }
}
