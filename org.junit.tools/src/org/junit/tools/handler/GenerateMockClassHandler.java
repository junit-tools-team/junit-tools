package org.junit.tools.handler;

import java.lang.annotation.Inherited;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.tools.Activator;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.generator.IMockClassGenerator;
import org.junit.tools.messages.Messages;
import org.junit.tools.ui.utils.EclipseUIUtils;

/**
 * Command implementation for the mock-class-generator
 * 
 * @author Robert Streng
 * 
 */
public class GenerateMockClassHandler extends JUTHandler {

    /**
     * {@link Inherited}
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	IWorkbenchWindow activeWorkbenchWindow;
	activeWorkbenchWindow = EclipseUIUtils.getActiveWorkbenchWindow();

	try {

	    ISelection selection = activeWorkbenchWindow.getSelectionService()
		    .getSelection();

	    ICompilationUnit result = null;
	    IMockClassGenerator mockClassGenerator = Activator.getDefault()
		    .getExtensionHandler().getMockClassGenerator();

	    if (selection instanceof IStructuredSelection) {
		result = mockClassGenerator.generate(activeWorkbenchWindow,
			(IStructuredSelection) selection);
	    } else {
		IEditorInput editorInput = EclipseUIUtils.getEditorInput();

		if (editorInput instanceof IFileEditorInput) {
		    result = mockClassGenerator.generate(activeWorkbenchWindow,
			    (IFileEditorInput) editorInput);
		}
	    }

	    if (result != null) {
		// make source beautiful
		IWorkbenchPartSite site = activeWorkbenchWindow.getActivePage()
			.getActivePart().getSite();
		EclipseUIUtils.organizeImports(site, result);
		EclipseUIUtils.format(site, result);

		String information = Messages.General_information;
		MessageDialog.openInformation(activeWorkbenchWindow.getShell(),
			information,
			Messages.General_info_generation_successful);
	    }
	} catch (JUTWarning e) {
	    handleWarning(e);
	} catch (Exception e) {
	    handleError(e);
	    return null;
	}

	return null;
    }

}
