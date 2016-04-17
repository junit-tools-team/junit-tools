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
import org.eclipse.swt.widgets.Label;

/**
 * The page for the static bindings preferences.
 * 
 * @author JUnit-Tools-Team
 * 
 */
public class JUTPreferenceStaticBindingsPage extends PreferencePage implements
	IWorkbenchPreferencePage, IJUTPreferenceConstants {
    public JUTPreferenceStaticBindingsPage() {
    }

    private List listStaticBindings;

    private Text baseProject;
    private GridData gd_cmpExceptions;
    private GridData data_2;
    private GridData gd_baseProject;
    private GridData gd_cmpStaticBindings;
    private Text testProject;

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
	Group cmpStaticBindings = new Group(cmpMain, SWT.NONE);
	cmpStaticBindings
		.setText("Static bindings");
	GridData data;
	gd_cmpStaticBindings = new GridData(GridData.FILL_HORIZONTAL);
	gd_cmpStaticBindings.verticalAlignment = SWT.FILL;
	gd_cmpStaticBindings.grabExcessVerticalSpace = true;
	gd_cmpStaticBindings.grabExcessHorizontalSpace = true;
	cmpStaticBindings.setLayoutData(gd_cmpStaticBindings);
	cmpStaticBindings.setLayout(new GridLayout());

	listStaticBindings = new List(cmpStaticBindings, SWT.BORDER);
	listStaticBindings.setItems(getStaticBindings());

	// Create a data that takes up the extra space in the dialog and spans
	// both columns.
	data = new GridData(GridData.FILL_BOTH);
	listStaticBindings.setLayoutData(data);

	Composite buttonCompositeFilterName = new Composite(
		cmpStaticBindings, SWT.NULL);

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
	
	Label lblProjectUnderTest = new Label(buttonCompositeFilterName, SWT.NONE);
	lblProjectUnderTest.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblProjectUnderTest.setText("Project under test");
	
		baseProject = new Text(buttonCompositeFilterName, SWT.BORDER);
		// Create a data that takes up the extra space in the dialog .
		gd_baseProject = new GridData(GridData.FILL_HORIZONTAL);
		gd_baseProject.grabExcessHorizontalSpace = true;
		baseProject.setLayoutData(gd_baseProject);
	
	Label lblTestproject = new Label(buttonCompositeFilterName, SWT.NONE);
	lblTestproject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblTestproject.setText("Testproject");
	
	testProject = new Text(buttonCompositeFilterName, SWT.BORDER);
	testProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(buttonCompositeFilterName, SWT.NONE);
	
		Button addButton = new Button(buttonCompositeFilterName, SWT.PUSH
			| SWT.CENTER);
		addButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
			addButton.setText("Add to List"); //$NON-NLS-1$
			addButton.addSelectionListener(new SelectionAdapter() {
			    public void widgetSelected(SelectionEvent event) {
				String newEntry = baseProject.getText() + LIST_ENTRY_SEPERATOR + testProject.getText();
				for (String item : listStaticBindings.getItems()) {
				    if (newEntry.equals(item)) {
					return;
				    }
				}
				
				listStaticBindings.add(newEntry,
					listStaticBindings.getItemCount());
			    }
			});
	new Label(buttonCompositeFilterName, SWT.NONE);
	
		Button removeButton = new Button(buttonCompositeFilterName, SWT.PUSH
			| SWT.CENTER);
		removeButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
			removeButton.setText("Remove Selection"); //$NON-NLS-1$
			removeButton.addSelectionListener(new SelectionAdapter() {
			    public void widgetSelected(SelectionEvent event) {
				listStaticBindings.remove(listStaticBindings
					.getSelectionIndex());
			    }
			});

	// test-method-filter modifier
	Group cmpExceptions = new Group(cmpMain, SWT.NONE);
	cmpExceptions
		.setText("Here you can define specific settings for the selected binding");

	// Create a data that takes up the extra space in the dialog .
	gd_cmpExceptions = new GridData(GridData.FILL_HORIZONTAL);
	gd_cmpExceptions.exclude = true;
	gd_cmpExceptions.verticalAlignment = SWT.FILL;
	gd_cmpExceptions.grabExcessVerticalSpace = true;
	gd_cmpExceptions.grabExcessHorizontalSpace = true;
	cmpExceptions.setLayoutData(gd_cmpExceptions);

	GridLayout gl_cmpExceptions = new GridLayout();
	cmpExceptions.setLayout(gl_cmpExceptions);

	return cmpMain;
    }

    /**
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(IWorkbench workbench) {
	// Initialize the preference store we wish to use
	setPreferenceStore(Activator.getDefault().getPreferenceStore());
	setDescription(Messages.JUTPreferenceStaticBindingsPage_description);
    }

    protected void performDefaults() {
	listStaticBindings.setItems(getDefaultStaticBindings());
    }

    public boolean performOk() {
	setStaticBindings(listStaticBindings.getItems());
	return super.performOk();
    }

    public void setStaticBindings(String[] values) {
	getPreferenceStore().setValue(STATIC_BINDINGS,
		JUTPreferences.convert(values));
    }

    public String[] getDefaultStaticBindings() {
	return JUTPreferences.convert(getPreferenceStore().getDefaultString(
		STATIC_BINDINGS));
    }
    
    public String[] getStaticBindings() {
	return JUTPreferences.convert(getPreferenceStore().getString(
		STATIC_BINDINGS));
    }
}