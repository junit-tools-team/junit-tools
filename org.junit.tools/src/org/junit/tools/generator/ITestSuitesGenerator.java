package org.junit.tools.generator;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.junit.tools.generator.model.JUTElements;

/**
 * Interface for the test-suites-generators.
 * 
 * @author JUnit-Tools-Team
 */
public interface ITestSuitesGenerator {

    /**
     * Generates the tests suites depend on the utm-elements.
     * 
     * @param utmElements
     * @return true if the creations were successful
     * @throws CoreException
     */
    boolean generateTestSuites(JUTElements utmElements) throws CoreException;

    /**
     * Generates the test-suites for a hole test-project.
     * 
     * @param testProject
     * @return true if the creations were successful
     * @throws CoreException
     */
    boolean generateTestSuites(IJavaProject testProject) throws CoreException;

    /**
     * Deletes the deleted test-suite-element in the corresponding test-suite.
     * If the test-suite has no elements, then the corresponding test-suite will
     * also be deleted. In this case the connected child-test-suites will be
     * connected with the next available parent-test-suite.
     * 
     * @param packageOfDeletedClass
     * 
     * @param deletedClass
     * @return true if processing was successfull
     * @throws CoreException
     */
    boolean deleteTestSuiteElement(IPackageFragment packageOfDeletedClass,
	    ICompilationUnit deletedClass) throws CoreException;

    ArrayList<ICompilationUnit> getGeneratedTestSuites();
}
