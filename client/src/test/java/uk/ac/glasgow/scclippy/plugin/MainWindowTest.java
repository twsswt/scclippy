package uk.ac.glasgow.scclippy.plugin;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import uk.ac.glasgow.scclippy.uicomponents.main.MainWindow;

public class MainWindowTest {

	@Mock
	private Project project;
	
	@Mock
	private ToolWindow toolWindow;

	@Before
	public void setUp() throws Exception {
		MainWindow mainWindow = new MainWindow();
		mainWindow.createToolWindowContent(project, toolWindow);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
