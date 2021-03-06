package xdi2.messaging.operations;

import xdi2.core.Relation;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.MessageBase;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * A $connect XDI operation, represented as a relation.
 * 
 * @author markus
 */
public class ConnectOperation extends Operation {

	private static final long serialVersionUID = -7845242233143721970L;

	protected ConnectOperation(MessageBase<?> messageBase, Relation relation) {

		super(messageBase, relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an relation is a valid XDI $connect operation.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $connect operation.
	 */
	public static boolean isValid(Relation relation) {

		if (XDIAddressUtil.startsWithXDIAddress(relation.getXDIAddress(), XDIMessagingConstants.XDI_ADD_CONNECT) == null) return false;
		if (! XDIMessagingConstants.XDI_ARC_DO.equals(relation.getContextNode().getXDIArc())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $connect operation bound to a given relation.
	 * @param relation The relation that is an XDI $connect operation.
	 * @return The XDI $connect operation.
	 */
	public static ConnectOperation fromMessageBaseAndRelation(MessageBase<?> messageBase, Relation relation) {

		if (! isValid(relation)) return null;

		return new ConnectOperation(messageBase, relation);
	}
}
