package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.List;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

/**
 * Represents a policy which can be created with a list of extracted matches.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class Policy {
	private final List<List<MatchType>> matches;
	private final ObligationExpressionsType obligations;
	
	/**
	 * Creates a new policy with the given matches.
	 * @param matches - the given matches
	 */
	public Policy(final List<List<MatchType>> matches, final ObligationExpressionsType obligations) {
		this.matches = matches;
		this.obligations = obligations;
	}
	
	/**
	 * Generates the XACML policy with the given matches.
	 * 
	 * @return the XACML policy
	 */
	public PolicyType getPolicyType() {
		final AnyOfType anyOf = new AnyOfType();
		final TargetType target = new TargetType();
		for (int i = 0; i < this.matches.size(); i++) {
			// AllOf
			final AllOfType allOf = new AllOfType();
			allOf.getMatch().addAll(this.matches.get(i));
			
			// AnyOf
			anyOf.getAllOf().add(allOf);
		}
		
		// Target
		target.getAnyOf().add(anyOf);
		
		final String entityName = "" + this.matches.get(0).get(0).getAttributeValue().getContent().get(0);
		
		// Rule
		final RuleType rule = new RuleType();
		rule.setDescription("Context check"); //TODO
		rule.setTarget(target);
		rule.setRuleId("c:" + entityName); //TODO
		rule.setEffect(EffectType.PERMIT);
		rule.setObligationExpressions(this.obligations); 
		//TODO noch || unterstuetzen --> anstatt allOf / anyOf, nur allOf und die verschiedenen matchlisten
		//TODO in verschiedene regeln machen
			
		// deny if not applicable rule
		final RuleType ruleDenyIfNotApplicable = new RuleType();
		ruleDenyIfNotApplicable.setDescription("this rule denies if this case is not applicable");
		ruleDenyIfNotApplicable.setTarget(new TargetType());
		ruleDenyIfNotApplicable.setRuleId("denyIfNotApplicable");
		ruleDenyIfNotApplicable.setEffect(EffectType.DENY);
			
		// Policy
		final PolicyType policy = new PolicyType();
		policy.setDescription("Policy for " + entityName); //TODO
		policy.setTarget(new TargetType());
		policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
		policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(ruleDenyIfNotApplicable);
		policy.setPolicyId(entityName);
		policy.setVersion("1.0");
		policy.setRuleCombiningAlgId(XACML3.ID_RULE_PERMIT_OVERRIDES.stringValue());
		return policy;
	}
}
