package de.dhbw.text2process.models.bpmn.epc;

import de.dhbw.text2process.models.bpmn.SequenceFlow;
import de.dhbw.text2process.models.bpmn.nodes.FlowObject;
import de.dhbw.text2process.models.bpmn.nodes.ProcessNode;
import java.util.LinkedList;
import java.util.List;

public class Artifact extends FlowObject {

  @Override
  public List<Class<? extends ProcessNode>> getVariants() {
    List<Class<? extends ProcessNode>> result = new LinkedList<Class<? extends ProcessNode>>();
    result.add(File.class);
    return result;
  }

  @Override
  public void setIncoming(SequenceFlow flow) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setOutgoing(SequenceFlow flow) {
    // TODO Auto-generated method stub

  }
}
