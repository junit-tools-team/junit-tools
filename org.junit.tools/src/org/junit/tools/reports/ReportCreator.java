package org.junit.tools.reports;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.tools.base.JUTException;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.base.MethodAnalyzer;
import org.junit.tools.base.MethodAnalyzer.MethodAnalyzeResult;
import org.junit.tools.generator.model.JUTElements;
import org.junit.tools.generator.model.tml.Testprio;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.reports.model.NewMethods;
import org.junit.tools.reports.model.ObjectFactory;
import org.junit.tools.reports.model.RepClass;
import org.junit.tools.reports.model.RepMethod;
import org.junit.tools.reports.model.RepPackage;
import org.junit.tools.reports.model.RepProject;
import org.junit.tools.reports.model.Report;
import org.junit.tools.reports.model.Statistics;

/**
 * This class creates a report (a xml file with trml-scheme) for the junit-tests
 * and saves it in the report container (default is reports).
 * 
 * It searches after all methods with logic and no junit-tests. New methods are
 * listed in a special block. A statistic on method layer is included too.
 * 
 * @author JUnit-Tools-Team
 * 
 */
// TODO refactor
public class ReportCreator {

    private final ObjectFactory of = new ObjectFactory();

    private String todayDate = "";

    public void createNecessaryTestclassesReport(JUTElements elements,
	    IProgressMonitor monitor) throws JUTWarning, JavaModelException,
	    JUTException {

	IJavaProject baseProject = elements.getProjects().getBaseProject();
	IJavaProject testProject = elements.getProjects().getTestProject();

	// read old report
	RepProject oldReport = null;
	try {
	    oldReport = readReport(testProject);
	} catch (Exception e) {
	    throw new JUTException(e);
	}

	// statistic
	int necessaryTestClassesCounter = 0;
	int necessaryTestMethodsCounter = 0;
	int availableTestClassesCounter = 0;
	int availableTestMethodsCounter = 0;
	int newMethodsCounter = 0;

	MethodAnalyzer ma = new MethodAnalyzer();

	if (testProject != null && testProject.exists()) {

	    monitor.beginTask("create report", 15);
	    int monitorCounter = 0;

	    RepProject repProject = of.createRepProject();
	    repProject.setId(baseProject.getElementName());

	    JUTElements utmElementsFromBase = JUTElements
		    .initJUTElements(baseProject);

	    for (IPackageFragment pf : baseProject.getPackageFragments()) {

		RepPackage repPackage = null;
		RepPackage oldRepPackage = null;

		for (ICompilationUnit baseCu : pf.getCompilationUnits()) {
		    monitorCounter++;
		    if (monitorCounter > 10) {
			monitorCounter = 0;
			if (incrementTask(monitor, 1)) {
			    return;
			}
		    }

		    Map<IMethod, MethodAnalyzeResult> analyzedMethods = ma
			    .analyzeAllMethods(baseCu);

		    boolean testClassNecessary = false;
		    boolean testClassAvailable = false;

		    // create report for cu
		    if (analyzedMethods.size() > 0) {
			RepClass repClass = null;
			RepClass oldRepClass = null;

			// init test-elements and check if test-class is
			// available
			ICompilationUnit testCu = null;
			try {
			    utmElementsFromBase.initClassesAndPackages(baseCu);
			    testCu = utmElementsFromBase
				    .getClassesAndPackages().getTestClass();
			    if (testCu != null && testCu.exists()) {
				testClassAvailable = true;
			    }
			} catch (Exception e) {
			    throw new RuntimeException(e);
			}

			// create report entries
			RepMethod repMethod;
			for (Entry<IMethod, MethodAnalyzeResult> analyzedMethodEntry : analyzedMethods
				.entrySet()) {

			    // check if test-method is already available
			    boolean isTestMethodAvailable = false;
			    IMethod method = analyzedMethodEntry.getKey();
			    MethodAnalyzeResult mar = analyzedMethodEntry
				    .getValue();
			    String subtype = "";

			    // TODO prefs - write only new method tag if
			    // test-prio > low
			    if (mar.getNumberOfIfStatements() < 2
				    && mar.isOnlyNullChecks()
				    && mar.getTestPrio() == Testprio.LOW) {
				continue;
			    }

			    necessaryTestMethodsCounter++;

			    if (testCu != null && testCu.exists()) {
				for (IMethod testMethod : testCu
					.findPrimaryType().getMethods()) {
				    if (GeneratorUtils
					    .checkMethodReferenceAndName(
						    method.getElementName(),
						    method.getSignature(),
						    GeneratorUtils
							    .createTestMethodName(method
								    .getElementName()),
						    testMethod)) {
					isTestMethodAvailable = true;
					break;
				    }
				}
			    }

			    // create report-entry only if necessary
			    // test-methods are not available
			    if (isTestMethodAvailable) {
				testClassNecessary = true;
				availableTestMethodsCounter++;
			    } else {
				// get/create package
				if (repPackage == null) {
				    repPackage = of.createRepPackage();
				    repPackage.setId(pf.getElementName());
				    repProject.getPackages().add(repPackage);

				    oldRepPackage = getPackageFromOldReport(
					    oldReport, pf.getElementName());
				}

				// get/create class
				if (repClass == null) {
				    repClass = of.createRepClass();
				    repClass.setId(baseCu.getElementName());
				    repPackage.getClasses().add(repClass);

				    oldRepClass = getClassFromOldReport(
					    oldRepPackage,
					    baseCu.getElementName());
				}

				// create method
				repMethod = of.createRepMethod();
				repMethod.setId(method.getElementName());
				repMethod.setSignature(method.getSignature());
				repClass.getMethods().add(repMethod);

				// get method from old report
				RepMethod oldRepMethod = getMethodFromOldReport(
					oldRepClass, method);

				Report report;

				// report for method already available
				if (oldRepMethod != null) {
				    // update report
				    report = oldRepMethod.getReport();
				    repMethod.setReport(report);

				    report.setIsNew(false);

				    if (report.isIsTestAvailable()) {
					testClassNecessary = true;
					availableTestMethodsCounter++;
				    } else if (!report.isIsTestNecessary()
					    || (report.getCoveredWith() != null && !""
						    .equals(report
							    .getCoveredWith()
							    .trim()))) {
					necessaryTestMethodsCounter--;

					// if if-statements increased check
					// again
					if (report.getNumberOfIfStatements() > 0
						&& mar.getNumberOfIfStatements() > report
							.getNumberOfIfStatements()) {
					    report.setCheckAgain(true);
					}
				    } else {
					testClassNecessary = true;
				    }
				} else {
				    // create new report
				    report = createReportEntry(elements,
					    analyzedMethodEntry);
				    repMethod.setReport(report);

				    IJavaElement type = analyzedMethodEntry
					    .getKey().getParent();
				    if (!type.getElementName().equals(
					    baseCu.getPrimaryElement()
						    .getElementName()
						    .replace(".java", ""))) {

					subtype = type.getElementName();
					repMethod.setSubtype(subtype);
				    }

				    // add new method entry
				    addNewMethodEntry(repProject, repPackage,
					    repClass, repMethod,
					    analyzedMethodEntry, subtype);

				    testClassNecessary = true;
				    newMethodsCounter++;
				}

				// general report data
				report.setNumberOfIfStatements(mar
					.getNumberOfIfStatements());
				report.setOnlyNullchecks(mar.isOnlyNullChecks());

			    }

			}
		    }

		    if (testClassNecessary) {
			necessaryTestClassesCounter++;

			if (testClassAvailable) {
			    availableTestClassesCounter++;
			}
		    }
		}

	    }

	    // set statistics
	    addStatisticsEntry(repProject, availableTestClassesCounter,
		    availableTestMethodsCounter, necessaryTestClassesCounter,
		    necessaryTestMethodsCounter, newMethodsCounter);

	    // write report
	    try {
		writeReport(testProject, repProject);
	    } catch (Exception e) {
		new JUTException(e);
	    }
	}

    }

