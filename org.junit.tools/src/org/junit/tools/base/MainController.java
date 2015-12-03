package org.junit.tools.base;

import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.tools.Activator;
import org.junit.tools.generator.IGeneratorConstants;
import org.junit.tools.generator.ITestClassGenerator;
import org.junit.tools.generator.ITestSuitesGenerator;
import org.junit.tools.generator.TestCasesGenerator;
import org.junit.tools.generator.model.GeneratorModel;
import org.junit.tools.generator.model.JUTElements;
import org.junit.tools.generator.model.JUTElements.JUTClassesAndPackages;
import org.junit.tools.generator.model.JUTElements.JUTConstructorsAndMethods;
import org.junit.tools.generator.model.JUTElements.JUTProjects;
import org.junit.tools.generator.model.tml.Settings;
import org.junit.tools.generator.model.tml.TMLProcessor;
import org.junit.tools.generator.model.tml.Test;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.messages.Messages;
import org.junit.tools.preferences.JUTPreferences;
import org.junit.tools.reports.ReportCreator;
import org.junit.tools.ui.generator.wizards.GeneratorWizard;
import org.junit.tools.ui.utils.EclipseUIUtils;

/**
 * Main controller for all junit-tools-commands and actions.
 * 
 * TODO move the logic to the handler classes.
 * 
 * @author Robert Streng
 */
public class MainController implements IGeneratorConstants {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private ICompilationUnit generatedTestClass = null;

    private boolean warning = false;
    
    private JUTWarning warningException;

    private boolean error = false;

    private Exception errorException;

    private int methodsCounter = 0;

    private ExtensionPointHandler extensionHandler = null;

    /**
     * Generates a test-class.
     * 
     * @param activeWorkbenchWindow
     * @param selection
     * @return true, if the test-class is successful generated. False otherwise.
     * @throws JUTException
     * @throws JUTWarning
     * @throws CoreException
     */
    public boolean generateTestclass(IWorkbenchWindow activeWorkbenchWindow,
	    IStructuredSelection selection) throws JUTException,
	    JUTWarning, CoreException {
	JUTElements jutElements = detectJUTElements(selection, null);
	return generateTestclass(activeWorkbenchWindow, jutElements);
    }

    /**
     * Generates a test-class.
     * 
     * @param activeWorkbenchWindow
     * @param fileEditorInput
     * @return true, if the test-class is successful generated. False otherwise.
     * @throws JUTException
     * @throws JUTWarning
     * @throws CoreException
     */
    public boolean generateTestclass(IWorkbenchWindow activeWorkbenchWindow,
	    IFileEditorInput fileEditorInput) throws JUTException,
	    JUTWarning, CoreException {
	JUTElements jutElements = detectJUTElements(null, fileEditorInput);
	return generateTestclass(activeWorkbenchWindow, jutElements);
    }
    
    protected ExtensionPointHandler getExtensionHandler() {
	if (extensionHandler == null) {
	    extensionHandler = Activator.getDefault().getExtensionHandler();
	}
	
	return extensionHandler;
    } 

