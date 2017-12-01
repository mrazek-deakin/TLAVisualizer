// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.
// Last modified on Thu 10 April 2008 at 14:31:23 PST by lamport 
//      modified on Wed Dec  5 22:37:20 PST 2001 by yuanyu 

package stateextractor;

import tla2sany.modanalyzer.SpecObj;
import tlc2.TLCGlobals;
import tlc2.output.EC;
import tlc2.output.MP;
import tlc2.tool.AbstractChecker;
import tlc2.tool.Cancelable;
import tlc2.tool.ModelChecker;
import tlc2.tool.fp.FPSet;
import tlc2.tool.fp.FPSetConfiguration;
import tlc2.tool.management.ModelCheckerMXWrapper;
import tlc2.tool.management.TLCStandardMBean;
import tlc2.util.FP64;
import tlc2.value.Value;
import util.DebugPrinter;
import util.FileUtil;
import util.SimpleFilenameToStream;
import util.ToolIO;
import util.UniqueString;

/**
 * Main State Extractor starter class
 * @author Yuan Yu
 * @author Leslie Lamport
 * @author Simon Zambrovski
 * @author Cameron Cross
 * @version $Id$
 */
public class Main
{



	// SZ Feb 20, 2009: the class has been 
	// transformed from static to dynamic
	private boolean cleanup;
	private boolean deadlock;


	private String mainFile;
	private String configFile;
	private String dumpFile;
	private String dotFile;
	private String fromChkpt;

	private int fpIndex;
	@SuppressWarnings("unused")
	private int traceDepth;
	private SimpleFilenameToStream resolver;
	private SpecObj specObj;

	// flag if the welcome message is already printed
	private boolean welcomePrinted;

	// handle to the cancellable instance (MC or Simulator)
	private Cancelable instance;

	private FPSetConfiguration fpSetConfiguration;

	/**
	 * Initialization
	 */
	public Main()
	{
		welcomePrinted = false;

		cleanup = false;
		deadlock = true;

		mainFile = null;
		configFile = null;
		dumpFile = null;
		dotFile = null;
		fromChkpt = null;
		resolver = null;

		fpIndex = 0;
		traceDepth = 100;

		// instance is not set
		instance = null;
		TLCGlobals.continuation = true;

		fpSetConfiguration = new FPSetConfiguration();
		resolver = new SimpleFilenameToStream();
	}

	/*
	 *    
	 */
	public static void main(String[] args)
	{
		Main main = new Main();

		// handle parameters
		if (main.handleParameters(args))
		{
			// call the actual processing method
			main.process();
			
		}
		// terminate
		System.exit(0);
	}

