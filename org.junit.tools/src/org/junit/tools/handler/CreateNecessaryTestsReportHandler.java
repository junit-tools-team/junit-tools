package org.junit.tools.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.base.MainController;
import org.junit.tools.messages.Messages;
import org.junit.tools.ui.utils.EclipseUIUtils;

public class CreateNecessaryTestsReportHandler extends JUTHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	try {
	    IWorkbenchWindow activeWorkbenchWindow = EclipseUIUtils
		    .getActiveWorkbenchWindow();
	    ISelection selection = activeWorkbenchWindow.getSelectionService()
		    .getSelection();

	    MainController mc = new MainController();
	    mc.createReport(activeWorkbenchWindow, selection);
	    MessageDialog.openInformation(EclipseUIUtils.getShell(),
		    information, Messages.General_info_process_successful);
	} catch (JUTWarning e) {
	    handleWarning(e);
	} catch (Exception e) {
	    handleError(e);
	}

	return null;
    }

}
