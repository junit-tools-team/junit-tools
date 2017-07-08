package org.junit.tools.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.tools.base.JUTException;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.base.MethodRef;
import org.junit.tools.generator.model.JUTElements;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.preferences.JUTPreferences;
import org.junit.tools.ui.generator.wizards.MockGeneratorWizard;
import org.junit.tools.ui.preferences.JUTPreferenceAnnotationsPage;
import org.junit.tools.ui.utils.EclipseUIUtils;

/**
 * The base generator for mock-classes based on a other class and the
 * mock-framework jmockit.
 * 
 * @author JUnit-Tools-Team
 * 
 */
public class MockClassGenerator implements IMockClassGenerator,
		IGeneratorConstants {

	private Logger logger = Logger
			.getLogger(MockClassGenerator.class.getName());
	private ICompilationUnit generatedCuMock = null;
	private JUTWarning jutWarning;

	@Override
	public ICompilationUnit generate(IWorkbenchWindow activeWorkbenchWindow,
			IFileEditorInput fileEditorInput) throws JUTWarning,
			JavaModelException, CoreException, JUTException {
		return generate(JDTUtils.getJavaElements(null, fileEditorInput),
				activeWorkbenchWindow);
	}

	@Override
	public ICompilationUnit generate(IWorkbenchWindow activeWorkbenchWindow,
			IStructuredSelection selection) throws JUTWarning,
			JavaModelException, CoreException, JUTException {
		Vector<IJavaElement> javaElements = JDTUtils.getJavaElements(selection,
				null, true);

		return generate(javaElements, activeWorkbenchWindow);
	}

	private ICompilationUnit generate(Vector<IJavaElement> javaElements,
			IWorkbenchWindow activeWorkbenchWindow) throws CoreException,
			JUTWarning, JUTException {
		if (javaElements.size() == 0) {
			return null;
		}

		IJavaElement iJavaElement = javaElements.get(0);

		// find mocked class
		if (iJavaElement instanceof ICompilationUnit) {
			IType mockedClass = findMockedClass((ICompilationUnit) iJavaElement);

			if (mockedClass != null) {
				iJavaElement = mockedClass.getParent();
			}
		}
		
		MockGeneratorWizard wizard = openWizard(iJavaElement,
				activeWorkbenchWindow);
		if (!wizard.isFinished()) {
			return null;
		}

		// generate mock
		ICompilationUnit generatedMock = generateMock(iJavaElement,
				wizard.getCheckedMethods(), wizard.getProject(),
				wizard.getTargetPackage(), wizard.isMockNew(),
				wizard.getMockClass(), wizard.getExistingMethods());

		if (generatedMock != null && generatedMock.exists()) {
			// make source beautiful
			IWorkbenchPartSite site = activeWorkbenchWindow.getActivePage()
					.getActivePart().getSite();
			EclipseUIUtils.organizeImports(site, generatedMock);
			EclipseUIUtils.format(site, generatedMock);
		}

		return generatedMock;
	}

	private ICompilationUnit generateMock(final IJavaElement cu,
			final Vector<IMethod> checkedMethods,
			final IJavaProject targetProject,
			final IPackageFragment targetPackageTmp, final boolean isMockNew,
			final ICompilationUnit mockClass,
			final HashMap<MethodRef, IMethod> existingMethods)
			throws CoreException, JUTWarning {
		if (checkedMethods == null || checkedMethods.size() == 0) {
			return null;
		}

		jutWarning = null;

		JavaCore.run(new IWorkspaceRunnable() {

			@Override
			public void run(IProgressMonitor monitor) throws CoreException {

				logger.log(Level.INFO,
						"generate mock for " + cu.getElementName() + " in "
								+ targetProject.getElementName());
				
				if (targetPackageTmp == null) {
					return;
				}

				IPackageFragment targetPackage = null;
				if (!targetPackageTmp.exists()) {
					IPackageFragmentRoot targetSourceFolder = JDTUtils.getPackageFragmentRoot(targetPackageTmp);
					targetPackage = JDTUtils.getPackage(targetProject, targetSourceFolder, targetPackageTmp.getElementName(), true);
				}
				else {
					targetPackage = targetPackageTmp;
				}

				ICompilationUnit cuMock;

				if (isMockNew) {
					cuMock = createMockClass(cu, targetPackage);
				}

				else {
					cuMock = mockClass;
				}

				// delete methods
				for (IMethod methodToDelete : GeneratorUtils
						.getMethodsToDelete(existingMethods, checkedMethods)) {
					methodToDelete.delete(true, null);

					// TODO delete further variables and methods
				}

				// create methods
				createMockMethods(cu, cuMock, GeneratorUtils
						.getMethodsToCreate(existingMethods, checkedMethods));

				save(cuMock);

			}

		}, null);

		if (jutWarning != null) {
			throw jutWarning;
		}

		return generatedCuMock;
	}

	protected void setJUTWarning(JUTWarning e) {
		this.jutWarning = e;
	}

	protected void save(ICompilationUnit cuMock)
			throws OperationCanceledException, CoreException {
		this.generatedCuMock = cuMock;
		cuMock.save(null, true);
		cuMock.makeConsistent(null);
		if (cuMock.isWorkingCopy()) {
			cuMock.commitWorkingCopy(true, null);
		}

	}

	public ICompilationUnit regenerate(final ICompilationUnit oldMockClass,
			final IJavaElement baseClass,
			final Vector<IMethod> mockMethodsToCreate) throws CoreException {

		IWorkspaceRunnable iWorkspaceRunnable = new IWorkspaceRunnable() {

			@Override
			public void run(IProgressMonitor monitor) throws CoreException {

				logger.log(Level.INFO,
						"regenerate mock for "
								+ oldMockClass.getElementName()
								+ " in "
								+ oldMockClass.getJavaProject()
										.getElementName());

				ICompilationUnit cuMock = createMockClass(baseClass,
						JDTUtils.getPackage(oldMockClass));

				createMockMethods(baseClass, cuMock, mockMethodsToCreate);
				
				save(cuMock);
				
				if (cuMock != null && cuMock.exists()) {
					IWorkbenchWindow activeWorkbenchWindow = EclipseUIUtils.getActiveWorkbenchWindow();		
					// make source beautiful
					IWorkbenchPartSite site = activeWorkbenchWindow.getActivePage()
							.getActivePart().getSite();
					EclipseUIUtils.organizeImports(site, cuMock);
					EclipseUIUtils.format(site, cuMock);
				}
			}

		};

		JavaCore.run(iWorkspaceRunnable, null);

		return this.generatedCuMock;
	}

	protected void setGeneratedMock(ICompilationUnit cuMock) {
		this.generatedCuMock = cuMock;
	}

	protected ICompilationUnit createMockClass(IJavaElement cu,
			IPackageFragment targetPackage) throws OperationCanceledException,
			CoreException {

		if (cu == null || !cu.exists()) {
			throw new RuntimeException("Source of class is not available!");
		}

		boolean isInterface = false;

		if (cu instanceof ICompilationUnit) {
			ICompilationUnit compU = (ICompilationUnit) cu;
			isInterface = compU.findPrimaryType().isInterface();
		}

		String cuName = cu.getElementName();
		String cuBaseName = cuName.replace(".java", "");
		cuBaseName = cuBaseName.replace(".class", "");

		String cuMockName = cuBaseName + "Mock.java";
		String cuMockNameTmp = cuBaseName + "Mock";

		String source = createMockClassBody(cuBaseName, cuMockNameTmp,
				isInterface);

		ICompilationUnit cuMock;
		cuMock = targetPackage.getCompilationUnit(cuMockName);

		// save and close cu in editor
		if (cuMock.isWorkingCopy()) {
			EclipseUIUtils.saveAndCloseEditor(cuMockName);
		}

		// delete mock if exists
		if (cuMock.exists()) {
			cuMock.delete(true, null);
		}

		cuMock = targetPackage.createCompilationUnit(cuMockName, source, true,
				null);

		cuMock.createPackageDeclaration(targetPackage.getElementName(), null);
		cuMock.createImport("java.util.*", null, null);

		// create createMock-method
		JDTUtils.createMethod(cuMock.findPrimaryType(), MOD_PUBLIC
				+ MOD_STATIC_WITH_BLANK, cuMockNameTmp, "create", null, null,
				"return new " + cuMockNameTmp + "();");

		return cuMock;

	}

	/**
	 * @param cuMockNameTmp
	 * @param cuBaseName
	 * @param isInterface
	 * @return source
	 */
	protected String createMockClassBody(String cuBaseName,
			String cuMockNameTmp, boolean isInterface) {

		String extendsClause = "";

		if (isInterface) {
			extendsClause = " implements " + cuBaseName;
		} else {
			extendsClause = " extends MockUp<" + cuBaseName + "> ";
		}

		String annoGenerated = GeneratorUtils.createAnnoGenerated();

		StringBuilder annotations = new StringBuilder();
		annotations.append(annoGenerated).append(createCustomAnnotations());

		String[] mockClassAnnotations = JUTPreferences
				.getMockClassAnnotations();
		for (String additionalAnno : mockClassAnnotations) {
			if (!additionalAnno.startsWith("@")) {
				additionalAnno = "@" + additionalAnno;
			}
			annotations.append(additionalAnno).append(RETURN);
		}

		String source = "/** Mock for " + "{ @link " + cuBaseName + " } */"
				+ annotations.toString() + MOD_PUBLIC + " class "
				+ cuMockNameTmp + extendsClause + "{ " + RETURN + "}";

		return source;
	}

	/**
	 * @return hook for custom annotations
	 */
	protected String createCustomAnnotations() {
		return "";
	}

	private void createMockMethods(IJavaElement cuBase,
			ICompilationUnit cuMock, List<IMethod> methodsToMock)
			throws JavaModelException {
		IType mockType = cuMock.findPrimaryType();

		if (cuBase instanceof ICompilationUnit) {
			IType baseType = ((ICompilationUnit) cuBase).findPrimaryType();

			if (baseType != null && baseType.isInterface()) {
				createMockMethodsForInterface(mockType, baseType, methodsToMock);
				return;
			}
		}

		createMockMethodsForClass(cuMock, mockType, methodsToMock);
	}

	/**
	 * @param cuMock
	 * @param type
	 * @param methodsToMock
	 * @throws JavaModelException
	 *             TODO refactor
	 */
	private void createMockMethodsForClass(ICompilationUnit cuMock, IType type,
			List<IMethod> methodsToMock) throws JavaModelException {
		String returnType;
		String methodParams, annoMethodRef, methodToMockName, methodToMockNameFirstCharUp;
		String returnValueName = "returnValue";
		StringBuilder newResetMethodsBody = new StringBuilder();

		// create new methods
		String annoMock = "@Mock";
		String varReturnValue, varMocked, varExecutions;

		for (IMethod methodToMock : methodsToMock) {
			StringBuilder methodBody;
			methodToMockName = methodToMock.getElementName();
			methodToMockNameFirstCharUp = GeneratorUtils
					.firstCharToUpper(methodToMockName);
			returnType = JDTUtils
					.createReturnType(methodToMock.getReturnType());

			// mock variables
			int increment = -1;
			varMocked = methodToMockName + "Mocked";
			varExecutions = methodToMockName + "Executions";
			varReturnValue = methodToMockName + "ReturnValue";

			IField createdField = JDTUtils.createField(type, MOD_PRIVATE,
					TYPE_BOOLEAN, varMocked, "false", true);

			// process increment
			increment = JDTUtils.getIncrement(createdField.getElementName(),
					null);
			if (increment != -1) {
				varMocked += "_" + increment;
				varExecutions += "_" + increment;
				varReturnValue += "_" + increment;
				methodToMockNameFirstCharUp += "_" + increment;
			}

			JDTUtils.createField(type, MOD_PRIVATE, TYPE_INT, varExecutions,
					"0", false);

			if (returnType.equals(TYPE_VOID)) {
				varReturnValue = "";
			} else {
				JDTUtils.createField(type, MOD_PRIVATE, returnType,
						varReturnValue,
						JDTUtils.createInitValue(returnType, true), false);
			}

			// mock method
			annoMethodRef = GeneratorUtils.createAnnoMethodRef(methodToMock);

			methodParams = JDTUtils.createParamList(methodToMock);
			if (methodParams.length() > 0) {
				methodParams = ", " + methodParams;
			}
			methodParams = "Invocation inv" + methodParams;

			methodBody = new StringBuilder();
			methodBody.append(varExecutions + "++;" + RETURN).append("if (")
					.append(varMocked).append(") {").append(RETURN)
					.append("return ").append(varReturnValue).append(";")
					.append("}").append(RETURN)
					.append(returnType.equals(TYPE_VOID) ? "" : "return ")
					.append("inv.proceed();");

			JDTUtils.createMethod(type, MOD_PACKAGE, returnType,
					methodToMockName, null, methodParams,
					methodBody.toString(), annoMethodRef, annoMock);

			// setUp-Method
			methodBody = new StringBuilder();
			if (returnType.equals(TYPE_VOID)) {
				methodParams = "";
			} else {
				methodParams = returnType + " " + returnValueName;
				methodBody.append(varReturnValue).append(" = ")
						.append(returnValueName).append(";").append(RETURN);
			}

			methodBody.append(varMocked).append(" = true;").append(RETURN);
			methodBody.append(varExecutions).append(" = 0;");

			JDTUtils.createMethod(type, MOD_PUBLIC, TYPE_VOID, "setUpMock"
					+ methodToMockNameFirstCharUp, "", methodParams,
					methodBody.toString(), false, true);

			// getter for executions
			methodBody = new StringBuilder();
			methodBody.append("return ").append(varExecutions).append(";");

			JDTUtils.createMethod(type, MOD_PUBLIC, TYPE_INT, "get"
					+ GeneratorUtils.firstCharToUpper(varExecutions), "", "",
					methodBody.toString());

			methodBody = new StringBuilder();
			methodBody.append("return ").append(varExecutions).append(" > 0;");

			JDTUtils.createMethod(type, MOD_PUBLIC, TYPE_BOOLEAN, "is"
					+ methodToMockNameFirstCharUp + "Executed", "", "",
					methodBody.toString());

			// reset-method
			methodBody = new StringBuilder();
			methodBody.append(varMocked).append(" = false;").append(RETURN);
			methodBody.append(varExecutions).append(" = 0;");

			JDTUtils.createMethod(type, MOD_PUBLIC, TYPE_VOID, "resetMock"
					+ methodToMockNameFirstCharUp, "", "",
					methodBody.toString());

			newResetMethodsBody.append("resetMock")
					.append(methodToMockNameFirstCharUp).append("();");
		}

		// extend resetAll-method
		IMethod resetAllMethod = type.getMethod("resetAllMocks", new String[0]);
		if (resetAllMethod.exists()) {
			String source = resetAllMethod.getSource();
			int firstIndexOf = source.indexOf("{") + 1;
			int lastIndexOf = source.lastIndexOf("}");
			String body = source.substring(firstIndexOf, lastIndexOf);
			newResetMethodsBody.append(body);
		}

		JDTUtils.createMethod(type, MOD_PUBLIC, TYPE_VOID, "resetAllMocks", "",
				"", newResetMethodsBody.toString(), false, true);
	}

	/**
	 * @param cuBase
	 * @param generatedCuMock
	 * @param baseType
	 * @param methodsToMock
	 * @throws JavaModelException
	 */
	private void createMockMethodsForInterface(IType mockType, IType baseType,
			List<IMethod> methodsToMock) throws JavaModelException {
		for (IMethod baseMethod : baseType.getMethods()) {

			String varName = "";
			String returnType = JDTUtils.createReturnType(baseMethod
					.getReturnType());
			if (!returnType.equals(TYPE_VOID)) {
				varName = baseMethod.getElementName() + "Result";
				if (!TYPE_VOID.equals(returnType)) {
					IField field = JDTUtils.createField(mockType, MOD_PRIVATE,
							returnType, varName,
							JDTUtils.createInitValue(returnType, true), true);
					varName = field.getElementName();
				}

				JDTUtils.createMethod(mockType, MOD_PUBLIC, TYPE_VOID, "set"
						+ GeneratorUtils.firstCharToUpper(varName), "",
						returnType + " " + varName, "this." + varName + " = "
								+ varName + ";", false, true);

				// create default implementation
				JDTUtils.createMethod(mockType, baseMethod, "return " + varName
						+ ";", "@Override");
			} else {
				// create default implementation
				JDTUtils.createMethod(mockType, baseMethod, "// nothing",
						"@Override");
			}

		}

	}

	private MockGeneratorWizard openWizard(IJavaElement javaElement,
			IWorkbenchWindow activeWorkbenchWindow) throws CoreException,
			JUTWarning {
		MockGeneratorWizard wizard = new MockGeneratorWizard(javaElement);

		WizardDialog dialog = new WizardDialog(
				activeWorkbenchWindow.getShell(), wizard);
		dialog.create();

		wizard.init();

		dialog.open();

		return wizard;
	}

	public void cleanMock(ICompilationUnit cu) throws CoreException, JUTException, JUTWarning {
		// make a rebuild
		IType mockedClass = findMockedClass(cu);
		if (mockedClass == null) {
			return;
		}

		// get mock methods to create
		HashMap<MethodRef, IMethod> existingMethods = GeneratorUtils
				.getExistingTestMethods(null, cu, true);

		Vector<IMethod> mockMethodsToCreate = new Vector<IMethod>();
		Vector<IMethod> cleanMethods = new Vector<IMethod>();
		HashMap<String, IMethod> dirtyMethods = new HashMap<String, IMethod>();

		IMethod[] methods = mockedClass.getMethods();
		String methodRefName;

		for (IMethod method : methods) {
			for (MethodRef methodRef : existingMethods.keySet()) {
				if (methodRef.getName().startsWith("setUpMock")) {
					methodRefName = GeneratorUtils.firstCharToLower(methodRef
							.getName().substring(9));
				} else {
					methodRefName = methodRef.getName();
				}

				if (method.getElementName().equals(methodRefName)) {
					if (method.getSignature().equals(methodRef.getSignature())) {
						cleanMethods.add(method);
						break;
					} else {
						if (!dirtyMethods.containsKey(method.getElementName())) {
							dirtyMethods.put(method.getElementName(), method);
						}
					}

				}
			}
		}

		// add clean methods
		mockMethodsToCreate.addAll(cleanMethods);

		// add dirty methods
		boolean found;
		for (Entry<String, IMethod> dirtyMethodEntry : dirtyMethods.entrySet()) {
			found = false;
			for (IMethod method : cleanMethods) {
				if (method.getElementName().equals(dirtyMethodEntry.getKey())) {
					found = true;
					break;
				}
			}

			if (!found
					&& !mockMethodsToCreate.contains(dirtyMethodEntry
							.getValue())) {
				mockMethodsToCreate.add(dirtyMethodEntry.getValue());
			}
		}

		IJavaElement mockClassElement = mockedClass.getCompilationUnit();
		if (mockClassElement == null) {
			mockClassElement = mockedClass.getClassFile();
		}
		
		regenerate(cu, mockClassElement, mockMethodsToCreate);
	}

	private IType findMockedClass(ICompilationUnit mockClass)
			throws JavaModelException, JUTException, JUTWarning {
		IType type = mockClass.findPrimaryType();

		if (type == null || !type.exists()) {
			return null;
		}

		String mockedClassName = null;

		// mocked name via superclass
		mockedClassName = GeneratorUtils.getMockedClassName(type);
		
		// check if it is a test-class
		if (mockedClassName == null) {
			boolean isTestclass = GeneratorUtils.isTestClass(type);
			if (isTestclass) {
				// initialize the JUT elements to get the base class
				try {
					JUTElements jutElements = JUTElements.initJUTElements(type.getJavaProject(), type.getCompilationUnit());
					return jutElements.getClassesAndPackages().getBaseClass().findPrimaryType();
				} catch (Exception e) {
					throw new JUTException("A test-class was selected to generate a mock-class and the base-class could not be resolved!");
				}
				
			}
		}

		if (mockedClassName == null) {
			throw new JUTWarning("The class to mock was not found!");
		}

		String fullQualifiedName = JDTUtils.getFullQualifiedName(
				mockClass.findPrimaryType(), mockedClassName);

		IType mockedClass;

		if (fullQualifiedName != null) {
			for (IJavaProject project : mockClass.getJavaModel()
					.getJavaProjects()) {
				mockedClass = project.findType(fullQualifiedName);
				if (mockedClass != null && mockedClass.exists()) {
					return mockedClass;
				}
			}
		}

		return null;
	}

}
