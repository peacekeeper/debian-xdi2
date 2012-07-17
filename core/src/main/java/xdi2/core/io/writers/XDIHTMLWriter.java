package xdi2.core.io.writers;

import java.util.Properties;

import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;

public class XDIHTMLWriter extends XDIStatementsWriter {

	private static final long serialVersionUID = 6165681888411006041L;

	public static final String FORMAT_NAME = "HTML";
	public static final String FILE_EXTENSION = "html";
	public static final MimeType[] MIME_TYPES = new MimeType[] { new MimeType("text/html") };

	public XDIHTMLWriter(Properties parameters) {

		super(parameters);
	}
	
	@Override
	protected void init() {
		
		this.parameters.setProperty(XDIWriterRegistry.PARAMETER_CONTEXTS, "1");
		this.parameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "1");
		this.parameters.setProperty(XDIWriterRegistry.PARAMETER_HTML, "1");
		
		super.init();
	}
}
