package org.junit.tools.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.tools.generator.model.JUTElements;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.preferences.JUTPreferences;
import org.junit.tools.ui.utils.EclipseUIUtils;

/**
 * The default test-suites-generator
 * 
 * @author Robert Streng
 * 
 *         TODO generate with AST
 */
public class TestSuitesGenerator implements ITestSuitesGenerator {

    private static final String TESTSUITE_PREFIX = "TestSuite";
    private ArrayList<ICompilationUnit> generatedTestSuites = new ArrayList<ICompilationUnit>();

    /**
     * Generates the test-suites after a test-class generation depend on the
     * utmElements (bottom-up)
     */
    @Override
    public boolean generateTestSuites(JUTElements utmElements)
	    throws CoreException {
	init();

	IJavaProject testProject = utmElements.getProjects().getTestProject();
	IPackageFragment testPackage = utmElements.getClassesAndPackages()
		.getTestPackage(true);
	String testClassName = utmElements.getClassesAndPackages()
		.getTestClassName();

	HashSet<String> childTestSuites = new HashSet<String>();

	boolean testSuiteExists;

	testSuiteExists = generateTestSuites(testProject, testPackage,
		testClassName, childTestSuites);

	if (testSuiteExists) {
	    return true;
	}

	IJavaElement parent = testPackage.getParent();
	IPackageFragmentRoot packageRoot;
	if (parent instanceof IPackageFragmentRoot) {
	    packageRoot = (IPackageFragmentRoot) parent;

	    IPath parentPackage = new Path(testPackage.getElementName()
		    .replace(".", "/"));
	    if ("test".equals(parentPackage.lastSegment())) {
		parentPackage = parentPackage.removeLastSegments(2);
		while ((parentPackage != null && !parentPackage.isEmpty() && !parentPackage
			.isRoot())) {
		    parentPackage = parentPackage.append("test");
		    testPackage = packageRoot.getPackageFragment(parentPackage
			    .toString().replace("/", "."));
		    generateTestSuites(testProject, testPackage, null,
			    childTestSuites);
		    parentPackage = parentPackage.removeLastSegments(2);
		}

	    } else {
		parentPackage = parentPackage.removeLastSegments(1);
		while ((parentPackage != null && !parentPackage.isEmpty() && !parentPackage
			.isRoot())) {
		    testPackage = packageRoot.getPackageFragment(parentPackage
			    .toString().replace("/", "."));
		    generateTestSuites(testProject, testPackage, null,
			    childTestSuites);
		    parentPackage = parentPackage.removeLastSegments(1);
		}
	    }

	    if (parentPackage.isEmpty()) {
		parentPackage = parentPackage.append("base").append("test");

		if (parentPackage != null) {
		    testPackage = packageRoot.getPackageFragment(parentPackage
			    .toString().replace("/", "."));
		    generateTestSuites(testProject, testPackage, null,
			    childTestSuites);
		}
	    }
	}

	return false;
    }

    private class TestSuiteDeclaration {

	private TestSuiteDeclaration parent = null;

	private String name;

	private IPackageFragment packageFragment;

	private ICompilationUnit[] cuList = null;

	private List<TestSuiteDeclaration> childTestSuiteDeclarations = new ArrayList<TestSuiteDeclaration>();

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getPackageName() {
	    return packageFragment.getElementName();
	}

	public ICompilationUnit[] getCuList() {
	    return cuList;
	}

	public void setCuList(ICompilationUnit[] cuList) {
	    this.cuList = cuList;
	}

	public List<TestSuiteDeclaration> getChildTestSuiteDeclarations() {
	    return childTestSuiteDeclarations;
	}

	public TestSuiteDeclaration getParent() {
	    return parent;
	}

	public void setParent(TestSuiteDeclaration parent) {
	    this.parent = parent;
	}

	public void setPackageFragment(IPackageFragment pf) {
	    this.packageFragment = pf;
	}

	public IPackageFragment getPackageFragment() {
	    return packageFragment;
	}

	public HashSet<String> createChildTestSuiteNameList() {
	    HashSet<String> childTestSuiteNameList = new HashSet<String>();
	    for (TestSuiteDeclaration tsd : childTestSuiteDeclarations) {
		childTestSuiteNameList.add(tsd.getPackageName() + "."
			+ tsd.getName() + ".class");
	    }
	    return childTestSuiteNameList;
	}

    }