	/**
	 * This method handles parameter arguments and prepares the actual call
	 * <strong>Note:</strong> This method set ups the static TLCGlobals variables
	 * @return status of parsing: true iff parameter check was ok, false otherwise
	 */
	// SZ Feb 23, 2009: added return status to indicate the error in parsing
	@SuppressWarnings("deprecation")
	public boolean handleParameters(String[] args)
	{
		// SZ Feb 20, 2009: extracted this method to separate the 
		// parameter handling from the actual processing

		int index = 0;
		while (index < args.length)
		{
			if (args[index].equals("-help"))
			{
				printHelp();
				System.exit(0);
			} else if (args[index].equals("-difftrace"))
			{
				index++;
				TLCGlobals.printDiffsOnly = true;
			} else if (args[index].equals("-deadlock"))
			{
				index++;
				deadlock = false;
			} else if (args[index].equals("-cleanup"))
			{
				index++;
				cleanup = true;
			} else if (args[index].equals("-nowarning"))
			{
				index++;
				TLCGlobals.warn = false;
			} else if (args[index].equals("-gzip"))
			{
				index++;
				TLCGlobals.useGZIP = false;
			} else if (args[index].equals("-terse"))
			{
				index++;
				Value.expand = false;
			} else if (args[index].equals("-view"))
			{
				index++;
				TLCGlobals.useView = true;
			} else if (args[index].equals("-debug"))
			{
				index++;
				TLCGlobals.debug = true;
			} else if (args[index].equals("-tool"))
			{
				index++;
				TLCGlobals.tool = true;
			} else if (args[index].equals("-help"))
			{
				printUsage();
				return false;
			} else if (args[index].equals("-config"))
			{
				index++;
				if (index < args.length)
				{
					configFile = args[index];
					int len = configFile.length();
					if (configFile.startsWith(".cfg", len - 4))
					{
						configFile = configFile.substring(0, len - 4);
					}
					index++;
				} else
				{
					printErrorMsg("Error: expect a file name for -config option.");
					return false;
				}
			} else if (args[index].equals("-dump"))
			{
				index++;
				if (index < args.length)
				{
					dumpFile = args[index];
					int len = dumpFile.length();
					if (!(dumpFile.startsWith(".dump", len - 5)))
					{
						dumpFile = dumpFile + ".dump";
					}
					index++;
				} else
				{
					printErrorMsg("Error: A file name for dumping states required.");
					return false;
				}
			} else if (args[index].equals("-dot"))
			{
				index++;
				if (index < args.length)
				{
					dotFile = args[index];
					int len = dotFile.length();
					if (!(dotFile.startsWith(".dot", len - 4)))
					{
						dotFile = dotFile + ".dot";
					}
					index++;
				} else
				{
					printErrorMsg("Error: A file name for dumping state transitions required.");
					return false;
				}
			} else if (args[index].equals("-coverage"))
			{
				index++;
				if (index < args.length)
				{
					try
					{
						TLCGlobals.coverageInterval = Integer.parseInt(args[index]) * 60 * 1000;
						if (TLCGlobals.coverageInterval < 0)
						{
							printErrorMsg("Error: expect a nonnegative integer for -coverage option.");
									return false;
						}
						index++;
					} catch (NumberFormatException e)
					{

						printErrorMsg("Error: An integer for coverage report interval required." + " But encountered "
								+ args[index]);
						return false;
					}
				} else
				{
					printErrorMsg("Error: coverage report interval required.");
					return false;
				}
			} else if (args[index].equals("-checkpoint"))
			{
				index++;
				if (index < args.length)
				{
					try
					{
						TLCGlobals.chkptDuration = Integer.parseInt(args[index]) * 1000 * 60;
						if (TLCGlobals.chkptDuration < 0)
						{
							printErrorMsg("Error: expect a nonnegative integer for -checkpoint option.");
									return false;
						}

						index++;
					} catch (Exception e)
					{
						printErrorMsg("Error: An integer for checkpoint interval is required. But encountered " + args[index]);
						return false;
					}
				} else
				{
					printErrorMsg("Error: checkpoint interval required.");
					return false;
				}
			} else if (args[index].equals("-maxSetSize"))
			{
				index++;
				if (index < args.length)
				{
					try
					{
						int bound = Integer.parseInt(args[index]);

						// make sure it's in valid range
						if (!TLCGlobals.isValidSetSize(bound)) {
							int maxValue = Integer.MAX_VALUE;
							printErrorMsg("Error: Value in interval [0, " + maxValue + "] for maxSetSize required. But encountered " + args[index]);
							return false;
						}
						TLCGlobals.setBound = bound;

						index++;
					} catch (Exception e)
					{
						printErrorMsg("Error: An integer for maxSetSize required. But encountered " + args[index]);
						return false;
					}
				} else
				{
					printErrorMsg("Error: maxSetSize required.");
					return false;
				}
			} else if (args[index].equals("-recover"))
			{
				index++;
				if (index < args.length)
				{
					fromChkpt = args[index++] + FileUtil.separator;
				} else
				{
					printErrorMsg("Error: need to specify the metadata directory for recovery.");
					return false;
				}
			} else if (args[index].equals("-metadir"))
			{
				index++;
				if (index < args.length)
				{
					TLCGlobals.metaDir = args[index++] + FileUtil.separator;
				} else
				{
					printErrorMsg("Error: need to specify the metadata directory.");
					return false;
				}
			} else if (args[index].equals("-workers"))
			{
				index++;
				if (index < args.length)
				{
					try
					{
						int num = Integer.parseInt(args[index]);
						if (num < 1)
						{
							printErrorMsg("Error: at least one worker required.");
							return false;
						}
						TLCGlobals.setNumWorkers(num);
						index++;
					} catch (Exception e)
					{
						printErrorMsg("Error: worker number required. But encountered " + args[index]);
						return false;
					}
				} else
				{
					printErrorMsg("Error: expect an integer for -workers option.");
					return false;
				}
			} else if (args[index].equals("-dfid"))
			{
				index++;
				if (index < args.length)
				{
					try
					{
						TLCGlobals.DFIDMax = Integer.parseInt(args[index]);
						if (TLCGlobals.DFIDMax < 0)
						{
							printErrorMsg("Error: expect a nonnegative integer for -dfid option.");
									return false;
						}
						index++;
					} catch (Exception e)
					{
						printErrorMsg("Errorexpect a nonnegative integer for -dfid option. " + "But encountered "
								+ args[index]);
						return false;
					}
				} else
				{
					printErrorMsg("Error: expect a nonnegative integer for -dfid option.");
					return false;
				}
			} else if (args[index].equals("-fp"))
			{
				index++;
				if (index < args.length)
				{
					try
					{
						fpIndex = Integer.parseInt(args[index]);
						if (fpIndex < 0 || fpIndex >= FP64.Polys.length)
						{
							printErrorMsg("Error: The number for -fp must be between 0 and " + (FP64.Polys.length - 1)
									+ " (inclusive).");
							return false;
						}
						index++;
					} catch (Exception e)
					{
						printErrorMsg("Error: A number for -fp is required. But encountered " + args[index]);
						return false;
					}
				} else
				{
					printErrorMsg("Error: expect an integer for -workers option.");
					return false;
				}
			} else if (args[index].equals("-fpmem"))
			{
				index++;
				if (index < args.length)
				{
					try
					{
						// -fpmem can be used in two ways:
							// a) to set the relative memory to be used for fingerprints (being machine independent)
						// b) to set the absolute memory to be used for fingerprints
						//
						// In order to set memory relatively, a value in the domain [0.0, 1.0] is interpreted as a fraction.
						// A value in the [2, Double.MaxValue] domain allocates memory absolutely.
						//
						// Independently of relative or absolute mem allocation,
						// a user cannot allocate more than JVM heap space
						// available. Conversely there is the lower hard limit TLC#MinFpMemSize.
						double fpMemSize = Double.parseDouble(args[index]);
						if (fpMemSize < 0) {
							printErrorMsg("Error: An positive integer or a fraction for fpset memory size/percentage required. But encountered " + args[index]);
							return false;
						} else if (fpMemSize > 1) {
							// For legacy reasons we allow users to set the
							// absolute amount of memory. If this is the case,
							// we know the user intends to allocate all 100% of
							// the absolute memory to the fpset.
							ToolIO.out
							.println("Using -fpmem with an abolute memory value has been deprecated. " +
									"Please allocate memory for the TLC process via the JVM mechanisms " +
									"and use -fpmem to set the fraction to be used for fingerprint storage.");
							fpSetConfiguration.setMemory((long) fpMemSize);
							fpSetConfiguration.setRatio(1.0d);
						} else {
							fpSetConfiguration.setRatio(fpMemSize);
						}
						index++;
					} catch (Exception e)
					{
						printErrorMsg("Error: An positive integer or a fraction for fpset memory size/percentage required. But encountered " + args[index]);
						return false;
					}
				} else
				{
					printErrorMsg("Error: fpset memory size required.");
					return false;
				}
			} else if (args[index].equals("-fpbits"))
			{
				index++;
				if (index < args.length)
				{
					try
					{
						int fpBits = Integer.parseInt(args[index]);

						// make sure it's in valid range
						if (!FPSet.isValid(fpBits)) {
							printErrorMsg("Error: Value in interval [0, 30] for fpbits required. But encountered " + args[index]);
							return false;
						}
						fpSetConfiguration.setFpBits(fpBits);

						index++;
					} catch (Exception e)
					{
						printErrorMsg("Error: An integer for fpbits required. But encountered " + args[index]);
						return false;
					}
				} else
				{
					printErrorMsg("Error: fpbits required.");
					return false;
				}
			} else if (args[index].equals("-path")) {
				index++;
				resolver.addPath(args[index]);
				index++;
			} else
			{
				if (args[index].charAt(0) == '-')
				{
					printErrorMsg("Error: unrecognized option: " + args[index]);
					return false;
				}
				if (mainFile != null)
				{
					printErrorMsg("Error: more than one input files: " + mainFile + " and " + args[index]);
					return false;
				}
				mainFile = args[index++];
				int len = mainFile.length();
				if (mainFile.startsWith(".tla", len - 4))
				{
					mainFile = mainFile.substring(0, len - 4);
				}
			}
		}

		if (mainFile == null)
		{
			printErrorMsg("Error: Missing input TLA+ module.");
			return false;
		}
		if (configFile == null)
		{
			configFile = mainFile;
		}

		if (TLCGlobals.debug) 
		{
			StringBuffer buffer = new StringBuffer("TLC argumens:");
			for (int i=0; i < args.length; i++)
			{
				buffer.append(args[i]);
				if (i < args.length - 1) 
				{
					buffer.append(" ");
				}
			}
			buffer.append("\n");
			DebugPrinter.print(buffer.toString());
		}

		// if no errors, print welcome message
		printWelcome();

		return true;
	}

