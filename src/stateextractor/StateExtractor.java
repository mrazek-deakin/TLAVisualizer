package stateextractor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import tla2sany.modanalyzer.SpecObj;
import tlc2.TLCGlobals;
import tlc2.output.EC;
import tlc2.output.MP;
import tlc2.tool.EvalException;
import tlc2.tool.ModelChecker;
import tlc2.tool.StateVec;
import tlc2.tool.TLCState;
import tlc2.tool.TLCStateMut;
import tlc2.tool.TLCStateMutSource;
import tlc2.tool.fp.FPSetConfiguration;
import tlc2.tool.liveness.LiveCheck;
import tlc2.util.LongVec;
import tlc2.util.ObjLongTable;
import util.FilenameToStream;

public class StateExtractor extends ModelChecker {

	Vector<StateTransition> stateTransitions = new Vector<StateTransition>();
	Map<Long, TLCState> stateMap = new HashMap<Long, TLCState>();

	String module = null;
	FilenameToStream resolver;

	public StateExtractor(String specFile, String configFile, String dumpFile,
			boolean deadlock, String fromChkpt, FilenameToStream resolver, SpecObj specObj, 
			FPSetConfiguration fpSetConfig)
					throws EvalException, IOException {
		super(specFile, configFile, dumpFile, deadlock, fromChkpt, resolver, specObj,
				fpSetConfig);
		module=specFile;
		this.resolver = resolver;

		// TODO Auto-generated constructor stub
	}

