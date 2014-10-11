package project.logic;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;

import org.junit.BeforeClass;
import org.junit.Test;

public class ProjectTest {

	private static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	private static Project p1, p2, p3, p4;
	
	@BeforeClass
	public static void setUp() {
		try {
			p1 = new OngoingProject("P1","P 1", df.parse("15-10-2014"),"C 1", df.parse("15-11-2014"), 5000, 15 );
			p2 = new OngoingProject("P1","P 1", df.parse("15-10-2014"),"C 1", df.parse("15-11-2014"), 5000, 15 );
			p3 = new FinishedProject("P2","P 2", df.parse("15-10-2014"),"C 1", df.parse("15-11-2014"), 5000 );
			p4 = new FinishedProject("P2","P 2", df.parse("15-10-2014"),"C 1", df.parse("15-11-2014"), 5000 );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void ProjectEqualsTest() {
		assertTrue(p1.equals(p2));
		assertTrue(p3.equals(p4));
		
		assertFalse(p1.equals(p3));
		assertFalse(p2.equals(p4));
		
		assertFalse(p1.equals(new OngoingProject()));
		assertFalse(p3.equals(new FinishedProject()));
	}
	
	@Test
	public void ProjectCompareToTest() {
		assertTrue(p1.compareTo(p2)==0);
		assertTrue(p3.compareTo(p4)==0);
		assertTrue(p1.compareTo(p3)<0);
		assertTrue(p4.compareTo(p2)>0);
	}
	
	@Test
	public void ProjectToTable() {
		Object[] p1_table = p1.toTable();
		Object[] p1_table_expected = {
				p1.getCode(),
				p1.getName(),
				p1.getClient(),
				p1.getStartDate(),
				((OngoingProject)p1).getDeadline(),
				((OngoingProject)p1).getBudget(),
				((OngoingProject)p1).getCompletion()};
		
		for(int i=0;i<p1_table_expected.length; i++) {
			assertTrue(p1_table_expected[i].equals(p1_table[i]));
		}
		
		Object[] p3_table = p3.toTable();
		Object[] p3_table_expected = {
				p3.getCode(),
				p3.getName(),
				p3.getClient(),
				p3.getStartDate(),
				((FinishedProject)p3).getEndDate(),
				((FinishedProject)p3).getTotalCost()};

		for(int i=0;i<p3_table_expected.length; i++) {
			assertTrue(p3_table_expected[i].equals(p3_table[i]));
		}
	}
	
	@Test
	public void ProjectConstructorsTest() {
		Project tp = new OngoingProject(p1);
		
		assertTrue(tp.equals(p1));
		
		tp = new FinishedProject(p3);
		
		assertTrue(tp.equals(p3));
		
		tp = new FinishedProject(p1);
		
		assertTrue(tp instanceof FinishedProject);
		assertFalse(tp.equals(p1));
		
		tp = new OngoingProject(p3);
		
		assertTrue(tp instanceof OngoingProject);
		assertFalse(tp.equals(p3));
	}
	
}
