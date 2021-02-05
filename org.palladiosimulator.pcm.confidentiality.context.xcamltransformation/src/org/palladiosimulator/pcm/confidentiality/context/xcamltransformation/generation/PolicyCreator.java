package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.confidentiality.context.model.ContextAttribute;
import org.palladiosimulator.pcm.confidentiality.context.model.ContextContainer;
import org.palladiosimulator.pcm.confidentiality.context.model.HierarchicalContext;
import org.palladiosimulator.pcm.confidentiality.context.model.IncludeDirection;
import org.palladiosimulator.pcm.confidentiality.context.model.RelatedContextSet;
import org.palladiosimulator.pcm.confidentiality.context.model.SingleAttributeContext;
import org.palladiosimulator.pcm.confidentiality.context.set.ContextSet;
import org.palladiosimulator.pcm.confidentiality.context.specification.assembly.MethodSpecification;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.matches.AssemblyContextMatch;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.matches.AttributeContextMatch;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.matches.SignatureMatch;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

/**
 * The PolicyCreator is responsible for creating of policy types for a specified policy.
 * @author vladsolovyev
 * @version 1.0
 */
public class PolicyCreator {

    private static final String VERSION = "1.0";
    private MethodSpecification methodSpecification;
    private List<ContextSet> contextSets;
    private String policyEntityName;
    private Set<HierarchicalContext> rootTopDownHierarchicalContexts;
    private Map<ContextAttribute, String> contextsToMatchRegex;

    /**
     * Creates new policy creator. Initialises its variables and determine top down hierarchical contexts
     * @param methodSpecification - description of the method of the policy
     * @param contextSets - all context sets of the policy
     * @param policyEntityName - the name of the policy
     * @param contextContainers - all available contexts of the model
     */
    public PolicyCreator(MethodSpecification methodSpecification, List<ContextSet> contextSets, String policyEntityName, List<ContextContainer> contextContainers) {
        this.methodSpecification = methodSpecification;
        this.contextSets = contextSets;
        this.policyEntityName = policyEntityName;
        this.rootTopDownHierarchicalContexts = fetchRootTopDownHierarchicalContexts(contextContainers);
        this.contextsToMatchRegex = new HashMap<>();
        createTopDownRegexExpressions();
    }

    /**
     * creates a policy type
     * @return policy type
     */
    public PolicyType createPolicyType() {
        String methodName = methodSpecification.getSignature().getEntityName();
        PolicyType policy = new PolicyType();
        policy.setDescription("Policy for " + methodName);
        TargetType targetType = createPolicyTargetType(methodName, getAssemblyContextEntityName(methodSpecification.getConnector()));
        policy.setTarget(targetType);
        List<RuleType> ruleTypesToAdd = Arrays.asList(createPermitAccessRule(createRuleAnyOfType(), methodName), createDenyAccessRule());
        policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().addAll(ruleTypesToAdd);
        policy.setPolicyId(policyEntityName);
        policy.setVersion(VERSION);
        policy.setRuleCombiningAlgId(XACML3.ID_RULE_FIRST_APPLICABLE.stringValue());
        return policy;
    }

    /**
     * @param connector of the method specification
     * @return an entity name of the assembly context of the method specification
     */
    private String getAssemblyContextEntityName(Connector connector) {
        String assemblyContextEntityName = "";
        if (connector instanceof AssemblyConnector) {
            AssemblyContext assemblyContext = ((AssemblyConnector)connector).getProvidingAssemblyContext_AssemblyConnector();
            assemblyContextEntityName = assemblyContext.getEntityName();
        } else if (connector instanceof ProvidedDelegationConnector) {
            AssemblyContext assemblyContext = ((ProvidedDelegationConnector)connector).getAssemblyContext_ProvidedDelegationConnector();
            assemblyContextEntityName=  assemblyContext.getEntityName();
        }
        return assemblyContextEntityName;
    }

    /**
     * creates a permit access rule
     * @param anyOfType of the target part of the rule
     * @param methodSpecification an entity name of the method signature
     * @return new rule type
     */
    private RuleType createPermitAccessRule(AnyOfType anyOfType, String methodSpecification) {
        RuleType rule = new RuleType();
        TargetType target = new TargetType();
        target.getAnyOf().add(anyOfType);
        rule.setDescription("Context check rule for entity " + methodSpecification);
        rule.setTarget(target);
        rule.setRuleId("Permit:" + methodSpecification);
        rule.setEffect(EffectType.PERMIT);
        return rule;
    }

