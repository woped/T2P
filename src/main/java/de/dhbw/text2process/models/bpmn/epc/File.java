package de.dhbw.text2process.models.bpmn.epc;

import de.dhbw.text2process.helper.ReferenceChooserRestriction;
import de.dhbw.text2process.models.bpmn.nodes.Linkable;
import java.io.Serializable;

public class File extends Artifact implements Linkable, Serializable {

  /** Property if this Data Object is a collection (0=FALSE, 1=TRUE) */
  public static final String PROP_COLLECTION = "collection";
  /** Property if this Data Object is Input or Output) */
  public static final String PROP_DATA = "data";
  /** Property to hold the state of the DataObject */
  public static final String PROP_STATE = "state";
  /** DataObject type is not input/output */
  public static final String DATA_NONE = "";
  /** DataObject type is Input */
  public static final String DATA_INPUT = "INPUT";
  /** DataObject type is Output */
  public static final String DATA_OUTPUT = "OUTPUT";

  public File() {
    super();
    initializeProperties();
  }

  public File(String text) {
    super();
    setText(text);
    initializeProperties();
  }

  private void initializeProperties() {
    setProperty(PROP_COLLECTION, null);
    setProperty(PROP_DATA, DATA_NONE);

    setProperty(PROP_STATE, "");
  }

  public String getState() {
    return getProperty(PROP_STATE);
  }

  public void setState(String state) {
    setProperty(PROP_STATE, state);
  }

  @Override
  public String toString() {
    return "EPC File (" + getText() + ")";
  }

  @Override
  public ReferenceChooserRestriction getReferenceRestrictions() {
    throw new IllegalArgumentException("not implemented yet!");
  }
}
