package org.junit.tools.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;
import org.junit.tools.Activator;
import org.junit.tools.generator.ITestSuitesGenerator;
import org.junit.tools.generator.model.JUTElements;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;

public class DeleteTestElements extends DeleteParticipant {

    private ICompilationUnit deletedCu = null;

    private IPackageFragment deletedPackage = null;

    private IJavaProject deletedProject;

    /**
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#
     *      initialize(java.lang.Object)
     */
    @Override
    protected boolean initialize(Object element) {
	if (element instanceof ICompilationUnit) {
	    this.deletedCu = (ICompilationUnit) element;
	} else if (element instanceof IPackageFragment) {
	    this.deletedPackage = (IPackageFragment) element;
	} else if (element instanceof IJavaProject) {
	    this.deletedProject = (IJavaProject) element;
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

	CompositeChange deleteTestElementsChange = new CompositeChange(
		"org.junit.tools delete dependent test-elements"); //$NON-NLS-1$

	PerformChangeOperation change = new PerformChangeOperation(

	new Change() {

	    @Override
	    public Change perform(IProgressMonitor pm2) throws CoreException {

		JUTElements utmElements = null;

		try {
		    if (deletedCu != null) {
			try {
			    utmElements = JUTElements.initJUTElements(
				    deletedCu.getJavaProject(), deletedCu);
			} catch (Exception ex) {
			    return null;
			}
			ICompilationUnit testClass = utmElements
				.getClassesAndPackages().getTestClass();

			if (utmElements.getProjects().isBaseProjectSelected()
				&& utmElements.getProjects()
					.isTestProjectFound()) {
			    if (testClass != null && testClass.exists()) {
				// delete the test-class
				testClass.delete(true, pm2);
			    } else {
				// nothing to do
				return null;
			    }
			}
			
			if (!utmElements.getProjects().isBaseProjectSelected() && utmElements.getProjects()
					.isTestProjectFound()) {
				if (!GeneratorUtils.isTestClass(deletedCu.findPrimaryType())) {
					return null;
				}
			}

			// actualize the test-suites
			for (ITestSuitesGenerator testSuiteGenerator : Activator
				.getDefault().getExtensionHandler()
				.getTestSuitesGenerators()) {
			    testSuiteGenerator.deleteTestSuiteElement(
				    utmElements.getClassesAndPackages()
					    .getTestPackage(), testClass);
			}

		    } else if (deletedPackage != null) {
			utmElements = JUTElements.initJUTElements(
				deletedPackage.getJavaProject(), deletedPackage);
			if (utmElements.getProjects().isBaseProjectSelected()
				&& utmElements.getProjects()
					.isTestProjectFound()) {
			    IPackageFragment testPackage = utmElements
				    .getClassesAndPackages().getTestPackage();
			    if (testPackage.hasSubpackages()) {
				for (ICompilationUnit cu : testPackage
					.getCompilationUnits()) {
				    cu.delete(true, pm2);
				}
			    } else {
				JDTUtils.deletePackagesWithParents(testPackage);
			    }
			}
		    } else if (deletedProject != null) {
			utmElements = JUTElements
				.initJUTElements(deletedProject);

			if (utmElements.getProjects().isBaseProjectSelected()
				&& utmElements.getProjects()
					.isTestProjectFound()) {
			    utmElements.getProjects().getTestProject().close();
			}
		    }

		} catch (Exception e) {
		    // nothing
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
		String ar = getArguments().getClass().toString();
		return ar;
	    }

	    @Override
	    public Object getModifiedElement() {
		return null;
	    }
	});

	deleteTestElementsChange.add(change.getChange());

	return deleteTestElementsChange;
    }

    @Override
    public String getName() {
	return getArguments().getClass().toString();
    }

}
