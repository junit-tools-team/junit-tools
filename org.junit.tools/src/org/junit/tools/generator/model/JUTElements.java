package org.junit.tools.generator.model;

import java.util.Vector;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.messages.Messages;
import org.junit.tools.preferences.JUTPreferences;

/**
 * Class for the junit-tools-elements: project, package and class from the base
 * and test.
 * 
 * @author Robert Streng
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

	private IPackageFragment basePackage = null;

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
	 */
	public ICompilationUnit getTestClass(boolean createIfNotExists)
		throws CoreException {
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
	public IPackageFragment getBasePackage() {
	    return basePackage;
	}

	/**
	 * @param basePackage
	 */
	public void setBasePackage(IPackageFragment basePackage) {
	    this.basePackage = basePackage;
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
	 */
	public IPackageFragment getTestPackage(boolean createIfNotExists)
		throws CoreException {
	    if (createIfNotExists
		    && (testPackage == null || !testPackage.exists())) {
		testPackage = JDTUtils.createPackage(projects.getTestProject(), JUTPreferences.getTestSourceFolderName(),
			testPackageName);
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

    public JUTProjects initProjects(IJavaProject project, ICompilationUnit cu) throws JUTWarning {
	JUTProjects tmpProjects = new JUTProjects();

	if (project == null) {
	    return null;
	}

	String projectName = project.getElementName();
	String testProjectPostfix = JUTPreferences.getTestProjectPostfix();

	// test project selected
	if (!testProjectPostfix.equals("") && projectName.endsWith(testProjectPostfix)) {
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
		testProject = (IJavaProject)project;
		
		// identify by test-class-name and test-source-folder-name
		if (cu != null) {
		    String testClassPostfix = JUTPreferences
			    .getTestClassPostfix();
		    
		    if (!"".equals(testClassPostfix)) {
			String cuName = cu.getElementName().replace(".java", "");
			cuName = cuName.replace(".class", "");
			
			if (!cuName.endsWith(testClassPostfix)) {
			    tmpProjects.setBaseProjectSelected(true);
			}
		    }
		    else {
			String testSourceFolderName = JUTPreferences.getTestProjectPostfix();
			if ("src".equals(testSourceFolderName) || "".equals(testSourceFolderName)) {
			    // same folder as base-class - no difference to base-class
			    tmpProjects.setBaseProjectSelected(true);
			}
			else {
			    // get the source folder of the selected cu
			    IPackageFragment cuPackage = JDTUtils
				    .getPackage(cu);
			    IJavaElement parent = cuPackage.getParent();

			    if (parent instanceof IFolder) {
				IFolder srcFolder = (IFolder) parent;

				// is the selected class in the test-source-folder
				if (!srcFolder.equals(testSourceFolderName)) {
				    tmpProjects.setBaseProjectSelected(true);
				}
			    }
			}
		    }
		}
		else {
		    tmpProjects.setBaseProjectSelected(true);
		}
	    }
	    else {
		// base-project selected
		tmpProjects.setBaseProjectSelected(true);
		
		projectName += testProjectPostfix;
		testProject = JDTUtils.getProject(projectName, false,
			project);
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

	ICompilationUnit baseCu;
	IPackageFragment basePackage;

	ICompilationUnit testCu = null;
	String baseCuName = "", testCuName = "";
	IPackageFragment testPackage = null;

	String testClassPostfix = JUTPreferences.getTestClassPostfix();
	String testProjectPostfix = JUTPreferences.getTestProjectPostfix();

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
	    basePackage = jutClassesAndPackages.getBasePackage();

	    if (projects.getTestProject() != null) {
		testPackage = jutClassesAndPackages.getTestPackage();

		baseCuName = baseCu.getElementName().replace(".java", "");
		testCuName = baseCuName + testClassPostfix;

		if (testPackage != null) {
		    testCu = testPackage.getCompilationUnit(testCuName
			    + ".java");
		}
	    }
	} else {
	    testCu = cuList.get(0);
	    testPackage = jutClassesAndPackages.getTestPackage();
	    basePackage = jutClassesAndPackages.getBasePackage();

	    testCuName = testCu.getElementName().replace(".java", "");
	    baseCuName = testCu.getElementName().replace(
		    testClassPostfix + ".java", "");

	    if (basePackage == null) {
		if (testPackage == null) {
		    throw new JUTWarning(
			    "The base and test package could not be found! Test-Class-Name: "
				    + testCuName + " Base-Class-Name: "
				    + baseCuName);
		} else {
		    throw new JUTWarning(
			    "The base package could not be found! Test-package-name: "
				    + testPackage.getElementName());
		}
	    }

	    baseCu = basePackage.getCompilationUnit(baseCuName + ".java");
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
	JUTClassesAndPackages jutClassesAndPackages = new JUTClassesAndPackages();
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
	    basePackageName = pack.getElementName();
	    testProjectName = null;

	    if (projects.getTestProject() != null) {
		testProjectName = projects.getTestProject().getElementName();

		if (testPackagePostfix.equals("")) {
		    testPackageName = basePackageName;
		}
		else if ("".equals(basePackageName)) {
		    testPackageName = testPackagePostfix;
		} else if (basePackageName.startsWith(baseProjectName)) {
		    testPackageName = basePackageName.replace(baseProjectName,
			    testProjectName);
		} else if (testPackagePostfix.equals("")) {
		    testPackageName = basePackageName;
		} else {
		    testPackageName = basePackageName + "."
			    + testPackagePostfix;
		}

		testPackage = JDTUtils.getPackage(projects.getTestProject(), JUTPreferences.getTestSourceFolderName(),
			testPackageName, false);
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

		if (lastIndexOf < 0) {
		    basePackageName = testPackageName.substring(0, lastIndexOf);
		} else {
		    throw new JUTWarning(
			    "The base package could not be found! Test-package-name: "
				    + testPackageName);
		}

		// this is a special option: the postfix is always like the
		// test-project name and the rest of the base-package-name +
		// test-package-postfix
		// else if (testPackageName.startsWith(testProjectName)) {
		// basePackageName = testPackageName.replace(testProjectName,
		// baseProjectName);
		// }

	    }

	    basePackage = JDTUtils.getPackage(projects.getBaseProject(), "src",
		    basePackageName, false);
	}

	if (basePackage == null || !basePackage.exists()) {
	    throw new JUTWarning(
		    "The base package could not be found! The base-class was moved manually or the preferences are not correct.");
	}

	jutClassesAndPackages.setTestPackageName(testPackageName);
	jutClassesAndPackages.setBasePackage(basePackage);
	jutClassesAndPackages.setTestPackage(testPackage);
	setClassesAndPackages(jutClassesAndPackages);

	return jutClassesAndPackages;
    }
}
