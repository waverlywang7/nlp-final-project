package code.nlp.lm;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class NGramModel {
    
   // HashMap<String, Double> unigram_map = new HashMap<String, Double>(); // includes all words that show up 0 times or more
    ArrayList<String> unigram_vocab = new ArrayList<String>(); //the words that occur at least 2 times,(the number of unique words including UNK) 
    HashMap<NGram, Double> ngram_map = new HashMap<NGram, Double>();
    HashMap<NGram, HashMap<NGram, Double>> n_1gram_map = new HashMap<NGram, HashMap<NGram, Double>>();

    public abstract void trainModel();

    public double getNGramCount(NGram ng) {

     
        if(ngram_map.keySet().contains(ng)) {
            return ngram_map.get(ng);
        }
        else {
            return 0.0;
        }
        
    }

    // to return count(aa) pass in the ngram "aad"
    public double getN_1GramCount(NGram ng) {
        NGram n_1gram = ng.getN_1Gram();
        HashMap<NGram, Double> nestedMap = n_1gram_map.get(n_1gram);
        double sum = 0.0;
        for(NGram key : nestedMap.keySet()) {
            sum += nestedMap.get(key);
        }
        return sum;
    }

    public double getVocabSize() {
        return (double) this.unigram_vocab.size();
    }

}
