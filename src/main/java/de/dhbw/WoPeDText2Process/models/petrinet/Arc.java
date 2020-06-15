package de.dhbw.WoPeDText2Process.models.petrinet;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import de.dhbw.WoPeDText2Process.processors.petrinet.IDHandler;
import de.dhbw.WoPeDText2Process.processors.petrinet.IPertiNetElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Arc extends PetriNetElement implements IPertiNetElement {

    Logger logger = LoggerFactory.getLogger(Arc.class);

    private double offsetX = 500.0, offsetY = -12.0;
    private String source, target;

    public Arc(String source, String target, String originID, IDHandler idHandler) {
        super(originID, idHandler);
        text = "1";
        ID = "a" + IDCounter;
        this.source = source;
        this.target = target;
    }


    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public void generateXmlString() {
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();

            logger.debug("Create an arc node ...");
            Element arcTag = doc.createElement("arc");
            arcTag.setAttribute("id", ID);
            arcTag.setAttribute("source", source);
            arcTag.setAttribute("target", target);
            doc.appendChild(arcTag);

            logger.debug("Create an inscription node ...");
            Element inscription = doc.createElement("inscription");
            arcTag.appendChild(inscription);

            logger.debug("Create a text node ...");
            Element textofArc = doc.createElement("text");
            textofArc.appendChild(doc.createTextNode(text));
            inscription.appendChild(textofArc);

            logger.debug("Create a graphics node ...");
            Element graphics = doc.createElement("graphics");
            inscription.appendChild(graphics);

            logger.debug("Create an offset node ...");
            Element offset = doc.createElement("offset");
            offset.setAttribute("x", ""+offsetX);
            offset.setAttribute("y", ""+offsetY);
            graphics.appendChild(offset);

            logger.debug("Create a toolspecific node ...");
            Element toolSpecific = doc.createElement("toolspecific");
            toolSpecific.setAttribute("tool", "WoPeD");
            toolSpecific.setAttribute("version", "1.0");
            arcTag.appendChild(toolSpecific);

            logger.debug("Create a probability node ...");
            Element probability = doc.createElement("probability");
            probability.appendChild(doc.createTextNode("1.0"));
            toolSpecific.appendChild(probability);

            logger.debug("Create a displayProbabilityOn node ...");
            Element displayProbabilityOn = doc.createElement("displayProbabilityOn");
            displayProbabilityOn.appendChild(doc.createTextNode("false"));
            toolSpecific.appendChild(displayProbabilityOn);

            logger.debug("Create a displayProbabilityPosition node ...");
            Element displayProbabilityPosition = doc.createElement("displayProbabilityPosition");
            displayProbabilityPosition.setAttribute("x", "500.0");
            displayProbabilityPosition.setAttribute("y", "12.0");
            toolSpecific.appendChild(displayProbabilityPosition);

            // Transform Document to XML String
            logger.debug("Initiate a new TransformerFactory instance.");
            TransformerFactory tf = TransformerFactory.newInstance();
            logger.debug("Get a new Transformer from the TransformerFactory.");
            Transformer transformer = tf.newTransformer();
            logger.debug("Initiate a new StringWriter.");
            StringWriter writer = new StringWriter();
            logger.debug("Transforming the doc into a String using the transformer object.");
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            // Get the String value of final xml document
            logger.debug("Remove the xml meta node");
            String xmlString = writer.getBuffer().toString().replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();

            //TODO Bad Design -> improve
            xmlString = xmlString.substring(xmlString.indexOf('\n')+1);
            setXmlString(xmlString);

        } catch (TransformerConfigurationException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (TransformerException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Arc{" +
                "offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                "} " + super.toString();
    }
}
