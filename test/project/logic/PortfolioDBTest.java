package project.logic;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;

import org.junit.BeforeClass;
import org.junit.Test;

public class PortfolioDBTest {
	private static Portfolio port = new Portfolio();
	private static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	private static FinishedProject fp;
	private static OngoingProject op;
	
	@BeforeClass
	public static void setUp() {
		try {
		op = new OngoingProject("TP001","Project Test 1", df.parse("15-08-2010"),"Client 1",df.parse("15-10-2010"), 5000,50);
		fp = new FinishedProject("TP002","Project Test 2", df.parse("15-08-2010"),"Client 1",df.parse("15-10-2010"), 5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPortfolioDB() {
		port.initDB();
		port.add(op, true);
		port.add(fp, true);
		
		assertTrue(port.findByCode(op.getCode())>0);
		assertTrue(port.findByCode(fp.getCode())>0);
		
		try {
			op = new OngoingProject("TP001","Project Test 1 - Modified", df.parse("15-08-2010"),"Client 1",df.parse("15-10-2010"), 5000,50);
			fp = new FinishedProject("TP002","Project Test 2 - Modified", df.parse("15-08-2010"),"Client 1",df.parse("15-10-2010"), 5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		port.replaceProject(op, port.findByCode("TP001"), true);
		port.replaceProject(fp, port.findByCode("TP002"), true);
		
		assertTrue(port.get(port.findByCode(op.getCode())).equals(op));
		assertTrue(port.get(port.findByCode(fp.getCode())).equals(fp));
		
		try {
			fp = new FinishedProject("TP001","Project Test 2 - Modified", df.parse("15-08-2010"),"Client 1",df.parse("15-10-2010"), 5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		port.replaceProject(fp, port.findByCode("TP001"), true);
		
		assertTrue(port.get(port.findByCode(fp.getCode())).equals(fp));
		
		port.replaceProject(op, port.findByCode("TP001"), true);
		
		assertTrue(port.get(port.findByCode(op.getCode())).equals(op));
		
		port.remove(port.findByCode("TP001"), true);
		port.remove(port.findByCode("TP002"), true);
		
		assertTrue(port.findByCode(fp.getCode())<0);
		assertTrue(port.findByCode(op.getCode())<0);
		
		port.close();
	}

}
