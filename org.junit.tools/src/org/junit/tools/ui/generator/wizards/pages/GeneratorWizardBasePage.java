package org.junit.tools.ui.generator.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.junit.tools.generator.model.GeneratorModel;
import org.junit.tools.ui.generator.wizards.GeneratorWizardBase;

/**
 * Base class for the wizard pages.
 * 
 * @author JUnit-Tools-Team
 * 
 */
public abstract class GeneratorWizardBasePage extends WizardPage {

    protected GeneratorWizardBase controller;

    public void init() {
	getController().initPage();
    }

    /**
     * Updates the UTM-Model.
     */
    public void update() {
	getController().updateModel();
    }

    /**
     * Constructor
     * 
     * @param title
     * @param description
     * @param pageName
     * @param model
     */
    public GeneratorWizardBasePage(String title, String description,
	    String pageName, GeneratorModel model) {
	super(pageName);
	setTitle(title);
	setDescription(description);
	createController(model);
    }

    /**
     * @return the wizard-controller
     */
    protected GeneratorWizardBase getController() {
	return controller;
    }

    /**
     * Creates the wizard-controller.
     * 
     * @param model
     */
    protected abstract void createController(GeneratorModel model);

    /**
     * Updates the page status.
     * 
     * @param message
     */
    public void updateStatus(String message) {
	setErrorMessage(message);
	setPageComplete(message == null);
    }

}
