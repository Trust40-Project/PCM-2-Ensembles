package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers;

import java.nio.file.Path;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;

import com.att.research.xacml.util.XACMLPolicyWriter;

/**
 * The SampleHandler is the main class for the plugin from which the generation is started.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class SampleHandler extends AbstractHandler {
    // SETTINGS ////////////////////////////////////////////////////////////////////////////////
    public static final String PATH_PREFIX = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/";
    private static final String PATH_INPUT_MODELS = "models/UseCasesTechnicalReport/";
    private static final String PATH_USECASE = "UC-Test/uc-test";

    private static final String FILENAME_OUTPUT_POLICYSET = "outSet.xml";
    ////////////////////////////////////////////////////////////////////////////////////////////

    public static final String PATH_DATA = PATH_PREFIX + PATH_INPUT_MODELS + PATH_USECASE + ".dataprocessing";

    public static final String PATH_OUTPUT_POLICYSET = PATH_PREFIX + FILENAME_OUTPUT_POLICYSET;

    public static final Logger LOGGER = PlatformUI.getWorkbench().getService(Logger.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        LOGGER.info("Using the eclipse logger");
        final ContextHandler ch = new ContextHandler(PATH_DATA);
        Path okPath = null;
        String error = null;
        try {
            // generate policy set
            var policySet = ch.createPolicySet();

            // write policy set
            final Path filenamePolicySet = Path.of(PATH_OUTPUT_POLICYSET);
            okPath = XACMLPolicyWriter.writePolicyFile(filenamePolicySet, policySet);
            error = okPath != null ? null : "an error uccured";
        } catch (IllegalStateException exc) {
            error = exc.getMessage();
        }

        // inform user
        final String result = error == null ? "policy set sucessfully written to " + okPath : error;
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        MessageDialog.openInformation(window.getShell(), "xacml-policygeneration" + (okPath != null ? "" : " ERROR"),
                result);

        return null;
    }
}
