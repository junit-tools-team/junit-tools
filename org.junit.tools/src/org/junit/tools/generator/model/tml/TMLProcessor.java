package org.junit.tools.generator.model.tml;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.tools.base.JUTException;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.preferences.JUTPreferences;

/**
 * TML-Processor to read and write TML-Files.
 * 
 * @author Robert Streng
 * 
 */
public class TMLProcessor {

    private IFile tmlFile;
    private String testPackageName;
    private String testClassName;
    private IJavaProject testProject;

    public TMLProcessor(IJavaProject testProject, String testPackageName,
	    String testClassName) {
	this.testProject = testProject;
	this.testPackageName = testPackageName;
	this.testClassName = testClassName;

	if (testProject == null)
	    return;

	initializeTMLFile(testProject);
    }

    /**
     * Sets the test project.
     * 
     * @param testProject
     */
    public void setTestProject(IJavaProject testProject) {
	if (testProject == null && testProject != this.testProject) {
	    this.testProject = testProject;
	    initializeTMLFile(testProject);
	}
    }

    /**
     * Initializes the TML-File.
     * 
     * @param testProject
     */
    private void initializeTMLFile(IJavaProject testProject) {
	// get TML-Container
	String tmlContainerName = JUTPreferences.getTmlContainer();
	IFolder tmlContainer = JDTUtils.createFolder(testProject,
		tmlContainerName);

	// get TML-file
	if (!tmlContainer.exists()) {
	    throw new RuntimeException("TML-container not found!");
	}

	tmlFile = tmlContainer.getFile(testPackageName.replace(".", "_") + "_"
		+ testClassName + ".tml");
    }

    /**
     * Read the TML-file and create the TML-model.
     * 
     * @return ClassProcessingInstructions
     * @throws Exception
     */
    public Test readTmlFile() throws JUTException {
	if (tmlFile == null || !tmlFile.exists()) {
	    return null;
	}

	Test test = null;

	try {
	    tmlFile.refreshLocal(0, null);

	    JAXBContext context = JAXBContext.newInstance(Test.class);
	    Unmarshaller unmarshaller = context.createUnmarshaller();

	    test = (Test) unmarshaller.unmarshal(tmlFile.getContents());

	} catch (Exception e) {
	    throw new JUTException(e);
	}

	return test;
    }

    /**
     * Writes the TML-model to a TML-file.
     * 
     * @param test
     * @throws Exception
     */
    public void writeTmlFile(Test test) throws Exception {
	if (tmlFile == null) {
	    throw new JUTException("TML-file is not initialized!");
	}

	// create TML-file
	if (!tmlFile.exists()) {
	    byte[] bytes = "".getBytes();
	    InputStream source = new ByteArrayInputStream(bytes);
	    tmlFile.create(source, IResource.NONE, null);
	    source.close();
	}

	JAXBContext context = JAXBContext.newInstance(Test.class);
	Marshaller marshaller = context.createMarshaller();
	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	FileWriter fileWriter = new FileWriter(tmlFile.getRawLocation()
		.makeAbsolute().toFile());
	marshaller.marshal(test, fileWriter);
	fileWriter.flush();
	fileWriter.close();

	tmlFile.refreshLocal(0, null);
    }

}
