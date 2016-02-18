package org.junit.tools.generator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.messages.Messages;
import org.junit.tools.preferences.JUTPreferences;

/**
 * Class for the junit-tools-elements: project, package and class from the base
 * and test.
 * 
 * @author JUnit-Tools-Team
 */
public class JUTElements {

    /**
     * Constructor and methods.
     */
    public static class JUTConstructorsAndMethods {

	private Vector<IMethod> baseClassMethods;

	private Vector<IMethod> baseClassConstructors;

	// the select base- or test-method
	private IMethod selectedMethod;

	/**
	 * @return base class methods
	 */
	public Vector<IMethod> getBaseClassMethods() {
	    return baseClassMethods;
	}

	/**
	 * @param baseClassMethods
	 */
	public void setBaseClassMethods(Vector<IMethod> baseClassMethods) {
	    this.baseClassMethods = baseClassMethods;
	}

	/**
	 * @return base class constructors
	 */
	public Vector<IMethod> getBaseClassConstructors() {
	    return baseClassConstructors;
	}

	/**
	 * @param baseClassConstructors
	 */
	public void setBaseClassConstructors(
		Vector<IMethod> baseClassConstructors) {
	    this.baseClassConstructors = baseClassConstructors;
	}

	public IMethod getSelectedMethod() {
	    return selectedMethod;
	}

	public void setSelectedMethod(IMethod selectedMethod) {
	    this.selectedMethod = selectedMethod;
	}

    }

    /**
     * Classes and packages.
     */
    public class JUTClassesAndPackages {

	private ICompilationUnit baseClass = null;

	private String testBaseName = "";

	private ICompilationUnit testClass = null;

	private String testClassName = "";

	private List<IPackageFragmentRoot> baseSrcFolders = null;

	private IPackageFragmentRoot testSrcFolder = null;

	private List<IPackageFragment> basePackages = null;

	private IPackageFragment testPackage = null;

	private String testPackageName = null;

	/**
	 * @return test base
	 */
	public ICompilationUnit getBaseClass() {
	    return baseClass;
	}

	/**
	 * @param baseClass
	 */
	public void setBaseTest(ICompilationUnit baseClass) {
	    this.baseClass = baseClass;
	}

	/**
	 * @return test class
	 */
	public ICompilationUnit getTestClass() {
	    return testClass;
	}

	/**
	 * @return test class
	 * @throws CoreException
	 * @throws JUTWarning 
	 */
	public ICompilationUnit getTestClass(boolean createIfNotExists)
		throws CoreException, JUTWarning {
	    if (createIfNotExists && (testClass == null || !testClass.exists())) {
		IPackageFragment testPckg = getTestPackage(true);
		testClass = testPckg.createCompilationUnit(testClassName
			+ ".java", "", true, null);
	    }

	    return testClass;
	}

	/**
	 * @param testClass
	 */
	public void setTestClass(ICompilationUnit testClass) {
	    this.testClass = testClass;
	}

	/**
	 * @return base package
	 */
	public List<IPackageFragment> getBasePackages() {
	    return basePackages;
	}

	/**
	 * @param basePackage
	 */
	public void setBasePackages(List<IPackageFragment> basePackages) {
	    this.basePackages = basePackages;
	}

	/**
	 * @return test package
	 */
	public IPackageFragment getTestPackage() {
	    return testPackage;
	}

	/**
	 * @return test package
	 * @throws CoreException
	 * @throws JUTWarning 
	 */
	public IPackageFragment getTestPackage(boolean createIfNotExists)
		throws CoreException, JUTWarning {
	    if (createIfNotExists
		    && (testPackage == null || !testPackage.exists())) {
		testPackage = JDTUtils.getPackage(
			projects.getTestProject(),
			getTestSourceFolder(projects.getTestProject(),
				classesAndPackages.getBasePackages().get(0)),
			testPackageName, true);
	    }
	    return testPackage;
	}

	/**
	 * @param testPackage
	 */
	public void setTestPackage(IPackageFragment testPackage) {
	    this.testPackage = testPackage;
	}

	/**
	 * @return test class name
	 */
	public String getTestClassName() {
	    return testClassName;
	}

	/**
	 * @param testClassName
	 */
	public void setTestClassName(String testClassName) {
	    this.testClassName = testClassName;
	}

	/**
	 * @return base class name
	 */
	public String getBaseClassName() {
	    return testBaseName;
	}

	/**
	 * @param baseClassName
	 */
	public void setBaseClassName(String baseClassName) {
	    this.testBaseName = baseClassName;
	}

