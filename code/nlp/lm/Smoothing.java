package code.nlp.lm;

import java.util.*;

public class Smoothing {
    

    public double getNGramProb(NGramModel ngm, NGram ng, double lambda) {
        // TODO: check in ng through the words,
        // if we've encountered the word before in training before getting the ngram! replace the word with unk
        


        double ngramCount = ngm.getNGramCount(ng);
        double n_1gramCount = ngm.getN_1GramCount(ng);
        double finalProb = (ngramCount + lambda) / (n_1gramCount + lambda * ngm.getVocabSize());

        return finalProb;
    }

    public static void main(String[] args) {
        TrigramModel model = new TrigramModel("data/sentences");
        for (NGram key : model.n_1gram_map.keySet()) {
            for (NGram innerKey : model.n_1gram_map.get(key).keySet()) {
                System.out.println(key.getNGramArrayList() + " " + innerKey.getNGramArrayList() + " " + model.n_1gram_map.get(key).get(innerKey));
            }
        }
        ArrayList<String> test_words = new ArrayList<String>();
        test_words.add("of");
        test_words.add("the");
        test_words.add("station");
        Smoothing smoother = new Smoothing();
        System.out.println(smoother.getNGramProb(model, new NGram(test_words), 0.5));

    }

}
