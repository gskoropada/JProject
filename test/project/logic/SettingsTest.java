package project.logic;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

public class SettingsTest {

	private static Settings s = new Settings();
	
	@BeforeClass
	public static void setUp() {
		s.save();
	}
	
	@Test
	public void testSaveSettings() {
		Settings old_settings = new Settings();
		old_settings.read();
		File old_settings_file = new File("settings.cfg");
		
		old_settings_file.delete();

		s.setGUI(false);
		s.setUpdateDB(false);
		s.setDefaultDateFormat("dd-MM-yy");
		s.setWorkingFile("testPortfolio.obj");
		s.setDefaultsFile("testDefaults.obj");
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(s.save());
		
		s = new Settings();
		
		assertTrue(s.read());
		assertTrue(s.getDefaultDateFormat().equals("dd-MM-yy"));
		assertFalse(s.isGUI());
		assertFalse(s.isUpdateDB());
		assertTrue(s.getWorkingFile().equals("testPortfolio.obj"));
		assertTrue(s.getDefaultsFile().equals("testDefaults.obj"));
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(s.save());
		
		s = new Settings(old_settings);
		
		assertTrue(s.save());

	}

}
