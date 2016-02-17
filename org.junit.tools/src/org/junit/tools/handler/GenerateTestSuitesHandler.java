package org.junit.tools.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.base.MainController;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.messages.Messages;
import org.junit.tools.ui.utils.EclipseUIUtils;

public class GenerateTestSuitesHandler extends JUTHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	try {

	    IWorkbenchWindow activeWorkbenchWindow;
	    activeWorkbenchWindow = PlatformUI.getWorkbench()
		    .getActiveWorkbenchWindow();

	    ISelection selection = activeWorkbenchWindow.getSelectionService()
		    .getSelection();

	    IJavaProject testProject = JDTUtils.getProject(selection);

	    MainController mc = new MainController();
	    boolean result = mc.generateTestSuites(activeWorkbenchWindow, testProject);

	    if (result) {
		MessageDialog.openInformation(EclipseUIUtils.getShell(),
			Messages.General_information,
			Messages.General_info_generation_successful);
	    }
	} catch (JUTWarning warning) {
	    handleWarning(warning);
	} catch (Exception e) {
	    handleError(e);
	}

	return null;
    }

}
