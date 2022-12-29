package de.dhbw.text2process.models.bpmn;

import de.dhbw.text2process.models.bpmn.nodes.ProcessEdge;
import de.dhbw.text2process.models.bpmn.nodes.ProcessNode;

public class SequenceFlow extends ProcessEdge {

  /** The direction of this SequenceFlow. Possible values are "STANDARD, DEFAULT, CONDITIONAL" */
  public static final String PROP_SEQUENCETYPE = "sequence_type";

  public static final String TYPE_STANDARD = "STANDARD";
  public static final String TYPE_DEFAULT = "DEFAULT";
  public static final String TYPE_CONDITIONAL = "CONDITIONAL";

  public SequenceFlow() {
    super();
    initializeProperties();
  }

  public SequenceFlow(ProcessNode source, ProcessNode target) {
    super();
    initializeProperties();
  }

  private void initializeProperties() {
    setProperty(PROP_SEQUENCETYPE, "TYPE_STANDARD");
    //      String[] type = { TYPE_STANDARD , TYPE_DEFAULT, TYPE_CONDITIONAL };
    // setPropertyEditor(PROP_SEQUENCETYPE, new ListSelectionPropertyEditor(type));

  }

  public boolean isOutlineSourceArrow() {
    String type = getProperty(PROP_SEQUENCETYPE).toLowerCase();
    if (type.equals(TYPE_CONDITIONAL)) {
      return true;
    }
    return false;
  }

  protected boolean isDockingSupported() {
    return true;
  }
}
