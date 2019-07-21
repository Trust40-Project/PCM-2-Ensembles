package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.FloatingComparisonContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.IntegralComparisonContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextRegistry;

/**
 * The match registry is used to make easy adding of mappings from contexts to matches possible.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class MatchRegistry extends ContextRegistry<Match> {
	private static MatchRegistry instance;

	private MatchRegistry() {
		super();
		put(InternalStateContext.class, StringComparisonMatch.class);
		put(PrivacyLevelContext.class, RegexMatchingMatch.class); 
		put(RoleContext.class, RegexMatchingMatch.class);
		put(LocationContext.class, RegexMatchingMatch.class);
		put(OrganisationContext.class, RegexMatchingMatch.class);
		put(IntegralComparisonContext.class, ComparisonMatch.class);
		put(FloatingComparisonContext.class, ComparisonMatch.class);
		put(ShiftContext.class, ShiftMatch.class);
		// TODO insert new context to match mappings here
	}

	/**
	 * Gets an instance of the registry.
	 * @return an instance of the registry
	 */
	public static MatchRegistry getInstance() {
		if (instance == null) {
			instance = new MatchRegistry();
		}
		return instance;
	}
}
