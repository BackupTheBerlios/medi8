package org.medi8.core.preferences;

import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.medi8.core.CorePlugin;
import org.medi8.core.file.AudioServer;
import org.medi8.internal.core.ui.ComboFieldEditor;
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
	public static final String P_BUFFER = "/medi8/audio/bufferSize";
	public static final String P_LOUT = "/medi8/audio/port/left";
	public static final String P_ROUT = "/medi8/audio/port/right";
	
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
		
		Properties prop = new Properties ();
		try {
			Process p = Runtime.getRuntime().exec(new String[] {
					System.getProperty ("medi8.workspace") 
						+ "/medi8-tools/audio/m8ainfo/m8ainfo" });

			prop.load(p.getInputStream());
			p.waitFor();
		} catch (Exception _) {
		  // Do nothing.
		}
	
		String choices[][] = new String[prop.size()][2];
		Enumeration e = prop.propertyNames();
		int i = 0;
		while (e.hasMoreElements ())
		  {
		  	String value = prop.getProperty ((String) e.nextElement());
		  	choices[i][0] = choices[i][1] = value;
		  	i++;
		  }
		  	           
		ComboFieldEditor cfe =
		  new ComboFieldEditor (P_LOUT, "Output jack, &left:", 
		                        choices, getFieldEditorParent());
		addField (cfe);
		
		cfe =
		  new ComboFieldEditor (P_ROUT, "Output jack, &right:", 
		                        choices, getFieldEditorParent());
		addField (cfe);
	}
	
	public void init(IWorkbench workbench) {
	}
}