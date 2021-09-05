package de.dhbw.text2process.helper.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import de.dhbw.text2process.enums.BpmnXmlElements;
import de.dhbw.text2process.helper.exporter.meta.Exporter;
import de.dhbw.text2process.models.bpmn.ComplexGateway;
import de.dhbw.text2process.models.bpmn.EventBasedGateway;
import de.dhbw.text2process.models.bpmn.ExclusiveGateway;
import de.dhbw.text2process.models.bpmn.InclusiveGateway;
import de.dhbw.text2process.models.bpmn.Lane;
import de.dhbw.text2process.models.bpmn.ParallelGateway;
import de.dhbw.text2process.models.bpmn.Pool;
import de.dhbw.text2process.models.bpmn.SequenceFlow;
import de.dhbw.text2process.models.bpmn.Task;
import de.dhbw.text2process.models.meta.BPMNModel;

public class BPMNExporter extends Exporter {

	private String start = BpmnXmlElements.BPMN_HEADER.getElementTag();

	private String end = "\n</" + BpmnXmlElements.BPMN_PROCESS.getElementTag() + ">" + "\n</"
			+ BpmnXmlElements.BPMN_DEFINITIONS.getElementTag() + ">";

	private StringBuffer bpmn = new StringBuffer(start);

	private BPMNModel model = null;

	private File outputFile;

	public BPMNExporter(BPMNModel bpmnm) {
		model = bpmnm;
		init(model);
	}

	private void init(BPMNModel bpmnm) {
		String id = bpmnm.getId();
		String name = bpmnm.getProcessName();
		bpmn.append("\n\n<" + BpmnXmlElements.BPMN_PROCESS.getElementTag()
				+ " processType=\"Private\" isExecutable=\"true\" id=\"" + id + "\"" + " name=\"" + name + "\">");
	}

	public void addFlowObjects(ArrayList<Task> tasks) {
		for (Task t : tasks) {
			String id = t.getId();
			String name = t.getName();
			bpmn.append("\n<" + BpmnXmlElements.BPMN_TASK.getElementTag() + " id=\"" + id + "\" name=\""
					+ name + "\"/>");
		}
	}

	public void addFlows(ArrayList<SequenceFlow> flows) {
		for (SequenceFlow f : flows) {
			if (f.getSource() != null && f.getTarget() != null) {
				String id = f.getId();
				String source = f.getSource().getId();
				String target = f.getTarget().getId();
				bpmn.append("\n<" + BpmnXmlElements.BPMN_SEQUENCEFLOW.getElementTag() + " id=\"" + id
						+ "\" sourceRef=\"" + source + "\" targetRef=\"" + target + "\"/>");
			}
		}
	}

	public void export(File outputFile) {
		if (outputFile == null) {
			this.outputFile = new File("BPMN.bpmn");
		} else {
			this.outputFile = outputFile;
		}
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(this.outputFile));
			String outText = bpmn.toString();
			out.write(outText);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addLanes(ArrayList<Lane> lanes) {
		bpmn.append("\n<" + BpmnXmlElements.BPMN_LANESET.getElementTag() + " id=\"4711\">");
		for (Lane l : lanes) {
			String id = l.getId();
			String name = l.getName();
			bpmn.append("\n<" + BpmnXmlElements.BPMN_LANE.getElementTag() + " name=\"" + name + "\" id=\"" + id
					+ "\"/>");
		}
		bpmn.append("\n</" + BpmnXmlElements.BPMN_LANESET.getElementTag() + ">");
	}

	public void addPools(ArrayList<Pool> pools) {
		for (Pool p : pools) {
			String id = p.getId();
			String name = p.getName();
			bpmn.append("\n<" + BpmnXmlElements.BPMN_COLLABORATION.getElementTag() + " id=\"" + id + "\">" + "\n<"
					+ BpmnXmlElements.BPMN_PARTICIPANT.getElementTag() + " name=\"" + name + "\" processRef=\""
					+ model.getId() + "\" id=\"" + id + "\"/>" + "\n</"
					+ BpmnXmlElements.BPMN_COLLABORATION.getElementTag() + ">");
		}
	}

	public void addGateways(ArrayList<ComplexGateway> com, ArrayList<EventBasedGateway> eBas,
			ArrayList<ExclusiveGateway> ex, ArrayList<InclusiveGateway> inc, ArrayList<ParallelGateway> par) {
		for (ComplexGateway g : com) {
			String id = g.getId();
			bpmn.append("\n<" + BpmnXmlElements.BPMN_COMPLEXGATEWAY.getElementTag() + " id=\"" + id + "\"/>");
		}
		for (EventBasedGateway g : eBas) {
			String id = g.getId();
			bpmn.append("\n<" + BpmnXmlElements.BPMN_EVENTBASEDGATEWAY.getElementTag() + " id=\"" + id + "\"/>");
		}
		for (ExclusiveGateway g : ex) {
			String id = g.getId();
			bpmn.append("\n<" + BpmnXmlElements.BPMN_EXCLUSIVEGATEWAY.getElementTag() + " id=\"" + id + "\"/>");
		}
		for (InclusiveGateway g : inc) {
			String id = g.getId();
			bpmn.append("\n<" + BpmnXmlElements.BPMN_INCLUSIVEGATEWAY.getElementTag() + " id=\"" + id + "\"/>");
		}
		for (ParallelGateway g : par) {
			String id = g.getId();
			bpmn.append("\n<" + BpmnXmlElements.BPMN_PARALLELGATEWAY.getElementTag() + " id=\"" + id + "\"/>");
		}
	}

	public void end() {
		bpmn.append(end);
	}

}
