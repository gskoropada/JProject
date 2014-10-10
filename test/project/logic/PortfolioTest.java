package project.logic;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

public class PortfolioTest {
	
	private static Portfolio port;
	private static ArrayList<Project> testData = new ArrayList<Project>();
	private static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	private static FinishedProject fp;
	private static OngoingProject op;

	@BeforeClass
	public static void setUp() {
		port = new Portfolio();
		try {
		testData.add(new FinishedProject("P0001","Project 1", df.parse("15-08-2010"),"Client 1",df.parse("15-10-2010"), 5000));
		testData.add(new OngoingProject("P0005","Project 5", df.parse("15-08-2010"),"Client 5",df.parse("15-10-2010"), 5000,50));
		testData.add(new FinishedProject("P0004","Project 4", df.parse("15-08-2010"),"Client 4",df.parse("15-10-2010"), 5000));
		testData.add(new FinishedProject("P0003","Project 3", df.parse("15-08-2010"),"Client 3",df.parse("15-10-2010"), 5000));
		testData.add(new OngoingProject("P0002","Project 2", df.parse("15-08-2010"),"Client 2",df.parse("15-10-2010"), 5000,50));
		op = new OngoingProject("PT001","Project Test 1", df.parse("15-08-2010"),"Client 1",df.parse("15-10-2010"), 5000,50);
		fp = new FinishedProject("PT002","Project Test 2", df.parse("15-08-2010"),"Client 1",df.parse("15-10-2010"), 5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		port.init(testData);
	}
	
	@Test
	public void testPortfolioSort() {
		assertEquals("P0001", port.get(0).getCode());
		assertEquals("P0002", port.get(1).getCode());
		assertEquals("P0003", port.get(2).getCode());
		assertEquals("P0004", port.get(3).getCode());
		assertEquals("P0005", port.get(4).getCode());
	}
	
	@Test
	public void testPortfolioFindByCode() {
		assertEquals(0, port.findByCode("P0001"));
		assertEquals(1, port.findByCode("P0002"));
		assertEquals(2, port.findByCode("P0003"));
		assertEquals(3, port.findByCode("P0004"));
		assertEquals(4, port.findByCode("P0005"));
		assertTrue(port.findByCode("P9999")<0);
	}
	
	@Test
	public void testPortfolioGetNewCode() {
		assertEquals("P0006",port.getNewCode());
	}
	
	@Test
	public void testPortfolioSave() {
		assertTrue(port.save("testPortfolioFile.obj"));
	}
	
	@Test
	public void testPortfolioReplace() {
		Project originalProject = port.get(1);
		port.replaceProject(fp, 1, false);
		
		assertFalse(originalProject.equals(port.get(1)));
		assertTrue(fp.equals(port.get(1)));
		
		port.replaceProject(originalProject, 1, false);
	}
	
	@Test
	public void testPortfolioCount() {
		assertEquals(2, port.getOngoingCount());
		assertEquals(3, port.getFinishedCount());
	}
	
}
