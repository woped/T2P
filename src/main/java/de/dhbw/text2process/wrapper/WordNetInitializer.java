/* This class initializes wordnet via the jwi interface.*/

package de.dhbw.text2process.wrapper;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordNetInitializer {

  // Initialize log4j to log information into the console
  Logger logger = LoggerFactory.getLogger(WordNetInitializer.class);

  String wordNetPath = System.getenv("WORDNET_HOME") + File.separator + "dict";
  // wordnet initializer instance
  private static WordNetInitializer wni;
  // dictionary instance
  private IRAMDictionary dict; // = new RAMDictionary (wnDir , ILoadPolicy.NO_LOAD );

  /**
   *
   *
   * <h1>Constructor of the WordNetInitializer</h1>
   *
   * <p>This Class is made to provide an WordNet dictionary instance wrapped into the
   * WordNetInitializer
   */
  private WordNetInitializer() {
    logger.info("Initializing WordNet dictionary ...");

    //        ApplicationHome ah = new ApplicationHome(this.getClass());
    //        wordNetPath = ah.getDir().getPath() + wordNetPath;

    logger.debug("WordNetUrl: " + wordNetPath);

    logger.info("Loading Wordnet into memory ... ");
    dict = new RAMDictionary(new File(wordNetPath), ILoadPolicy.NO_LOAD);
  }

  /**
   * Method to get an Instance of WordNetInitializer
   *
   * @return wni WordNetInitializer
   */
  public static synchronized WordNetInitializer getInstance() {
    if (wni == null) {
      synchronized (FrameNetInitializer.class) {
        if (wni == null) {
          wni = new WordNetInitializer();
          wni.init();
        }
      }
    }
    return wni;
  }

  public static synchronized void resetInstance() {
    wni = null;
  }

  public synchronized IRAMDictionary getDict() {
    return dict;
  }

  /**
   * Initialization of the actual WordNet dictionary. Loading it into the memory an providing access
   * trough the wmi object.
   */
  private synchronized void init() {

    try {
      // construct the dictionary object and open it
      logger.debug("Opening dictionary");
      dict.open();

      long t = System.currentTimeMillis();
      // loading into memory

      // dict.load(true);
      logger.info("done (" + (System.currentTimeMillis() - t) + " msec )");

    } catch (IOException ioe) {
      logger.error("Exception occured while opening the dictionary: ");
      ioe.printStackTrace();
    }
  }
}
