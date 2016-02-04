package org.junit.tools.messages;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for junit-tools.
 * 
 * @author Robert Streng
 * 
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.junit.tools.messages.messages"; //$NON-NLS-1$

    // general messages
    public static String General_information;
    public static String General_warning;
    public static String General_error;

    // GUI messages
    public static String General_select_all;

    // Info-, warning- and error-messages
    public static String General_warning_test_project_not_found;
    public static String General_warning_base_project_not_found;
    public static String General_warning_project_initialization;
    public static String General_warning_nothing_selected;
    public static String General_error_processing;
    public static String General_info_generation_successful;

    public static String General_info_process_successful;

    public static String GeneratorUtils_ErrorPackageCreation;

    public static String GeneratorUtils_MethodExists;

    public static String GeneratorUtils_OnlyJavaProjects;

    public static String GeneratorUtils_SelectionEnd;

    public static String GeneratorUtils_SelectionNotSupported;

    public static String GeneratorWizardMainPage_afterMethodCall;

    public static String GeneratorWizardMainPage_beforeMethodCall;

    public static String GeneratorWizardMainPage_Browse;

    public static String GeneratorWizardMainPage_createTestBaseAfter;

    public static String GeneratorWizardMainPage_createTestBaseBefore;

    public static String GeneratorWizardMainPage_Deselect_all;

    public static String GeneratorWizardMainPage_existing_methods;

    public static String GeneratorWizardMainPage_fail_assertions;

    public static String GeneratorWizardMainPage_Filter;

    public static String GeneratorWizardMainPage_Generate;

    public static String GeneratorWizardMainPage_high;

    public static String GeneratorWizardMainPage_Hooks_for_manuals;

    public static String GeneratorWizardMainPage_logger;

    public static String GeneratorWizardMainPage_low;

    public static String GeneratorWizardMainPage_main_settings;

    public static String GeneratorWizardMainPage_methods;

    public static String GeneratorWizardMainPage_modifier;

    public static String GeneratorWizardMainPage_name;

    public static String GeneratorWizardMainPage_Other;

    public static String GeneratorWizardMainPage_Select_all;

    public static String GeneratorWizardMainPage_Standardmethods;

    public static String GeneratorWizardMainPage_setUp;

    public static String GeneratorWizardMainPage_setUpBeforeClass;

    public static String GeneratorWizardMainPage_standard;

    public static String GeneratorWizardMainPage_Super_class;

    public static String GeneratorWizardMainPage_tearDown;

    public static String GeneratorWizardMainPage_tearDownAfterClass;

    public static String GeneratorWizardMainPage_testsuites;

    public static String GeneratorWizardMainPage_toggle;

    public static String GeneratorWizardMainPage_Testmethod_prefix;

    public static String GeneratorWizardMainPage_Testpriority;

    public static String GeneratorWizardMainPage_Testproject;

    public static String GeneratorWizardTestBases_ErrorDuringConstructorHandling;

    public static String GeneratorWizardTestBases_ErrorWhileGetParamsFromSelectedMethod;

    public static String GeneratorWizardTestBases_ErrorWhileWritingTmlFile;

    public static String GeneratorWizardTestBases_method;

    public static String GeneratorWizardTestBases_MustEditTestIds;

    public static String GeneratorWizardTestBases_ReturnValue;

    public static String GeneratorWizardTestBases_testBaseId;

    public static String GeneratorWizardTestBases_TestBaseIdMustBeUnique;

    public static String GeneratorWizardTestBasesPage_Constructor;

    public static String GeneratorWizardTestBasesPage_mocks;

    public static String GeneratorWizardTestBasesPage_testbases;

    public static String GeneratorWizardTestCasesPage_assertions;
    public static String GeneratorWizardTestCasesPage_Method;
    public static String GeneratorWizardTestCasesPage_testcases;
    public static String JUTPreferenceFilterPage_description_filters;

    public static String JUTPreferenceFilterPage_Modifier_filter;

    public static String JUTPreferenceFilterPage_Name_filter;

    public static String JUTPreferenceMainPage_description_Main_settings;
    public static String JUTPreferenceMainPage_Test_class_postfix;
    public static String JUTPreferenceMainPage_Test_Method_postfix;
    public static String JUTPreferenceMainPage_Test_method_prefix;
    public static String JUTPreferenceMainPage_Test_source_folder_name;
    public static String JUTPreferenceMainPage_Test_package_postfix;
    public static String JUTPreferenceMainPage_Test_project_postfix;
    public static String JUTPreferenceMainPage_Testclass_supertype;
    public static String JUTPreferenceMainPage_write_TML;
    public static String JUTPreferenceMainPage_TML_container;
    public static String JUTPreferenceMainPage_Mock_Project;

    public static String GeneratorWizard_description_main;
    public static String GeneratorWizard_description_testbases;
    public static String GeneratorWizard_description_testcases;
    public static String GeneratorWizard_mainPage;
    public static String GeneratorWizard_testbases;
    public static String GeneratorWizard_testbasesPage;
    public static String GeneratorWizard_testcases;
    public static String GeneratorWizard_testcasesPage;
    public static String GeneratorWizard_Unit_test_class_generator;

    public static String TableViewerBase_Add;

    public static String TableViewerBase_Copy;

    public static String TableViewerBase_Delete;

    static {
	// initialize resource bundle
	NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