    /**
     * Generates a test-class.
     * 
     * @param activeWorkbenchWindow
     * @param jutElements
     * @return true, if the test-class is successful generated. False otherwise.
     * @throws JUTException
     * @throws JUTWarning
     */
    protected boolean generateTestclass(
	    final IWorkbenchWindow activeWorkbenchWindow,
	    JUTElements jutElements) throws JUTException,
	    JUTWarning {
	if (jutElements == null) {
	    throw new JUTWarning(
		    "No elements found! Perhaps baseclass changed.");
	}

	JUTProjects projects = jutElements.getProjects();
	if (projects == null) {
	    throw new JUTWarning(
		    "No project found! Perhaps baseclass changed.");
	}

	try {

	    IJavaProject testProject = projects.getTestProject();

	    JUTClassesAndPackages classesAndPackages = jutElements
		    .getClassesAndPackages();
	    String testPackageName = classesAndPackages.getTestPackageName();
	    String testClassName = classesAndPackages.getTestClassName();
	    ICompilationUnit testClass = classesAndPackages.getTestClass();

	    boolean writeTML = JUTPreferences.isWriteTML();
	    Test tmlTest = null;

	    // TODO old
	    TMLProcessor tmlProcessor = null;
	    if (writeTML) {
		// get the TML-file
		if (testClass != null && testClass.exists()) {
		    tmlProcessor = new TMLProcessor(testProject,
			    testPackageName, testClassName);
		    tmlTest = tmlProcessor.readTmlFile();
		}
	    }

	    // create the model
	    final GeneratorModel model = new GeneratorModel(jutElements,
		    tmlTest);

	    // Open wizard
	    if (!runGeneratorWizard(model, activeWorkbenchWindow)) {
		return false;
	    }

	    // generate test-cases (in tml)
	    try {
		TestCasesGenerator tcg = new TestCasesGenerator();
		tcg.generateTestCases(model);
	    } catch (Exception ex) {
		// only log the exception
		logger.warning("Exception occured during the test-cases-generation! "
			+ ex.getMessage());
	    }

	    if (writeTML) {
		// update TML-file
		tmlProcessor.writeTmlFile(model.getTmlTest());
	    }

	    // save and close opened test-class-file
	    EclipseUIUtils.saveAndCloseEditor(testClassName);

	    // Generate test-elements
	    IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {

		@Override
		public void run(IProgressMonitor monitor) {
		    try {
			ICompilationUnit generatedClass = null;
			for (ITestClassGenerator testClassGenerator : getExtensionHandler().getTestClassGenerators()) {
			    generatedClass = testClassGenerator.generate(model,
				    getExtensionHandler().getTestDataFactories(), monitor);
			}
			setGeneratedTestClass(generatedClass);

			monitor.done();
		    } catch (Exception e) {
			setError(true, e);
		    }
		}
	    };

	    setError(false);
	    try {
		activeWorkbenchWindow.run(true, true, runnableWithProgress);
	    } catch (Exception ex) {
		throw new JUTException(ex);
	    }

	    if (isError()) {
		throw new JUTException(errorException);
	    }

	    // make source beautiful
	    IWorkbenchPartSite site = activeWorkbenchWindow.getActivePage()
		    .getActivePart().getSite();
	    EclipseUIUtils.organizeImports(site, testClass);
	    EclipseUIUtils.format(site, testClass);

	    // generate test-suites
	    Settings settings = model.getTmlTest().getSettings();
	    ArrayList<ICompilationUnit> generateTestSuites = null;
	    if (settings != null) {
		if (settings.isTestsuites()) {
		    generateTestSuites = generateTestSuites(jutElements);
		}
	    } else {
		generateTestSuites(jutElements);
	    }

	    if (generateTestSuites != null) {
		// make source beautiful
		for (ICompilationUnit cu : generateTestSuites) {

		    EclipseUIUtils.organizeImports(site, cu);
		}
		EclipseUIUtils
			.format(site,
				generateTestSuites
					.toArray(new ICompilationUnit[generateTestSuites
						.size()]));
	    }

	    // open in editor
	    openInEditor(activeWorkbenchWindow.getShell(),
		    (IFile) getGeneratedTestClass().getResource());

	} catch (JUTException ex) {
	    throw ex;
	} catch (Exception ex) {
	    throw new JUTException(ex);
	}

	return true;
    }

    protected void setError(boolean error, Exception ex) {
	setError(error);
	this.errorException = ex;
    }

    /**
     * Generates the test-suites
     * 
     * @param jutElements
     * @return
     * @throws CoreException
     */
    protected ArrayList<ICompilationUnit> generateTestSuites(
	    JUTElements jutElements) throws CoreException {

	for (ITestSuitesGenerator testSuitesGenerator : Activator.getDefault().getExtensionHandler().getTestSuitesGenerators()) {
	    testSuitesGenerator.generateTestSuites(jutElements);
	    return testSuitesGenerator.getGeneratedTestSuites();
	}

	return new ArrayList<ICompilationUnit>();
    }

    private ICompilationUnit getGeneratedTestClass() {
	return this.generatedTestClass;
    }

    protected void setGeneratedTestClass(ICompilationUnit generatedClass) {
	this.generatedTestClass = generatedClass;
    }

    protected void openInEditor(Shell shell, IFile generatedTestclass) {
	EclipseUIUtils.openInEditor(shell, generatedTestclass);
    }


    private JUTElements detectJUTElements(ISelection selection) throws JUTException, JUTWarning, CoreException {
	if (selection instanceof IStructuredSelection) {
	    return detectJUTElements((IStructuredSelection)selection, null);
	}
	else if (selection instanceof IFileEditorInput) {
	    return detectJUTElements(null, (IFileEditorInput)selection);
	}
	
	return null;
    }
    