	private void printHelp() {
		 	 System.out.println("* This State Extractor provides the following functionalities:");
			 System.out.println("*  Extraction of states, preconditions, postcondition and actions");
			 System.out.println("*");
			 System.out.println("* The command line also provides the following options:");
			 System.out.println("*  o -config file: provide the config file.");
			 System.out.println("*    Defaults to spec.cfg if not provided");
			 System.out.println("*  o -deadlock: do not check for deadlock.");
			 System.out.println("*    Defaults to checking deadlock if not specified");
			 System.out.println("*  o -recover path: recover from the checkpoint at path");
			 System.out.println("*    Defaults to scratch run if not specified");
			 System.out.println("*  o -bound: The upper limit for sets effectively limiting the number of init states");
			 System.out.println("*    (@see http://bugzilla.tlaplus.net/show_bug.cgi?id=264)");
			 System.out.println("*    Defaults to 1000000 if not specified");
			 System.out.println("*  o -metadir path: store metadata in the directory at path");
			 System.out.println("*    Defaults to specdir/states if not specified");
			 System.out.println("*  o -workers num: the number of TLC worker threads");
			 System.out.println("*    Defaults to 1");
			 System.out.println("*  o -dfid num: use depth-first iterative deepening with initial depth num");
			 System.out.println("*  o -cleanup: clean up the states directory");
			 System.out.println("*  o -dump file: dump all the states into file");
			 System.out.println("*  o -dot file: dump all the state transitions into file");
			 System.out.println("*  o -difftrace: when printing trace, show only");
			 System.out.println("*                the differences between successive states");
			 System.out.println("*    Defaults to printing full state descriptions if not specified");
			 System.out.println("*    (Added by Rajeev Joshi)");
			 System.out.println("*  o -terse: do not expand values in Print statement");
			 System.out.println("*    Defaults to expand value if not specified");
			 System.out.println("*  o -coverage minutes: collect coverage information on the spec,");
			 System.out.println("*                       print out the information every minutes.");
			 System.out.println("*    Defaults to no coverage if not specified");
			 System.out.println("*  o -nowarning: disable all the warnings");
			 System.out.println("*    Defaults to report warnings if not specified");
			 System.out.println("*  o -fp num: use the num'th irreducible polynomial from the list");
			 System.out.println("*    stored in the class FP64.");
			 System.out.println("*  o -view: apply VIEW (if provided) when printing out states.");
			 System.out.println("*  o -gzip: control if gzip is applied to value input/output stream.");
			 System.out.println("*    Defaults to use gzip.");
			 System.out.println("*  o -debug: debbuging information (non-production use)");
			 System.out.println("*  o -tool: tool mode (put output codes on console)");
			 System.out.println("*  o -checkpoint num: interval for check pointing (in minutes)");
			 System.out.println("*     Defaults to 30");
			 System.out.println("*  o -fpmem num: the number of megabytes of memory used to store");
			 System.out.println("*                the fingerprints of found states.");
			 System.out.println("*  Defaults to 1/4 physical memory.  (Added 6 Apr 2010 by Yuan Yu.)");
			 System.out.println("*  o -fpbits num: the number of msb used by MultiFPSet to create nested FPSets.");
			 System.out.println("*  Defaults to 1");
			 System.out.println("*  o -maxSetSize num: the size of the largest set TLC will enumerate.");
			 System.out.println("*                     default: 1000000");
		
	}

