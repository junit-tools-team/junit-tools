package org.junit.tools.generator.model;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.junit.tools.base.MethodRef;
import org.junit.tools.generator.model.tml.Method;
import org.junit.tools.generator.model.tml.Test;

/**
 * Generator-model with the JUnit-Tools-Elements and the TML-file.
 * 
 * @author Robert Streng
 * 
 */
public class GeneratorModel {

    private JUTElements jutElements;
    private Test tmlTest;

    private HashMap<IMethod, Method> methodMap = new HashMap<IMethod, Method>();
    private List<IMethod> methodsToCreate;
    private List<IMethod> methodsToDelete;
    private HashMap<MethodRef, IMethod> existingMethods;

    /**
     * @param jutElements
     * @param tmlTest
     */
    public GeneratorModel(JUTElements jutElements, Test tmlTest) {
	this.jutElements = jutElements;
	this.tmlTest = tmlTest;
    }

    /**
     * @return JUT-elements
     */
    public JUTElements getJUTElements() {
	return jutElements;
    }

    /**
     * @param jutElements
     */
    public void setJUTElements(JUTElements jutElements) {
	this.jutElements = jutElements;
    }

    /**
     * @return TML-Test
     */
    public Test getTmlTest() {
	return tmlTest;
    }

    /**
     * @param tmlTest
     */
    public void setTmlTest(Test tmlTest) {
	this.tmlTest = tmlTest;
    }

    /**
     * @return the method map
     */
    public HashMap<IMethod, Method> getMethodMap() {
	return methodMap;
    }

    /**
     * @param methodMap
     */
    public void setMethodMap(HashMap<IMethod, Method> methodMap) {
	this.methodMap = methodMap;
    }

    public void setMethodsToCreate(List<IMethod> methodsToCreate) {
	this.methodsToCreate = methodsToCreate;
    }

    public void setMethodsToDelete(List<IMethod> methodsToDelete) {
	this.methodsToDelete = methodsToDelete;
    }

    public List<IMethod> getMethodsToCreate() {
	return methodsToCreate;
    }

    public List<IMethod> getMethodsToDelete() {
	return methodsToDelete;
    }

    public void setExistingMethods(HashMap<MethodRef, IMethod> existingMethods) {
	this.existingMethods = existingMethods;
    }

    public HashMap<MethodRef, IMethod> getExistingMethods() {
	return existingMethods;
    }
}
