package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers.SampleHandler;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

/**
 * Represents a policy set.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class PolicySet {
    private final List<PolicyType> policies;

    /**
     * Creates a new policy set with the given policies.
     * 
     * @param policies
     *            - the given policies
     */
    public PolicySet(final List<PolicyType> policies) {
        this.policies = policies;
    }

    /**
     * Generates the combined XACML policy set for all actions.
     * 
     * @return the combined XACML policy set
     */
    public PolicySetType getPolicySet() {
        final PolicySetType policySet = new PolicySetType();
        policySet.setDescription("all policies combined"); // TODO
        policySet.setPolicySetId("completePolicySet"); // TODO
        policySet.setTarget(new TargetType());
        for (final PolicyType policy : this.policies) {
            final QName qname = new QName(XACML3.XMLNS, XACML3.ELEMENT_POLICY);
            policySet.getPolicySetOrPolicyOrPolicySetIdReference()
                    .add(new JAXBElement<PolicyType>(qname, PolicyType.class, policy));
        }
        if (this.policies.isEmpty()) {
            SampleHandler.LOGGER.warn("model error: model defines no actions!");
        }
        policySet.setVersion("1.0");
        policySet.setPolicyCombiningAlgId(XACML3.ID_POLICY_PERMIT_OVERRIDES.stringValue());
        return policySet;
    }
}
