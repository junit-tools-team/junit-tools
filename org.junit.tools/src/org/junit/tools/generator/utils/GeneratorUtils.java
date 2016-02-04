package org.junit.tools.generator.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.junit.tools.base.MethodRef;
import org.junit.tools.generator.IGeneratorConstants;
import org.junit.tools.generator.model.tml.Method;
import org.junit.tools.generator.model.tml.Param;
import org.junit.tools.preferences.JUTPreferences;

/**
 * This class provides help methods for the generation of test- and
 * mock-classes.
 * 
 * @author Robert Streng
 * 
 */
public class GeneratorUtils implements IGeneratorConstants {

    /**
     * Creates a test-method-name.
     * 
     * @param methodName
     * @return test-method-name
     */
    public static String createTestMethodName(String methodName) {
	return createMethodName(methodName,
		JUTPreferences.getTestMethodPrefix());
    }

    /**
     * Creates a method-name with a prefix.
     * 
     * @param methodName
     * @param methodPrefix
     * @return method-name with prefix
     */
    public static String createMethodName(String methodName, String methodPrefix) {
	return createMethodName(methodName, methodPrefix, true);
    }

    /**
     * Concats or replaces the methodPrefix.
     * 
     * @param methodName
     * @param methodPrefix
     * @param concat
     *            if <code>true</code> the methodPrefix will be concat otherwise
     *            it will be replaced
     * @return method-name with or without prefix
     */
    public static String createMethodName(String methodName,
	    String methodPrefix, boolean concat) {
	if (concat) {
	    if (methodName.length() == 0) {
		return methodPrefix;
	    } else if (methodPrefix.length() == 0) {
		return methodName;
	    }

	    String firstLetter = String.valueOf(methodName.charAt(0))
		    .toUpperCase();
	    String restMethodName = methodName.substring(1);

	    methodName = methodPrefix + firstLetter + restMethodName;
	} else {
	    methodName = methodName.replaceFirst(methodPrefix, "");
	    String firstLetter = String.valueOf(methodName.charAt(0))
		    .toLowerCase();
	    String restMethodName = methodName.substring(1);

	    methodName = firstLetter + restMethodName;
	}

	return methodName;
    }

    public static String createMethodNameFromTest(String testMethodName) {
	return createMethodName(testMethodName,
		JUTPreferences.getTestMethodPrefix(), false);
    }

    /**
     * Creates a class-name.
     * 
     * @param className
     * @return class-name with a upper cased first letter
     */
    public static String createTestClassName(String className) {
	if (className.length() == 0)
	    return ""; //$NON-NLS-1$
	String firstLetter = String.valueOf(className.charAt(0)).toUpperCase();
	String restClassName = className.substring(1);

	String testClassPostfix = JUTPreferences.getTestClassPostfix();
	if (testClassPostfix == null) {
	    testClassPostfix = "";
	}

	return firstLetter + restClassName + testClassPostfix;
    }

    /**
     * Changes the first character to upper.
     * 
     * @param value
     * @return String with a upper first character
     */
    public static String firstCharToUpper(String value) {
	if (value != null && value.length() > 0)
	    return Character.toUpperCase(value.charAt(0)) + value.substring(1);

	return value;
    }

    /**
     * Changes the first character to lower.
     * 
     * @param value
     * @return String with a lower first character
     */
    public static String firstCharToLower(String value) {
	if (value != null && value.length() > 0)
	    return Character.toLowerCase(value.charAt(0)) + value.substring(1);

	return value;
    }

    /**
     * Compares the methods.
     * 
     * @param method
     * @param tmlMethod
     * @return true if the methods are equal
     */
    public static boolean compareMethods(IMethod method, Method tmlMethod) {
	List<Param> tmlParams;
	ILocalVariable[] parameters;
	String paramType;

	if (tmlMethod == null) {
	    if (method == null)
		return true;

	    return false;
	}

	if (tmlMethod.getName() == null) {
	    return false;
	}

	if (tmlMethod.getName().equals(method.getElementName())) {
	    tmlParams = tmlMethod.getParam();
	    try {
		parameters = method.getParameters();
	    } catch (JavaModelException e) {
		throw new RuntimeException(e);
	    }

	    // check parameters
	    if (tmlParams.size() == parameters.length) {
		for (int i = 0; i < tmlParams.size(); i++) {
		    paramType = Signature.getSignatureSimpleName(parameters[i]
			    .getTypeSignature());
		    if (!tmlParams.get(i).getType().equals(paramType)) {
			return false;
		    }
		}

		return true;
	    }

	}

	return false;
    }

