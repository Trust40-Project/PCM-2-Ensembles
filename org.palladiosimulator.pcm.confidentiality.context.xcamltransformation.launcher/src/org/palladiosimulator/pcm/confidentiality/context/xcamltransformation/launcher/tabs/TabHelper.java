package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher.tabs;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TabHelper {

    /**
     * creates input section with different fields
     * @param parentContainer
     * @param modifyListener
     * @param groupLabel
     * @param fileExtensionRestrictions
     * @param textFileNameToLoad
     * @param dialogTitle
     * @param dialogShell
     * @param showWorkspaceSelectionButton
     * @param showFileSystemSelectionButton
     * @param useFolder
     */
    private static void createInputSection(Composite parentContainer, ModifyListener modifyListener, String groupLabel,
            String[] fileExtensionRestrictions, Text textFileNameToLoad, String dialogTitle, Shell dialogShell,
            boolean showWorkspaceSelectionButton, boolean showFileSystemSelectionButton, boolean useFolder) {
        Group fileInputGroup = new Group(parentContainer, 0);
        GridLayout glFileInputGroup = new GridLayout();
        int numColumns = 1;

        if (showWorkspaceSelectionButton) {
            ++numColumns;
        }

        if (showFileSystemSelectionButton) {
            ++numColumns;
        }

        glFileInputGroup.numColumns = numColumns;
        fileInputGroup.setLayout(glFileInputGroup);
        fileInputGroup.setText(groupLabel);
        fileInputGroup.setLayoutData(new GridData(4, 16777216, true, false));
        textFileNameToLoad.setParent(fileInputGroup);
        GridData gridDataTextFileName = new GridData(4, 16777216, true, false);
        gridDataTextFileName.widthHint = 200;
        textFileNameToLoad.setLayoutData(gridDataTextFileName);
        textFileNameToLoad.addModifyListener(modifyListener);
        if (showWorkspaceSelectionButton) {
            Button workspaceButton = new Button(fileInputGroup, 0);
            workspaceButton.setText("Workspace...");
            workspaceButton.addSelectionListener(new WorkspaceButtonSelectionListener(textFileNameToLoad,
                    fileExtensionRestrictions, dialogTitle, dialogShell, useFolder));
        }

        if (showFileSystemSelectionButton) {
            Button fileSystemButton = new Button(fileInputGroup, 0);
            fileSystemButton.setText("File System...");
            fileSystemButton.addSelectionListener(new LocalFileSystemButtonSelectionAdapter(textFileNameToLoad,
                    fileExtensionRestrictions, dialogTitle, dialogShell, useFolder));
        }
    }

    /**
     * creates folder input section
     * @param parentContainer
     * @param modifyListener
     * @param groupLabel
     * @param textFileNameToLoad
     * @param dialogShell
     */
    public static void createFolderInputSection(Composite parentContainer, ModifyListener modifyListener,
            String groupLabel, Text textFileNameToLoad, Shell dialogShell) {
        createInputSection(parentContainer, modifyListener, groupLabel, new String[0], textFileNameToLoad, "Select" + groupLabel,
                dialogShell, true, true, true);
    }

    /**
     * creates file input section
     * @param parentContainer
     * @param modifyListener
     * @param groupLabel
     * @param fileExtensionRestrictions
     * @param textFileNameToLoad
     * @param dialogShell
     */
    public static void createFileInputSection(Composite parentContainer, ModifyListener modifyListener, String groupLabel, String[] fileExtensionRestrictions, Text textFileNameToLoad, Shell dialogShell) {
        createInputSection(parentContainer, modifyListener, groupLabel, fileExtensionRestrictions, textFileNameToLoad, "Select " + groupLabel, dialogShell, true, true, false);
    }

    /**
     * creates text input section
     * @param parentContainer
     * @param modifyListener
     * @param groupLabel
     * @param textFileNameToLoad
     * @param dialogShell
     */
    public static void createTextInputSection(Composite parentContainer, ModifyListener modifyListener, String groupLabel, Text textFileNameToLoad, Shell dialogShell) {
        createInputSection(parentContainer, modifyListener, groupLabel, new String[0], textFileNameToLoad, "Select " + groupLabel, dialogShell, false, false, false);
    }
}
