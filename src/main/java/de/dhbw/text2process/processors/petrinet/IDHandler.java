package de.dhbw.text2process.processors.petrinet;

public class IDHandler {
    private int  id;
    public IDHandler(int startID){
     id=startID;
    }

    public int getNext(){
        return id++;
    }

}
