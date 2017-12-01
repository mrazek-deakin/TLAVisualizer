package tlavisualiser;

import javax.swing.JPanel;

import tlc2.tool.TLCState;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;

public class StatePreconditionsPanel  extends JPanel {

	public StatePreconditionsPanel(TLCState st) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		
		int i = 0;
		for (String pre : st.getPreconditions().toStringArray()) {
		
			JLabel lblPrecondition = new JLabel(pre);
			GridBagConstraints gbc_lblPrecondition = new GridBagConstraints();
			gbc_lblPrecondition.gridx = 0;
			gbc_lblPrecondition.gridy = i+1;
			add(lblPrecondition, gbc_lblPrecondition);
			i++;
		}
	}

}
