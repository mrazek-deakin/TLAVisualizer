package tlavisualiser;

import javax.swing.JPanel;

import tlc2.tool.TLCState;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;

public class StateActionsPanel  extends JPanel {

	public StateActionsPanel(TLCState st) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		int i = 0;
		if (st.getAction() != null) {
			JLabel lblAction = new JLabel(st.getAction().name);
			GridBagConstraints gbc_lblAction = new GridBagConstraints();
			gbc_lblAction.gridx = 0;
			gbc_lblAction.gridy = i+1;
			add(lblAction, gbc_lblAction);
			i++;
		}
		for (String act : st.getEvents().toStringArray()) {

			JLabel lblAction = new JLabel(act);
			GridBagConstraints gbc_lblAction = new GridBagConstraints();
			gbc_lblAction.gridx = 0;
			gbc_lblAction.gridy = i+1;
			add(lblAction, gbc_lblAction);
			i++;
		}
		// TODO Auto-generated constructor stub
	}

}
