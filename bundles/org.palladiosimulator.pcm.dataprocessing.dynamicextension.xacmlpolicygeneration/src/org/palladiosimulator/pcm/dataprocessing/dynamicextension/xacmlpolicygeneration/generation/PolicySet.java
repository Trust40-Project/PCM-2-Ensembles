package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

public class PolicySet {
	private final List<PolicyType> policies;
	
	public PolicySet(final List<PolicyType> policies) {
		this.policies = policies;
	}
	
	public PolicySetType getPolicySet() {
		final PolicySetType policySet = new PolicySetType();
		policySet.setDescription("all policies combined"); //TODO
		policySet.setPolicySetId("completePolicySet"); //TODO
		policySet.setTarget(new TargetType());
		for (final PolicyType policy : policies) {
			QName qname = new QName(XACML3.XMLNS, XACML3.ELEMENT_POLICY);
			policySet.getPolicySetOrPolicyOrPolicySetIdReference().add(new JAXBElement<PolicyType>(qname , PolicyType.class, policy));
		}
		policySet.setVersion("1.0");
		policySet.setPolicyCombiningAlgId(XACML3.ID_POLICY_PERMIT_OVERRIDES.stringValue());
		return policySet;
	}
}
