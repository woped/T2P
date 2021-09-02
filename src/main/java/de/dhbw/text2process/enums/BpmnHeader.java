package de.dhbw.text2process.enums;

public enum BpmnHeader {

	BPMN_2_0("<definitions id=\\\"Definition\\\"\" + \r\n"
			+ "            \"\\ntargetNamespace=\\\"http://www.example.org/MinimalExample\\\"\" +\r\n"
			+ "            \"\\ntypeLanguage=\\\"http://www.java.com/javaTypes\\\"\" + \r\n"
			+ "            \"\\nexpressionLanguage=\\\"http://www.mvel.org/2.0\\\"\" + \r\n"
			+ "            \"\\nxmlns=\\\"http://www.omg.org/spec/BPMN/20100524/MODEL\\\"\" + \r\n"
			+ "            \"\\nxmlns:xs=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"\" + \r\n"
			+ "            \"\\nxs:schemaLocation=\\\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\\\"\" +\r\n"
			+ "            \"\\nxmlns:bpmndi=\\\"http://www.omg.org/spec/BPMN/20100524/DI\\\"\" +\r\n"
			+ "            \"\\nxmlns:dc=\\\"http://www.omg.org/spec/DD/20100524/DC\\\"\" +\r\n"
			+ "            \"\\nxmlns:di=\\\"http://www.omg.org/spec/DD/20100524/DI\\\"\" +\r\n"
			+ "            \"\\nxmlns:tns=\\\"http://www.jboss.org/drools\\\">\""),
	
	CAMUNDA_7_15_0("<?xml version=\"1.0\" "
			+ "encoding=\"UTF-8\"?>\r\n"
			+ "<bpmn:definitions "
			+ "xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" "
			+ "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" "
			+ "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" "
			+ "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" "
			+ "xmlns:modeler=\"http://camunda.org/schema/modeler/1.0\" "
			+ "id=\"Definitions_15n8ggd\" "
			+ "targetNamespace=\"http://bpmn.io/schema/bpmn\" "
			+ "exporter=\"Camunda Modeler\" "
			+ "exporterVersion=\"4.9.0\" "
			+ "modeler:executionPlatform=\"Camunda Platform\" "
			+ "modeler:executionPlatformVersion=\"7.15.0\">");
	
	String header;

	private BpmnHeader(String header) {
		this.header = header;
	}

	public String getHeader() {
		return header;
	}
		
}
