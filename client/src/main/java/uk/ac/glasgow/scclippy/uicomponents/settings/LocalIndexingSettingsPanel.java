package uk.ac.glasgow.scclippy.uicomponents.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

import com.intellij.ui.JBColor;

import uk.ac.glasgow.scclippy.lucene.StackoverflowLuceneIndexer;
import uk.ac.glasgow.scclippy.plugin.editor.IntellijFacade;
import uk.ac.glasgow.scclippy.uicomponents.search.SearchController;

public class LocalIndexingSettingsPanel extends JPanel {

	private Properties properties;
	
	private JLabel indexingStatusLabel;
	private JLabel folderForNewIndexLabel;
	
	public LocalIndexingSettingsPanel (Properties properties, SearchController searchController){
		this.properties = properties;
	
		Border lineBorder = BorderFactory.createLineBorder(JBColor.cyan);
		setBorder(BorderFactory.createTitledBorder(lineBorder, "Local index options"));
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.ipadx = 10;
		gbc.ipady = 10;

		// Local index used option
		String text = properties.getProperty("indexPath");
		if (text == null)
			text = "Index directory not selected.";
		JLabel indexChosenLabel = new JLabel(text);
		
		JButton indexChooserButton = 
			createIndexChooserButton(searchController);
		
		JPanel indexForSearchingPanel = new JPanel();
		indexForSearchingPanel.add(indexChosenLabel);
		indexForSearchingPanel.add(indexChooserButton);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(indexForSearchingPanel, gbc);

		// Creating/Updating index
		JPanel createIndexOptions = new JPanel();
		createIndexOptions.setLayout(new GridBagLayout());
		createIndexOptions.setBorder(BorderFactory.createTitledBorder(lineBorder, "Create/Update Local Index"));
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(createIndexOptions, gbc);

		folderForNewIndexLabel = new JLabel("Index directory not selected.");
		JButton folderForNewIndexButton =
			createFolderChooserButton("Choose index folder", folderForNewIndexLabel);
		
		JPanel createIndexFolderOptions = new JPanel(new GridBagLayout());
		createIndexFolderOptions.add(folderForNewIndexLabel);
		createIndexFolderOptions.add(folderForNewIndexButton);
		gbc.gridx = 0;
		gbc.gridy = 0;
		createIndexOptions.add(createIndexFolderOptions, gbc);

		// data chooser
		JLabel dataFolderLabel = new JLabel("Data directory not selected.");
		JButton dataFolderChooser =
			createFolderChooserButton("Choose data folder", dataFolderLabel);
		JPanel dataFolderChooserPanel = new JPanel(new GridBagLayout());
		dataFolderChooserPanel.add(dataFolderLabel);
		dataFolderChooserPanel.add(dataFolderChooser);
		gbc.gridx = 0;
		gbc.gridy = 1;
		createIndexOptions.add(dataFolderChooserPanel, gbc);

		// update index option
		JCheckBox updateIndex = new JCheckBox("Update index");
		gbc.gridx = 0;
		gbc.gridy = 2;
		createIndexOptions.add(updateIndex);

		indexingStatusLabel = new JLabel("");
		JButton indexButton = createStartIndexingButton();

		
		JPanel startCreatingIndexPanel = new JPanel(new GridBagLayout());
		startCreatingIndexPanel.add(indexButton);
		startCreatingIndexPanel.add(indexingStatusLabel);
		gbc.gridx = 0;
		gbc.gridy = 3;
		createIndexOptions.add(startCreatingIndexPanel, gbc);

	}

	private JButton createStartIndexingButton() {
		JButton indexButton = new JButton("Create index.");
		indexButton.setBackground(JBColor.orange);
		indexButton.addActionListener
			(e -> {
				indexingStatusLabel.setText("Indexing files...");
				
				Path indexPath = Paths.get(folderForNewIndexLabel.getText());
				
				StackoverflowLuceneIndexTask task = new StackoverflowLuceneIndexTask(null, indexPath, indexingStatusLabel);
				task.execute();
			});
		return indexButton;
	}

	private JButton createIndexChooserButton(SearchController searchController) {
		JButton indexChooserButton = new JButton("Choose index");
		indexChooserButton.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Choose index");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				String indexPathString = chooser.getSelectedFile().getAbsolutePath();
				properties.put("indexPath", indexPathString);
				Path indexPath = Paths.get(indexPathString).toAbsolutePath();

				searchController.setIndexPath(indexPath);
				folderForNewIndexLabel.setText(chooser.getSelectedFile().getName());
				folderForNewIndexLabel.setToolTipText(chooser.getSelectedFile().getPath());
			}
		});
		return indexChooserButton;
	}

	private JButton createFolderChooserButton(String s,
		JLabel folderLabel) {
		JButton button = new JButton(s);
		button.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle(s);
			chooser.setFileSelectionMode(
				JFileChooser.DIRECTORIES_ONLY);

			if (chooser.showOpenDialog(
				null) == JFileChooser.APPROVE_OPTION) {
				folderLabel.setText(
					chooser.getSelectedFile().getName());
				folderLabel.setToolTipText(
					chooser.getSelectedFile().getPath());
			}
		});
		return button;
	}
	
	private class StackoverflowLuceneIndexTask extends SwingWorker<Void, Void> {

		private StackoverflowLuceneIndexer stackoverflowLuceneIndexer;
		private JLabel statusLabel;

		public StackoverflowLuceneIndexTask(Connection connection, Path indexPath, JLabel statusLabel) {
			stackoverflowLuceneIndexer = new StackoverflowLuceneIndexer (connection, indexPath);		}

		/**
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			try {
				stackoverflowLuceneIndexer.indexDocuments();
			} catch (IOException | SQLException e) {
				IntellijFacade.createErrorNotification(e.getMessage());
			}
			return null;
		}

		/**
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			statusLabel.setText("Finished indexing files.");
			IntellijFacade.createInfoNotification("Finished Indexing stackoverflow posts.");
		}
	}
	
}
