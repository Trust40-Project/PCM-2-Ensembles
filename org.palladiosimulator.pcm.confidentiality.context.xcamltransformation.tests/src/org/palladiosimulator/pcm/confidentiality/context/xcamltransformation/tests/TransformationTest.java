package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.tests;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.ContextHandler;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.pep.PEPEngine;
import com.att.research.xacml.api.pep.PEPEngineFactory;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.util.XACMLPolicyWriter;
import com.att.research.xacml.util.XACMLProperties;


public class TransformationTest {

    private String getDataPath() {
        return Path.of("testfiles", "models", "scenarios", "test_model.context").toAbsolutePath().toString();
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

        ContextHandler ch = new ContextHandler(this.getDataPath());
        var policySet = ch.createPolicySet();

        final Path filenamePolicySet = Path.of("testfiles", "out1.xml");
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, Path.of("testfiles", "xacml.properties").toAbsolutePath().toString());
        XACMLPolicyWriter.writePolicyFile(filenamePolicySet, policySet);
        XACMLProperties.setProperty("example.file", Path.of("testfiles", "out1.xml").toString());
        Path requestFile = Path.of("testfiles", requestFileName);

        Request request = DOMRequest.load(requestFile.toFile());
        PEPEngine pep = PEPEngineFactory.newInstance().newEngine();
        final var result = pep.decide(request);
        System.out.println(result + System.lineSeparator());
        Assert.assertEquals(expectedDecision, result.getResults().iterator().next().getDecision());
    }
}
