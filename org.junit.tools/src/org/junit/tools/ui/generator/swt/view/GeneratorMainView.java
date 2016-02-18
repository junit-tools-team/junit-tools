package org.junit.tools.ui.generator.swt.view;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.junit.tools.messages.Messages;

public class GeneratorMainView extends Composite {

    public Text getTxtSuperClass() {
	return txtSuperClass;
    }

    public Button getBtnLogger() {
	return btnLogger;
    }

    public Text getMethodPrefix() {
	return methodPrefix;
    }

    public Text getTxtTestProject() {
	return txtTestProject;
    }

    /*public Button getBtnPrioHigh() {
	return btnPrioHigh;
    }

    public Button getBtnPrioStandard() {
	return btnPrioStandard;
    }

    public Button getBtnPrioLow() {
	return btnPrioLow;
    }*/

    public Button getBtnFailassertion() {
	return btnFailassertion;
    }

    public Button getBtnSetupbeforeclass() {
	return btnSetupbeforeclass;
    }

    public Button getBtnTeardown() {
	return btnTeardown;
    }

    public Button getBtnTeardownafterclass() {
	return btnTeardownafterclass;
    }

    public Button getBtnToggleStandardMethods() {
	return btnToggleStandardMethods;
    }

    public Button getBtnTestsuites() {
	return btnTestsuites;
    }

    public Button getBtnToggleOther() {
	return btnToggleOther;
    }

    public Button getBtnSetup() {
	return btnSetup;
    }

    public GroupMethodSelectionView getMethodSelectionGroup() {
	return methodSelectionGroup;
    }

//    public Button getBtnTestProject() {
//	return btnTestProject;
//    }

    public Button getBtnSuperClass() {
	return btnSuperClass;
    }

    private Text txtSuperClass;

    private final Button btnLogger;

    private final Text methodPrefix;

    private final Text txtTestProject;

    /*private final Button btnPrioHigh;

    private final Button btnPrioStandard;

    private final Button btnPrioLow;*/

    private final Button btnFailassertion;

    private final Button btnSetupbeforeclass;

    private final Button btnTeardown;

    private final Button btnTeardownafterclass;

    private final Button btnToggleStandardMethods;

    private final Button btnTestsuites;

    private final Button btnToggleOther;

    private final Button btnSetup;

    private final GroupMethodSelectionView methodSelectionGroup;

//    private final Button btnTestProject;

    private Button btnSuperClass;

