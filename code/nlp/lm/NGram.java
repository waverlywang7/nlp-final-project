package code.nlp.lm;

import java.util.ArrayList;
import java.util.*;

public class NGram {
    
    private int n;
    private ArrayList<String> words = new ArrayList<String>();

    
    public NGram(List<String> ngram) {
        n = ngram.size();
        for(String word : ngram) {
            words.add(word);
        }
    }

    public NGram(String word) {
        n = 1;
        words.add(word);
    }

    public int getN() {
        return n;
    }

    public ArrayList<String> getNGramArrayList() {
        return words;
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

    @Override
    public String toString() {
        String wordsString = "";
        for(String word : words) {
            wordsString += word + " ";
        }
        return wordsString.substring(0, wordsString.length() - 1);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof NGram) {
            return this.words.equals(((NGram) o).getNGramArrayList());
        }
        else {
            return false;
        }
    }

    // for the sake of hashing we need any two objects containing the same stuff to hash to the same thing
    @Override
    public int hashCode() {
        return words.hashCode() * n;
    }
}
