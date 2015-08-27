package xdi2.core.features.linkcontracts.instantiation;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CompoundCopyStrategy;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceEscapedVariablesCopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceXDIAddressCopyStrategy;

public class LinkContractInstantiation {

	private static final Logger log = LoggerFactory.getLogger(LinkContractInstantiation.class);

	private LinkContractTemplate linkContractTemplate;
	private XDIAddress authorizingAuthority;
	private XDIAddress requestingAuthority;
	private Map<XDIArc, XDIAddress> variableValues;

	public LinkContractInstantiation(LinkContractTemplate linkContractTemplate, XDIAddress authorizingAuthority, XDIAddress requestingAuthority, Map<XDIArc, XDIAddress> variableValues) {

		this.linkContractTemplate = linkContractTemplate;
		this.authorizingAuthority = authorizingAuthority;
		this.requestingAuthority = requestingAuthority;
		this.variableValues = variableValues;
	}

	public LinkContractInstantiation(LinkContractTemplate linkContractTemplate) {

		this(linkContractTemplate, null, null, null);
	}

	public LinkContractInstantiation() {

		this(null, null, null, null);
	}

	public LinkContract execute(boolean singleton, boolean create) {

		XDIAddress templateAuthorityAndId = this.getLinkContractTemplate().getTemplateAuthorityAndId();

		// create generic link contract

		if (this.getAuthorizingAuthority() == null) throw new NullPointerException("Cannot instantiate link contract without known authorizing authority.");
		if (this.getRequestingAuthority() == null) throw new NullPointerException("Cannot instantiate link contract without known requesting authority.");

		Graph linkContractGraph = MemoryGraphFactory.getInstance().openGraph();

		LinkContract linkContract = GenericLinkContract.findGenericLinkContract(linkContractGraph, this.getAuthorizingAuthority(), this.getRequestingAuthority(), templateAuthorityAndId, singleton, create);
		if (linkContract == null) return null;
		if (linkContract != null && ! create) return linkContract;

		if (log.isDebugEnabled()) log.debug("Instantiated link contract " + linkContract + " from link contract template " + this.getLinkContractTemplate());

		// set up variable values

		Map<XDIArc, XDIAddress> allVariableValues = new HashMap<XDIArc, XDIAddress> ();
		if (this.getVariableValues() != null) allVariableValues.putAll(this.getVariableValues());
		allVariableValues.put(XDILinkContractConstants.XDI_ARC_V_FROM, this.getRequestingAuthority());
		allVariableValues.put(XDILinkContractConstants.XDI_ARC_V_TO, this.getAuthorizingAuthority());
		allVariableValues.put(XDILinkContractConstants.XDI_ARC_V_FROM_ROOT, XDIAddress.fromComponent(XdiPeerRoot.createPeerRootXDIArc(this.getRequestingAuthority())));
		allVariableValues.put(XDILinkContractConstants.XDI_ARC_V_TO_ROOT, XDIAddress.fromComponent(XdiPeerRoot.createPeerRootXDIArc(this.getAuthorizingAuthority())));

		if (log.isDebugEnabled()) log.debug("Variable values: " + allVariableValues);

		// TODO: make sure all variables in the link contract template have assigned values

		// instantiate

		CopyStrategy copyStrategy = new CompoundCopyStrategy(
				new ReplaceXDIAddressCopyStrategy(allVariableValues),
				new ReplaceEscapedVariablesCopyStrategy());
		CopyUtil.copyContextNodeContents(this.getLinkContractTemplate().getContextNode(), linkContract.getContextNode(), copyStrategy);

		// add push permission inverse relations

		linkContract.setupPushPermissionInverseRelations();

		// add type statement

		Dictionary.setContextNodeType(linkContract.getContextNode(), this.getLinkContractTemplate().getContextNode().getXDIAddress());

		// done

		return linkContract;
	}

	/*
	 * Getters and setters
	 */

	public LinkContractTemplate getLinkContractTemplate() {

		return this.linkContractTemplate;
	}

	public void setLinkContractTemplate(LinkContractTemplate linkContractTemplate) {

		this.linkContractTemplate = linkContractTemplate;
	}

	public XDIAddress getAuthorizingAuthority() {

		return this.authorizingAuthority;
	}

	public void setAuthorizingAuthority(XDIAddress authorizingAuthority) {

		this.authorizingAuthority = authorizingAuthority;
	}

	public XDIAddress getRequestingAuthority() {

		return this.requestingAuthority;
	}

	public void setRequestingAuthority(XDIAddress requestingAuthority) {

		this.requestingAuthority = requestingAuthority;
	}

	public Map<XDIArc, XDIAddress> getVariableValues() {

		return this.variableValues;
	}

	public void setVariableValues(Map<XDIArc, XDIAddress> variableValues) {

		this.variableValues = variableValues;
	}
}
