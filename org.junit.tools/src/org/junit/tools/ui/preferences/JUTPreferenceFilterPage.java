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
 * The page for the filter preferences.
 * 
 * @author JUnit-Tools-Team
 * 
 */
public class JUTPreferenceFilterPage extends PreferencePage implements
	IWorkbenchPreferencePage, IJUTPreferenceConstants {
    public JUTPreferenceFilterPage() {
    }

    private List listMethodFilterName;

    private Text newMethodFilterName;

    private List listMethodFilterModifier;

    private Text newMethodFilterModifier;
    private GridData data_1;
    private GridData data_2;
    private GridData data_3;

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
	Group cmpTestmethodFilterName = new Group(cmpMain, SWT.NONE);
	cmpTestmethodFilterName
		.setText(Messages.JUTPreferenceFilterPage_Name_filter);
	GridData data = new GridData(GridData.FILL_HORIZONTAL);
	data.verticalAlignment = SWT.FILL;
	data.grabExcessVerticalSpace = true;
	data.grabExcessHorizontalSpace = true;
	cmpTestmethodFilterName.setLayoutData(data);
	cmpTestmethodFilterName.setLayout(new GridLayout());

	listMethodFilterName = new List(cmpTestmethodFilterName, SWT.BORDER);
	listMethodFilterName.setItems(getMethodFilterNamePref());

	// Create a data that takes up the extra space in the dialog and spans
	// both columns.
	data = new GridData(GridData.FILL_BOTH);
	listMethodFilterName.setLayoutData(data);

	Composite buttonCompositeFilterName = new Composite(
		cmpTestmethodFilterName, SWT.NULL);

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
		String newEntry = newMethodFilterName.getText();
		for (String item : listMethodFilterName.getItems()) {
		    if (newEntry.equals(item)) {
			return;
		    }
		}
		
		listMethodFilterName.add(newEntry,
			listMethodFilterName.getItemCount());
	    }
	});

	newMethodFilterName = new Text(buttonCompositeFilterName, SWT.BORDER);
	// Create a data that takes up the extra space in the dialog .
	data = new GridData(GridData.FILL_HORIZONTAL);
	data.grabExcessHorizontalSpace = true;
	newMethodFilterName.setLayoutData(data);

	Button removeButton = new Button(buttonCompositeFilterName, SWT.PUSH
		| SWT.CENTER);

	removeButton.setText("Remove Selection"); //$NON-NLS-1$
	removeButton.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		listMethodFilterName.remove(listMethodFilterName
			.getSelectionIndex());
	    }
	});

	data = new GridData();
	data.horizontalSpan = 2;
	removeButton.setLayoutData(data);

	// test-method-filter modifier
	Group cmpTestmethodFilterModifier = new Group(cmpMain, SWT.NONE);
	cmpTestmethodFilterModifier
		.setText(Messages.JUTPreferenceFilterPage_Modifier_filter);

	// Create a data that takes up the extra space in the dialog .
	data_1 = new GridData(GridData.FILL_HORIZONTAL);
	data_1.verticalAlignment = SWT.FILL;
	data_1.grabExcessVerticalSpace = true;
	data_1.grabExcessHorizontalSpace = true;
	cmpTestmethodFilterModifier.setLayoutData(data_1);

	GridLayout layout = new GridLayout();
	cmpTestmethodFilterModifier.setLayout(layout);

	listMethodFilterModifier = new List(cmpTestmethodFilterModifier,
		SWT.BORDER);
	listMethodFilterModifier.setItems(getMethodFilterModifierPref());

	// Create a data that takes up the extra space in the dialog and spans
	// both columns.
	data = new GridData(GridData.FILL_BOTH);
	listMethodFilterModifier.setLayoutData(data);

	Composite buttonComposite = new Composite(cmpTestmethodFilterModifier,
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
		String newEntry = newMethodFilterModifier.getText();
		for (String item : listMethodFilterModifier.getItems()) {
		    if (newEntry.equals(item)) {
			return;
		    }
		}
		
		listMethodFilterModifier.add(newEntry,
			listMethodFilterModifier.getItemCount());
	    }
	});

	newMethodFilterModifier = new Text(buttonComposite, SWT.BORDER);
	// Create a data that takes up the extra space in the dialog .
	data = new GridData(GridData.FILL_HORIZONTAL);
	data.grabExcessHorizontalSpace = true;
	newMethodFilterModifier.setLayoutData(data);

	removeButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);

	removeButton.setText("Remove Selection"); //$NON-NLS-1$
	removeButton.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		listMethodFilterModifier.remove(listMethodFilterModifier
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
	setDescription(Messages.JUTPreferenceFilterPage_description_filters);
    }

    protected void performDefaults() {
	listMethodFilterName.setItems(getDefaultMethodFilterNamePref());
	listMethodFilterModifier.setItems(getDefaultMethodFilterModifierPref());
    }

    public boolean performOk() {
	setMethodFilterNamePref(listMethodFilterName.getItems());
	setMethodFilterModifierPref(listMethodFilterModifier.getItems());
	return super.performOk();
    }

    /**
     * Sets the method filter name preferences
     * 
     * @param values
     */
    public void setMethodFilterNamePref(String[] values) {
	getPreferenceStore().setValue(TEST_METHOD_FILTER_NAME,
		JUTPreferences.convert(values));
    }

    /**
     * @return default method filter name preferences
     */
    public String[] getDefaultMethodFilterNamePref() {
	return JUTPreferences.convert(getPreferenceStore().getDefaultString(
		TEST_METHOD_FILTER_NAME));
    }

    /**
     * @return method filter name preferences
     */
    public String[] getMethodFilterNamePref() {
	return JUTPreferences.convert(getPreferenceStore().getString(
		TEST_METHOD_FILTER_NAME));
    }

    /**
     * Sets the method filter modifier preferences
     * 
     * @param values
     */
    public void setMethodFilterModifierPref(String[] values) {
	getPreferenceStore().setValue(TEST_METHOD_FILTER_MODIFIER,
		JUTPreferences.convert(values));
    }

    /**
     * @return default method filter modifier preferences
     */
    public String[] getDefaultMethodFilterModifierPref() {
	return JUTPreferences.convert(getPreferenceStore().getDefaultString(
		TEST_METHOD_FILTER_MODIFIER));
    }

    /**
     * @return method filter modifier preferences
     */
    public String[] getMethodFilterModifierPref() {
	return JUTPreferences.convert(getPreferenceStore().getString(
		TEST_METHOD_FILTER_MODIFIER));
    }
}