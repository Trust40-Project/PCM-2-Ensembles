package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.Activator;

/**
 * Represents the preferences page and handles the plugin's preferences, i.e. the paths of the in- and output models.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class PreferencesHandler extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    private static final IPreferenceStore PREF_STORE = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
    
    private static final String IN = Activator.PLUGIN_ID + "/inPath";
    private static final String OUT = Activator.PLUGIN_ID + "/outPath";
    
    private static final int WIDTH = 100; 
    
    /**
     * Creates a new PreferencesHandler.
     */
    public PreferencesHandler() {
        
    }
    
    /**
     * Gets the path to the input model's dataprocessing file set in the plugin's preferences.
     * 
     * @return the path to the input model's dataprocessing file set in the plugin's preferences.
     */
    public static String getDataPath() {
        return PREF_STORE.getString(IN) + ".dataprocessing";
    }
    
    /**
     * Gets the path to the output XACML policy set file.
     * 
     * @return the path to the output XACML policy set file.
     */
    public static String getOutputPolicySetPath() {
        return PREF_STORE.getString(OUT);
    }

    @Override
    protected void createFieldEditors() {
        final var parent = getFieldEditorParent();
        
        final var inEditor = new StringFieldEditor(IN, 
                "input path (path of input model without filename ending)", 
                WIDTH, parent);
        final var outEditor = new StringFieldEditor(OUT, 
                "output path (path of output file including filename ending)", 
                WIDTH, parent);
        addField(inEditor); 
        addField(outEditor);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(PREF_STORE);
        setDescription("Preference page for the plugin " + Activator.PLUGIN_ID);
    }
}
