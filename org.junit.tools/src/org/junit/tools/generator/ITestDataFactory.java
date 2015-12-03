package org.junit.tools.generator;

import org.eclipse.jdt.core.IType;

/**
 * Interface for a test-data-factory. With a test-data-factory a special
 * creation-code can be added for special types.
 * 
 * @author Robert Streng
 * 
 */
public interface ITestDataFactory {

    /**
     * Creates the creation for the given param-type.
     * 
     * @param paramType
     * @param classCreationChain
     * @return true if the creation was done
     */
    boolean createTypeCreation(IType paramType, StringBuilder classCreationChain);

}
