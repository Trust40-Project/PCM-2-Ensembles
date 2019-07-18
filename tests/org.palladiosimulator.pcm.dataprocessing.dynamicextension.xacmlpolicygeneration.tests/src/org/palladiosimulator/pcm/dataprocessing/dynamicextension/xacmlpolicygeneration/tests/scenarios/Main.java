package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.scenarios;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.att.research.xacml.api.Decision;

public class Main {
	public static void main(String[] args) {
		final List<TestScenario> scenarios = new ArrayList<>();
		
		scenarios.add(new TestScenario("UC-Test/", "testTime.xml", Decision.PERMIT));
		
		final LocalTime now = LocalTime.now(ZoneId.systemDefault()).minusHours(1);
		System.out.println("time UTC: " + now + "\n");
		
		final boolean permit = now.toSecondOfDay() >= 6 * 3600 && now.toSecondOfDay() <= 14 * 3600;
		final Decision decisionTimeRelevant = permit ? Decision.PERMIT : Decision.DENY;
		
		scenarios.add(new TestScenario("UC-Test/", "test.xml", decisionTimeRelevant));

		// TODO add scenarios here
		
		var handler = new TestScenarioHandler(scenarios);
		handler.testAll();
	}
}
