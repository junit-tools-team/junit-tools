package org.junit.tools.handler;

import java.util.Vector;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.tools.Activator;
import org.junit.tools.base.JUTException;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.generator.IMockClassGenerator;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.messages.Messages;
import org.junit.tools.ui.utils.EclipseUIUtils;

/**
 * Clean mocks handler
 * 
 * @author JUnit-Tools-Team
 * 
 */
public class CleanMocksHandler extends JUTHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			cleanMocks();

			MessageDialog.openInformation(EclipseUIUtils.getShell(),
					information, Messages.General_info_generation_successful);
		} catch (JUTWarning e) {
			handleWarning(e);
		} catch (Exception e) {
			handleError(e);
		}

		return null;
	}

	private void cleanMocks() throws JUTWarning, CoreException, JUTException {

		IWorkbenchWindow activeWorkbenchWindow = EclipseUIUtils
				.getActiveWorkbenchWindow();

		ISelection selection = activeWorkbenchWindow.getSelectionService()
				.getSelection();

		IMockClassGenerator mockClassGenerator = Activator.getDefault()
				.getExtensionHandler().getMockClassGenerator();

		Vector<IJavaElement> elements = JDTUtils.getCompilationUnits(selection);

		for (IJavaElement element : elements) {
			if (element instanceof ICompilationUnit) {
				if (element.getElementName().indexOf("TestSuite") == -1
				&&	element.getElementName().endsWith("Mock.java")
				&&  JDTUtils.getPackage(element).getElementName().endsWith(".mock")) 
					mockClassGenerator.cleanMock((ICompilationUnit) element);
			}
		}

	}
}
