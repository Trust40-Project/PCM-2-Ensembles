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
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicextensionPackage;

/**
 * 
 * @author majuwa
 *
 */
public class ModelLoader {
	private String pathDynamic;
	private String pathDataprocessing;
	private ResourceSet resourceSet;
	private Registry resourceRegistry;

	public ModelLoader(String pathDynamic, String pathDataprocessing) {
		this.pathDynamic = pathDynamic;
		this.pathDataprocessing = pathDataprocessing;
		// here start loading
		// Datap
		DataprocessingPackage.eINSTANCE.eClass();
		DynamicextensionPackage.eINSTANCE.eClass();
		resourceSet = new ResourceSetImpl();
		resourceRegistry = Resource.Factory.Registry.INSTANCE;
		final Map<String, Object> map = resourceRegistry.getExtensionToFactoryMap();
		map.put("*", new XMIResourceFactoryImpl());
		resourceSet.setResourceFactoryRegistry(resourceRegistry);

	}
	/**
	 * 
	 * @return
	 */
	public DynamicSpecification loadDynamicModel() {
		var resourceDynamic = loadResource(resourceSet, pathDynamic);
		return (DynamicSpecification) resourceDynamic.getContents().get(0);

	}
	/**
	 * 
	 * @return
	 */
	public DataSpecification loadDataSpecification() {
		var resourceData = loadResource(resourceSet, pathDataprocessing);
		return (DataSpecification) resourceData.getContents().get(0);
	}

	private Resource loadResource(ResourceSet resourceSet, String path) {
		return resourceSet.getResource(URI.createFileURI(path), true);
	}

}
