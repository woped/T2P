package de.dhbw.text2process.models.bpmn;

public class EventBasedGateway extends Gateway {

  /** Defines if this Gateway is instantiating: NONE, EXCLUSIVE, PARALLEL */
  public static final String PROP_INSTANTIATE = "instantiate";

  public static final String TYPE_INSTANTIATE_NONE = "NONE";
  public static final String TYPE_INSTANTIATE_EXCLUSIVE = "EXCLUSIVE";
  public static final String TYPE_INSTANTIATE_PARALLEL = "PARALLEL";

  public EventBasedGateway() {
    super();
    initializeProperties();
  }

  public void initializeProperties() {
    setProperty(PROP_INSTANTIATE, TYPE_INSTANTIATE_NONE);
    //       String[] inst = { TYPE_INSTANTIATE_NONE , TYPE_INSTANTIATE_EXCLUSIVE,
    // TYPE_INSTANTIATE_PARALLEL };
    // setPropertyEditor(PROP_INSTANTIATE, new ListSelectionPropertyEditor(inst));
  }
}
