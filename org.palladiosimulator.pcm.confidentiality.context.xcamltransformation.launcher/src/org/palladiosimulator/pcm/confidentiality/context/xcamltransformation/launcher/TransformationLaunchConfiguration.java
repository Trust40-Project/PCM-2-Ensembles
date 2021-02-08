package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher;

import java.nio.file.Path;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.handlers.MainHandler;

public class TransformationLaunchConfiguration extends LaunchConfigurationDelegate {
    public final static String OUTPUT_FILE = "Output";
    public final static String INPUT_FILE = "Input";

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
            throws CoreException {
        Path inputFile = Path.of(configuration.getAttribute(INPUT_FILE, ""));
        Path outputFile = Path.of(configuration.getAttribute(OUTPUT_FILE, ""));
        MainHandler handler = new MainHandler();
        handler.createXACML(inputFile, outputFile);
    }
}