    /**
     * creates a deny access rule
     * @return new rule type
     */
    private RuleType createDenyAccessRule() {
        RuleType denyRule = new RuleType();
        denyRule.setDescription("this rule denies if this case is not applicable");
        denyRule.setTarget(new TargetType());
        denyRule.setRuleId("denyIfNotApplicable");
        denyRule.setEffect(EffectType.DENY);
        return denyRule;
    }

    /**
     * creates a target type of the policy
     * @param methodSpecificationName - signature name of the method
     * @param assemblyContextEntityName - entity name of the assembly context of the method
     * @return target type of the policy
     */
    private TargetType createPolicyTargetType(String methodSpecificationName, String assemblyContextEntityName) {
        TargetType targetType = new TargetType();
        AllOfType allOfType = new AllOfType();
        allOfType.getMatch().add(new SignatureMatch(methodSpecificationName).createMatchType());
        allOfType.getMatch().add(new AssemblyContextMatch(assemblyContextEntityName).createMatchType());

        AnyOfType anyOf = new AnyOfType();
        anyOf.getAllOf().add(allOfType);

        targetType.getAnyOf().add(anyOf);
        return targetType;
    }

    /**
     * creates anyOfType of a rule part which contains all access-subject match types
     * @return anyOfType of a rule part
     */
    private AnyOfType createRuleAnyOfType() {
        AnyOfType anyOfType = new AnyOfType();
        for (ContextSet contextset : contextSets) {
            List<MatchType> matches = new ArrayList<>();
            for (ContextAttribute contextAttribute : contextset.getContexts()) {
                matches.addAll(createRuleMatchTypes(contextAttribute, ""));
            }
            AllOfType allOfType = new AllOfType();
            allOfType.getMatch().addAll(matches);
            anyOfType.getAllOf().add(allOfType);
        }
        return anyOfType;
    }

    /**
     * creates a list of match types of a rule part
     * @param contextAttribute - a context attribute for which match types are created
     * @param prefixRelatedContextEntityName - prefix for an entity name
     * @return list of created match types. It is a list because RelatedContextSet may contains multiple context attributes
     */
    private List<MatchType> createRuleMatchTypes(ContextAttribute contextAttribute, String prefixRelatedContextEntityName) {
        String regex = "";
        if (contextAttribute instanceof SingleAttributeContext) {
            regex = contextsToMatchRegex.getOrDefault(contextAttribute, contextAttribute.getEntityName());
        } else if (contextAttribute instanceof HierarchicalContext) {
            HierarchicalContext hierarchicalContext = (HierarchicalContext) contextAttribute;
            switch (hierarchicalContext.getDirection()) {
                case BOTTOM_UP:
                    regex = createBottomUpRegexExpression(hierarchicalContext);
                    break;
                case TOP_DOWN:
                    regex = contextsToMatchRegex.get(hierarchicalContext);
                    break;
                default:
                    return Collections.emptyList();
            }
        } else if (contextAttribute instanceof RelatedContextSet) {
            RelatedContextSet relatedContextSet = (RelatedContextSet) contextAttribute;
            return createMatchTypesForRelatedContext(relatedContextSet, prefixRelatedContextEntityName);
        }
        return Collections.singletonList(createRuleMatchType(contextAttribute, regex, prefixRelatedContextEntityName));
    }

    /**
     * creates a list of match types of relatedContextSet
     * @param relatedContextSet - a context attribute which contains a context set of other context attributes
     * @param prefixRelatedContextEntityName - prefix for an entity name
     * @return list of created match types. It is a list because RelatedContextSet may contains multiple context attributes
     */
    private List<MatchType> createMatchTypesForRelatedContext(RelatedContextSet relatedContextSet, String prefixRelatedContextEntityName) {
        String newPrefixRelatedContextEntityName = String.format("%s%s:", prefixRelatedContextEntityName, relatedContextSet.getEntityName());
        ContextSet contextSet = relatedContextSet.getContextset();
        List<MatchType> matchTypes = new ArrayList<>();
        if (Objects.nonNull(contextSet)) {
            for (ContextAttribute attribute : contextSet.getContexts()) {
                matchTypes.addAll(createRuleMatchTypes(attribute, newPrefixRelatedContextEntityName));
            }
        }
        return matchTypes;

    }

