package org.junit.tools.generator;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.junit.tools.generator.model.GeneratorModel;

/**
 * Interface for the test-class-generators.
 * 
 * @author Robert Streng
 */
public interface ITestClassGenerator {
    /**
     * Generates a test class with the model. The base is a class, general
     * options and the tml-model.
     * 
     * @param model
     * @param testDataFactories
     * @param monitor
     * @return the generate test class as saved file
     * @throws Exception
     */
    public ICompilationUnit generate(GeneratorModel model,
	    List<ITestDataFactory> testDataFactories, IProgressMonitor monitor)
	    throws Exception;

    public static final String ANNO_TESTPRIO_NAME = "Testprio";

    public static final String ANNO_TESTPRIO = "@" + ANNO_TESTPRIO_NAME;

    public static final String ANNO_JUNIT_TEST = "@Test";

    public static final String ANNO_JUNIT_BEFORE = "@Before";

    public static final String ANNO_JUNIT_BEFORE_CLASS = "@BeforeClass";

    public static final String ANNO_JUNIT_AFTER = "@After";

    public static final String ANNO_JUNIT_AFTER_CLASS = "@AfterClass";

    public final static String FAIL_ASSERTION = "Assert.fail();";

    public final static String TESTSUBJECT_METHOD_PREFIX = "createTestSubject";

    public final static String STANDARD_METHOD_BEFORE = "setUp";

    public final static String STANDARD_METHOD_BEFORE_ClASS = "setUpBeforeClass";

    public final static String STANDARD_METHOD_AFTER = "tearDown";

    public final static String STANDARD_METHOD_AFTER_CLASS = "tearDownAfterClass";

    public final static String EXCEPTION = "Exception";

}
