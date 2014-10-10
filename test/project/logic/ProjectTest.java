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
	}
	
	@Test
	public void ProjectCompareToTest() {
		assertTrue(p1.compareTo(p2)==0);
		assertTrue(p3.compareTo(p4)==0);
		assertTrue(p1.compareTo(p3)<0);
		assertTrue(p4.compareTo(p2)>0);
	}

}
