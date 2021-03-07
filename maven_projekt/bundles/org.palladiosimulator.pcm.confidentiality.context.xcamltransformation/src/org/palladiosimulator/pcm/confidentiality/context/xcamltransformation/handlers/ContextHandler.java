package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.handlers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.confidentiality.context.ConfidentialAccessSpecification;
import org.palladiosimulator.pcm.confidentiality.context.set.ContextSet;
import org.palladiosimulator.pcm.confidentiality.context.specification.PolicySpecification;
import org.palladiosimulator.pcm.confidentiality.context.specification.assembly.MethodSpecification;
import org.palladiosimulator.pcm.confidentiality.context.specification.assembly.SystemPolicySpecification;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.PolicyCreator;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.PolicySetCreator;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.modelloader.ModelLoader;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

/**
 * The ContextHandler class handles loading of the model and creating of policies.
 * @author vladsolovyev
 * @version 1.0.0
 */
public class ContextHandler {

    private final String id;
    private final ConfidentialAccessSpecification confidentialAccessSpecification;

    /**
     * Initialises the ContextHandler. The model folder is used as id of the created policy.
     * @param modelPath is a location of the context model for which XACML transformation has to be created
     */
    public ContextHandler(Path modelPath) {
        ModelLoader modelloader = new ModelLoader(modelPath);
        this.id = modelloader.getModelFolder();
        this.confidentialAccessSpecification = modelloader.loadConfidentialAccessSpecification();
    }

    /**
     * creates policy set which contains multiple policies
     * @return policy set type
     */
    public PolicySetType createPolicySet() {
        List<PolicyType> policies = new ArrayList<>();
        List<SystemPolicySpecification> systemPolicySpecifications = getSystemPolicySpecifications();
        for (SystemPolicySpecification systemPolicySpecification : systemPolicySpecifications) {
            handleSystemPolicySpecification(systemPolicySpecification).ifPresent(policy -> policies.add(policy));
        }
        return new PolicySetCreator(this.id, policies).createPolicySetType();
    }

    /**
     * filters policy specifications and returns only specifications of the type SystemPolicySpecification
     * @return list of specifications of the type SystemPolicySpecification
     */
    private List<SystemPolicySpecification> getSystemPolicySpecifications() {
        List<PolicySpecification> policySpecifications =
                confidentialAccessSpecification.getPcmspecificationcontainer().getPolicyspecification();
        return policySpecifications.stream()
                .filter(policySpecification -> policySpecification instanceof SystemPolicySpecification)
                .map(policySpecification -> (SystemPolicySpecification) policySpecification)
                .collect(Collectors.toList());
    }

    /**
     * creates policy type for the specified system policy specification
     * @param systemPolicySpecification policy specification for which policy type has to be created
     * @return policy type
     */
    private Optional<PolicyType> handleSystemPolicySpecification(SystemPolicySpecification systemPolicySpecification) {
        List<ContextSet> contextSets = systemPolicySpecification.getPolicy();
        MethodSpecification methodSpecification = systemPolicySpecification.getMethodspecification();
        if (Objects.nonNull(methodSpecification) && Objects.nonNull(contextSets)) {
            PolicyCreator policyCreator = new PolicyCreator(methodSpecification, contextSets,
                    systemPolicySpecification.getEntityName(), confidentialAccessSpecification.getContextContainer());
            return Optional.of(policyCreator.createPolicyType());
        }
        return Optional.empty();
    }
}
