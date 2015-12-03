package org.junit.tools.base;

/**
 * Exception-class for warnings.
 * 
 * @author Robert Streng
 */
public class JUTWarning extends Exception {

    private static final long serialVersionUID = -1584284318773929357L;

    public JUTWarning(String message) {
	super(message);
    }

}
