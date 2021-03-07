package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.tests;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.handlers.MainHandler;

/**
 * Test cases for MainHandler.
 * @author vladsolovyev
 * @version 1.0.0
 */

public class MainHandlerTest {

    @Test
    public void inputModelNotFound() {
        Path inputPath = Path.of("not_existing_model.context");
        assertTransformation(inputPath, false);
    }

    @Test
    public void modelWithoutExtensionIsNotSupported() {
        Path inputPath = Path.of("testfiles", "modelWithoutExtension");
        assertTransformation(inputPath, true);
    }

    @Test
    public void notValidModelNotTransformed() {
        Path inputPath = Path.of("testfiles", "notValid.context");
        assertTransformation(inputPath, true);
    }

    private void assertTransformation(Path inputPath, boolean inputExists) {
        MainHandler handler = new MainHandler();
        Path outPath = Path.of("testfiles", "output.xml");
        Assert.assertTrue(Files.notExists(outPath));
        Assert.assertEquals(Files.exists(inputPath), inputExists);
        handler.performTransformation(inputPath, outPath);
        Assert.assertTrue(Files.notExists(outPath));
    }
}
