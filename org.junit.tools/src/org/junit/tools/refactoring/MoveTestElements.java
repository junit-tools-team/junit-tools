package org.junit.tools.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.junit.tools.generator.model.JUTElements;
import org.junit.tools.generator.model.JUTElements.JUTClassesAndPackages;
import org.junit.tools.generator.utils.JDTUtils;

public class MoveTestElements extends MoveParticipant {

    private ICompilationUnit movedCu;

    @Override
    protected boolean initialize(Object element) {
	if (element instanceof ICompilationUnit) {
	    movedCu = (ICompilationUnit) element;
	}

	return true;
    }

    @Override
    public String getName() {
	return "move test-elements";
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm,
	    CheckConditionsContext context) throws OperationCanceledException {
	return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException,
	    OperationCanceledException {
	return new Change() {

	    @Override
	    public Change perform(IProgressMonitor pm2) throws CoreException {

		try {
		    if (movedCu == null) {
			return null;
		    }

		    JUTElements utmElements = JUTElements.initJUTElements(
			    movedCu.getJavaProject(), movedCu);

		    IPackageFragment newPackage = (IPackageFragment) getArguments()
			    .getDestination();

		    JUTClassesAndPackages classesAndPackages = utmElements
			    .getClassesAndPackages();

		    ICompilationUnit testCu = classesAndPackages.getTestClass();

		    if (testCu.exists()) {
			IJavaProject testProject = utmElements.getProjects()
				.getTestProject();
			IPackageFragment newTestPackage = JDTUtils.getPackage(
				testProject, newPackage.getElementName(), true);

			testCu.move(newTestPackage, null, null, true, pm2);

			// TODO organise imports, testsuites aktualisieren,
			// package evtl. l�schen
		    }

		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		return null;
	    }

	    @Override
	    public RefactoringStatus isValid(IProgressMonitor pm2)
		    throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	    }

	    @Override
	    public void initializeValidationData(IProgressMonitor pm2) {
		// nothing
	    }

	    @Override
	    public String getName() {
		return "move test-elements change";
	    }

	    @Override
	    public Object getModifiedElement() {
		return null;
	    }
	};
    }

}
