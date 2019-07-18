package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.scenarios;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.pep.PEPEngine;
import com.att.research.xacml.api.pep.PEPEngineFactory;
import com.att.research.xacml.api.pep.PEPException;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.std.dom.DOMStructureException;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.XACMLPolicyWriter;

/**
 * Handles test scenarios.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class TestScenarioHandler {
	private List<TestScenario> scenarios;
	
	public TestScenarioHandler(final List<TestScenario> scenarios) {
		this.scenarios = scenarios;
	}
	
	public void testAll() {
		for (final TestScenario scenario : this.scenarios) {
			System.out.println(scenario.getTestName());
			testScenario(scenario);
			System.out.println();
		}
	}
	
	private void testScenario(final TestScenario scenario) {
		ContextHandler ch = new ContextHandler(scenario.getDynamicPath(), scenario.getDataPath());
		var policySet = ch.createPolicySet();
		// write policySet
		final Path filenamePolicySet = Path.of(TestScenario.PATH_OUTPUT_POLICYSET);
		XACMLPolicyWriter.writePolicyFile(filenamePolicySet, policySet);
		
		// Request-Test
		try {
			testRequest(scenario);
		} catch (DOMStructureException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		} catch (PEPException e) {
			e.printStackTrace();
		}
	}
	
	private void testRequest(final TestScenario scenario) throws DOMStructureException, PEPException, FactoryException {
		Request request =  DOMRequest.load(new File(scenario.getTestRequestPath()));
		
		PEPEngine pep = PEPEngineFactory.newInstance().newEngine();
		final var result = pep.decide(request);
		System.out.println(result);
		if (!result.getResults().iterator().next().getDecision().equals(scenario.getExpected())) {
			System.err.println("unexpected decision!, expected= " + scenario.getExpected());
		}
	}
}
