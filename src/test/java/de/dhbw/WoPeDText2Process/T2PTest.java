package de.dhbw.WoPeDText2Process;

import java.util.Iterator;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class T2PTest {

  Logger logger = LoggerFactory.getLogger(T2PTest.class);

  /*
  Some Basic Utils for all T2P Tests
  */

  protected boolean euqualsWeakly(String exspected, String actual) {
    boolean equals = true;
    String a = sanitizeText(exspected);
    String b = sanitizeText(actual);
    DiffMatchPath dmp = new DiffMatchPath();
    LinkedList<DiffMatchPath.Diff> x = dmp.diff_main(exspected, actual);
    Iterator<DiffMatchPath.Diff> i = x.iterator();
    while (i.hasNext()) {
      DiffMatchPath.Diff diff = i.next();
      if (!diff.operation.equals(DiffMatchPath.Operation.EQUAL)) equals = false;
    }
    if (!equals) {
      logger.info("Actual and exscpect differ. The  following characters need to be fixed: ");
      LinkedList<DiffMatchPath.Patch> pl = dmp.patch_make(actual, exspected);
      Iterator<DiffMatchPath.Patch> k = pl.iterator();
      while (k.hasNext()) {
        DiffMatchPath.Patch patch = k.next();
        logger.info("Mismatch at " + patch.start1 + ": ");
        logger.info(patch.diffs.toString());
      }
    }
    return equals;
  }

  protected static String sanitizeText(String text) {
    // get rid of tabs and newlines
    text = text.replace("\t", "");
    text = text.replace("\n", "");

    // deal with x*space based tabs
    while (text.contains("  ")) {
      text = text.replace("  ", " ");
    }
    return text;
  }
}
