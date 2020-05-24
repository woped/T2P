package de.dhbw.WoPeDText2Process.processors.petrinet;

public class IDHandler {
    private int  id;
    public IDHandler(int startID){
     id=startID;
    }

    public int getNext(){
        return id++;
    }

}
