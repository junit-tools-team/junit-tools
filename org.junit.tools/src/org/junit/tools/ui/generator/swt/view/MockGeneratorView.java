package org.junit.tools.ui.generator.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class MockGeneratorView extends Composite {

    private final GroupMethodSelectionView methodSelectionGroup;
    private final Button btnSelectProject;
    private final Text txtProject;

    public MockGeneratorView(Composite parent, int style) {
	super(parent, style);
	setLayout(new GridLayout(1, false));

	methodSelectionGroup = new GroupMethodSelectionView(this, SWT.NONE);
	methodSelectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
		true, true, 1, 1));

	Composite projectContainer = new Composite(this, SWT.NONE);
	projectContainer.setLayout(new GridLayout(2, false));

	txtProject = new Text(projectContainer, SWT.NONE);
	GridData gd_txtProject = new GridData(SWT.LEFT, SWT.CENTER, true,
		false, 1, 1);
	gd_txtProject.widthHint = 200;
	txtProject.setLayoutData(gd_txtProject);
	txtProject.setEditable(false);

	btnSelectProject = new Button(projectContainer, SWT.NONE);
	btnSelectProject.setText("Select project");
    }

    public GroupMethodSelectionView getMethodSelectionGroup() {
	return methodSelectionGroup;
    }

    public Button getBtnSelectProject() {
	return btnSelectProject;
    }

    public Text getTxtProject() {
	return txtProject;
    }

}
