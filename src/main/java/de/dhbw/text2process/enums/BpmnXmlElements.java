package de.dhbw.text2process.enums;

public enum BpmnXmlElements {

	BPMN_HEADER("<definitions id=\"Definition\"\r\n"
			+ "\ttargetNamespace=\"http://www.example.org/MinimalExample\"\r\n"
			+ "\ttypeLanguage=\"http://www.java.com/javaTypes\"\r\n"
			+ "\texpressionLanguage=\"http://www.mvel.org/2.0\"\r\n"
			+ "\txmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\r\n"
			+ "\txmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
			+ "\txs:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\"\r\n"
			+ "\txmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\r\n"
			+ "\txmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\r\n"
			+ "\txmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\r\n"
			+ "\txmlns:tns=\"http://www.jboss.org/drools\">"),
	BPMN_PROCESS("process"), BPMN_DEFINITIONS("definitions"), BPMN_TASK("task"),
	BPMN_COMPLEXGATEWAY("complexGateway"), BPMN_EXCLUSIVEGATEWAY("exclusiveGateway"),
	BPMN_EVENTBASEDGATEWAY("eventBasedGateway"), BPMN_INCLUSIVEGATEWAY("inclusiveGateway"),
	BPMN_PARALLELGATEWAY("parallelGateway"), BPMN_SEQUENCEFLOW("sequenceFlow"),
	BPMN_COLLABORATION("collaboration"), BPMN_PARTICIPANT("participant"), BPMN_LANE("lane"),
	BPMN_LANESET("laneSet"),
	CAMUNDA_HEADER("<?xml version=\"1.0\" " + "encoding=\"UTF-8\\\"?>\r\n" + "<bpmn:definitions \r\n"
			+ "xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" \r\n"
			+ "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" \r\n"
			+ "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" \r\n"
			+ "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" \r\n"
			+ "xmlns:modeler=\"http://camunda.org/schema/modeler/1.0\" \r\n" + "id=\"Definitions_15n8ggd\" \r\n"
			+ "targetNamespace=\"http://bpmn.io/schema/bpmn\" \r\n" + "exporter=\"Camunda Modeler\" \r\n"
			+ "exporterVersion=\"4.9.0\" \r\n" + "modeler:executionPlatform=\"Camunda Platform\" \r\n"
			+ "modeler:executionPlatformVersion=\"7.15.0\">"),
	CAMUNDA_PROCESS("bpmn:process"), CAMUNDA_TASK("bpmn:task"),
	CAMUNDA_EXCLUSIVEGATEWAY("bpmn:exclusiveGateway"), CAMUNDA_SEQUENCEFLOW("bpmn:sequenceFlow"),
	CAMUNDA_DIAGRAM("bpmndi:BPMNDiagram"), CAMUNDA_PLANE("bpmndi:BPMNPlane"),
	CAMUNDA_EDGE("bpmndi:BPMNEdge"), CAMUNDA_SHAPE("bpmndi:BPMNShape");

	String elementTag;

	private BpmnXmlElements(String elementTag) {
		this.elementTag = elementTag;
	}

	public String getElementTag() {
		return elementTag;
	}

}