    /**
     * Creates the annotation generated.
     * 
     * @return the created annotation
     */
    public static String createAnnoGenerated() {
	return ANNO_GENERATED + "(value=" + QUOTES + VERSION + QUOTES + ")"
		+ RETURN;
    }

    public static String createAnnoMethodRef(IMethod method)
	    throws JavaModelException {
	return createAnnoMethodRef(method.getElementName(),
		method.getSignature());
    }

    public static String createAnnoMethodRef(String methodName,
	    String methodSignature) {
	return "@MethodRef(name=\"" + methodName + "\", signature=\""
		+ methodSignature + "\")";
    }

    public static HashMap<MethodRef, IMethod> getExistingTestMethods(
	    ICompilationUnit cuBase, ICompilationUnit cu)
	    throws JavaModelException {
	return getExistingTestMethods(cuBase, cu, false);
    }

    public static HashMap<MethodRef, IMethod> getExistingTestMethods(
	    ICompilationUnit cu) throws JavaModelException {
	return getExistingTestMethods(null, cu, false);
    }

    public static HashMap<MethodRef, IMethod> getExistingTestMethods(
	    ICompilationUnit cuBase, ICompilationUnit cuWithRef,
	    boolean withByName) throws JavaModelException {
	HashMap<MethodRef, IMethod> existingMethods = new HashMap<MethodRef, IMethod>();

	if (cuWithRef == null) {
	    return existingMethods;
	}

	IType primaryType = cuWithRef.findPrimaryType();

	if (primaryType == null) {
	    return existingMethods;
	}

	IMethod[] methods = primaryType.getMethods();

	for (IMethod method : methods) {
	    MethodRef mr = getMethodRef(method);

	    if (mr != null) {
		existingMethods.put(mr, method);
	    } else {
		if (withByName) {
		    if (method.getElementName().startsWith(
			    JUTPreferences.getTestMethodPrefix())) {
			// find base method
			String baseMethodName = createMethodNameFromTest(method
				.getElementName());
			List<IMethod> baseMethods = JDTUtils.getMethods(cuBase,
				false, baseMethodName);

			if (baseMethods.size() == 1) {
			    IMethod baseMethod = baseMethods.get(0);
			    mr = new MethodRef(baseMethod.getElementName(),
				    baseMethod.getSignature());
			    mr.setSignatureNew(baseMethod.getSignature());
			    existingMethods.put(mr, method);
			} else {
			    mr = new MethodRef(baseMethodName, "");
			    mr.setUnresolvedConflict(true);
			    existingMethods.put(mr, method);
			}

		    }
		}
	    }
	}

	if (cuBase != null) {
	    checkSignatures(existingMethods, cuBase);
	}

	return existingMethods;
    }

    private static boolean matchesMethodRef(MethodRef methodRefToCheck,
	    IType typeBase, Set<MethodRef> allMethodRefs)
	    throws JavaModelException {

	List<IMethod> baseMethodsWithMatchingName = new ArrayList<IMethod>();

	for (IMethod method : typeBase.getMethods()) {
	    if (method.getElementName().equals(methodRefToCheck.getName())) {
		if (method.getSignature().equals(
			methodRefToCheck.getSignature())) {
		    return true;
		}

		baseMethodsWithMatchingName.add(method);
	    }
	}

	if (baseMethodsWithMatchingName.size() == 0) {
	    return false;
	} else if (baseMethodsWithMatchingName.size() == 1) {
	    methodRefToCheck.setSignatureNew(baseMethodsWithMatchingName.get(0)
		    .getSignature());
	} else {
	    // check if method-references are available for the other methods
	    boolean found;
	    List<IMethod> notMatchingBaseMethods = new ArrayList<IMethod>();

	    for (IMethod method : baseMethodsWithMatchingName) {
		found = false;
		for (MethodRef methodRefTmp : allMethodRefs) {
		    if (isMethodRefEqual(method, methodRefTmp)) {
			found = true;
			break;
		    }
		}

		if (found) {
		    continue;
		} else {
		    notMatchingBaseMethods.add(method);
		}

	    }

	    // if == 1, it is obvious
	    if (notMatchingBaseMethods.size() == 1) {
		methodRefToCheck.setSignatureNew(baseMethodsWithMatchingName
			.get(0).getSignature());
		return true;
	    } else {
		// unresolved method-reference conflict
		methodRefToCheck.setUnresolvedConflict(true);
		return true;
	    }
	}

	return false;
    }

