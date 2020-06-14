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

public class Place extends PetriNetElement implements IPertiNetElement {

    Logger logger = LoggerFactory.getLogger(Place.class);

    private int XposPlace = 0, YposPlace = 0, XposText = 0, YposText = 0, dimensionX = 40, dimensionY = 40;

    private boolean hasMarking = false;

    public Place(boolean hasMarking, String originID, IDHandler idHandler) {
        super(originID, idHandler);
        ID = "p" + IDCounter;
        text = "p" + IDCounter;
        this.hasMarking = hasMarking;
    }

    public void setHasMarking(boolean hasMarking) {
        this.hasMarking = hasMarking;
    }

    public boolean hasMarking() {
        return hasMarking;
    }

    @Override
    public void generateXmlString() {
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;

        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();

            Element placeTag = doc.createElement("place");
            placeTag.setAttribute("id", ID);
            doc.appendChild(placeTag);

            Element nameTag = doc.createElement("name");
            placeTag.appendChild(nameTag);

            Element textTag = doc.createElement("text");
            textTag.appendChild(doc.createTextNode(text));
            nameTag.appendChild(textTag);

            Element graphicsForText = doc.createElement("graphics");
            nameTag.appendChild(graphicsForText);

            Element offsetOfText = doc.createElement("offset");
            offsetOfText.setAttribute("x", "" + XposText);
            offsetOfText.setAttribute("y", "" + YposText);
            graphicsForText.appendChild(offsetOfText);

            Element graphicsForPlace = doc.createElement("graphics");
            placeTag.appendChild(graphicsForPlace);

            Element position = doc.createElement("position");
            position.setAttribute("x", "" + XposPlace);
            position.setAttribute("y", "" + YposPlace);
            graphicsForPlace.appendChild(position);

            Element dimension = doc.createElement("dimension");
            dimension.setAttribute("x", "" + dimensionX);
            dimension.setAttribute("y", "" + dimensionY);
            graphicsForPlace.appendChild(dimension);

            if (hasMarking == true) {
                Element initialMarking = doc.createElement("initialMarking");
                placeTag.appendChild(initialMarking);

                Element textOfMarking = doc.createElement("text");
                textOfMarking.appendChild(doc.createTextNode("1"));
                initialMarking.appendChild(textOfMarking);
            }

            // Transform Document to XML String
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            // Get the String value of final xml document
            logger.debug("Remove the xml meta node");
            String xmlString = writer.getBuffer().toString().replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
            //TODO Bad Design -> improve
            xmlString = xmlString.substring(xmlString.indexOf('\n')+1);
            setXmlString(xmlString);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Place{" +
                "XposPlace=" + XposPlace +
                ", YposPlace=" + YposPlace +
                ", XposText=" + XposText +
                ", YposText=" + YposText +
                ", dimensionX=" + dimensionX +
                ", dimensionY=" + dimensionY +
                ", hasMarking=" + hasMarking +
                "} " + super.toString();
    }
}


