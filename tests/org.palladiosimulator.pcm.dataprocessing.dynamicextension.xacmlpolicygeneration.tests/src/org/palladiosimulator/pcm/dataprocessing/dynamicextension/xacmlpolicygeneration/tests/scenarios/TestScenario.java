package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.scenarios;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.jupiter.api.function.Executable;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.pep.PEPEngine;
import com.att.research.xacml.api.pep.PEPEngineFactory;
import com.att.research.xacml.api.pep.PEPException;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.std.dom.DOMStructureException;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.XACMLPolicyWriter;

/**
 * Defines a test scenario. Change settings here in order to adapt to where you have stored the
 * input files.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class TestScenario implements Executable {
    // SETTINGS ////////////////////////////////////////////////////////////////////////////////
    // base path is the
    // org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests folder
    // inside the tests folder in the PCM-2-XACML git
    private static final String RELATIVE_PATH_PREFIX = Paths.get("../../../../").toAbsolutePath().toString() + "/";

    public static final String PATH_INPUT_MODELS = "models/UseCasesTechnicalReport/";
    private static final String FILENAME_PREFIX_INPUT_MODEL = "uc-test";

    private static final String PATH_GITBASE = "models/PCM-2-XACML/";

    private static final String FILENAME_OUTPUT_POLICYSET = "outSet.xml";
    private static final String EXPLICIT_OUTPUT_POLICYSET_DIR = "out/";
    ////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_TEST_DIR = RELATIVE_PATH_PREFIX + PATH_GITBASE
            + "tests/org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests/testrequests/";

    public static final String PATH_OUTPUT_POLICYSET = RELATIVE_PATH_PREFIX + FILENAME_OUTPUT_POLICYSET;

    public static final String PATH_OUT_DIR = RELATIVE_PATH_PREFIX + EXPLICIT_OUTPUT_POLICYSET_DIR;

    private final String scenarioDirectoryName;

    private final String modelPath;
    private final String testRequestPath;
    private final String testName;
    private final Decision expected;

    public TestScenario(final String scenarioDirectoryName, final String testFileName, final Decision expected) {
        this.scenarioDirectoryName = scenarioDirectoryName;
        this.modelPath = RELATIVE_PATH_PREFIX + PATH_INPUT_MODELS + scenarioDirectoryName
                + getFilenamePrefixInputModel(scenarioDirectoryName);
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
        final File[] fileList = new File(RELATIVE_PATH_PREFIX + PATH_INPUT_MODELS + scenarioDirectoryName).listFiles();
        if (Arrays.stream(fileList).anyMatch(f -> f.getName().endsWith(".dynamicextension"))) {
            // get filename prefix
            return Arrays.stream(fileList).filter(f -> f.getName().endsWith(".dynamicextension"))
                    .map(f -> f.getName().replaceAll("\\.dynamicextension", "")).toArray()[0].toString();
        }
        return FILENAME_PREFIX_INPUT_MODEL;
    }

    public String getExplicitOutputPolicyPath() {
        return RELATIVE_PATH_PREFIX + EXPLICIT_OUTPUT_POLICYSET_DIR
                + this.scenarioDirectoryName.substring(0, this.scenarioDirectoryName.length() - 1) + ".xml";
    }

    @Override
    public void execute() throws Throwable {
        System.out.println(getTestName());
        System.out.println("-----------------------------------------------------");
        ContextHandler ch = new ContextHandler(ModelLoader.getIdOfModel(this.getDataPath()),
                this.getDataPath());
        var policySet = ch.createPolicySet();
        // write policySet
        final Path filenamePolicySet = Path.of(TestScenario.PATH_OUTPUT_POLICYSET);
        final Path filenamePolicySetExplicit = Path.of(this.getExplicitOutputPolicyPath());
        XACMLPolicyWriter.writePolicyFile(filenamePolicySet, policySet);
        // also writing policy set to extra file for manual checks for correctness
        XACMLPolicyWriter.writePolicyFile(filenamePolicySetExplicit, policySet);

        // Request-Test
        try {
            Request request = DOMRequest.load(new File(this.getTestRequestPath()));

            PEPEngine pep = PEPEngineFactory.newInstance().newEngine();
            final var result = pep.decide(request);
            System.out.println(result + "\n");
            Assert.assertEquals(this.getExpected(), result.getResults().iterator().next().getDecision());
        } catch (DOMStructureException e) {
            e.printStackTrace();
        } catch (FactoryException e) {
            e.printStackTrace();
        } catch (PEPException e) {
            e.printStackTrace();
        }
    }
}
