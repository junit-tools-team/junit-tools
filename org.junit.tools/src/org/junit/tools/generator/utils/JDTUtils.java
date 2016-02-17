package org.junit.tools.generator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.generator.IGeneratorConstants;
import org.junit.tools.generator.ITestDataFactory;
import org.junit.tools.messages.Messages;
import org.junit.tools.ui.utils.EclipseUIUtils;

/**
 * Utils for the selection, creation, manipulation and deletion of java-elements
 * with the jdt framework.
 * 
 * @author JUnit-Tools-Team
 * 
 */
public class JDTUtils implements IGeneratorConstants {

    private static Logger logger = Logger.getLogger(JDTUtils.class.getName());

    private static ICompilationUnit lastParsedCu = null;

    private static CompilationUnit parsedCu = null;

    /**
     * Creates a AST-root from a compilation unit an caches it (only the last
     * one).
     * 
     * @param cu
     * @return CompilationUnit
     * @throws JavaModelException
     */
    public static CompilationUnit createASTRoot(ICompilationUnit cu)
	    throws JavaModelException {
	// creation of DOM/AST from a ICompilationUnit (without caching)
//	if (lastParsedCu != null && lastParsedCu.getSource().length() == cu.getSource().length()) {
//	    return parsedCu;
//	}
//	lastParsedCu = cu;

	ASTParser parser = createASTParser(cu);
	parsedCu = (CompilationUnit) parser.createAST(null);

	return parsedCu;
    }

    /**
     * Collects the compilation units.
     * 
     * @param javaElements
     * @param compilationUnits
     * @throws JavaModelException
     */
    private static void collectCompilationUnits(IJavaElement[] javaElements,
	    Vector<IJavaElement> compilationUnits, String filter)
	    throws JavaModelException {
	for (IJavaElement javaElement : javaElements) {
	    collectCompilationUnits(javaElement, compilationUnits, filter);
	}
    }

    public static Vector<IJavaElement> collectCompilationUnits(
	    List<IJavaProject> javaProjects) throws JavaModelException {
	Vector<IJavaElement> compilationUnits = new Vector<IJavaElement>();
	for (IJavaElement javaProject : javaProjects) {
	    collectCompilationUnits(javaProject, compilationUnits, "");
	}
	return compilationUnits;
    }

    public static void collectCompilationUnits(IJavaElement javaElement,
	    Vector<IJavaElement> compilationUnits) throws JavaModelException {
	collectCompilationUnits(javaElement, compilationUnits, "");
    }

    public static Vector<IJavaElement> collectCompilationUnits(
	    IJavaElement javaElement) throws JavaModelException {
	Vector<IJavaElement> selectedCompilationUnits = new Vector<IJavaElement>();
	collectCompilationUnits(javaElement, selectedCompilationUnits, null);
	return selectedCompilationUnits;
    }

    /**
     * Collects the compilation units.
     * 
     * @param javaElement
     * @param selectedCompilationUnits
     * @throws JavaModelException
     */
    public static void collectCompilationUnits(IJavaElement javaElement,
	    Vector<IJavaElement> selectedCompilationUnits, String filter)
	    throws JavaModelException {

	switch (javaElement.getElementType()) {
	case IJavaElement.FIELD:
	    addCompilationUnit(selectedCompilationUnits,
		    ((IField) javaElement).getCompilationUnit(), filter);
	    break;
	case IJavaElement.METHOD:
	    addCompilationUnit(selectedCompilationUnits,
		    ((IMethod) javaElement).getCompilationUnit(), filter);
	    break;
	case IJavaElement.JAVA_MODEL:
	    break;
	case IJavaElement.JAVA_PROJECT:
	    collectCompilationUnits(((IJavaProject) javaElement).getChildren(),
		    selectedCompilationUnits, filter);
	    break;
	case IJavaElement.PACKAGE_FRAGMENT_ROOT:
	    collectCompilationUnits(
		    ((IPackageFragmentRoot) javaElement).getChildren(),
		    selectedCompilationUnits, filter);
	    break;
	case IJavaElement.PACKAGE_FRAGMENT:
	    collectCompilationUnits((IPackageFragment) javaElement,
		    selectedCompilationUnits, filter);
	    break;
	case IJavaElement.COMPILATION_UNIT:
	    addCompilationUnit(selectedCompilationUnits,
		    (ICompilationUnit) javaElement, filter);
	    break;
	case IJavaElement.TYPE:
	    addCompilationUnit(selectedCompilationUnits, (IType) javaElement,
		    filter);
	    break;
	case IJavaElement.CLASS_FILE:
	    addCompilationUnit(selectedCompilationUnits,
		    (IClassFile) javaElement, filter);
	    break;
	default:
	}
    }

    private static void addCompilationUnit(
	    Vector<IJavaElement> selectedCompilationUnits,
	    IClassFile classFile, String filter) {
	selectedCompilationUnits.add(classFile);
    }

    private static void addCompilationUnit(
	    Vector<IJavaElement> selectedCompilationUnits, IType type,
	    String filter) {
	addCompilationUnit(selectedCompilationUnits, type.getCompilationUnit(),
		filter);
    }

    private static void addCompilationUnit(
	    Vector<IJavaElement> selectedCompilationUnits, ICompilationUnit cu,
	    String filter) {
	if (filter == null || "".equals(filter)) {
	    selectedCompilationUnits.add(cu);
	} else if (cu.getElementName().startsWith(filter)) {
	    selectedCompilationUnits.add(cu);
	}

    }

    /**
     * Collect the compilation units.
     * 
     * @param pkgFrgmt
     * @param selectedCompilationUnits
     * @throws JavaModelException
     */
    private static void collectCompilationUnits(IPackageFragment pkgFrgmt,
	    Vector<IJavaElement> selectedCompilationUnits, String filter)
	    throws JavaModelException {
	ICompilationUnit[] compilationUnits = pkgFrgmt.getCompilationUnits();
	for (ICompilationUnit cu : compilationUnits) {
	    addCompilationUnit(selectedCompilationUnits, cu, filter);
	}
    }

