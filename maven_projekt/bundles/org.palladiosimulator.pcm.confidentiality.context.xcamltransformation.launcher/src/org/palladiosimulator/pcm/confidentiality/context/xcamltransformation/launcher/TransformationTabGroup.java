package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * this class creates a group of tabs.
 * @author vladsolovyev
 * @version 1.0.0
 */
public class TransformationTabGroup extends AbstractLaunchConfigurationTabGroup {

    /**
     * crates a group of tabs containing a transformation tab and a common tab.
     */
    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        setTabs(new ILaunchConfigurationTab[] {new TransformationTab(), new CommonTab()});
    }

}