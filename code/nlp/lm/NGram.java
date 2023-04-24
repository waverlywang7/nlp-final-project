package code.nlp.lm;

import java.util.ArrayList;

public class NGram {
    
    private int n;
    private ArrayList<String> words = new ArrayList<String>();


    public NGram(ArrayList<String> ngram) {
        n = ngram.size();
        for(String word : ngram) {
            words.add(word);
        }
    }

    public ArrayList<String> getN_1Gram() {
        return (ArrayList) words.subList(0, n-1);
    }

    public ArrayList<String> getNGram() {
        return this.words;
    }
}