    public static IJavaProject getProject(ISelection selection) {
	return getProject(selection, null);
    }

    /**
     * @param selection
     * @param fileEditorInput
     * @return java-project from the selection or from the editor.
     */
    public static IJavaProject getProject(ISelection selection,
	    IFileEditorInput fileEditorInput) {
	Object firstElement;
	IProject project = null;

	if (fileEditorInput != null) {
	    firstElement = fileEditorInput;
	} else {
	    firstElement = EclipseUIUtils.getFirstSelectedElement(selection);

	    if (firstElement == null) {
		return null;
	    }
	}

	if (firstElement instanceof IJavaElement) {
	    return getProject((IJavaElement) firstElement);
	} else if (firstElement instanceof IJavaProject) {
	    return (IJavaProject) firstElement;
	}
	if (firstElement instanceof IProject) {
	    project = (IProject) firstElement;
	} else if (firstElement instanceof IFile) {
	    IFile file = (IFile) firstElement;
	    project = file.getProject();
	} else if (firstElement instanceof IFileEditorInput) {
	    IFileEditorInput fileEditorInputTmp = (IFileEditorInput) firstElement;
	    project = fileEditorInputTmp.getFile().getProject();
	} else if (firstElement instanceof ICompilationUnit) {
	    ICompilationUnit compilationUnit = (ICompilationUnit) firstElement;
	    return compilationUnit.getJavaProject();
	}

	if (project == null) {
	    return null;
	}

	return convertToJavaProject(project);
    }

    /**
     * @param project
     * @return converted java project
     */
    public static IJavaProject convertToJavaProject(IProject project) {
	IJavaProject jProject;
	try {
	    if (project.hasNature(JavaCore.NATURE_ID)) {
		jProject = JavaCore.create(project);
	    } else {
		throw new RuntimeException(
			Messages.GeneratorUtils_OnlyJavaProjects);
	    }
	} catch (CoreException e) {
	    return null;
	}

	return jProject;
    }

    public static IFolder createFolder(IJavaProject jproject, String folderName) {
	// folderName = folderName.replace(".", "/");
	IFolder folder = jproject.getProject().getFolder(folderName);
	return createFolder(jproject, folder);
    }

    private static IFolder createFolder(IJavaProject jproject, IFolder folder) {
	try {
	    if (!folder.exists()) {
		folder.create(true, true, null);
	    }

	} catch (Exception e) {
	    logger.severe(e.getMessage());
	    throw new RuntimeException(
		    Messages.GeneratorUtils_ErrorPackageCreation);
	}

	return folder;
    }

    public static IPackageFragment getPackage(IJavaProject javaProject,
	    String name, boolean createIfNotExists) throws CoreException {
	return getPackage(javaProject, "src", name, createIfNotExists);
    }

    public static IPackageFragment getPackage(IJavaProject javaProject,
	    String name, IFolder srcFolder, boolean createIfNotExists)
	    throws CoreException {
	// create package fragment
	IPackageFragmentRoot parentFolder = javaProject
		.getPackageFragmentRoot(srcFolder);

	return getPackage(javaProject, parentFolder, name, createIfNotExists);
    }

    public static IPackageFragment getPackage(IJavaProject javaProject,
	    String srcFolder, String name, boolean createIfNotExists)
	    throws CoreException {
	String srcFolderName = "";

	if (srcFolder == null || "".equals(srcFolder)) {
	    srcFolderName = "src";
	} else {
	    srcFolderName = srcFolder;
	}

	IFolder folder = javaProject.getProject().getFolder(srcFolderName);

	if (!folder.exists()) {
	    if (createIfNotExists) {
		createSourceFolder(javaProject, folder);
	    } else {
		return null;
	    }
	}

	return getPackage(javaProject, name, folder, createIfNotExists);
    }

    public static IPackageFragmentRoot createSourceFolder(
	    IJavaProject javaProject, IPath folderPath) throws CoreException {
	IFolder folder = javaProject.getProject().getFolder(folderPath);

	if (!folder.exists()) {
	    return createSourceFolder(javaProject, folder);
	}

	return javaProject.getPackageFragmentRoot(folder);
    }

    private static IPackageFragmentRoot createSourceFolder(
	    IJavaProject javaProject, IFolder folder) throws CoreException {
	
	IContainer parent = folder.getParent();
	if (parent instanceof IFolder && !parent.exists()) {
	    IFolder parentFolder = (IFolder)folder.getParent();
	    return createSourceFolder(javaProject, parentFolder, parentFolder.getFullPath());
	}

	return createSourceFolder(javaProject, folder, null);

    }

    private static IPackageFragmentRoot createSourceFolder(IJavaProject javaProject,
	    IFolder folder, IPath parentPath) throws CoreException {
	// for nested and new parent folders
	if (parentPath != null) {
	    IPath folderPath = folder.getFullPath().removeFirstSegments(parentPath.segmentCount());
	    folder = javaProject.getProject().getFolder(folderPath);
	}

	folder.create(true, true, null);

	// add new folder to class path
	IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);
	IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
	IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
	System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
	newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
	javaProject.setRawClasspath(newEntries, null);