	/**
	 * The processing method
	 */
	public void process()
	{
		ToolIO.cleanToolObjects(TLCGlobals.ToolId);
		// UniqueString.initialize();

		// a JMX wrapper that exposes runtime statistics 
		TLCStandardMBean modelCheckerMXWrapper = TLCStandardMBean.getNullTLCStandardMBean();

		// SZ Feb 20, 2009: extracted this method to separate the 
		// parameter handling from the actual processing
		try
		{
			// Initialize:
			if (fromChkpt != null)
			{
				// We must recover the intern var table as early as possible
				UniqueString.internTbl.recover(fromChkpt);
			}
			if (cleanup && fromChkpt == null)
			{
				// clean up the states directory only when not recovering
				FileUtil.deleteDir(TLCGlobals.metaRoot, true);
			}
			FP64.Init(fpIndex);

			// model checking
			MP.printMessage(EC.TLC_MODE_MC);

			StateExtractor se = null;
			//TODO: Create a DepthFirst state implementation
			//if (TLCGlobals.DFIDMax == -1)
			//{
				se = new StateExtractor(mainFile, configFile, dumpFile, deadlock, fromChkpt, resolver, specObj, fpSetConfiguration);
				modelCheckerMXWrapper = new ModelCheckerMXWrapper((ModelChecker) se);
			/*} else
			{
				se = new DepthFirstStateExtractor(mainFile, configFile, dumpFile, deadlock, fromChkpt, true, resolver, specObj);
			}*/
			// The following statement moved to Spec.processSpec by LL on 10 March 2011               
			//                MP.printMessage(EC.TLC_STARTING);
			instance = se;
			se.modelCheck();
			if (dotFile != null) {
				OutputDotFile odf = new OutputDotFile(se.getTransitions(), se.getModule());
				odf.write(dotFile);
			}

		} catch (Throwable e)
		{
			if (e instanceof StackOverflowError)
			{
				System.gc();
				MP.printError(EC.SYSTEM_STACK_OVERFLOW, e);
			} else if (e instanceof OutOfMemoryError)
			{
				System.gc();
				MP.printError(EC.SYSTEM_OUT_OF_MEMORY, e);
			} else if (e instanceof RuntimeException) 
			{
				// SZ 29.07.2009 
				// printing the stack trace of the runtime exceptions
				MP.printError(EC.GENERAL, e);
				// e.printStackTrace();
			} else
			{
				MP.printError(EC.GENERAL, e);
			}
		} finally 
		{
			modelCheckerMXWrapper.unregister();
			MP.printMessage(EC.TLC_FINISHED);
			MP.flush();
		}
	}

