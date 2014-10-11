package project.logic;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

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
		port.save("testPortfolioFile.obj");
	}
	
	@Test
	public void testPortfolioGetOngoingProjects() {
		port.init(testData);
		Collections.sort(testData);
		ArrayList<Project> ongoing = port.getOngoingProjects();
		assertEquals(testData.get(1), ongoing.get(0));
		assertEquals(testData.get(4), ongoing.get(1));
	}
	
	@Test
	public void testPortfolioGetFinishedProjects() {
		port.init(testData);
		Collections.sort(testData);
		ArrayList<Project> finished = port.getFinishedProjects();
		assertEquals(testData.get(0), finished.get(0));
		assertEquals(testData.get(2), finished.get(1));
		assertEquals(testData.get(3), finished.get(2));
	}
	
	@Test
	public void testPortfolioInit() {
		port.init(testData);
		Collections.sort(testData);
		assertEquals(testData, port.getPortfolio());
	}
	
	@Test
	public void testPortfolioFindByCodeExcluding() {
		int index = port.findByCode("P0002");
		assertTrue(port.findByCode("P0002", index)==-1);
		assertTrue(port.findByCode("P0002",index+1)>0);
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
	public void testPortfolioAddRemove() {
		fp.setCode(port.getNewCode());
		port.add(fp, false);
		op.setCode(port.getNewCode());
		port.add(op, false);
		
		int index1 = port.findByCode(fp.getCode());
		int index2 = port.findByCode(op.getCode());
		
		assertTrue(index1>0);
		assertTrue(index2>0);
		
		port.remove(index2, false);
		port.remove(index1, false);
		
		assertTrue(port.findByCode(fp.getCode())<0);
		assertTrue(port.findByCode(op.getCode())<0);
		
		
	}
	
	@Test
	public void testPortfolioSave() {
		assertTrue(port.save("testPortfolioFileSave.obj"));
	}
	
	@Test
	public void testPortfolioReplace() {
		Project originalProject = port.get(1);
		port.replaceProject(fp, 1, false);
		
		assertFalse(originalProject.equals(port.get(1)));
		assertTrue(fp.equals(port.get(1)));
		
		port.replaceProject(op, 1, false);
		
		assertFalse(fp.equals(port.get(1)));
		assertTrue(op.equals(port.get(1)));
		
		port.replaceProject(originalProject, 1, false);
	}
	
	@Test
	public void testPortfolioCount() {
		assertEquals(2, port.getOngoingCount());
		assertEquals(3, port.getFinishedCount());
	}
	
	
	@Test
	@SuppressWarnings("unused")
	public void testPortfolioIndexOutOfBounds() {
		try {
			Project p1 = port.get(-1);
		} catch (Exception e) {
			assertTrue(e instanceof IndexOutOfBoundsException);
		}
		try {
			Project p2 = port.get(50);
		} catch (Exception e) {
			assertTrue(e instanceof IndexOutOfBoundsException);
		}
		
	}
	
	@Test
	public void testPortfolioInitFile() {
		port.init("testPortfolioFile.obj");
		Collections.sort(testData);
		ArrayList<Project> port_from_file = port.getPortfolio();
		for(int i=0;i<testData.size(); i++) {
			assertTrue(testData.get(i).equals(port_from_file.get(i)));
		}
	}
	
	@Test
	public void testPortfolioListProjects() {
		port.listProjects();
	}
}
