package org.junit.tools.handler;

import java.lang.annotation.Inherited;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.base.MainController;
import org.junit.tools.ui.utils.EclipseUIUtils;

/**
 * Command for the switch-function between test-class and test-subject. The
 * standard shortcut is ctrl-shift-<.
 * 
 * @author JUnit-Tools-Team
 * 
 */
public class SwitchClassHandler extends JUTHandler {

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

		try {

			if (selection instanceof IStructuredSelection) {
				ctrl.switchClass(activeWorkbenchWindow,
						(IStructuredSelection) selection);

			} else {
				IEditorInput editorInput = EclipseUIUtils.getEditorInput();

				if (editorInput instanceof IFileEditorInput) {
					ctrl.switchClass(activeWorkbenchWindow,
							(IFileEditorInput) editorInput);
				}
			}
		} catch (JUTWarning e) {
			handleWarning(e);
		} catch (Exception e) {
			handleError(e);
		}

		return null;
	}

}
