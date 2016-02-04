package org.junit.tools.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.junit.tools.Activator;
import org.junit.tools.messages.Messages;
import org.junit.tools.preferences.IJUTPreferenceConstants;

/**
 * The page for the main preferences.
 * 
 * @author Robert Streng
 * 
 */
public class JUTPreferenceMainPage extends FieldEditorPreferencePage implements
	IWorkbenchPreferencePage, IJUTPreferenceConstants {

    public JUTPreferenceMainPage() {
    }

    @Override
    public void init(IWorkbench workbench) {
	setPreferenceStore(Activator.getDefault().getPreferenceStore());
	setDescription(Messages.JUTPreferenceMainPage_description_Main_settings);
    }

    @Override
    protected void createFieldEditors() {
	addField(new StringFieldEditor(TEST_PROJECT_POSTFIX,
		Messages.JUTPreferenceMainPage_Test_project_postfix,
		getFieldEditorParent()));

	addField(new StringFieldEditor(TEST_SOURCE_FOLDER_NAME,
		Messages.JUTPreferenceMainPage_Test_source_folder_name,
		getFieldEditorParent()));
	
	addField(new StringFieldEditor(TEST_PACKAGE_POSTFIX,
		Messages.JUTPreferenceMainPage_Test_package_postfix,
		getFieldEditorParent()));

	addField(new StringFieldEditor(TEST_CLASS_POSTFIX,
		Messages.JUTPreferenceMainPage_Test_class_postfix,
		getFieldEditorParent()));

	addField(new StringFieldEditor(TEST_METHOD_PREFIX,
		Messages.JUTPreferenceMainPage_Test_method_prefix,
		getFieldEditorParent()));

	addField(new StringFieldEditor(TEST_METHOD_POSTFIX,
		Messages.JUTPreferenceMainPage_Test_Method_postfix,
		getFieldEditorParent()));

	addField(new StringFieldEditor(TEST_CLASS_SUPER_TYPE,
		Messages.JUTPreferenceMainPage_Testclass_supertype,
		getFieldEditorParent()));

	addField(new StringFieldEditor(MOCK_PROJECT,
		Messages.JUTPreferenceMainPage_Mock_Project,
		getFieldEditorParent()));

	addField(new BooleanFieldEditor(WRITE_TML,
		Messages.JUTPreferenceMainPage_write_TML,
		getFieldEditorParent()));

	addField(new StringFieldEditor(TML_CONTAINER,
		Messages.JUTPreferenceMainPage_TML_container,
		getFieldEditorParent()));

    }

}
