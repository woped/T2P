FROM adoptopenjdk/openjdk11:jre-11.0.11_9-ubuntu

RUN apt-get update &&\ 
    apt-get install -y wget &&\
    apt-get install -y  tar;

ENV WORDNET_HOME="/NLPTools/WordNet-3.0"
ENV WORDNET2_HOME="/NLPTools/WordNet-2.1"

RUN mkdir /NLPTools; mkdir /NLPTools/WordNet

RUN cd /NLPTools; \
wget http://wordnetcode.princeton.edu/3.0/WordNet-3.0.tar.gz; \
tar -xzvf WordNet-3.0.tar.gz; 

RUN cd /NLPTools;\ 
wget http://wordnetcode.princeton.edu/2.1/WordNet-2.1.tar.gz; \
tar -xzvf WordNet-2.1.tar.gz;

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar
COPY target/NLPTools/FrameNet /NLPTools/FrameNet
COPY target/classes/jwnl.configuration jwnl.configuration

ENTRYPOINT ["java","-jar","/app.jar"]
