package org.junit.tools.ui.generator.swt.control;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.junit.tools.base.MethodRef;
import org.junit.tools.generator.IGeneratorConstants;
import org.junit.tools.generator.model.GeneratorModel;
import org.junit.tools.generator.model.JUTElements;
import org.junit.tools.generator.model.JUTElements.JUTConstructorsAndMethods;
import org.junit.tools.generator.model.tml.Method;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.preferences.JUTPreferences;
import org.junit.tools.ui.generator.swt.view.GroupMethodSelectionView;
import org.junit.tools.ui.generator.wizards.IMethodeSelectionChangedListener;

public class GroupMethodSelectionCtrl implements IGeneratorConstants {

    private final Logger logger = Logger
	    .getLogger(GroupMethodSelectionCtrl.class.getName());

    private ViewerFilterMethods viewerFilterMethods;

    private final Vector<IMethod> checkedMethods = new Vector<IMethod>();

    private GroupMethodSelectionView group;

    private final Vector<IMethodeSelectionChangedListener> listeners = new Vector<IMethodeSelectionChangedListener>();

    // filter-instances for the methods
    private final Vector<String> nameFilterStartsWith = new Vector<String>();

    private final Vector<String> nameFilterEquals = new Vector<String>();

    private final Vector<String> nameFilterEndsWith = new Vector<String>();

    private boolean modifierFilterSelected = true;

    private boolean nameFilterSelected = true;

    private boolean existingMethodsFilterSelected = true;

    // checked methods
    private Vector<IMethod> baseClassMethods = null;

    private GeneratorModel model;

    private IJavaElement treeClass;

    private HashMap<MethodRef, IMethod> existingMethods;

    /**
     * The filter for the methods.
     * 
     * @author Robert Streng
     */
    private class ViewerFilterMethods extends ViewerFilter {

	private final List<Method> tmlMethods;

	public ViewerFilterMethods(List<Method> tmlMethods) {
	    this.tmlMethods = tmlMethods;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement,
		Object element) {

	    IMethod method;
	    if (element instanceof IMethod) {
		method = (IMethod) element;

		try {
		    if (method.isConstructor()) {
			return false;
		    }

		    if (isExistingMethodsFilterSelected()) {
			if (JUTPreferences.isWriteTML()) {
			    if (tmlMethods != null
				    && containMethod(method, tmlMethods)) {
				return false;
			    }
			} else {
			    // if (clazz != null) { // && containMethod(method,
			    // clazz)) {
			    // return false;
			    // }
			}

			if (existingMethods != null
				&& existingMethods.size() > 0) {
			    if (GeneratorUtils.findMethod(
				    existingMethods.keySet(), method) != null) {
				return false;
			    }
			}
		    }

		    if (!isMethodAllowed(method, isNameFilterSelected(),
			    isModifierFilterSelected())) {
			return false;
		    }

		} catch (JavaModelException e) {
		    throw new RuntimeException(e);
		}

		return true;
	    } else if (element instanceof IType
		    && !(parentElement instanceof IType)) {
		return true;
	    }

	    return false;
	}
    }

    /**
     * The check state provider for the methods.
     * 
     * @author Robert Streng
     */
    class CheckStateProvider implements ICheckStateProvider {

	@Override
	public boolean isChecked(Object element) {
	    if (getCheckedMethods().contains(element)) {
		return true;
	    }

	    if (element instanceof IJavaElement) {
		IJavaElement javaElement = (IJavaElement) element;
		if (isParentChecked(javaElement.getParent())) {
		    return true;
		}
	    }

	    return false;
	}

	private boolean isParentChecked(IJavaElement element) {
	    if (element == null) {
		return false;
	    }
	    if (element instanceof IMethod) {
		if (getCheckedMethods().contains(element)) {
		    return true;
		}
	    }

	    return isParentChecked(element.getParent());
	}