    private HashSet<String> createRootTestSuiteNameList(
	    List<TestSuiteDeclaration> tsdList) {
	HashSet<String> rootTestSuiteNameList = new HashSet<String>();
	for (TestSuiteDeclaration tsd : tsdList) {
	    rootTestSuiteNameList.add(tsd.getPackageName() + "."
		    + tsd.getName() + ".class");
	}
	return rootTestSuiteNameList;
    }

    /**
     * Generates all necessary test-suites (top-down)
     * 
     * @throws CoreException
     * @throws OperationCanceledException
     */
    @Override
    public boolean generateTestSuites(final IJavaProject testProject)
	    throws OperationCanceledException, CoreException {
	init();

	JavaCore.run(new IWorkspaceRunnable() {

	    @Override
	    public void run(IProgressMonitor monitor) throws CoreException {
		String testSourceFolderName = JUTPreferences.getTestSourceFolderName();
		
		for (IPackageFragmentRoot fragmentRoot : testProject
			.getPackageFragmentRoots()) {
		    if (testSourceFolderName.equals(fragmentRoot.getPath().lastSegment())) {
			generateTestSuites(monitor, fragmentRoot);
		    }
		}
		
	    }

	    private void generateTestSuites(IProgressMonitor monitor,
		    IPackageFragmentRoot testFragmentRoot) throws JavaModelException, OperationCanceledException, CoreException {
		List<TestSuiteDeclaration> testSuiteDeclarations = new ArrayList<TestSuiteDeclaration>();

		TestSuiteDeclaration tsdTmp, tsdBefore = null;
		String packageBefore = "";
		IPackageFragment baseTestSuitePackage = null;
		ICompilationUnit[] baseTestSuiteCuList = null;

		for (IJavaElement javaElement : testFragmentRoot.getChildren()) {

		    if (javaElement instanceof IPackageFragment) {
			IPackageFragment pf = (IPackageFragment) javaElement;
			if (pf.getKind() == IPackageFragmentRoot.K_SOURCE) {

			    if (pf.getElementName().endsWith(".testbase")) {
				baseTestSuitePackage = pf;
				baseTestSuiteCuList = pf.getCompilationUnits();
				continue;
			    }

			    if (pf.getCompilationUnits().length == 0) {
				continue;
			    }

			    // TestSuiteDeclaration erzeugen
			    tsdTmp = new TestSuiteDeclaration();
			    tsdTmp.setName(TESTSUITE_PREFIX);
			    tsdTmp.setPackageFragment(pf);
			    tsdTmp.setCuList(pf.getCompilationUnits());

			    // zuordnen
			    if (tsdBefore == null || "".equals(packageBefore)) {
				testSuiteDeclarations.add(tsdTmp);
			    } else if (JDTUtils.isParentPackage(packageBefore,
				    pf.getElementName())) {
				tsdTmp.setParent(tsdBefore);
				tsdBefore.getChildTestSuiteDeclarations().add(
					tsdTmp);
			    } else if (tsdBefore.getParent() != null
				    && JDTUtils.isParentPackage(tsdBefore
					    .getParent().getPackageName(), pf
					    .getElementName())) {
				tsdTmp.setParent(tsdBefore.getParent());
				tsdBefore.getParent()
					.getChildTestSuiteDeclarations()
					.add(tsdTmp);
			    } else {
				testSuiteDeclarations.add(tsdTmp);
			    }

			    tsdBefore = tsdTmp;
			    packageBefore = pf.getElementName();
			}
		    }

		    for (TestSuiteDeclaration tsd : testSuiteDeclarations) {
			processTestSuiteDeclaration(tsd);
		    }

		    if (baseTestSuitePackage == null) {
			baseTestSuitePackage = JDTUtils.getPackage(testProject,
				testFragmentRoot, testProject.getElementName()
					+ ".testbase", true);
			baseTestSuiteCuList = new ICompilationUnit[0];
		    }

		    // create base-test-suite
		    processTestSuiteDeclaration("TestSuiteAll",
			    baseTestSuitePackage, baseTestSuiteCuList,
			    createRootTestSuiteNameList(testSuiteDeclarations));
		}
		
	    }

	}, null);

	return true;
    }

    private void processTestSuiteDeclaration(TestSuiteDeclaration tsd)
	    throws OperationCanceledException, CoreException {
	IPackageFragment testSuitePackage = tsd.getPackageFragment();

	processTestSuiteDeclaration(tsd.getName(), testSuitePackage,
		tsd.getCuList(), tsd.createChildTestSuiteNameList());

	// create child test-suites
	for (TestSuiteDeclaration childTsd : tsd
		.getChildTestSuiteDeclarations()) {
	    processTestSuiteDeclaration(childTsd);
	}

    }

