package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.io.IOException;
import java.nio.file.Path;
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
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers.SampleHandler;

import com.att.research.xacml.util.XACMLPolicyWriter;

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
	//private DynamicSpecification dynamicContainer; //TODO frage: wofuer wird der benoetigt?

	/**
	 * Creates a new ContextHandler with the model instance at the given path.
	 * 
	 * @param pathDynamic - unused at the moment //TODO
	 * @param pathData - the path to the data specification
	 */
	public ContextHandler(final String pathDynamic, final String pathData) {
		var modelloader = new ModelLoader(pathDynamic, pathData);
		this.dataContainer = modelloader.loadDataSpecification();
		//this.dynamicContainer = modelloader.loadDynamicModel();
	}

	/**
	 * Generates the policy set for the whole model instance.
	 * 
	 * @throws IOException - if an read/write error occurs
	 */
	public void createPolicySet() throws IOException {
		final List<PolicyType> policies = new ArrayList<>();
		this.dataContainer.getRelatedCharacteristics().stream().forEach(e -> {
			var matchExtractor = new MatchExtractor(e);
			var obligationExtractor = new ObligationExtractor(e);
			final Policy policy = new Policy(matchExtractor.extract(), obligationExtractor.extract());
			policies.add(policy.getPolicyType());
		});
		final PolicySetType policySet = new PolicySet(policies).getPolicySet(); 
		
		// test write policySet
		final Path filenamePolicySet = Path.of(SampleHandler.PATH_OUTPUT_POLICYSET);
		XACMLPolicyWriter.writePolicyFile(filenamePolicySet, policySet);
	}
	
	/**
	 * Gets the contexts of the characteristic with the given index in the characteristic list
	 * of the given related characteristic.
	 * 
	 * @param relatedCharacteristic - the related characteristic
	 * @param index - the list index of the characteristic list
	 * @return the contexts of the characteristic specified by the related characteristic and the index
	 */
	public static List<Context> getContexts(final RelatedCharacteristics relatedCharacteristic, final int index) {
		return getCharacteristicsList(relatedCharacteristic).get(index).getContext();
	}
	
	/**
	 * Gets the characteristic list of the given related characteristic.
	 * 
	 * @param relatedCharacteristic - the related characteristic
	 * @return the characteristic list of the given related characteristic
	 */
	public static List<ContextCharacteristic> getCharacteristicsList(final RelatedCharacteristics relatedCharacteristic) {
		return relatedCharacteristic.getCharacteristics().getOwnedCharacteristics().stream()
				.filter(ContextCharacteristic.class::isInstance)
				.map(ContextCharacteristic.class::cast)
				.collect(Collectors.toList());
	}
	
}
