package code.nlp.lm;

import java.util.ArrayList;
import java.util.*;

public class NGram {
    
    private int n;
    private ArrayList<String> words = new ArrayList<String>();

    /**
	 * Given a list of words, add words to arraylist of words
	 * 
	 * @param ngram a list of words
	 * set size of ngram
     * add words to word list
	 */
    public NGram(List<String> ngram) {
        n = ngram.size();
        for(String word : ngram) {
            words.add(word);
        }
    }

    /**
	 * Given on word, add one
	 * 
	 * @param word a list of words
	 * set size of ngram
     * add words to wordlist
	 */
    public NGram(String word) {
        n = 1;
        words.add(word);
    }


    /**
	 * Get the size of the ngram
	 * 
	 * @return the size of the ngram
	 */
    public int getN() {
        return n;
    }

    /**
	 * when called on the ngram will return the arraylist
	 * 
	 * @return the list of words which is the ngram
	 */
    public ArrayList<String> getNGramArrayList() {
        return words;
    }

    /**
	 * when called on ngram will chop off the last word and return ngram with every word except last word
	 * 
	 * @return ngram object with every word in ngram except last word
	 */
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


    /**
	 * turns ngram into a string
	 * 
	 * @return string version of ngram
	 */
    @Override
    public String toString() {
        String wordsString = "";
        for(String word : words) {
            wordsString += word + " ";
        }
        return wordsString.substring(0, wordsString.length() - 1);
    }

    /**
	 * checks to make sure ngram is equal
	 * 
	 * @return boolean to tell if ngram is equal to another ngram
	 */
    @Override
    public boolean equals(Object o) {
        if(o instanceof NGram) {
            return this.words.equals(((NGram) o).getNGramArrayList());
        }
        else {
            return false;
        }
    }

    /**
	 *  for the sake of hashing we need any two objects containing the same stuff to hash to the same thing
	 * 
	 * @return hashcode of ngram 
	 */
    @Override
    public int hashCode() {
        return words.hashCode() * n;
    }
}
