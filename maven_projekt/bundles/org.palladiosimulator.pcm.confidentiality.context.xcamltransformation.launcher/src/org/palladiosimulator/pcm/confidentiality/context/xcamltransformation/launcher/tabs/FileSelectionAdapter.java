package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher.tabs;

import java.util.Optional;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * this class is responsible for a file selection
 * @author vladsolovyev
 * @version 1.0.0
 */
abstract class FileSelectionAdapter extends SelectionAdapter {
    private Text field;
    private String[] extensions;
    private Shell shell;
    private String dialogTitle;
    private boolean useFolder = false;

    public FileSelectionAdapter(Text field, String[] fileExtension, String dialogTitle, Shell shell,
            boolean useFolder) {
        this.field = field;
        this.extensions = fileExtension;
        this.shell = shell;
        this.dialogTitle = dialogTitle;
        this.useFolder = useFolder;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (this.useFolder) {
            openFolderDialog(this.field.getText()).ifPresent(selectedFile -> this.field.setText(selectedFile));
        } else {
            openFileDialog(this.field.getText()).ifPresent(selectedFile -> this.field.setText(selectedFile));
        }
    }

    /**
     * @return a widget shell
     */
    protected Shell getShell() {
        return this.shell;
    }

    /**
     * @return a dialog title
     */
    protected String getDialogTitle() {
        return this.dialogTitle;
    }

    /**
     * @return extensions which are used for filtering
     */
    protected String[] getExtensions() {
        return this.extensions;
    }

    /**
     * opens a file dialog which allows to select one file with a specified extension
     * @param filePath - a file path which is opened at initialisation
     * @return a selected file
     */
    protected abstract Optional<String> openFileDialog(String filePath);

    /**
     * opens a folder dialog which allows to select one folder
     * @param filePath - a folder path which is opened at initialisation
     * @return a selected folder
     */
    protected abstract Optional<String> openFolderDialog(String filePath);
}
