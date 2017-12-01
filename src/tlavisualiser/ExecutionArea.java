package tlavisualiser;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import stateextractor.StateExtractor;
import java.awt.BorderLayout;

public class ExecutionArea extends JPanel {
	StateDiagramPanel stateDiagramPanel;
	GetNextStatePanel getNextStatePanel;
	/**
	 * Create the panel.
	 */
	public ExecutionArea() {
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		add(splitPane);
		
		stateDiagramPanel = new StateDiagramPanel();
		splitPane.setRightComponent(stateDiagramPanel);
		
		getNextStatePanel = new GetNextStatePanel();
		splitPane.setLeftComponent(getNextStatePanel);

	}

	public void setStateExtractor(StateExtractor stateExtractor) {
		getNextStatePanel.setStateExtractor(stateExtractor);
		stateDiagramPanel.drawDiagram(stateExtractor);
	}

}
