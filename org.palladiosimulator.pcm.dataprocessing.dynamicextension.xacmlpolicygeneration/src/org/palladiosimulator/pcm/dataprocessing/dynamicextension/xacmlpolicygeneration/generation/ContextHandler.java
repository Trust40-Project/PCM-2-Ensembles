package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPEngineFactory;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.std.StdAttribute;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.datatypes.DataTypeString;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.std.dom.DOMStructureException;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.XACMLProperties;
import com.att.research.xacml.std.IdentifierImpl;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Role;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Organisation;

public class ContextHandler {
	private DataSpecification dataContainer;
	private DynamicSpecification dynamicContainer;

	public ContextHandler(String pathDynamic, String pathData) {
		var modelloader = new ModelLoader(pathDynamic, pathData);
		this.dataContainer = modelloader.loadDataSpecification();
		this.dynamicContainer = modelloader.loadDynamicModel();
	}
	
	private static final String PATH_DYNAMIC = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/models/UseCasesTechnicalReport/UC-Shift/uc-combined.dynamicextension";
	private static final String PATH_DATA = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/models/UseCasesTechnicalReport/UC-Shift/uc-combined.dataprocessing";
	
	public static void main(String[] args) throws IOException, DOMStructureException, FactoryException, PDPException {
		ContextHandler ch = new ContextHandler(PATH_DYNAMIC, PATH_DATA);
		var contextGeneration = new ContextGeneration(ch.dataContainer, ch.dynamicContainer);
		var characteristicToAttributeMap = getAttributeMap(contextGeneration.getRelatedRoleContexts(ch.dataContainer));
		StringEqualityWriter sew = new StringEqualityWriter(characteristicToAttributeMap, XACML3.ID_SUBJECT_ROLE.stringValue());
		sew.write();
		
		// Request-Test
		testRequest();
	}
	
	private static Map<RelatedCharacteristics, String> getAttributeMap(Map<RelatedCharacteristics, Role> characteristicToRoleMap) {
		var characteristicToAttributeMap = new HashMap<RelatedCharacteristics, String>();
		for (var entry : characteristicToRoleMap.entrySet()) {
			//TODO eher ID verwenden
			final String attributeValue = entry.getValue().getEntityName();
			characteristicToAttributeMap.put(entry.getKey(), attributeValue);
		}
		return characteristicToAttributeMap;
	}
	
	private static void testRequest() throws DOMStructureException, FactoryException, PDPException {
		Request request =  DOMRequest.load(new File("/home/jojo/Schreibtisch/KIT/Bachelorarbeit/test.xml"));
		
		PDPEngine pdp = PDPEngineFactory.newInstance().newEngine();
		System.out.println(pdp.decide(request));
	}

}
