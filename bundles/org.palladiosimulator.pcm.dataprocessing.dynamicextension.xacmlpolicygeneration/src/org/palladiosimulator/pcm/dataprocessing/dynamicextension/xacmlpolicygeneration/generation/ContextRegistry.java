package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers.SampleHandler;

/**
 * Represents an abstract context to V registry, which registers mappings from contexts to V.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @param <V> - the value type to which the registry should map
 */
public abstract class ContextRegistry<V> {
	private Map<Class<? extends Context>, Class<? extends V>> map;
	
	protected ContextRegistry() {
		this.map = new HashMap<>();
	}
	
	protected Class<? extends V> put(final Class<? extends Context> key, final Class<? extends V> value) {
		return map.put(key, value);
	}
	
	/**
	 * Gets a list of V from a list of contexts.
	 * Unregistered contexts are ignored.
	 * 
	 * @param contexts - a list of contexts
	 * @return a list of V representing the different contexts
	 * @throws IllegalStateException - when an illegal mapping is found
	 */
	public List<V> getAll(final List<Context> contexts) throws IllegalStateException {
		final List<V> vs = new ArrayList<>();
		for (var context : contexts) {
			final Class<?> contextInterface = getContextInterface(context.getClass());
			if (contextInterface == null) {
				final String error = "no context interface found";
				SampleHandler.LOGGER.error(error);
				throw new IllegalArgumentException(error);
			}
			final Class<? extends V> matchClass = this.map.get(contextInterface);
			if (matchClass != null) {
				// only contexts in map can be mapped to matches
				try {
					vs.add(matchClass.getConstructor(contextInterface).newInstance(context));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					final String error = "illegal mapping. Constructor in " + matchClass 
							+ " for " + contextInterface + " not found.";
					SampleHandler.LOGGER.error(error);
					throw new IllegalStateException(error);
				}
			}
		}
		return vs;
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
