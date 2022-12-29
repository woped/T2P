package de.dhbw.text2process.models.bpmn;

import de.dhbw.text2process.models.bpmn.nodes.FlowObject;
import de.dhbw.text2process.models.bpmn.nodes.ProcessNode;
import java.util.LinkedList;
import java.util.List;

public class ChoreographyActivity extends FlowObject {

  @Override
  public List<Class<? extends ProcessNode>> getVariants() {
    List<Class<? extends ProcessNode>> result = new LinkedList<Class<? extends ProcessNode>>();
    result.add(ChoreographyTask.class);
    result.add(ChoreographySubProcess.class);
    return result;
  }

  @Override
  public void setIncoming(SequenceFlow flow) {
    throw new IllegalArgumentException("not implemented yet!");
  }

  @Override
  public void setOutgoing(SequenceFlow flow) {
    throw new IllegalArgumentException("not implemented yet!");
  }
}