	public void setTestPackageName(String testPackageName) {
	    this.testPackageName = testPackageName;
	}

	public String getTestPackageName() {
	    return testPackageName;
	}

	public void setTestSrcFolder(IPackageFragmentRoot testSrcFolder) {
	    this.testSrcFolder = testSrcFolder;
	}

	public IPackageFragmentRoot getTestFolder() {
	    return testSrcFolder;
	}

	public void addBaseSrcFolder(IPackageFragmentRoot baseSrcFolder) {
	    if (getBaseSrcFolders() == null) {
		baseSrcFolders = new ArrayList<IPackageFragmentRoot>();
	    }

	    baseSrcFolders.add(baseSrcFolder);
	}

	public void setBaseSrcFolders(List<IPackageFragmentRoot> baseSrcFolders) {
	    this.baseSrcFolders = baseSrcFolders;
	}

	public List<IPackageFragmentRoot> getBaseSrcFolders() {
	    return baseSrcFolders;
	}
    }

    /**
     * Test project and the project of the test base.
     */
    public static class JUTProjects {

	private IJavaProject testProject = null;

	private IJavaProject baseProject = null;

	private String testProjectName = "";

	private boolean isBaseProjectSelected = true;

	/**
	 * @return test project
	 */
	public IJavaProject getTestProject() {
	    return testProject;
	}

	/**
	 * @param testProject
	 */
	public void setTestProject(IJavaProject testProject) {
	    this.testProject = testProject;
	}

	/**
	 * @return base project
	 */
	public IJavaProject getBaseProject() {
	    return baseProject;
	}

	/**
	 * @param baseProject
	 */
	public void setBaseProject(IJavaProject baseProject) {
	    this.baseProject = baseProject;
	}

	/**
	 * @return true if base project is selected
	 */
	public boolean isBaseProjectSelected() {
	    return isBaseProjectSelected;
	}

	/**
	 * @param isBaseProjectSelected
	 */
	public void setBaseProjectSelected(boolean isBaseProjectSelected) {
	    this.isBaseProjectSelected = isBaseProjectSelected;
	}

	/**
	 * @return test project name
	 */
	public String getTestProjectName() {
	    return testProjectName;
	}

	/**
	 * @param testProjectName
	 */
	public void setTestProjectName(String testProjectName) {
	    this.testProjectName = testProjectName;
	}

	public boolean isTestProjectFound() {
	    return getTestProject() != null;
	}

    }

    private JUTProjects projects;

    private JUTClassesAndPackages classesAndPackages;

    private JUTConstructorsAndMethods constructorsAndMethods;

    /**
     * @return the projects
     */
    public JUTProjects getProjects() {
	return projects;
    }

    /**
     * @param projects
     * @throws JUTWarning
     */
    public void setProjects(JUTProjects projects) throws JUTWarning {
	if (projects.isBaseProjectSelected()) {
	    if (projects.getBaseProject() == null) {
		throw new JUTWarning(
			Messages.General_warning_project_initialization);
	    } else if (projects.getTestProject() == null) {
		throw new JUTWarning(
			Messages.General_warning_test_project_not_found);
	    }
	} else {
	    if (projects.getTestProject() == null) {
		throw new JUTWarning(
			Messages.General_warning_project_initialization);
	    } else if (projects.getBaseProject() == null) {
		throw new JUTWarning(
			Messages.General_warning_base_project_not_found);
	    }
	}

	this.projects = projects;
    }

    /**
     * @return classes and packages
     */
    public JUTClassesAndPackages getClassesAndPackages() {
	return classesAndPackages;
    }

    /**
     * @param classesAndPackages
     */
    public void setClassesAndPackages(JUTClassesAndPackages classesAndPackages) {
	this.classesAndPackages = classesAndPackages;
    }

    /**
     * @return constructors and methods
     */
    public JUTConstructorsAndMethods getConstructorsAndMethods() {
	return constructorsAndMethods;
    }

    /**
     * @param constructorsAndMethods
     */
    public void setConstructorsAndMethods(
	    JUTConstructorsAndMethods constructorsAndMethods) {
	this.constructorsAndMethods = constructorsAndMethods;
    }

