package de.dhbw.text2process.wrapper;

import de.dhbw.text2process.models.worldModel.Text;
import de.dhbw.text2process.models.worldModel.T2PSentence;
import de.dhbw.text2process.processors.worldmodel.processing.ITextParsingStatusListener;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StanfordParserFunctionality {

	Logger logger = LoggerFactory.getLogger(StanfordParserFunctionality.class);
	
    private final StanfordParserInitializer SPInitializer = StanfordParserInitializer.getInstance();
    private final DocumentPreprocessor dpp;
    private final GrammaticalStructureFactory gsf;
    private final LexicalizedParser parser;
    private final TreebankLanguagePack tlp;

    private static StanfordParserFunctionality instance;

    public StanfordParserFunctionality(){
        dpp = SPInitializer.getDpp();
        gsf = SPInitializer.getGsf();
        parser = SPInitializer.getParser();
        tlp = SPInitializer.getTlp();
    }

    public synchronized static StanfordParserFunctionality getInstance(){
        if(instance==null){
            instance=new StanfordParserFunctionality();
        }
        return instance;
    }

    public synchronized static void resetInstance(){
        instance=null;
    }

    public synchronized Text createText(String input) throws IOException {
        return createText(input, null);
    }
    
    public Text createText(File file) throws IOException{
		return createText(file, null);
	}

    public synchronized Text createText(String input, ITextParsingStatusListener listener) throws IOException {
        Text _result = new Text();

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<List<? extends HasWord>> _sentences = dpp.getSentencesFromText(reader);

        if(listener != null) listener.setNumberOfSentences(_sentences.size());
        int _sentenceNumber = 1;
        int sentenceOffset = 0;
        for(List<? extends HasWord> _sentence:_sentences){
            if(_sentence.get(0).word().equals("#")) {
                //comment line - skip
                if(listener != null) listener.sentenceParsed(_sentenceNumber++);
                sentenceOffset += ((Word)_sentence.get(_sentence.size()-1)).endPosition();
                continue;
            }
            ArrayList<Word> _list = new ArrayList<Word>();
            for(HasWord w:_sentence){
                if(w instanceof Word){
                    _list.add((Word) w);
                }else{
                    logger.error("Error occured while creating a Word!");
                }
            }
            T2PSentence _s = createSentence(_list);
            _s.setCommentOffset(sentenceOffset);
            _result.addSentence(_s);
            if(listener != null) listener.sentenceParsed(_sentenceNumber++);
        }
        return _result;
    }
    
    public Text createText(File file, ITextParsingStatusListener listener) throws IOException{
		Text _result = new Text();
        
		List<List<? extends HasWord>> _sentences = dpp.getSentencesFromText(file.getAbsolutePath());
		if(listener != null) listener.setNumberOfSentences(_sentences.size());
		int _sentenceNumber = 1;
		int sentenceOffset = 0;
		for(List<? extends HasWord> _sentence:_sentences){
			if(_sentence.get(0).word().equals("#")) {
				//comment line - skip
				if(listener != null) listener.sentenceParsed(_sentenceNumber++);
				sentenceOffset += ((Word)_sentence.get(_sentence.size()-1)).endPosition();
				continue;
			}
			ArrayList<Word> _list = new ArrayList<Word>();
			for(HasWord w:_sentence){
				if(w instanceof Word){
					_list.add((Word) w);
				}else{
					System.out.println("Error occured while creating a Word!");
				}
			}
			T2PSentence _s = createSentence(_list);
			_s.setCommentOffset(sentenceOffset);
			_result.addSentence(_s);	
			if(listener != null) listener.sentenceParsed(_sentenceNumber++);				
		}
		return _result;
}



    private T2PSentence createSentence(ArrayList<Word> _list) throws IOException {
        T2PSentence t2pSentence = new T2PSentence(_list);
        String tr;
        
        logger.info("Trying to connect to the Stanford Core NLP microservice ...");
        URL url = new URL("https://woped.dhbw-karlsruhe.de/t2p-stanford");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        String jsonInputString = t2pSentence.toStringFormated();
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            tr = response.toString();
            logger.debug(tr);
        }
        Tree t = Tree.valueOf(tr);
        t2pSentence.setTree(t);
        GrammaticalStructure _gs = gsf.newGrammaticalStructure(t);
        logger.debug("_gs"+_gs);
        t2pSentence.setGrammaticalStructure(_gs);
        logger.debug("s.setGS"+t2pSentence);
        return t2pSentence;
    }

}