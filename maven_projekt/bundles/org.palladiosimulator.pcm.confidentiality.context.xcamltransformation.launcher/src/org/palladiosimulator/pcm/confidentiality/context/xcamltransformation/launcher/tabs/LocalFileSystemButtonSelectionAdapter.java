package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher.tabs;

import java.util.Optional;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * this class is responsible for selection of files in the local file system
 * @author vladsolovyev
 * @version 1.0.0
 */
public class LocalFileSystemButtonSelectionAdapter extends FileSelectionAdapter {

    public LocalFileSystemButtonSelectionAdapter(Text field, String[] fileExtension, String dialogTitle, Shell shell,
            boolean useFolder) {
        super(field, fileExtension, dialogTitle, shell, useFolder);
    }

    @Override
    public Optional<String> openFileDialog(String filePath) {
        FileDialog dialog = new FileDialog(this.getShell(), 4096);
        dialog.setFilterExtensions(getExtensions());
        dialog.setText(this.getDialogTitle());
        dialog.setFileName(filePath);
        return Optional.ofNullable(dialog.open());
    }

    @Override
    public Optional<String> openFolderDialog(String filePath) {
        DirectoryDialog dialog = new DirectoryDialog(this.getShell(), 4096);
        dialog.setFilterPath(filePath);
        dialog.setText(this.getDialogTitle());
        return Optional.ofNullable(dialog.open());
    }
}