	/**
	 * Compute the set of the next states.  For each next state, check that
	 * it is a valid state, check that the invariants are satisfied, check
	 * that it satisfies the constraints, and enqueue it in the state queue.
	 * Return true if the model checking should stop.
	 * 
	 * This method is called from the workers on every step
	 */
	public final boolean doNext(TLCState curState, ObjLongTable counts) throws Throwable
	{
		// SZ Feb 23, 2009: cancel the calculation
		if (this.cancellationFlag)
		{
			return false;
		}

		boolean deadLocked = true;
		TLCState succState = null;
		StateVec liveNextStates = null;
		LongVec liveNextFPs = null;

		if (this.checkLiveness)
		{
			liveNextStates = new StateVec(2);
			liveNextFPs = new LongVec(2);
		}

		try
		{
			int k = 0;
			// <--
			// <--
			for (int i = 0; i < this.actions.length; i++)
			{
				// SZ Feb 23, 2009: cancel the calculation
				if (this.cancellationFlag)
				{
					return false;
				}

				//add current state to map of states
				stateMap.put(curState.fingerPrint(), curState);

				//get next states
				StateVec nextStates = this.tool.getNextStates(this.actions[i], curState);


				int sz = nextStates.size();

				this.incNumOfGenStates(sz);
				deadLocked = deadLocked && (sz == 0);

				for (int j = 0; j < sz; j++)
				{
					succState = nextStates.elementAt(j);
					
					//add state to state map, statemap should not contain duplicates
					if (!stateMap.containsKey(succState.fingerPrint())) {
						stateMap.put(succState.fingerPrint(), succState);
					}
					// add transition to list of transitions
					stateTransitions.add(new StateTransition(stateMap.get(curState.fingerPrint()), 
							this.actions[i], 
							succState.getPreconditions(), 
							succState.getEvents(), 
							stateMap.get(succState.fingerPrint())));

					// Check if succState is a legal state.
					if (!this.tool.isGoodState(succState))
					{
						if (this.setErrState(curState, succState, false))
						{
							MP.printError(EC.TLC_STATE_NOT_COMPLETELY_SPECIFIED_NEXT);
							this.trace.printTrace(curState, succState);
							this.theStateQueue.finishAll();

							synchronized (this)
							{
								this.notify();
							}
						}
						return true;
					}
					if (TLCGlobals.coverageInterval >= 0)
					{
						((TLCStateMutSource) succState).addCounts(counts);
					}

					boolean inModel = (this.tool.isInModel(succState) && this.tool.isInActions(curState, succState));
					boolean seen = false;
					if (inModel)
					{
						long fp = succState.fingerPrint();
						seen = this.theFPSet.put(fp);
						if (!seen)
						{
							// Write out succState when needed:
								if (this.allStateWriter != null)
								{
									this.allStateWriter.writeState(succState);
								}
						// Enqueue succState only if it satisfies the model constraints:
								long loc = this.trace.writeState(curState, fp);
								succState.uid = loc;
								this.theStateQueue.sEnqueue(succState);
						}
						// For liveness checking:
						if (this.checkLiveness)
						{
							liveNextStates.addElement(succState);
							liveNextFPs.addElement(fp);
						}
					}
					// Check if succState violates any invariant:
					if (!seen)
					{
						try
						{
							int len = this.invariants.length;
							for (k = 0; k < len; k++)
							{
								// SZ Feb 23, 2009: cancel the calculation
								if (this.cancellationFlag)
								{
									return false;
								}

								if (!tool.isValid(this.invariants[k], succState))
								{
									// We get here because of invariant violation:
									synchronized (this)
									{
										if (TLCGlobals.continuation)
										{
											MP.printError(EC.TLC_INVARIANT_VIOLATED_BEHAVIOR,
													this.tool.getInvNames()[k]);
											this.trace.printTrace(curState, succState);
											break;
										} else
										{
											if (this.setErrState(curState, succState, false))
											{
												MP.printError(EC.TLC_INVARIANT_VIOLATED_BEHAVIOR, this.tool
														.getInvNames()[k]);
												this.trace.printTrace(curState, succState);
												this.theStateQueue.finishAll();
												this.notify();
											}
											return true;
										}
									}
								}
							}
							if (k < len)
								continue;
						} catch (Exception e)
						{
							if (this.setErrState(curState, succState, true))
							{
								MP.printError(EC.TLC_INVARIANT_EVALUATION_FAILED, new String[] {
										this.tool.getInvNames()[k], 
										(e.getMessage()==null)?e.toString():e.getMessage() });
								this.trace.printTrace(curState, succState);
								this.theStateQueue.finishAll();
								this.notify();
							}
							throw e;
						}
					}
					// Check if the state violates any implied action. We need to do it
					// even if succState is not new.
					try
					{
						int len = this.impliedActions.length;
						for (k = 0; k < len; k++)
						{
							// SZ Feb 23, 2009: cancel the calculation
							if (this.cancellationFlag)
							{
								return false;
							}

							if (!tool.isValid(this.impliedActions[k], curState, succState))
							{
								// We get here because of implied-action violation:
								synchronized (this)
								{
									if (TLCGlobals.continuation)
									{
										MP.printError(EC.TLC_ACTION_PROPERTY_VIOLATED_BEHAVIOR, this.tool
												.getImpliedActNames()[k]);
										this.trace.printTrace(curState, succState);
										break;
									} else
									{
										if (this.setErrState(curState, succState, false))
										{
											MP.printError(EC.TLC_ACTION_PROPERTY_VIOLATED_BEHAVIOR, this.tool
													.getImpliedActNames()[k]);
											this.trace.printTrace(curState, succState);
											this.theStateQueue.finishAll();
											this.notify();
										}
										return true;
									}
								}
							}
						}
						if (k < len)
							continue;
					} catch (Exception e)
					{
						if (this.setErrState(curState, succState, true))
						{
							MP.printError(EC.TLC_ACTION_PROPERTY_EVALUATION_FAILED, new String[] {
									this.tool.getImpliedActNames()[k], 
									(e.getMessage()==null)?e.toString():e.getMessage() });
							this.trace.printTrace(curState, succState);
							this.theStateQueue.finishAll();
							this.notify();
						}
						throw e;
					}
				}
				// Must set state to null!!!
				succState = null;
			}
			// Check for deadlock:
			if (deadLocked && this.checkDeadlock)
			{
				synchronized (this)
				{
					if (this.setErrState(curState, null, false))
					{
						MP.printError(EC.TLC_DEADLOCK_REACHED);
						this.trace.printTrace(curState, null);
						this.theStateQueue.finishAll();
						this.notify();
					}
				}
				return true;
			}
			// Finally, add curState into the behavior graph for liveness checking:
			if (this.checkLiveness)
			{
				// Add the stuttering step:
				long curStateFP = curState.fingerPrint();
				liveNextStates.addElement(curState);
				liveNextFPs.addElement(curStateFP);
				LiveCheck.addNextState(curState, curStateFP, liveNextStates, liveNextFPs);
			}
			return false;
		} catch (Throwable e)
		{
			// Assert.printStack(e);
			boolean keep = ((e instanceof StackOverflowError) || (e instanceof OutOfMemoryError));
			synchronized (this)
			{
				if (this.setErrState(curState, succState, !keep))
				{
					if (e instanceof StackOverflowError)
					{
						MP.printError(EC.SYSTEM_STACK_OVERFLOW, e);
					} else if (e instanceof OutOfMemoryError)
					{
						MP.printError(EC.SYSTEM_OUT_OF_MEMORY, e);
					} else if (e.getMessage() != null)
					{
						MP.printError(EC.GENERAL, e);  // LL changed call 7 April 2012
					}
					this.trace.printTrace(curState, succState);
					this.theStateQueue.finishAll();
					this.notify();
				}
			}
			throw e;
		}
	}

	public Vector<StateTransition> getTransitions() {
		return stateTransitions;
	}

	public String getModule() {
		return module;
	}

	public TLCState getInitialState() {
		if (stateTransitions.size() != 0) {
			return stateTransitions.get(0).getInitialState();
		}
		return null;
	}

	/**
	 * gets an array of all states, used by the gui. 
	 * @param state
	 * @return array with all states or empty array if none.
	 */
	public List<TLCState> getNextState(TLCState state) {
		List<TLCState> stateArray = new ArrayList<TLCState>();
		for (int i = 0; i < this.actions.length; i++)
		{
			StateVec nextStates = this.tool.getNextStates(this.actions[i], state);
			for (int j = 0; j < nextStates.size(); j++)
			{
				nextStates.elementAt(j).addAction(this.actions[i]);
				stateArray.add(nextStates.elementAt(j));
			}
		}
		return stateArray;
	}

	public File getSVGFile() {
		File temp = null;
		try {
			temp = File.createTempFile("stateextractor", ".svg");
			OutputDotFile odf = new OutputDotFile(this.getTransitions(), this.getModule());
			odf.write(temp.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return temp;
	}

	public Collection<TLCState> getAllStates() {
		// TODO Auto-generated method stub
		return stateMap.values();
	}
}
