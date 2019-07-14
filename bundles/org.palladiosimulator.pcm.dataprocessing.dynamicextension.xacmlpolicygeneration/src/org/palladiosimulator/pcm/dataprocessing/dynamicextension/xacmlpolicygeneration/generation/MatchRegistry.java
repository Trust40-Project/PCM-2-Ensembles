package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.FloatingComparisonContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.IntegralComparisonContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftContext;

/**
 * The match registry is used to make easy adding of mappings from contexts to matches possible.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class MatchRegistry {
	private static MatchRegistry instance;

	private Map<Class<? extends Context>, Class<? extends Match>> map;

	private MatchRegistry() {
		this.map = new HashMap<>();
		this.map.put(PrivacyLevelContext.class, StringComparisonMatch.class); // TODO evtl. noch auf regex umstellen und
																			  // TODO inkludierte privacy levels beachten
		this.map.put(InternalStateContext.class, StringComparisonMatch.class);
		this.map.put(RoleContext.class, RegexMatchingMatch.class);
		this.map.put(LocationContext.class, RegexMatchingMatch.class);
		this.map.put(OrganisationContext.class, RegexMatchingMatch.class);
		this.map.put(IntegralComparisonContext.class, ComparisonMatch.class);
		this.map.put(FloatingComparisonContext.class, ComparisonMatch.class);
		this.map.put(ShiftContext.class, ShiftMatch.class);
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

	/**
	 * Gets a list of matches from a list of contexts.
	 * Unregistered contexts are ignored.
	 * 
	 * @param contexts - a list of contexts
	 * @return a list of matches representing the different contexts
	 */
	public List<Match> getAllMatches(final List<Context> contexts) {
		final List<Match> matches = new ArrayList<>();
		for (var context : contexts) {
			final Class<?> contextInterface = getContextInterface(context.getClass());
			if (contextInterface == null) {
				// TODO exc no context interface found
			}
			final Class<? extends Match> matchClass = this.map.get(contextInterface);
			if (matchClass != null) {
				// only contexts in map can be mapped to matches
				try {
					matches.add(matchClass.getConstructor(contextInterface).newInstance(context));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO exc no matching constructor found
					e.printStackTrace();
				}
			}
		}
		return matches;
	}

	private Class<?> getContextInterface(final Class<? extends Context> contextClass) {
		final Class<?>[] interfaces = contextClass.getInterfaces();
		for (var interfaceElement : interfaces) {
			if (Context.class.isAssignableFrom(interfaceElement)) {
				return interfaceElement;
			}
		}
		return null;
	}
}
