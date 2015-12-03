package org.junit.tools.ui.generator.swt.view;

import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.junit.tools.messages.Messages;

public class GroupMethodSelectionView extends Composite {

    private CheckboxTreeViewer checkboxTreeViewer;

    private Button btnNamefilter;

    private Button btnModifierfilter;

    private Button btnSelectAll;

    private Button btnDeselectAll;

    private Button btnExistingMethodsFilter;

    public GroupMethodSelectionView(Composite parent, int style) {
	super(parent, style);
	setLayout(new GridLayout(1, false));

	Group group = new Group(this, SWT.NONE);

	group.setText(Messages.GeneratorWizardMainPage_methods);

	group.setLayout(new GridLayout());
	group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	SashForm sashForm = new SashForm(group, SWT.NONE);
	sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	Composite composite = new Composite(sashForm, SWT.FILL);
	composite.setLayout(new GridLayout(1, false));

	ScrolledComposite scrolledComposite = new ScrolledComposite(composite,
		SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		true));
	scrolledComposite.setExpandVertical(true);
	scrolledComposite.setExpandHorizontal(true);

	// tree-viewer
	createTreeViewer(scrolledComposite);
	Tree tree = checkboxTreeViewer.getTree();
	scrolledComposite.setContent(tree);
	scrolledComposite
		.setMinSize(tree.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	// tree-config
	Composite composite_3 = new Composite(sashForm, SWT.NONE);
	composite_3.setLayout(new GridLayout(1, false));

	btnSelectAll = new Button(composite_3, SWT.ALL);
	btnSelectAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
		false));
	btnSelectAll.setText(Messages.GeneratorWizardMainPage_Select_all);

	btnDeselectAll = new Button(composite_3, SWT.NONE);
	btnDeselectAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
		false));
	btnDeselectAll.setText(Messages.GeneratorWizardMainPage_Deselect_all);

	Label lblFilter = new Label(composite_3, SWT.NONE);
	lblFilter.setText(Messages.GeneratorWizardMainPage_Filter);

	btnNamefilter = new Button(composite_3, SWT.CHECK);
	btnNamefilter.setSelection(true);
	btnNamefilter.setText(Messages.GeneratorWizardMainPage_name);

	btnModifierfilter = new Button(composite_3, SWT.CHECK);
	btnModifierfilter.setText(Messages.GeneratorWizardMainPage_modifier);
	btnModifierfilter.setSelection(true);

	btnExistingMethodsFilter = new Button(composite_3, SWT.CHECK);
	btnExistingMethodsFilter
		.setText(Messages.GeneratorWizardMainPage_existing_methods);
	btnExistingMethodsFilter.setSelection(true);

	sashForm.setWeights(new int[] { 10, 3 });

    }

    private CheckboxTreeViewer createTreeViewer(Composite parent) {
	checkboxTreeViewer = new ContainerCheckedTreeViewer(parent, SWT.CHECK);

	ITreeContentProvider contentProvider = new StandardJavaElementContentProvider(
		true);
	checkboxTreeViewer.setContentProvider(contentProvider);

	// label provider
	ILabelProvider labelProvider = new JavaElementLabelProvider(
		JavaElementLabelProvider.SHOW_DEFAULT
			| JavaElementLabelProvider.SHOW_RETURN_TYPE);
	checkboxTreeViewer.setLabelProvider(labelProvider);

	return checkboxTreeViewer;
    }

    public CheckboxTreeViewer getCheckboxTreeViewer() {
	return checkboxTreeViewer;
    }

    public Button getBtnNamefilter() {
	return btnNamefilter;
    }

    public Button getBtnModifierfilter() {
	return btnModifierfilter;
    }

    public Button getBtnSelectAll() {
	return btnSelectAll;
    }

    public Button getBtnDeselectAll() {
	return btnDeselectAll;
    }

    public Button getBtnExistingMethodsFilter() {
	return btnExistingMethodsFilter;
    }

}
