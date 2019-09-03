package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests;

import java.nio.file.Paths;
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
    // base path is the org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests folder inside the tests folder in the PCM-2-XACML git
	private static final String RELATIVE_PATH_PREFIX = Paths.get("../../../").toAbsolutePath().toString();
	private static final String PATH_INPUT_MODELS = "/UseCasesTechnicalReport/";
	private static final String PATH_USECASE = "UC-Test/uc-test";

	public static final String DATA_PATH = RELATIVE_PATH_PREFIX + PATH_INPUT_MODELS + PATH_USECASE + ".dataprocessing";
	
	private TestUnitHandler() {
		assert false;
	}
	
	protected static Stream<Context> getContexts(RelatedCharacteristics e) {
		return e.getCharacteristics().getOwnedCharacteristics().stream().filter(ContextCharacteristic.class::isInstance)
				.map(ContextCharacteristic.class::cast).flatMap(i -> i.getContext().stream());
	}
}
