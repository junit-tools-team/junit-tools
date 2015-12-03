package org.junit.tools.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.junit.tools.generator.model.GeneratorModel;
import org.junit.tools.generator.model.tml.Assertion;
import org.junit.tools.generator.model.tml.AssertionType;
import org.junit.tools.generator.model.tml.Method;
import org.junit.tools.generator.model.tml.ObjectFactory;
import org.junit.tools.generator.model.tml.Param;
import org.junit.tools.generator.model.tml.ParamAssignment;
import org.junit.tools.generator.model.tml.TestCase;
import org.junit.tools.generator.utils.GeneratorUtils;
import org.junit.tools.generator.utils.JDTUtils;

/**
 * The test-cases-generator. It's only a beta version.
 * 
 * @author Robert Streng
 * 
 */
public class TestCasesGenerator {

    public class ExpressionAnalyzer {

	private final Map<Expression, Integer> expressionOrder = new HashMap<Expression, Integer>();

	private final Map<Expression, org.eclipse.jdt.core.dom.PostfixExpression.Operator> expressionPostfixes = new HashMap<Expression, org.eclipse.jdt.core.dom.PostfixExpression.Operator>();

	private final Map<Expression, org.eclipse.jdt.core.dom.PrefixExpression.Operator> expressionPrefixes = new HashMap<Expression, org.eclipse.jdt.core.dom.PrefixExpression.Operator>();

	int filledCount = 0;

	List<NullLiteral> nullLiterals = new ArrayList<NullLiteral>();

	List<NumberLiteral> numberLiterals = new ArrayList<NumberLiteral>();

	private Operator operator;

	private List<Param> paramBaseList = null;

	private final List<Param> params = new ArrayList<Param>();

	List<PostfixExpression> postfixExpressions = new ArrayList<PostfixExpression>();

	List<PrefixExpression> prefixExpressions = new ArrayList<PrefixExpression>();

	List<SimpleName> simpleNames = new ArrayList<SimpleName>();

	private final List<List<TestCase>> testCasesToMerge = new ArrayList<List<TestCase>>();

	private Method tmlMethod = null;

	public ExpressionAnalyzer() {

	}

	public ExpressionAnalyzer(Method tmlMethod) {
	    this.tmlMethod = tmlMethod;
	    this.paramBaseList = tmlMethod.getParam();
	}

	/**
	 * @param processIfExpressions
	 */
	private void addTestCasesToMerge(List<TestCase> testCasesToMerge) {
	    this.testCasesToMerge.add(testCasesToMerge);
	}

	/**
	 * @param expression
	 */
	public void analyze(Expression expression) {
	    // param == null, null == param
	    if (expression.getNodeType() == ASTNode.NULL_LITERAL) {
		setNullLiteral((NullLiteral) expression);
	    } else if (expression.getNodeType() == ASTNode.SIMPLE_NAME) {
		setSimpleName((SimpleName) expression);
	    } else if (expression.getNodeType() == ASTNode.NUMBER_LITERAL) {
		setNumberLiteral((NumberLiteral) expression);
	    } else if (expression.getNodeType() == ASTNode.PREFIX_EXPRESSION) {
		setPrefixExpression((PrefixExpression) expression);
	    } else if (expression.getNodeType() == ASTNode.POSTFIX_EXPRESSION) {
		setPostfixExpression((PostfixExpression) expression);
	    } else if (expression.getNodeType() == ASTNode.PARENTHESIZED_EXPRESSION
		    || expression.getNodeType() == ASTNode.INFIX_EXPRESSION
		    || expression.getNodeType() == ASTNode.METHOD_INVOCATION) {
		// addTestCasesToMerge(processIfExpressions(expression,
		// tmlMethod));
	    } else {
		// TODO
		System.out.println("Expression could not be analyzed: "
			+ expression.getNodeType() + ". Expression: "
			+ expression.toString());
	    }
	}

