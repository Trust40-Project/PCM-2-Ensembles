package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.handlers;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.ContextHandler;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.modelproperties.ModelProperties;

import com.att.research.xacml.util.XACMLPolicyWriter;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;

/**
 * The MainHandler is the main class for the plugin from which the generation is started.
 * @author vladsolovyev
 * @version 1.0
 */
public class MainHandler extends AbstractHandler {

    private static final Logger LOGGER = Logger.getLogger(MainHandler.class.getName());
    private final ModelProperties modelProperties = new ModelProperties();

    /**
     * starts processing
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Objects.requireNonNull(event);
        String result = null;
        Optional<String> modelPath = modelProperties.getModelPath();
        if (modelPath.isPresent()) {
            result = performTransformation(modelPath.get());
        } else {
            result = "Model path is not specified, the transformation will not be started. Please check the properties file.";
            LOGGER.severe(result);
        }
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        MessageDialog.openInformation(window.getShell(), "XACML - Policy generation result", result);
        return null;
    }

    /**
     * performs transformation of the context model to an XACML policy
     * @param modelPath is a path of the model which has to be transformed
     * @return a string which contains information if the model could be transformed successfully
     */
    private String performTransformation(String modelPath) {
        LOGGER.info(String.format("Model %s will be transformed", modelPath));
        ContextHandler ch = new ContextHandler(modelPath);
        String result = null;
        try {
            PolicySetType policySet = ch.createPolicySet();
            Path outputFile = Path.of(modelProperties.getOutputFilePath());
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
        return result;
    }
}
