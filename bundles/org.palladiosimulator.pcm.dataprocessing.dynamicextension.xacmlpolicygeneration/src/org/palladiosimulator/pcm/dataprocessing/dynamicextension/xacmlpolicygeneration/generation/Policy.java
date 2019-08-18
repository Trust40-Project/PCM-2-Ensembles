package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.ArrayList;
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
    private final List<ObligationExpressionsType> obligationsList;

    /**
     * Creates a new policy with the given matches.
     * 
     * @param matches
     *            - the given matches
     * @param obligationsList
     *            - the given obligations
     */
    public Policy(final List<List<MatchType>> matches, final List<ObligationExpressionsType> obligationsList) {
        this.matches = matches;
        this.obligationsList = obligationsList;
    }

    /**
     * Generates the XACML policy with the given matches and obligations. The different
     * characteristics are mapped to different rules which are combined with OR.
     * 
     * @return the XACML policy representing the action defined in the attribute found in
     *         {@code matches.get(0).get(0)}.
     */
    public PolicyType getPolicyType() {
        final String entityName = "" + this.matches.get(0).get(0).getAttributeValue().getContent().get(0);
        final List<RuleType> rules = getRules(entityName);

        // deny if not applicable rule
        final RuleType ruleDenyIfNotApplicable = new RuleType();
        ruleDenyIfNotApplicable.setDescription("this rule denies if this case is not applicable");
        ruleDenyIfNotApplicable.setTarget(new TargetType());
        ruleDenyIfNotApplicable.setRuleId("denyIfNotApplicable");
        ruleDenyIfNotApplicable.setEffect(EffectType.DENY);

        // Policy
        final PolicyType policy = new PolicyType();
        policy.setDescription("Policy for " + entityName);
        policy.setTarget(new TargetType());
        policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().addAll(rules);
        policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(ruleDenyIfNotApplicable);
        policy.setPolicyId("policy:" + entityName);
        policy.setVersion("1.0");
        policy.setRuleCombiningAlgId(XACML3.ID_RULE_PERMIT_OVERRIDES.stringValue());
        return policy;
    }

    /**
     * Gets the rules for this policy.
     * 
     * @param entityName - the name of the action entity
     * @return the rules for this policy
     */
    private List<RuleType> getRules(final String entityName) {
        final List<RuleType> rules = new ArrayList<>();
        for (int i = 0; i < this.matches.size(); i++) {
            // AllOf
            final AllOfType allOf = new AllOfType();
            allOf.getMatch().addAll(this.matches.get(i));

            // AnyOf
            final AnyOfType anyOf = new AnyOfType();
            anyOf.getAllOf().add(allOf);

            // Target
            final TargetType target = new TargetType();
            target.getAnyOf().add(anyOf);

            // Rule
            final RuleType rule = new RuleType();
            rule.setDescription("Context check rule for entity " + entityName + "'s characteristic " + i);
            rule.setTarget(target);
            rule.setRuleId("rule:" + entityName + ":" + i);
            rule.setEffect(EffectType.PERMIT);
            if (!this.obligationsList.isEmpty() && !this.obligationsList.get(i).getObligationExpression().isEmpty()) {
                rule.setObligationExpressions(this.obligationsList.get(i));
            }
            rules.add(rule);
        }
        return rules;
    }
}
