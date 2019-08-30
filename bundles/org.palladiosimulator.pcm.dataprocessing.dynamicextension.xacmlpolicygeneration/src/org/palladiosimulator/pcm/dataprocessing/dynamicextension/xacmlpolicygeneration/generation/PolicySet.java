package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers.MainHandler;

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
    private static final String VERSION = "1.0";
    
    private final String id;
    private final List<PolicyType> policyList;

    /**
     * Creates a new policy set with the given id and the given policies.
     * 
     * @param id
     *            - the given id for this policy set
     * @param policies
     *            - the given list of XACML policy elements
     */
    public PolicySet(final String id, final List<PolicyType> policies) {
        this.id = id;
        this.policyList = policies;
    }

    /**
     * Generates the combined XACML policy set for all actions.
     * 
     * @return the combined XACML policy set
     */
    public PolicySetType getPolicySetType() {
        final PolicySetType policySet = new PolicySetType();
        policySet.setDescription("all policies combined for policy set with id " + this.id);
        policySet.setPolicySetId(this.id);
        policySet.setTarget(new TargetType());
        for (final PolicyType policy : this.policyList) {
            final QName qname = new QName(XACML3.XMLNS, XACML3.ELEMENT_POLICY);
            policySet.getPolicySetOrPolicyOrPolicySetIdReference()
                    .add(new JAXBElement<PolicyType>(qname, PolicyType.class, policy));
        }
        if (this.policyList.isEmpty()) {
            MainHandler.LOGGER.warn("model error: model defines no actions!");
        }
        policySet.setVersion(VERSION);
        policySet.setPolicyCombiningAlgId(XACML3.ID_POLICY_PERMIT_OVERRIDES.stringValue());
        return policySet;
    }
}
