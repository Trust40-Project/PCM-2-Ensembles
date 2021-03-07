package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher.tabs;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * TabHelper class creates input dialogs of different types
 * @author vladsolovyev
 * @version 1.0.0
 *
 */
public class TabHelper {

    /**
     * creates input section with different fields
     * @param parentContainer - parent of the dialog
     * @param modifyListener - listener which reacts to modifications
     * @param groupLabel - label of the created group
     * @param fileExtensionRestrictions - extensions which have to be concerned
     * @param textField - an input text field
     * @param dialogTitle - a title of the dialog
     * @param dialogShell - a shell of the dialog
     * @param showWorkspaceSelectionButton - if true, then workspace selection button is shown
     * @param showFileSystemSelectionButton - if true, then file system selection button is shown
     * @param useFolder - decides if a selection should be a folder of a file
     */
    private static void createInputSection(Composite parentContainer, ModifyListener modifyListener, String groupLabel,
            String[] fileExtensionRestrictions, Text textField, String dialogTitle, Shell dialogShell,
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
        textField.setParent(fileInputGroup);
        GridData gridDataTextFileName = new GridData(4, 16777216, true, false);
        gridDataTextFileName.widthHint = 200;
        textField.setLayoutData(gridDataTextFileName);
        textField.addModifyListener(modifyListener);
        if (showWorkspaceSelectionButton) {
            Button workspaceButton = new Button(fileInputGroup, 0);
            workspaceButton.setText("Workspace...");
            workspaceButton.addSelectionListener(new WorkspaceButtonSelectionListener(textField,
                    fileExtensionRestrictions, dialogTitle, dialogShell, useFolder));
        }

        if (showFileSystemSelectionButton) {
            Button fileSystemButton = new Button(fileInputGroup, 0);
            fileSystemButton.setText("File System...");
            fileSystemButton.addSelectionListener(new LocalFileSystemButtonSelectionAdapter(textField,
                    fileExtensionRestrictions, dialogTitle, dialogShell, useFolder));
        }
    }

    /**
     * creates folder input section
     *
     * @param parentContainer - parent of the dialog
     * @param modifyListener - listener which reacts to modifications
     * @param groupLabel - label of the created group
     * @param textField - an input text field
     * @param dialogShell - a shell of the dialog
     */
    public static void createFolderInputSection(Composite parentContainer, ModifyListener modifyListener,
            String groupLabel, Text textField, Shell dialogShell) {
        createInputSection(parentContainer, modifyListener, groupLabel, new String[0], textField, "Select" + groupLabel,
                dialogShell, true, true, true);
    }

    /**
     * creates file input section
     *
     * @param parentContainer - parent of the dialog
     * @param modifyListener - listener which reacts to modifications
     * @param groupLabel - label of the created group
     * @param fileExtensionRestrictions - extensions which have to be concerned
     * @param textField - an input text field
     * @param dialogShell - a shell of the dialog
     */
    public static void createFileInputSection(Composite parentContainer, ModifyListener modifyListener,
            String groupLabel, String[] fileExtensionRestrictions, Text textField, Shell dialogShell) {
        createInputSection(parentContainer, modifyListener, groupLabel, fileExtensionRestrictions, textField,
                "Select " + groupLabel, dialogShell, true, true, false);
    }

    /**
     * creates text input section
     *
     * @param parentContainer - parent of the dialog
     * @param modifyListener - listener which reacts to modifications
     * @param groupLabel - label of the created group
     * @param textField - an input text field
     * @param dialogShell - a shell of the dialog
     */
    public static void createTextInputSection(Composite parentContainer, ModifyListener modifyListener,
            String groupLabel, Text textField, Shell dialogShell) {
        createInputSection(parentContainer, modifyListener, groupLabel, new String[0], textField,
                "Select " + groupLabel, dialogShell, false, false, false);
    }
}
