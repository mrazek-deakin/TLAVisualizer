package tlavisualiser;

import javax.swing.JPanel;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JList;

import stateextractor.StateExtractor;
import tlc2.tool.TLCState;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.awt.FlowLayout;
import javax.swing.JTable;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GetNextStatePanel extends JPanel {
	private StateExtractor stateExtractor = null;
	private JPanel panel_2;
	private JPanel panel_1;
	private StateViewPanel svp;
	/**
	 * Create the panel.
	 */
	public GetNextStatePanel() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);
		
		JSplitPane splitPane = new JSplitPane();
		scrollPane.setViewportView(splitPane);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		panel_1 = new JPanel();
		splitPane.setRightComponent(panel_1);


		
		
		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JLabel lblCurrentState = new JLabel("Current State");
		panel.add(lblCurrentState);
		
		panel_2 = new JPanel();
		panel.add(panel_2);
		
		JButton btnGetNextStates = new JButton("Get Next States");
		btnGetNextStates.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				getNextStates();
			}
		});
		btnGetNextStates.setVerticalAlignment(SwingConstants.BOTTOM);
		panel.add(btnGetNextStates);
		
		

	}

	protected void getNextStates() {
		TLCState state = svp.getState();
		List<TLCState> nextStates = stateExtractor.getNextState(state);
		panel_1.removeAll();
		
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		

		
		JLabel lblState = new JLabel("State");
		GridBagConstraints gbc_lblState = new GridBagConstraints();
		gbc_lblState.insets = new Insets(0, 0, 0, 5);
		gbc_lblState.gridx = 0;
		gbc_lblState.gridy = 0;
		panel_1.add(lblState, gbc_lblState);
		
		JLabel lblPreconditions = new JLabel("Preconditions");
		GridBagConstraints gbc_lblPreconditions = new GridBagConstraints();
		gbc_lblPreconditions.insets = new Insets(0, 0, 0, 5);
		gbc_lblPreconditions.gridx = 1;
		gbc_lblPreconditions.gridy = 0;
		panel_1.add(lblPreconditions, gbc_lblPreconditions);
		
		JLabel lblActions = new JLabel("Actions");
		GridBagConstraints gbc_lblActions = new GridBagConstraints();
		gbc_lblActions.insets = new Insets(0, 0, 0, 5);
		gbc_lblActions.gridx = 2;
		gbc_lblActions.gridy = 0;
		panel_1.add(lblActions, gbc_lblActions);
		
		JLabel lblGetNext = new JLabel("Execute");
		GridBagConstraints gbc_lblGetNext = new GridBagConstraints();
		gbc_lblGetNext.insets = new Insets(0, 0, 5, 0);
		gbc_lblGetNext.gridx = 3;
		gbc_lblGetNext.gridy = 0;
		panel_1.add(lblGetNext, gbc_lblGetNext);
		
		
		
		for (int i = 0; i < nextStates.size(); i++) {
			final TLCState st = nextStates.get(i);
			
			StateViewPanel lblState1 = new StateViewPanel(st, false);
			GridBagConstraints gbc_lblState1 = new GridBagConstraints();
			gbc_lblState1.insets = new Insets(0, 0, 0, 5);
			gbc_lblState1.gridx = 0;
			gbc_lblState1.gridy = i+1;
			panel_1.add(lblState1, gbc_lblState1);
			
			StatePreconditionsPanel lblPreconditions1 = new StatePreconditionsPanel(st);
			GridBagConstraints gbc_lblPreconditions1 = new GridBagConstraints();
			gbc_lblPreconditions1.insets = new Insets(0, 0, 0, 5);
			gbc_lblPreconditions1.gridx = 1;
			gbc_lblPreconditions1.gridy = i+1;
			panel_1.add(lblPreconditions1, gbc_lblPreconditions1);
			
			StateActionsPanel lblActions1 = new StateActionsPanel(st);
			GridBagConstraints gbc_lblActions1 = new GridBagConstraints();
			gbc_lblActions1.insets = new Insets(0, 0, 0, 5);
			gbc_lblActions1.gridx = 2;
			gbc_lblActions1.gridy = i+1;
			panel_1.add(lblActions1, gbc_lblActions1);
			
			JButton lblGetNext1 = new JButton();
			lblGetNext1.setText("Execute");
			lblGetNext1.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					fillStatePanels(st);
					getNextStates();
				}
			});
			
			GridBagConstraints gbc_lblGetNext1 = new GridBagConstraints();
			gbc_lblGetNext1.insets = new Insets(0, 0, 5, 0);
			gbc_lblGetNext1.gridx = 3;
			gbc_lblGetNext1.gridy = i+1;
			panel_1.add(lblGetNext1, gbc_lblGetNext1);
		}
		
		panel_1.revalidate();
		panel_1.repaint();
	}

	public void setStateExtractor(StateExtractor stateExtractor) {
		this.stateExtractor = stateExtractor;
		fillStatePanels(stateExtractor.getInitialState());
		getNextStates();
	}
	
	public void fillStatePanels(TLCState tlcState) {
		svp = new StateViewPanel(tlcState, true);
		panel_2.removeAll();
		panel_2.add(svp);
		panel_2.revalidate();
		panel_2.repaint();
		
	}
}
