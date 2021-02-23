package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * this class is responsible for selection of files in the workspace
 * @author vladsolovyev
 */
public class WorkspaceButtonSelectionListener extends FileSelectionAdapter {

    public WorkspaceButtonSelectionListener(Text field, String[] fileExtension, String dialogTitle, Shell shell,
            boolean useFolder) {
        super(field, fileExtension, dialogTitle, shell, useFolder);
    }

    @Override
    public Optional<String> openFileDialog(String filePath) {
        Object[] initialSelection = getInitialSelection(filePath);
        IResource[] files = WorkspaceResourceDialog.openFileSelection(this.getShell(), null, this.getDialogTitle(),
                false, initialSelection, getFilters());
        return getSelectedFileName(files);
    }

    @Override
    public Optional<String> openFolderDialog(String filePath) {
        Object[] initialSelection = getInitialSelection(filePath);
        IResource[] dirs = WorkspaceResourceDialog.openFolderSelection(this.getShell(), null,
                this.getDialogTitle(), false, initialSelection, null);
        return getSelectedFileName(dirs);
    }

    /**
     * determines platform file name
     * @param resources - selected files/folders
     * @return platform file name
     */
    private Optional<String> getSelectedFileName(IResource[] resources) {
        if (resources.length != 0 && resources[0] != null) {
            String portableString = resources[0].getFullPath().toPortableString();
            return Optional.of(String.format("platform:/resource%s", portableString));
        } else {
            return Optional.empty();
        }
    }

    /**
     * creates initial selection which specifies a location where a workspace resource dialog is opened
     * @param filePath - location of initial selection
     * @return - initial selection for a workspace resource dialog
     */
    private Object[] getInitialSelection(String filePath) {
        Object[] initialSelection = null;
        URI fileURI = URI.createURI(filePath);
        if (fileURI.isPlatformResource()) {
            IResource fileAsResource = EcorePlugin.getWorkspaceRoot().findMember(fileURI.toPlatformString(true));
            if (fileAsResource != null) {
                initialSelection = new Object[] {fileAsResource};
            }
        }
        return initialSelection;
    }

    /**
     * creates a list of ViewerFilter, specifying allowed extensions
     * @return a list of ViewerFilter
     */
    private List<ViewerFilter> getFilters() {
        List<ViewerFilter> filters = new ArrayList<>();
        if (getExtensions() != null) {
            FilePatternFilter filter = new FilePatternFilter();
            filter.setPatterns(getExtensions());
            filters.add(filter);
        }
        return filters;
    }
}