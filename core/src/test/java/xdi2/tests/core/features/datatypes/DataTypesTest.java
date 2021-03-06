package xdi2.tests.core.features.datatypes;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.features.datatypes.DataTypes;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;

public class DataTypesTest extends TestCase {

	public void testDatatypes() throws Exception {

		StringBuilder buffer = new StringBuilder();

		buffer.append("=markus<#age>/&/\"33\"\n");
		buffer.append("=markus<#age>/$is#/$xsd$int\n");
		buffer.append("=markus<#age>/$is#/$json$number\n");
		String xdiString = buffer.toString();

		Graph graph = (new MemoryGraphFactory()).parseGraph(xdiString, "XDI DISPLAY", null);

		DataTypes.setDataType(graph.getDeepContextNode(XDIAddress.create("=markus<#age>")), XDIAddress.create("$mime$image$png"));

		List<XDIAddress> dataTypes = DataTypes.getDataTypes(graph.getDeepContextNode(XDIAddress.create("=markus<#age>")));

		assertNotNull(dataTypes);

		for (XDIAddress dataType : dataTypes) {

			if (dataType.toString().contains("json")) {

				assertEquals("number", DataTypes.jsonTypeFromDataTypeXDIAddress(dataType).toString());
			} else if (dataType.toString().contains("xsd")) {

				assertEquals("xsd:int", DataTypes.xsdTypeFromDataTypeXDIAddress(dataType).toString());
			} else if (dataType.toString().contains("mime")) {

				assertEquals("image/png", DataTypes.mimeTypeFromDataTypeXDIAddress(dataType).toString());
			}
		}
		
		graph.close();
	}

	@Test
	public void testDuplicateDatatypes() throws Exception {

		StringBuilder buffer = new StringBuilder();

		buffer.append("=markus<#age>/&/\"33\"\n");
		buffer.append("=markus<#age>/$is#/+$xsd$int\n");
		buffer.append("=markus<#age>/$is#/+$json$number\n");
		String xdiString = buffer.toString();

		try {

			Graph graph = (new MemoryGraphFactory()).parseGraph(xdiString);
			DataTypes.setDataType(graph.getDeepContextNode(XDIAddress.create("=markus<#age>")), XDIAddress.create("+$json$number"));

			fail();
			
			graph.close();
		} catch (Exception ex) { }
	}
}