    /**
     * @param repProject
     * @param newMethodsCounter
     * @param necessaryTestMethodsCounter
     * @param necessaryTestClassesCounter
     * @param availableTestMethodsCounter
     * @param availableTestClassesCounter
     * 
     */
    private void addStatisticsEntry(RepProject repProject,
	    int availableTestClassesCounter, int availableTestMethodsCounter,
	    int necessaryTestClassesCounter, int necessaryTestMethodsCounter,
	    int newMethodsCounter) {
	Statistics statistics = of.createStatistics();
	statistics.setAvailableTestClassesCounter(availableTestClassesCounter);
	statistics.setAvailableTestMethodsCounter(availableTestMethodsCounter);
	statistics.setNecessaryTestClassesCounter(necessaryTestClassesCounter);
	statistics.setNecessaryTestMethodsCounter(necessaryTestMethodsCounter);
	statistics.setNewMethodsCounter(newMethodsCounter);

	double classCoverage = 0;
	double methodCoverage = 0;

	if (necessaryTestClassesCounter == 0) {
	    classCoverage = 100;
	} else if (availableTestClassesCounter == 0) {
	    classCoverage = 0;
	} else {
	    classCoverage = availableTestClassesCounter * 100
		    / necessaryTestClassesCounter;
	}

	if (necessaryTestMethodsCounter == 0) {
	    methodCoverage = 100;
	} else if (availableTestMethodsCounter == 0) {
	    methodCoverage = 0;
	} else {
	    methodCoverage = availableTestMethodsCounter * 100
		    / necessaryTestMethodsCounter;
	}
	statistics.setClassCoverage(classCoverage);
	statistics.setMethodCoverage(methodCoverage);

	repProject.setStatistics(statistics);
    }

