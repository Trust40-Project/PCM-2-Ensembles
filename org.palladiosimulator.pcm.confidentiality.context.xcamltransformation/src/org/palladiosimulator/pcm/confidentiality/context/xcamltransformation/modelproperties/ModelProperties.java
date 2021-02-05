package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.modelproperties;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.Activator;

/**
 * ModelProperties allow to set Properties of the transformation in the following file properties\\ModelProperties.properties
 * MODEL_PATH is a path where the model is located
 * OUTPUT_FILE is path where the XACML policy has to be saved
 * @author vladsolovyev
 * @version 1.0
 */
public class ModelProperties extends Properties {

    private static final String MODEL_PROPERTIES_FILE = "properties\\ModelProperties.properties";
    private static final String MODEL_PATH = "MODEL_PATH";
    private static final String OUTPUT_FILE = "OUTPUT_FILE";
    private static final String DEFAULT_OUTPUTFILE_PATH = "defaultOutput.xml";
    private static final Logger LOGGER =  Logger.getLogger(ModelProperties.class.getName());

    /**
     * creates properties of the transformation
     */
    public ModelProperties() {
        try {
            Bundle bundle = Activator.getDefault().getBundle();
            IPath path = new Path(MODEL_PROPERTIES_FILE);
            URL setupUrl = FileLocator.find(bundle, path, Collections.emptyMap());
            String propertiesPath = FileLocator.toFileURL(setupUrl).getPath();
            try(InputStream is = new FileInputStream(propertiesPath)) {
                this.load(is);
            }
        } catch (Exception e) {
            LOGGER.severe("Could not load properties file");
        }
    }

    /**
     * @return a path of the input model
     */
    public Optional<String> getModelPath() {
        return Optional.ofNullable(getProperty(MODEL_PATH));
    }

    /**
     * @return a path where XACML policy has to be created
     */
    public String getOutputFilePath() {
        return getProperty(OUTPUT_FILE, DEFAULT_OUTPUTFILE_PATH);
    }
}
