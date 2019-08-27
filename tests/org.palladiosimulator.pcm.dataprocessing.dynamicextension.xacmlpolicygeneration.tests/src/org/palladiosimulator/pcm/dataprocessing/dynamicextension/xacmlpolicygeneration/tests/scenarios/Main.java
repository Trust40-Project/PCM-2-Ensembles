package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.scenarios;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.att.research.xacml.api.Decision;

/**
 * The main class for testing scenarios for PCM-2-XACML transformation.
 * Add scenarios here in order to do further testing.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class Main {
	public static void main(String[] args) {
		final List<TestScenario> scenarios = new ArrayList<>();
		
		// main functionality test
		final String ucTest = "UC-Test/";
		scenarios.add(new TestScenario(ucTest, "testTime.xml", Decision.PERMIT));
		scenarios.add(new TestScenario(ucTest, "testPermit_1.xml", Decision.PERMIT));
		scenarios.add(new TestScenario(ucTest, "testPermit_2.xml", Decision.PERMIT));
		scenarios.add(new TestScenario(ucTest, "testPermit_3.xml", Decision.PERMIT));
		scenarios.add(new TestScenario(ucTest, "testDeny_1.xml", Decision.DENY));
		scenarios.add(new TestScenario(ucTest, "testDeny_2.xml", Decision.DENY));
		scenarios.add(new TestScenario(ucTest, "testDeny_3.xml", Decision.DENY));
		scenarios.add(new TestScenario(ucTest, "testDeny_4.xml", Decision.DENY));

		// 2nd rule test
		scenarios.add(new TestScenario(ucTest, "testPermit2.xml", Decision.PERMIT));
		scenarios.add(new TestScenario(ucTest, "testDeny2.xml", Decision.DENY));
		scenarios.add(new TestScenario(ucTest, "testDeny2_2.xml", Decision.DENY));

		// empty model test
		scenarios.add(new TestScenario("UC-Empty/", "testEmpty.xml", Decision.NOTAPPLICABLE));
		
		// uc3 test
		final String uc3 = "UC3/";
		scenarios.add(new TestScenario(uc3, "test3Permit_1.xml", Decision.PERMIT));
		scenarios.add(new TestScenario(uc3, "test3Permit_2.xml", Decision.PERMIT));
		scenarios.add(new TestScenario(uc3, "test3Deny_1.xml", Decision.DENY));
		scenarios.add(new TestScenario(uc3, "test3Deny_2.xml", Decision.DENY));
		
		// uc-combined test
		final String ucc = "UC-Combined/";
		scenarios.add(new TestScenario(ucc, "uccPermit_1.xml", Decision.PERMIT));
		scenarios.add(new TestScenario(ucc, "uccDeny_1.xml", Decision.DENY));
		
		// uc-shift test
		final String ucShift = "UC-Shift/";
		scenarios.add(new TestScenario(ucShift, "shiftPermit_1.xml", Decision.PERMIT));
		scenarios.add(new TestScenario(ucShift, "shiftDeny_1.xml", Decision.DENY));
		
		// uc-running test
		final String ucRunning = "UC-Running/";
		scenarios.add(new TestScenario(ucRunning, "runningPermit_1.xml", Decision.PERMIT));
		scenarios.add(new TestScenario(ucRunning, "runningPermit_2.xml", Decision.PERMIT));
		scenarios.add(new TestScenario(ucRunning, "runningDeny_1.xml", Decision.DENY));
		
		// TODO add scenarios here
		
		// test with relevant system time
		final LocalTime now = LocalTime.now(ZoneId.systemDefault()).minusHours(1); // TODO adapt if necessary
		System.out.println("time UTC: " + now + "\n");
		final boolean permit = now.toSecondOfDay() >= 6 * 3600 && now.toSecondOfDay() <= 14 * 3600;
		final Decision decisionTimeRelevant = permit ? Decision.PERMIT : Decision.DENY;
		scenarios.add(new TestScenario(ucTest, "test.xml", decisionTimeRelevant));
		
		var handler = new TestScenarioHandler(scenarios);
		handler.testAll();
	}
}