    public JUTProjects initProjects(IJavaProject project, ICompilationUnit cu)
	    throws JUTWarning {
	JUTProjects tmpProjects = new JUTProjects();

	if (project == null) {
	    return null;
	}

	String projectName = project.getElementName();
	String testProjectPostfix = JUTPreferences.getTestProjectPostfix();

	// test project selected
	if (!testProjectPostfix.equals("")
		&& projectName.endsWith(testProjectPostfix)) {
	    tmpProjects.setBaseProjectSelected(false);

	    tmpProjects.setTestProject((IJavaProject) project);

	    // get base project
	    int lastIx = projectName.lastIndexOf(testProjectPostfix);
	    if (lastIx < 1) {
		throw new JUTWarning(Messages.General_warning_nothing_selected);
	    } else {
		projectName = projectName.substring(0, lastIx);
	    }

	    IJavaProject baseProject = (IJavaProject) JDTUtils
		    .getProject(projectName);
	    tmpProjects.setBaseProject(baseProject);
	} else {
	    tmpProjects.setBaseProject((IJavaProject) project);
	    IJavaProject testProject;

	    if (testProjectPostfix.equals("")) {
		// base- and test-project have same names
		tmpProjects.setBaseProjectSelected(false);
		testProject = (IJavaProject) project;

		// identify by test-class-name and test-source-folder-name
		if (cu != null) {
		    String testClassPrefix = JUTPreferences
			    .getTestClassPrefix();
		    String testClassPostfix = JUTPreferences
			    .getTestClassPostfix();

		    if (!"".equals(testClassPostfix)
			    || !"".equals(testClassPrefix)) {
			String cuName = cu.getElementName()
				.replace(".java", "");
			cuName = cuName.replace(".class", "");

			if (!cuName.startsWith(testClassPrefix)
				|| !cuName.endsWith(testClassPostfix)) {
			    tmpProjects.setBaseProjectSelected(true);
			}
		    } else {
			String testSourceFolderName = JUTPreferences
				.getTestProjectPostfix();
			if ("src".equals(testSourceFolderName)
				|| "".equals(testSourceFolderName)) {
			    // same folder as base-class - no difference to
			    // base-class
			    tmpProjects.setBaseProjectSelected(true);
			} else {
			    // get the source folder of the selected cu
			    IPackageFragment cuPackage = JDTUtils
				    .getPackage(cu);
			    IJavaElement parent = cuPackage.getParent();

			    if (parent instanceof IFolder) {
				IFolder srcFolder = (IFolder) parent;

				// is the selected class in the
				// test-source-folder
				if (!srcFolder.equals(testSourceFolderName)) {
				    tmpProjects.setBaseProjectSelected(true);
				}
			    }
			}
		    }
		} else {
		    tmpProjects.setBaseProjectSelected(true);
		}
	    } else {
		// base-project selected
		tmpProjects.setBaseProjectSelected(true);

		projectName += testProjectPostfix;
		testProject = JDTUtils.getProject(projectName, false, project);
	    }

	    if (testProject != null) {
		tmpProjects.setTestProjectName(testProject.getElementName());
		tmpProjects.setTestProject(testProject);
	    }
	}

	setProjects(tmpProjects);

	return tmpProjects;
    }

    public JUTProjects initProjects(IJavaProject project) throws JUTWarning {
	return initProjects(project, null);
    }

    public JUTClassesAndPackages initClassesAndPackages(ICompilationUnit cu)
	    throws JUTWarning, CoreException {
	Vector<ICompilationUnit> cuList = new Vector<ICompilationUnit>();
	cuList.add(cu);
	return initClassesAndPackages(cuList);
    }

