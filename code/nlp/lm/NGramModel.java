package code.nlp.lm;

import java.util.HashMap;

public abstract class NGramModel {
    
    HashMap<NGram, Double> ngram_map;
    HashMap<NGram, Double> n_1gram_map;

    public abstract void trainModel();

}