    /**
     * @throws CoreException
     * @throws JavaModelException
     *             Detects all the necessary JUT-Elements: project, package and
     *             class for the base and test.
     * @return detected JUT-Elements
     * @throws JUTException
     * @throws JUTWarning
     * @throws
     */
    private JUTElements detectJUTElements(IStructuredSelection selection,
	    IFileEditorInput fileEditorInput) throws JUTException,
	    JUTWarning, CoreException {

	JUTElements jutElements = new JUTElements();

	// get active editor if nothing selected
	if ((selection == null || selection.isEmpty())
		&& fileEditorInput == null) {
	    IEditorInput editorInput = EclipseUIUtils.getEditorInput();

	    if (editorInput != null && editorInput instanceof IFileEditorInput) {
		fileEditorInput = (IFileEditorInput) editorInput;
	    } else {
		throw new JUTWarning(
			Messages.General_warning_nothing_selected);
	    }
	}

	// projects
	// get project via selection
	IJavaProject project = JDTUtils.getProject(selection, fileEditorInput);
	jutElements.initProjects(project);

	// classes and packages
	Vector<IJavaElement> elements = JDTUtils.getCompilationUnits(
		selection, fileEditorInput);

	Vector<ICompilationUnit> cuList;
	cuList = new Vector<ICompilationUnit>();

	for (IJavaElement element : elements) {
	    if (element instanceof ICompilationUnit) {
		cuList.add((ICompilationUnit) element);
	    }
	}

	if (cuList.size() > 0) {
	    jutElements.initClassesAndPackages(cuList);

	    if (!jutElements.getClassesAndPackages().getBaseClass().exists()) {
		return jutElements;
	    }

	    // base-class constructors and methods
	    jutElements
		    .setConstructorsAndMethods(getConstructorsAndMethods(jutElements
			    .getClassesAndPackages().getBaseClass()));

	    // set selected method
	    IMethod selectedMethod = null;

	    if (selection != null) {
		Object firstElement = selection.getFirstElement();

		if (firstElement != null && firstElement instanceof IMethod) {
		    selectedMethod = (IMethod) firstElement;
		}
	    } else if (fileEditorInput != null) {
		selectedMethod = JDTUtils
			.getSelectedMethod(fileEditorInput);
	    }

	    jutElements.getConstructorsAndMethods().setSelectedMethod(
		    selectedMethod);

	}

	return jutElements;
    }

    private JUTConstructorsAndMethods getConstructorsAndMethods(
	    ICompilationUnit baseclass) {

	Vector<IMethod> baseclassConstructors = new Vector<IMethod>();
	Vector<IMethod> baseclassMethods = new Vector<IMethod>();

	try {
	    for (IType type : baseclass.getTypes()) {
		for (IMethod method : type.getMethods()) {
		    if (method.isConstructor())
			baseclassConstructors.add(method);
		    else
			baseclassMethods.add(method);
		}
	    }
	} catch (JavaModelException e) {
	    throw new RuntimeException(e);
	}

	JUTConstructorsAndMethods constructorsAndMethods = new JUTConstructorsAndMethods();
	constructorsAndMethods.setBaseClassConstructors(baseclassConstructors);
	constructorsAndMethods.setBaseClassMethods(baseclassMethods);

	return constructorsAndMethods;
    }

    /**
     * Runs the generator-wizard.
     * 
     * @param model
     * @param workbenchPart
     * @return boolean true
     */
    protected boolean runGeneratorWizard(GeneratorModel model,
	    IWorkbenchWindow workbenchPart) {

	GeneratorWizard wizard = new GeneratorWizard(model);

	WizardDialog dialog = new WizardDialog(workbenchPart.getShell(), wizard);
	dialog.create();

	wizard.initPages();

	dialog.open();

	if (wizard.isFinished()) {
	    return true;
	}

	return false;
    }

    /**
     * Indicates if an error occurred.
     * 
     * @return true if an error occurred
     */
    public boolean isError() {
	return error;
    }

    /**
     * Sets the error-flag.
     * 
     * @param error
     */
    public void setError(boolean error) {
	this.error = error;
    }

    public boolean generateTestSuites(IJavaProject testProject)
	    throws CoreException, JUTWarning {
	if (testProject == null) {
	    return false;
	}

	String testProjectPostfix = JUTPreferences.getTestProjectPostfix();

	String testProjectName = testProject.getElementName();

	if (!testProjectName.endsWith(testProjectPostfix)) {
	    throw new JUTWarning("Select a test-project!");
	}

	for (ITestSuitesGenerator testSuitesGenerator : Activator.getDefault().getExtensionHandler().getTestSuitesGenerators()) {
	    if (!testSuitesGenerator.generateTestSuites(testProject)) {
		return false;
	    }
	}

	return true;
    }

    /**
     * switches between test-class and test-subject
     * 
     * @throws CoreException
     */
    public boolean switchClass(IWorkbenchWindow activeWorkbenchWindow,
	    IStructuredSelection selection) throws JUTException,
	    JUTWarning, CoreException {
	JUTElements uTMElements = detectJUTElements(selection, null);
	if (uTMElements == null) {
	    return false;
	}

	return switchClass(activeWorkbenchWindow, uTMElements);
    }

