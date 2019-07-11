package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;
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
			final Policy policy = new Policy(matchExtractor.getMatches());
			policies.add(policy.getPolicyType());
		});
		final PolicySetType policySet = new PolicySet(policies).getPolicySet(); 
		
		// test write policySet
		final Path filenamePolicySet = Path.of(SampleHandler.PATH_PREFIX + "outSet.xml");
		XACMLPolicyWriter.writePolicyFile(filenamePolicySet, policySet);
	}
	
}
