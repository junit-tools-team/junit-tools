package org.junit.tools.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.tools.Activator;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.messages.Messages;
import org.junit.tools.ui.utils.EclipseUIUtils;

/**
 * General JUT handler
 * 
 * @author JUnit-Tools-Team
 * 
 */
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
	Status status = new Status(Status.ERROR, pluginId, e.getMessage(), e);
	log.log(status);

	// open error dialog
	try {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);

	    // convert stack trace lines to status objects
	    final String trace = sw.toString();
	    List<Status> stackStatus = new ArrayList<Status>();
	    for (String line : trace.split(System.getProperty("line.separator"))) {
		stackStatus.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, line));
	    }

	    MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, stackStatus.toArray(new Status[] {}),
		    e.getLocalizedMessage(), e);

	    // open error dialog
	    ErrorDialog.openError(shell, error, errorMsg, ms);
	} catch (Exception ex2) {
	    ErrorDialog.openError(shell, error, errorMsg, status);
	}

    }

    protected void handleWarning(JUTWarning e) {
	Shell shell = EclipseUIUtils.getShell();

	String warningMsg = e.getMessage();

	// open error dialog
	MessageDialog.openWarning(shell, warning, warningMsg);
    }

}
