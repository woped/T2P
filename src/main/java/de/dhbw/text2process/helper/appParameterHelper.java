package de.dhbw.text2process.helper;

import java.io.*;
import java.util.Properties;

/**
    * @author <a href="mailto:lamers.alexander@student.dhbw-karlsruhe.de">Alexander Lamers</a>
    * @author <a href="mailto:wolf.moritz@student.dhbw-karlsruhe.de">Moritz Wolf</a>
 */

public class appParameterHelper {

    public static Properties GetConfigFile(){

        try (InputStream input = new FileInputStream("src/main/resources/appParameter.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            System.out.println(prop);

            return prop;

        } catch (IOException io) {
            //io.printStackTrace();
            return null;
        }
    }

}
