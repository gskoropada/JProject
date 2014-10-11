package project.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	project.logic.PortfolioTest.class,
	project.logic.ProjectTest.class,
	project.logic.SettingsTest.class,
	project.logic.PortfolioDBTest.class
})
public class JProjectTestSuite {

}
