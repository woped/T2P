package de.dhbw.text2process.models.bpmn.epc;

import java.util.ArrayList;

import de.dhbw.text2process.models.bpmn.SequenceFlow;
import de.dhbw.text2process.models.bpmn.nodes.FlowObject;


public class Connector extends FlowObject {

	private ArrayList<SequenceFlow> incoming = new ArrayList<SequenceFlow>();
	private ArrayList<SequenceFlow> outgoing = new ArrayList<SequenceFlow>();

	public Connector() {
		super();
	}

	@Override
	public void setIncoming(SequenceFlow flow) {
		incoming.add(flow);
	}

	@Override
	public void setOutgoing(SequenceFlow flow) {
		outgoing.add(flow);
	}

	public ArrayList<SequenceFlow> getIncoming() {
		return incoming;
	}

	public ArrayList<SequenceFlow> getOutgoing() {
		return outgoing;
	}

	public void overrideIncoming(ArrayList<SequenceFlow> cons) {
		incoming = cons;
	}

	public void overrideOutgoing(ArrayList<SequenceFlow> cons) {
		outgoing = cons;
	}
}
