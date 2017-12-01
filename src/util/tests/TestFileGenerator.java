package util.tests;

import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import junitx.framework.FileAssert;

public class TestFileGenerator {

	public static int NAME = 0;
	public static int PATH = 1;



	public static String[][] testCases = {
		{"test", "src/examples/test1"},
		{"test", "src/examples/test2"},
		{"test", "src/examples/test3"},
		{"DieHard", "src/examples/DieHard"},
		{"MC", "src/examples/Model_1"},
		{"MC", "src/examples/Model_2"}

	};





	public static void generateAllTestFiles() {

		for (String[] strArray : testCases) {
			generateTestFile(strArray[PATH], strArray[NAME]);
		}
	}

	public static void generateTestFile(String path, String name) {
		String output = path+File.separator+"output.dot";
		ProcessBuilder pb = new ProcessBuilder("java", "-cp", "bin/"+File.pathSeparator+"lib/tla2tools.jar", "stateextractor.Main", name, "-path", path, "-dot", output);
		pb.redirectErrorStream(true);
		try {
			Process p = pb.start();
			inheritIO(p.getInputStream(), System.out);
			p.waitFor();
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	/*
	 * only works in linux
	 */
	public static void generateSvgFiles() {
		for (String[] strArray : testCases) {
			generateSvgFile(strArray[PATH], strArray[NAME]);	
		}
	}

	static void generateSvgFile(String path, String name) {
		String output = path+File.separator+"output.dot";
		String svg = path+File.separator+"output.svg";
		ProcessBuilder pb = new ProcessBuilder("dot", "-Tsvg", output);
		File svgFile  = new File(svg);
		
		try {
			FileOutputStream fos = new FileOutputStream(svgFile, false);
			PrintStream printStream = new PrintStream(fos);
			pb.redirectErrorStream(false);
			Process p = pb.start();
			inheritIO(p.getInputStream(), printStream);
			p.waitFor();
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void inheritIO(final InputStream src, final PrintStream dest) {
		new Thread(new Runnable() {
			public void run() {
				Scanner sc = new Scanner(src);
				while (sc.hasNextLine()) {
					dest.println(sc.nextLine());
				}
				sc.close();
			}
		}).start();
	}

	public static void deleteAllTestFiles() {
		for (String[] strArray : testCases) {
			String output = strArray[PATH]+File.pathSeparator+"output.dot";
			String svg = strArray[PATH]+File.pathSeparator+"output.svg";
			new File(output).delete();
			new File(svg).delete();
		}
	}

	public static void main(String [] args) {
		File file  = new File("src/util/tests/StateExtractorTest.java");
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);

			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			writePreClass(bufferedWriter);
			for (String[] strArray : testCases) {
				writeTest(bufferedWriter, strArray);
			}
			writePostClass(bufferedWriter);

			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void writePostClass(BufferedWriter bufferedWriter) throws IOException {
		bufferedWriter.write("}\n");

	}

	private static void writeTest(BufferedWriter bufferedWriter, String[] strArray) throws IOException {
		String testName = strArray[PATH].replace("/", "_")+"_"+strArray[NAME];
		
		bufferedWriter.write("@Test\n"
				+ "public void test_"+testName+"() {\n"
				+ " 	TestFileGenerator.runTest(\""+strArray[PATH]+"\", \""+strArray[NAME]+"\");\n"
				+ "}\n");
	}

	private static void writePreClass(BufferedWriter bufferedWriter) throws IOException {
		bufferedWriter.write("package util.tests;\n"
				+ "import org.junit.Test;\n"
				+ "public class StateExtractorTest {\n");

	}
	
	static void runTest(String path, String name) {
		TestFileGenerator.generateTestFile(path ,name);
			File baseline = new File(path+File.separator+"baseline.dot");
			File output = new File(path+File.separator+"output.dot");
			if (output.exists()) {
				if (baseline.exists()) {
					FileAssert.assertEquals(name + " Failed", output, baseline);
					} else {
					//fail("Baseline doesnt exist");
				}
			} else {
				fail("Output not generated");
			}
//		 TestFileGenerator.generateSvgFile(path,name);
//		 File svg = new File(path+File.separator+"output.svg");
//		 if (!svg.exists()) {
//				fail("SVG not generated");
//		 }
	}
}
