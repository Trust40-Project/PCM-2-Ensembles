package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.handlers.MainHandler;

/**
 * the class is responsible for a launch of the transformation configuration
 * @author vladsolovyev
 */
public class TransformationLaunchConfiguration extends LaunchConfigurationDelegate {
    protected final static String OUTPUT_DIR = "Output_Directory";
    protected final static String OUTPUT_FILE = "Output_File";
    protected final static String INPUT_FILE = "Input_File";
    private static final Logger LOGGER = Logger.getLogger(TransformationLaunchConfiguration.class.getName());

    /**
     * the method launches a transformation
     */
    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
            throws CoreException {
        String inputFile = configuration.getAttribute(INPUT_FILE, "");
        String outputDir = configuration.getAttribute(OUTPUT_DIR, "");
        String outputFile = configuration.getAttribute(OUTPUT_FILE, "");

        getFilePath(inputFile).ifPresentOrElse(input -> {
            getOutputFile(outputDir, outputFile).ifPresentOrElse(output -> {
                MainHandler handler = new MainHandler();
                String result = handler.performTransformation(input, output);
                showMessageDialog(result);
            }, () -> {showMessageDialog("Could not determine a path of the output file. The model will not be transformed");});
        }, () -> {showMessageDialog("Could not determine a path of the input file. The model will not be transformed");});
    }

    /**
     * shows a message dialog on the GUI. It has to run so with asyncExec, otherwise it does not work.
     * @param message - a message to be shown
     */
    private void showMessageDialog(String message) {
        Display.getDefault().asyncExec(() ->
        {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            MessageDialog.openInformation(window.getShell(), "XACML - Policy generation result", message);
        });
    }

    /**
     * determines a file path. If the resource is a platform resource then it should be transformed using a path of the workspace
     * @param file - a path as a string for which a file path has to be determined.
     * @return optional of a path if it could be determined, Optional.empty() otherwise
     */
    private Optional<Path> getFilePath(String file) {
        URI fileURI = URI.createURI(file);
        if (fileURI.isPlatformResource()) {
            file = fileURI.toPlatformString(true);
            IResource fileAsResource = EcorePlugin.getWorkspaceRoot().findMember(file);
            if (Objects.isNull(fileAsResource)) {
                LOGGER.severe(String.format("Platform resource %s can not be found", file));
                return Optional.empty();
            } else {
                file = fileAsResource.getLocation().toString();
            }
        }
        return Optional.of(Paths.get(file));
    }

    /**
     * determines an output file. If an output directory does not exist, it is created.
     * @param outputDir - an output directory where an output file has to be created.
     * @param outputFile - a name of the output file
     * @return an optional of an output file path if it could be determined, Optional.empty() otherwise
     */
    private Optional<Path> getOutputFile(String outputDir, String outputFile) {
        Optional<Path> outputDirectory = getFilePath(outputDir);
        if (outputDirectory.isPresent()) {
            try {
                var path = outputDirectory.get();
                Files.createDirectories(path);
                return Optional.of(path.resolve(outputFile));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, (String.format("Can not create output directories %s", outputDirectory.toString())), e);
            }
        }
        return Optional.empty();
    }
}