	private Expression getExpressionFromList(
		List<? extends Expression> expressions, int i) {
	    if (expressions.size() > i) {
		return expressions.get(i);
	    }
	    return null;
	}

	public Expression getNullLiteral() {
	    return getExpressionFromList(nullLiterals, 0);
	}

	public Expression getNullLiteralSecond() {
	    return getExpressionFromList(nullLiterals, 1);
	}

	public NumberLiteral getNumberLiteral() {
	    return (NumberLiteral) getExpressionFromList(numberLiterals, 0);
	}

	public Expression getNumberLiteralSecond() {
	    return getExpressionFromList(numberLiterals, 1);
	}

	/**
	 * Liefert das Attribut operator
	 * 
	 * @return Wert von operator
	 */
	public Operator getOperator() {
	    return operator;
	}

	/**
	 * Liefert das Attribut param
	 * 
	 * @return Wert von param
	 */
	public Param getParam() {
	    return getParam(0);
	}

	public Param getParam(int i) {
	    if (params.size() > i) {
		return params.get(i);
	    }
	    return null;
	}

	public Param getParamSecond() {
	    return getParam(1);
	}

	public PostfixExpression getPostfixExpression() {
	    return (PostfixExpression) getExpressionFromList(
		    postfixExpressions, 0);
	}

	public List<PostfixExpression> getPostfixExpressions() {
	    return postfixExpressions;
	}

	public PostfixExpression getPostfixExpressionSecond() {
	    return (PostfixExpression) getExpressionFromList(
		    postfixExpressions, 1);
	}

	public PrefixExpression getPrefixExpression() {
	    return (PrefixExpression) getExpressionFromList(prefixExpressions,
		    0);
	}

	public List<PrefixExpression> getPrefixExpressions() {
	    return prefixExpressions;
	}

	public PrefixExpression getPrefixExpressionSecond() {
	    return (PrefixExpression) getExpressionFromList(prefixExpressions,
		    1);
	}

	public Expression getSimpleName() {
	    return getExpressionFromList(simpleNames, 0);
	}

	public Expression getSimpleNameSecond() {
	    return getExpressionFromList(simpleNames, 1);
	}

	/**
	 * Liefert das Attribut testCasesToMerge
	 * 
	 * @return Wert von testCasesToMerge
	 */
	public List<List<TestCase>> getTestCasesToMerge() {
	    return testCasesToMerge;
	}

	public void setNullLiteral(NullLiteral nullLiteral) {
	    this.expressionOrder.put(nullLiteral, filledCount++);
	    this.nullLiterals.add(nullLiteral);
	}

	public void setNumberLiteral(NumberLiteral numberLiteral) {
	    this.expressionOrder.put(numberLiteral, filledCount++);
	    this.numberLiterals.add(numberLiteral);
	}

	/**
	 * @param operator
	 */
	public void setOperator(Operator operator) {
	    this.operator = operator;
	}

	/**
	 * @param string
	 */
	private void setParam(String name) {
	    for (Param tmpParam : paramBaseList) {
		if (tmpParam.getName().equals(name)) {
		    this.params.add(tmpParam);
		    return;
		}
	    }
	}

	/**
	 * @param expression
	 */
	private void setParenthesizedExpression(
		ParenthesizedExpression expression) {
	    Expression mainExpression = expression.getExpression();
	    analyze(mainExpression);
	}

	/**
	 * @param expression
	 */
	public void setPostfixExpression(PostfixExpression expression) {
	    this.postfixExpressions.add(expression);
	    Expression operand = expression.getOperand();
	    expressionPostfixes.put(operand, expression.getOperator());
	    analyze(operand);
	}

	/**
	 * @param expression
	 */
	public void setPrefixExpression(PrefixExpression expression) {
	    this.prefixExpressions.add(expression);
	    Expression operand = expression.getOperand();
	    expressionPrefixes.put(operand, expression.getOperator());
	    analyze(operand);
	}

