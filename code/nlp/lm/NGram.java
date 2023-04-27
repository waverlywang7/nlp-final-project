package code.nlp.lm;

import java.util.ArrayList;
import java.util.*;

public class NGram {
    
    private int n;
    private ArrayList<String> words = new ArrayList<String>();

    
    public NGram(ArrayList<String> ngram) {
        n = ngram.size();
        for(String word : ngram) {
            words.add(word);
        }
    }

    public NGram getN_1Gram() {
        // subList only works for List<String> so we need to convert the subList
        // back to an ArrayList
        List<String> list_words = words.subList(0, n-1);
        ArrayList<String> ngram_words = new ArrayList<String>();
        for (String word : list_words) {
            ngram_words.add(word);
        }
        return new NGram(ngram_words);
    }

    public ArrayList<String> getNGramArrayList() {
        return this.words;
    }
}
