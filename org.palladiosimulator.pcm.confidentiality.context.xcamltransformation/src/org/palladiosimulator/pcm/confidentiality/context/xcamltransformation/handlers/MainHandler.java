package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.handlers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
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

    /**
     * starts transformation of the context model to XACML output file
     * @param modelPath an input file of the model
     * @param outputFile an output XACML file
     */
    public void createXACML(Path modelPath, Path outputFile) {
        String result = performTransformation(modelPath, outputFile);
        if (Platform.isRunning()) {
            Display.getDefault().asyncExec(() ->
            {
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                MessageDialog.openInformation(window.getShell(), "XACML - Policy generation result", result);
            });
        }
    }

    /**
     * performs transformation of the context model to an XACML policy
     * @param modelPath an input file of the model
     * @param outputFile an output XACML file
     * @return a string which contains information if the model could be transformed successfully
     */
    private String performTransformation(Path modelPath, Path outputFile) {
        String result = null;
        if (Files.exists(modelPath)) {
            LOGGER.info(String.format("Model %s will be transformed", modelPath.toAbsolutePath().toString()));
            ContextHandler ch = new ContextHandler(modelPath);
            try {
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
            result = String.format("Model %s does not exist", modelPath.toAbsolutePath().toString());
            LOGGER.severe(result);
        }
        return result;
    }
}
