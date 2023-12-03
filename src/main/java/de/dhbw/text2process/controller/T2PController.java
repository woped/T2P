/**
 *
 *
 * <h1>T2PController</h1>
 *
 * <p>The T2PController is a SpringBoot specific ReST-Controller to build a WebService. This
 * interface is used by the WoPeD Tool to create a PetriNet based on the response bodies.
 *
 * <p>Please do not misuse this controller for any other reason instead of building new interfaces
 * nodes.
 *
 * @author <a href="mailto:kanzler.benjamin@student.dhbe-karlsruhe.de">Benjamin Kanzler</a>
 */
package de.dhbw.text2process.controller;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.dhbw.text2process.exceptions.BpmnGenerationException;
import de.dhbw.text2process.exceptions.InvalidInputException;
import de.dhbw.text2process.exceptions.PetrinetGenerationException;
import de.dhbw.text2process.exceptions.WorldModelGenerationException;
import de.dhbw.text2process.helper.rest.Response;
import de.dhbw.text2process.helper.rest.T2PControllerHelper;
import de.dhbw.text2process.wrapper.FrameNetInitializer;
import de.dhbw.text2process.wrapper.StanfordParserInitializer;
import de.dhbw.text2process.wrapper.WordNetInitializer;
import de.dhbw.text2process.wrapper.WordNetWrapper;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.didion.jwnl.JWNLException;

@RestController()
public class T2PController {

  // Initialize log4j to log information into the console
  Logger logger = LoggerFactory.getLogger(T2PController.class);

  /*
   * Helper class to keep the controller clean. Please place any methods
   * performing the work here.
   */
  T2PControllerHelper t2PControllerHelper = new T2PControllerHelper();

  public T2PController() throws JWNLException {
    StanfordParserInitializer.getInstance();
    WordNetInitializer.getInstance();
    WordNetWrapper.init();
    FrameNetInitializer.getInstance();
  }

  /**
   *
   *
   * <h1>generateBPMNFileFromText</h1>
   *
   * <p>POST based controller method. Tailored to generate a BPMN from a given Text. Use the proper
   * URL to get generate the String which can be interpreted by the WoPeD-Tool.
   *
   * @author <a href="mailto:kanzler.benjamin@student.dhbe-karlsruhe.de">Benjamin Kanzler</a>
   * @param param: plainText in as application/json
   * @return A generic Response Object with the PNML-String in the response attribute.
   */
  @ApiOperation(value = "Translate text into a process model file type bpmn.")
  @PostMapping(
      value = "/generateBPMNFile",
      consumes = "application/json",
      produces = "application/json")
  public File generateBPMNFileFromText(
      @RequestBody String param, HttpServletRequest request, HttpServletResponse response) {

    Response<File> bpmnResponse;

    logger.info("Trying to generate a BPMN with the given String parameter");
    try {
      logger.info("Validating the String information, scanning for incompatible characters");
      t2PControllerHelper.checkInputValidity(param);
      logger.info("Starting generating BPMN -String ...");
      bpmnResponse = new Response<File>(t2PControllerHelper.generateBpmnFileFromText(param, false));
      logger.info("Finished generating PNML-String");
    } catch (WorldModelGenerationException e) {
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<File>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    } catch (InvalidInputException e) {
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<File>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    } catch (BpmnGenerationException e) {
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<File>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    } catch (IOException e) {
      // TODO: handle exception
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<File>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    }

    // Response status should not be set here: Rcs defined in catch blocks are
    // overwritten!
    logger.info("Returning the pnmlString");
    return bpmnResponse.getResponse();
  }

  /**
   *
   *
   * <h1>generateBPMNFromText</h1>
   *
   * <p>POST based controller method. Tailored to generate a BPMN from a given Text. Use the proper
   * URL to get generate the String which can be interpreted by the WoPeD-Tool.
   *
   * @author <a href="mailto:kanzler.benjamin@student.dhbe-karlsruhe.de">Benjamin Kanzler</a>
   * @param param: plainText in as application/json
   * @return A generic Response Object with the PNML-String in the response attribute.
   */
  @ApiOperation(value = "Translate text into a process model type bpmn.")
  @PostMapping(
      value = "/generateBPMN",
      consumes = "application/json",
      produces = "application/json")
  public String generateBPMNFromText(
      @RequestBody String param, HttpServletRequest request, HttpServletResponse response) {

    Response<String> bpmnResponse;

    logger.info("Trying to generate a BPMN with the given String parameter");
    try {
      logger.info("Validating the String information, scanning for incompatible characters");
      t2PControllerHelper.checkInputValidity(param);
      logger.info("Starting generating BPMN -String ...");
      bpmnResponse = new Response<String>(t2PControllerHelper.generateBpmnFromText(param, false));
      logger.info("Finished generating PNML-String");
    } catch (WorldModelGenerationException e) {
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    } catch (InvalidInputException e) {
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    } catch (BpmnGenerationException e) {
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    } catch (IOException e) {
      // TODO: handle exception
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    }

    // Response status should not be set here: Rcs defined in catch blocks are
    // overwritten!
    logger.info("Returning the bpmnString");
    return bpmnResponse.getResponse();
  }

