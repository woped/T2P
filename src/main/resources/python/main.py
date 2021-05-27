from nltk.parse.corenlp import CoreNLPParser
from nltk.parse.corenlp import CoreNLPServer
import argparse
#um alle nötigen Pakete zu holen ist das in die Kommandozeile einzugeben:
#pip install -r requirements.txt

argParser = argparse.ArgumentParser(description="A script to be used as a workaround for the usage of a newer NLP-parser")
argParser.add_argument("sent_string", help="Sentence to be used")
args = argParser.parse_args()
sentence = args.sent_string


nlpServer = CoreNLPServer(path_to_jar="src\\main\\resources\\python\\stanford-corenlp-4.2.1.jar",
                          path_to_models_jar="src\\main\\resources\\python\\stanford-corenlp-4.2.1-models.jar",)

nlpServer.start()

nlpParser = CoreNLPParser(url="http://localhost:9000")


parsed = nlpParser.parse(sentence.split())

for i in parsed:
    print(i)



#with open("hehe.txt", "w") as f:
#         for i in res:
#            f.write(str(i))

nlpServer.stop()
