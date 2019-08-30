package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.io.IOException;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.Activator;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextRegistry;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers.MainHandler;

import com.google.common.reflect.ClassPath;

/**
 * The match registry is used to make easy adding of mappings from contexts to matches possible.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public final class MatchRegistry extends ContextRegistry<Match> {
    static {
        try {
            ClassPath cp = ClassPath.from(MatchRegistry.class.getClassLoader());
            final var classes = 
                    cp.getTopLevelClasses(Activator.PLUGIN_ID + ".generation.matches");
            for (final var classInfo : classes) {
                Class.forName(classInfo.getName());
            }
        } catch (IOException | ClassNotFoundException e) {
            MainHandler.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static MatchRegistry instance;

    /**
     * Private constructor of match registry.
     */
    private MatchRegistry() {
        super();
    }

    /**
     * Gets an instance of the registry.
     * 
     * @return an instance of the registry
     */
    public static MatchRegistry getInstance() {
        if (instance == null) {
            instance = new MatchRegistry();
        }
        return instance;
    }
}
