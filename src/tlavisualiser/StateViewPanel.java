package tlavisualiser;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextField;

import tlc2.tool.TLCState;
import tlc2.value.BoolValue;
import tlc2.value.IntValue;
import tlc2.value.Value;
import util.UniqueString;

public class StateViewPanel extends JPanel {
	private Map<UniqueString,JComponent> components = new HashMap<UniqueString, JComponent>();
	private TLCState state;

	/**
	 * Create the panel.
	 */
	public StateViewPanel(TLCState state, boolean editable) {

		this.state = state.deepCopy();

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JLabel lblVariableName = new JLabel("Name");
		GridBagConstraints gbc_lblVariableName = new GridBagConstraints();
		gbc_lblVariableName.insets = new Insets(0, 0, 5, 5);
		gbc_lblVariableName.gridx = 0;
		gbc_lblVariableName.gridy = 0;
		add(lblVariableName, gbc_lblVariableName);

		JLabel lblVariableValue = new JLabel("Value");
		GridBagConstraints gbc_lblVariableValue = new GridBagConstraints();
		gbc_lblVariableValue.insets = new Insets(0, 0, 5, 0);
		gbc_lblVariableValue.gridx = 1;
		gbc_lblVariableValue.gridy = 0;
		add(lblVariableValue, gbc_lblVariableValue);

		Value[] values = state.getValues();

		for (int i = 0; i < values.length; i++) {

			JLabel lblVara = new JLabel(state.getVarName(i).toString());
			GridBagConstraints gbc_lblVara = new GridBagConstraints();
			gbc_lblVara.anchor = GridBagConstraints.EAST;
			gbc_lblVara.insets = new Insets(0, 0, 0, 5);
			gbc_lblVara.gridx = 0;
			gbc_lblVara.gridy = i+1;
			add(lblVara, gbc_lblVara);


			switch (values[i].getKind()) {
			case Value.INTVALUE:
				JTextField textField = new JTextField();
				GridBagConstraints gbc_textField = new GridBagConstraints();
				gbc_textField.fill = GridBagConstraints.HORIZONTAL;
				gbc_textField.gridx = 1;
				gbc_textField.gridy = i+1;
				add(textField, gbc_textField);
				textField.setColumns(10);
				textField.setEditable(editable);
				textField.setText(values[i].toString());
				components.put(state.getVarName(i), textField);
				break;
			case Value.BOOLVALUE:
				JCheckBox checkBox = new JCheckBox();
				GridBagConstraints gbc_textField1 = new GridBagConstraints();
				gbc_textField1.fill = GridBagConstraints.HORIZONTAL;
				gbc_textField1.gridx = 1;
				gbc_textField1.gridy = i+1;
				add(checkBox, gbc_textField1);
				checkBox.setSelected(((BoolValue)values[i]).val);
				checkBox.setEnabled(editable);
				components.put(state.getVarName(i), checkBox);
				break;
			default:
			}





		}
	}

	public TLCState getState() {
		for (UniqueString varname : components.keySet()) {
			JComponent comp = components.get(varname);
			Value val = state.lookup(varname);
			switch (val.getKind()) {
			case Value.INTVALUE:
				String str = ((JTextField)comp).getText();
				int intval = Integer.parseInt(str);
				val = new IntValue(intval);
				break;
			case Value.BOOLVALUE:
				Boolean bool = ((JCheckBox)comp).isSelected();
				val = new BoolValue(bool);
				break;
			default:
				System.out.println("Unsupported kind: " + val.getKind());
			}

			state.bind(varname, val, null);
		}
		return state;

	}

}
