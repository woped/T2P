package de.dhbw.text2process.processors.petrinet;

import de.dhbw.text2process.models.petrinet.Arc;
import de.dhbw.text2process.models.petrinet.Place;
import de.dhbw.text2process.models.petrinet.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetrinetElementBuilder {

  Logger logger = LoggerFactory.getLogger(PetrinetElementBuilder.class);

  private IDHandler transitionIDH, placeIDH, arcIDH;

  public PetrinetElementBuilder() {
    transitionIDH = new IDHandler(1);
    placeIDH = new IDHandler(1);
    arcIDH = new IDHandler(1);
  }

  public Place createPlace(boolean hasMarking, String originID) {
    Place p = new Place(hasMarking, originID, placeIDH);
    p.generateXmlString();
    return p;
  }

  public Transition createTransition(
      String text, boolean hasResource, boolean isGateway, String originID) {
    Transition t = new Transition(text, hasResource, isGateway, originID, transitionIDH);
    t.generateXmlString();
    return t;
  }

  public Arc createArc(String source, String target, String originID) {
    Arc a = new Arc(source, target, originID, arcIDH);
    a.generateXmlString();
    return a;
  }
}
