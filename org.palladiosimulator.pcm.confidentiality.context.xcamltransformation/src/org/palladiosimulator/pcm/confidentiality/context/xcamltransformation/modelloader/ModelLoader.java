package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.modelloader;

import java.nio.file.Path;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.pcm.confidentiality.context.ConfidentialAccessSpecification;
import org.palladiosimulator.pcm.confidentiality.context.ContextPackage;

/**
 * ModelLoader is responsible for loading of confidential access specification
 * @author vladsolovyev
 * @version 1.0.0
 */
public class ModelLoader {

    private Path modelPath;
    private ResourceSet resourceSet;

    /**
     * Creates a new model loader.
     * @param modelPath - a path of the model
     */
    public ModelLoader(Path modelPath) {
        this.modelPath = modelPath;
        this.resourceSet = new ResourceSetImpl();
        if (!Platform.isRunning()) {
            ContextPackage.eINSTANCE.eClass();
            Registry resourceRegistry = Resource.Factory.Registry.INSTANCE;
            final Map<String, Object> map = resourceRegistry.getExtensionToFactoryMap();
            map.put("*", new XMIResourceFactoryImpl());
            this.resourceSet.setResourceFactoryRegistry(resourceRegistry);
        }
    }

    /**
     * Loads the confidential access specification.
     * @return the confidential access specification
     */
    public ConfidentialAccessSpecification loadConfidentialAccessSpecification() {
        Resource resourceData = resourceSet.getResource(URI.createFileURI(modelPath.toAbsolutePath().toString()), true);
        return (ConfidentialAccessSpecification) resourceData.getContents().get(0);
    }

    /**
     * Gets the name of the folder in which the model is saved
     * @return the name of the folder in which the model is saved
     */
    public String getModelFolder() {
        Path folderPath = modelPath.getParent();
        return folderPath.getFileName().toString();
    }
}
