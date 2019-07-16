package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.obligations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ExtensionContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrerequisiteContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;

public class ObligationExtractor {
	private final RelatedCharacteristics relatedCharacteristics;

	/**
	 * Creates a new obligation extractor. 
	 * 
	 * @param relatedCharacteristics - the characteristics
	 */
	public ObligationExtractor(final RelatedCharacteristics relatedCharacteristics) {
		this.relatedCharacteristics = relatedCharacteristics;
	}
	
	public ObligationExpressionsType getObligations() {
		final int index = 0; //TODO || unterstuetzung
		
		final List<Context> obligationContexts = getObligationContexts(index);
		final List<ObligationExpressionType> obligationsList = new ArrayList<>();
		
		for (var context : obligationContexts) {
			final ObligationExpressionType obligationExpression = new Obligation(context).getObligation();
			if (obligationExpression != null) {
				obligationsList.add(obligationExpression);
			}
		}
		
		final ObligationExpressionsType obligations = new ObligationExpressionsType();
		obligations.getObligationExpression().addAll(obligationsList);
		return obligations;
	}

	private List<Context> getObligationContexts(final int index) {
		return ContextHandler.getContexts(this.relatedCharacteristics, index)
				.stream().filter(x -> ExtensionContext.class.isInstance(x) || PrerequisiteContext.class.isInstance(x))
				.collect(Collectors.toList());
	}
}
