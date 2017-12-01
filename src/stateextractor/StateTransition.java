package stateextractor;

import tlc2.tool.Action;
import tlc2.tool.Events;
import tlc2.tool.Preconditions;
import tlc2.tool.TLCState;

public class StateTransition {
	private TLCState initialState;
	private TLCState finalState;
	Action action;
	Preconditions preconditions;
	Events events;

	public StateTransition(TLCState initialState, Action action, Preconditions preconditions, Events events, TLCState finalState) {
		this.setInitialState(initialState);
		this.action = action;
		this.setFinalState(finalState);
		this.preconditions = preconditions;
		this.events = events;
	}

	public TLCState getFinalState() {
		return finalState;
	}

	public void setFinalState(TLCState finalState) {
		this.finalState = finalState;
	}

	public TLCState getInitialState() {
		return initialState;
	}

	public void setInitialState(TLCState initialState) {
		this.initialState = initialState;
	}

	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		String name = action.getName();
		String eventStr = events.toString();
		String preconditionStr = preconditions.toString();
		


		if (name != null) {
			sb.append(name);
		} else if (eventStr != null) {
			sb.append(eventStr);
		} else {
			NoActionException e = new NoActionException("Didnt find the name of the Action, this is a bug");
			e.printStackTrace();
		}
		
		if (preconditionStr != null) {
			sb.append("[");
			sb.append(preconditionStr);
			sb.append("]");
		}
		
		return sb.toString();
		
	};




}
