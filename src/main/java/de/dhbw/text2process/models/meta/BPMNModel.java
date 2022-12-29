package de.dhbw.text2process.models.meta;

import de.dhbw.text2process.models.bpmn.Activity;
import de.dhbw.text2process.models.bpmn.Artifact;
import de.dhbw.text2process.models.bpmn.Association;
import de.dhbw.text2process.models.bpmn.ChoreographyActivity;
import de.dhbw.text2process.models.bpmn.ComplexGateway;
import de.dhbw.text2process.models.bpmn.Conversation;
import de.dhbw.text2process.models.bpmn.ConversationLink;
import de.dhbw.text2process.models.bpmn.EndEvent;
import de.dhbw.text2process.models.bpmn.Event;
import de.dhbw.text2process.models.bpmn.EventBasedGateway;
import de.dhbw.text2process.models.bpmn.ExclusiveGateway;
import de.dhbw.text2process.models.bpmn.Gateway;
import de.dhbw.text2process.models.bpmn.InclusiveGateway;
import de.dhbw.text2process.models.bpmn.IntermediateEvent;
import de.dhbw.text2process.models.bpmn.Lane;
import de.dhbw.text2process.models.bpmn.LaneableCluster;
import de.dhbw.text2process.models.bpmn.Message;
import de.dhbw.text2process.models.bpmn.MessageFlow;
import de.dhbw.text2process.models.bpmn.ParallelGateway;
import de.dhbw.text2process.models.bpmn.Pool;
import de.dhbw.text2process.models.bpmn.SequenceFlow;
import de.dhbw.text2process.models.bpmn.StartEvent;
import de.dhbw.text2process.models.bpmn.nodes.Cluster;
import de.dhbw.text2process.models.bpmn.nodes.FlowObject;
import de.dhbw.text2process.models.bpmn.nodes.ProcessEdge;
import de.dhbw.text2process.models.bpmn.nodes.ProcessNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BPMNModel extends ProcessModel implements Serializable {

  private ArrayList<Event> events = new ArrayList<Event>();

  ArrayList<ComplexGateway> comGates = new ArrayList<ComplexGateway>();
  ArrayList<EventBasedGateway> eBasGates = new ArrayList<EventBasedGateway>();
  ArrayList<ExclusiveGateway> exGates = new ArrayList<ExclusiveGateway>();
  ArrayList<InclusiveGateway> incGates = new ArrayList<InclusiveGateway>();
  ArrayList<ParallelGateway> parGates = new ArrayList<ParallelGateway>();

  public BPMNModel() {
    super();
  }

  public BPMNModel(String name) {
    super(name);
  }

  public String getDescription() {
    return "BPMN 2.0";
  }

  public void addFlowObject(FlowObject o) {
    super.addNode(o);
  }

  public void addFlow(ProcessEdge e) {
    addEdge(e);
  }

  public List<FlowObject> getFlowObjects() {
    // Figure out all flow objects
    List<FlowObject> result = new LinkedList<FlowObject>();
    for (ProcessNode n : super.getNodes()) {
      if (n instanceof FlowObject) {
        result.add((FlowObject) n);
      }
    }

    return result;
  }

  public List<SequenceFlow> getSequenceFlows() {
    List<SequenceFlow> result = new LinkedList<SequenceFlow>();
    for (ProcessEdge f : super.getEdges()) {
      if (f instanceof SequenceFlow) {
        result.add((SequenceFlow) f);
      }
    }
    return result;
  }

  public LinkedList<Association> getAssociations() {
    LinkedList<Association> result = new LinkedList<Association>();
    for (ProcessEdge f : super.getEdges()) {
      if (f instanceof Association) {
        result.add((Association) f);
      }
    }
    return result;
  }

  /**
   * Detects the Pool where the ProcessNode is contained inside. Returns <b>null</b> if not in any
   * Pool.
   *
   * @param node
   * @return
   */
  public Pool getPoolForNode(ProcessNode node) {
    // Get Cluster
    Cluster c = getClusterForNode(node);
    if (c == null) return null; // Not in any Cluster
    while (c != null) {
      if (c instanceof Pool) return (Pool) c;
      c = getClusterForNode(c);
    }
    return null;
  }

  @Override
  public String toString() {
    if (getProcessName() == null) {
      return super.toString();
    }
    return getProcessName() + " (BPMN)";
  }

  @Override
  public List<Class<? extends ProcessNode>> getSupportedNodeClasses() {
    List<Class<? extends ProcessNode>> result = new LinkedList<Class<? extends ProcessNode>>();
    result.add(Activity.class);
    result.add(StartEvent.class);
    result.add(IntermediateEvent.class);
    result.add(EndEvent.class);
    result.add(Gateway.class);
    result.add(Artifact.class);
    result.add(Pool.class);
    result.add(ChoreographyActivity.class);
    result.add(Conversation.class);
    result.add(Message.class);
    // result.add(StickyNote.class);
    return result;
  }

  @Override
  public List<Class<? extends ProcessEdge>> getSupportedEdgeClasses() {
    List<Class<? extends ProcessEdge>> result = new LinkedList<Class<? extends ProcessEdge>>();
    result.add(SequenceFlow.class);
    result.add(MessageFlow.class);
    result.add(Association.class);
    result.add(ConversationLink.class);
    return result;
  }

  @Override
  public void removeNode(ProcessNode node) {
    super.removeNode(node);
    if (node instanceof LaneableCluster) {
      LaneableCluster _lc = (LaneableCluster) node;
      for (Lane l : _lc.getLanes()) {
        this.removeNode(l);
      }
    }
  }

  public void extractGateways() {
    for (ProcessNode pn : super.getNodes()) {
      if (pn instanceof ComplexGateway) {
        ComplexGateway cg = (ComplexGateway) pn;
        comGates.add(cg);
      }
      if (pn instanceof EventBasedGateway) {
        EventBasedGateway cg = (EventBasedGateway) pn;
        eBasGates.add(cg);
      }
      if (pn instanceof ExclusiveGateway) {
        ExclusiveGateway cg = (ExclusiveGateway) pn;
        exGates.add(cg);
      }
      if (pn instanceof InclusiveGateway) {
        InclusiveGateway cg = (InclusiveGateway) pn;
        incGates.add(cg);
      }
      if (pn instanceof ParallelGateway) {
        ParallelGateway cg = (ParallelGateway) pn;
        parGates.add(cg);
      }
    }
  }

  public ArrayList<ComplexGateway> getComplexGateways() {
    return comGates;
  }

  public ArrayList<EventBasedGateway> getEventBasedGateways() {
    return eBasGates;
  }

  public ArrayList<ExclusiveGateway> getExclusiveGateways() {
    return exGates;
  }

  public ArrayList<InclusiveGateway> getInclusiveGateways() {
    return incGates;
  }

  public ArrayList<ParallelGateway> getParallelGateways() {
    return parGates;
  }

  public void addEvent(Event e) {
    events.add(e);
  }

  public ArrayList<Event> getEvents() {
    return events;
  }
}
