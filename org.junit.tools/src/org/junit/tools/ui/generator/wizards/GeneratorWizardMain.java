package org.junit.tools.ui.generator.wizards;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.junit.tools.base.MethodRef;
import org.junit.tools.generator.IGeneratorConstants;
import org.junit.tools.generator.model.GeneratorModel;
import org.junit.tools.generator.model.tml.Method;
import org.junit.tools.generator.model.tml.Param;
import org.junit.tools.generator.model.tml.Result;
import org.junit.tools.generator.model.tml.Settings;
import org.junit.tools.generator.model.tml.Test;
import org.junit.tools.generator.model.tml.Testprio;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.preferences.JUTPreferences;
import org.junit.tools.ui.generator.swt.control.GroupMethodSelectionCtrl;
import org.junit.tools.ui.generator.wizards.pages.GeneratorWizardMainPage;
import org.junit.tools.ui.utils.EclipseUIUtils;

/**
 * The controller for the main page.
 * 
 * @author JUnit-Tools-Team
 * 
 */
public class GeneratorWizardMain extends GeneratorWizardBase implements
	IGeneratorConstants, IMethodeSelectionChangedListener {

    private final ICompilationUnit testBase;

    private final ICompilationUnit testClass;

    private GroupMethodSelectionCtrl methodSelection;

    private Vector<IMethod> checkedMethods = new Vector<IMethod>(0);

    private IJavaProject selectedTestProject;

    /**
     * Constructor
     * 
     * @param model
     * @param generatorWizardMainPage
     */
    public GeneratorWizardMain(GeneratorModel model,
	    GeneratorWizardMainPage generatorWizardMainPage) {
	super(model, generatorWizardMainPage);
	testBase = getModel().getJUTElements().getClassesAndPackages()
		.getBaseClass();
	testClass = getModel().getJUTElements().getClassesAndPackages()
		.getTestClass();

	selectedTestProject = getModel().getJUTElements().getProjects()
		.getTestProject();

    }

    @Override
    public GeneratorWizardMainPage getPage() {
	return (GeneratorWizardMainPage) super.getPage();
    }

    /**
     * Adds the listener to the page elements.
     * 
     * @param page
     */
    private void addListener(GeneratorWizardMainPage page) {

	// toggle buttons
	page.getView().getBtnToggleStandardMethods()
		.addSelectionListener(new SelectionAdapter() {

		    @Override
		    public void widgetSelected(SelectionEvent e) {
			handleToggleStandardMethods();
		    }
		});

	page.getView().getBtnToggleOther()
		.addSelectionListener(new SelectionAdapter() {

		    @Override
		    public void widgetSelected(SelectionEvent e) {
			handleToggleOther();
		    }
		});

	page.getView().getBtnTestProject()
		.addSelectionListener(new SelectionAdapter() {

		    @Override
		    public void widgetSelected(SelectionEvent e) {
			super.widgetSelected(e);
			handleTestProject();
		    }
		});

	if (page.getView().getBtnSuperClass() != null) {
	    page.getView().getBtnSuperClass()
		    .addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			    super.widgetSelected(e);
			    handleSuperClass();
			}
		    });
	}

	methodSelection.addListener(this);
    }

    protected void handleTestProject() {
	IJavaProject project = EclipseUIUtils
		.getJavaProjectFromDialog(getPage().getShell());

	if (project != null) {
	    getPage().getView().getTxtTestProject()
		    .setText(project.getElementName());
	    getPage().getView().getTxtTestProject().setData(project);
	    selectedTestProject = project;
	}

    }

    protected void handleSuperClass() {
	SelectionDialog dialog;
	try {
	    String filter = "";
	    Object superClass = getPage().getView().getTxtSuperClass()
		    .getData();
	    if (superClass instanceof IType) {
		filter = ((IType) superClass).getFullyQualifiedName();
	    }

	    dialog = JavaUI
		    .createTypeDialog(getPage().getShell(), getPage()
			    .getWizard().getContainer(), SearchEngine
			    .createWorkspaceScope(),
			    IJavaElementSearchConstants.CONSIDER_CLASSES,
			    false, filter);
	} catch (JavaModelException e) {
	    return;
	}

	if (dialog.open() == Dialog.OK) {
	    Object[] results = dialog.getResult();
	    if (results.length > 0) {
		for (Object result : results) {
		    if (result instanceof IType) {
			IType superClass = ((IType) result);
			getPage().getView().getTxtSuperClass()
				.setText(superClass.getFullyQualifiedName());
			getPage().getView().getTxtSuperClass()
				.setData(superClass);
			return;
		    }
		}
	    }
	}
    }

    /**
     * Handle the toggle button for the other
     */
    protected void handleToggleOther() {
	toggleButton(getPage().getView().getBtnTestsuites());
	toggleButton(getPage().getView().getBtnLogger());
	toggleButton(getPage().getView().getBtnFailassertion());
    }

    /**
     * Handle the toggle button for the methods
     */
    protected void handleToggleStandardMethods() {
	toggleButton(getPage().getView().getBtnSetup());
	toggleButton(getPage().getView().getBtnSetupbeforeclass());
	toggleButton(getPage().getView().getBtnTeardown());
	toggleButton(getPage().getView().getBtnTeardownafterclass());
    }

    /**
     * Toggles the button.
     */
    private void toggleButton(Button btn) {
	boolean selection = !btn.getSelection();
	btn.setSelection(selection);
    }

    /**
     * Is called if the method selection changed.
     */
    @Override
    public void methodSelectionChanged(Vector<IMethod> chkdMethods) {
	this.checkedMethods = chkdMethods;

	if (checkedMethods.size() == 0) {
	    getPage().updateStatus("Select at least one method!");
	    return;
	}

	getPage().getView().getCheckboxTreeViewer().expandAll();

	getPage().updateStatus(null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.junit.tools.ui.generator.wizards.IMethodeSelectionChangedListener#selectedMethodChecked(org.eclipse.jdt.core.IMethod)
     */
    @Override
    public void selectedMethodChecked(IMethod selectedMethod) {
	String description = getPage().getDescription();
	if (description == null) {
	    description = "";
	}

	if (!"".equals(description.trim())) {
	    description += RETURN;
	}

	description += "Method " + selectedMethod.getElementName()
		+ " automatically checked!";

	getPage().setDescription(description);
    }

    public Vector<IMethod> getCheckedMethods() {
	return checkedMethods;
    }

    @Override
    public void initPage() {

	GeneratorWizardMainPage page = getPage();
	Test tmlTest = getModel().getTmlTest();
	List<Method> tmlMethods = null;
	initDefaults(page);

	if (tmlTest != null) {
	    tmlMethods = tmlTest.getMethod();

	    // initialize settings
	    initPageSettings(page, tmlTest.getSettings());

	    initTestprio(page, tmlTest);
	}

	methodSelection = new GroupMethodSelectionCtrl();

	// add listener (before initializing the method-selection)
	addListener(page);

	try {
	    boolean writeTML = JUTPreferences.isWriteTML();
	    if (writeTML) {
		methodSelection.init(page.getView().getMethodSelectionGroup(),
			testBase, tmlMethods, getModel());
	    } else {
		methodSelection.init(page.getView().getMethodSelectionGroup(),
			testBase, testClass, getModel());
	    }

	    methodSelectionChanged(methodSelection.getCheckedMethods());
	    methodSelection.deactivateFilters();
	} catch (JavaModelException e) {
	    // TODO fehlermeldung
	}
    }

    /**
     * Initializes the defaults.
     * 
     * @param page
     */
    private void initDefaults(GeneratorWizardMainPage page) {
	String testProjectPostfix = JUTPreferences.getTestProjectPostfix();
	String baseProjectName = getModel().getJUTElements().getProjects()
		.getBaseProject().getElementName();
	String testProjectName = baseProjectName + testProjectPostfix;

	page.getView().getTxtTestProject().setText(testProjectName);

	if (page.getView().getTxtSuperClass() != null) {
	    page.getView().getTxtSuperClass()
		    .setText(JUTPreferences.getTestClassSuperType());
	}

	page.getView().getMethodPrefix()
		.setText(JUTPreferences.getTestMethodPrefix());
	page.getView().getBtnTestsuites().setSelection(true);
    }

    /**
     * Initializes the page settings.
     * 
     * @param page
     * @param settings
     */
    private void initPageSettings(GeneratorWizardMainPage page,
	    Settings settings) {
	if (settings == null) {
	    return;
	}

	// standard methods
	page.getView().getBtnSetup().setSelection(settings.isSetUp());
	page.getView().getBtnSetupbeforeclass()
		.setSelection(settings.isSetUpBeforeClass());
	page.getView().getBtnTeardown().setSelection(settings.isTearDown());
	page.getView().getBtnTeardownafterclass()
		.setSelection(settings.isTearDownBeforeClass());

	// other
	page.getView().getBtnTestsuites().setSelection(settings.isTestsuites());
	page.getView().getBtnFailassertion()
		.setSelection(settings.isFailAssertions());
	page.getView().getBtnLogger().setSelection(settings.isLogger());

    }

    /**
     * Initializes the test priority.
     * 
     * @param page
     * @param tmlTest
     */
    private void initTestprio(GeneratorWizardMainPage page, Test tmlTest) {
	page.getView().getBtnPrioHigh().setSelection(false);
	page.getView().getBtnPrioStandard().setSelection(false);
	page.getView().getBtnPrioLow().setSelection(false);

	Testprio testprio = tmlTest.getTestPrio();

	if (testprio != null) {
	    switch (testprio) {
	    case HIGH:
		page.getView().getBtnPrioHigh().setSelection(true);
		break;
	    case DEFAULT:
		page.getView().getBtnPrioStandard().setSelection(true);
		break;
	    case LOW:
		page.getView().getBtnPrioLow().setSelection(true);
		break;
	    default:
		page.getView().getBtnPrioStandard().setSelection(true);
		break;
	    }
	} else {
	    page.getView().getBtnPrioStandard().setSelection(true);
	}
    }

    @Override
    public void updateModel() {
	Test tmlTest = getModel().getTmlTest();
	GeneratorWizardMainPage page = getPage();

	// test
	if (tmlTest == null) {
	    tmlTest = getObjectFactory().createTest();
	    getModel().setTmlTest(tmlTest);
	}

	tmlTest.setTestBase(testBase.getPath().toString());

	if (testClass != null) {
	    tmlTest.setTestClass(testClass.getPath().toString());
	}

	// TML-version
	tmlTest.setVersion(TML_VERSION_ACTUAL);

	// test-priority
	if (page.getView().getBtnPrioHigh().getSelection()) {
	    tmlTest.setTestPrio(Testprio.HIGH);
	} else if (page.getView().getBtnPrioStandard().getSelection()) {
	    tmlTest.setTestPrio(Testprio.DEFAULT);
	} else if (page.getView().getBtnPrioLow().getSelection()) {
	    tmlTest.setTestPrio(Testprio.LOW);
	}

	Text txtSuperClass = page.getView().getTxtSuperClass();
	if (txtSuperClass != null) {
	    Object data = txtSuperClass.getData();
	    if (data != null && data instanceof IType) {
		tmlTest.setSuperClassPackage(((IType) data)
			.getPackageFragment().getElementName());
		tmlTest.setSuperClass(((IType) data).getElementName());
	    } else {
		tmlTest.setSuperClass(txtSuperClass.getText());
	    }
	}

	updateModelSettings(page, tmlTest);

	// methods
	try {
	    updateModelMethods(tmlTest);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Updates the settings in the model from the page.
     * 
     * @param page
     * @param tmlTest
     */
    private void updateModelSettings(GeneratorWizardMainPage page, Test tmlTest) {
	Settings settings = tmlTest.getSettings();

	if (settings == null) {
	    settings = getObjectFactory().createSettings();
	    tmlTest.setSettings(settings);
	}

	// standard methods
	settings.setSetUp(page.getView().getBtnSetup().getSelection());
	settings.setSetUpBeforeClass(page.getView().getBtnSetupbeforeclass()
		.getSelection());
	settings.setTearDown(page.getView().getBtnTeardown().getSelection());
	settings.setTearDownBeforeClass(page.getView()
		.getBtnTeardownafterclass().getSelection());

	// other
	settings.setTestsuites(page.getView().getBtnTestsuites().getSelection());
	settings.setLogger(page.getView().getBtnLogger().getSelection());
	settings.setFailAssertions(page.getView().getBtnFailassertion()
		.getSelection());
    }

    /**
     * Updates the methods from the page.
     * 
     * @param tmlTest
     * @throws JavaModelException
     */
    private void updateModelMethods(Test tmlTest) throws JavaModelException {
	Method tmlMethod;

	// delete old methods
	tmlTest.getMethod().clear();

	// add methods
	HashMap<IMethod, Method> methodMap = new HashMap<IMethod, Method>();
	getModel().setMethodMap(methodMap);

	for (IMethod method : methodSelection.getCheckedMethods()) {
	    tmlMethod = getObjectFactory().createMethod();
	    tmlTest.getMethod().add(tmlMethod);

	    updateModelMethod(method, tmlMethod);

	    // save in method-map
	    methodMap.put(method, tmlMethod);
	}

	HashMap<MethodRef, IMethod> existingMethods = methodSelection
		.getExistingMethods();

	getModel().setExistingMethods(existingMethods);
	getModel().setMethodsToCreate(
		GeneratorUtils.getMethodsToCreate(existingMethods,
			checkedMethods));
	getModel().setMethodsToDelete(
		GeneratorUtils.getMethodsToDelete(existingMethods,
			checkedMethods));
    }

    /**
     * Updates the method from the page.
     * 
     * @param method
     * @param tmlMethod
     * @throws JavaModelException
     */
    public void updateModelMethod(IMethod method, Method tmlMethod)
	    throws JavaModelException {
	Result result;
	Param param;
	String returnType;

	tmlMethod.setName(method.getElementName());
	tmlMethod.setModifier(JDTUtils.getMethodModifier(method));
	tmlMethod.setStatic(JDTUtils.isStatic(method));
	tmlMethod.setSignature(method.getSignature());

	// parameters
	ILocalVariable[] parameters = method.getParameters();

	for (ILocalVariable parameter : parameters) {
	    param = getObjectFactory().createParam();
	    param.setName(parameter.getElementName());
	    param.setType(Signature.getSignatureSimpleName(parameter
		    .getTypeSignature()));

	    tmlMethod.getParam().add(param);
	}

	// return type
	returnType = method.getReturnType();
	if (returnType != null && !returnType.equals("V")) {
	    result = getObjectFactory().createResult();
	    result.setName("result");
	    result.setType(Signature.getSignatureSimpleName(returnType));
	    tmlMethod.setResult(result);
	}
    }

    public IJavaProject getSelectedProject() {
	return selectedTestProject;
    }

}
