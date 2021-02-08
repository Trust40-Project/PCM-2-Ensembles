package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import com.att.research.xacml.api.pep.PEPEngine;
import com.att.research.xacml.api.pep.PEPEngineFactory;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.util.XACMLProperties;

@TestInstance(Lifecycle.PER_CLASS)
public class TransformationTest {

    private Path outPath;
    private final static Path TEST_FILES_FOLDER = Path.of("testfiles");
    private final static Path MODEL_PATH = TEST_FILES_FOLDER.resolve("models").resolve("scenarios").resolve("test_model.context");

    @BeforeAll
    private void beforeAll() {
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, TEST_FILES_FOLDER.resolve("xacml.properties").toAbsolutePath().toString());
    }

    @BeforeEach
    private void beforeEach() throws IOException {
        outPath = Files.createTempFile(Path.of("testfiles"), "output", ".xml");
    }

    @AfterEach
    private void afterEach() throws IOException {
        Files.deleteIfExists(outPath);
    }

    private static Stream<Arguments> requestFiles() {
        return Stream.of(
          Arguments.of("evalUC0Permit.xml", Decision.PERMIT),
          Arguments.of("evalUC0Deny.xml", Decision.DENY)
        );
    }

    @ParameterizedTest
    @MethodSource("requestFiles")
    public void testTransformation(String requestFileName, Decision expectedDecision) throws Exception {
        MainHandler handler = new MainHandler();
        handler.createXACML(MODEL_PATH, outPath);

        XACMLProperties.setProperty("properties.file", outPath.toAbsolutePath().toString());

        Path requestFile = Path.of("testfiles", requestFileName);
        Request request = DOMRequest.load(requestFile.toFile());
        PEPEngine pep = PEPEngineFactory.newInstance().newEngine();
        final var result = pep.decide(request);
        System.out.println(result + System.lineSeparator());
        Assert.assertEquals(expectedDecision, result.getResults().iterator().next().getDecision());
    }
}
