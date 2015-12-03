package org.junit.tools.ui.generator.wizards;

import org.junit.tools.generator.model.GeneratorModel;
import org.junit.tools.generator.model.tml.ObjectFactory;
import org.junit.tools.ui.generator.wizards.pages.GeneratorWizardBasePage;

/**
 * The controller for the base page.
 * 
 * @author Robert Streng
 * 
 */
public abstract class GeneratorWizardBase {

    private final GeneratorWizardBasePage generatorWizardPage;
    private final GeneratorModel model;
    private ObjectFactory of = new ObjectFactory();

    /**
     * @param model
     * @param generatorWizardPageBase
     */
    public GeneratorWizardBase(GeneratorModel model,
	    GeneratorWizardBasePage generatorWizardPageBase) {
	this.model = model;
	this.generatorWizardPage = generatorWizardPageBase;
    }

    /**
     * Returns the Page.
     * 
     * @return the page
     */
    protected GeneratorWizardBasePage getPage() {
	return generatorWizardPage;
    }

    /**
     * Returns the junit-tools-model with all test data.
     * 
     * @return the UTM-model
     */
    protected GeneratorModel getModel() {
	return model;
    }

    /**
     * Returns the object factory for the TML-elements.
     * 
     * @return the object factory
     */
    public ObjectFactory getObjectFactory() {
	return of;
    }

    /**
     * Sets the object factory.
     * 
     * @param of
     */
    public void setObjectFactory(ObjectFactory of) {
	this.of = of;
    }

    /**
     * Initializes the page
     */
    public abstract void initPage();

    /**
     * Updates the UTM-model.
     */
    public abstract void updateModel();

}