    public static boolean isMethodRefEqual(IMethod method, MethodRef methodRef)
	    throws JavaModelException {
	return method.getElementName().equals(methodRef.getName())
		&& method.getSignature().equals(methodRef.getSignature());
    }

    public static MethodRef getMethodRef(IMethod method)
	    throws JavaModelException {
	if (method == null) {
	    return null;
	}

	IAnnotation annotation = method.getAnnotation("MethodRef");

	if (annotation != null && annotation.exists()) {

	    boolean found = false;

	    String methodName = "", methodSignature = "";

	    for (IMemberValuePair valuePair : annotation.getMemberValuePairs()) {
		String valueName = valuePair.getMemberName();
		String value = valuePair.getValue().toString();

		if ("name".equals(valueName)) {
		    methodName = value;
		    found = true;
		} else if ("signature".equals(valueName)) {
		    methodSignature = value;
		}

	    }

	    if (found) {
		MethodRef methodRef = new MethodRef(methodName, methodSignature);
		return methodRef;
	    }
	}

	return null;
    }

    public static List<IMethod> getMethodsToCreate(
	    HashMap<MethodRef, IMethod> existingMethods,
	    Vector<IMethod> checkedMethods) throws JavaModelException {
	List<IMethod> methodsToCreate = new ArrayList<IMethod>();

	if (existingMethods == null || existingMethods.size() == 0) {
	    return checkedMethods;
	}

	boolean found;

	for (IMethod checkedMethod : checkedMethods) {
	    found = false;

	    for (MethodRef methodRef : existingMethods.keySet()) {
		if (methodRef.getName().equals(checkedMethod.getElementName())) {
		    if (methodRef.getSignatureToCompare().equals(
			    checkedMethod.getSignature())) {
			found = true;
			break;
		    }
		}
	    }

	    if (!found) {
		methodsToCreate.add(checkedMethod);
	    }

	}

	return methodsToCreate;
    }

    /**
     * @param type
     * @param b
     * @return a random value for the type
     */
    public static String createRandomValue(String type, boolean formatValue) {
	String value;

	if (JDTUtils.isString(type) || JDTUtils.isChar(type)) {
	    value = "A";
	} else if (JDTUtils.isByte(type)) {
	    value = "A";
	} else if (JDTUtils.isBoolean(type)) {
	    value = "false";
	} else if (JDTUtils.isNumber(type)) {
	    if (JDTUtils.isDouble(type)) {
		value = "100.0";
	    } else {
		value = "1";
	    }
	} else {
	    value = "null";
	}

	if (formatValue) {
	    value = JDTUtils.formatValue(value, type);
	}

	return value;
    }

    public static List<String> createCombination(String type) {
	String initValue = JDTUtils.createInitValue(type);
	return createCombination(type, initValue);
    }

    public static List<String> createCombination(String type, String initValue) {
	List<String> combs = new ArrayList<String>();

	// add init value
	combs.add(initValue);

	// add combinations
	if (JDTUtils.isString(type) || JDTUtils.isChar(type)) {
	    combs.add("null");
	    combs.add("abc");
	} else if (JDTUtils.isByte(type)) {
	    combs.add("A");
	} else if (JDTUtils.isBoolean(type)) {
	    combs.add("true");
	} else if (JDTUtils.isNumber(type)) {
	    if (JDTUtils.isDouble(type)) {
		double value;
		try {
		    value = Double.parseDouble(initValue);
		} catch (NumberFormatException exception) {
		    value = 0.0;
		}

		combs.add(Double.toString(value - 1));
		combs.add(Double.toString(value + 1));
	    } else {
		int value;
		try {
		    value = Integer.parseInt(initValue);
		} catch (NumberFormatException exception) {
		    value = 0;
		}

		combs.add(Integer.toString(value - 1));
		combs.add(Integer.toString(value + 1));
	    }
	}

	for (int i = 0; i < combs.size(); i++) {
	    String value = combs.get(i);
	    combs.set(i, JDTUtils.formatValue(value, type));
	}

	return combs;
    }

