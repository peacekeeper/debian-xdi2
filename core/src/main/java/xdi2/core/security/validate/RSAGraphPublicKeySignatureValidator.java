package xdi2.core.security.validate;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphAware;
import xdi2.core.util.GraphUtil;

/**
 * This is an RSAPublicKeySignatureValidater that validate an XDI RSASignature by
 * obtaining public keys from a "public key graph".
 */
public class RSAGraphPublicKeySignatureValidator extends RSAPublicKeySignatureValidator implements GraphAware {

	private static Logger log = LoggerFactory.getLogger(RSAGraphPublicKeySignatureValidator.class.getName());

	private Graph publicKeyGraph;

	public RSAGraphPublicKeySignatureValidator(Graph publicKeyGraph) {

		super();

		this.publicKeyGraph = publicKeyGraph;
	}

	public RSAGraphPublicKeySignatureValidator() {

		super();

		this.publicKeyGraph = null;
	}

	@Override
	public PublicKey getPublicKey(XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// signer address

		if (signerXDIAddress == null) {

			signerXDIAddress = GraphUtil.getOwnerXDIAddress(this.getPublicKeyGraph());
		}

		// signer entity

		XdiEntity signerXdiEntity = XdiCommonRoot.findCommonRoot(this.getPublicKeyGraph()).getXdiEntity(signerXDIAddress, false);
		signerXdiEntity = signerXdiEntity == null ? null : signerXdiEntity.dereference();

		if (log.isDebugEnabled()) log.debug("Signer entity: " + signerXdiEntity);

		if (signerXdiEntity == null) return null;

		// find public key

		PublicKey publicKey = Keys.getSignaturePublicKey(signerXdiEntity);

		// done

		return publicKey;
	}

	/*
	 * GraphAware
	 */

	@Override
	public void setGraph(Graph graph) {

		if (this.getPublicKeyGraph() == null) this.setPublicKeyGraph(graph);
	}

	/*
	 * Getters and setters
	 */

	public Graph getPublicKeyGraph() {

		return this.publicKeyGraph;
	}

	public void setPublicKeyGraph(Graph publicKeyGraph) {

		this.publicKeyGraph = publicKeyGraph;
	}
}
