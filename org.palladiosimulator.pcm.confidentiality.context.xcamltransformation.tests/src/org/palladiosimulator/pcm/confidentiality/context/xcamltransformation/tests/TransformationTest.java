package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.handlers.MainHandler;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.util.XACMLProperties;
import com.att.research.xacmlatt.pdp.ATTPDPEngineFactory;

@TestInstance(Lifecycle.PER_CLASS)
public class TransformationTest {

    private static final Logger LOGGER = Logger.getLogger(TransformationTest.class.getName());

    private Path outPath;
    private static final Path TEST_FILES_FOLDER = Path.of("testfiles");
    private static final Path MODEL_PATH = TEST_FILES_FOLDER.resolve("models").resolve("Scenarios");
    private static final Path REQUESTS = TEST_FILES_FOLDER.resolve("requests");

    @BeforeAll
    private void beforeAll() {
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME,
                TEST_FILES_FOLDER.resolve("xacml.properties").toAbsolutePath().toString());
    }

    @BeforeEach
    private void beforeEach() throws IOException {
        outPath = Files.createTempFile(TEST_FILES_FOLDER, "output", ".xml");
    }

    @AfterEach
    private void afterEach() throws IOException {
        Files.deleteIfExists(outPath);
    }

    private static Stream<Arguments> fileNames() {
        return Stream.of(
          Arguments.of("single_attribute"),
          Arguments.of("hierarchical_bottom_up"),
          Arguments.of("hierarchical_top_down"),
          Arguments.of("relatedContextSet"),
          Arguments.of("two_allof"),
          Arguments.of("two_policies"),
          Arguments.of("recursiveIncluding")
        );
    }

    @ParameterizedTest
    @MethodSource("fileNames")
    public void testTransformation(String fileName) throws Exception {
        String inputFile = String.format("%s.context", fileName);
        MainHandler handler = new MainHandler();
        handler.performTransformation(MODEL_PATH.resolve(inputFile), outPath);
        checkContent(String.format("%s.xml", fileName));
        testRequests(fileName);
    }

    private void checkContent(String expectedFile) throws IOException {
        List<String> expectedLines = Files.readAllLines(MODEL_PATH.resolve(expectedFile));
        List<String> actualLines = Files.readAllLines(outPath);
        Assert.assertEquals(expectedLines.size(), actualLines.size());
        Assert.assertTrue(expectedLines.containsAll(actualLines));
    }


    private void testRequests(String fileName) throws Exception {
        XACMLProperties.setProperty("properties.file", outPath.toAbsolutePath().toString());
        String permitFileName = String.format("%sPermit.xml", fileName);
        String denyFileName = String.format("%sDeny.xml", fileName);
        String notApplicable = String.format("%sNotApplicable.xml", fileName);
        executeRequest(REQUESTS.resolve(permitFileName), Decision.PERMIT);
        executeRequest(REQUESTS.resolve(denyFileName), Decision.DENY);
        executeRequest(REQUESTS.resolve(notApplicable), Decision.NOTAPPLICABLE);
    }

    private void executeRequest(Path requestFile, Decision expectedDecision) throws Exception {
        Request request = DOMRequest.load(requestFile.toFile());
        PDPEngine pdpEngine = ATTPDPEngineFactory.newInstance().newEngine();
        final var result = pdpEngine.decide(request);
        LOGGER.info(result.toString());
        Assert.assertEquals(expectedDecision, result.getResults().iterator().next().getDecision());
    }
}
