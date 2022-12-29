package de.dhbw.text2process.models.bpmn;

import de.dhbw.text2process.helper.ReferenceChooserRestriction;
import de.dhbw.text2process.models.bpmn.nodes.Linkable;
import java.util.LinkedList;

public class DataObject extends Artifact implements Linkable {

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

  public static ReferenceChooserRestriction restrictions;

  public DataObject() {
    super();
    initializeProperties();
  }

  public DataObject(int xPos, int yPos, String text) {
    super();
    setText(text);
    initializeProperties();
  }

  private void initializeProperties() {
    setProperty(PROP_COLLECTION, null);
    setProperty(PROP_DATA, DATA_NONE);
    //      String[] data = { DATA_NONE, DATA_INPUT, DATA_OUTPUT };

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
    return "BPMN Data Object (" + getText() + ")";
  }

  public ReferenceChooserRestriction getReferenceRestrictions() {
    if (restrictions == null) {
      LinkedList<Class> classes = new LinkedList<Class>();
      // classes.add(DomainClass.class);
      restrictions = new ReferenceChooserRestriction(null, classes);
    }
    return restrictions;
  }
}
