package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.scenarios;

import java.io.File;
import java.util.Arrays;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;

import com.att.research.xacml.api.Decision;

/**
 * Defines a test scenario. Change settings here in order to adapt to where you have stored the input files.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class TestScenario {
	// SETTINGS ////////////////////////////////////////////////////////////////////////////////
	public static final String PATH_PREFIX = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/";
	
	public static final String PATH_INPUT_MODELS = "models/UseCasesTechnicalReport/";
	private static final String FILENAME_PREFIX_INPUT_MODEL = "uc-test";
	
	private static final String PATH_GITBASE = "models/PCM-2-XACML/"; 
	
	private static final String FILENAME_OUTPUT_POLICYSET = "outSet.xml";
	private static final String EXPLICIT_OUTPUT_POLICYSET_DIR = "out/";
	////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final String PATH_TEST_DIR = PATH_PREFIX + PATH_GITBASE
			+ "tests/org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests/testrequests/";
		
	public static final String PATH_OUTPUT_POLICYSET = PATH_PREFIX + FILENAME_OUTPUT_POLICYSET;
	
	public static final String PATH_OUT_DIR = PATH_PREFIX + EXPLICIT_OUTPUT_POLICYSET_DIR;
	
	private final String scenarioDirectoryName;
	
	private final String modelPath;
	private final String testRequestPath;
	private final String testName;
	private final Decision expected;
	
	public TestScenario(final String scenarioDirectoryName, final String testFileName, final Decision expected) {
		this.scenarioDirectoryName = scenarioDirectoryName;
		this.modelPath = PATH_PREFIX + PATH_INPUT_MODELS + scenarioDirectoryName + getFilenamePrefixInputModel(scenarioDirectoryName);
		this.testRequestPath = PATH_TEST_DIR + testFileName;
		this.testName = testFileName.replaceAll("\\.xml", "");
		this.expected = expected;
	}

	public String getDataPath() {
		return this.modelPath + ".dataprocessing";
	}
	
	public DataSpecification load() {
	    return new ModelLoader(getDataPath()).loadDataSpecification();
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
	
	private String getFilenamePrefixInputModel(final String scenarioDirectoryName) {
		final File[] fileList = new File(PATH_PREFIX + PATH_INPUT_MODELS + scenarioDirectoryName).listFiles();
		if (Arrays.stream(fileList).anyMatch(f -> f.getName().endsWith(".dynamicextension"))) {
			// get filename prefix
			return Arrays.stream(fileList)
					.filter(f -> f.getName().endsWith(".dynamicextension"))
					.map(f -> f.getName().replaceAll("\\.dynamicextension", ""))
					.toArray()[0].toString();
		}
		return FILENAME_PREFIX_INPUT_MODEL;
	}

	public String getExplicitOutputPolicyPath() {
		return PATH_PREFIX + EXPLICIT_OUTPUT_POLICYSET_DIR
				+ this.scenarioDirectoryName.substring(0, this.scenarioDirectoryName.length() - 1) + ".xml";
	}
}