    public GeneratorMainView(Composite parent, int style, boolean withSuperclass) {
	super(parent, style);
	setLayout(new GridLayout(1, false));

	methodSelectionGroup = new GroupMethodSelectionView(this, SWT.NONE);
	methodSelectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
		true, true, 1, 1));

	// main settings
	// ScrolledComposite composite_1 = new ScrolledComposite(this,
	// SWT.H_SCROLL | SWT.V_SCROLL);
	// composite_1.setLayout(new GridLayout(2, false));
	// composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
	// true, 1 ,1));
	// composite_1.setExpandVertical(true);
	// composite_1.setExpandHorizontal(true);

	Composite projectContainer = new Composite(this, SWT.NONE);
	projectContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 1, 1));
	projectContainer.setLayout(new GridLayout(2, false));

	Group grpMainSettings = new Group(projectContainer, SWT.NONE);
	grpMainSettings.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		true, 2, 1));
	grpMainSettings.setText(Messages.GeneratorWizardMainPage_main_settings);
	grpMainSettings.setLayout(new GridLayout(1, false));

	Composite composite_2 = new Composite(grpMainSettings, SWT.NONE);
	composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
		1, 1));
	GridLayout gl_composite_2 = new GridLayout(3, false);
	gl_composite_2.marginWidth = 0;
	gl_composite_2.marginHeight = 0;
	gl_composite_2.horizontalSpacing = 0;
	composite_2.setLayout(gl_composite_2);

	Label lblTestproject = new Label(composite_2, SWT.NONE);
	lblTestproject.setText(Messages.GeneratorWizardMainPage_Testproject);

	txtTestProject = new Text(composite_2, SWT.BORDER);
	txtTestProject.setEditable(false);
	txtTestProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false));

	new Label(composite_2, SWT.NONE);
	/*btnTestProject = new Button(composite_2, SWT.NONE);
	btnTestProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
		false));
	btnTestProject.setText(Messages.GeneratorWizardMainPage_Browse);*/

	if (withSuperclass) {
	    Label lblNewLabel = new Label(composite_2, SWT.NONE);
	    lblNewLabel.setText(Messages.GeneratorWizardMainPage_Super_class);

	    txtSuperClass = new Text(composite_2, SWT.BORDER);
	    txtSuperClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
		    true, false));

	    btnSuperClass = new Button(composite_2, SWT.NONE);
	    btnSuperClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
		    false, false));
	    btnSuperClass.setText(Messages.GeneratorWizardMainPage_Browse);
	}

	Label lblMethodprefix = new Label(composite_2, SWT.NONE);
	lblMethodprefix
		.setText(Messages.GeneratorWizardMainPage_Testmethod_prefix);

	methodPrefix = new Text(composite_2, SWT.BORDER);
	methodPrefix.setEditable(false);
	methodPrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false));
	new Label(composite_2, SWT.NONE);

	/*Label lblPrioritt = new Label(composite_2, SWT.NONE);
	lblPrioritt.setText(Messages.GeneratorWizardMainPage_Testpriority);

	Composite composite_4 = new Composite(composite_2, SWT.NONE);
	RowLayout rl_composite_4 = new RowLayout(SWT.HORIZONTAL);
	rl_composite_4.marginTop = 0;
	rl_composite_4.marginRight = 0;
	rl_composite_4.marginLeft = 0;
	rl_composite_4.marginBottom = 0;
	composite_4.setLayout(rl_composite_4);

	btnPrioHigh = new Button(composite_4, SWT.RADIO);
	btnPrioHigh.setText(Messages.GeneratorWizardMainPage_high);

	btnPrioStandard = new Button(composite_4, SWT.RADIO);
	btnPrioStandard.setText(Messages.GeneratorWizardMainPage_standard);
	btnPrioStandard.setSelection(true);

	btnPrioLow = new Button(composite_4, SWT.RADIO);
	btnPrioLow.setText(Messages.GeneratorWizardMainPage_low);
	new Label(composite_2, SWT.NONE);*/

	Label label = new Label(composite_2, SWT.SEPARATOR | SWT.HORIZONTAL
		| SWT.CENTER);
	label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

	Label label_1 = new Label(composite_2, SWT.SEPARATOR | SWT.HORIZONTAL);
	label_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

	Label label_2 = new Label(composite_2, SWT.SEPARATOR | SWT.HORIZONTAL);
	label_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

	Label lblGenerate = new Label(composite_2, SWT.NONE);
	lblGenerate.setText(Messages.GeneratorWizardMainPage_Generate);
	new Label(composite_2, SWT.NONE);
	new Label(composite_2, SWT.NONE);

	Label lblStandardTestmethods = new Label(composite_2, SWT.NONE);
	lblStandardTestmethods
		.setText(Messages.GeneratorWizardMainPage_Standardmethods);

	Composite composite_5 = new Composite(composite_2, SWT.NONE);
	GridLayout gl_composite_5 = new GridLayout(2, false);
	gl_composite_5.marginHeight = 0;
	gl_composite_5.verticalSpacing = 0;
	gl_composite_5.marginWidth = 0;
	composite_5.setLayout(gl_composite_5);
	composite_5
		.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

	btnSetup = new Button(composite_5, SWT.CHECK);
	btnSetup.setText(Messages.GeneratorWizardMainPage_setUp);

	btnTeardown = new Button(composite_5, SWT.CHECK);
	btnTeardown.setText(Messages.GeneratorWizardMainPage_tearDown);

	btnSetupbeforeclass = new Button(composite_5, SWT.CHECK);
	btnSetupbeforeclass
		.setText(Messages.GeneratorWizardMainPage_setUpBeforeClass);

	btnTeardownafterclass = new Button(composite_5, SWT.CHECK);
	btnTeardownafterclass
		.setText(Messages.GeneratorWizardMainPage_tearDownAfterClass);

	btnToggleStandardMethods = new Button(composite_2, SWT.NONE);
	btnToggleStandardMethods.addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// nothing
	    }
	});
	btnToggleStandardMethods.setLayoutData(new GridData(SWT.FILL,
		SWT.CENTER, false, false));
	btnToggleStandardMethods
		.setText(Messages.GeneratorWizardMainPage_toggle);

	Label lblOther = new Label(composite_2, SWT.NONE);
	lblOther.setText(Messages.GeneratorWizardMainPage_Other);

	Composite composite_7 = new Composite(composite_2, SWT.NONE);
	RowLayout rl_composite_7 = new RowLayout(SWT.HORIZONTAL);
	rl_composite_7.marginTop = 0;
	rl_composite_7.marginLeft = 0;
	rl_composite_7.marginRight = 0;
	rl_composite_7.marginBottom = 0;
	composite_7.setLayout(rl_composite_7);

	btnTestsuites = new Button(composite_7, SWT.CHECK);
	btnTestsuites.setText(Messages.GeneratorWizardMainPage_testsuites);

	btnLogger = new Button(composite_7, SWT.CHECK);
	btnLogger.setText(Messages.GeneratorWizardMainPage_logger);

	btnFailassertion = new Button(composite_7, SWT.CHECK);
	btnFailassertion
		.setText(Messages.GeneratorWizardMainPage_fail_assertions);

	btnToggleOther = new Button(composite_2, SWT.NONE);
	btnToggleOther.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
		false, 1, 1));
	btnToggleOther.setText(Messages.GeneratorWizardMainPage_toggle);

	// composite_1.setContent(grpMainSettings);
	// composite_1.setMinSize(grpMainSettings.computeSize(SWT.DEFAULT,
	// SWT.DEFAULT));
    }

    public CheckboxTreeViewer getCheckboxTreeViewer() {
	return methodSelectionGroup.getCheckboxTreeViewer();
    }

}