    /**
     * @param repProject
     * @param repPackage
     * @param repClass
     * @param repMethod
     * @param methodEntry
     * @param subtype
     * @throws JavaModelException
     */
    private void addNewMethodEntry(RepProject repProject,
	    RepPackage repPackage, RepClass repClass, RepMethod repMethod,
	    Entry<IMethod, MethodAnalyzeResult> methodEntry, String subtype)
	    throws JavaModelException {
	// get new methods list
	NewMethods nm = getNewMethodsList(repProject, methodEntry);

	if (subtype == null) {
	    subtype = "";
	}

	if (!"".equals(subtype)) {
	    subtype += ".";
	}

	// add entry
	nm.getFullQualifiedName().add(
		repPackage.getId() + "."
			+ repClass.getId().replace(".java", "") + "." + subtype
			+ repMethod.getId() + "("
			+ JDTUtils.createParamList(methodEntry.getKey()) + ")");
    }

    /**
     * @param methodEntry
     * @param repProject
     * @return newMethodsList
     */
    private NewMethods getNewMethodsList(RepProject repProject,
	    Entry<IMethod, MethodAnalyzeResult> methodEntry) {
	List<NewMethods> newMethods = repProject.getNewMethods();
	for (NewMethods nm : newMethods) {
	    if (nm.getTestprio().equals(
		    methodEntry.getValue().getTestPrio().value())) {
		return nm;
	    }
	}

	NewMethods nm = of.createNewMethods();
	nm.setTestprio(methodEntry.getValue().getTestPrio().value());
	repProject.getNewMethods().add(nm);

	return nm;
    }

    private RepClass getClassFromOldReport(RepPackage repPackage,
	    String className) {
	if (repPackage == null) {
	    return null;
	}

	for (RepClass repClass : repPackage.getClasses()) {
	    if (repClass.getId().equals(className)) {
		return repClass;
	    }
	}

	return null;
    }

    private RepPackage getPackageFromOldReport(RepProject oldReport,
	    String packageName) {
	if (oldReport == null) {
	    return null;
	}

	for (RepPackage repPackage : oldReport.getPackages()) {
	    if (repPackage.getId().equals(packageName)) {
		return repPackage;
	    }
	}

	return null;
    }

    private RepMethod getMethodFromOldReport(RepClass oldRepClass,
	    IMethod method) throws JavaModelException {
	if (oldRepClass == null) {
	    return null;
	}

	for (RepMethod repMethod : oldRepClass.getMethods()) {
	    if (repMethod.getId().equals(method.getElementName())
		    && repMethod.getSignature().equals(method.getSignature())) {
		return repMethod;
	    }
	}

	return null;
    }