	public void setSimpleName(SimpleName simpleName) {
	    this.expressionOrder.put(simpleName, filledCount++);
	    this.simpleNames.add(simpleName);

	    if (simpleName != null) {
		setParam(simpleName.toString());
	    }

	}

    }

    private static final ArrayList<TestCase> EMPTY_LIST_TC = new ArrayList<TestCase>();

    private final ObjectFactory of = new ObjectFactory();

    private void analyzeBaseMethod(IMethod method, Method tmlMethod)
	    throws IllegalArgumentException, JavaModelException {
	CompilationUnit cu = JDTUtils
		.createASTRoot(method.getCompilationUnit());
	MethodDeclaration md = JDTUtils.createMethodDeclaration(cu, method);

	Block body = md.getBody();

	if (body != null) {
	    for (Object statement : body.statements()) {
		if (statement instanceof Statement) {
		    Statement st = (Statement) statement;
		    processIfStatements(st, tmlMethod);
		}
	    }
	}

    }

    private TestCase createTestCase() {
	TestCase tc = of.createTestCase();
	tc.setName("test");
	tc.setTestBase("");
	return tc;
    }

    /**
     * @param testCases
     * @param string
     * @param param
     * @param infixExpression
     */
    private List<TestCase> createTestcaseForCheck(Param param, String value) {
	List<TestCase> testCases = new ArrayList<TestCase>(2);

	TestCase tc = createTestCase();
	ParamAssignment pa = of.createParamAssignment();
	pa.setParamName(param.getName());
	pa.setAssignment(value);
	tc.getParamAssignments().add(pa);
	testCases.add(tc);

	String initValue = JDTUtils.createInitValue(param.getType(), true);
	if (initValue.equals(value)) {
	    initValue = GeneratorUtils.createRandomValue(param.getType(), true);
	}
	if (initValue.equals(value)) {
	    return testCases;
	}

	tc = createTestCase();
	pa = of.createParamAssignment();
	pa.setParamName(param.getName());
	pa.setAssignment(initValue);
	tc.getParamAssignments().add(pa);
	testCases.add(tc);

	return testCases;
    }

    /**
     * @param ec
     * @return
     */
    private Collection<? extends TestCase> createTestCasesForCompare(
	    ExpressionAnalyzer ec) {
	if (ec.getNumberLiteral() != null) {
	    String number = ec.getNumberLiteral().getToken();
	    if (ec.getParam() != null) {
		return createTestCasesForCompare(number, ec.getParam());
	    } else if (ec.getSimpleName() != null) {
		return createTestCasesForCompare(number, ec.getSimpleName());
	    }

	} else if (ec.getParam() != null) {
	    if (ec.getParamSecond() != null) {
		return createTestCasesForCompare(ec.getParam(),
			ec.getParamSecond());
	    } else if (ec.getParamSecond() != null) {
		return createTestCasesForCompare(ec.getParam(),
			ec.getSimpleName());
	    }
	}

	return EMPTY_LIST_TC;
    }

    /**
     * @param param
     * @param expression
     * @return
     */
    private Collection<? extends TestCase> createTestCasesForCompare(
	    Param param, Expression expression) {
	return EMPTY_LIST_TC;
    }

    /**
     * @param param
     * @param paramSecond
     * @return
     */
    private Collection<? extends TestCase> createTestCasesForCompare(
	    Param param, Param paramSecond) {
	List<TestCase> testCases = new ArrayList<TestCase>();

	List<String> comb = GeneratorUtils.createCombination(param.getType());
	if (comb.size() == 0) {
	    return testCases;
	}

	TestCase tc;
	ParamAssignment pa1, pa2;

	// param1
	pa1 = of.createParamAssignment();
	pa1.setParamName(param.getName());
	pa1.setAssignment(comb.get(0));

	for (String paramValue : comb) {
	    tc = createTestCase();
	    // param2
	    pa2 = of.createParamAssignment();
	    pa2.setParamName(paramSecond.getName());
	    pa2.setAssignment(paramValue);

	    // add param assignments
	    tc.getParamAssignments().add(pa1);
	    tc.getParamAssignments().add(pa2);

	    testCases.add(tc);
	}

	return testCases;
    }

