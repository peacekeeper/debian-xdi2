package xdi2.core.features.linkcontracts.condition;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;

/**
 * An XDI $greater condition, represented as a statement.
 * 
 * @author markus
 */
public class GreaterCondition extends Condition {

	private static final long serialVersionUID = 2302071980940540935L;

	protected GreaterCondition(XDIStatement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $greater condition.
	 * @param statement The statement to check.
	 * @return True if the statement is a valid XDI $greater condition.
	 */
	public static boolean isValid(XDIStatement statement) {

		if (! statement.isRelationStatement()) return false;

		if (! XDIPolicyConstants.XDI_ADD_GREATER.equals(statement.getRelationAddress())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $greater condition bound to a given statement.
	 * @param statement The statement that is an XDI $greater condition.
	 * @return The XDI $greater condition.
	 */
	public static GreaterCondition fromStatement(XDIStatement statement) {

		if (! isValid(statement)) return null;

		return new GreaterCondition(statement);
	}

	public static GreaterCondition fromSubjectAndObject(XDIAddress subject, XDIAddress object) {

		return fromStatement(XDIStatement.fromRelationComponents(subject, XDIPolicyConstants.XDI_ADD_GREATER, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		ContextNode subject = policyEvaluationContext.getContextNode(this.getStatementXri().getSubject());
		ContextNode object = policyEvaluationContext.getContextNode((XDIAddress) this.getStatementXri().getObject());

		if (subject == null || object == null) return Boolean.FALSE;

		if (subject.containsLiteral()) {

			if (! object.containsLiteral()) return Boolean.FALSE;

			Object subjectLiteralData = subject.getLiteral().getLiteralData();
			Object objectLiteralData = object.getLiteral().getLiteralData();

			return Boolean.valueOf(AbstractLiteral.LITERALDATACOMPARATOR.compare(subjectLiteralData, objectLiteralData) > 0);
		}

		return Boolean.FALSE;
	}
}
