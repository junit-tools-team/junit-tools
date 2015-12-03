package org.junit.tools.ui.generator.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.junit.tools.ui.generator.swt.view.MockGeneratorView;

public class MockGeneratorWizardPage extends WizardPage {

    private MockGeneratorView view;

    public MockGeneratorWizardPage(String pageName) {
	super(pageName);
    }

    @Override
    public void createControl(Composite parent) {
	view = new MockGeneratorView(parent, SWT.NONE);
	setControl(parent);
    }

    public MockGeneratorView getView() {
	return view;
    }
}
