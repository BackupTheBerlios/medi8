package org.medi8.core;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import org.medi8.core.file.AudioServer;
import org.osgi.framework.BundleContext;

import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class CorePlugin extends AbstractUIPlugin {
	//The shared instance.
	private static CorePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public CorePlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle("org.medi8.core.medi8"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static CorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= CorePlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// Start the audio server.
		AudioServer.start ();
	}
	
	public void stop(BundleContext context) throws Exception {
		try {
      // Shutdown the audio server. 
		  AudioServer.stop ();
		} 
		finally {
			super.stop(context);
		}
	}

}
