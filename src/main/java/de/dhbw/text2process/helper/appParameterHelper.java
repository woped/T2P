package de.dhbw.text2process.helper;

import java.io.*;
import java.util.Properties;

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
