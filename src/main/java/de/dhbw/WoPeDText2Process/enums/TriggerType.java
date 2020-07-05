package de.dhbw.WoPeDText2Process.enums;

public enum TriggerType {
    TRIGGER_TYPE_NOT_TRIGGERED(0),
    TRIGGER_TYPE_RESOURCE(200),
    TRIGGER_TYPE_MESSAGE(201),
    TRIGGER_TYPE_TIME(202);
    int type;

    TriggerType(int type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }
}
