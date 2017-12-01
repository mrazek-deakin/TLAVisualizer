package tlc2.tool;

import java.util.Vector;

import tla2sany.semantic.ExprOrOpArgNode;
import tla2sany.semantic.OpApplNode;

public class Events {
	private Vector<OpApplNode> events;

	public Events() {
		events = new Vector<OpApplNode>();
		// TODO Auto-generated constructor stub
	}

	public Events(Events events2) {
		events = new Vector<OpApplNode>(events2.getEvents());
	}

	public Vector<OpApplNode> getEvents() {
		return events;
	}

	public void add(OpApplNode act) {
		events.add(act);
		
	}

	public void clear() {
		events.clear();
	}
	
	@Override
	public String toString() {
		if (events.size() == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (OpApplNode eve : events) {
			sb.append("/\\ ");
			sb.append(eve.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	
	public String[] toStringArray() {
		String[] strarr = new String[events.size()];
		int i = 0;
		for (OpApplNode pre : events) {
			strarr[i]="/\\ " + pre.toString();
			i++;
		}
		return strarr;
	}

}
