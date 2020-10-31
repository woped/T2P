package de.dhbw.t2ppreprocessor.models;

import edu.mit.jwi.item.POS;

import javax.swing.*;
import java.util.List;

public class POSTagPatternItem {
    private List<String> POSTags;

    public POSTagPatternItem(List<String> POSTags){
        this.POSTags = POSTags;
    }

    public List<String> getPOSTags(){
        return POSTags;
    }

}