    private void processTestSuiteDeclaration(String testSuiteName,
	    IPackageFragment testSuitePackage, ICompilationUnit[] cuList,
	    HashSet<String> childTestSuiteNameList)
	    throws OperationCanceledException, CoreException {
	ICompilationUnit testSuite = testSuitePackage
		.getCompilationUnit(testSuiteName + ".java");

	// create new source
	IType testSuiteFrame = createTestSuiteFrame(testSuiteName, testSuite,
		cuList, null, childTestSuiteNameList, true);

	if (testSuiteFrame == null) {
	    return;
	}
	
	// save compilation-unit
	testSuite.save(null, false);
	testSuite.makeConsistent(null);
	if (testSuite.hasUnsavedChanges()) {
	    testSuite.commitWorkingCopy(true, null);
	}

	generatedTestSuites.add(testSuite);
    }

    /**
     * Generates the test suites.
     * 
     * @param testProject
     * @param testPackage
     * @param newTestSuiteElementName
     * @param childTestSuites
     * @return true if test test suite already exists
     * @throws CoreException
     */
    private boolean generateTestSuites(IJavaProject testProject,
	    IPackageFragment testPackage, String newTestSuiteElementName,
	    HashSet<String> childTestSuites) throws CoreException {
	boolean exists = false;

	if (testPackage == null || !testPackage.exists()) {
	    return exists;
	}

	String testSuiteName = TESTSUITE_PREFIX;

	ICompilationUnit testSuite;
	ICompilationUnit[] testClasses;

	testSuite = getTestSuite(testPackage);
	testClasses = testPackage.getCompilationUnits();

	if (testClasses.length == 0
		&& !"base".equals(testPackage.getPath().lastSegment())) {
	    return exists;
	}

	// create new source
	IType testSuiteFrame = createTestSuiteFrame(testSuiteName, testSuite,
		testClasses, newTestSuiteElementName, childTestSuites, false);
	if (testSuiteFrame == null) {
	    return true;
	}

	// create new compilation-unit
	testSuite.save(null, false);
	testSuite.makeConsistent(null);
	if (testSuite.hasUnsavedChanges()) {
	    testSuite.commitWorkingCopy(true, null);
	}

	generatedTestSuites.add(testSuite);

	// clear child test-suites
	childTestSuites.clear();
	// add generated test-suite
	String testSuitePath = "";

	if (!"".equals(testPackage.getElementName())) {
	    testSuitePath = testPackage.getElementName() + ".";
	}

	childTestSuites.add(testSuitePath + TESTSUITE_PREFIX + ".class");

	return exists;
    }

    /**
     * Creates the test suite frame.
     * 
     * @param testSuiteName
     * @param testSuite
     * @param testClasses
     * @param newTestSuiteElementName
     * @param generatedTestSuites
     * @param createNew
     * @return the test-suite
     * @throws JavaModelException
     */
    protected IType createTestSuiteFrame(String testSuiteName,
	    ICompilationUnit testSuite, ICompilationUnit[] testClasses,
	    String newTestSuiteElementName,
	    HashSet<String> generatedTestSuites, boolean createNew)
	    throws JavaModelException {
	IType type = testSuite.getType(testSuiteName);

	String testElementList;

	if (type.exists()) {
	    if (!createNew) {
		for (IAnnotation annotation : type.getAnnotations()) {
		    if ("Suite.SuiteClasses"
			    .equals(annotation.getElementName())) {
			for (IMemberValuePair memberValuePairs : annotation
				.getMemberValuePairs()) {
			    Object value = memberValuePairs.getValue();
			    String suiteClassName;

			    if (value instanceof Object[]) {
				for (Object testSuiteObject : (Object[]) value) {
				    suiteClassName = testSuiteObject.toString();
				    if (suiteClassName
					    .startsWith(TESTSUITE_PREFIX)) {
					generatedTestSuites.add(suiteClassName
						+ ".class");
				    }
				    if (suiteClassName
					    .equals(newTestSuiteElementName)) {
					return null;
				    }
				}
			    }
			}

		    }
		}
	    }

	}

	testElementList = createCommaSeparatedClassList(testClasses,
		generatedTestSuites);

	return refreshTestSuiteElements(testSuite, testSuiteName,
		testElementList);
    }

    /**
     * Creates a comma separated class list.
     * 
     * @param testClasses
     * @param generatedTestSuites
     * @return separated class list
     */
    protected String createCommaSeparatedClassList(
	    ICompilationUnit[] testClasses, HashSet<String> generatedTestSuites) {
	return createCommaSeparatedClassList(testClasses, generatedTestSuites,
		"");
    }

