package org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.contexts;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicSpecification;

public abstract class ContextGeneration {
	private DataSpecification dataContainer;
	private DynamicSpecification dynamicContainer;
	public ContextGeneration(DataSpecification dataContainer, DynamicSpecification dynamicContainer) {
		this.dataContainer = dataContainer;
		this.dynamicContainer = dynamicContainer;
		dataContainer.getCharacteristicTypeContainers();
	}
	/**
	 * @return the dataContainer
	 */
	public final DataSpecification getDataContainer() {
		return dataContainer;
	}
	/**
	 * @return the dynamicContainer
	 */
	public final DynamicSpecification getDynamicContainer() {
		return dynamicContainer;
	}

}
