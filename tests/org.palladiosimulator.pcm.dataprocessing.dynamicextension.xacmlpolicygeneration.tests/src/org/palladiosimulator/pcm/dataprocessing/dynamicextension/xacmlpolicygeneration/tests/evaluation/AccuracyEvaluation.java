package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.scenarios.TestScenario;

import com.att.research.xacml.api.Decision;

public class AccuracyEvaluation {
    @TestFactory
    public Collection<DynamicTest> createTests() {
        final List<TestScenario> scenarios = new ArrayList<>();
        
        {
            // UC0
            final String uc0 = "UC0/";
            scenarios.add(new TestScenario(uc0, getXml(uc0, true, ""), Decision.PERMIT));
            scenarios.add(new TestScenario(uc0, getXml(uc0, false, "State"), Decision.DENY));
            scenarios.add(new TestScenario(uc0, getXml(uc0, false, "Time"), Decision.DENY));
            scenarios.add(new TestScenario(uc0, getXml(uc0, false, "Role"), Decision.DENY));
            scenarios.add(new TestScenario(uc0, getXml(uc0, false, "Organisation"), Decision.DENY));
        }
        
        {
            // UC1
            final String uc1 = "UC1/";
            scenarios.add(new TestScenario(uc1, getXml(uc1, true, "Send"), Decision.PERMIT));
            scenarios.add(new TestScenario(uc1, getXml(uc1, true, "Receive"), Decision.PERMIT));
            scenarios.add(new TestScenario(uc1, getXml(uc1, true, "Show"), Decision.PERMIT));
            scenarios.add(new TestScenario(uc1, getXml(uc1, false, "Send"), Decision.DENY));
            scenarios.add(new TestScenario(uc1, getXml(uc1, false, "Receive"), Decision.DENY));
            scenarios.add(new TestScenario(uc1, getXml(uc1, false, "Show"), Decision.DENY));
        }
        
        {
            // UC2
            final String uc2 = "UC2/";
            scenarios.add(new TestScenario(uc2, getXml(uc2, true, "InformForeman"), Decision.PERMIT));
            scenarios.add(new TestScenario(uc2, getXml(uc2, true, "InformSupplier"), Decision.PERMIT));
            scenarios.add(new TestScenario(uc2, getXml(uc2, false, "InformForemanRole"), Decision.DENY));
            scenarios.add(new TestScenario(uc2, getXml(uc2, false, "InformForemanLocation"), Decision.DENY));
            scenarios.add(new TestScenario(uc2, getXml(uc2, false, "InformForemanState"), Decision.DENY));
            scenarios.add(new TestScenario(uc2, getXml(uc2, false,"InformSupplierRole"), Decision.DENY));
            scenarios.add(new TestScenario(uc2, getXml(uc2, false, "InformSupplierLocation"), Decision.DENY));
        }
        
        {
            // UC3
            final String uc3 = "UC3/";
            scenarios.add(new TestScenario(uc3, getXml(uc3, true, "Send"), Decision.PERMIT));
            scenarios.add(new TestScenario(uc3, getXml(uc3, true, "ReceiveA"), Decision.PERMIT));
            scenarios.add(new TestScenario(uc3, getXml(uc3, true, "ReceiveB"), Decision.PERMIT));
            scenarios.add(new TestScenario(uc3, getXml(uc3, false, "Send"), Decision.DENY));
            scenarios.add(new TestScenario(uc3, getXml(uc3, false, "Receive"), Decision.DENY));
        }
        
        {
            // UC4
            final String uc4 = "UC4/";
            scenarios.add(new TestScenario(uc4, getXml(uc4, true, "Access"), Decision.PERMIT));
            scenarios.add(new TestScenario(uc4, getXml(uc4, false, "AccessTime"), Decision.DENY));
            scenarios.add(new TestScenario(uc4, getXml(uc4, false, "AccessLocation"), Decision.DENY));

            // UC5
            final String uc5 = "UC5/";
            // this scenario exists only for creation of the xacml file, the evaluation of the alternative
            // model is considered in the evaluation of UC5 the ensembles generation
            scenarios.add(new TestScenario(uc5, getXml(uc4, true, "Access"), Decision.PERMIT));
        }
        
        {
            // UC6
            final String uc6 = "UC-Combined/";
            //TODO wahrscheinlich Privacy Level checks fuer permit und deny und fuer verschiedene incident sends
            scenarios.add(new TestScenario(uc6, getXml("UC6/", true, ""), Decision.PERMIT));
        }
        
        final List<DynamicTest> tests = new ArrayList<DynamicTest>(scenarios.size());
        for (final TestScenario scenario : scenarios) {
            tests.add(DynamicTest.dynamicTest(scenario.getTestName(), scenario));
        }
        return tests;
    }
    
    private static String getXml(final String usecase, final boolean isPermit, final String info) {
        final String usecaseName = usecase.substring(0, usecase.length() - 1);
        return String.format("eval%s%s%s.xml", usecaseName, isPermit ? "Permit" : "Deny", info);
    }
}
