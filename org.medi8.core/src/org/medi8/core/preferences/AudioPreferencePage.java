package org.medi8.core.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.medi8.core.CorePlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */


public class AudioPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	public static final String P_BUFFER = "bufferPreference";

	public AudioPreferencePage() {
		super(GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
		setDescription("Options for real-time audio output:");
		initializeDefaults();
	}
/**
 * Sets the default values of the preferences.
 */
	private void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(P_BUFFER, 2048);
	}
	
/**
 * Creates the field editors. Field editors are abstractions of
 * the common GUI blocks needed to manipulate various types
 * of preferences. Each field editor knows how to save and
 * restore itself.
 */

	public void createFieldEditors() {
	  IntegerFieldEditor ife =
	    new IntegerFieldEditor (P_BUFFER, "Output &buffer size:", getFieldEditorParent());
	  ife.setValidRange (1024, 8192);
	  ife.setErrorMessage ("Buffer size must be between 1024 and 8192");
		addField(ife);
	}
	
	public void init(IWorkbench workbench) {
	}
}