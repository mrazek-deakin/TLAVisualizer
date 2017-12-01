package tlc2.tool;

import java.util.LinkedHashMap;

import tla2sany.semantic.ExprOrOpArgNode;

public class Preconditions {
	private LinkedHashMap<ExprOrOpArgNode, Boolean> preconditions;

	public Preconditions() {
		preconditions = new LinkedHashMap<ExprOrOpArgNode, Boolean>();
	}

	public Preconditions(Preconditions preconditions2) {
		preconditions = new LinkedHashMap<ExprOrOpArgNode, Boolean>(preconditions2.getPreconditions());
	}

	public void put(ExprOrOpArgNode exprOrOpArgNode, boolean b) {
		preconditions.put(exprOrOpArgNode, b);
		
	}

	public void clear() {
		preconditions.clear();
		
	}
	
	public LinkedHashMap<ExprOrOpArgNode, Boolean> getPreconditions() {
		return preconditions;
	}
	
	@Override
	public String toString() {
		if (preconditions.size() == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (ExprOrOpArgNode pre : preconditions.keySet()) {
			sb.append(pre.toString().replace("\n", ""));
			if (preconditions.get(pre) == false) {
				sb.append(" == FALSE");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public String[] toStringArray() {
		String[] strarr = new String[preconditions.size()];
		int i = 0;
		for (ExprOrOpArgNode pre : preconditions.keySet()) {
			if (preconditions.get(pre) == false) {
				strarr[i]=pre.toString().replace("\n", "")+" == FALSE";
			} else {
				strarr[i]=pre.toString().replace("\n", "");
			}
			i++;
		}
		return strarr;
		
	}
	
}
