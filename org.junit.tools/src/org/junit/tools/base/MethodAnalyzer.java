package org.junit.tools.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.junit.tools.generator.model.tml.Testprio;
import org.junit.tools.generator.utils.JDTUtils;

/**
 * Analyzer for a base method. The result can be used for test-case generations
 * and test-reports.
 * 
 * @author JUnit-Tools-Team
 * 
 */
public class MethodAnalyzer {

    public class MethodAnalyzeResult {

	private Testprio testPrio = Testprio.DEFAULT;

	private List<IfStatement> ifStatements = new ArrayList<IfStatement>();

	private boolean onlyNullChecks = false;

	public Testprio getTestPrio() {
	    return testPrio;
	}

	public void setTestPrio(Testprio testPrio) {
	    this.testPrio = testPrio;
	}

	public List<IfStatement> getIfStatements() {
	    return ifStatements;
	}

	public void setIfStatements(List<IfStatement> ifInstructions) {
	    this.ifStatements = ifInstructions;
	}

	public boolean isOnlyNullChecks() {
	    return onlyNullChecks;
	}

	public void setOnlyNullChecks(boolean onlyNullChecks) {
	    this.onlyNullChecks = onlyNullChecks;
	}

	public int getNumberOfIfStatements() {
	    return ifStatements.size();
	}
    }

    private List<IfStatement> collectIfStatements(Statement st) {
	List<IfStatement> ifStatements = new ArrayList<IfStatement>();

	if (st == null) {
	    return ifStatements;
	}

	if (st.getNodeType() == ASTNode.IF_STATEMENT) {
	    IfStatement ifSt = (IfStatement) st;
	    ifStatements.add(ifSt);

	    ifStatements.addAll(collectIfStatements(ifSt.getThenStatement()));
	    ifStatements.addAll(collectIfStatements(ifSt.getElseStatement()));
	} else if (st.getNodeType() == ASTNode.BLOCK) {
	    Block block = (Block) st;

	    for (Object blockSt : block.statements()) {
		if (blockSt instanceof Statement) {
		    ifStatements
			    .addAll(collectIfStatements((Statement) blockSt));
		}
	    }
	} else if (st.getNodeType() == ASTNode.DO_STATEMENT) {
	    DoStatement doSt = (DoStatement) st;
	    ifStatements.addAll(collectIfStatements(doSt.getBody()));
	} else if (st.getNodeType() == ASTNode.WHILE_STATEMENT) {
	    WhileStatement whileSt = (WhileStatement) st;
	    ifStatements.addAll(collectIfStatements(whileSt.getBody()));
	}

	return ifStatements;
    }

    public Map<IMethod, MethodAnalyzeResult> analyzeAllMethods(
	    ICompilationUnit cu) throws JavaModelException {
	Map<IMethod, MethodAnalyzeResult> analyzeResult = new HashMap<IMethod, MethodAnalyzeResult>();

	CompilationUnit astCu = JDTUtils.createASTRoot(cu);
	MethodAnalyzeResult mar;

	for (IType type : cu.getAllTypes()) {
	    for (IMethod method : type.getMethods()) {
		mar = analyzeMethod(astCu, method);

		if (mar != null && mar.getTestPrio() != null) {
		    analyzeResult.put(method, mar);
		}
	    }
	}

	return analyzeResult;
    }

    private MethodAnalyzeResult analyzeMethod(CompilationUnit astCu,
	    IMethod method) {
	MethodAnalyzeResult mar = new MethodAnalyzeResult();
	MethodDeclaration md = JDTUtils.createMethodDeclaration(astCu, method);
	if (md == null) {
	    return mar;
	}

	List<IfStatement> ifStatements = collectIfStatements(md);
	int numberOfIfStatements = ifStatements.size();

	mar.setIfStatements(ifStatements);

	// check if only null checks
	if (ifStatements.size() > 0) {
	    boolean onlyNullChecks = true;

	    for (IfStatement ifSt : ifStatements) {
		Expression expression = ifSt.getExpression();

		if (expression.getNodeType() == ASTNode.INFIX_EXPRESSION) {
		    InfixExpression infixEx = (InfixExpression) expression;
		    Expression leftOperand = infixEx.getLeftOperand();
		    Expression rightOperand = infixEx.getRightOperand();
		    Operator operator = infixEx.getOperator();

		    if (operator.equals(Operator.EQUALS)
			    || operator.equals(Operator.NOT_EQUALS)) {
			if (leftOperand.getNodeType() == ASTNode.NULL_LITERAL
				|| rightOperand.getNodeType() == ASTNode.NULL_LITERAL) {
			    continue;
			}
		    }

		}

		onlyNullChecks = false;
	    }

	    mar.setOnlyNullChecks(onlyNullChecks);
	}

	// define test priority
	if (numberOfIfStatements > 6) {
	    mar.setTestPrio(Testprio.HIGH);
	} else if (numberOfIfStatements > 3) {
	    mar.setTestPrio(Testprio.DEFAULT);
	} else if (numberOfIfStatements > 0) {
	    mar.setTestPrio(Testprio.LOW);
	} else {
	    mar.setTestPrio(null);
	}

	return mar;
    }

    private List<IfStatement> collectIfStatements(MethodDeclaration md) {
	List<IfStatement> ifStatements = new ArrayList<IfStatement>();

	Block body = md.getBody();

	if (body == null) {
	    return ifStatements;
	}

	for (Object statement : body.statements()) {
	    if (statement instanceof Statement) {
		Statement st = (Statement) statement;
		ifStatements.addAll(collectIfStatements(st));
	    }
	}

	return ifStatements;
    }
}
