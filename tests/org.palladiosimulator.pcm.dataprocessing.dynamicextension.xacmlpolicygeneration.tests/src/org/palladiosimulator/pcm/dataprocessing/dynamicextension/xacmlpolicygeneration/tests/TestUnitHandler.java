package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests;

import java.util.stream.Stream;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;

/**
 * A handler mock-up for the unit tests.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public final class TestUnitHandler {
	private static final String PATH_PREFIX = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/";
	private static final String PATH_INPUT_MODELS = "models/UseCasesTechnicalReport/";
	private static final String PATH_USECASE = "UC-Test/uc-test";

	public static final String PATH_DATA = PATH_PREFIX + PATH_INPUT_MODELS + PATH_USECASE + ".dataprocessing";
	
	private TestUnitHandler() {
		assert false;
	}
	
	protected static Stream<Context> getContexts(RelatedCharacteristics e) {
		return e.getCharacteristics().getOwnedCharacteristics().stream().filter(ContextCharacteristic.class::isInstance)
				.map(ContextCharacteristic.class::cast).flatMap(i -> i.getContext().stream());
	}
}
