package org.junit.tools.ui.generator.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.junit.tools.generator.model.GeneratorModel;
import org.junit.tools.messages.Messages;
import org.junit.tools.ui.generator.wizards.pages.GeneratorWizardMainPage;

/**
 * Main-class for the generator wizard.
 * 
 * @author Robert Streng
 * 
 */
public class GeneratorWizard extends Wizard implements INewWizard {

    private GeneratorWizardMainPage mainPage;

    private boolean finished = false;

    private final GeneratorModel utmModel;

    private boolean initialized = false;

    public GeneratorWizard(GeneratorModel model) {
	this.utmModel = model;
    }

    /**
     * Adding the page to the wizard.
     */

    @Override
    public void addPages() {
	String title = Messages.GeneratorWizard_Unit_test_class_generator;

	mainPage = createMainPage(title);
	addPage(mainPage);
    }

    @Override
    public void createPageControls(Composite pageContainer) {
	super.createPageControls(pageContainer);
    }

    /**
     * Initializes the pages.
     */
    public void initPages() {

	if (initialized) {
	    return;
	} else {
	    initialized = true;
	}

	getMainPage().init();
    }

    /**
     * Creates the main page.
     * 
     * @param title
     * @return the main page
     */
    private GeneratorWizardMainPage createMainPage(String title) {
	String description = Messages.GeneratorWizard_description_main;
	GeneratorWizardMainPage page = new GeneratorWizardMainPage(title,
		description, Messages.GeneratorWizard_mainPage, utmModel);

	return page;
    }

    /**
     * Returns if the pages are initialized.
     * 
     * @return true if pages are initialized
     */
    protected boolean isPagesInitialized() {
	return this.initialized;
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard.
     */
    @Override
    public boolean performFinish() {
	getMainPage().update();

	finished = true;
	return true;
    }

    /**
     * Returns if the processing is finished.
     * 
     * @return true if the processing is finished.
     */
    public boolean isFinished() {
	return finished;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
	// nothing
    }

    /**
     * @return main page
     */
    private GeneratorWizardMainPage getMainPage() {
	return mainPage;
    }

    @Override
    public boolean needsProgressMonitor() {
	return true;
    }

}