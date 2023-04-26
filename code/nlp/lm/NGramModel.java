package code.nlp.lm;

import java.util.HashMap;

public abstract class NGramModel {
    
    HashMap<String, Double> unigram_map;
    HashMap<NGram, Double> ngram_map;
    HashMap<NGram, HashMap<NGram, Double>> n_1gram_map;

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
        return (double) this.unigram_map.size();
    }

}