    public JUTClassesAndPackages initClassesAndPackages(
	    Vector<ICompilationUnit> cuList) throws JUTWarning, CoreException {

	ICompilationUnit baseCu = null;

	ICompilationUnit testCu = null;
	String baseCuName = "", testCuName = "";
	IPackageFragment testPackage = null;

	String testClassPrefix = JUTPreferences.getTestClassPrefix();
	String testClassPostfix = JUTPreferences.getTestClassPostfix();

	if (cuList.size() == 0) {
	    throw new JUTWarning(Messages.General_warning_nothing_selected);
	}

	// initialize project, if necessary
	ICompilationUnit cu = cuList.get(0);
	if (projects.getBaseProject() == null) {
	    initProjects(cu.getJavaProject());
	}

	JUTClassesAndPackages jutClassesAndPackages = initPackages(JDTUtils
		.getPackage(cu));

	if (projects.isBaseProjectSelected()) {
	    baseCu = cuList.get(0);

	    if (projects.getTestProject() != null) {
		testPackage = jutClassesAndPackages.getTestPackage();

		baseCuName = baseCu.getElementName().replace(".java", "");

		if (!"".equals(testClassPrefix)) {
		    baseCuName = GeneratorUtils.firstCharToUpper(baseCuName);
		}
		testCuName = testClassPrefix + baseCuName + testClassPostfix;

		if (testPackage != null) {
		    testCu = testPackage.getCompilationUnit(testCuName
			    + ".java");
		}
	    }
	} else {
	    testCu = cuList.get(0);
	    testPackage = jutClassesAndPackages.getTestPackage();

	    testCuName = testCu.getElementName().replace(".java", "");
	    baseCuName = testCu.getElementName().replace(
		    testClassPostfix + ".java", "");
	    if (!"".equals(testClassPrefix)) {
		baseCuName = baseCuName.replaceFirst(testClassPrefix, "");
	    }

	    if (jutClassesAndPackages.getBasePackages().size() == 0) {
		if (testPackage == null) {
		    throw new JUTWarning(
			    "The base and test package could not be found! Test-Class-Name: "
				    + testCuName + " Base-Class-Name: "
				    + baseCuName);
		} else {
		    throw new JUTWarning(
			    "The base package could not be found (perhaps moved/renamed manually or configuration is wrong)! Test-package-name: "
				    + testPackage.getElementName());
		}
	    }

	    for (IPackageFragment tmpBasePackage : jutClassesAndPackages
		    .getBasePackages()) {
		baseCu = tmpBasePackage
			.getCompilationUnit(baseCuName + ".java");

		if (baseCu != null && baseCu.exists()) {
		    break;
		}
	    }

	}

	if (baseCu == null) {
	    throw new JUTWarning(
		    "The base class could not be found (perhaps moved/renamed manually or the configuration is wrong)! Test-class-name: "
			    + testCuName);
	}

	jutClassesAndPackages.setBaseTest(baseCu);
	jutClassesAndPackages.setBaseClassName(baseCuName);
	jutClassesAndPackages.setTestClass(testCu);
	jutClassesAndPackages.setTestClassName(testCuName);

	return jutClassesAndPackages;
    }

    public static JUTElements initJUTElements(IJavaProject project)
	    throws JUTWarning {
	JUTElements jutElements = new JUTElements();

	jutElements.initProjects(project);

	return jutElements;
    }

    public static JUTElements initJUTElements(IJavaProject project,
	    ICompilationUnit cu) throws JUTWarning, CoreException {
	JUTElements jutElements = new JUTElements();

	jutElements.initProjects(project, cu);
	jutElements.initClassesAndPackages(cu);

	return jutElements;
    }

    public static JUTElements initJUTElements(IJavaProject project,
	    IPackageFragment pack) throws JUTWarning, CoreException {
	JUTElements jutElements = new JUTElements();

	jutElements.initProjects(project);
	jutElements.initPackages(pack);

	return jutElements;
    }

    /**
     * Initialize the packages
     * 
     * @param pack
     * @return JUTClassesAndPackages the initialized packages
     * @throws CoreException
     */
    protected JUTClassesAndPackages initPackages(IPackageFragment pack)
	    throws CoreException, JUTWarning {

	List<IPackageFragmentRoot> baseSrcFolders = new ArrayList<IPackageFragmentRoot>();
	IPackageFragmentRoot testSrcFolder = null;

	JUTClassesAndPackages jutClassesAndPackages = new JUTClassesAndPackages();
	List<IPackageFragment> basePackages = new ArrayList<IPackageFragment>();
	IPackageFragment basePackage = null;
	IPackageFragment testPackage = null;

	String testPackagePostfix = JUTPreferences.getTestPackagePostfix();

	String baseProjectName = "";
	String basePackageName = "";
	String testProjectName = "";
	String testPackageName = "";

	if (projects.isBaseProjectSelected()) {
	    baseProjectName = projects.getBaseProject().getElementName();
	    basePackage = pack;
	    basePackages.add(basePackage);
	    basePackageName = pack.getElementName();
	    testProjectName = null;

	    if (projects.getTestProject() != null) {
		testProjectName = projects.getTestProject().getElementName();

		if (testPackagePostfix.equals("")) {
		    testPackageName = basePackageName;
		} else if ("".equals(basePackageName)) {
		    testPackageName = testPackagePostfix;
		} else if (basePackageName.startsWith(baseProjectName)) {
		    testPackageName = basePackageName.replace(baseProjectName,
			    testProjectName);
		    testPackageName += testPackagePostfix;
		} else {
		    testPackageName = basePackageName + "."
			    + testPackagePostfix;
		}

		testSrcFolder = getTestSourceFolder(projects.getTestProject(),
			basePackage);

		testPackage = JDTUtils.getPackage(projects.getTestProject(),
			testSrcFolder, testPackageName, false);
	    }
	} else {
	    testPackage = pack;
	    baseProjectName = projects.getBaseProject().getElementName();
	    testProjectName = projects.getTestProject().getElementName();
	    testPackageName = testPackage.getElementName();

	    if (testPackagePostfix.equals("")) {
		basePackageName = testPackageName;
	    } else if (testPackagePostfix.equals(testPackageName)) {
		basePackageName = ""; // default package
	    } else {
		int lastIndexOf = testPackageName
			.lastIndexOf(testPackagePostfix);

		if (lastIndexOf > -1) {
		    basePackageName = testPackageName.substring(0, lastIndexOf);
		} else {
		    throw new JUTWarning(
			    "The base package could not be found! Test-package-name: "
				    + testPackageName);
		}

	    }

	    baseSrcFolders = getBaseSourceFolders(projects.getBaseProject(),
		    testPackage);

	    for (IPackageFragmentRoot root : baseSrcFolders) {
		basePackage = JDTUtils.getPackage(projects.getBaseProject(),
			root, basePackageName, false);
		if (basePackage != null && basePackage.exists()) {
		    basePackages.add(basePackage);
		}
	    }

	}

	if (basePackages.size() == 0) {
	    throw new JUTWarning(
		    "The base package could not be found! The base-class was moved manually or the preferences are not correct.");
	}

	jutClassesAndPackages.setTestPackageName(testPackageName);
	jutClassesAndPackages.setBasePackages(basePackages);
	jutClassesAndPackages.setBaseSrcFolders(baseSrcFolders);
	jutClassesAndPackages.setTestPackage(testPackage);
	jutClassesAndPackages.setTestSrcFolder(testSrcFolder);
	setClassesAndPackages(jutClassesAndPackages);

	return jutClassesAndPackages;
    }

