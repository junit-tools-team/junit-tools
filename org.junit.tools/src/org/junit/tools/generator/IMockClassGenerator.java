package org.junit.tools.generator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.tools.base.JUTException;
import org.junit.tools.base.JUTWarning;

/**
 * Interface for the mock-class-generators.
 * 
 * @author JUnit-Tools-Team
 * 
 */
public interface IMockClassGenerator {
    public ICompilationUnit generate(IWorkbenchWindow activeWorkbenchWindow,
	    IFileEditorInput fileEditorInput) throws JUTWarning, CoreException, JUTException;

    public ICompilationUnit generate(IWorkbenchWindow activeWorkbenchWindow,
	    IStructuredSelection selection) throws JUTWarning, CoreException, JUTException;

    public void cleanMock(ICompilationUnit cu) throws CoreException, JUTException, JUTWarning;
}
