package org.junit.tools.ui.generator.wizards;

import java.util.Vector;

import org.eclipse.jdt.core.IMethod;

public interface IMethodeSelectionChangedListener {

    public void methodSelectionChanged(Vector<IMethod> checkedMethods);

    public void selectedMethodChecked(IMethod selectedMethod);
}
