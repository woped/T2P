package de.dhbw.text2process.helper.rest;

import de.dhbw.text2process.exceptions.BpmnGenerationException;
import de.dhbw.text2process.exceptions.InvalidInputException;
import de.dhbw.text2process.exceptions.PetrinetGenerationException;
import de.dhbw.text2process.exceptions.WorldModelGenerationException;
import de.dhbw.text2process.helper.TextToProcess;
import de.dhbw.text2process.models.worldModel.Action;
import de.dhbw.text2process.models.worldModel.Actor;
import de.dhbw.text2process.models.worldModel.Flow;
import de.dhbw.text2process.models.worldModel.Resource;
import de.dhbw.text2process.models.worldModel.WorldModel;
import de.dhbw.text2process.processors.bpmn.BPMNModelBuilder;
import de.dhbw.text2process.processors.petrinet.PetrinetBuilder;
import de.dhbw.text2process.processors.worldmodel.WorldModelBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dhbw.text2process.helper.appParameterHelper;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

public class T2PControllerHelper {

  // Initialize log4j to log information into the console
  Logger logger = LoggerFactory.getLogger(T2PControllerHelper.class);

  // Reject any Request larger than this
  public static final int MAX_INPUT_LENGTH = 15000;
  public static final int MIN_INPUT_LENGTH = 10;


  /**
   *
   *
   * <h1>generateWorldModelFromText</h1>
   *
   * <p>
   *
   * @author KanBen86
   * @param text
   * @return A WorldModel
   * @throws WorldModelGenerationException
   */
  public WorldModel generateWorldModelFromText(String text)
      throws WorldModelGenerationException, IOException {
    logger.debug("Instantiating the WorldModelBuilder ...");
    WorldModelBuilder worldModelBuilder = new WorldModelBuilder(text);
    WorldModel worldModel = worldModelBuilder.buildWorldModel(false);
    List<Action> actions = worldModel.getActions();
    List<Actor> actors = worldModel.getActors();
    List<Flow> elements = worldModel.getFlows();
    List<Resource> resources = worldModel.getResources();

    logger.info(actions.toString());
    logger.info(actors.toString());
    logger.info(elements.toString());
    logger.info(resources.toString());
    return worldModel;
  }

  /**
   *
   *
   * <h1>generatePetrinetFromText</h1>
   *
   * <p>
   *
   * @author
   * @param text
   * @return A string which represents the PetriNet
   * @throws PetrinetGenerationException
   */
  public String generatePetrinetFromText(String text)
      throws PetrinetGenerationException, WorldModelGenerationException, IOException {
    logger.debug("Instantiating the PetrinetBuilder based on the WorldModel ...");
    PetrinetBuilder petriNetBuilder = new PetrinetBuilder(generateWorldModelFromText(text));
    logger.debug("Build the PNML-String ...");
    String pnml = petriNetBuilder.buildPNML();
    logger.debug("Minimizing the PNML-String ...");
    pnml = minifyResult(pnml);
    logger.debug("The PNML-String is done. Method is returning the final PNML-String.");
    return pnml;
  }

  /**
   *
   *
   * <h1>generateBpmnFileFromText</h1>
   *
   * <p>
   *
   * @author
   * @param text
   * @return A file which represents the BPMN process model
   * @throws BpmnGenerationException, WorldModelGenerationException, IOException
   */
  public File generateBpmnFileFromText(String text, boolean newBpmn)
      throws BpmnGenerationException, WorldModelGenerationException, IOException {
    logger.debug("Creating a file for the Export ...");
    File bpmnFile = new File("bpmn.file");
    if (!bpmnFile.exists()) bpmnFile.createNewFile();

    createBPMNFile(text, bpmnFile, newBpmn);

    return bpmnFile;
  }

  /**
   *
   *
   * <h1>generateBPMNFromText</h1>
   *
   * <p>
   *
   * @author
   * @param text
   * @return A string which represents the BPMN process model
   * @throws BpmnGenerationException, WorldModelGenerationException, IOException
   */
  public String generateBpmnFromText(String text, boolean newBpmn)
      throws BpmnGenerationException, WorldModelGenerationException, IOException {

    logger.debug("Creating a file for the Export ...");
    File bpmnFile = new File("bpmn.file");
    if (!bpmnFile.exists()) bpmnFile.createNewFile();

    createBPMNFile(text, bpmnFile, newBpmn);

    logger.debug("Converting BPMN model to string");
    String bpmnString = "";

    Scanner fileScanner = new Scanner(bpmnFile);

    while (fileScanner.hasNext()) {
      bpmnString += fileScanner.next() + System.lineSeparator();
    }

    logger.debug("The string contains:\n" + bpmnString);
    return bpmnString;
  }

