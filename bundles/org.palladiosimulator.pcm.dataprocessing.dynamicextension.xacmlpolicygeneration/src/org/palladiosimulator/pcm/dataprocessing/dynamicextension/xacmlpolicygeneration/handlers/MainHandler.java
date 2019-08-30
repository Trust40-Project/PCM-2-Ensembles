package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers;

import java.nio.file.Path;
import java.util.Objects;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;

import com.att.research.xacml.util.XACMLPolicyWriter;

/**
 * The MainHandler is the main class for the plugin from which the generation is started.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class MainHandler extends AbstractHandler {
    // SETTINGS ////////////////////////////////////////////////////////////////////////////////
    public static final String PATH_PREFIX = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/";
    private static final String INPUT_MODELS_PATH = "models/UseCasesTechnicalReport/";
    private static final String USECASE_PATH = "UC-Test/uc-test";

    private static final String FILENAME_OUTPUT_POLICYSET = "outSet.xml";
    
    private static final boolean IS_ECLIPSE_LOGGING = false;
    ////////////////////////////////////////////////////////////////////////////////////////////

    public static final String DATA_PATH = PATH_PREFIX + INPUT_MODELS_PATH + USECASE_PATH + ".dataprocessing";

    public static final String OUTPUT_POLICYSET_PATH = PATH_PREFIX + FILENAME_OUTPUT_POLICYSET;
    
    public static final Logger LOGGER = IS_ECLIPSE_LOGGING ? PlatformUI.getWorkbench().getService(Logger.class)
            : new MockLogger();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Objects.requireNonNull(event);
        LOGGER.info(IS_ECLIPSE_LOGGING ? "Using the eclipse logger" : "Using an eclipse logger mock-up");
        final ContextHandler ch = new ContextHandler(ModelLoader.getIdOfModel(DATA_PATH), DATA_PATH);
        Path okPath = null;
        String error = null;
        try {
            // generate policy set
            var policySet = ch.createPolicySet();

            // write policy set
            final Path filenamePolicySet = Path.of(OUTPUT_POLICYSET_PATH);
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
