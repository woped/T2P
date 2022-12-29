package de.dhbw.WoPeDText2Process;

import de.dhbw.text2process.models.worldModel.SpecifiedElement;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class T2PScenarioTest extends T2PTest {

  static Logger logger = LoggerFactory.getLogger(T2PScenarioTest.class);

  /*
  Some Basic Utils specifically for T2P Scenario Tests
  including Commandline outputs, text cleansing and performance measurement
  */

  protected static long startTime;
  protected static String filePath;
  protected static Document doc;

  protected static double calculateDeltaScore(
      int actualElementsCount, int exscpectedElementsCount) {
    double actual = (double) actualElementsCount;
    double excspected = (double) exscpectedElementsCount;
    double delta = Math.abs(actual - excspected);
    double relativeDelta = delta / actual;
    return 1 - relativeDelta;
  }

  protected void startPerformanceTrace() {
    startTime = System.nanoTime();
  }

  protected long endPerformanceTrace() {
    long endTime = System.nanoTime();

    long duration = (endTime - startTime);
    return duration / 1000000; // milliseconds
  }

  protected static void printInfo(int currentTestcaseNumber, String currentTestcase) {
    logger.info("--------------------------------------------------------------");
    logger.info("    Test " + (currentTestcaseNumber + 1) + ": " + currentTestcase);
    logger.info("--------------------------------------------------------------");
  }

  protected static void prinComparisonTable(String[][] comparison) {
    // prints a balanced 2 Dimensional Array of abitrary format
    int[] messures = new int[comparison.length];
    int offset = 7;
    int maxElements = 0;
    String format = "";
    int width = 4;
    for (int i = 0; i < comparison.length; i++) {
      int colMax = 0;
      if (comparison[i].length > maxElements) maxElements = comparison[i].length;
      for (int j = 0; j < comparison[i].length; j++) {
        if (comparison[i][j].length() > colMax) colMax = comparison[i][j].length();
      }
      width += (colMax + offset);
      format += "%-" + (colMax + offset) + "s";
    }
    String line = new String(new char[width]).replace("\0", "-");
    logger.info("" + line);
    for (int i = 0; i < maxElements; i++) {
      String[] row = new String[comparison.length];
      for (int j = 0; j < comparison.length; j++) {
        row[j] = comparison[j][i];
      }
      logger.info("| ");
      logger.info(String.format(format, row));
      logger.info(" |\n");
      if (i == 0) logger.info(line + "\n");
    }
    logger.info(line + "\n");
  }

  protected static void printScore(String message, double score) {
    logger.info(message + " in Percent: %.2f%%%n", score * 100);
  }

  protected static List<? extends SpecifiedElement> getDisjointList(
      List<? extends SpecifiedElement> elements) {
    ArrayList<String> alreadyOccured = new ArrayList<String>();
    for (int i = 0; i < elements.size(); i++) {
      boolean occured = false;
      for (int j = 0; j < alreadyOccured.size(); j++) {
        if (elements.get(i).getName().equalsIgnoreCase(alreadyOccured.get(j))) occured = true;
      }
      if (occured) {
        elements.remove(i);
      } else {
        alreadyOccured.add(elements.get(i).getName());
      }
    }
    return elements;
  }

  protected static void parseTestFile(String fileName) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      File inputFile = new File(filePath + fileName);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.parse(inputFile);
      doc.getDocumentElement().normalize();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
  }

  protected static String getPlainTextDescription() {
    return doc.getElementsByTagName("PlainTextDescription")
        .item(0)
        .getChildNodes()
        .item(0)
        .getNodeValue()
        .toString();
  }
}