  /**
   *
   *
   * <h1>generateBPMNFromText</h1>
   *
   * <p>POST based controller method. Tailored to generate a BPMN from a given Text. Use the proper
   * URL to get generate the String which can be interpreted by the WoPeD-Tool.
   *
   * @author <a href="mailto:kanzler.benjamin@student.dhbe-karlsruhe.de">Benjamin Kanzler</a>
   * @param param: plainText in as application/json
   * @return A generic Response Object with the PNML-String in the response attribute.
   */
  @ApiOperation(value = "Translate text into a process model type bpmn.")
  @PostMapping(
      value = "/generateBPMNv2",
      consumes = "application/json",
      produces = "application/json")
  public String generateBPMNFromTextv2(
      @RequestBody String param, HttpServletRequest request, HttpServletResponse response) {

    Response<String> bpmnResponse;

    logger.info("Trying to generate a BPMN with the given String parameter");
    try {
      logger.info("Validating the String information, scanning for incompatible characters");
      t2PControllerHelper.checkInputValidity(param);
      logger.info("Starting generating BPMN -String ...");
      bpmnResponse = new Response<String>(t2PControllerHelper.generateBpmnFromText(param, true));
      logger.info("Finished generating PNML-String");
    } catch (WorldModelGenerationException e) {
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    } catch (InvalidInputException e) {
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    } catch (BpmnGenerationException e) {
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    } catch (IOException e) {
      // TODO: handle exception
      // TODO Text anpassen
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      bpmnResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    }

    // Response status should not be set here: Rcs defined in catch blocks are
    // overwritten!
    logger.info("Returning the bpmnString");
    return bpmnResponse.getResponse();
  }

  /**
   *
   *
   * <h1>generatePetriNetFromText</h1>
   *
   * <p>POST based controller method. Tailored to generate a PetriNet from a given Text. Use the
   * proper URL to get generate the String which can be interpreted by the WoPeD-Tool.
   *
   * @author <a href="mailto:kanzler.benjamin@student.dhbe-karlsruhe.de">Benjamin Kanzler</a>
   * @param param:
   * @return A generic Response Object with the PNML-String in the response attribute.
   */
  @ApiOperation(value = "Translate text into a process model type petrinet.")
  @PostMapping(
      value = "/generatePNML",
      consumes = "application/json",
      produces = "application/json")
  public String generatePetriNetFromText(
      @RequestBody String param, HttpServletRequest request, HttpServletResponse response) {

    Response<String> pnmlResponse;

    logger.info("Trying to generate a PetriNet with the given String parameter");
    try {
      logger.info("Validating the String information, scanning for incompatible characters");
      t2PControllerHelper.checkInputValidity(param);
      logger.info("Starting generating PNML-String ...");
      pnmlResponse = new Response<String>(t2PControllerHelper.generatePetrinetFromText(param));
      logger.info("Finished generating PNML-String");
    } catch (InvalidInputException e) {
      logger.error(
          "The given parameter ist not a valid one. Please check the String and pass a correct"
              + " one. More details on the error is stored in the response json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      pnmlResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    } catch (PetrinetGenerationException e) {
      logger.error(
          "The PetriNet was not properly built. Please contact the development department. <a"
              + " href=mailto:woped-service@dhbw-karlsruhe.de>WoPeD-Service<a>");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      e.printStackTrace();
      pnmlResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    } catch (Exception e) {
      logger.error(
          "An unexpected error interrupted the process. Exception information is given to the"
              + " return json");
      logger.error(e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      pnmlResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
    }

    // Response status should not be set here: Rcs defined in catch blocks are
    // overwritten!
    logger.info("Returning the pnmlString");
    return pnmlResponse.getResponse();
  }
}