    /**
     * @param number
     * @param simpleName
     * @return
     */
    private Collection<? extends TestCase> createTestCasesForCompare(
	    String number, Expression simpleName) {
	return EMPTY_LIST_TC;
    }

    /**
     * @param number
     * @param testCases
     * @param string
     * @param param
     * @param infixExpression
     */
    private List<TestCase> createTestCasesForCompare(String number, Param param) {
	List<TestCase> testCases = new ArrayList<TestCase>();

	List<String> comb = GeneratorUtils.createCombination(param.getType(),
		number);

	TestCase tc;
	ParamAssignment pa;
	for (String paramValue : comb) {
	    tc = createTestCase();
	    pa = of.createParamAssignment();
	    pa.setParamName(param.getName());
	    pa.setAssignment(paramValue);
	    tc.getParamAssignments().add(pa);
	    testCases.add(tc);
	}

	return testCases;
    }

    public void generateTestCases(GeneratorModel utmModel)
	    throws JavaModelException {
	for (IMethod method : utmModel.getMethodsToCreate()) {

	    Method tmlMethod = utmModel.getMethodMap().get(method);

	    // create default test-cases
	    TestCase testCase = of.createTestCase();

	    // analyze the base-method
	    analyzeBaseMethod(method, tmlMethod);

	    // add default test-case
	    if (tmlMethod.getTestCase().size() == 0) {
		testCase.setTestBase("");
		testCase.setName("default test");
		tmlMethod.getTestCase().add(testCase);
	    } else {
		int i = 1;
		for (TestCase tc : tmlMethod.getTestCase()) {
		    tc.setName(tc.getName() + " " + i++);
		}
	    }

	}
    }

    /**
     * @param numberLiteral
     */
    protected Number getNumber(NumberLiteral numberLiteral) {
	String sNumber = numberLiteral.getToken();
	Number number = 0;

	try {
	    number = Integer.parseInt(sNumber);

	} catch (NumberFormatException e) {
	    number = 0;
	}

	return number;
    }

    protected String getNumber(PrefixExpression prefixExpression) {
	String number = null;

	Expression operand = prefixExpression.getOperand();
	if (operand.getNodeType() == ASTNode.NUMBER_LITERAL) {
	    org.eclipse.jdt.core.dom.PrefixExpression.Operator operator = prefixExpression
		    .getOperator();

	    if (org.eclipse.jdt.core.dom.PrefixExpression.Operator.MINUS
		    .equals(operator)) {
		number = "-" + operand.toString();
	    } else if (org.eclipse.jdt.core.dom.PrefixExpression.Operator.PLUS
		    .equals(operator)
		    || org.eclipse.jdt.core.dom.PrefixExpression.Operator.DECREMENT
			    .equals(operator)
		    || org.eclipse.jdt.core.dom.PrefixExpression.Operator.INCREMENT
			    .equals(operator)) {
		number = operand.toString();
	    } else {
		number = "0";
	    }

	}

	return number;
    }

    /**
     * @param operator
     * @return
     */
    private boolean isChainOperator(Operator operator) {

	if (operator == Operator.AND || operator == Operator.CONDITIONAL_AND
		|| operator == Operator.OR
		|| operator == Operator.CONDITIONAL_OR
		|| operator == Operator.XOR) {
	    return true;
	}

	return false;
    }

