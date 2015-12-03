package org.junit.tools.handler;

import java.lang.annotation.Inherited;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.tools.Activator;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.generator.IGeneratorConstants;
import org.junit.tools.messages.Messages;
import org.junit.tools.ui.utils.EclipseUIUtils;

public abstract class JUTHandler implements IHandler {

    protected static ILog log = Activator.getLogger();

    protected static String pluginId = Activator.PLUGIN_ID;

    protected String information = Messages.General_information;
    protected String warning = Messages.General_warning;
    protected String error = Messages.General_error;
    protected String errorMsg = Messages.General_error_processing;

    /**
     * {@link Inherited}
     */
    @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
	// nothing
    }

    /**
     * {@link Inherited}
     */
    @Override
    public void dispose() {
	// nothing
    }

    /**
     * {@link Inherited}
     */
    @Override
    public boolean isEnabled() {
	return true;
    }

    /**
     * {@link Inherited}
     */
    @Override
    public boolean isHandled() {
	return true;
    }

    /**
     * {@link Inherited}
     */
    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
	// nothing
    }

    protected void handleError(Exception e) {
	Shell shell = EclipseUIUtils.getShell();

	// log to error log
	IStatus status = new Status(Status.ERROR, pluginId, e.getMessage(), e);
	log.log(status);

	// open error dialog
	MessageDialog.openError(shell, error, errorMsg
		+ IGeneratorConstants.RETURN + e.getMessage());
    }

    protected void handleWarning(JUTWarning e) {
	Shell shell = EclipseUIUtils.getShell();

	String warningMsg = e.getMessage();

	// open error dialog
	MessageDialog.openWarning(shell, warning, warningMsg);
    }

}
