package de.dhbw.t2ppreprocessor.models;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SentencePattern {

    public static final String semanticClassActivity = "ACTIVITY";
    public static final String semanticClassAction = "ACTION";
    private final static AtomicInteger counter = new AtomicInteger();

    private List<DependencyPatternItem> dependencyPatterns;
    private List<POSTagPatternItem> POSPatterns;
    private String semanticClass;
    private int id;
    private String description;

    public SentencePattern(List<DependencyPatternItem> dependencyPatterns,List<POSTagPatternItem> POSPatterns, String semanticClass){
        this.POSPatterns = POSPatterns;
        this.semanticClass = semanticClass;
        this.dependencyPatterns = dependencyPatterns;
        this.id = counter.incrementAndGet();;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getSemanticClass(){ return semanticClass;}

    public List<POSTagPatternItem> getPOSTags(){ return POSPatterns;}

    public List<DependencyPatternItem> getDependencyPaterns() {
        return dependencyPatterns;
    }

    public String toString(){
        return "Pattern ID "+id+" class "+semanticClass+": "+description;
    }
}
