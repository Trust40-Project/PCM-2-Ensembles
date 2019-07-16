package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.obligations;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ExtensionContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrerequisiteContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextRegistry;

/**
 * The obligation registry is used to make easy adding of mappings from contexts to obligations possible.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ObligationRegistry extends ContextRegistry<Obligation> {
	private static ObligationRegistry instance;

	private ObligationRegistry() {
		super();
		put(ExtensionContext.class, TextObligation.class);
		put(PrerequisiteContext.class, TextObligation.class);
		// TODO insert new context to obligation mappings here
	}

	/**
	 * Gets an instance of the registry.
	 * @return an instance of the registry
	 */
	public static ObligationRegistry getInstance() {
		if (instance == null) {
			instance = new ObligationRegistry();
		}
		return instance;
	}
}
