package org.junit.tools;

import org.eclipse.core.runtime.ILog;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.junit.tools.base.ExtensionPointHandler;
import org.osgi.framework.BundleContext;

/**
 * The activator for the junit-tools-plug-in
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.junit.tools"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    private ExtensionPointHandler extensionHandler = null;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;

	initExtensionHandler();
    }

    private void initExtensionHandler() {
	this.extensionHandler = new ExtensionPointHandler();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    public void stop(BundleContext context) throws Exception {
	plugin = null;
	super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
	return plugin;
    }

    public static ILog getLogger() {
	return plugin.getLog();
    }

    public ExtensionPointHandler getExtensionHandler() {
	return this.extensionHandler;
    }
}
