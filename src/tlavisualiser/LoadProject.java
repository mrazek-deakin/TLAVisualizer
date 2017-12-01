package tlavisualiser;

import javax.swing.JFrame;

import java.awt.FlowLayout;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoadProject  extends JPanel {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Create the application.
	 */
	public LoadProject() {
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("39px"),
				ColumnSpec.decode("63px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("304px"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("35px"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("25px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblMainFile = new JLabel("Main File");
		add(lblMainFile, "2, 2, left, center");
		
		JPanel panel = new JPanel();
		add(panel, "4, 2, left, top");
				
				JLabel lblFileName = new JLabel("File Name");
				add(lblFileName, "2, 4");
				
				textField = new JTextField();
				add(textField, "4, 4");
				textField.setColumns(10);
				textField.setText("src"+File.separator+"examples"+File.separator+"test3"+File.separator+"test.tla");
				
				JButton btnOpenFile = new JButton("Open File");
				btnOpenFile.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						JFileChooser fc = new JFileChooser();
						fc.showDialog(null, "Main TLA file");
						textField.setText(fc.getSelectedFile().toString());
						
					}
				});
				add(btnOpenFile, "6, 4");
				
						
						JButton btnOpenProject = new JButton("Open Project");
						add(btnOpenProject, "6, 6, center, top");
	}
	
	public String getFile() {
		String file = textField.getText();
		return file.substring(file.lastIndexOf(File.separator)+1,file.lastIndexOf("."));
	}
	
	public String[] getPaths() {
		String file = textField.getText();
		Vector<String> paths = new Vector<String>();
		
		paths.add(file.substring(0, file.lastIndexOf(File.separator)));
		String[] pathsArray = new String[paths.size()];
		paths.toArray(pathsArray);
		
		return pathsArray;
	}

	public String getConfigFile() {
		return getFile();
	}

	public int getFPInit() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
