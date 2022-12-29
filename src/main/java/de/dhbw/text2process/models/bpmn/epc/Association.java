package de.dhbw.text2process.models.bpmn.epc;

import de.dhbw.text2process.models.bpmn.nodes.ProcessEdge;
import de.dhbw.text2process.models.bpmn.nodes.ProcessNode;

public class Association extends ProcessEdge {

  public static final String PROP_DIRECTION = "direction";

  public static final String DIRECTION_SOURCE = "SOURCE";
  public static final String DIRECTION_TARGET = "TARGET";
  public static final String DIRECTION_BOTH = "BOTH";
  public static final String DIRECTION_NONE = "NONE";

  public Association(ProcessNode source, ProcessNode target) {
    super(source, target);
    initializeProperties();
  }

  private void initializeProperties() {
    setProperty(PROP_DIRECTION, DIRECTION_TARGET);
  }
}
