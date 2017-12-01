package stateextractor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;


public class OutputDotFile {
	Vector<StateTransition> stateTransitions;
	String module;
	
	
	public OutputDotFile(Vector<StateTransition> transitions, String module) {
		this.stateTransitions = transitions;
		this.module = module;
	}


	public void write(String transFile) {
		PrintWriter writer;
		if (transFile == null) {
			System.out.println("digraph " + module + " {");
			for (StateTransition st : stateTransitions) {
				System.out.println(DotString(st));
			}
			System.out.println("}");
			return;
		}
		try {
			writer = new PrintWriter(transFile, "UTF-8");
			writer.println("digraph " + module + " {");
			for (StateTransition st : stateTransitions) {
				writer.println(DotString(st));
			}		
			writer.println("}");
			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String DotString(StateTransition st) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		sb.append(escape(st.getInitialState().toString()));
		sb.append("\"");
		sb.append(" -> ");
		sb.append("\"");
		sb.append(escape(st.getFinalState().toString()));
		sb.append("\"");
		sb.append(" [label=\"");

		sb.append(escape(st.toString()));


		sb.append("\"];");
		//sb.append(initialState);
		//sb.append(finalState);

		return sb.toString();
	}
	
	private String escape(String val) {
		return val.replace("\\", "\\\\").replace("]", "\\]").replace("[", "\\[");
		
	}
	



}