	/**
	 * Sets resolver for the file names
	 * @param resolver a resolver for the names, if <code>null</code> is used, 
	 * the standard resolver {@link SimpleFilenameToStream} is used
	 */
	public void setResolver(SimpleFilenameToStream resolver)
	{
		this.resolver = resolver;
		ToolIO.setDefaultResolver(resolver);
	}

	/**
	 * Set external specification object
	 * @param specObj spec object created external SANY run
	 */
	public void setSpecObject(SpecObj specObj) 
	{
		this.specObj = specObj;
	}

	/**
	 * Delegate cancellation request to the instance
	 * @param flag
	 */
	public void setCanceledFlag(boolean flag)
	{
		if (this.instance != null) 
		{
			this.instance.setCancelFlag(flag);
			DebugPrinter.print("Cancel flag set to " + flag);
		}
	}

	/**
	 * Print out an error message, with usage hint
	 * @param msg, message to print
	 * TODO remove this method and replace the calls
	 */
	private void printErrorMsg(String msg)
	{
		printWelcome();
		MP.printError(EC.WRONG_COMMANDLINE_PARAMS_TLC, msg);
	}

	/**
	 * Prints the welcome message once per instance
	 */
	private void printWelcome()
	{
		if (!this.welcomePrinted) 
		{
			this.welcomePrinted = true;
			System.out.println("Welcome to State Extractor");
		}
	}

	/**
	 * 
	 */
	private void printUsage()
	{
		printWelcome();
		printHelp();
	}

	FPSetConfiguration getFPSetConfiguration() {
		return fpSetConfiguration;
	}
}
