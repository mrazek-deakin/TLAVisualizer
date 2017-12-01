package tlavisualiser;

import java.awt.EventQueue;

import javax.management.NotCompliantMBeanException;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import stateextractor.StateExtractor;
import tlc2.tool.EvalException;
import tlc2.tool.ModelChecker;
import tlc2.tool.fp.FPSetConfiguration;
import tlc2.tool.management.ModelCheckerMXWrapper;
import tlc2.util.FP64;
import util.FilenameToStream;
import util.SimpleFilenameToStream;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JProgressBar;
import javax.swing.plaf.ProgressBarUI;

public class VisualGUI {

	private JFrame frame;
	private StateExtractor stateExtractor = null;
	private LoadProject lp;
	private JProgressBar progressBar;
	private ExecutionArea executionPanel;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VisualGUI window = new VisualGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VisualGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1280, 1024);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenuItem mntmLoadProject = new JMenuItem("Load Project");
		mntmLoadProject.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				loadProject();
			}
		});
		menuBar.add(mntmLoadProject);
		

		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		lp = new LoadProject();
		tabbedPane.addTab("Project Properties", null, lp, null);
		
//		JPanel panel = new FileViewerPanel();
//		tabbedPane.addTab("File View", null, panel, null);
		
		executionPanel = new ExecutionArea();
		tabbedPane.addTab("Next State", null, executionPanel, null);
		
		progressBar = new JProgressBar();
		frame.getContentPane().add(progressBar, BorderLayout.SOUTH);
		

	}

	protected void loadProject() {
		progressBar.setIndeterminate(true);
		String specFile = lp.getFile();
		String paths[] = lp.getPaths();
		SimpleFilenameToStream resolver = new SimpleFilenameToStream();
		resolver.addPath(paths);
		String configFile = lp.getConfigFile();
		FPSetConfiguration fpSetConfig = new FPSetConfiguration();
		FP64.Init(lp.getFPInit());
		try {
			stateExtractor = new StateExtractor(specFile, configFile, null, false, null, resolver, null, fpSetConfig);
			new ModelCheckerMXWrapper((ModelChecker) stateExtractor);
			stateExtractor.modelCheck();
			
			executionPanel.setStateExtractor(stateExtractor);
			
		} catch (EvalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		progressBar.setIndeterminate(false);
	}

}