    private RepProject readReport(IJavaProject testProject)
	    throws CoreException {
	IFolder reportContainer = getReportContainer(testProject);
	IFile oldReport = null;
	String fileName;
	String lastDate = "", actualDate;

	for (IResource resource : reportContainer.members()) {
	    if (resource instanceof IFile) {
		fileName = resource.getName();

		if (fileName.startsWith("necessaryTestsReport_")) {
		    int lastIndexOf = fileName.lastIndexOf("_");
		    actualDate = fileName.substring(lastIndexOf + 1);
		    actualDate = actualDate.replace(".xml", "");

		    if (!actualDate.equals(getTodayDate())
			    && actualDate.compareTo(lastDate) >= 1) {
			lastDate = actualDate;
			oldReport = (IFile) resource;
		    }
		}
	    }
	}

	if (oldReport == null || !oldReport.exists()) {
	    return null;
	}

	RepProject repProject = null;

	try {
	    oldReport.refreshLocal(0, null);

	    JAXBContext context = JAXBContext.newInstance(RepProject.class);
	    Unmarshaller unmarshaller = context.createUnmarshaller();
	    repProject = (RepProject) unmarshaller.unmarshal(oldReport
		    .getContents());

	} catch (Exception e) {
	    throw new RuntimeException(e);
	}

	return repProject;
    }

    private boolean incrementTask(IProgressMonitor monitor, int i) {
	if (monitor.isCanceled()) {
	    return true;
	}
	monitor.worked(i);
	return false;
    }

    private void writeReport(IJavaProject project, RepProject repProject)
	    throws JUTWarning, CoreException, JAXBException, IOException {

	// get output folder and file
	IFolder reportsContainer = getReportContainer(project);

	IFile reportFile = reportsContainer.getFile("necessaryTestsReport_"
		+ getTodayDate() + ".xml");
	if (reportFile == null) {
	    throw new JUTWarning("The report file could not be created!");
	}

	// write report to file
	if (!reportFile.exists()) {
	    byte[] bytes = "".getBytes();
	    InputStream source = new ByteArrayInputStream(bytes);
	    reportFile.create(source, IResource.NONE, null);
	    source.close();
	}

	JAXBContext context = JAXBContext.newInstance(RepProject.class);
	Marshaller marshaller = context.createMarshaller();
	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	FileWriter fileWriter = new FileWriter(reportFile.getRawLocation()
		.makeAbsolute().toFile());
	marshaller.marshal(repProject, fileWriter);
	fileWriter.flush();
	fileWriter.close();

	reportFile.refreshLocal(0, null);
    }

    private IFolder getReportContainer(IJavaProject project) {
	IFolder reportContainer = JDTUtils.createFolder(project, "reports"); // TODO
									     // preferences

	if (!reportContainer.exists()) {
	    throw new RuntimeException("Report-container could not be created"); // TODO
										 // messages
	}

	return reportContainer;
    }

    private Report createReportEntry(JUTElements elements,
	    Entry<IMethod, MethodAnalyzeResult> methodEntry) {
	Report report = of.createReport();

	MethodAnalyzeResult mar = methodEntry.getValue();

	report.setTestPrio(mar.getTestPrio().value());
	report.setIsTestAvailable(false);
	report.setIsTestNecessary(true);
	report.setCoveredWith("");
	report.setIsNew(true);

	// set timestamp
	GregorianCalendar gc = getTimestamp();
	XMLGregorianCalendar date = null;

	try {
	    date = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
	} catch (DatatypeConfigurationException e) {
	    // nothing
	}

	report.setDate(date);

	return report;
    }

    private GregorianCalendar getTimestamp() {
	GregorianCalendar gc = (GregorianCalendar) GregorianCalendar
		.getInstance();
	gc.setTimeInMillis(System.currentTimeMillis());
	return gc;
    }

    private String getTodayDate() {
	if ("".equals(todayDate)) {
	    Date time = new Date(System.currentTimeMillis());
	    todayDate = time.toString();
	}

	return todayDate;
    }

    public int countAllTestMethods(JUTElements utmElements,
	    IProgressMonitor monitor) throws JUTException {
	IJavaProject testProject = utmElements.getProjects().getTestProject();
	if (testProject != null && testProject.exists()) {
	    try {
		Vector<IJavaElement> allCompilationUnits = JDTUtils
			.collectCompilationUnits(testProject);
		return countAllTestMethods(allCompilationUnits);

	    } catch (Exception e) {
		throw new JUTException(e);
	    }
	}

	return 0;
    }

    private int countAllTestMethods(Vector<IJavaElement> allCompilationUnits)
	    throws JavaModelException {
	int counter = 0;

	for (IJavaElement element : allCompilationUnits) {
	    counter += JDTUtils.getMethods(element, true, "test", true,
		    GeneratorUtils.MOD_PUBLIC).size();
	}

	return counter;
    }
}
