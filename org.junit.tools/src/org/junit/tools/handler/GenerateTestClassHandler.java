package org.junit.tools.handler;

import java.lang.annotation.Inherited;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.base.MainController;
import org.junit.tools.messages.Messages;
import org.junit.tools.ui.utils.EclipseUIUtils;

/**
 * Command for the generation of a test-class. The standard shortcut is
 * ctrl-shift-<.
 * 
 * @author Robert Streng
 * 
 */
public class GenerateTestClassHandler extends JUTHandler {

    /**
     * {@link Inherited}
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

	MainController ctrl = new MainController();
	IWorkbenchWindow activeWorkbenchWindow;
	activeWorkbenchWindow = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow();

	ISelection selection = activeWorkbenchWindow.getSelectionService()
		.getSelection();
	boolean result = false;

	try {
	    if (selection instanceof IStructuredSelection) {
		result = ctrl.generateTestclass(activeWorkbenchWindow,
			(IStructuredSelection) selection);

	    } else {
		IEditorInput editorInput = EclipseUIUtils.getEditorInput();

		if (editorInput instanceof IFileEditorInput) {
		    result = ctrl.generateTestclass(activeWorkbenchWindow,
			    ((IFileEditorInput) editorInput));
		}
	    }
	} catch (JUTWarning e) {
	    handleWarning(e);
	} catch (Exception e) {
	    handleError(e);
	}

	if (result) {
	    String information = Messages.General_information;
	    MessageDialog.openInformation(activeWorkbenchWindow.getShell(),
		    information, Messages.General_info_generation_successful);
	}

	return null;
    }

}
