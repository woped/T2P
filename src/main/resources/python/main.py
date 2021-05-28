from nltk.parse.corenlp import CoreNLPParser
from nltk.parse.corenlp import CoreNLPServer
import argparse
import os
#um alle noetigen Pakete zu holen ist das in die Kommandozeile einzugeben:
#pip install -r requirements.txt

argParser = argparse.ArgumentParser(description="A script to be used as a workaround for the usage of a newer NLP-parser")
argParser.add_argument("sent_string", help="Sentence to be used")
args = argParser.parse_args()
sentence = args.sent_string

pathname1 = os.path.join("src", "main", "resources", "python", "stanford-corenlp-4.2.1.jar")

pathname2 = os.path.join("src", "main", "resources", "python", "stanford-corenlp-4.2.1-models.jar")


#nlpServer = CoreNLPServer(path_to_jar="/Users/jannik/Documents/DHBW/5. Semester/T2P/src/main/resources/python/stanford-corenlp-4.2.1.jar",
#                          path_to_models_jar="/Users/jannik/Documents/DHBW/5. Semester/T2P/src/main/resources/python/stanford-corenlp-4.2.1-models.jar",)

nlpServer = CoreNLPServer(path_to_jar=pathname1,
                          path_to_models_jar=pathname2,)


nlpServer.start()

nlpParser = CoreNLPParser(url="http://localhost:9000")


parsed = nlpParser.parse(sentence.split())

for i in parsed:
    print(i)



#with open("hehe.txt", "w") as f:
#         for i in res:
#            f.write(str(i))

nlpServer.stop()
