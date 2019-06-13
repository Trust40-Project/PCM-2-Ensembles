package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
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
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.StdAttribute;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.datatypes.DataTypeString;
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
	
	public static void main(String[] args) throws IOException {
		ContextHandler ch = new ContextHandler(PATH_DYNAMIC, PATH_DATA);
		var contextGeneration = new ContextGeneration(ch.dataContainer, ch.dynamicContainer);
		var characteristicToAttributeMap = getAttributeMap(contextGeneration.getRelatedRoleContexts(ch.dataContainer));
		
	}
	
	private static HashMap<RelatedCharacteristics, Attribute> getAttributeMap(Map<RelatedCharacteristics, Role> characteristicToRoleMap) {
		var characteristicToAttributeMap = new HashMap<RelatedCharacteristics, Attribute>();
		for (var entry : characteristicToRoleMap.entrySet()) {
			final Identifier categoryId = XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE; //TODO noch schauen ob das so passt
			final Identifier attributeId = XACML3.ID_SUBJECT_ROLE; //TODO noch schauen ob das so passt
			final AttributeValue<String> value = new StdAttributeValue<>(XACML3.ID_DATATYPE_STRING, entry.getValue().getEntityName());
			final Attribute attribute = new StdAttribute(categoryId, attributeId, value);
			characteristicToAttributeMap.put(entry.getKey(), attribute);
		}
		return characteristicToAttributeMap;
	}

	public void createContext() {
		//var typeDefinition =  new TypeDefinition();
		//var contextGeneration = new ContextGeneration(dataContainer,dynamicContainer);
		//var scenarioDefinition = new ScenarioDefinition();
		try (var writer = new PrintWriter(new File("/home/majuwa/out.scala"), Charset.forName("UTF-8"))) {
			//typeDefinition.createTypes(dynamicContainer, dataContainer, writer);
			//List<String> listEnsembleNames = contextGeneration.generateRelatedContexts(dataContainer, writer);
			//typeDefinition.createRootEnsemble(writer, listEnsembleNames);

			/*scenarioDefinition.writeScenario(writer, dynamicContainer.getSubjectContainer().getSubject().parallelStream()
					.filter(Organisation.class::isInstance).map(Organisation.class::cast).collect(Collectors.toList()));*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
