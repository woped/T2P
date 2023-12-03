FROM amazoncorretto:17.0.9-alpine3.17

RUN apt-get update \
    && apt-get install -y wget \
    && apt-get install -y tar

ENV WORDNET_HOME="/NLPTools/WordNet-3.0"
ENV WORDNET2_HOME="/NLPTools/WordNet-2.1"

RUN mkdir /NLPTools \
    && mkdir /NLPTools/WordNet

WORKDIR /NLPTools
RUN wget http://wordnetcode.princeton.edu/3.0/WordNet-3.0.tar.gz \
    && tar -xzvf WordNet-3.0.tar.gz
RUN wget http://wordnetcode.princeton.edu/2.1/WordNet-2.1.tar.gz \
    && tar -xzvf WordNet-2.1.tar.gz

COPY target/*.jar /app.jar
COPY target/classes/*.configuration /
COPY target/NLPTools/FrameNet /NLPTools/FrameNet

WORKDIR /
ENTRYPOINT ["java","-jar","/app.jar"]
