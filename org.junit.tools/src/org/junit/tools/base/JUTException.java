package org.junit.tools.base;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Exception-class for errors.
 * 
 * @author JUnit-Tools-Team
 */
public class JUTException extends Exception {

    private static final long serialVersionUID = -7377779767226130892L;

    public JUTException(String message) {
	super(message);
    }

    public JUTException(Throwable exception) {
	super(ExceptionUtils.getFullStackTrace(exception));
    }

}
