package de.dhbw.text2process.helper.rest;

import de.dhbw.text2process.models.meta.BPMNModel;
import de.dhbw.text2process.models.worldModel.*;
import de.dhbw.text2process.processors.worldmodel.WorldModelBuilder;
import de.dhbw.text2process.processors.bpmn.BPMNModelBuilder;
import de.dhbw.text2process.processors.petrinet.PetrinetBuilder;
import de.dhbw.text2process.Text2ProcessApplication;
import de.dhbw.text2process.exceptions.BpmnGenerationException;
import de.dhbw.text2process.exceptions.InvalidInputException;
import de.dhbw.text2process.exceptions.PetrinetGenerationException;
import de.dhbw.text2process.exceptions.WorldModelGenerationException;
import de.dhbw.text2process.helper.TextToProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import javax.lang.model.element.Element;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class T2PControllerHelper {

	// Initialize log4j to log information into the console
	Logger logger = LoggerFactory.getLogger(T2PControllerHelper.class);

	// Reject any Request larger than this
	public static final int MAX_INPUT_LENGTH = 15000;

	/**
	 * <h1>generateWorldModelFromText</h1>
	 *
	 * <p>
	 * </p>
	 *
	 * @author KanBen86
	 * @param text
	 * @return A WorldModel
	 * @throws WorldModelGenerationException
	 */
	public WorldModel generateWorldModelFromText(String text) throws WorldModelGenerationException, IOException {
		logger.debug("Instantiating the WorldModelBuilder ...");
		WorldModelBuilder worldModelBuilder = new WorldModelBuilder(text);
		WorldModel worldModel = worldModelBuilder.buildWorldModel(false);
		List<Action> actions = worldModel.getActions();
		List<Actor> actors = worldModel.getActors();
		List<Flow> elements = worldModel.getFlows();
		List<Resource> resources = worldModel.getResources();

		System.out.println(actions.toString());
		System.out.println(actors.toString());
		System.out.println(elements.toString());
		System.out.println(resources.toString());
		System.out.println();
		return worldModel;
	}

	/**
	 * <h1>generatePetrinetFromText</h1>
	 *
	 * <p>
	 * </p>
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
	 * <h1>generatePetrinetFromText</h1>
	 *
	 * <p>
	 * </p>
	 *
	 * @author
	 * @param text
	 * @return A string which represents the PetriNet
	 * @throws PetrinetGenerationException
	 */
	public File generateBpmnFileFromText(String text)
			throws BpmnGenerationException, WorldModelGenerationException, IOException {
		logger.debug("Instantiating the TextToProcess object ...");
		TextToProcess textToProcess = new TextToProcess();
		logger.debug("Instantiating the BPNMModelBuilder object with the TextToProcess object as parameter ...");
		BPMNModelBuilder bpmnBuilder = new BPMNModelBuilder(textToProcess);
		
		logger.debug("Creating a file for the Export ...");
		File bpmnFile = new File("." + File.separator + "files"  + File.separator + "bpmn_file");
		logger.debug("Set Text ...");
		textToProcess.setProcessText(text);
		logger.debug("Starting analyzing the given text ...");
		textToProcess.analyzeText(true, true, bpmnFile);
		
		return bpmnFile;
	}
	
	/**
	 * <h1>generatePetrinetFromText</h1>
	 *
	 * <p>
	 * </p>
	 *
	 * @author
	 * @param text
	 * @return A string which represents the PetriNet
	 * @throws PetrinetGenerationException
	 */
	public String generateBpmnFromText(String text)
			throws BpmnGenerationException, WorldModelGenerationException, IOException {
		logger.debug("Instantiating the TextToProcess object ...");
		TextToProcess textToProcess = new TextToProcess();
		logger.debug("Instantiating the BPNMModelBuilder object with the TextToProcess object as parameter ...");
		BPMNModelBuilder bpmnBuilder = new BPMNModelBuilder(textToProcess);
		logger.debug("Creating a new BMPN-Model ...");
		BPMNModel bpmnModel = (BPMNModel)bpmnBuilder.createProcessModel(textToProcess.getWorldModel(text));
		logger.debug("Converting BPMN model to json");
		String bpmnString = new Gson().toJson(bpmnModel);
		
		return bpmnString;
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
	 *
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
	 *
	 * @param text
	 * @throws InvalidInputException
	 */
	public void checkInputValidity(String text) throws InvalidInputException {

		if (text.length() > MAX_INPUT_LENGTH | text.equals("")) {
			throw new InvalidInputException("The input is too long.");
		}
		// Accept only characters common in a plain text in english language
		Pattern p = Pattern.compile("[^a-z0-9,./?:!\\s\\t\\n£$%&*()_\\-`]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(text);
		int count = 0;
		String message = "";
		while (m.find()) {
			count = count + 1;
			message += "position " + m.start() + ": " + text.charAt(m.start()) + "\n";
		}
		if (count > 0) {
			throw new InvalidInputException("There are " + count + " invalid characters in the input:\n" + message);
		}
	}

	/**
	 *
	 */
	public void resetNLPTools() {
		WorldModelBuilder.resetNLPTools();
	}
}
