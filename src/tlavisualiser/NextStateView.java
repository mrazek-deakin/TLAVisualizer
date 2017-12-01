package tlavisualiser;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JSplitPane;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.GridLayout;

public class NextStateView extends JPanel {

	/**
	 * Create the panel.
	 */
	public NextStateView() {
		setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel panel = new JPanel();
		add(panel);
		
		JLabel lblState = new JLabel("State");
		panel.add(lblState);
		
		JPanel panel_4 = new JPanel();
		panel.add(panel_4);
		
		JPanel panel_1 = new JPanel();
		add(panel_1);
		
		JLabel lblPreconditions = new JLabel("Preconditions");
		panel_1.add(lblPreconditions);
		
		JPanel panel_5 = new JPanel();
		panel_1.add(panel_5);
		
		JPanel panel_2 = new JPanel();
		add(panel_2);
		
		JLabel lblActions = new JLabel("Actions");
		panel_2.add(lblActions);
		
		JPanel panel_6 = new JPanel();
		panel_2.add(panel_6);
		
		JPanel panel_3 = new JPanel();
		add(panel_3);
		
		JButton btnNewButton = new JButton("Select State");
		panel_3.add(btnNewButton);

	}

}
