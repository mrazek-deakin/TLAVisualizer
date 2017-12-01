// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.
// Last modified on Mon 30 Apr 2007 at 15:29:58 PST by lamport
//      modified on Tue Oct 23 16:48:38 PDT 2001 by yuanyu

package tlc2.tool;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Vector;

import tla2sany.semantic.ExprOrOpArgNode;
import tla2sany.semantic.OpApplNode;
import tla2sany.semantic.OpDeclNode;
import tla2sany.semantic.SemanticNode;
import tla2sany.semantic.SymbolNode;
import tlc2.value.BoolValue;
import tlc2.value.Value;
import tlc2.value.ValueInputStream;
import tlc2.value.ValueOutputStream;
import util.UniqueString;

public abstract class TLCState implements Cloneable, Serializable {
	public long uid = -1;   // Must be set to a non-negative number

	// Set by subclasses. Cannot set until we know what the variables are.
	public static TLCState Empty = null;

	// The state variables.
	protected static OpDeclNode[] vars = null;

	public static void setVariables(OpDeclNode[] variables) 
	{
		vars = variables;
		// SZ 10.04.2009: since this method is called exactly one from Spec#processSpec
		// moved the call of UniqueString#setVariables to that place

		// UniqueString[] varNames = new UniqueString[variables.length];
		// for (int i = 0; i < varNames.length; i++)
		// {
		//  varNames[i] = variables[i].getName();
		//}
		//UniqueString.setVariables(varNames);
	}

	public void read(ValueInputStream vis) throws IOException {
		this.uid = vis.readLongNat();
	}

	public void write(ValueOutputStream vos) throws IOException {
		vos.writeLongNat(this.uid);
	}

	/**
	 * Bind functions set the value of a variable:
	 * Example usage:
	 * 		UniqueString str = UniqueString.uniqueStringOf("varA");
	 *	if (curState.containsKey(str)) {
	 *		Value val = new BoolValue(true);
	 *		curState.bind(str, val, null); //sets the value to true
	 *	}
	 * 
	 * @return
	 */
	public abstract TLCState bind(UniqueString name, Value value, SemanticNode expr);
	public abstract TLCState bind(SymbolNode id, Value value, SemanticNode expr);  
	public abstract TLCState unbind(UniqueString name);
	public abstract Value lookup(UniqueString var);
	public abstract boolean containsKey(UniqueString var);
	public abstract TLCState copy();
	public abstract TLCState deepCopy();
	public abstract StateVec addToVec(StateVec states);
	public abstract void deepNormalize();
	public abstract long fingerPrint();
	public abstract boolean allAssigned();
	public abstract TLCState createEmpty();

	/* Returns a string representation of this state.  */
	public abstract String toString();
	public abstract String toString(TLCState lastState);

	/* Adds an action that was applied to the state to get to this position */
	public abstract void addEvent(OpApplNode pred);
	public abstract void clearEvent();
	public abstract Events getEvents();

	public abstract void addPrecondition(ExprOrOpArgNode exprOrOpArgNode, boolean b);
	public abstract void clearPreconditions();
	public abstract Preconditions getPreconditions();
	
	public abstract Value[] getValues();
	public UniqueString getVarName(int index) {
		return vars[index].getName();
	}

	public abstract void addAction(Action action);
	public abstract Action getAction();
	
	
}
