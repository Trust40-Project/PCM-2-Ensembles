package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.nio.file.Path;
import java.util.Map;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.util.XACMLPolicyWriter;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

public class StringEqualityWriter {
	private Map<RelatedCharacteristics, Attribute> attributeMap;
	
	public StringEqualityWriter(Map<RelatedCharacteristics, Attribute> attributeMap) {
		this.attributeMap = attributeMap;
	}
	
	public void write() { //TODO rueckgabe bzw. write to file
		int index = 0;
		for (var entry : attributeMap.entrySet()) {
			// Attribute value
			final AttributeValueType attributeValue = new AttributeValueType();
			final AttributeValue<?> attrValue = entry.getValue().getValues().iterator().next();
			final Object value = attrValue.getValue();
			attributeValue.getContent().add(value);
			attributeValue.setDataType(attrValue.getDataTypeId().stringValue());
			
			// Attribute designator
			final AttributeDesignatorType attributeDesignator = new AttributeDesignatorType();
			attributeDesignator.setAttributeId(entry.getValue().getAttributeId().stringValue());
			attributeDesignator.setCategory(entry.getValue().getCategory().stringValue());
			attributeDesignator.setDataType(attrValue.getDataTypeId().stringValue());
			attributeDesignator.setMustBePresent(true);
			
			// Match
			final MatchType match = new MatchType();
			match.setAttributeValue(attributeValue);
			match.setAttributeDesignator(attributeDesignator);
			match.setMatchId(XACML3.ID_FUNCTION_STRING_EQUAL.stringValue());
			
			// AllOf
			final AllOfType allOf = new AllOfType();
			allOf.getMatch().add(match);
			
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
			
			// Policy
			final PolicyType policy = new PolicyType();
			policy.setDescription("Role check policy");
			policy.setTarget(new TargetType());
			policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
			policy.setPolicyId("roletest:policy:" + index);
			policy.setVersion("1.0");
			policy.setRuleCombiningAlgId(XACML3.ID_POLICY_DENY_OVERRIDES.stringValue());
			
			//TODO relating with characteristic, i.e. action/resource in policy
			
			//TODO not direct write, but creating policy set
			final Path filename = Path.of("/home/jojo/Schreibtisch/KIT/Bachelorarbeit/out" + index + ".xml");
			XACMLPolicyWriter.writePolicyFile(filename, policy);
			
			index++;
		}
	}
}
