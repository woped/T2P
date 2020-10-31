package de.dhbw.text2process.wrapper;

import de.saar.coli.salsa.reiter.framenet.DatabaseReader;
import de.saar.coli.salsa.reiter.framenet.FNDatabaseReader;
import de.saar.coli.salsa.reiter.framenet.FrameNet;
import de.saar.coli.salsa.reiter.framenet.fncorpus.AnnotationCorpus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;


public class FrameNetInitializer {
    // Initialize log4j to log information into the console
    Logger logger = LoggerFactory.getLogger(FrameNetInitializer.class);
    //framenet source directory
    public static String frameNetHome = "/NLPTools/FrameNet/fndata-1.5/";
    //framenet initializer instance
    private static FrameNetInitializer fni;
    //framenet instance (dictionary)
    private FrameNet f_frameNet;
    //reduced annotation corpus
    private AnnotationCorpus f_corpus;
    // TODO: prüfen ob weiterhin benötigt:
    private boolean generateButton = false;

    private FrameNetInitializer (){
        logger.debug("Initializing FrameNet ...");
        f_frameNet = new FrameNet();

        ApplicationHome ah = new ApplicationHome(this.getClass());
        String path = ah.getDir().getPath();

        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e2) {

            e2.printStackTrace();
        }

        frameNetHome = path + frameNetHome;
        logger.info("Reading FrameNet Path as: " + frameNetHome);
    }

    //getter
    public synchronized static FrameNetInitializer getInstance(){
        if(fni == null){
            synchronized (FrameNetInitializer.class) {
                if(fni == null){
                    fni = new FrameNetInitializer();
                    fni.init();
                }
            }
        }
        return fni;
    }

    public synchronized static void resetInstance(){
        fni=null;
    }

    public synchronized FrameNet getFN() {
        return f_frameNet;
    }
    public synchronized AnnotationCorpus getCorpus() {
        return f_corpus;
    }
    public synchronized boolean getGenrateButton(){
        return generateButton;
    } // TODO: prüfen ob weiterhin benötigt

    public synchronized void init() {
        try {

            //start time for tracking tracking
            long _start = System.currentTimeMillis();

            // Reading FrameNet
            DatabaseReader reader = new FNDatabaseReader(new File(frameNetHome), false);
            f_frameNet.readData(reader);
            java.util.logging.Logger _l = java.util.logging.Logger.getLogger("this");
            _l.setLevel(Level.SEVERE);

            //logging loading time
            long _annoStart = System.currentTimeMillis();
            logger.info("Loaded FrameNet in: "+(_annoStart-_start)+"ms");

            //reading valence patterns from reduced corpus
            f_corpus = new AnnotationCorpus(f_frameNet,_l);
            f_corpus.setScanSubCorpuses(false);
            f_corpus.parse(new File(frameNetHome +"lu"));

            //logging loading time
            logger.info("Loaded FrameNet-Annotations in: "+(System.currentTimeMillis()-_annoStart)+"ms");
            generateButton = true;

        } catch (Exception ex) {
            logger.error("could not initialize FrameNetWrapper: "+ex.getMessage());
            ex.printStackTrace();
        }
    }

}
