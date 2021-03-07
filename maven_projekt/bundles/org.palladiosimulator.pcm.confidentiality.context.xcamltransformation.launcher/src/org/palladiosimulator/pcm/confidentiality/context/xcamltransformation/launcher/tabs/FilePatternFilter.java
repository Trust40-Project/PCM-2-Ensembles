package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.launcher.tabs;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;

/**
 * file filter for specified extensions
 * @author vladsolovyev
 * @version 1.0.0
 */
public class FilePatternFilter extends ResourcePatternFilter {

    /**
     * selects files which match specified patterns
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IResource) {
            IResource resource = (IResource) element;
            if (resource.getType() != 8 && resource.getType() != 4 && resource.getType() != 2) {
                return !super.select(viewer, parentElement, element);
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
