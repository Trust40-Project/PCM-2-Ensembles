package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataprocessingPackage;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicextensionPackage;

/**
 * Loads the data processing part of the model.
 * 
 * @author majuwa
 * @author Jonathan Schenkenberger
 * @version 1.01
 */
public class ModelLoader {
    //private String pathDynamic;
    private String pathDataprocessing;
    private ResourceSet resourceSet;
    private Registry resourceRegistry;

    /**
     * Creates a new model loader.
     * 
     * @param pathDataprocessing - the path to the data processing part of the model
     */
    public ModelLoader(final String pathDataprocessing) {
        this.pathDataprocessing = pathDataprocessing;
        // here start loading
        // Datap
        DataprocessingPackage.eINSTANCE.eClass();
        DynamicextensionPackage.eINSTANCE.eClass();
        this.resourceSet = new ResourceSetImpl();
        this.resourceRegistry = Resource.Factory.Registry.INSTANCE;
        final Map<String, Object> map = this.resourceRegistry.getExtensionToFactoryMap();
        map.put("*", new XMIResourceFactoryImpl());
        this.resourceSet.setResourceFactoryRegistry(this.resourceRegistry);
    }

    /**
     * Loads the data specification.
     * 
     * @return the data specification
     */
    public DataSpecification loadDataSpecification() {
        var resourceData = loadResource(this.resourceSet, this.pathDataprocessing);
        return (DataSpecification) resourceData.getContents().get(0);
    }

    /**
     * Loads a resource.
     * 
     * @param resourceSet - the resource set
     * @param path - the path
     * @return the resource
     */
    private Resource loadResource(final ResourceSet resourceSet, final String path) {
        return resourceSet.getResource(URI.createFileURI(path), true);
    }

}