	return root;
    }

    public static IPackageFragment getPackage(IJavaProject javaProject,
	    IPackageFragmentRoot srcFolder, String name,
	    boolean createIfNotExists) throws CoreException {
	IPackageFragment packageFragment = srcFolder.getPackageFragment(name);
	if (packageFragment == null || !packageFragment.exists()) {
	    if (createIfNotExists) {
		packageFragment = srcFolder.createPackageFragment(name, true,
			null);
	    } else {
		packageFragment = srcFolder.getPackageFragment(name);
	    }
	}

	return packageFragment;
    }

    public static List<IPackageFragment> getSubPackages(IPackageFragment p)
	    throws JavaModelException {
	List<IPackageFragment> result = new ArrayList<IPackageFragment>();
	IJavaElement[] children = ((IPackageFragmentRoot) p.getParent())
		.getChildren();

	for (IJavaElement element : children) {
	    if (element instanceof IPackageFragment) {
		if (isParentPackage(p.getElementName(),
			element.getElementName())) {
		    result.add((IPackageFragment) element);
		}
	    }
	}

	return result;
    }

    /**
     * creates src- and package-folders
     * 
     * @param name
     * @throws CoreException
     */
    public static IPackageFragment createPackage(IJavaProject javaProject,
	    String name) throws CoreException {
	return getPackage(javaProject, name, true);
    }

    /**
     * creates src- and package-folders
     * 
     * @param name
     * @throws CoreException
     */
    public static IPackageFragment createPackage(IJavaProject javaProject,
	    String parentFolderName, String name) throws CoreException {
	IFolder folder = javaProject.getProject().getFolder(parentFolderName);

	IPackageFragmentRoot parentFolder = null;
	if (!folder.exists()) {
	    parentFolder = createSourceFolder(javaProject, folder);
	} else {
	    parentFolder = javaProject.getPackageFragmentRoot(folder);
	}

	// create package fragment
	IPackageFragment packageFragment = parentFolder
		.getPackageFragment(name);
	if (packageFragment == null || !packageFragment.exists()) {
	    packageFragment = parentFolder.createPackageFragment(name, true,
		    null);
	}

	return packageFragment;
    }

    public static IJavaProject createProject(String name) throws CoreException {
	return createProject(name, null);
    }

    /**
     * Creates a java project.
     * 
     * @param name
     * @param baseProject
     * @return the created java project
     * @throws CoreException
     */
    public static IJavaProject createProject(String name,
	    IJavaProject baseProject) throws CoreException {
	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	IWorkspaceRoot root = workspace.getRoot();

	IProject project = root.getProject(name);

	if (project.exists())
	    return JavaCore.create(project);

	// create if not exists
	project.create(null);
	project.open(null);

	// transfer the project-nature
	IProjectDescription description = project.getDescription();
	project.setDescription(description, null);

	// create Java-Project
	IJavaProject javaProject = JavaCore.create(project);

	if (baseProject != null) {
	    description.setNatureIds(baseProject.getProject().getDescription()
		    .getNatureIds());

	    Vector<IClasspathEntry> buildPath = new Vector<IClasspathEntry>();
	    for (IClasspathEntry entry : baseProject.getRawClasspath()) {
		if (!(entry.getEntryKind() == IClasspathEntry.CPE_SOURCE))
		    buildPath.add(entry);
	    }

	    // add source-path
	    buildPath.add(JavaCore.newSourceEntry(javaProject.getPath().append(
		    "src"))); //$NON-NLS-1$
	    // add base-project-path
	    buildPath.add(JavaCore.newProjectEntry(baseProject.getPath()));

	    // set the path
	    IClasspathEntry[] entries = new IClasspathEntry[buildPath.size()];
	    entries = buildPath.toArray(entries);
	    javaProject.setRawClasspath(entries, null);
	}

	return javaProject;
    }

    /**
     * Returns the java project.
     * 
     * @return java project
     */
    public static IJavaProject getProject(IJavaElement javaElement) {
	return (IJavaProject) javaElement
		.getAncestor(IJavaElement.JAVA_PROJECT);
    }

    /**
     * Returns the java project.
     * 
     * @param name
     * @return java project
     */
    public static IJavaProject getProject(String name) {
	return getProject(name, false, null);
    }

    /**
     * Gets or creates a java project.
     * 
     * @param name
     * @param createIfNotExists
     * @param baseProject
     * @return the java project
     */
    public static IJavaProject getProject(String name,
	    boolean createIfNotExists, IJavaProject baseProject) {
	for (IProject project : getProjects()) {
	    if (project.getName().equals(name)) {
		return convertToJavaProject(project);
	    }
	}

	// create project
	if (createIfNotExists) {
	    try {
		return createProject(name, baseProject);
	    } catch (CoreException e) {
		throw new RuntimeException(e);
	    }
	}

	return null;
    }

    /**
     * Returns the projects from the workspace.
     * 
     * @return projects
     */
    public static IProject[] getProjects() {
	IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
		.getProjects();
	return projects;
    }

    public static List<IJavaProject> getJavaProjects() {
	List<IJavaProject> javaProjects = new ArrayList<IJavaProject>();
	IJavaProject javaProject;

	for (IProject project : getProjects()) {
	    try {
		javaProject = convertToJavaProject(project);
	    } catch (Exception ex) {
		continue;
	    }

	    if (javaProject != null) {
		javaProjects.add(javaProject);
	    }
	}

	return javaProjects;
    }

    /**
     * Returns the package.
     * 
     * @param javaElement
     * @return the package
     */
    public static IPackageFragment getPackage(IJavaElement javaElement) {
	IJavaElement tmp = javaElement
		.getAncestor(IJavaElement.PACKAGE_FRAGMENT);

	if (tmp == null)
	    return null;

	return (IPackageFragment) tmp;
    }

    /**
     * Creates a method.
     * 
     * @param type
     * @param modifier
     * @param returnType
     * @param methodName
     * @param throwsClause
     * @param params
     * @param body
     * @param annotations
     * @return the created method
     * @throws JavaModelException
     */
    public static IMethod createMethod(IType type, String modifier,
	    String returnType, String methodName, String throwsClause,
	    String params, String body, String... annotations)
	    throws JavaModelException {
	return createMethod(type, modifier, returnType, methodName,
		throwsClause, params, body, true, annotations);
    }

    /**
     * Creates a method.
     * 
     * @param type
     * @param modifier
     * @param returnType
     * @param methodName
     * @param throwsClause
     * @param params
     * @param body
     * @param increment
     * @param annotations
     * @return the created method
     * @throws JavaModelException
     */
    public static IMethod createMethod(IType type, String modifier,
	    String returnType, String methodName, String throwsClause,
	    String params, String body, boolean increment,
	    String... annotations) throws JavaModelException {
	return createMethod(type, modifier, returnType, methodName,
		throwsClause, params, body, true, false, annotations);
    }

    /**
     * @param type
     * @param method
     * @return created method
     * @throws JavaModelException
     */
    public static IMethod createMethod(IType type, IMethod method, String body,
	    String annotations) throws JavaModelException {
	return createMethod(type, method, method.getElementName(), body,
		annotations);
    }

    /**
     * @param type
     * @param method
     * @return created method
     * @throws JavaModelException
     */
    public static IMethod createMethod(IType type, IMethod method,
	    String methodName, String body, String annotations)
	    throws JavaModelException {
	String modifier = getMethodModifier(method);
	String returnType = createReturnType(method.getReturnType());
	String params = createParamList(method);

	String throwsClause = "";
	String comma = "";
	for (String exceptionType : method.getExceptionTypes()) {
	    throwsClause = comma + exceptionType;
	    comma = ",";
	}

	return createMethod(type, modifier, returnType, methodName,
		throwsClause, params, body, false, true, annotations);
    }

    /**
     * @param type
     * @param method
     * @return created method
     * @throws JavaModelException
     */
    public static IMethod createMethod(IType type, IMethod method)
	    throws JavaModelException {
	String returnType = createReturnType(method.getReturnType());
	String body = "";
	if (!TYPE_VOID.equals(returnType)) {
	    body = "return " + createInitValue(returnType, true) + ";";
	}

	return createMethod(type, method, body, "");
    }

    /**
     * @param type
     * @param method
     * @return created method
     * @throws JavaModelException
     */
    public static IMethod createMethod(IType type, IMethod method,
	    String annotation) throws JavaModelException {
	String returnType = createReturnType(method.getReturnType());
	String body = "";
	if (!TYPE_VOID.equals(returnType)) {
	    body = "return " + createInitValue(returnType, true) + ";";
	}

	return createMethod(type, method, body, annotation);
    }

    public static IMethod createMethod(IType type, String modifier,
	    String returnType, String methodName, String throwsClause,
	    String params, String body, boolean increment, boolean force,
	    String... annotations) throws JavaModelException {
	IMethod tmpMethod = type.getMethod(methodName, null); // TODO mit
							      // parametern
	StringBuilder sbMethod = new StringBuilder();

	if (force && tmpMethod.exists()) {
	    tmpMethod.delete(true, null);
	}

	if (increment) {
	    while (tmpMethod.exists()) {
		methodName = increment(methodName, "_"); //$NON-NLS-1$
		tmpMethod = type.getMethod(methodName, null);
	    }
	}

	// if (tmpMethod.exists()) {
	// logger.fine(Messages.GeneratorUtils_MethodExists + methodName);
	// return tmpMethod;
	// }

	if (annotations != null) {
	    for (String annotation : annotations) {
		if (!annotation.startsWith("@")) { //$NON-NLS-1$
		    annotation = "@" + annotation; //$NON-NLS-1$
		}

		sbMethod.append(annotation);
		sbMethod.append("\n"); //$NON-NLS-1$
	    }
	}

	if (throwsClause != null && throwsClause.length() > 0) {
	    throwsClause = "throws " + throwsClause; //$NON-NLS-1$
	} else
	    throwsClause = ""; //$NON-NLS-1$

	if (returnType == null)
	    returnType = ""; //$NON-NLS-1$

	if (params == null)
	    params = ""; //$NON-NLS-1$

	sbMethod.append(modifier)
		.append(" ").append(returnType).append(" ").append(methodName) //$NON-NLS-1$ //$NON-NLS-2$
		.append("(").append(params).append(") " + throwsClause + " {\n").append(body) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		.append("\n}"); //$NON-NLS-1$

	try {
	    tmpMethod = type.createMethod(sbMethod.toString(), null, force,
		    null);
	} catch (Exception ex) {
	    logger.severe(ExceptionUtils.getFullStackTrace(ex));
	}

	return tmpMethod;
    }

    /**
     * Increments a method name.
     * 
     * @param input
     * @param incrementSeperator
     * @return incremented method name
     */
    public static String increment(String input, String incrementSeperator) {
	if (incrementSeperator == null) {
	    incrementSeperator = INCREMENT_SEPERATOR;
	}

	int inputLength = input.length();
	int incrementSeperatorIx = getIncrementSeperatorIndex(input,
		incrementSeperator);

	if (incrementSeperatorIx != -1) {
	    int firstNumberIndex = incrementSeperatorIx + 1;
	    int postfixNumber;
	    if (firstNumberIndex < inputLength) {
		postfixNumber = Integer.parseInt(input.substring(
			incrementSeperatorIx + 1, inputLength));
	    } else {
		postfixNumber = 0;
	    }
	    postfixNumber++;

	    input = input.substring(0, incrementSeperatorIx + 1)
		    + postfixNumber;
	} else {
	    // first increment
	    input += incrementSeperator + "1"; //$NON-NLS-1$
	}

	return input;
    }

    private static int getIncrementSeperatorIndex(String input,
	    String incrementSeperator) {
	if (incrementSeperator == null) {
	    incrementSeperator = INCREMENT_SEPERATOR;
	}

	String lastLetter = ""; //$NON-NLS-1$
	int inputLength = input.length();
	int lastLetterIndex = inputLength - 1;

	for (int i = lastLetterIndex; i >= 0; i--) {
	    lastLetter = input.substring(i, i + 1);

	    try {
		Integer.parseInt(lastLetter);
	    } catch (NumberFormatException e) {
		lastLetterIndex = i;
		break;
	    }
	}

	if (lastLetter.equals(incrementSeperator)) {
	    return lastLetterIndex;
	}

	return -1;

    }

    public static List<IMethod> getMethods(IJavaElement javaElement,
	    boolean withSubTypes) throws JavaModelException {
	return getMethods(javaElement, withSubTypes, null);
    }

    public static List<IMethod> getMethods(IJavaElement javaElement,
	    boolean withSubTypes, String nameFilter) throws JavaModelException {
	return getMethods(javaElement, withSubTypes, nameFilter, false, null);
    }

    public static List<IMethod> getMethods(IJavaElement javaElement,
	    boolean withSubTypes, String nameFilter, boolean startsWith,
	    String modifierFilter) throws JavaModelException {
	List<IMethod> methods = new ArrayList<IMethod>();

	IType[] types;
	ICompilationUnit cu = null;

	if (javaElement instanceof ICompilationUnit) {
	    cu = (ICompilationUnit) javaElement;
	    types = cu.getAllTypes();
	} else if (javaElement instanceof IClassFile) {
	    javaElement = ((IClassFile) javaElement).findPrimaryType();
	}

	if (javaElement instanceof IType) {
	    return getMethods((IType) javaElement, nameFilter, startsWith,
		    modifierFilter);
	}

	if (cu == null) {
	    return methods;
	}

	if (withSubTypes) {
	    types = cu.getAllTypes();
	} else {
	    types = new IType[1];
	    types[0] = cu.findPrimaryType();
	}

	for (IType type : types) {
	    methods.addAll(getMethods(type, nameFilter, startsWith,
		    modifierFilter));
	}

	return methods;
    }

    public static List<IMethod> getMethods(IType type, String nameFilter,
	    boolean startsWith, String modifierFilter)
	    throws JavaModelException {
	List<IMethod> methods = new ArrayList<IMethod>();
	for (IMethod method : type.getMethods()) {

	    if (validateMethod(method, nameFilter, startsWith, modifierFilter)) {
		methods.add(method);
	    }

	}

	return methods;
    }

    public static boolean validateMethod(IMethod method, String nameFilter,
	    boolean startsWith, String modifierFilter)
	    throws JavaModelException {
	String methodName = method.getElementName();

	if (nameFilter != null) {
	    if (startsWith) {
		if (!methodName.startsWith(nameFilter)) {
		    return false;
		}
	    } else if (!nameFilter.equals(methodName)) {
		return false;
	    }
	}

	if (modifierFilter != null) {
	    String methodModifier = getMethodModifier(method);
	    if (!modifierFilter.equals(methodModifier)) {
		return false;
	    }
	}

	return true;
    }

    public static List<IMethod> getMethods(ICompilationUnit cu)
	    throws JavaModelException {
	return getMethods(cu, true);
    }

    public static MethodDeclaration createMethodDeclaration(CompilationUnit cu,
	    IMethod method) {
	ASTNode declaringNode = null;
	if (method.isResolved()) {
	    declaringNode = cu.findDeclaringNode(method.getKey());
	} else {
	    for (IBinding binding : createBindings(method.getCompilationUnit(),
		    method)) {
		if (binding instanceof IMethodBinding) {
		    declaringNode = cu
			    .findDeclaringNode(((IMethodBinding) binding)
				    .getKey());
		    break;
		}
	    }
	}

	if (!(declaringNode instanceof MethodDeclaration)) {
	    return null;
	}

	return (MethodDeclaration) declaringNode;
    }

    public static int getIncrement(String input, String incrementSeperator) {
	if (incrementSeperator == null) {
	    incrementSeperator = INCREMENT_SEPERATOR;
	}

	int incrementIx = getIncrementSeperatorIndex(input, incrementSeperator);

	if (incrementIx != -1) {
	    try {
		return Integer.parseInt(input.substring(incrementIx + 1));
	    } catch (NumberFormatException ex) {
		// nothing
	    }
	}

	return -1;
    }

    public static MethodDeclaration getASTMethod(ASTNode node) {
	if (node instanceof MethodDeclaration) {
	    return (MethodDeclaration) node;
	}

	if (node != null && node.getParent() != null) {
	    return getASTMethod(node.getParent());
	}

	return null;
    }

    private static String getFullQualifiedName(IImportDeclaration[] imports,
	    String paramType) {
	if (imports == null) {
	    return null;
	}

	String importFullQualifiedName;
	String importType;

	for (IImportDeclaration importDeclaration : imports) {
	    importFullQualifiedName = importDeclaration.getElementName();
	    importType = importFullQualifiedName
		    .substring(importFullQualifiedName.lastIndexOf(".") + 1);
	    if (paramType.equals(importType)) {
		return importFullQualifiedName;
	    }
	}

	return null;
    }

    /**
     * Gets the full qualified name from the type of a variable in a type from a
     * compilation unit
     * 
     * @param type
     * @param variableToSearch
     * @return the full qualified name for the type of the variable
     * @throws JavaModelException
     */
    public static String getFullQualifiedName(IType type,
	    ILocalVariable variableToSearch) throws JavaModelException {
	String variableTypeName = Signature
		.getSignatureSimpleName(variableToSearch.getTypeSignature());
	return getFullQualifiedName(type, variableTypeName);
    }

    public static String getFullQualifiedName(IType type,
	    String variableTypeName) throws JavaModelException {

	IImportDeclaration[] imports = null;
	List<?> importsFromBinary = null;
	ICompilationUnit cu = type.getCompilationUnit();

	if (cu != null) {
	    imports = cu.getImports();
	} else {
	    ASTParser parser = ASTParser.newParser(AST.JLS4);
	    parser.setSource(type.getClassFile().getBuffer().getCharacters());
	    CompilationUnit cuParsed = (CompilationUnit) parser.createAST(null);

	    if (cuParsed != null) {
		importsFromBinary = cuParsed.imports();
	    }
	}

	String fullQualifiedName = null;

	if (imports != null) {
	    // from source
	    fullQualifiedName = getFullQualifiedName(imports, variableTypeName);
	} else if (importsFromBinary != null) {
	    // from binary
	    fullQualifiedName = getFullQualifiedName(importsFromBinary,
		    variableTypeName);
	}

	if (fullQualifiedName == null) {
	    // if no import available, then try to find in same package
	    fullQualifiedName = type.getPackageFragment().getElementName()
		    + "." + variableTypeName;
	}

	return fullQualifiedName;
    }

    private static String getFullQualifiedName(List<?> importsFromBinary,
	    String paramTypeName) {
	for (Object importFromBinary : importsFromBinary) {
	    String importFromBinaryS = importFromBinary.toString()
		    .replace("\\n", "").replace("\n", "").replace(";", "");
	    int lastIndexOf = importFromBinaryS.lastIndexOf(".");
	    if (lastIndexOf == -1) {
		lastIndexOf = 0;
	    } else {
		lastIndexOf += 1;
	    }

	    String typeName = importFromBinaryS.substring(lastIndexOf);

	    if (paramTypeName.equals(typeName)) {
		return importFromBinaryS.replace("import", "").trim();
	    }
	}

	return null;
    }

    /**
     * Returns the method modifier as String.
     * 
     * @param method
     * @return method modifier
     * @throws JavaModelException
     */
    public static String getMethodModifier(IMethod method)
	    throws JavaModelException {
	int methodFlags = method.getFlags();

	if (Flags.isPublic(methodFlags)) {
	    return MOD_PUBLIC;
	} else if (Flags.isProtected(methodFlags)) {
	    return MOD_PROTECTED;
	} else if (Flags.isPrivate(methodFlags)) {
	    return MOD_PRIVATE;
	} else if (Flags.isPackageDefault(methodFlags)) {
	    return MOD_PACKAGE;
	}

	return ""; //$NON-NLS-1$
    }

    /**
     * Creates a AST-parser
     * 
     * @param cu
     * @return the created AST-parser
     */
    private static ASTParser createASTParser(ICompilationUnit cu) {
	ASTParser parser = ASTParser.newParser(AST.JLS4);
	parser.setProject(cu.getJavaProject());
	parser.setUnitName(cu.getElementName());
	parser.setResolveBindings(true);
	parser.setSource(cu);
	return parser;
    }

    public static IField createField(IType type, String modifier,
	    String fieldType, String fieldName, String assignment,
	    boolean increment) throws JavaModelException {

	if (increment) {
	    while (type.getField(fieldName).exists()) {
		fieldName = increment(fieldName, "_");
	    }
	}

	IField createdField = type.createField(modifier + " " + fieldType + " "
		+ fieldName + " = " + assignment + ";", null, true, null);

	return createdField;

    }

    public static IBinding[] createBindings(ICompilationUnit cu,
	    IJavaElement... javaElements) {
	ASTParser parser = createASTParser(cu);
	return parser.createBindings(javaElements, null);
    }

    public static boolean isMethodNameUnique(IMethod method)
	    throws JavaModelException {
	int methodCount = 0;

	IType type = (IType) method.getParent();
	IMethod[] methods = type.getMethods();

	for (IMethod tmpMethod : methods) {
	    if (tmpMethod.getElementName().equals(method.getElementName())) {
		methodCount++;
	    }
	}

	return methodCount <= 1;
    }

    /**
     * @param p
     *            the package fragment; may not be null
     * @return the parent package fragment, or null if the given package
     *         fragment is the default package or a top level package
     */
    public static IPackageFragment getParentPackage(IPackageFragment p) {
	if (p.isDefaultPackage()) {
	    return null;
	}

	final int index = p.getElementName().lastIndexOf('.');
	if (index == -1) {
	    return null;
	}

	final IPackageFragmentRoot root = (IPackageFragmentRoot) p.getParent();
	final String parentPackageName = p.getElementName().substring(0, index);

	final IPackageFragment parent = root
		.getPackageFragment(parentPackageName);

	if (parent.exists()) {
	    return parent;
	} else {
	    return null;
	}
    }

    /**
     * Deletes the packages if no sub-packages exists and all parent-packages
     * without any compilation-unit.
     * 
     * @param packageToDelete
     * @throws JavaModelException
     */
    public static void deletePackages(IPackageFragment packageToDelete)
	    throws JavaModelException {
	if (!packageToDelete.hasSubpackages()) {
	    deletePackagesWithParents(packageToDelete);
	}
    }

    public static void deletePackagesWithParents(
	    IPackageFragment packageToDelete) throws JavaModelException {

	if (packageToDelete == null) {
	    return;
	}

	packageToDelete.delete(true, null);

	IPackageFragment parentPackage = getParentPackage(packageToDelete);

	if (parentPackage.getCompilationUnits().length == 0) {
	    deletePackagesWithParents(parentPackage);
	}
    }

    public static boolean isParentPackage(String parentPackage,
	    String childPackage) {
	int parentPackageSegmentSize = parentPackage.split("\\.").length;
	int childPackageSegmentSize = childPackage.split("\\.").length;

	if (parentPackageSegmentSize != childPackageSegmentSize
		&& childPackage.startsWith(parentPackage)) {
	    return true;
	}

	return false;
    }

    public static String createClassCreationChain(IType baseType,
	    StringBuilder classCreationChain,
	    List<ITestDataFactory> testDataFactories) {
	try {
	    // begin creation block
	    classCreationChain.append("new ").append(baseType.getElementName())
		    .append("(");

	    // create chain for the first constructor
	    for (IMethod method : baseType.getMethods()) {
		if (method.isConstructor()) {
		    createClassCreationChain(baseType, method,
			    classCreationChain, testDataFactories);
		    break;
		}
	    }

	} catch (Exception e) {
	    // TODO error log
	    classCreationChain.append("null");
	} finally {
	    // end creation block
	    classCreationChain.append(")");
	}

	return classCreationChain.toString();
    }

    private static void createClassCreationChain(IType baseType,
	    IMethod constructor, StringBuilder classCreationChain,
	    List<ITestDataFactory> testDataFactories) throws JavaModelException {
	// add parameters
	ILocalVariable[] params = constructor.getParameters();

	String fullQualifiedName;
	IType paramType;

	if (params != null && params.length != 0) {
	    String typeSignature;
	    String comma = "";
	    for (ILocalVariable param : params) {
		classCreationChain.append(comma);

		typeSignature = param.getTypeSignature();
		int typeSignatureKind = Signature
			.getTypeSignatureKind(typeSignature);
		String signatureSimpleName = Signature
			.getSignatureSimpleName(typeSignature);

		// if simple type
		if (Signature.BASE_TYPE_SIGNATURE == typeSignatureKind
			|| isString(signatureSimpleName)) {
		    if (!TYPE_VOID.equals(Signature
			    .getSimpleName(typeSignature))) {
			classCreationChain.append(decorateValue(
				JDTUtils.createInitValue(signatureSimpleName),
				signatureSimpleName));
		    }
		} else {
		    paramType = null;
		    fullQualifiedName = getFullQualifiedName(baseType, param);

		    try {
			paramType = baseType.getJavaProject().findType(
				fullQualifiedName);
		    } catch (JavaModelException e) {
			paramType = null;
		    }

		    if (paramType != null) {
			boolean creationDone = false;
			// if it is a special type
			for (ITestDataFactory testDataFactory : testDataFactories) {
			    creationDone = testDataFactory.createTypeCreation(
				    paramType, classCreationChain);
			}

			if (paramType.getFullyQualifiedName().equals(
				baseType.getFullyQualifiedName())) {
			    classCreationChain.append("null");
			} else if (!creationDone) {
			    createClassCreationChain(paramType,
				    classCreationChain, testDataFactories);
			}
		    } else {
			classCreationChain.append("null");
		    }

		}

		comma = ", ";
	    }
	}
    }

    /**
     * Returns if the modifiers are equal.
     * 
     * @param method
     * @param modifier
     * @return true if the modifiers are equal
     */
    public static boolean isMethodModifierEqual(IMethod method, String modifier) {
	String methodModifier;
	try {
	    methodModifier = getMethodModifier(method);
	} catch (JavaModelException e) {
	    return false;
	}

	if (methodModifier.equals(modifier)) {
	    return true;
	}

	return false;
    }

    /**
     * @param method
     * @return true if method is static
     * @throws JavaModelException
     */
    public static boolean isStatic(IMethod method) throws JavaModelException {
	return Flags.isStatic(method.getFlags());
    }

    /**
     * Returns if the type is a number.
     * 
     * @param type
     * @return true if type is a number
     */
    public static boolean isNumber(String type) {
	if (isInt(type) || isInteger(type) || isDouble(type) || isFloat(type)) {
	    return true;
	}

	return false;
    }

    /**
     * Returns if the type is String.
     * 
     * @param type
     * @return true if type is String
     */
    public static boolean isString(String type) {
	if (type.startsWith(TYPE_STRING)) {
	    return true;
	}

	return false;
    }

    public static String createReturnType(String returnType) {

	if (returnType != null && !returnType.equals("V")) {
	    returnType = Signature.getSignatureSimpleName(returnType);
	} else {
	    returnType = TYPE_VOID;
	}

	return returnType;
    }

    /**
     * Do the type specific formats for the value if length greater than 0 or
     * not null otherwise a formated initialized-value is created.
     * 
     * @param value
     *            value to format
     * @param type
     *            JUT-type (mandatory parameter)
     * @return formated an if value
     */
    public static String formatValue(String value, String type) {
	String resultValue = "";

	if (value == null || value.length() == 0) {
	    resultValue = createInitValue(type);
	    resultValue = decorateValue(resultValue, type);
	} else {
	    if (isArray(type)) {
		String[] values = value.split(",");
		boolean firstValue = true;

		for (String tmpValue : values) {
		    tmpValue = tmpValue.trim();

		    if ("".equals(tmpValue)) {
			tmpValue = createInitValue(type);
		    }

		    tmpValue = decorateValue(tmpValue, type);

		    if (firstValue) {
			resultValue = tmpValue;
			firstValue = false;
		    } else {
			resultValue = resultValue + (", ") + tmpValue;
		    }
		}
	    } else {
		resultValue = decorateValue(value, type);
	    }
	}

	if (isArray(type)) {
	    resultValue = "new " + type + "{" + resultValue + "}";
	}

	return resultValue;
    }

    /**
     * Creates a initializer value.
     * 
     * @param type
     * @return initializer value
     */
    public static String createInitValue(String type) {
	return createInitValue(type, false);
    }

    /**
     * Creates a initializer value.
     * 
     * @param type
     * @return initializer value
     */
    public static String createInitValue(String type, boolean formatValue) {
	String value;

	if (isString(type) || isChar(type)) {
	    value = "";
	} else if (isByte(type)) {
	    value = " ";
	} else if (isBoolean(type)) {
	    value = "false";
	} else if (isNumber(type)) {
	    if (isDouble(type)) {
		value = "0.0";
	    } else {
		value = "0";
	    }
	} else {
	    value = "null";
	}

	if (formatValue) {
	    value = formatValue(value, type);
	}

	return value;
    }

    public static String createParamList(IMethod method)
	    throws JavaModelException {
	return createParamList(method, true);
    }

    public static String createParamList(IMethod method, boolean withParamTypes)
	    throws JavaModelException {
	StringBuilder params = new StringBuilder();
	String comma = "";
	for (ILocalVariable variable : method.getParameters()) {
	    params.append(comma);
	    comma = ", ";

	    if (withParamTypes) {
		params.append(
			Signature.getSignatureSimpleName(variable
				.getTypeSignature())).append(" ");
	    }

	    params.append(variable.getElementName());
	}

	return params.toString();
    }

    /**
     * Decorates the value depend on the type.
     * 
     * @param value
     * @param type
     * @return decorated value
     */
    public static String decorateValue(String value, String type) {
	if (isString(type)) {
	    value = QUOTES + value + QUOTES;
	} else if (isChar(type) || isByte(type)) {
	    if (value.length() == 0) {
		value = " ";
	    }
	    value = "'" + value + "'";
	}

	return value;
    }

    /**
     * Returns if the type is a array.
     * 
     * @param type
     * @return true if type is a array
     */
    public static boolean isArray(String type) {
	if (type.endsWith(TYPE_ARRAY)) {
	    return true;
	}

	return false;
    }

    /**
     * Returns if the type is boolean.
     * 
     * @param type
     * @return true if type is boolean
     */
    public static boolean isBoolean(String type) {
	if (type.startsWith(TYPE_BOOLEAN)) {
	    return true;
	}

	return false;
    }

    /**
     * Returns if the type is char.
     * 
     * @param type
     * @return true if type is char
     */
    public static boolean isChar(String type) {
	if (type.startsWith(TYPE_CHAR)) {
	    return true;
	}

	return false;
    }

    public static boolean isByte(String type) {
	if (type.startsWith(TYPE_BYTE)) {
	    return true;
	}

	return false;
    }

    /**
     * Returns if the type is double.
     * 
     * @param type
     * @return true if type is double
     */
    public static boolean isDouble(String type) {
	if (type.startsWith(TYPE_DOUBLE)) {
	    return true;
	}

	return false;
    }

    /**
     * Returns if the type is float.
     * 
     * @param type
     * @return true if type is float
     */
    public static boolean isFloat(String type) {
	if (type.startsWith(TYPE_FLOAT)) {
	    return true;
	}

	return false;
    }

    /**
     * Returns if the type is int.
     * 
     * @param type
     * @return true if type is int
     */
    public static boolean isInt(String type) {
	if (type.startsWith(TYPE_INT)) {
	    return true;
	}

	return false;
    }

    /**
     * Returns if the type is Integer.
     * 
     * @param type
     * @return true if type is Integer
     */

    public static boolean isInteger(String type) {
	if (type.startsWith(TYPE_INTEGER)) {
	    return true;
	}

	return false;
    }

    public static IMethod getSelectedMethod(IFileEditorInput fileEditorInput)
	    throws JavaModelException {
	ITextEditor editor = (ITextEditor) EclipseUIUtils.getActiveEditor();
	ITextSelection sel = (ITextSelection) editor.getSelectionProvider()
		.getSelection();
	ITypeRoot typeRoot = JavaUI.getEditorInputTypeRoot(editor
		.getEditorInput());
	ICompilationUnit icu = (ICompilationUnit) typeRoot
		.getAdapter(ICompilationUnit.class);
	CompilationUnit cu = createASTRoot(icu);
	NodeFinder finder = new NodeFinder(cu, sel.getOffset(), sel.getLength());

	ASTNode node = finder.getCoveringNode();

	MethodDeclaration methodDeclaration = getASTMethod(node);
	if (methodDeclaration == null) {
	    return null;
	}

	IMethodBinding methodBinding = methodDeclaration.resolveBinding();

	IMethod selectedMethod = null;
	if (methodBinding != null) {
	    selectedMethod = (IMethod) methodBinding.getJavaElement();
	}

	return selectedMethod;
    }

    public static IMethod getSelectedMethod() throws JavaModelException {
	IWorkbenchPage page = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();
	ITextEditor editor = (ITextEditor) page.getActiveEditor();
	IJavaElement elem = JavaUI.getEditorInputJavaElement(editor
		.getEditorInput());
	if (elem instanceof ICompilationUnit) {
	    ITextSelection sel = (ITextSelection) editor.getSelectionProvider()
		    .getSelection();
	    IJavaElement selected = ((ICompilationUnit) elem).getElementAt(sel
		    .getOffset());
	    if (selected != null
		    && selected.getElementType() == IJavaElement.METHOD) {
		return (IMethod) selected;
	    }
	}

	return null;
    }

    public static Vector<IJavaElement> getCompilationUnits(
	    ISelection selection, IFileEditorInput fileEditorInput)
	    throws JavaModelException, JUTWarning {
	Vector<IJavaElement> javaElements = getJavaElements(selection,
		fileEditorInput);

	return javaElements;
    }

    /**
     * Collect the compilation units from the selection.
     * 
     * @param selection
     * @param fileEditorInput
     * @return all compilationUnits in the selection
     * @throws JavaModelException
     * @throws JUTWarning
     */
    public static Vector<IJavaElement> getJavaElements(ISelection selection,
	    IFileEditorInput fileEditorInput, boolean withBinary)
	    throws JavaModelException, JUTWarning {

	Object[] selectionArray;

	if (fileEditorInput != null) {
	    selectionArray = new Object[1];
	    selectionArray[0] = fileEditorInput;
	} else {
	    IStructuredSelection structuredSelection;

	    if (selection instanceof IStructuredSelection) {
		structuredSelection = ((IStructuredSelection) selection);
	    } else
		return null;

	    selectionArray = structuredSelection.toArray();
	}

	Vector<IJavaElement> selectedCompilationUnits = new Vector<IJavaElement>();

	for (Object tmpSelection : selectionArray) {
	    if (tmpSelection instanceof IJavaElement) {
		IJavaElement javaElement = (IJavaElement) tmpSelection;
		collectCompilationUnits(javaElement, selectedCompilationUnits);
	    } else if (tmpSelection instanceof IParent) {
		IParent parentSelection = (IParent) tmpSelection;
		IJavaElement[] javaElements = parentSelection.getChildren();

		for (IJavaElement javaElement : javaElements) {
		    collectCompilationUnits(javaElement,
			    selectedCompilationUnits);
		}
	    } else if (tmpSelection instanceof IFileEditorInput) {
		IFileEditorInput fileEditorInputTmp = (IFileEditorInput) tmpSelection;
		IFile ifile = fileEditorInputTmp.getFile();

		if (ifile.getName().endsWith(".java")) { //$NON-NLS-1$
		    ICompilationUnit cu = JavaCore
			    .createCompilationUnitFrom(ifile);
		    selectedCompilationUnits.add(cu);
		} else {
		    throw new JUTWarning(Messages.GeneratorUtils_SelectionEnd);
		}
	    }
	}

	return selectedCompilationUnits;
    }

    public static Vector<IJavaElement> getJavaElements(ISelection selection,
	    IFileEditorInput fileEditorInput) throws JavaModelException,
	    JUTWarning {
	return getJavaElements(selection, fileEditorInput, false);
    }

    public static Vector<IJavaElement> getCompilationUnits(ISelection selection)
	    throws JavaModelException, JUTWarning {
	if (selection instanceof IStructuredSelection) {
	    return getCompilationUnits(selection, null);
	} else {
	    IEditorInput editorInput = EclipseUIUtils.getEditorInput();

	    if (editorInput instanceof IFileEditorInput) {

		return getCompilationUnits(null, (IFileEditorInput) editorInput);

	    }
	}

	return new Vector<IJavaElement>();
    }

}
