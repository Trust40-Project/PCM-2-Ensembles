package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @author vladsolovyev
 * @version 1.0
 */
public class Activator extends AbstractUIPlugin {

	private static Activator plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * @return the shared instance of the plugin
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
