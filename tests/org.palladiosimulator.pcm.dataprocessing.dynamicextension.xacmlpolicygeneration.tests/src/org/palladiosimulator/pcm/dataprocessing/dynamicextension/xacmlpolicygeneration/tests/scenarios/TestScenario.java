package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.scenarios;

import com.att.research.xacml.api.Decision;

public class TestScenario {
	// SETTINGS ////////////////////////////////////////////////////////////////////////////////
	public static final String PATH_PREFIX = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/";
	
	public static final String PATH_INPUT_MODELS = "models/UseCasesTechnicalReport/";
	private static final String FILENAME_PREFIX_INPUT_MODEL = "uc-test";
	
	private static final String PATH_GITBASE = "models/PCM-2-XACML/"; 
	
	private static final String FILENAME_OUTPUT_POLICYSET = "outSet.xml";
	////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final String PATH_TEST_DIR = PATH_PREFIX + PATH_GITBASE
			+ "tests/org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests/testrequests/";
		
	public static final String PATH_OUTPUT_POLICYSET = PATH_PREFIX + FILENAME_OUTPUT_POLICYSET;
	
	private final String modelPath;
	private final String testRequestPath;
	private final String testName;
	private final Decision expected;
	
	public TestScenario(final String scenarioDirectoryName, final String testFileName, final Decision expected) {
		this.modelPath = PATH_PREFIX + PATH_INPUT_MODELS + scenarioDirectoryName + FILENAME_PREFIX_INPUT_MODEL;
		this.testRequestPath = PATH_TEST_DIR + testFileName;
		this.testName = testFileName.replaceAll("\\.xml", "");
		this.expected = expected;
	}
	
	public TestScenario(final TestScenario sameModelScenario, final String testFileName, final Decision expected) {
		this.modelPath = sameModelScenario.modelPath;
		this.testRequestPath = PATH_TEST_DIR + testFileName;
		this.testName = testFileName.replaceAll("\\.xml", "");
		this.expected = expected;
	}
	
	public String getDataPath() {
		return this.modelPath + ".dataprocessing";
	}
	
	public String getDynamicPath() {
		return this.modelPath + ".dynamicextension";
	}
	
	public String getTestRequestPath() {
		return this.testRequestPath;
	}
	
	public String getTestName() {
		return this.testName;
	}
	
	public Decision getExpected() {
		return this.expected;
	}
}