    public static boolean checkMethodReference(String baseMethodName,
	    String baseMethodSignature, IMethod methodToCheck)
	    throws JavaModelException {
	IAnnotation anno = methodToCheck.getAnnotation("MethodRef");

	if (anno == null || !anno.exists()) {
	    return false;
	}

	IMemberValuePair[] valuePair = anno.getMemberValuePairs();
	boolean signatureFound = false, nameFound = false;

	for (IMemberValuePair vP : valuePair) {
	    Object o = vP.getValue();
	    if (o != null) {
		String methodRefValue = o.toString();

		if ("signature".equals(vP.getMemberName())) {
		    if (methodRefValue.equals(baseMethodSignature)) {
			signatureFound = true;
		    } else {
			return false;
		    }
		} else if ("name".equals(vP.getMemberName())) {
		    if (methodRefValue.equals(baseMethodName)) {
			nameFound = true;
		    } else {
			return false;
		    }
		}
	    }
	}

	return signatureFound && nameFound;

    }

    public static boolean checkMethodReferenceAndName(String baseMethodName,
	    String baseMethodSignature, String nameToCheck,
	    IMethod methodToCheck) throws JavaModelException {
	// check by method-reference
	if (checkMethodReference(baseMethodName, baseMethodSignature,
		methodToCheck)) {
	    return true;
	}

	// check by name

	if (nameToCheck != null
		&& nameToCheck.equals(methodToCheck.getElementName())) {
	    return true;
	}

	return false;
    }

    public static void checkSignatures(
	    HashMap<MethodRef, IMethod> existingMethods, ICompilationUnit cuBase)
	    throws JavaModelException {

	IType typeBase = cuBase.findPrimaryType();

	for (Entry<MethodRef, IMethod> entry : existingMethods.entrySet()) {
	    if (matchesMethodRef(entry.getKey(), typeBase,
		    existingMethods.keySet())) {
		continue;
	    }

	    // handle conflict
	}

    }

    public static IMethod findMethod(Collection<IMethod> methods,
	    MethodRef methodRef) throws JavaModelException {
	boolean nameMatched;
	IMethod nameMatchedMethod = null;
	int nameMatchedCounter = 0;
	
	for (IMethod method : methods) {

	    nameMatched = false;

	    if (methodRef.getName().equals(method.getElementName())) {
		if (methodRef.getSignature().equals(method.getSignature())) {
		    return method;
		} else {
		    nameMatched = true;
		}
	    }

	    MethodRef methodRefTarget = getMethodRef(method);
	    String baseMethodName = createMethodNameFromTest(methodRef
		    .getName());
	    if (methodRefTarget != null
		    && methodRefTarget.getName().equals(baseMethodName)
		    && methodRefTarget.getSignatureToCompare().equals(
			    methodRef.getSignatureToCompare())) {
		return method;
	    }

	    if (nameMatched) {
		nameMatchedCounter++;
		nameMatchedMethod = method;
	    }

	}

	if (nameMatchedCounter == 1) {
	    return nameMatchedMethod;
	}
	
	return null;
    }

    /**
     * @param methodRefs
     * @param method
     * @return the
     * @throws JavaModelException
     */
    public static IMethod findMethod(Collection<MethodRef> methodRefs,
	    IMethod method) throws JavaModelException {
	for (MethodRef methodRef : methodRefs) {
	    if (methodRef.getName().equals(method.getElementName())
		    && methodRef.getSignature().equals(method.getSignature())) {
		return method;
	    }
	}

	return null;
    }

    public static List<IMethod> getMethodsToDelete(
	    HashMap<MethodRef, IMethod> existingMethods,
	    Vector<IMethod> checkedMethods) throws JavaModelException {
	List<IMethod> methodsToDelete = new ArrayList<IMethod>();
	boolean found;

	if (existingMethods == null) {
	    return methodsToDelete;
	}

	MethodRef methodRef;
	for (Entry<MethodRef, IMethod> method : existingMethods.entrySet()) {
	    found = false;
	    methodRef = method.getKey();

	    if (methodRef.isUnresolvedConflict()) {
		continue;
	    }

	    for (IMethod checkedMethod : checkedMethods) {
		if (methodRef.isEquals(checkedMethod)) {
		    found = true;
		    break;

		}
	    }

	    if (!found) {
		methodsToDelete.add(method.getValue());
	    }

	}

	return methodsToDelete;
    }

    /**
     * Searches the closest method.
     * 
     * @param method
     * @param tmlMethods
     * @return closest method
     */
    public static Method getClosestMethod(IMethod method,
	    List<Method> tmlMethods) {
	Method tmlMethodTmp = null;
	for (Method tmlMethod : tmlMethods) {
	    if (compareMethods(method, tmlMethod)) {
		if (JDTUtils.isMethodModifierEqual(method,
			tmlMethod.getModifier())) {
		    return tmlMethod;
		}
		tmlMethodTmp = tmlMethod;
	    }
	}

	return tmlMethodTmp;
    }

}
