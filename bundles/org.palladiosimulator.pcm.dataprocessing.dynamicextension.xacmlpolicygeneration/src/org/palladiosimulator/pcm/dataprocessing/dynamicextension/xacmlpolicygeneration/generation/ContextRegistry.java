package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;

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
	 */
	public List<V> getAll(final List<Context> contexts) {
		final List<V> vs = new ArrayList<>();
		for (var context : contexts) {
			final Class<?> contextInterface = getContextInterface(context.getClass());
			if (contextInterface == null) {
				// TODO exc no context interface found
			}
			final Class<? extends V> matchClass = this.map.get(contextInterface);
			if (matchClass != null) {
				// only contexts in map can be mapped to matches
				try {
					vs.add(matchClass.getConstructor(contextInterface).newInstance(context));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO exc no matching constructor found
					e.printStackTrace();
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