  private void createBPMNFile(String text, File bpmnFile, boolean newBpmn) throws IOException {
    logger.debug("Instantiating the TextToProcess object ...");
    TextToProcess textToProcess = new TextToProcess();
    logger.debug(
        "Instantiating the BPNMModelBuilder object with the TextToProcess object as parameter ...");
    BPMNModelBuilder bpmnBuilder = new BPMNModelBuilder(textToProcess);

    logger.debug("Set Text ...");
    textToProcess.setProcessText(text);
    logger.debug("Starting analyzing the given text ...");
    textToProcess.analyzeText(true, true, bpmnFile, newBpmn);
  }

  private String minifyResult(String result) {
    // A few characters less to bother the internet with ¯\_(ツ)_/¯
    logger.debug("replacing \\n and \\t");
    result = result.replaceAll("\n", "");
    result = result.replaceAll("\t", "");
    logger.debug("Replacing two space characters with one.");
    while (result.contains("  ")) {
      result = result.replace("  ", " ");
    }
    return result;
  }

  /**
   * @param request
   * @return
   */
  private String readRequestBody(HttpServletRequest request) {
    BufferedReader bufferedReader;
    StringBuilder stringBuilder = new StringBuilder();
    try {
      bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
      char[] charBuffer = new char[128];
      bufferedReader.read(charBuffer);
      int bytesRead = -1;
      while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
        stringBuilder.append(charBuffer, 0, bytesRead);
      }
      logger.debug("The read input: " + stringBuilder.toString());
      bufferedReader.close();
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
    return stringBuilder.toString();
  }

  /**
   * @param text
   * @throws InvalidInputException
   */
  public String checkInputValidity(String text) throws InvalidInputException {
    Response.ErrorCodeHolder responseCode = new Response.ErrorCodeHolder();
    return checkInputValidity(text, responseCode);
  }

  /**
   * @param text
   * @throws InvalidInputException
   */
  public String checkInputValidity(String text, Response.ErrorCodeHolder code) throws InvalidInputException {

  Properties props = appParameterHelper.GetConfigFile();

    Response<String> pnmlResponse;
    pnmlResponse = new Response<String>(true, null, "", null);

    //if (text.length() > MAX_INPUT_LENGTH | text.equals("")) {
    if (text.length() > Integer.parseInt(props.getProperty("request.maxTextLength")) | text.equals("")) {
      pnmlResponse.setResponse("The input is too long.");

      //ENUMparameter set for response status-code
      code.code = Response.ErrorCodes.INVALIDREQUEST;
      return pnmlResponse.getResponse();
    }
    //if (text.length() < MIN_INPUT_LENGTH | text.equals("")) {
    if (text.length() < Integer.parseInt(props.getProperty("request.minTextLength")) | text.equals("")) {

      pnmlResponse.setResponse("The input is too short.");
      return pnmlResponse.getResponse();
    }
    // Accept only characters common in a plain text in english language
    //Pattern p = Pattern.compile("[^a-z0-9,./?:!\\s\\t\\n'£$%&*()_\\-`]", Pattern.CASE_INSENSITIVE);
    Pattern p = Pattern.compile(props.getProperty("other.validation.regex"), Pattern.CASE_INSENSITIVE);

    //Pattern p = Pattern.compile("[^a-z0-9,./?:!\\s\\t'n£$%&*()_\\-`]", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(text);
    int count = 0;
    String message = "";
    while (m.find()) {
      count = count + 1;
      String messageAdded = "[...]";
      for(int i = -5; i < 5; i++){
        if(m.start() + i >= 0 && m.start() + i <= text.length() - 1)
          messageAdded += text.charAt(m.start() + i);
      }
      messageAdded +="[...]";
      message += "position " + m.start() + ": " + text.charAt(m.start()) + " in context: " + messageAdded + "\n";
    }
    if (count > 0) {
      code.code = Response.ErrorCodes.INVALIDREQUEST;
      pnmlResponse.setResponse("There are " + count + " invalid characters in the input:\n" + message);
      return pnmlResponse.getResponse();
    }
    return "";
  }

  /** */
  public void resetNLPTools() {
    WorldModelBuilder.resetNLPTools();
  }
}
