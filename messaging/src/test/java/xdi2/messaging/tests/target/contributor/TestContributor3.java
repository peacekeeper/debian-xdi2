package xdi2.messaging.tests.target.contributor;

import xdi2.core.util.StatementUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorXri;

@ContributorXri(addresses={"(+test)"})
public class TestContributor3 extends AbstractContributor {

	@Override
	public boolean getContext(
			XDI3Segment[] contributorXris,
			XDI3Segment contributorsXri,
			XDI3Segment contextNodeXri,
			GetOperation operation,
			MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		messageResult.getGraph().createStatement(StatementUtil.fromRelationComponents(
				XDI3Segment.create("" + contributorsXri + "=markus"),
				XDI3Segment.create("" + "+friend"),
				XDI3Segment.create("" + contributorsXri + "=animesh")));

		return false;
	}
}