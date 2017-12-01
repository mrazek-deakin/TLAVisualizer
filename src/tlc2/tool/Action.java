// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.

package tlc2.tool;

import java.io.Serializable;

import tla2sany.semantic.SemanticNode;
import tla2sany.st.Location;
import tlc2.util.Context;

public final class Action implements ToolGlobals, Serializable {
	/* A TLA+ action.   */

	/* Fields  */
	public SemanticNode pred;     // Expression of the action
	public Context con;           // Context of the action
	public String name;
	public String operation;

	/* Constructors */
	public Action(SemanticNode pred, Context con, String name) {
		this(pred, con);
		this.name = name;
	}
	
	public Action(SemanticNode pred, Context con) {
		this.pred = pred;
		this.con = con;
		this.operation = getOperationsFromPred();
	}


	/* Returns a string representation of this action.  */
	public final String toString() {
		return "<Action " + name + ">";
	}

	public final String getLocationAsString() {
		return "<Action " + pred.getLocation() + ">";
	}
	
	public Location getLocation() {
		return pred.getLocation();
	}
	

	public String getName() {
		return name;
	}

	
	public String getOperationsFromPred() {
		StringBuilder sb = new StringBuilder();
		pred.stn.toString(sb);
		return sb.toString();	
	}
	
	public String getOperations() {
		return operation;
	}
	
	
}

