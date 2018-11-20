package org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.generation;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.MainLoader.ModelLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Organisation;

public class ContextHandler {
	private DataSpecification dataContainer;
	private DynamicSpecification dynamicContainer;

	public ContextHandler(String pathDynamic, String pathData) {
		var modelloader = new ModelLoader(pathDynamic, pathData);
		this.dataContainer = modelloader.loadDataSpecification();
		this.dynamicContainer = modelloader.loadDynamicModel();
	}

	public void createContext() {
		var typeDefinition =  new TypeDefinition();
		var contextGeneration = new ContextGeneration(dataContainer,dynamicContainer);
		var scenarioDefinition = new ScenarioDefinition();
		try (var writer = new PrintWriter(new File("/home/majuwa/kit/Trust4.0/Software/source/icsa2019-ensembles/src/main/scala/scenarios/RunningExample.scala"), Charset.forName("UTF-8"))) {
			typeDefinition.createTypes(dynamicContainer, dataContainer, writer);
			List<String> listEnsembleNames = contextGeneration.generateRelatedContexts(dataContainer, writer);
			typeDefinition.createRootEnsemble(writer, listEnsembleNames);

			scenarioDefinition.writeScenario(writer, dynamicContainer.getSubjectContainer().getSubject().parallelStream()
					.filter(Organisation.class::isInstance).map(Organisation.class::cast).collect(Collectors.toList()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