    private boolean isParamVariable(List<Param> params, String variableName) {
	for (Param param : params) {
	    if (param.getName().equals(variableName)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * @param tcLeftOperand
     * @param tcRightOperand
     * @return
     */
    private List<TestCase> mergeTestCases(List<TestCase> tcList1,
	    List<TestCase> tcList2) {
	List<TestCase> mergedTcList = new ArrayList<TestCase>();

	if (tcList2.size() == 0) {
	    return tcList1;
	} else if (tcList1.size() == 0) {
	    return tcList2;
	}

	// merge tc-list2 to tc-list1
	for (TestCase tc1 : tcList1) {
	    for (TestCase tc2 : tcList2) {
		// avoid duplicates
		// param assignments
		for (ParamAssignment pa2 : tc2.getParamAssignments()) {
		    boolean duplicateFound = false;
		    for (ParamAssignment pa1 : tc1.getParamAssignments()) {
			if (pa1.getParamName().equals(pa2.getParamName())) {
			    duplicateFound = true;
			    break;
			}
		    }

		    if (!duplicateFound) {
			tc1.getParamAssignments().add(pa2);
		    }
		}

	    }
	}

	// merge tc-list1 to tc-list2
	for (TestCase tc2 : tcList2) {
	    for (TestCase tc1 : tcList1) {
		// avoid duplicates
		// param assignments
		for (ParamAssignment pa1 : tc1.getParamAssignments()) {
		    boolean duplicateFound = false;
		    for (ParamAssignment pa2 : tc2.getParamAssignments()) {
			if (pa2.getParamName().equals(pa1.getParamName())) {
			    duplicateFound = true;
			    break;
			}
		    }

		    if (!duplicateFound) {
			tc2.getParamAssignments().add(pa1);
		    }
		}
	    }
	}

	mergedTcList.addAll(tcList1);
	mergedTcList.addAll(tcList2);

	return mergedTcList;
    }

    /**
     * @param tcLeftOperand
     * @param tcRightOperand
     * @return
     */
    private List<TestCase> multiplyTestCases(List<TestCase> tcList1,
	    List<TestCase> tcList2) {
	List<TestCase> mergedTcList = new ArrayList<TestCase>();
	TestCase mergedTc;

	if (tcList2.size() == 0) {
	    return tcList1;
	} else if (tcList1.size() == 0) {
	    return tcList2;
	}

	for (TestCase tc1 : tcList1) {
	    for (TestCase tc2 : tcList2) {
		mergedTc = of.createTestCase();
		mergedTc.setTestBase(tc1.getTestBase());
		mergedTc.setName(tc1.getName());

		mergedTc.getPreconditions().addAll(tc1.getPreconditions());
		mergedTc.getPreconditions().addAll(tc2.getPreconditions());

		mergedTc.getParamAssignments()
			.addAll(tc1.getParamAssignments());
		mergedTc.getParamAssignments()
			.addAll(tc2.getParamAssignments());

		mergedTc.getAssertion().addAll(tc1.getAssertion());
		mergedTc.getAssertion().addAll(tc2.getAssertion());

		mergedTcList.add(mergedTc);
	    }
	}

	return mergedTcList;
    }

    private List<TestCase> processIfExpressions(Expression expression,
	    Method tmlMethod) {
	List<Param> tmlMethodParam = tmlMethod.getParam();

	List<TestCase> allTestCases = new ArrayList<TestCase>();

	if (expression.getNodeType() == ASTNode.INFIX_EXPRESSION) {
	    InfixExpression infixExpression = (InfixExpression) expression;
	    Expression leftOperand = infixExpression.getLeftOperand();
	    Expression rightOperand = infixExpression.getRightOperand();

	    Operator operator = infixExpression.getOperator();

	    List<TestCase> tcLeftOperand = processIfExpressions(leftOperand,
		    tmlMethod);
	    List<TestCase> tcRightOperand = processIfExpressions(rightOperand,
		    tmlMethod);

	    // analyze expressions

	    ExpressionAnalyzer ec = new ExpressionAnalyzer(tmlMethod);
	    ec.analyze(leftOperand);
	    ec.analyze(rightOperand);
	    ec.setOperator(operator);

	    // &&, &, ||, |, XOR
	    if (isChainOperator(operator)) {
		allTestCases.addAll(mergeTestCases(tcLeftOperand,
			tcRightOperand));
	    }
	    // ==, !=
	    else if (operator == Operator.NOT_EQUALS
		    || operator == Operator.EQUALS) {
		// create test-cases
		if (ec.getParam() != null) {
		    if (ec.getNullLiteral() != null) {
			allTestCases.addAll(createTestcaseForCheck(
				ec.getParam(), "null"));
		    } else if (ec.getNumberLiteral() != null) {
			allTestCases.addAll(createTestcaseForCheck(
				ec.getParam(),
				"" + getNumber(ec.getNumberLiteral())));
		    }
		}

	    }
	    // <, <=, >, >=
	    else if (operator == Operator.LESS
		    || operator == Operator.LESS_EQUALS
		    || operator == Operator.GREATER
		    || operator == Operator.GREATER_EQUALS) {
		allTestCases.addAll(createTestCasesForCompare(ec));
	    } else {
		allTestCases.addAll(tcLeftOperand);
		allTestCases.addAll(tcRightOperand);
	    }

	}

	else if (expression.getNodeType() == ASTNode.METHOD_INVOCATION) {
	    MethodInvocation mi = (MethodInvocation) expression;
	    String methodName = mi.getName().toString();
	    Expression methodExpression = mi.getExpression();
	    List arguments = mi.arguments();

	    if (methodName.equals("equals")) {
		if (methodExpression.getNodeType() == ASTNode.STRING_LITERAL) {
		    for (Object argumentO : arguments) {
			if (argumentO instanceof ASTNode) {
			    ASTNode argumentNode = (ASTNode) argumentO;

			    if (argumentNode.getNodeType() == ASTNode.SIMPLE_NAME) {
				if (isParamVariable(tmlMethodParam,
					argumentNode.toString())) {
				    TestCase tc = createTestCase();

				    ParamAssignment pa = of
					    .createParamAssignment();
				    pa.setParamName(argumentNode.toString());
				    pa.setAssignment(methodExpression
					    .toString());
				    tc.getParamAssignments().add(pa);

				    allTestCases.add(tc);
				}
			    }

			}
		    }
		} else if (methodExpression.getNodeType() == ASTNode.SIMPLE_NAME) {
		    if (isParamVariable(tmlMethodParam,
			    methodExpression.toString())) {
			for (Object argumentO : arguments) {
			    if (argumentO instanceof ASTNode) {
				ASTNode argumentNode = (ASTNode) argumentO;
				if (argumentNode.getNodeType() == ASTNode.STRING_LITERAL) {
				    TestCase tc = createTestCase();

				    ParamAssignment pa = of
					    .createParamAssignment();
				    pa.setParamName(methodExpression.toString());
				    pa.setAssignment(argumentNode.toString());

				    tc.getParamAssignments().add(pa);

				    allTestCases.add(tc);
				}
			    }
			}
		    }
		}
	    }

	}

	else if (expression.getNodeType() == ASTNode.PARENTHESIZED_EXPRESSION) {
	    Expression mainExpression = ((ParenthesizedExpression) expression)
		    .getExpression();
	    return processIfExpressions(mainExpression, tmlMethod);
	}

	return allTestCases;

    }

    private void processIfStatements(Statement st, Method tmlMethod) {
	List<TestCase> testCases = tmlMethod.getTestCase();

	if (st != null && st.getNodeType() == ASTNode.IF_STATEMENT) {

	    IfStatement ifSt = (IfStatement) st;

	    Expression expression = ifSt.getExpression();
	    testCases.addAll(processIfExpressions(expression, tmlMethod));

	    // add assertions for result variable
	    for (TestCase tc : testCases) {
		if (tc.getAssertion().size() == 0
			&& tmlMethod.getResult() != null) {
		    Assertion assertion = of.createAssertion();
		    assertion.setType(AssertionType.EQUALS);
		    assertion.setBase("{result}");
		    assertion.setValue("");
		    tc.getAssertion().add(assertion);
		}
	    }

	    // process other statements
	    processIfStatements(ifSt.getThenStatement(), tmlMethod);
	    processIfStatements(ifSt.getElseStatement(), tmlMethod);
	}
    }
}
