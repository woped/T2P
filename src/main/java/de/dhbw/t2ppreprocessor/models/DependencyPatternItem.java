package de.dhbw.t2ppreprocessor.models;

import java.util.ArrayList;
import java.util.List;

public class DependencyPatternItem {
    private List<String> dependencyTypes;
    private List<String> linkedPOSTags;

    //dependencyTypes ([<1>] OR ... OR dependencyTypes[<n>]) AND (linkedPOSTags[<1>] OR...OR linkedPOSTags[<n>])
    //empty linkedPOSTags --> Pattern is not restricted by POS

    public DependencyPatternItem(List<String> dependencyTypes, List<String> linkedPOSTags) {

        if (dependencyTypes != null ){
            this.dependencyTypes = dependencyTypes;
        }else{
            this.dependencyTypes = new ArrayList<String>();
        }
        if (linkedPOSTags != null ){
            this.linkedPOSTags = linkedPOSTags;
        }else{
            this.linkedPOSTags = new ArrayList<String>();
        }
    }

    public DependencyPatternItem(List<String> dependencyTypes) {
        this.dependencyTypes = dependencyTypes;
    }

    public List<String> getDependencyType() {
        return dependencyTypes;
    }

    public List<String> getLinkedPOSTags() {
        return linkedPOSTags;
    }
}