    /**
     * creates a match type of a rule part
     * @param contextAttribute - a context attribute for which a match type is created
     * @param regex - regex which used used as an attribute value
     * @param prefixRelatedContextEntityName - prefix for an entity name
     * @return a match type of a rule part
     */
    private MatchType createRuleMatchType(ContextAttribute contextAttribute, String regex, String prefixRelatedContextEntityName) {
        String dataType = "";
        if (Objects.nonNull(contextAttribute.getContexttype())) {
            dataType = contextAttribute.getContexttype().getEntityName();
        }
        AttributeContextMatch attributeContextMatch = new AttributeContextMatch(regex, prefixRelatedContextEntityName.concat(dataType));
        return attributeContextMatch.createMatchType();
    }

    /**
     * fetches root top down hierarchical contexts
     * @param contextContainers - all context containers which contain all context attributes
     * @return set of root top down hierarchical contexts
     */
    private Set<HierarchicalContext> fetchRootTopDownHierarchicalContexts(List<ContextContainer> contextContainers) {
        Set<HierarchicalContext> topDownHierarchicalContexts = contextContainers.stream().flatMap(contextContainer -> contextContainer.getContext().stream())
            .filter(context -> context instanceof HierarchicalContext)
            .map(context -> (HierarchicalContext)context)
            .filter(context -> IncludeDirection.TOP_DOWN.equals(context.getDirection()))
            .collect(Collectors.toSet());
        Set<ContextAttribute> includingContexts = new HashSet<>();
        for (HierarchicalContext topDownHierarchicalContext : topDownHierarchicalContexts) {
            includingContexts.addAll(topDownHierarchicalContext.getIncluding());
        }
        topDownHierarchicalContexts.removeAll(includingContexts);
        return topDownHierarchicalContexts;
    }

    /**
     * iterates over including contexts of top down hierarchical contexts and
     * creates a mapping of context attributes to its expected regex
     */
    private void createTopDownRegexExpressions() {
        for (HierarchicalContext rootTopDownHierarchicalContext : rootTopDownHierarchicalContexts) {
            createTopDownRegex("", rootTopDownHierarchicalContext);
        }
    }

    /**
     * creates a mapping of a context attribute to its expected regex
     * @param regex - is a first part of regex. It includes all parents context attributes.
     * @param context - context attribute which is added to the mapping
     */
    private void createTopDownRegex(String regex, ContextAttribute context) {
        regex = String.format("%s%s", regex, context.getEntityName());
        contextsToMatchRegex.put(context, regex);
        if (context instanceof HierarchicalContext) {
            HierarchicalContext hierarchicalContext = (HierarchicalContext) context;
            for (ContextAttribute includingContext : hierarchicalContext.getIncluding()) {
                createTopDownRegex(String.format("%s|", regex), includingContext);
            }
        }
    }

    /**
     * creates a regex for a bottom up hierarchical context which contains all including contexts
     * @param bottomUpHierarchicalContext - a context attribute for which a regex is created
     * @return a regex for a bottom up hierarchical context
     */
    private String createBottomUpRegexExpression(HierarchicalContext bottomUpHierarchicalContext) {
        Set<ContextAttribute> allowedContexts = getAllowedContexts(bottomUpHierarchicalContext);
        String regex = bottomUpHierarchicalContext.getEntityName();
        for (ContextAttribute allowedContext : allowedContexts) {
            regex = String.format("%s|%s", regex, allowedContext.getEntityName());
        }
        return regex;
    }

    /**
     * iterates recursively over all including contexts and returns all possible including contexts
     * @param context for which all including contexts are fetched
     * @return all possible including contexts
     */
    private Set<ContextAttribute> getAllowedContexts(ContextAttribute context) {
        Set<ContextAttribute> allowedContexts = new HashSet<>();
        if (context instanceof HierarchicalContext) {
            HierarchicalContext hierarchicalContext = (HierarchicalContext) context;
            List<ContextAttribute> includingContexts = hierarchicalContext.getIncluding();
            allowedContexts.addAll(includingContexts);
            for (ContextAttribute includingContext : includingContexts) {
                allowedContexts.addAll(getAllowedContexts(includingContext));
            }
        }
        return allowedContexts;
    }
}
