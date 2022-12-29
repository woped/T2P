package de.dhbw.WoPeDText2Process.UTToolwrapper;

import static org.junit.Assert.assertEquals;

import de.dhbw.text2process.wrapper.FrameNetFunctionality;
import de.dhbw.text2process.wrapper.FrameNetInitializer;
import java.io.IOException;
import org.junit.Test;

public class UTFrameNet {
  @Test
  public void evaluateFramnetInvocation() throws IOException {

    /***check Initialiaztion***/
    FrameNetInitializer fni = FrameNetInitializer.getInstance();
    assertEquals("FrameNet Initialization Issue: Not initialized.", true, fni != null);

    /***check Functionality***/
    FrameNetFunctionality fnf = new FrameNetFunctionality();

    // TODO: Saubere Tests schreiben
    fnf.printAllFrames();
    // fnf.determineSpecifierFrameElement(fe, );
    // fnf.toPT(fe);
  }
}
