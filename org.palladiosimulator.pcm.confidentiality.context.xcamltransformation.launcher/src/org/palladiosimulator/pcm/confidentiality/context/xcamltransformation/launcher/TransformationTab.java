package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TransformationTab extends AbstractLaunchConfigurationTab {

    private Text inputModelPath;
    private Text outputFilePath;

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

        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);

        Label inputLabel = new Label(comp, SWT.NONE);
        inputLabel.setText("Model path:");
        GridDataFactory.swtDefaults().applyTo(inputLabel);

        inputModelPath = new Text(comp, SWT.BORDER);
        inputModelPath.setMessage("please enter a model path");
        inputModelPath.addModifyListener(modifyListener);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(inputModelPath);

        Label outputLabel = new Label(comp, SWT.NONE);
        outputLabel.setText("Output file path:");
        GridDataFactory.swtDefaults().applyTo(inputLabel);

        outputFilePath = new Text(comp, SWT.BORDER);
        outputFilePath.setMessage("please enter an output file path");
        outputFilePath.addModifyListener(modifyListener);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(outputFilePath);
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
            consoleText = configuration.getAttribute(TransformationLaunchConfiguration.OUTPUT_FILE, "");
            outputFilePath.setText(consoleText);
            setDirty(true);
            updateLaunchConfigurationDialog();
        } catch (CoreException e) {
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(TransformationLaunchConfiguration.INPUT_FILE, inputModelPath.getText());
        configuration.setAttribute(TransformationLaunchConfiguration.OUTPUT_FILE, outputFilePath.getText());
        setDirty(true);
    }

    @Override
    public String getName() {
        return "XACML Transformation properties";
    }
}
