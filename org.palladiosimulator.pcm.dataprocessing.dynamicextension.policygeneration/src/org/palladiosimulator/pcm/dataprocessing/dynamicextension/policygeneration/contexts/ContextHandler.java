package org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.contexts;

import java.util.stream.Collectors;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristicType;

public class ContextHandler {
	private DataSpecification dataContainer;
	private DynamicSpecification dynamicContainer;

	public ContextHandler(DataSpecification dataContainer, DynamicSpecification dynamicContainer) {
		this.dataContainer = dataContainer;
		this.dynamicContainer = dynamicContainer;
	}

	public void createContext() {
		var gatherList = dataContainer.getCharacteristicTypeContainers().parallelStream()
				.flatMap(e -> e.getCharacteristicTypes().parallelStream())
				.filter(e -> e instanceof ContextCharacteristicType)
				.flatMap(e -> ((ContextCharacteristicType) e).getContext().parallelStream())
				.collect(Collectors.toList());
		gatherList.forEach(e -> System.out.println(e.getEntityName()));
		
		
	}

}
