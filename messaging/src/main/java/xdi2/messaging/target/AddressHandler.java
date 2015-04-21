package xdi2.messaging.target;

import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;

/**
 * An AddressHandler can execute an XDI operation against an address.
 * 
 * @author markus
 */
public interface AddressHandler {

	/**
	 * Executes an XDI operation on an address.
	 * @param targetXDIAddress The target address.
	 * @param operation The operation that is being executed.
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 */
	public void executeOnAddress(XDIAddress targetXDIAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
