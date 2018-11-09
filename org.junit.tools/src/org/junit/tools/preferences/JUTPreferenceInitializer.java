package org.junit.tools.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.tools.Activator;
import org.junit.tools.base.ExtensionPointHandler;

/**
 * Initializer for the junit-tools-preference-values.
 * 
 * @author Robert Streng
 */
public class JUTPreferenceInitializer extends AbstractPreferenceInitializer
	implements IJUTPreferenceConstants {

    public static final String DEFAULT_METHOD_FILTER_NAME = "get*;set*;";
    public static final String DEFAULT_METHOD_FILTER_MODIFIER = "private;protected;package;";

    @Override
    public void initializeDefaultPreferences() {
	IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	store.setDefault(WRITE_TML, false);

	store.setDefault(TML_CONTAINER, "test_files");

	store.setDefault(TEST_PROJECT_POSTFIX, ".test");
	
	store.setDefault(TEST_SOURCE_FOLDER_NAME, "src");

	store.setDefault(TEST_CLASS_SUPER_TYPE, "");

	store.setDefault(TEST_CLASS_PREFIX, "");
	
	store.setDefault(TEST_CLASS_POSTFIX, "Test");

	store.setDefault(TEST_PACKAGE_POSTFIX, "");

	store.setDefault(TEST_METHOD_PREFIX, "test");
	store.setDefault(TEST_METHOD_POSTFIX, "");

	store.setDefault(TEST_METHOD_FILTER_NAME, DEFAULT_METHOD_FILTER_NAME);
	store.setDefault(TEST_METHOD_FILTER_MODIFIER,
		DEFAULT_METHOD_FILTER_MODIFIER);

	store.setDefault(MOCK_PROJECT, "org.junit.tools.mock");
	
	store.setDefault(MOCK_SAVE_IN_TESTPROJECT, true);
	
	store.setDefault(MOCK_SAVE_IN_TESTPROJECT, "powermock");

	store.setDefault(TEST_CLASS_ANNOTATIONS, "");
	
	store.setDefault(MOCK_CLASS_ANNOTATIONS, "");
	
	store.setDefault(STATIC_BINDINGS, "");
	
	// initialize JUT-preferences
	JUTPreferences.initialize();

	// set custom preferences
	ExtensionPointHandler extensionHandler = Activator.getDefault()
		.getExtensionHandler();
	for (AbstractPreferenceInitializer initializer : extensionHandler
		.getPreferenceInitializer()) {
	    initializer.initializeDefaultPreferences();
	}

    }

}
