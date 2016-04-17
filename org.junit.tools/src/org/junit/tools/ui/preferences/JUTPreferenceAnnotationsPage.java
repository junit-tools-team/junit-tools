package org.junit.tools.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.junit.tools.Activator;
import org.junit.tools.messages.Messages;
import org.junit.tools.preferences.IJUTPreferenceConstants;
import org.junit.tools.preferences.JUTPreferences;

/**
 * The page for the annotations preferences.
 * 
 * @author JUnit-Tools-Team
 * 
 */
public class JUTPreferenceAnnotationsPage extends PreferencePage implements
	IWorkbenchPreferencePage, IJUTPreferenceConstants {
    public JUTPreferenceAnnotationsPage() {
    }

    private List listAnnotationsTestClass;

    private Text newAnnotationTestClass;

    private List listAnnotationsMockClass;

    private Text newAnnotationMockClass;
    private GridData gd_cmpAnnotationsMockClass;
    private GridData data_2;
    private GridData data_3;
    private GridData gd_newAnnotationTestClass;
    private GridData gd_newAnnotationMockClass;
    private GridData gd_cmpAnnotationsTestClass;

    /**
     * @see PreferencePage#createContents(Composite)
     * @param parent
     */
    protected Control createContents(Composite parent) {
	Composite cmpMain = new Composite(parent, SWT.NONE);

	GridData cmpMainLayoutData = new GridData(GridData.FILL_HORIZONTAL);
	cmpMainLayoutData.grabExcessHorizontalSpace = true;
	cmpMain.setLayoutData(cmpMainLayoutData);
	cmpMain.setLayout(new GridLayout());

	// test-method-filter name
	Group cmpAnnotationsTestClass = new Group(cmpMain, SWT.NONE);
	cmpAnnotationsTestClass
		.setText("Additional annotations for the test-class");
	GridData data;
	gd_cmpAnnotationsTestClass = new GridData(GridData.FILL_HORIZONTAL);
	gd_cmpAnnotationsTestClass.verticalAlignment = SWT.FILL;
	gd_cmpAnnotationsTestClass.grabExcessVerticalSpace = true;
	gd_cmpAnnotationsTestClass.grabExcessHorizontalSpace = true;
	cmpAnnotationsTestClass.setLayoutData(gd_cmpAnnotationsTestClass);
	cmpAnnotationsTestClass.setLayout(new GridLayout());

	listAnnotationsTestClass = new List(cmpAnnotationsTestClass, SWT.BORDER);
	listAnnotationsTestClass.setItems(getAnnotationsTestClass());

	// Create a data that takes up the extra space in the dialog and spans
	// both columns.
	data = new GridData(GridData.FILL_BOTH);
	listAnnotationsTestClass.setLayoutData(data);

	Composite buttonCompositeFilterName = new Composite(
		cmpAnnotationsTestClass, SWT.NULL);

	GridLayout buttonLayout = new GridLayout();
	buttonLayout.numColumns = 2;
	buttonCompositeFilterName.setLayout(buttonLayout);

	// Create a data that takes up the extra space in the dialog and spans
	// both columns.
	data_2 = new GridData(GridData.FILL_BOTH
		| GridData.VERTICAL_ALIGN_BEGINNING);
	data_2.verticalAlignment = SWT.CENTER;
	data_2.grabExcessVerticalSpace = false;
	buttonCompositeFilterName.setLayoutData(data_2);

	Button addButton = new Button(buttonCompositeFilterName, SWT.PUSH
		| SWT.CENTER);

	addButton.setText("Add to List"); //$NON-NLS-1$
	addButton.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		String newEntry = newAnnotationTestClass.getText();
		for (String item : listAnnotationsTestClass.getItems()) {
		    if (newEntry.equals(item)) {
			return;
		    }
		}
		
		listAnnotationsTestClass.add(newEntry,
			listAnnotationsTestClass.getItemCount());
	    }
	});

	newAnnotationTestClass = new Text(buttonCompositeFilterName, SWT.BORDER);
	// Create a data that takes up the extra space in the dialog .
	gd_newAnnotationTestClass = new GridData(GridData.FILL_HORIZONTAL);
	gd_newAnnotationTestClass.grabExcessHorizontalSpace = true;
	newAnnotationTestClass.setLayoutData(gd_newAnnotationTestClass);

	Button removeButton = new Button(buttonCompositeFilterName, SWT.PUSH
		| SWT.CENTER);

	removeButton.setText("Remove Selection"); //$NON-NLS-1$
	removeButton.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		listAnnotationsTestClass.remove(listAnnotationsTestClass
			.getSelectionIndex());
	    }
	});

	data = new GridData();
	data.horizontalSpan = 2;
	removeButton.setLayoutData(data);

	// test-method-filter modifier
	Group cmpAnnotationsMockClass = new Group(cmpMain, SWT.NONE);
	cmpAnnotationsMockClass
		.setText("Additional annotations for the mock-class");

	// Create a data that takes up the extra space in the dialog .
	gd_cmpAnnotationsMockClass = new GridData(GridData.FILL_HORIZONTAL);
	gd_cmpAnnotationsMockClass.verticalAlignment = SWT.FILL;
	gd_cmpAnnotationsMockClass.grabExcessVerticalSpace = true;
	gd_cmpAnnotationsMockClass.grabExcessHorizontalSpace = true;
	cmpAnnotationsMockClass.setLayoutData(gd_cmpAnnotationsMockClass);

	GridLayout gl_cmpAnnotationsMockClass = new GridLayout();
	cmpAnnotationsMockClass.setLayout(gl_cmpAnnotationsMockClass);

	listAnnotationsMockClass = new List(cmpAnnotationsMockClass,
		SWT.BORDER);
	listAnnotationsMockClass.setItems(getAnnotationsMockClass());

	// Create a data that takes up the extra space in the dialog and spans
	// both columns.
	data = new GridData(GridData.FILL_BOTH);
	listAnnotationsMockClass.setLayoutData(data);

	Composite buttonComposite = new Composite(cmpAnnotationsMockClass,
		SWT.NULL);

	buttonLayout = new GridLayout();
	buttonLayout.numColumns = 2;
	buttonComposite.setLayout(buttonLayout);

	// Create a data that takes up the extra space in the dialog and spans
	// both columns.
	data_3 = new GridData(GridData.FILL_BOTH
		| GridData.VERTICAL_ALIGN_BEGINNING);
	data_3.verticalAlignment = SWT.CENTER;
	data_3.grabExcessVerticalSpace = false;
	buttonComposite.setLayoutData(data_3);

	addButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);

	addButton.setText("Add to List"); //$NON-NLS-1$
	addButton.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		String newEntry = newAnnotationMockClass.getText();
		for (String item : listAnnotationsMockClass.getItems()) {
		    if (newEntry.equals(item)) {
			return;
		    }
		}
		
		listAnnotationsMockClass.add(newEntry,
			listAnnotationsMockClass.getItemCount());
	    }
	});

	newAnnotationMockClass = new Text(buttonComposite, SWT.BORDER);
	// Create a data that takes up the extra space in the dialog .
	gd_newAnnotationMockClass = new GridData(GridData.FILL_HORIZONTAL);
	gd_newAnnotationMockClass.grabExcessHorizontalSpace = true;
	newAnnotationMockClass.setLayoutData(gd_newAnnotationMockClass);

	removeButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);

	removeButton.setText("Remove Selection"); //$NON-NLS-1$
	removeButton.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		listAnnotationsMockClass.remove(listAnnotationsMockClass
			.getSelectionIndex());
	    }
	});

	data = new GridData();
	data.horizontalSpan = 2;
	removeButton.setLayoutData(data);

	return cmpMain;
    }

    /**
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(IWorkbench workbench) {
	// Initialize the preference store we wish to use
	setPreferenceStore(Activator.getDefault().getPreferenceStore());
	setDescription(Messages.JUTPreferenceAnnotationsPage_description);
    }

    protected void performDefaults() {
	listAnnotationsTestClass.setItems(getDefaultAnnotationsTestClass());
	listAnnotationsMockClass.setItems(getDefaultAnnotationsMockClass());
    }

    public boolean performOk() {
	setAnnotationsTestClass(listAnnotationsTestClass.getItems());
	setAnnotationsMockClass(listAnnotationsMockClass.getItems());
	return super.performOk();
    }

    public void setAnnotationsTestClass(String[] values) {
	getPreferenceStore().setValue(TEST_CLASS_ANNOTATIONS,
		JUTPreferences.convert(values));
    }

    public String[] getDefaultAnnotationsTestClass() {
	return JUTPreferences.convert(getPreferenceStore().getDefaultString(
		TEST_CLASS_ANNOTATIONS));
    }

 
    public String[] getAnnotationsTestClass() {
	return JUTPreferences.convert(getPreferenceStore().getString(
		TEST_CLASS_ANNOTATIONS));
    }


    public void setAnnotationsMockClass(String[] values) {
	getPreferenceStore().setValue(MOCK_CLASS_ANNOTATIONS,
		JUTPreferences.convert(values));
    }

    public String[] getDefaultAnnotationsMockClass() {
	return JUTPreferences.convert(getPreferenceStore().getDefaultString(
		MOCK_CLASS_ANNOTATIONS));
    }

    public String[] getAnnotationsMockClass() {
	return JUTPreferences.convert(getPreferenceStore().getString(
		MOCK_CLASS_ANNOTATIONS));
    }
}