package de.dhbw.WoPeDText2Process.petrinet.processors;

public class IDHandler {
    private int  id;
    public IDHandler(int startID){
     id=startID;
    }

    public int getNext(){
        return id++;
    }

}
