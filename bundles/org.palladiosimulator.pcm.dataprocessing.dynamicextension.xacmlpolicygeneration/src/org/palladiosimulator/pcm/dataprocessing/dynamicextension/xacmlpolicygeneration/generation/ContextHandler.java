package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches.MatchExtractor;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.obligations.ObligationExtractor;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

/**
 * The ContextHandler handles the model instance and can be used .
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ContextHandler {
    private DataSpecification dataContainer;

    /**
     * Creates a new ContextHandler with the model instance at the given path.
     * 
     * @param pathData
     *            - the path to the data specification
     */
    public ContextHandler(final String pathData) {
        var modelloader = new ModelLoader(pathData);
        this.dataContainer = modelloader.loadDataSpecification();
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
        return new PolicySet(policies).getPolicySet();
    }

    /**
     * Gets the contexts of the characteristic with the given index in the characteristic list of
     * the given related characteristic.
     * 
     * @param relatedCharacteristic
     *            - the related characteristic
     * @param index
     *            - the list index of the characteristic list
     * @return the contexts of the characteristic specified by the related characteristic and the
     *         index
     */
    public static List<Context> getContexts(final RelatedCharacteristics relatedCharacteristic, final int index) {
        return getCharacteristicsList(relatedCharacteristic).get(index).getContext();
    }

    /**
     * Gets the characteristic list of the given related characteristic.
     * 
     * @param relatedCharacteristic
     *            - the related characteristic
     * @return the characteristic list of the given related characteristic
     */
    public static List<ContextCharacteristic> getCharacteristicsList(
            final RelatedCharacteristics relatedCharacteristic) {
        return relatedCharacteristic.getCharacteristics().getOwnedCharacteristics().stream()
                .filter(ContextCharacteristic.class::isInstance).map(ContextCharacteristic.class::cast)
                .collect(Collectors.toList());
    }

}
