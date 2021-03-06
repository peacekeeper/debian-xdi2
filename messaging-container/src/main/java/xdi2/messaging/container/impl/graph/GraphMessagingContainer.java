package xdi2.messaging.container.impl.graph;

import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.container.AddressHandler;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.Prototype;
import xdi2.messaging.container.StatementHandler;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;
import xdi2.messaging.container.impl.AbstractMessagingContainer;

/**
 * An XDI messaging container backed by some implementation of the Graph interface.
 * 
 * @author markus
 */
public class GraphMessagingContainer extends AbstractMessagingContainer implements Prototype<GraphMessagingContainer> {

	private static final Logger log = LoggerFactory.getLogger(GraphMessagingContainer.class);

	private Graph graph;
	private GraphContextHandler graphContextHandler;

	public GraphMessagingContainer(Graph graph) {

		super();

		this.graph = graph;
		this.graphContextHandler = new GraphContextHandler(graph);
	}

	public GraphMessagingContainer() {

		super();

		this.graph = null;
		this.graphContextHandler = null;
	}

	@Override
	public void init() throws Exception {

		super.init();
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();

		this.graph.close();
	}

	@Override
	public XDIArc getOwnerPeerRootXDIArc() {

		return GraphUtil.getOwnerPeerRootXDIArc(this.getGraph());
	}

	@Override
	public XDIAddress getOwnerXDIAddress() {

		return GraphUtil.getOwnerXDIAddress(this.getGraph());
	}

	@Override
	public void setOwnerPeerRootXDIArc(XDIArc ownerPeerRootXDIArc) {

		GraphUtil.setOwnerPeerRootXDIArc(this.getGraph(), ownerPeerRootXDIArc);
	}

	@Override
	public void setOwnerXDIAddress(XDIAddress ownerXDIAddress) {

		GraphUtil.setOwnerXDIAddress(this.getGraph(), ownerXDIAddress);
	}

	@Override
	public boolean ownsPeerRootXDIArc(XDIArc peerRootXDIArc) {

		return GraphUtil.ownsPeerRootXDIArc(this.getGraph(), peerRootXDIArc);
	}

	@Override
	public boolean before(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		this.graph.beginTransaction();

		return super.before(messageEnvelope, executionContext, executionResult);
	}

	@Override
	public void after(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		this.graph.commitTransaction();

		super.after(messageEnvelope, executionContext, executionResult);
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult, Xdi2MessagingException ex) throws Xdi2MessagingException {

		super.exception(messageEnvelope, executionContext, executionResult, ex);

		this.graph.rollbackTransaction();
	}

	@Override
	public AddressHandler getAddressHandler(XDIAddress XDIaddress) throws Xdi2MessagingException {

		return this.graphContextHandler;
	}

	@Override
	public StatementHandler getStatementHandler(XDIStatement statement) throws Xdi2MessagingException {

		return this.graphContextHandler;
	}

	/*
	 * Prototype
	 */

	@Override
	public GraphMessagingContainer instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new messaging container

		MessagingContainer messagingContainer = new GraphMessagingContainer();

		// instantiate new graph

		Graph graph;

		try {

			String identifier = XdiPeerRoot.createPeerRootXDIArc(prototypingContext.getOwnerXDIAddress()).toString();
			graph = this.getGraph().getGraphFactory().openGraph(identifier);

			if (log.isDebugEnabled()) log.debug("Opened graph " + graph.getClass().getCanonicalName() + " for " + identifier);
		} catch (IOException ex) {

			throw new Xdi2MessagingException("Cannot open graph: " + ex.getMessage(), ex, null);
		}

		((GraphMessagingContainer) messagingContainer).setGraph(graph);

		// done

		return (GraphMessagingContainer) messagingContainer;
	}

	/*
	 * Convenience methods
	 */

	public static ContextNode getContextNode(Graph graph, XDIAddress XDIaddress) {

		ContextNode contextNode = graph.getRootContextNode();
		
		for (XDIArc XDIarc : XDIaddress.getXDIArcs()) {
			
			contextNode = contextNode.getContextNode(XDIarc);
			if (contextNode == null) return null;

			while (true) {

				ContextNode referenceContextNode = Equivalence.getReferenceContextNode(contextNode);
				if (referenceContextNode != null) { contextNode = referenceContextNode; continue; }

				ContextNode replacementContextNode = Equivalence.getReplacementContextNode(contextNode);
				if (replacementContextNode != null) { contextNode = replacementContextNode; continue; }

				break;
			}
		}

		return contextNode;
	}

	@SuppressWarnings("unchecked")
	public static <T extends XdiContext<T>> XdiContext<T> getXdiContext(Graph graph, XDIAddress XDIaddress) {

		ContextNode contextNode = getContextNode(graph, XDIaddress);
		if (contextNode == null) return null;

		return (XdiContext<T>) XdiAbstractContext.fromContextNode(contextNode);
	}

	public static class MappingXDIAddressContextNodeIterator extends MappingIterator<XDIAddress, ContextNode> {

		private Graph graph;
		
		public MappingXDIAddressContextNodeIterator(Graph graph, Iterator<XDIAddress> XDIaddresses) {

			super(XDIaddresses);
			
			this.graph = graph;
		}

		@Override
		public ContextNode map(XDIAddress XDIaddress) {

			return GraphMessagingContainer.getContextNode(this.graph, XDIaddress);
		}
	}

	public static class MappingXDIAddressXdiContextIterator<T extends XdiContext<T>> extends MappingIterator<XDIAddress, XdiContext<T>> {

		private Graph graph;
		
		public MappingXDIAddressXdiContextIterator(Graph graph, Iterator<XDIAddress> XDIaddresses) {

			super(XDIaddresses);
			
			this.graph = graph;
		}

		@Override
		public XdiContext<T> map(XDIAddress XDIaddress) {

			return GraphMessagingContainer.getXdiContext(this.graph, XDIaddress);
		}
	}

	/*
	 * Getters and setters
	 */

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
		this.graphContextHandler = new GraphContextHandler(graph);
	}
}
