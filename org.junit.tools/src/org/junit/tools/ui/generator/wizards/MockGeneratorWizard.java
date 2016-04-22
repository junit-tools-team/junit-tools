package org.junit.tools.ui.generator.wizards;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.ui.preferences.ProjectSelectionDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.junit.tools.base.JUTWarning;
import org.junit.tools.base.MethodRef;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;
import org.junit.tools.preferences.JUTPreferences;
import org.junit.tools.ui.generator.swt.control.GroupMethodSelectionCtrl;
import org.junit.tools.ui.generator.wizards.pages.MockGeneratorWizardPage;

@SuppressWarnings("restriction")
public class MockGeneratorWizard extends Wizard implements INewWizard,
		IMethodeSelectionChangedListener {

	private boolean finished = false;

	private MockGeneratorWizardPage mainPage;

	private final IJavaElement classToMock;

	private GroupMethodSelectionCtrl methodSelection;

	private Vector<IMethod> checkedMethods;

	private IJavaProject selectedProject = null;

	private IPackageFragment targetPackage;

	private boolean isMockNew = true;

	private ICompilationUnit mockClass;

	public MockGeneratorWizard(IJavaElement javaElement) {
		this.classToMock = javaElement;
	}

	@Override
	public void addPages() {
		String title = "Create mock-class";
		mainPage = new MockGeneratorWizardPage(title);
		mainPage.setTitle(title);

		addPage(mainPage);
	}

	public void init() throws CoreException, JUTWarning {

		// get mock project
		IJavaProject project = JDTUtils.getProject(JUTPreferences
				.getMockProject());

		if (project == null) {
			project = JDTUtils.createProject(JUTPreferences.getMockProject(),
					classToMock.getJavaProject());
		}

		methodSelection = new GroupMethodSelectionCtrl();
		methodSelection.init(mainPage.getView().getMethodSelectionGroup(),
				classToMock);
		methodSelection.addListener(this);
		methodSelection.deactivateFilters();

		isMockNew = true;

		if (project != null) {
			Text txtProject = mainPage.getView().getTxtProject();
			txtProject.setText(project.getElementName());
			txtProject.setData(project);

			this.selectedProject = project;

			// get target package
			IPackageFragment pckg = JDTUtils.getPackage(classToMock);
			this.targetPackage = JDTUtils.getPackage(project,
					pckg.getElementName() + ".mock", false);

			// get mock class
			if (targetPackage != null && targetPackage.exists()) {
				String mockClassName = classToMock.getElementName().replace(
						".java", "Mock.java");
				mockClassName = mockClassName.replace(".class", "Mock.java");
				if (!mockClassName.endsWith("Mock.java")) {
					mockClassName += "Mock.java";
				}

				mockClass = targetPackage.getCompilationUnit(mockClassName);

				// get existing methods
				if (mockClass != null && mockClass.exists()) {
					isMockNew = false;

					HashMap<MethodRef, IMethod> existingMethods = GeneratorUtils
							.getExistingTestMethods(mockClass);
					methodSelection.setExistingMethods(existingMethods);
				}
			}
		}

		mainPage.getView().getBtnSelectProject()
				.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						super.widgetSelected(e);
						handleContainer();
					}
				});
	}

	@Override
	public boolean performFinish() {
		finished = true;
		return true;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		// nothing
	}

	/**
	 * Returns if the processing is finished.
	 * 
	 * @return true if the processing is finished.
	 */
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void methodSelectionChanged(Vector<IMethod> chckdMethods) {
		this.checkedMethods = chckdMethods;
	}

	public Vector<IMethod> getCheckedMethods() {
		return checkedMethods;
	}

	private void handleContainer() {
		List<IJavaProject> javaProjects = JDTUtils.getJavaProjects();
		Set<IJavaProject> setProjects = new HashSet<IJavaProject>(javaProjects);

		@SuppressWarnings("restriction")
		ProjectSelectionDialog dialog = new ProjectSelectionDialog(getShell(),
				setProjects);

		if (dialog.open() == Window.OK) {
			Object[] results = dialog.getResult();
			if (results.length > 0) {
				for (Object result : results) {
					if (result instanceof IJavaProject) {
						IJavaProject project = ((IJavaProject) result);
						mainPage.getView().getTxtProject()
								.setText(project.getElementName());
						mainPage.getView().getTxtProject().setData(result);
						selectedProject = project;
						return;
					}
				}
			}
		}
	}

	public IJavaProject getProject() {
		return selectedProject;
	}

	public IPackageFragment getTargetPackage() {
		return targetPackage;
	}

	public boolean isMockNew() {
		return isMockNew;
	}

	public ICompilationUnit getMockClass() {
		return mockClass;
	}

	public HashMap<MethodRef, IMethod> getExistingMethods() {
		return methodSelection.getExistingMethods();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.junit.tools.ui.generator.wizards.IMethodeSelectionChangedListener#selectedMethodChecked(org.eclipse.jdt.core.IMethod)
	 */
	@Override
	public void selectedMethodChecked(IMethod selectedMethod) {
		// nothing
	}
}
