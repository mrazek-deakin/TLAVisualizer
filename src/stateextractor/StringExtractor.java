package stateextractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import tla2sany.st.Location;
import util.FilenameToStream;

/**
 * This is a filthy hack to get the predicate name out. Should be done in the parser somewhere
 * @author Cameron Cross
 *
 */

public class StringExtractor {
	public static String[] extractSection(FilenameToStream resolver, Location loc) {
		ArrayList<String> lines = new ArrayList<String>();
		try {
			
			FileInputStream in = new FileInputStream(resolver.resolve(loc.source(), true));
			BufferedReader reader =
				      new BufferedReader(new InputStreamReader(in));
			for (int i = 0; i < loc.beginLine()-1; i++) {
				reader.readLine();
			}
			int length = loc.beginColumn()+loc.endColumn();
			for (int i = loc.beginLine(); i < loc.endLine()+1; i++) {
				String line = reader.readLine();
				line = line.substring(loc.beginColumn()-1);
				lines.add(line);
			}
			reader.close();
			in.close();
			String arr[] = new String[lines.size()];
			lines.toArray(arr);
			return arr;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public static String extractSectionAsString(FilenameToStream resolver, Location loc) {
		String arr[] = extractSection(resolver, loc);
		StringBuilder sb = new StringBuilder();
		for (String str : arr) {
			sb.append(str);
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
