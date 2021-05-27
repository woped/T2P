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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StanfordParserFunctionality {

    private StanfordParserInitializer SPInitializer = StanfordParserInitializer.getInstance();
    private DocumentPreprocessor dpp;
    private GrammaticalStructureFactory gsf;
    private LexicalizedParser parser;
    private TreebankLanguagePack tlp;

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
        T2PSentence _s = new T2PSentence(_list);
        ProcessBuilder processBuilder = new ProcessBuilder("venv\\Scripts\\python.exe",
                "src/main/resources/python/main.py", _s.toStringFormated());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader bfr = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String tr = bfr.lines().collect(Collectors.joining());
        System.out.println("Kiwi"+tr);
        Tree t = Tree.valueOf(tr);
        //parser.getTreePrint().printTree(t);

        //String line = "";
        //while ((line = bfr.readLine()) != null) {
        //    System.out.println(line);
        //}

        _s.setTree(t);
        GrammaticalStructure _gs = gsf.newGrammaticalStructure(t);
        _s.setGrammaticalStructure(_gs);
        return _s;
    }

}