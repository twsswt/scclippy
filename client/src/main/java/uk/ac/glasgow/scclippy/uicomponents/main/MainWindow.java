
package uk.ac.glasgow.scclippy.uicomponents.main;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;

import uk.ac.glasgow.scclippy.plugin.search.LocalIndexedSearch;
import uk.ac.glasgow.scclippy.plugin.search.SearchController;
import uk.ac.glasgow.scclippy.plugin.search.StackoverflowJSONAPISearch;
import uk.ac.glasgow.scclippy.plugin.search.WebServiceSearch;
import uk.ac.glasgow.scclippy.uicomponents.feedbacktab.FeedbackTab;
import uk.ac.glasgow.scclippy.uicomponents.history.SearchHistoryTab;
import uk.ac.glasgow.scclippy.uicomponents.infotab.InfoTab;
import uk.ac.glasgow.scclippy.uicomponents.search.SearchTab;
import uk.ac.glasgow.scclippy.uicomponents.settings.SettingsTab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Main Tool Window class
 */
public class MainWindow implements ToolWindowFactory {

	private JTabbedPane tabsPanel;

	private SearchTab searchTab;
	private SearchHistoryTab searchHistoryTab;
	private SettingsTab settingsTab;
	private InfoTab infoTab;
	private FeedbackTab feedbackTab;
	
	/**
	 * Creates tool main content
	 */
	@Override
	public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
		
		Properties properties = initialiseProperties();
		
		SearchController searchController = initialiseSearchController(properties);
		initTabsPanel(searchController, properties);
		
		Component toolWindowComponent = toolWindow.getComponent();
		toolWindowComponent.getParent().add(tabsPanel);
		
		// init mouse selection listener
		Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
		if (editor == null)
			return;

		editor.addEditorMouseListener(
			new TextSelectionMouseListener(
				searchTab.getQueryInputPane(), searchController));

	}

	private SearchController initialiseSearchController(Properties properties) {
		
		SearchController searchController = new SearchController ();
		
		String indexPathString = properties.getProperty("indexPath");
		Path indexPath = Paths.get(indexPathString).toAbsolutePath();
		LocalIndexedSearch localIndexedSearch = new LocalIndexedSearch(indexPath);
		searchController.addSearchMechanism("Local Index", localIndexedSearch);

		String webServiceURI = properties.getProperty("webServiceURI");
		WebServiceSearch webServiceSearch = new WebServiceSearch(webServiceURI);
		searchController.addSearchMechanism("Web Service", webServiceSearch);
		
		StackoverflowJSONAPISearch stackoverflowJSONAPISearch = new StackoverflowJSONAPISearch();
		searchController.addSearchMechanism("StackExchange API", stackoverflowJSONAPISearch);
		
		Integer defaultMaximumPostsToRetrieve = Integer.parseInt(properties.getProperty("defaultMaximumPostsToRetrieve"));
		searchController.setDefaultMaximumPostsToRetrieve(defaultMaximumPostsToRetrieve);
		
		Integer extraPostsToRetrieveOnScroll = Integer.parseInt(properties.getProperty("extraPostsToRetrieveOnScroll"));
		searchController.setExtraPostsToRetrieveOnScroll(extraPostsToRetrieveOnScroll);
		
		searchController.resetMaximumPostsToRetrieve ();
		
		Integer minimumUpVotes = Integer.parseInt(properties.getProperty("minimumUpVotes"));
		searchController.setMinimumUpVotes(minimumUpVotes);

		searchController.setSearchType("StackExchange API");
		searchController.setSortType("Score");
		searchController.setQuery("");
		
		return searchController;
	}
	
	private Properties initialiseProperties() {

		IdeaPluginDescriptor ipd = null;
		try {
			ipd = PluginManager.getPlugin(PluginId.getId("uk.ac.glasgow.scclippy"));
		} catch (AssertionError rte){
			
		}
		
		String pluginPath = (ipd == null) ? "src/main/resource" : ipd.getPath().getAbsolutePath();
		String settingsPath = pluginPath+"/config.properties";

		File propertiesFile = new java.io.File(settingsPath);
		Properties properties = new PersistentProperties(settingsPath);

		try {
			InputStream propertiesStream = new FileInputStream(propertiesFile);
			properties.load(propertiesStream);
		} catch (IOException e) {
			throw new RuntimeException("During initialisation of Scclippy plugin properties.", e);
		}
		return properties;
	}

	/**
	 * Creates the tabs panel and the tabs
	 * for search, history and settings
	 * (and sets a mnemonic for each)
	 */
	private void initTabsPanel(SearchController searchController, Properties properties) {
		
		tabsPanel = new JBTabbedPane();

		searchHistoryTab = new SearchHistoryTab();
		searchController.addSearchChangeListener(searchHistoryTab);
		
		searchTab = new SearchTab(searchController);
		settingsTab = new SettingsTab(properties, searchController, searchTab.getQueryInputPane(), searchTab.getPostsPane());
		infoTab = new InfoTab();
		feedbackTab = new FeedbackTab();
		
		tabsPanel = new JBTabbedPane();

		tabsPanel.addTab("Search", null, searchTab, "Searching by index or using Stackoverflow API");
		tabsPanel.addTab("History", null, searchHistoryTab, "View input history");
		tabsPanel.addTab("Settings", null, settingsTab, "Change settings");
		tabsPanel.addTab("Info", null, infoTab.getScroll(), "Description of the plugin");
		tabsPanel.addTab("Feedback", null, feedbackTab.getScroll(), "Provide feedback");

		tabsPanel.setMnemonicAt(0, KeyEvent.VK_1);
		tabsPanel.setMnemonicAt(1, KeyEvent.VK_2);
		tabsPanel.setMnemonicAt(2, KeyEvent.VK_3);
		tabsPanel.setMnemonicAt(3, KeyEvent.VK_4);
		tabsPanel.setMnemonicAt(4, KeyEvent.VK_5);
	}

}