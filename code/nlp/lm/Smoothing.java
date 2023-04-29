package code.nlp.lm;

import java.util.*;

public class Smoothing {
    

    public double getNGramProb(NGramModel ngm, NGram ng, double lambda) {

        ArrayList<String> ng_list = ng.getNGramArrayList(); 
        
        ArrayList<String> new_ng_list = new ArrayList<String>();
        // go thru words in ngram, and check if the word was seen in unigram_vocab, if not, replace the word with unk
        for (String word : ng_list) {
            // check if word has not been seen in training (in unigram_vocab)
            if (!ngm.unigram_map.containsKey(word)){
                // replace the word with UNK
                new_ng_list.add("<UNK>");
            }else{
                new_ng_list.add(word);
            }
        }

        NGram new_ng = new NGram(new_ng_list); 

        double ngramCount = ngm.getNGramCount(new_ng);
        double n_1gramCount = ngm.getN_1GramCount(new_ng);
        double finalProb = (ngramCount + lambda) / (n_1gramCount + lambda * ngm.getVocabSize());

        return finalProb;
    }

    public static void main(String[] args) {
        BigramModel model = new BigramModel("data/sentences");
        for (NGram key : model.n_1gram_map.keySet()) {
            for (NGram innerKey : model.n_1gram_map.get(key).keySet()) {
                System.out.println(key.getNGramArrayList() + " " + innerKey.getNGramArrayList() + " " + model.n_1gram_map.get(key).get(innerKey));
            }
        }
        ArrayList<String> test_words = new ArrayList<String>();
        test_words.add("the");
        test_words.add("station");
        Smoothing smoother = new Smoothing();
        System.out.println(smoother.getNGramProb(model, new NGram(test_words), 0.5));

    }

}