	@Override
	public boolean isGrayed(Object element) {
	    return getGroup().getCheckboxTreeViewer().getGrayed(element);
	}
    }

    private void addInternalListener(GroupMethodSelectionView grp) {
	grp.getBtnSelectAll().addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		try {
		    handleSelectAll();
		} catch (JavaModelException e1) {
		    logger.log(Level.SEVERE, e1.getMessage());
		}
	    }
	});

	grp.getBtnDeselectAll().addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		try {
		    handleDeselectAll();
		} catch (JavaModelException e1) {
		    logger.log(Level.SEVERE, e1.getMessage());
		}
	    }
	});

	grp.getBtnNamefilter().addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		handleNameFilterSelection();
	    }
	});

	grp.getBtnModifierfilter().addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		handleModifierFilterSelection();
	    }
	});

	grp.getBtnExistingMethodsFilter().addSelectionListener(
		new SelectionAdapter() {

		    @Override
		    public void widgetSelected(SelectionEvent e) {
			handleExistingMethodsFilterSelection();
		    }
		});

	grp.getCheckboxTreeViewer().addCheckStateListener(
		new ICheckStateListener() {

		    @Override
		    public void checkStateChanged(CheckStateChangedEvent event) {
			Object element = event.getElement();

			try {
			    if (element instanceof IMethod) {
				handleMethodSelection(event.getChecked(),
					(IMethod) element);
			    } else if (element instanceof IType) {
				if (event.getChecked()) {
				    handleSelectAll();
				} else {
				    handleDeselectAll();
				}
			    }
			} catch (JavaModelException e) {
			    // nothing
			}

			methodSelectionChanged();
		    }

		    private void handleMethodSelection(boolean checked,
			    IMethod element) throws JavaModelException {
			if (element.isConstructor()) {
			    return;
			}

			if (checked) {
			    if (!checkedMethods.contains(element)) {
				checkedMethods.add(element);
			    }
			} else {
			    checkedMethods.remove(element);
			}

		    }
		});

    }

    public void deactivateFilters() {
	group.getBtnExistingMethodsFilter().setSelection(false);
	group.getBtnModifierfilter().setSelection(false);
	group.getBtnNamefilter().setSelection(false);

	handleExistingMethodsFilterSelection();
	handleModifierFilterSelection();
	handleNameFilterSelection();
    }

    public void init(GroupMethodSelectionView grp, IJavaElement javaElement,
	    List<Method> tmlMethods, GeneratorModel utmModel)
	    throws JavaModelException {
	this.viewerFilterMethods = new ViewerFilterMethods(tmlMethods);
	initReal(grp, javaElement, utmModel);
    }

    public void init(GroupMethodSelectionView grp, IJavaElement javaElement,
	    ICompilationUnit testClass, GeneratorModel utmModel)
	    throws JavaModelException {
	initReal(grp, javaElement, utmModel);
    }

    public void init(GroupMethodSelectionView grp, IJavaElement javaElement)
	    throws JavaModelException {
	initReal(grp, javaElement, null);
    }

    private void initReal(GroupMethodSelectionView grp,
	    IJavaElement javaElement, GeneratorModel utmModel)
	    throws JavaModelException {
	this.group = grp;
	this.model = utmModel;
	this.treeClass = javaElement;

	if (viewerFilterMethods == null) {
	    this.viewerFilterMethods = new ViewerFilterMethods(null);
	    group.getCheckboxTreeViewer().addFilter(viewerFilterMethods);
	}

	group.getCheckboxTreeViewer().setCheckStateProvider(
		new CheckStateProvider());
	group.getCheckboxTreeViewer().setInput(javaElement);
	group.getCheckboxTreeViewer().expandAll();
	group.getCheckboxTreeViewer().setComparator(new ViewerComparator() {

	    @Override
	    public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof IMethod && e2 instanceof IMethod) {
		    IMethod m1, m2;
		    m1 = (IMethod) e1;
		    m2 = (IMethod) e2;

		    return m1.getElementName().compareTo(m2.getElementName());
		}

		return super.compare(viewer, e1, e2);
	    }
	});

	initNameFilters();

	// initialize existing methods
	initCheckedMethods();

	group.getCheckboxTreeViewer().refresh();

	addInternalListener(grp);
    }

    /**
     * Selects or deselects all methods.
     * 
     * @param state
     * @throws JavaModelException
     */
    private void selectAll(boolean state) {
	CheckboxTreeViewer checkboxTreeViewer = getGroup()
		.getCheckboxTreeViewer();
	ITreeContentProvider contentProvider = (ITreeContentProvider) checkboxTreeViewer
		.getContentProvider();
	Object[] objects = contentProvider.getElements(checkboxTreeViewer
		.getInput());

	for (Object object : objects) {
	    checkboxTreeViewer.setChecked(object, state);
	}

	for (IMethod method : getBaseClassMethods()) {
	    if (viewerFilterMethods.select(null, method.getParent(), method)) {
		if (state) {
		    checkedMethods.add(method);
		} else {
		    checkedMethods.remove(method);
		}
	    }
	}
    }

    /**
     * Handle the select all action.
     * 
     * @throws JavaModelException
     */
    private void handleSelectAll() throws JavaModelException {
	selectAll(true);
	methodSelectionChanged();
    }

    private void methodSelectionChanged() {
	for (IMethodeSelectionChangedListener listener : listeners) {
	    listener.methodSelectionChanged(getCheckedMethods());
	}
    }

    private void selectedMethodChecked(IMethod method) {
	for (IMethodeSelectionChangedListener listener : listeners) {
	    listener.selectedMethodChecked(method);
	}
    }

    /**
     * Handle the deselect all action.
     * 
     * @throws JavaModelException
     */
    private void handleDeselectAll() throws JavaModelException {
	selectAll(false);
	methodSelectionChanged();
    }

    /**
     * Handle the name filter selecion.
     */
    private void handleNameFilterSelection() {
	nameFilterSelected = getGroup().getBtnNamefilter().getSelection();
	getGroup().getCheckboxTreeViewer().refresh();
	methodSelectionChanged();
    }

    /**
     * Handle the modifier filer selection.
     */
    private void handleModifierFilterSelection() {
	modifierFilterSelected = getGroup().getBtnModifierfilter()
		.getSelection();
	getGroup().getCheckboxTreeViewer().refresh();
	methodSelectionChanged();
    }

    /**
     * Handle the existing methods filter selection.
     */
    private void handleExistingMethodsFilterSelection() {
	existingMethodsFilterSelected = getGroup()
		.getBtnExistingMethodsFilter().getSelection();
	getGroup().getCheckboxTreeViewer().refresh();
	methodSelectionChanged();
    }

    /**
     * Returns the checked methods.
     * 
     * @return checked methods
     */
    public Vector<IMethod> getCheckedMethods() {
	return checkedMethods;
    }

    /**
     * Returns if the name filter is selected.
     * 
     * @return true if the name filter is selected
     */
    protected boolean isNameFilterSelected() {
	return nameFilterSelected;
    }

    /**
     * Returns if the modifier filter is selected.
     * 
     * @return true if modifier filter is selected
     */
    protected boolean isModifierFilterSelected() {
	return modifierFilterSelected;
    }

    /**
     * Returns if the existing methods filter is selected.
     * 
     * @return true if the existing methods filter is selected
     */
    protected boolean isExistingMethodsFilterSelected() {
	return existingMethodsFilterSelected;
    }

    private GroupMethodSelectionView getGroup() {
	return group;
    }

    /**
     * Returns if the method is in the method list and add the method if not
     * exists.
     * 
     * @param method
     * @param tmlMethods
     * @return true if method is in the method list
     */
    protected boolean containMethod(IMethod method, List<Method> tmlMethods) {
	HashMap<IMethod, Method> methodMap = getModel().getMethodMap();

	if (methodMap.containsKey(method)) {
	    if (methodMap.get(method) != null)
		return true;
	    else
		return false;
	}

	Method tmlMethod = GeneratorUtils.getClosestMethod(method, tmlMethods);
	if (tmlMethod != null) {
	    methodMap.put(method, tmlMethod);
	    return true;
	}

	methodMap.put(method, null);
	return false;
    }

    /**
     * Initializes the existing methods.
     * 
     * @throws JavaModelException
     */
    private void initCheckedMethods() throws JavaModelException {
	// initialisierung via tml-model
	if (getModel() != null && getModel().getTmlTest() != null) {
	    for (IMethod baseClassMethod : getBaseClassMethods()) {
		if (containMethod(baseClassMethod, getModel().getTmlTest()
			.getMethod())) {
		    checkedMethods.add(baseClassMethod);
		}
	    }

	    return;
	}

	// initialize existing methods
	if (existingMethods == null && getModel() != null) {
	    JUTElements utmElements = getModel().getJUTElements();
	    ICompilationUnit baseClass = utmElements.getClassesAndPackages()
		    .getBaseClass();
	    ICompilationUnit testClass = utmElements.getClassesAndPackages()
		    .getTestClass();

	    existingMethods = GeneratorUtils.getExistingTestMethods(baseClass,
		    testClass, true);

	    // add selected base method
	    if (utmElements.getProjects().isBaseProjectSelected()) {
		JUTConstructorsAndMethods constructorsAndMethods = utmElements
			.getConstructorsAndMethods();
		IMethod selectedMethod = constructorsAndMethods
			.getSelectedMethod();
		if (selectedMethod != null) {
		    checkedMethods.add(selectedMethod);
		    selectedMethodChecked(selectedMethod);
		}
	    }
	}

	// initialization via methodref-annotation or via name if the
	// method-name is unique
	boolean nameMatched;
	String signatureToCompare;
	if (existingMethods != null && existingMethods.size() > 0) {
	    List<IMethod> methods;
	    try {
		methods = JDTUtils.getMethods(treeClass, false);
		for (IMethod method : methods) {

		    nameMatched = false;
		    for (MethodRef methodRef : existingMethods.keySet()) {
			if (methodRef.isSignatureChanged()) {
			    signatureToCompare = methodRef.getSignatureNew();
			} else {
			    signatureToCompare = methodRef.getSignature();
			}

			if (methodRef.getName().equals(method.getElementName())) {
			    if (signatureToCompare
				    .equals(method.getSignature())) {
				checkedMethods.add(method);
				break;
			    } else {
				nameMatched = true;
			    }
			}
		    }

		    if (nameMatched && JDTUtils.isMethodNameUnique(method)) {
			checkedMethods.add(method);
		    }

		}
	    }

	    catch (JavaModelException e) {
		// TODO
		logger.warning(e.getMessage());
	    }
	}

    }

    /**
     * @return base class methods
     * @throws JavaModelException
     */
    private Vector<IMethod> getBaseClassMethods() {
	if (getModel() == null) {
	    try {
		return new Vector<IMethod>(
			JDTUtils.getMethods(treeClass, false));
	    } catch (JavaModelException e) {
		logger.log(Level.SEVERE, e.getMessage());
	    }

	    return new Vector<IMethod>(0);
	}

	if (baseClassMethods == null) {
	    baseClassMethods = getModel().getJUTElements()
		    .getConstructorsAndMethods().getBaseClassMethods();
	}

	return baseClassMethods;
    }

    /**
     * Initializes the name filters.
     */
    private void initNameFilters() {
	for (String filter : JUTPreferences.getTestMethodFilterName()) {
	    if (filter.startsWith("*")) {
		nameFilterEndsWith.add(filter.replaceFirst("*", ""));
	    } else if (filter.endsWith("*")) {
		nameFilterStartsWith.add(filter.replace("*", ""));
	    } else {
		nameFilterEquals.add(filter);
	    }
	}
    }

    /**
     * Returns if the method name is allowed.
     * 
     * @param method
     * @return true if the method name is allowed
     */
    private boolean isMethodNameAllowed(IMethod method) {
	String methodName = method.getElementName().replace(".java", "");

	Vector<String> startsWithFilter = getTestClassNameFilterStartsWith();
	Vector<String> equalsFilter = getTestClassNameFilterEquals();
	Vector<String> endsFilter = getTestClassNameFilterEndsWith();

	for (String filter : startsWithFilter) {
	    if (methodName.startsWith(filter)) {
		return false;
	    }
	}

	for (String filter : equalsFilter) {
	    if (methodName.equalsIgnoreCase(filter)) {
		return false;
	    }
	}

	for (String filter : endsFilter) {
	    if (methodName.endsWith(filter)) {
		return false;
	    }
	}

	return true;
    }

    /**
     * Return if the method is allowed.
     * 
     * @param method
     * @param nameFilter
     * @param modifierFilter
     * @return true when the method is allowed
     * @throws JavaModelException
     */
    private boolean isMethodAllowed(IMethod method, boolean nameFilter,
	    boolean modifierFilter) throws JavaModelException {
	if (modifierFilter && !isModifierAllowed(method)) {
	    return false;
	}

	if (nameFilter && !isMethodNameAllowed(method)) {
	    return false;
	}

	return true;
    }

    /**
     * @return test class name filters - starts with
     */
    private Vector<String> getTestClassNameFilterStartsWith() {
	return nameFilterStartsWith;
    }

    /**
     * @return test class name filters - equals
     */
    private Vector<String> getTestClassNameFilterEquals() {
	return nameFilterEquals;
    }

    /**
     * @return test class name filters - ends with
     */
    private Vector<String> getTestClassNameFilterEndsWith() {
	return nameFilterEndsWith;
    }

    /**
     * Returns the junit-tools-Model with all test data.
     * 
     * @return the generator-model
     */
    protected GeneratorModel getModel() {
	return model;
    }

    /**
     * 
     * @param method
     * @return
     * @throws JavaModelException
     */
    private boolean isModifierAllowed(IMethod method) throws JavaModelException {
	String modifier = JDTUtils.getMethodModifier(method);

	for (String notAllowedModifier : JUTPreferences
		.getTestMethodFilterModifier()) {

	    if (MOD_PUBLIC.equals(modifier)) {
		if (MOD_PUBLIC.equalsIgnoreCase(notAllowedModifier)) {
		    return false;
		}
	    } else if (MOD_PROTECTED.equals(modifier)) {
		if (MOD_PROTECTED.equalsIgnoreCase(notAllowedModifier)) {
		    return false;
		}
	    } else if (MOD_PRIVATE.equals(modifier)) {
		if (MOD_PRIVATE.equalsIgnoreCase(notAllowedModifier)) {
		    return false;
		}
	    } else if (MOD_PACKAGE.equals(modifier)) {
		if (MOD_PACKAGE.equalsIgnoreCase(notAllowedModifier)) {
		    return false;
		}
	    }

	}

	return true;
    }

    public void addListener(
	    IMethodeSelectionChangedListener methodSelectionChangedListener) {
	this.listeners.add(methodSelectionChangedListener);
    }

    public void removeListener(
	    IMethodeSelectionChangedListener methodSelectionChangedListener) {
	this.listeners.remove(methodSelectionChangedListener);
    }

    public void setExistingMethods(HashMap<MethodRef, IMethod> existingMethods)
	    throws JavaModelException {
	this.existingMethods = existingMethods;
	initCheckedMethods();
	handleExistingMethodsFilterSelection();
    }

    public HashMap<MethodRef, IMethod> getExistingMethods() {
	return existingMethods;
    }

}
