package de.dhbw.WoPeDText2Process.UTWorldModelToPetriNet;

import static org.junit.Assert.assertEquals;

import de.dhbw.WoPeDText2Process.T2PUnitTest;
import de.dhbw.text2process.models.petrinet.Place;
import de.dhbw.text2process.processors.petrinet.IDHandler;
import org.junit.Test;

public class UTPlace extends T2PUnitTest {

  /*Unit test for Class WorldModelToPetrinet.Place*/

  String exspectedPNML =
      "<place id=\"p1\"><name><text>p1</text>"
          + "<graphics><offset x=\"0\" y=\"0\"/></graphics>"
          + "</name><graphics><position x=\"0\" y=\"0\"/>"
          + "<dimension x=\"40\" y=\"40\"/></graphics></place>";

  @Test
  public void evaluatePlace() {
    Place p = new Place(false, "", new IDHandler(1));
    assertEquals(
        "Place did not create exspected PNML.", true, euqualsWeakly(exspectedPNML, p.toString()));
  }
}
