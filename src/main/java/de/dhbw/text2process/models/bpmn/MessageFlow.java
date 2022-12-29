package de.dhbw.text2process.models.bpmn;

import de.dhbw.text2process.models.bpmn.nodes.ProcessEdge;
import de.dhbw.text2process.models.bpmn.nodes.ProcessNode;

public class MessageFlow extends ProcessEdge {

  public MessageFlow() {
    super();
    initializeProperties();
  }

  public MessageFlow(ProcessNode source, ProcessNode target) {
    super();
    initializeProperties();
  }

  private void initializeProperties() {
    // empty yet
  }
}