    /**
     * switches between test-class and test-subject
     * 
     * @throws CoreException
     */
    public boolean switchClass(IWorkbenchWindow activeWorkbenchWindow,
	    IFileEditorInput fileEditorInput) throws JUTException,
	    JUTWarning, CoreException {
	JUTElements uTMElements = detectJUTElements(null, fileEditorInput);
	if (uTMElements == null) {
	    return false;
	}
	return switchClass(activeWorkbenchWindow, uTMElements);
    }

    private boolean switchClass(IWorkbenchWindow activeWorkbenchWindow,
	    JUTElements uTMElements) throws JUTException,
	    JUTWarning, JavaModelException {
	IMethod selectedMethod = uTMElements.getConstructorsAndMethods()
		.getSelectedMethod();
	JUTProjects projects = uTMElements.getProjects();
	JUTClassesAndPackages classesAndPackages = uTMElements
		.getClassesAndPackages();
	ICompilationUnit classToOpen;
	Shell shell = activeWorkbenchWindow.getShell();

	MethodRef mr = null;
	if (projects.isBaseProjectSelected()) {
	    classToOpen = classesAndPackages.getTestClass();

	    if (selectedMethod != null) {
		mr = new MethodRef(
			GeneratorUtils.createTestMethodName(selectedMethod
				.getElementName()),
			selectedMethod.getSignature());
	    }
	} else {
	    classToOpen = classesAndPackages.getBaseClass();

	    if (selectedMethod != null) {
		mr = GeneratorUtils.getMethodRef(selectedMethod);
	    }
	}

	if (classToOpen != null && classToOpen.exists()) {
	    EclipseUIUtils.openInEditor(shell,
		    (IFile) classToOpen.getResource());

	    if (selectedMethod != null) {
		EclipseUIUtils.selectMethodInEditor(mr);
	    }

	    return true;
	} else {
	    if (projects.isBaseProjectSelected()) {
		boolean result = MessageDialog
			.openConfirm(
				shell,
				"Neue Testklasse generieren?",
				"Es konnte keine bestehende Testklasse gefunden werden. M�chten Sie eine neue Testklasse generieren?");

		if (result) {
		    generateTestclass(activeWorkbenchWindow, uTMElements);
		}
	    } else {
		return false;
	    }
	}

	return true;
    }

    public void createReport(IWorkbenchWindow workbenchWindow,
	    final ISelection selection) throws JUTException,
	    JUTWarning, CoreException {
	setError(false);
	setWarning(null);
	
	IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {

	    @Override
	    public void run(IProgressMonitor monitor) {
		try {
		    JUTElements jutElements = detectJUTElements(selection);
		    if (jutElements == null) {
			setWarning(new JUTWarning(Messages.GeneratorUtils_SelectionNotSupported));
			return;
		    }
		    
		    ReportCreator rc = new ReportCreator();
		    rc.createNecessaryTestclassesReport(jutElements, monitor);
		    monitor.done();
		} catch (Exception e) {
		    setError(true, e);
		}
	    }
	};

	try {
	    workbenchWindow.run(true, true, runnableWithProgress);
	} catch (Exception ex) {
	    throw new JUTException(ex);
	}

	if (isWarning()) {
	    throw warningException;
	}
	else if (isError()) {
	    throw new JUTException(errorException);
	}

    }

    protected void setWarning(JUTWarning jutWarning) {
	warningException = jutWarning;
	
	if (jutWarning == null) {
	    warning = false;
	}
	else {
	    warning = true;
	}
    }
    
    private boolean isWarning() {
	return warning;
    }

    public int countAllTestMethods(final IWorkbenchWindow workbenchWindow,
	    final IStructuredSelection selection) throws JUTException {
	IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {

	    @Override
	    public void run(IProgressMonitor monitor) {
		try {
		    setError(false);
		    JUTElements jutElements = detectJUTElements(selection, null);
		    ReportCreator rc = new ReportCreator();
		    int methodsCounter = rc.countAllTestMethods(jutElements,
			    monitor);
		    setMethodsCounter(methodsCounter);
		    monitor.done();
		} catch (Exception e) {
		    setError(true, e);
		}
	    }
	};

	try {
	    workbenchWindow.run(true, true, runnableWithProgress);
	} catch (Exception ex) {
	    throw new JUTException(ex);
	}

	if (isError()) {
	    throw new JUTException(errorException);
	}

	return this.methodsCounter;
    }

    protected void setMethodsCounter(int methodsCounter) {
	this.methodsCounter = methodsCounter;
    }

}
