package de.dhbw.t2ppreprocessor;

import ch.qos.logback.classic.net.SocketAppender;
import com.sun.source.util.SourcePositions;
import de.dhbw.t2ppreprocessor.Controller.T2PPreProcessorController;
import jdk.swing.interop.SwingInterOpUtils;

import javax.swing.plaf.synth.SynthOptionPaneUI;

public class T2PPreProcessorApplication {

    public static void main(String [] args){
        T2PPreProcessorController b = new T2PPreProcessorController("The secretary sends the file.");
        System.out.println(b.preprocessTextToText());
    }

}
