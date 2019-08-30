package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.obligations;

import java.io.IOException;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.Activator;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextRegistry;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches.MatchRegistry;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers.MainHandler;

import com.google.common.reflect.ClassPath;

/**
 * The obligation registry is used to make easy adding of mappings from contexts to obligations
 * possible.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public final class ObligationRegistry extends ContextRegistry<Obligation> {
    static {
        try {
            ClassPath cp = ClassPath.from(MatchRegistry.class.getClassLoader());
            final var classes = 
                    cp.getTopLevelClasses(Activator.PLUGIN_ID + ".generation.obligations");
            for (final var classInfo : classes) {
                Class.forName(classInfo.getName());
            }
        } catch (IOException | ClassNotFoundException e) {
            MainHandler.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static ObligationRegistry instance;

    /**
     * Private constructor of obligation registry.
     */
    private ObligationRegistry() {
        super();
    }

    /**
     * Gets an instance of the registry.
     * 
     * @return an instance of the registry
     */
    public static ObligationRegistry getInstance() {
        if (instance == null) {
            instance = new ObligationRegistry();
        }
        return instance;
    }
}
