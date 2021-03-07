package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher.tabs.TabHelper;

/**
 * setting tab which allows to set attributes for a model transformation
 * @author vladsolovyev
 * @version 1.0.0
 */
public class TransformationTab extends AbstractLaunchConfigurationTab {

    private Text inputModelPath;
    private Text outputDirectoryPath;
    private Text outputFile;

    @Override
    public void createControl(Composite parent) {
        final ModifyListener modifyListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setDirty(true);
                updateLaunchConfigurationDialog();
            }
        };

        Composite comp = new Group(parent, SWT.BORDER);
        setControl(comp);

        GridLayoutFactory.swtDefaults().numColumns(1).applyTo(comp);

        inputModelPath = new Text(comp, SWT.BORDER);
        inputModelPath.setMessage("please enter a model path");
        inputModelPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        TabHelper.createFileInputSection(comp, modifyListener, "Model path",
                new String[] { "*.context" }, inputModelPath, Display.getCurrent().getActiveShell());

        outputDirectoryPath = new Text(comp, SWT.BORDER);
        outputDirectoryPath.setMessage("please enter an output directory");
        outputDirectoryPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        TabHelper.createFolderInputSection(comp, modifyListener, "Output directory",
                outputDirectoryPath, Display.getCurrent().getActiveShell());


        outputFile = new Text(comp, SWT.BORDER);
        outputFile.setMessage("please enter an output file name");
        outputFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        TabHelper.createTextInputSection(comp, modifyListener, "Output file name",
                outputFile, Display.getCurrent().getActiveShell());

        updateLaunchConfigurationDialog();
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            String consoleText = configuration.getAttribute(TransformationLaunchConfiguration.INPUT_FILE, "");
            inputModelPath.setText(consoleText);
            consoleText = configuration.getAttribute(TransformationLaunchConfiguration.OUTPUT_DIR, "");
            outputDirectoryPath.setText(consoleText);
            consoleText = configuration.getAttribute(TransformationLaunchConfiguration.OUTPUT_FILE, "");
            outputFile.setText(consoleText);
            setDirty(true);
            updateLaunchConfigurationDialog();
        } catch (CoreException e) {
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(TransformationLaunchConfiguration.INPUT_FILE, inputModelPath.getText());
        configuration.setAttribute(TransformationLaunchConfiguration.OUTPUT_DIR, outputDirectoryPath.getText());
        configuration.setAttribute(TransformationLaunchConfiguration.OUTPUT_FILE, outputFile.getText());
        setDirty(true);
    }

    @Override
    public String getName() {
        return "XACML Transformation properties";
    }
}
