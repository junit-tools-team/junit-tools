package org.junit.tools.refactoring;

import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.junit.tools.generator.model.JUTElements;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.preferences.JUTPreferences;

public class RenameTestElements extends RenameParticipant {

    private IType oldBaseType = null;

    private IMethod oldBaseMethod = null;

    private String oldBaseMethodSignature = null;

    private IPackageFragment oldBasePackage = null;

    /**
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#
     *      initialize(java.lang.Object)
     */
    @Override
    protected boolean initialize(Object element) {
	if (element instanceof IMethod) {
	    this.oldBaseMethod = (IMethod) element;
	    try {
		this.oldBaseMethodSignature = ((IMethod) element)
			.getSignature();
	    } catch (JavaModelException e) {
		this.oldBaseMethodSignature = null;
	    }
	} else if (element instanceof IType) {
	    this.oldBaseType = (IType) element;
	} else if (element instanceof IPackageFragment) {
	    this.oldBasePackage = (IPackageFragment) element;
	}

	return true;
    }

    /**
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#
     *      checkConditions(org.eclipse.core.runtime.IProgressMonitor,
     *      org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
     */
    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm,
	    CheckConditionsContext context) {
	return new RefactoringStatus();
    }

    /**
     * Creates the change for the renaming
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#
     *      createChange(org.eclipse.core.runtime.IProgressMonitor)
     */

    @Override
    public Change createChange(IProgressMonitor pm1) throws CoreException {

	CompositeChange renameMethodNameChange = new CompositeChange(
		"org.junit.tools refactor dependent test-methods"); //$NON-NLS-1$

	PerformChangeOperation change = new PerformChangeOperation(

	new Change() {

	    @Override
	    public Change perform(IProgressMonitor pm2) throws CoreException {

		JUTElements utmElementsOld = null;
		ICompilationUnit testCu = null;

		try {
		    if (oldBaseType != null) {
			utmElementsOld = JUTElements.initJUTElements(
				oldBaseType.getJavaProject(),
				oldBaseType.getCompilationUnit());
			if (!utmElementsOld.getProjects()
				.isBaseProjectSelected()
				|| !utmElementsOld.getClassesAndPackages()
					.getTestClass().exists()) {
			    return null;
			}

			testCu = utmElementsOld.getClassesAndPackages()
				.getTestClass();
			renameTestClass(testCu, getArguments().getNewName(),
				pm2);
		    } else if (oldBaseMethod != null) {
			utmElementsOld = JUTElements.initJUTElements(
				oldBaseMethod.getJavaProject(),
				oldBaseMethod.getCompilationUnit());
			if (!utmElementsOld.getProjects()
				.isBaseProjectSelected()
				|| !utmElementsOld.getClassesAndPackages()
					.getTestClass().exists()) {
			    return null;
			}

			testCu = utmElementsOld.getClassesAndPackages()
				.getTestClass();
			renameTestMethod(testCu, getArguments().getNewName(),
				pm2);

			// format source
			IPackageFragment testPackage = utmElementsOld
				.getClassesAndPackages().getTestPackage();
			testPackage.createCompilationUnit(
				testCu.getElementName(), testCu.getSource(),
				true, pm2);
		    } else if (oldBasePackage != null) {
			utmElementsOld = JUTElements.initJUTElements(
				oldBasePackage.getJavaProject(), oldBasePackage);

			if (utmElementsOld.getProjects()
				.isBaseProjectSelected()) {
			    IPackageFragment newBasePackage = JDTUtils
				    .getPackage(
					    oldBasePackage.getJavaProject(),
					    utmElementsOld
						.getClassesAndPackages().getTestFolder(),
					    getArguments().getNewName(), false);
			    JUTElements utmElementsNew = JUTElements
				    .initJUTElements(
					    oldBasePackage.getJavaProject(),
					    newBasePackage);

			    IPackageFragment testPackage = utmElementsOld
				    .getClassesAndPackages().getTestPackage();
			    if (testPackage != null && testPackage.exists()) {
				testPackage.rename(utmElementsNew
					.getClassesAndPackages()
					.getTestPackageName(), true, pm2);
			    }
			}
		    } else {
			return null;
		    }

		} catch (Exception e) {
		    // only log - refactoring not possible
		    Logger.getLogger(this.getName()).warning(
			    "Refactoring not possible: " + e.getMessage());
		}

		if (testCu == null || !testCu.exists()) {
		    return null;
		}

		return null;
	    }

	    @Override
	    public RefactoringStatus isValid(IProgressMonitor pm)
		    throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	    }

	    @Override
	    public void initializeValidationData(IProgressMonitor pm) {
		// nothing
	    }

	    @Override
	    public String getName() {
		String ar = getArguments().getNewName();
		return ar;
	    }

	    @Override
	    public Object getModifiedElement() {
		return null;
	    }
	});

	renameMethodNameChange.add(change.getChange());

	return renameMethodNameChange;
    }

    @Override
    public String getName() {
	RenameArguments args = this.getArguments();
	String newName = args.getNewName();
	return newName;
    }

    /**
     * Renames the method and method body
     * 
     * @param testCu
     * @param newName
     */
    protected void renameTestMethod(ICompilationUnit testCu, String newName,
	    IProgressMonitor pm) {
	try {
	    if (testCu != null) {
		testCu.makeConsistent(null);
		if (testCu.hasUnsavedChanges()) {
		    testCu.commitWorkingCopy(true, null);
		}

		IMethod methodToChange = null;

		String oldBaseMethodName = oldBaseMethod.getElementName();
		String oldTestMethodName = GeneratorUtils
			.createTestMethodName(oldBaseMethodName);

		IType testType = testCu.findPrimaryType();
		String newTestMethodName;

		// find test-method by name or method-reference
		for (IMethod testMethod : testType.getMethods()) {
		    if (!testMethod.isConstructor()) {

			// by name
			if (testMethod.getElementName().equals(
				oldTestMethodName)) {
			    methodToChange = testMethod;
			}

			// by method-reference-annotation
			if (GeneratorUtils.checkMethodReference(
				oldBaseMethod.getElementName(),
				oldBaseMethodSignature, testMethod)) {
			    methodToChange = testMethod;
			    break;
			}
		    }
		}

		if (methodToChange != null) {
		    newTestMethodName = GeneratorUtils
			    .createTestMethodName(newName);

		    methodToChange.rename(newTestMethodName, true, pm);

		    IMethod newTestMethod = testType.getMethod(
			    newTestMethodName,
			    methodToChange.getParameterTypes());
		    String newSource = refactorTestMethodBody(newTestMethod,
			    oldBaseMethod.getElementName(), newName);

		    if (newSource != null) {
			newTestMethod.delete(true, null);
			testType.createMethod(newSource, null, true, pm);
		    }
		}

	    }

	} catch (Exception e) {
	    Logger.getLogger(this.getName()).warning(e.getMessage());
	}

    }

    /**
     * Refactor the method body
     * 
     * @param method
     * @param oldName
     * @param newName
     * @return the refactored method body
     * @throws JavaModelException
     */
    protected String refactorTestMethodBody(IMethod method, String oldName,
	    String newName) throws JavaModelException {
	String source = method.getSource();
	source = source
		.replaceAll("\"" + oldName + "\"", "\"" + newName + "\"");

	return source;
    }

    /**
     * Renames the test-class
     * 
     * @param testCu
     * @param newName
     */
    protected void renameTestClass(ICompilationUnit testCu, String newName,
	    IProgressMonitor pm) {
	try {
	    if (testCu != null) {
		testCu.makeConsistent(null);
		if (testCu.hasUnsavedChanges()) {
		    testCu.commitWorkingCopy(true, null);
		}

		String newtestClassName = GeneratorUtils
			.createTestClassName(newName);
		String oldTestClassName = testCu.getElementName().replace(
			".java", "");

		// rename test-class
		testCu.rename(newtestClassName + ".java", false, pm);

		// rename test-class in test-suite
		IJavaElement parent = testCu.getParent();
		if (parent instanceof IPackageFragment) {
		    IPackageFragment testPackage = (IPackageFragment) parent;
		    ICompilationUnit testSuite = testPackage
			    .getCompilationUnit("TestSuite.java");
		    if (testSuite.exists()) {
			String source = testSuite.getSource();
			source = source.replace(oldTestClassName + ".class",
				newtestClassName + ".class");
			testSuite.getBuffer().setContents(source);
			testSuite.save(pm, true);
			testSuite.commitWorkingCopy(true, pm);
		    }

		}

		// for (IType testType : testCu.getTypes()) {
		// if (testType.getElementName().equals(oldTestClassName)) {
		// testType.rename(newtestClassName, true, null);
		// }
		// }
	    }
	} catch (Exception e) {
	    Logger.getLogger(this.getName()).warning(e.getMessage());
	}

    }
}
