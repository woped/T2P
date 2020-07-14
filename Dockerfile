FROM adoptopenjdk/openjdk11:alpine-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY target/NLPTools NLPTools
ENTRYPOINT ["java","-jar","/app.jar"]