    private List<IPackageFragmentRoot> getBaseSourceFolders(
	    IJavaProject baseProject, IPackageFragment testPackage)
	    throws CoreException {
	IJavaElement parent = testPackage.getParent();

	List<IPackageFragmentRoot> baseSourceFolders = new ArrayList<IPackageFragmentRoot>();

	// only if same project use different source folders
	if (parent instanceof IPackageFragmentRoot) {
	    IPackageFragmentRoot testRoot = (IPackageFragmentRoot) parent;

	    IPath basePath = null;

	    // delete project path segment
	    IPath testPath = testRoot.getPath();
	    IPath onlyFolderPath = testPath.removeFirstSegments(1);

	    // delete test source folder segment
	    onlyFolderPath = onlyFolderPath.removeLastSegments(1);
	    
	    // add default source folder name for base
	    basePath = onlyFolderPath.append("src");

	    // search for relevant folders
	    IPath pathToCompare;
	    for (IPackageFragmentRoot root : baseProject
		    .getPackageFragmentRoots()) {
		pathToCompare = root.getPath().removeFirstSegments(1);
		
		if (pathToCompare.matchingFirstSegments(basePath) == basePath
			.segmentCount()) {
		    baseSourceFolders.add(root);
		}
	    }

	}

	// set default source folder
	if (baseSourceFolders.size() == 0) {
	    IFolder folder = baseProject.getProject().getFolder("src");
	    baseSourceFolders.add(baseProject.getPackageFragmentRoot(folder));
	}

	return baseSourceFolders;
    }

    private IPackageFragmentRoot getTestSourceFolder(IJavaProject testProject,
	    IPackageFragment basePackage) throws CoreException, JUTWarning {
	List<String> testSegments = new ArrayList<String>();

	IJavaElement parent = basePackage.getParent();
	if (parent instanceof IPackageFragmentRoot) {
	    IPackageFragmentRoot baseRoot = (IPackageFragmentRoot) parent;

	    for (String segment : baseRoot.getPath().segments()) {

		if (segment.equals(basePackage.getJavaProject()
			.getElementName())
			|| segment.equals(testProject.getElementName())) {
		    continue;
		}

		if ("src".equals(segment)) {
		    break;
		} else {
		    testSegments.add(segment);
		}
	    }
	}

	// test folder name
	String testSourceFolderName = JUTPreferences.getTestSourceFolderName();
	if ("".equals(testSourceFolderName)) {
	    testSourceFolderName = "src";
	}
	testSegments.add(testSourceFolderName);

	// get full test path
	String testFullPath = "";
	for (String testSegment : testSegments) {
	    testFullPath += "/" + testSegment;
	}

	IPath testPath = new Path(testFullPath);

	IPackageFragmentRoot testSourceFolder = JDTUtils.createSourceFolder(
		testProject, testPath);
	return testSourceFolder;
    }

    protected void getBaseFolders() {

    }
}
