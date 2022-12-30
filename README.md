# Text to Process (T2P)
This application is tailored to handle all requests from WoPeD to generate a PNML-String from a given Text.

# Live demo
| URL           | Description   | 
| ------------- |:-------------:|
| https://woped.dhbw-karlsruhe.de/t2p/ | Embedded UI|
| https://woped.dhbw-karlsruhe.de/t2p/swagger-ui/| Swagger UI|

# Related repositories
| URL           | Description   |
| ------------- |:-------------:|
| https://github.com/tfreytag/P2T | Process2Text Webservice |
| https://github.com/tfreytag/WoPeD | WoPeD-Client |

# Resources
| URL           | Description   |
| ------------- |:-------------:|
| https://hub.docker.com/r/woped/text2process | Docker Hub|

# Requirements for development
- <a href="https://aws.amazon.com/de/corretto/">OpenJDK 11</a> or higher
- <a href="https://maven.apache.org/">Apache Maven</a>
- <a href="https://git-scm.com/">Git</a>
- <a href="https://www.jetbrains.com/de-de/idea/">IntelliJ IDEA</a>
- <a href="https://wordnet.princeton.edu/download">WordNet</a>

# Testing
### Testing via Swagger UI
1. Start the application.
2. Navigate to `http://localhost:8081/t2p/swagger-ui.`
3. Insert your business process description in the body of the `POST /t2p/generatePNML` endpoint.

### Testing via the embedded GUI
1. Start the application.
2. Navigate to `http://localhost:8081/t2p/`.
3. Insert your business process description into the second text area and click on `generate`.

### Testing via the WoPeD-Client
1. Start the application.
2. Follow the installation instructions of the WoPeD-Client (`https://github.com/tfreytag/WoPeD`).
3. Start WoPeD-Client and.
4. Open the configuration and navigate to `NLP Tools`. Adapt the `Text2Process` configuration:
    - `Server host`: `localhost`
    - `Port`: `8081`
    - `URI`: `/t2p`
5. Test your configuration.
6. Close the configuration.
7. Navigate to `Analyse` -> `Translate to process model` and execute. The text will now be transformed by your locally started T2P webservice.

# Hosting the webservice yourself
### Option 1: Use our pre-build docker image
1. Pull our pre-build docker image from docker hub (see above).
2. Run this image on your server.
### Option 2: Build the docker image yourself
1. Build your own docker image with the Dockerfile.
2. Run this image on your server.

# Dependencies
This repository uses jars that are unavailable on Maven central. Hence, these jar files are stored in this repository in
the folder `lib`. The chosen procedure was described in this [SO answer](https://stackoverflow.com/a/51647143/11711692).

# Formatting
To check the formatting of all Java files, run `mvn spotless:check`. <br>
If formatting are identified, run `mvn spotless:apply` to automatically reformat that affected files.

# Configuration guide
<h3>Set a environment variable WORDNET_HOME</h3>
<p>
You need to configure an environment variable on your system to make sure the T2P-WebService can find the WordNet dictionary. Therefore we use WORDNET_HOME the same way you are familiar with from JAVA_HOME.
</p>
<p>
<h4>Windows example</h4>
WORDNET_HOME=C:\Program Files (x86)\WordNet\2.1
</p>
<h3>Starting the Application</h3>
<p>
Finally it is time to give it a try.
You can find the main file in the source package.
The are two ways to start the application.
On the one hand you can click on the play button at the upper right connor in your IDE or on the other it is possible to run it by clicking right on the WoPeDText2ProcessApplication-Class.
</p>
<img src="./img/start_server_1.PNG">
<img src="./img/start_server_2.PNG">
<p>
Maven will automatically compile the source code to a runnable application. After that the SpringBootServer will start and load the configuration given by the application.properties file.
After a short time of loading the server will listen to the port and root path you configured.
</p>

# Impementation in your Sourcecode (e. g. Java-Call)

The first thing you need to know, the api is using a Rosponse-Object (/src/main/java/de/dhbw/text2process/helper/Response.java)

The controller is located here (/src/main/java/de/dhbw/text2process/controller/T2PController.java)

This is converted to a json String. If there are any errors during the NLP processing the exceptions and the stacktrace are stored in this generic Response<E> as well as the correct result.

Use getResponse() to extract the generated process.

If you want to call the API then use the Swagger-UI mentions above to identify the correct URI. You will get 

## Java impementation

This is what you have to do in Java to implement the T2P-Interface to your code:

```java
BufferedReader bufferedReader = new BufferedReader(
	new InputStreamReader(connection.getInputStream())
);

StringBuilder responseJson = new StringBuilder();
String responseLine;
// Reading the incoming json line by line and transforming it to a single String
while ((responseLine = bufferedReader.readLine()) != null) {
	responseJson.append(responseLine.trim());
}
String processModelXmlString = responseJson.toString();
```

Now you can do what ever you like with the String in your Java application.

