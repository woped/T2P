package de.dhbw.WoPeDT2PPrePro;
import de.dhbw.t2ppreprocessor.Controller.T2PPreProcessorController;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class STT2PPrePro extends T2PScenarioTest{
    private static String [] TestExamples ={"ST_Ressource_Computer_Repair.xml","ST_Resource_Lemon_Chicken_Recipe.xml"};
    @Test
    public void test(){
        filePath = System.getProperty("user.dir");
        filePath = filePath + "/src/test/resources/";
        int counter = 1;
        startPerformanceTrace();
        for(String testCase: TestExamples){
            parseTestFile(testCase);
            printInfo(counter,testCase);
            T2PPreProcessorController controller = new T2PPreProcessorController(getPlainTextDescription());
            controller.preprocessTextToText();
        }
        System.out.println("executed "+TestExamples.length+" test cases in "+endPerformanceTrace()+" milliseconds");

    }

}
