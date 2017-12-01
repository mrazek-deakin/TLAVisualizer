package util.tests;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import util.SimpleFilenameToStream;


public class SimpleFilenameToStreamTest {

	@Test
	public void testResolve() {
		SimpleFilenameToStream resolver = new SimpleFilenameToStream("src"+File.separatorChar+"examples"+File.separatorChar+"DieHard");
		assertNull("Should return null as there is no asdf module", resolver.resolve("asdf", true));
		assertNull("Shouldnt find naturals module", resolver.resolve("Naturals", false));
		assertTrue("Should find the Naturals module", resolver.resolve("Naturals", true).exists());
		assertTrue("Should find the Naturals module", resolver.resolve("Naturals.tla", false).exists());
		assertTrue("Should find the DieHard module", resolver.resolve("DieHard", true).exists());
		assertTrue("Should find the DieHard module", resolver.resolve("DieHard.tla", false).exists());
	}
	
	@Test
	public void testIsStandardModule() {
		SimpleFilenameToStream resolver = new SimpleFilenameToStream("src"+File.separatorChar+"examples"+File.separatorChar+"DieHard");
		assertFalse("Diehard is not standard", resolver.isStandardModule("DieHard"));
		assertFalse("asdf doesnt exist", resolver.isStandardModule("asdf"));
		assertTrue("Naturals is standard", resolver.isStandardModule("Naturals"));
	}
	
	@Test
	public void testAdd() {
		SimpleFilenameToStream resolver = new SimpleFilenameToStream();
		assertEquals("Initially only 1 library path, the default", resolver.libraryPaths.length, 1);
		resolver.addPath("asdf");
		assertEquals("Added a invalid library path", resolver.libraryPaths.length, 1);
		resolver.addPath("src"+File.separatorChar+"examples"+File.separatorChar+"DieHard");
		assertEquals("Added a valid library path", resolver.libraryPaths.length, 2);
	}
	
	
	
	

}