    protected String createCommaSeparatedClassList(
	    ICompilationUnit[] testClasses,
	    HashSet<String> generatedTestSuites, String... exceptionClasses) {
	boolean first = true;
	StringBuilder sb = new StringBuilder();
	String testClassName;
	String testClassNameWithClassSuffix;

	for (ICompilationUnit testClass : testClasses) {
	    if (!testClass.exists()) {
		continue;
	    }

	    testClassNameWithClassSuffix = testClass.getElementName().replace(
		    ".java", ".class");
	    testClassName = testClassNameWithClassSuffix.replace(".class", "");

	    if (testClassName.equals(TESTSUITE_PREFIX)
		    || !isValidTestClassName(testClassName)) {
		continue;
	    }

	    if (isExceptionClass(testClassName, exceptionClasses)) {
		continue;
	    }

	    if (!first) {
		sb.append(", ");
	    } else {
		first = false;
	    }

	    sb.append(testClassNameWithClassSuffix);
	}

	if (generatedTestSuites != null) {
	    for (String generatedTestSuite : generatedTestSuites) {
		if (generatedTestSuite.startsWith(".")) {
		    continue;
		}

		if (!first) {
		    sb.append(", ");
		} else {
		    first = false;
		}

		sb.append(generatedTestSuite);
	    }
	}

	return sb.toString();
    }

    private boolean isValidTestClassName(String testClassName) {
	String testClassPrefix = JUTPreferences.getTestClassPrefix();
	String testClassPostfix = JUTPreferences.getTestClassPostfix();

	if (!"".equals(testClassPrefix)) {
	    if (!testClassName.startsWith(testClassPrefix)) {
		return false;
	    }
	}

	if (!"".equals(testClassPostfix)) {
	    if (!testClassName.endsWith(testClassPostfix)) {
		return false;
	    }
	}

	return true;
    }

    private boolean isExceptionClass(String testClassName,
	    String[] exceptionClasses) {
	for (String exceptionClass : exceptionClasses) {
	    if (exceptionClass.equals(testClassName)) {
		return true;
	    }
	}

	return false;
    }

    private void init() {
	generatedTestSuites = new ArrayList<ICompilationUnit>();
    }

    @Override
    public boolean deleteTestSuiteElement(
	    final IPackageFragment packageOfDeletedClass,
	    final ICompilationUnit deletedClass) throws CoreException {
	init();

	if (packageOfDeletedClass == null) {
	    return true;
	}

	JavaCore.run(new IWorkspaceRunnable() {

	    @Override
	    public void run(IProgressMonitor monitor) throws CoreException {

		ICompilationUnit testSuite = getTestSuite(packageOfDeletedClass);

		String testElementList = createCommaSeparatedClassList(
			packageOfDeletedClass.getCompilationUnits(), null,
			deletedClass.getElementName().replace(".java", ""));

		IType testSuiteType = refreshTestSuiteElements(testSuite,
			TESTSUITE_PREFIX, testElementList);

		// if only the test-class and the test-suite is in the package,
		// then delete the complete package
		if (testSuiteType == null
			&& packageOfDeletedClass.getCompilationUnits().length == 2) {
		    // delete empty packages, if no class is inside
		    JDTUtils.deletePackages(packageOfDeletedClass);
		}

	    }

	}, null);

	return true;
    }

    protected ICompilationUnit getTestSuite(IPackageFragment p)
	    throws JavaModelException {
	return p.getCompilationUnit(TESTSUITE_PREFIX + ".java");
    }

    private IType refreshTestSuiteElements(ICompilationUnit testSuite,
	    String testSuiteName, String testElementList)
	    throws JavaModelException {
	IType testSuiteType = testSuite.getType(testSuiteName);

	if (testSuiteType.exists()) {
	    testSuiteType.delete(true, null);
	}

	if (testElementList.length() > 0) {
	    String testAnnotation = "@RunWith(Suite.class)\n@Suite.SuiteClasses(\n\n{"
		    + testElementList + "}\n)";

	    testSuiteType = testSuite.createType(testAnnotation
		    + "\npublic class " + testSuiteType.getElementName()
		    + "{ // nothing\n}", null, true, null);
	} else {
	    if (testSuite.exists()) {
		testSuite.delete(true, null);
	    }
	    return null;
	}

	return testSuiteType;
    }

    public ArrayList<ICompilationUnit> getGeneratedTestSuites() {
	return generatedTestSuites;
    }
}
