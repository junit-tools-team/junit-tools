package org.junit.tools.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.junit.tools.Activator;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.messages.Messages;
import org.junit.tools.preferences.IJUTPreferenceConstants;

/**
 * The page for the main preferences.
 * 
 * @author JUnit-Tools-Team
 * 
 */
public class JUTPreferenceMainPage extends FieldEditorPreferencePage implements
	IWorkbenchPreferencePage, IJUTPreferenceConstants {

    private Label projectExample;
    private Label folderExample;
    private Label packageExample;
    private Label classExample;
    private Label methodExample;
    private Label superTypeExample;

    private boolean projectIsEqual = false;
    private boolean srcFolderIsEqual = false;
    private boolean packageIsEqual = false;
    private boolean classIsEqual = false;
    private boolean methodIsEqual = false;

    private StringFieldEditor fieldProject;
    private StringFieldEditor fieldSrcFolder;
    private StringFieldEditor fieldPackage;
    private StringFieldEditor fieldClassPre;
    private StringFieldEditor fieldClassPost;
    private StringFieldEditor fieldMethodPre;
    private StringFieldEditor fieldMethodPost;
    private StringFieldEditor fieldSuperType;

    public JUTPreferenceMainPage() {
	super(FieldEditorPreferencePage.GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
	setPreferenceStore(Activator.getDefault().getPreferenceStore());
	setDescription(Messages.JUTPreferenceMainPage_description_Main_settings);
	setValid(true);
    }

    @Override
    protected void createFieldEditors() {

	// project
	fieldProject = new StringFieldEditor(TEST_PROJECT_POSTFIX,
		Messages.JUTPreferenceMainPage_Test_project_postfix,
		getFieldEditorParent()) {

	    @Override
	    protected void valueChanged() {
		super.valueChanged();
		setExampleValue();
	    }

	    @Override
	    protected void doLoad() {
		super.doLoad();
		setExampleValue();
	    }

	    private void setExampleValue() {

		if ("".equals(getStringValue())) {
		    projectExample
			    .setText("Project under test and test project is equal");
		    projectIsEqual = true;
		} else {
		    projectExample
			    .setText("If the project under test is \"org.example\" the test project is \"org.example"
				    + getStringValue() + "\"");
		    projectIsEqual = false;
		}

		projectExample.getParent().layout();

		setExampleValueFolder();
	    }
	};
	addField(fieldProject);

	projectExample = new Label(getFieldEditorParent(), SWT.NONE);
	projectExample.setLayoutData(createGridDataWithIndent());

	// source folder
	fieldSrcFolder = new StringFieldEditor(TEST_SOURCE_FOLDER_NAME,
		Messages.JUTPreferenceMainPage_Test_source_folder_name,
		getFieldEditorParent()) {

	    @Override
	    protected void valueChanged() {
		super.valueChanged();
		setExampleValueFolder();
	    }

	    @Override
	    protected void doLoad() {
		super.doLoad();
		setExampleValueFolder();
	    }
	};

	addField(fieldSrcFolder);

	folderExample = new Label(getFieldEditorParent(), SWT.NONE);
	folderExample.setLayoutData(createGridDataWithIndent());

	// package
	fieldPackage = new StringFieldEditor(TEST_PACKAGE_POSTFIX,
		Messages.JUTPreferenceMainPage_Test_package_postfix,
		getFieldEditorParent()) {
	    @Override
	    protected void valueChanged() {
		super.valueChanged();
		setExampleValuePackage();
	    }

	    @Override
	    protected void doLoad() {
		super.doLoad();
		setExampleValuePackage();
	    }
	};
	addField(fieldPackage);

	packageExample = new Label(getFieldEditorParent(), SWT.NONE);
	packageExample.setLayoutData(createGridDataWithIndent());

	// class prefix
	fieldClassPre = new StringFieldEditor(TEST_CLASS_PREFIX,
		Messages.JUTPreferenceMainPage_Test_class_prefix,
		getFieldEditorParent()) {
	    @Override
	    protected void valueChanged() {
		super.valueChanged();
		setExampleValueClass();
	    }

	    @Override
	    protected void doLoad() {
		super.doLoad();
		setExampleValueClass();
	    }
	};
	addField(fieldClassPre);

	// class postfix
	fieldClassPost = new StringFieldEditor(TEST_CLASS_POSTFIX,
		Messages.JUTPreferenceMainPage_Test_class_postfix,
		getFieldEditorParent()) {
	    @Override
	    protected void valueChanged() {
		super.valueChanged();
		setExampleValueClass();
	    }

	    @Override
	    protected void doLoad() {
		super.doLoad();
		setExampleValueClass();
	    }
	};
	addField(fieldClassPost);

	classExample = new Label(getFieldEditorParent(), SWT.NONE);
	classExample.setLayoutData(createGridDataWithIndent());

	// method prefix
	fieldMethodPre = new StringFieldEditor(TEST_METHOD_PREFIX,
		Messages.JUTPreferenceMainPage_Test_method_prefix,
		getFieldEditorParent()) {
	    @Override
	    protected void valueChanged() {
		super.valueChanged();
		setExampleValueMethod();
	    }

	    @Override
	    protected void doLoad() {
		super.doLoad();
		setExampleValueMethod();
	    }
	};
	addField(fieldMethodPre);

	// method postfix
	fieldMethodPost = new StringFieldEditor(TEST_METHOD_POSTFIX,
		Messages.JUTPreferenceMainPage_Test_Method_postfix,
		getFieldEditorParent()) {
	    @Override
	    protected void valueChanged() {
		super.valueChanged();
		setExampleValueMethod();
	    }

	    @Override
	    protected void doLoad() {
		super.doLoad();
		setExampleValueMethod();
	    }

	};
	addField(fieldMethodPost);

	methodExample = new Label(getFieldEditorParent(), SWT.NONE);
	methodExample.setLayoutData(createGridDataWithIndent());

	// super type
	fieldSuperType = new StringFieldEditor(TEST_CLASS_SUPER_TYPE,
		Messages.JUTPreferenceMainPage_Testclass_supertype,
		getFieldEditorParent()) {
	    @Override
	    protected void valueChanged() {
		super.valueChanged();
		setExampleValueSuperType();
	    }

	    @Override
	    protected void doLoad() {
		super.doLoad();
		setExampleValueSuperType();
	    }

	};
	addField(fieldSuperType);

	superTypeExample = new Label(getFieldEditorParent(), SWT.NONE);
	superTypeExample.setLayoutData(createGridDataWithIndent());

	addBlankLine();

	// mock project
	Label label = new Label(getFieldEditorParent(), SWT.WRAP);
	label.setLayoutData(createGridData());
	label.setText("Here you can set the target project for the generated mock objects:");

	addField(new StringFieldEditor(MOCK_PROJECT,
		Messages.JUTPreferenceMainPage_Mock_Project,
		getFieldEditorParent()));

	addBlankLine();

	adjustGridLayout();
    }

    protected void setExampleValueFolder() {

	if ("".equals(fieldSrcFolder.getStringValue())) {
	    srcFolderIsEqual = true;

	    if (projectIsEqual) {
		folderExample
			.setText("Source folder and test source folder is equal (\"src\")");
	    } else {
		folderExample
			.setText("The default for the test source folder is \"src\"");
	    }
	} else {
	    srcFolderIsEqual = false;

	    folderExample
		    .setText("The source folder target for the test elements is \""
			    + fieldSrcFolder.getStringValue() + "\"");
	}

	folderExample.getParent().layout();

	setExampleValuePackage();
    }

    protected void setExampleValuePackage() {

	if ("".equals(fieldPackage.getStringValue())) {
	    packageIsEqual = true;

	    if (projectIsEqual && srcFolderIsEqual) {
		packageExample
			.setText("Source package and test package is equal");
	    } else {
		packageExample
			.setText("Package names are equal for base and test");
	    }
	} else {
	    packageIsEqual = false;
	    packageExample
		    .setText("The package name for the package \"base.logic\"is \"base.logic"
			    + fieldPackage.getStringValue() + "\"");
	}

	packageExample.getParent().layout();

	setExampleValueClass();
    }

    protected void setExampleValueClass() {

	if ("".equals(fieldClassPre.getStringValue())
		&& "".equals(fieldClassPost.getStringValue())) {
	    classIsEqual = true;

	    if (projectIsEqual && srcFolderIsEqual && packageIsEqual) {
		classExample.setText("Class for base and test is equal");
	    } else {
		classExample
			.setText("The base and test name for the class is equal");
	    }
	} else {
	    classIsEqual = false;

	    classExample
		    .setText("If the class under test is \"Calculator\" the test class is \""
			    + fieldClassPre.getStringValue()
			    + "Calculator"
			    + fieldClassPost.getStringValue() + "\"");
	}

	classExample.getParent().layout();

	setExampleValueMethod();
    }

    protected void setExampleValueMethod() {
	checkValid();

	if ("".equals(fieldMethodPre.getStringValue())
		&& "".equals(fieldMethodPost.getStringValue())) {
	    methodIsEqual = true;

	    if (projectIsEqual && srcFolderIsEqual && packageIsEqual
		    && classIsEqual) {
		methodExample.setText("Base and test method is equal");
	    } else {
		methodExample
			.setText("Base method name and test method name are equal");
	    }
	} else {
	    methodIsEqual = false;

	    String methodName = "";
	    if (fieldMethodPre.getStringValue().equals("")) {
		methodName = "calculate";
	    } else {
		methodName = "Calculate";
	    }

	    methodExample
		    .setText("If the method under test is \"calculate\" the test method is \""
			    + fieldMethodPre.getStringValue()
			    + methodName
			    + GeneratorUtils.firstCharToUpper(fieldMethodPost
				    .getStringValue()) + "\"");
	}

	methodExample.getParent().layout();
	getFieldEditorParent().layout();
    }

    private void checkValid() {
	if (projectIsEqual && srcFolderIsEqual && packageIsEqual
		&& classIsEqual && methodIsEqual) {
	    setErrorMessage("There must be a difference between the elements under test and the corresponding test elements!");
	    setValid(false);
	} else {
	    // reset error message
	    setErrorMessage(null);
	    setValid(true);
	}
    }

    protected void setExampleValueSuperType() {
	checkValid();

	if ("".equals(fieldSuperType.getStringValue())) {
	    superTypeExample
		    .setText("There is no super type defined for the test class");
	} else {
	    superTypeExample
		    .setText("The default super class for the test class is \""
			    + fieldSuperType.getStringValue() + "\"");
	}

	superTypeExample.getParent().layout();
	getFieldEditorParent().layout();
    }

    private void addBlankLine() {
	Label label = new Label(getFieldEditorParent(), SWT.NONE);
	label.setLayoutData(createGridData());
    }

    @Override
    public Point computeSize() {
	Composite fieldEditorParent2 = getFieldEditorParent();
	GridData gd = (GridData) fieldEditorParent2.getLayoutData();

	if (gd != null) {
	    gd.widthHint = 500;
	}
	return super.computeSize();
    }

    private GridData createGridData() {
	GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
	gridData.horizontalSpan = 2;

	return gridData;
    }

    private GridData createGridDataWithIndent() {
	GridData gridData = createGridData();
	gridData.horizontalIndent = 15;
	return gridData;
    }

}
