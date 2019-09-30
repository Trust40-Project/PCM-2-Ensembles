package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches.MatchExtractor;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.obligations.ObligationExtractor;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers.MainHandler;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

/**
 * The ContextHandler handles the model instance and can be used to create a respective XACML policy set.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ContextHandler {
    private final String id;
    private final DataSpecification dataContainer;

    /**
     * Creates a new ContextHandler with the model instance at the given path.
     * 
     * @param id - the id, i.e. the name of the model
     * @param pathData
     *            - the path to the data specification
     */
    public ContextHandler(final String id, final String pathData) {
        this.id = Objects.requireNonNull(id);
        var modelloader = new ModelLoader(Objects.requireNonNull(pathData));
        this.dataContainer = modelloader.loadDataSpecification();
    }
    
    /**
     * Creates a new ContextHandler with the given data container.
     * This constructor is only for the scalability evaluation scenario.
     * 
     * @param id - the id, i.e. the name of the model
     * @param dataContainer - the given data container
     */
    public ContextHandler(final String id, final DataSpecification dataContainer) {
        this.id = Objects.requireNonNull(id);
        this.dataContainer = Objects.requireNonNull(dataContainer);
    }

    /**
     * Generates the policy set for the whole model instance.
     * 
     * @return the XACML policy set type representing the policy set for the whole model instance
     * @throws IllegalStateException
     *             - when an illegal mapping is detected
     */
    public PolicySetType createPolicySet() throws IllegalStateException {
        final List<PolicyType> policies = new ArrayList<>();
        this.dataContainer.getRelatedCharacteristics().stream().forEach(e -> {
            var matchExtractor = new MatchExtractor(e);
            var obligationExtractor = new ObligationExtractor(e);
            final Policy policy = new Policy(matchExtractor.extract(), obligationExtractor.extract());
            policies.add(policy.getPolicyType());
        });
        return new PolicySet(this.id, policies).getPolicySetType();
    }

    /**
     * Gets the characteristic list of the given related characteristic.
     * 
     * @param relatedCharacteristics
     *            - the related characteristics
     * @return the characteristic list of the given related characteristic
     */
    public static List<ContextCharacteristic> getCharacteristicsList(
            final RelatedCharacteristics relatedCharacteristics) {
        if (relatedCharacteristics != null && relatedCharacteristics.getCharacteristics() != null) {
            return relatedCharacteristics.getCharacteristics().getOwnedCharacteristics().stream()
                    .filter(ContextCharacteristic.class::isInstance).map(ContextCharacteristic.class::cast)
                    .collect(Collectors.toList());
        } else {
            MainHandler.LOGGER.warn("an element concerning a related characteristics of one action is null!"
                    + " assuming empty list");
            return new ArrayList<>();
        }
    }

}
