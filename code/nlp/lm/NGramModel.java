package code.nlp.lm;

import java.util.HashMap;
import java.util.HashSet;

public abstract class NGramModel {
    
    HashSet<NGram> vocab;
    HashMap<NGram, Double> ngram_map;
    HashMap<NGram, HashMap<NGram, Double>> n_1gram_map;

    public abstract void trainModel();

    public double getNGramCount(NGram ng) {
        return ngram_map.get(ng);
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

}
