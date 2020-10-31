package de.dhbw.t2ppreprocessor.models;

public class T2PPreProWord {
    private int index;
    private String word;

    public T2PPreProWord(int index, String word){
        this.index = index;
        this.word = word;
    }

    public int getIndex() {
        return index;
    }
    public String getWord(){
        return word;
    }

    public String toString(){
        return word + " index:"+index;
    }

}
