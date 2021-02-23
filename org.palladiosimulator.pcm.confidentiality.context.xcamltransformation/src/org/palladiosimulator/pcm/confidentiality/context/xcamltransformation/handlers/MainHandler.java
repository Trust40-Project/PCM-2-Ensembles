package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.handlers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.ContextHandler;

import com.att.research.xacml.util.XACMLPolicyWriter;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;

/**
 * The MainHandler is the main class for the plugin from which the generation is started.
 * @author vladsolovyev
 * @version 1.0
 */
public class MainHandler {

    private static final Logger LOGGER = Logger.getLogger(MainHandler.class.getName());
    public static final String MODEL_SUFFIX = ".context";

    /**
     * performs transformation of the context model to an XACML policy
     * @param modelPath an input file of the model
     * @param outputFile an output XACML file
     * @return a string which contains information if the model could be transformed successfully
     */
    public String performTransformation(Path modelPath, Path outputFile) {
        String result = null;
        if (Files.exists(modelPath) && modelPath.toString().endsWith(MODEL_SUFFIX)) {
            LOGGER.info(String.format("Model %s will be transformed", modelPath.toAbsolutePath().toString()));
            try {
                ContextHandler ch = new ContextHandler(modelPath);
                PolicySetType policySet = ch.createPolicySet();
                LOGGER.info(String.format("Transformation output will be written to %s", outputFile.toAbsolutePath().toString()));
                Path transformationOutputFile = XACMLPolicyWriter.writePolicyFile(outputFile, policySet);
                if (Objects.isNull(transformationOutputFile)) {
                    result = "Policy set could not be written to the output file";
                    LOGGER.severe(result);
                } else {
                    result = String.format("Policy set sucessfully written to %s", transformationOutputFile.toAbsolutePath().toString());
                    LOGGER.info(result);
                }
            } catch (Exception ex) {
                result = String.format("Transformation could not be completed: %s", ex.getMessage());
                LOGGER.severe(result);
            }
        } else {
            result = String.format("Model %s does not exist or is not a valid context model", modelPath.toAbsolutePath().toString());
            LOGGER.severe(result);
        }
        return result;
    }
}
