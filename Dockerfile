FROM adoptopenjdk/openjdk11:jre-11.0.16.1_1-ubuntu

RUN apt-get update \
    && apt-get install -y wget \
    && apt-get install -y tar


RUN mkdir /NLPTools

WORKDIR /NLPTools

COPY target/*.jar /app.jar
COPY target/classes/ /target/classes
COPY target/classes/NLPTools/FrameNet /NLPTools/FrameNet

WORKDIR /
ENTRYPOINT ["java","-jar","/app.jar"]
