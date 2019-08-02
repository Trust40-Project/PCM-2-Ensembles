package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.obligations;

import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.Extractor;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;

/**
 * Extracts the obligations from the context information of a related characteristics.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ObligationExtractor extends Extractor<ObligationExpressionsType> {

    /**
     * Creates a new obligation extractor.
     * 
     * @param relatedCharacteristics
     *            - the characteristics
     */
    public ObligationExtractor(final RelatedCharacteristics relatedCharacteristics) {
        super(relatedCharacteristics);
    }

    @Override
    protected ObligationExpressionsType extractOneElement(final int index) {
        final List<Context> allContexts = ContextHandler.getContexts(getRelatedCharacteristics(), index);
        // getting all obligations contained in the context information
        final List<ObligationExpressionType> obligationsList = ObligationRegistry.getInstance().getAll(allContexts)
                .stream().map(Obligation::getObligations).flatMap(List::stream).collect(Collectors.toList());

        final ObligationExpressionsType obligations = new ObligationExpressionsType();
        obligations.getObligationExpression().addAll(obligationsList);
        return obligations;
    }
}
