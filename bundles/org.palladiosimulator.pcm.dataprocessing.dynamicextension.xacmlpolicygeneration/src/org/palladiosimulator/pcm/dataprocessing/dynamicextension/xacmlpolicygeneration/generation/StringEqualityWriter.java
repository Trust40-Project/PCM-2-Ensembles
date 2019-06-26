package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;

import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.util.XACMLPolicyWriter;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

public class StringEqualityWriter {
	public static final String ID_ENTITY = "entity:id";
	public static final String ID_CATEGORY_ACTION = XACML3.ID_ATTRIBUTE_CATEGORY_ACTION.stringValue();
	public static final String ID_CATEGORY_SUBJECT = XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT.stringValue(); 
	//TODO frage wie werden ressourcen behandelt?
	
	private Map<RelatedCharacteristics, String> attributeMap;
	private String attributeId;
	
	public StringEqualityWriter(final Map<RelatedCharacteristics, String> attributeMap, final String attributeId) {
		this.attributeMap = attributeMap;
		this.attributeId = attributeId;
	}
	
	public void write() {
		final List<PolicyType> policies = new ArrayList<>();
		int index = 0;
		for (var entry : attributeMap.entrySet()) {
			// Match entity name
			final String entityName = entry.getKey().getEntityName();
			final MatchType matchName = createMatchType(ID_CATEGORY_ACTION, ID_ENTITY, entityName);
			
			// Match Role
			final String attributeValue = entry.getValue();
			final MatchType matchRole = createMatchType(ID_CATEGORY_SUBJECT, this.attributeId, attributeValue);
			
			// AllOf
			final AllOfType allOf = new AllOfType();
			allOf.getMatch().add(matchName);
			allOf.getMatch().add(matchRole);
			
			// AnyOf
			final AnyOfType anyOf = new AnyOfType();
			anyOf.getAllOf().add(allOf);
			
			// Target
			final TargetType target = new TargetType();
			target.getAnyOf().add(anyOf);
			
			// Rule
			final RuleType rule = new RuleType();
			rule.setDescription("Role check");
			rule.setTarget(target);
			rule.setRuleId("roletest:" + index);
			rule.setEffect(EffectType.PERMIT);
			
			// deny if not applicable rule
			final RuleType ruleDenyIfNotApplicable = new RuleType();
			ruleDenyIfNotApplicable.setDescription("this rule denies if this case is not applicable");
			ruleDenyIfNotApplicable.setTarget(new TargetType());
			ruleDenyIfNotApplicable.setRuleId("denyIfNotApplicable");
			ruleDenyIfNotApplicable.setEffect(EffectType.DENY);
			
			// Policy
			final PolicyType policy = new PolicyType();
			policy.setDescription("Role check policy for " + entityName);
			policy.setTarget(new TargetType());
			policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
			policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(ruleDenyIfNotApplicable);
			policy.setPolicyId(entityName);
			policy.setVersion("1.0");
			policy.setRuleCombiningAlgId(XACML3.ID_RULE_PERMIT_OVERRIDES.stringValue());
			
			// test write single policy
			final Path filenameSinglePolicy = Path.of("/home/jojo/Schreibtisch/KIT/Bachelorarbeit/out" + index + ".xml");
			XACMLPolicyWriter.writePolicyFile(filenameSinglePolicy, policy);
			
			policies.add(policy);
			index++;
		}
		
		// PolicySet
		final PolicySetType policySet = new PolicySetType();
		policySet.setDescription("all policies combined");
		policySet.setPolicySetId("completePolicySet");
		policySet.setTarget(new TargetType());
		for (final PolicyType policy : policies) {
			QName qname = new QName(XACML3.XMLNS, XACML3.ELEMENT_POLICY);
			policySet.getPolicySetOrPolicyOrPolicySetIdReference().add(new JAXBElement<PolicyType>(qname , PolicyType.class, policy));
		}
		policySet.setVersion("1.0");
		policySet.setPolicyCombiningAlgId(XACML3.ID_POLICY_PERMIT_OVERRIDES.stringValue());
		
		// test write policySet
		final Path filenamePolicySet = Path.of("/home/jojo/Schreibtisch/KIT/Bachelorarbeit/outSet.xml");
		XACMLPolicyWriter.writePolicyFile(filenamePolicySet, policySet);
	}
	
	private MatchType createMatchType(final String categoryId, final String attributeId, final String attributeValueStr) {
		// Attribute value
		final AttributeValueType attributeValue = new AttributeValueType();
		attributeValue.getContent().add(attributeValueStr);
		attributeValue.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
					
		// Attribute designator
		final AttributeDesignatorType attributeDesignator = new AttributeDesignatorType();
		attributeDesignator.setAttributeId(attributeId);
		attributeDesignator.setCategory(categoryId);
		attributeDesignator.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
		attributeDesignator.setMustBePresent(false);
					
		// Match
		final MatchType match = new MatchType();
		match.setAttributeValue(attributeValue);
		match.setAttributeDesignator(attributeDesignator);
		match.setMatchId(XACML3.ID_FUNCTION_STRING_EQUAL.stringValue());
		return match;
	}
}
