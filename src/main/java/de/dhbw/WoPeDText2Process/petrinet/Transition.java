package de.dhbw.WoPeDText2Process.petrinet;

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

import de.dhbw.WoPeDText2Process.petrinet.processors.IDHandler;
import de.dhbw.WoPeDText2Process.petrinet.processors.IPertiNetElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Transition extends PetriNetElement implements IPertiNetElement {

    Logger logger = LoggerFactory.getLogger(Transition.class);

    private String roleName, organizationalUnitName = "all", resourceName;
    private String idGateway;
    private int textPositionX = 0;
    private int textPositionY = 0;
    private int transPositionX = 0;
    private int transPositionY = 0;
    private int triggerPositionX = 0;
    private int triggerPositionY = 0;
    private int resourcePositionX = 0;
    private int resourcePositionY = 0;
    private int actorPositionX = 0;
    private int actorPositionY = 0;
    private int dimensionX = 40;
    private int dimensionY = 40;
    private int triggerType = 200;
    private int triggerDimensionX = 24;
    private int triggerDimensionY = 22;
    private int resourceDimensionX = 60;
    private int resourceDimensionY = 22;
    private int operatorType;
    private int orientationCode=1;
    private boolean hasResource = false, isGateway = false; //hasResource bezieht sich auf die Rolle, nicht auf die Ressource

    public Transition(String text, boolean hasResource, boolean isGateway, String originID, IDHandler idHandler) {
        super(originID, idHandler);
        this.text = text;
        this.hasResource = hasResource;
        this.isGateway = isGateway;

        // Set Id of transition
        this.ID = "t" + IDCounter;
    }

    public void setOrientationCode(int orientationCode) {
        this.orientationCode = orientationCode;
    }

    public void setPartOfGateway(int subID,String transID){
        idGateway=transID;
        this.ID=transID+"_op_"+subID;
    }

    // getter and setter for role
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    // getter and setter for organizational unit
    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }

    public void setOrganizationalUnitName(String organizationalUnitName) {
        this.organizationalUnitName = organizationalUnitName;
    }

    // getter and setter for resource name
    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    // getter and setter for type of trigger e.g. trigger type 200 for roles
    public int getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }

    @Override
    public void generateXmlString() {
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;

        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();

            logger.debug("Create a transition node ...");
            Element transitionTag;
            transitionTag = doc.createElement("transition");
            transitionTag.setAttribute("id", ID);
            doc.appendChild(transitionTag);

            logger.debug("Create a name node ...");
            Element name = doc.createElement("name");
            transitionTag.appendChild(name);

            logger.debug("Create a text node ...");
            Element text = doc.createElement("text");
            text.appendChild(doc.createTextNode(this.text));
            name.appendChild(text);

            logger.debug("Create a graphics node for text ...");
            Element graphicsOfText = doc.createElement("graphics");
            name.appendChild(graphicsOfText);

            logger.debug("Create an offset node ...");
            Element offsetOfText = doc.createElement("offset");
            offsetOfText.setAttribute("x", "" + textPositionX);
            offsetOfText.setAttribute("y", "" + textPositionY);
            graphicsOfText.appendChild(offsetOfText);

            logger.debug("Create a graphics node for transition ...");
            Element graphicsOfTrans = doc.createElement("graphics");
            transitionTag.appendChild(graphicsOfTrans);

            logger.debug("Create a position node for trasition graphics...");
            Element position = doc.createElement("position");
            position.setAttribute("x", "" + transPositionX);
            position.setAttribute("y", "" + transPositionY);
            graphicsOfTrans.appendChild(position);

            logger.debug("Create a dimension node for transition graphics...");
            Element dimension = doc.createElement("dimension");
            dimension.setAttribute("x", "" + dimensionX);
            dimension.setAttribute("y", "" + dimensionY);
            graphicsOfTrans.appendChild(dimension);

            logger.debug("Create a toolspecific node for transition ...");
            Element toolSpecific = doc.createElement("toolspecific");
            toolSpecific.setAttribute("tool", "WoPeD");
            toolSpecific.setAttribute("version", "1.0");
            transitionTag.appendChild(toolSpecific);

            logger.debug("Check wether there is a Gateway or not");
            if (isGateway == true) {

                logger.debug("\tGateway detected");
                logger.debug("\tCreate an operator node for toolspecific ...");
                Element operator = doc.createElement("operator");
                operator.setAttribute("id", idGateway);
                operator.setAttribute("type", "" + getOperatorType());
                toolSpecific.appendChild(operator);
            }

            logger.debug("Check wether there is a Resource or not");
            if (hasResource == true) {
                logger.debug("\tResource detected");
                logger.debug("\tCreate a trigger node for toolspecific ...");
                Element trigger = doc.createElement("trigger");
                trigger.setAttribute("id", "");
                trigger.setAttribute("type", "" + triggerType);
                toolSpecific.appendChild(trigger);

                logger.debug("\tCreate a graphics node for toolspecific trigger ...");
                Element graphicsOfTrigger = doc.createElement("graphics");
                trigger.appendChild(graphicsOfTrigger);

                logger.debug("\tCreate a position node for toolspecific trigger graphics...");
                Element positionOfTrigger = doc.createElement("position");
                positionOfTrigger.setAttribute("x", "" + triggerPositionX);
                positionOfTrigger.setAttribute("y", "" + triggerPositionY);
                graphicsOfTrigger.appendChild(positionOfTrigger);

                logger.debug("\tCreate a dimension node for toolspecific trigger graphics...");
                Element dimensionOfTrigger = doc.createElement("dimension");
                dimensionOfTrigger.setAttribute("x", "" + triggerDimensionX);
                dimensionOfTrigger.setAttribute("y", "" + triggerDimensionY);
                graphicsOfTrigger.appendChild(dimensionOfTrigger);

                logger.debug("\tCreate a transitionResource node for toolspecific ...");
                Element transResource = doc.createElement("transitionResource");
                transResource.setAttribute("roleName", roleName);
                transResource.setAttribute("organizationalUnitName", organizationalUnitName);
                toolSpecific.appendChild(transResource);

                logger.debug("\tCreate a graphics node for toolspecific transitionResource ...");
                Element graphicsOfResource = doc.createElement("graphics");
                transResource.appendChild(graphicsOfResource);

                logger.debug("\tCreate a position node for toolspecific transitionResource graphics ...");
                Element positionOfResource = doc.createElement("position");
                positionOfResource.setAttribute("x", "" + resourcePositionX);
                positionOfResource.setAttribute("y", "" + resourcePositionY);
                graphicsOfResource.appendChild(positionOfResource);

                logger.debug("\tCreate a dimension node for toolspecific transitionResource graphics ...");
                Element dimensionOfResource = doc.createElement("dimension");
                dimensionOfResource.setAttribute("x", "" + resourceDimensionX);
                dimensionOfResource.setAttribute("y", "" + resourceDimensionY);
                graphicsOfResource.appendChild(dimensionOfResource);

            }

            logger.debug("Create a time node for toolspecific ...");
            Element time = doc.createElement("time");
            time.appendChild(doc.createTextNode("0"));
            toolSpecific.appendChild(time);

            logger.debug("Create a timeUnit node for toolspecific ...");
            Element timeUnit = doc.createElement("timeUnit");
            timeUnit.appendChild(doc.createTextNode("1"));
            toolSpecific.appendChild(timeUnit);

            logger.debug("Create a orientation node for toolspecific ...");
            Element orientation = doc.createElement("orientation");
            orientation.appendChild(doc.createTextNode("" + orientationCode));
            toolSpecific.appendChild(orientation);

            // Transform Document to XML String
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            // Get the String value of final xml document
            setXmlString(writer.getBuffer().toString());

            logger.debug("Remove the xml meta node");
            setXmlString(getXmlString().replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim());

            //TODO Bad Design -> improve
            setXmlString(getXmlString().substring(getXmlString().indexOf('\n')+1));

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    public int getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(int operatorType) {
        this.operatorType = operatorType;
    }

    public String getIdGateway() {
        return idGateway;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "roleName='" + roleName + '\'' +
                ", organizationalUnitName='" + organizationalUnitName + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", idGateway='" + idGateway + '\'' +
                ", textPositionX=" + textPositionX +
                ", textPositionY=" + textPositionY +
                ", transPositionX=" + transPositionX +
                ", transPositionY=" + transPositionY +
                ", triggerPositionX=" + triggerPositionX +
                ", triggerPositionY=" + triggerPositionY +
                ", resourcePositionX=" + resourcePositionX +
                ", resourcePositionY=" + resourcePositionY +
                ", actorPositionX=" + actorPositionX +
                ", actorPositionY=" + actorPositionY +
                ", dimensionX=" + dimensionX +
                ", dimensionY=" + dimensionY +
                ", triggerType=" + triggerType +
                ", triggerDimensionX=" + triggerDimensionX +
                ", triggerDimensionY=" + triggerDimensionY +
                ", resourceDimensionX=" + resourceDimensionX +
                ", resourceDimensionY=" + resourceDimensionY +
                ", operatorType=" + operatorType +
                ", orientationCode=" + orientationCode +
                ", hasResource=" + hasResource +
                ", isGateway=" + isGateway +
                "} " + super.toString();
    }
}

