package org.junit.tools.ui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.preferences.ProjectSelectionDialog;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.FormatAllAction;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.tools.base.MethodRef;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.messages.Messages;

/**
 * Utils for the selection and manipulation of java-elements with the Eclipse
 * UI.
 * 
 * @author Robert Streng
 * 
 */
public class EclipseUIUtils {

    public static Logger logger = Logger.getLogger(EclipseUIUtils.class
	    .getName());

    /**
     * Open the editor with the file.
     * 
     * @param shell
     * @param file
     */
    public static void openInEditor(Shell shell, final IFile file) {
	shell.getDisplay().asyncExec(new Runnable() {

	    @Override
	    public void run() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getActivePage();
		try {
		    if (file != null) {
			IDE.openEditor(page, file, true);
		    }
		} catch (PartInitException e) {
		    logger.severe(ExceptionUtils.getStackTrace(e));
		}
	    }
	});
    }

    /**
     * Formats the compilation unit.
     * 
     * @param site
     * @param cus
     */
    public static void format(IWorkbenchPartSite site, ICompilationUnit... cus) {
	if (site == null) {
	    return;
	}

	FormatAllAction faa = new FormatAllAction(site);
	faa.runOnMultiple(cus);
    }

    /**
     * Save the file in the active editor and close it.
     * 
     * @param className
     */
    public static void saveAndCloseEditor(String className) {
	IWorkbenchPage page = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();

	IEditorPart activeEditor = getActiveEditor();

	if (activeEditor == null) {
	    return;
	}

	String title = activeEditor.getTitle();
	title = title.replace(".java", ""); //$NON-NLS-1$ //$NON-NLS-2$
	className = className.replace(".java", "");
	if (title.equals(className)) {
	    activeEditor.doSave(null);
	    page.closeEditor(activeEditor, false);
	}
    }

    public static IWorkbenchWindow getActiveWorkbenchWindow() {
	return PlatformUI.getWorkbench()
	.getActiveWorkbenchWindow();
    }
    
    /**
     * @return active editor
     */
    public static IEditorPart getActiveEditor() {
	IWorkbenchPage page = getActiveWorkbenchWindow().getActivePage();
	return page.getActiveEditor();
    }

    public static IEditorInput getEditorInput() {
	IEditorPart activeEditor = getActiveEditor();
	if (activeEditor != null) {
	    return activeEditor.getEditorInput();
	}
	
	return null;
    }
    
    /**
     * @param selection
     * @return first element of the selection
     */
    public static Object getFirstSelectedElement(ISelection selection) {
	if (selection instanceof TreeSelection) {
	    TreeSelection treeSelection = (TreeSelection) selection;
	    return treeSelection.getFirstElement();
	} else if (selection instanceof StructuredSelection) {
	    StructuredSelection structuredSelection = (StructuredSelection) selection;
	    return structuredSelection.getFirstElement();
	} else if (selection instanceof IFileEditorInput) {
	    IFileEditorInput editorInput = (FileEditorInput) selection;
	    return editorInput.getFile();
	} else if (selection instanceof TextSelection) {
	    return null;
	} else {
	    throw new RuntimeException(
		    Messages.GeneratorUtils_SelectionNotSupported);
	}
    }

    /**
     * Organize the imports of a compilation unit.
     * 
     */
    public static void organizeImports(IWorkbenchPartSite site,
	    ICompilationUnit cu) {
	if (cu == null) {
	    return;
	}

	if (site == null) {
	    return;
	}

	OrganizeImportsAction importAction = new OrganizeImportsAction(site);
	importAction.setSpecialSelectionProvider(new ISelectionProvider() {

	    private ISelection selection;

	    private final List<ISelectionChangedListener> listener = new ArrayList<ISelectionChangedListener>();

	    @Override
	    public void setSelection(ISelection selection) {
		this.selection = selection;
	    }

	    @Override
	    public ISelection getSelection() {
		return this.selection;
	    }

	    @Override
	    public void addSelectionChangedListener(
		    ISelectionChangedListener arg0) {
		listener.add(arg0);
	    }

	    @Override
	    public void removeSelectionChangedListener(
		    ISelectionChangedListener arg0) {
		listener.remove(arg0);
	    }

	});
	importAction.run(cu);
    }

    public static void selectMethodInEditor(final MethodRef methodRef) {
	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
		.getDisplay().asyncExec(new Runnable() {

		    @Override
		    public void run() {
			if (methodRef == null) {
			    return;
			}

			IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
			ITextEditor editor = (ITextEditor) page
				.getActiveEditor();
			ITypeRoot typeRoot = JavaUI
				.getEditorInputTypeRoot(editor.getEditorInput());
			ICompilationUnit icu = (ICompilationUnit) typeRoot
				.getAdapter(ICompilationUnit.class);
			IType type = icu.findPrimaryType();
			IMethod method = null;
			try {
			    method = GeneratorUtils.findMethod(
				    Arrays.asList(type.getMethods()), methodRef);
			} catch (JavaModelException e1) {
			    // not found
			    return;
			}

			IJavaElement element = method;
			JavaUI.revealInEditor(editor, element);
		    }
		});
    }

    public static void selectMethodInEditor(final IMethod method) {
	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
		.getDisplay().asyncExec(new Runnable() {

		    @Override
		    public void run() {
			IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
			ITextEditor editor = (ITextEditor) page
				.getActiveEditor();
			IJavaElement element = method;
			JavaUI.revealInEditor(editor, element);
		    }
		});
    }

    public static IJavaProject getJavaProjectFromDialog(Shell shell) {
	List<IJavaProject> javaProjects = JDTUtils.getJavaProjects();
	Set<IJavaProject> allProjects = new HashSet<IJavaProject>(javaProjects);

	@SuppressWarnings("restriction")
	ProjectSelectionDialog dialog = new ProjectSelectionDialog(shell,
		allProjects);

	if (dialog.open() == Window.OK) {
	    Object[] results = dialog.getResult();
	    if (results.length > 0) {
		for (Object result : results) {
		    if (result instanceof IJavaProject) {
			return ((IJavaProject) result);
		    }
		}
	    }
	}

	return null;
    }

    public static Shell getShell() {
	return getActiveWorkbenchWindow().getShell();
    }

}
