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
            scenarios.add(new TestScenario(uc0, "evalUC0Permit.xml", Decision.PERMIT));
            scenarios.add(new TestScenario(uc0, "evalUC0DenyState.xml", Decision.DENY));
            scenarios.add(new TestScenario(uc0, "evalUC0DenyTime.xml", Decision.DENY));
            scenarios.add(new TestScenario(uc0, "evalUC0DenyRole.xml", Decision.DENY));
            scenarios.add(new TestScenario(uc0, "evalUC0DenyOrganisation.xml", Decision.DENY));
        }
        
        {
            // UC1
            final String uc1 = "UC1/";
            scenarios.add(new TestScenario(uc1, "evalUC1PermitSend.xml", Decision.PERMIT));
            scenarios.add(new TestScenario(uc1, "evalUC1PermitReceive.xml", Decision.PERMIT));
            scenarios.add(new TestScenario(uc1, "evalUC1PermitShow.xml", Decision.PERMIT));
            scenarios.add(new TestScenario(uc1, "evalUC1DenySend.xml", Decision.DENY));
            scenarios.add(new TestScenario(uc1, "evalUC1DenyReceive.xml", Decision.DENY));
            scenarios.add(new TestScenario(uc1, "evalUC1DenyShow.xml", Decision.DENY));
        }
        
        {
            // UC2
            final String uc2 = "UC2/";
            scenarios.add(new TestScenario(uc2, "evalUC2PermitInformForeman.xml", Decision.PERMIT));
            scenarios.add(new TestScenario(uc2, "evalUC2PermitInformSupplier.xml", Decision.PERMIT));
            scenarios.add(new TestScenario(uc2, "evalUC2DenyInformForemanRole.xml", Decision.DENY));
            scenarios.add(new TestScenario(uc2, "evalUC2DenyInformForemanLocation.xml", Decision.DENY));
            scenarios.add(new TestScenario(uc2, "evalUC2DenyInformSupplierRole.xml", Decision.DENY));
            scenarios.add(new TestScenario(uc2, "evalUC2DenyInformSupplierLocation.xml", Decision.DENY));
        }
        
        {
            // UC3
            final String uc3 = "UC3/";
            scenarios.add(new TestScenario(uc3, "evalUC3PermitSend.xml", Decision.PERMIT));
            scenarios.add(new TestScenario(uc3, "evalUC3PermitReceiveA.xml", Decision.PERMIT));
            scenarios.add(new TestScenario(uc3, "evalUC3PermitReceiveB.xml", Decision.PERMIT));
            scenarios.add(new TestScenario(uc3, "evalUC3DenySend.xml", Decision.DENY));
            scenarios.add(new TestScenario(uc3, "evalUC3DenyReceive.xml", Decision.DENY));
        }
        
        final List<DynamicTest> tests = new ArrayList<DynamicTest>(scenarios.size());
        for (final TestScenario scenario : scenarios) {
            tests.add(DynamicTest.dynamicTest(scenario.getTestName(), scenario));
        }
        return tests;
    }
}
