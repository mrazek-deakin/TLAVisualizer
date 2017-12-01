package tlavisualiser;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class FileViewerPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public FileViewerPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);
		
		JMenuItem mntmOpenFile = new JMenuItem("Open File");
		menuBar.add(mntmOpenFile);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.SOUTH);
		


	}

}
