package code.nlp.lm;

import java.util.*;

public class Smoothing {
    

    public double getNGramProbLambda(NGramModel ngm, NGram ng, double lambda) {
        //System.out.println(ngm.n_1gram_map + "wee"); // UH OH why isn't the n_1gram_map here... 
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

        double ngramCount = ngm.getNGramCount(new_ng); // wonder if this would work without using a getNgramCount method and just directly get the value?
        double n_1gramCount = ngm.getN_1GramCount(new_ng);
        double finalProb = (ngramCount + lambda) / (n_1gramCount + lambda * ngm.getVocabSize());

        return finalProb;
    }


    public double getNGramProbDiscount(NGramModel ngm, NGram ng, double discount) {
        ArrayList<String> ng_list = ng.getNGramArrayList(); 
            
        ArrayList<String> new_ng_list = new ArrayList<String>();
        // go thru words in ngram, and check if the word was seen in unigram_vocab, if not, replace the word with unk
        for (String word : ng_list) {
            if (!ngm.unigram_map.containsKey(word)){
                new_ng_list.add("<UNK>");
            }else{
                new_ng_list.add(word);
            }
        }

        NGram new_ng = new NGram(new_ng_list); 


        // if bigram exists, calculate the bigram prob using count of ngram and count of n_1grams
        if (ngm.ngram_map.containsKey(new_ng))  {
            double ngram_count = ngm.ngram_map.get(new_ng);
            double n_1gram_count = ngm.getN_1GramCount(new_ng); 
            return (ngram_count - discount)/ n_1gram_count;

        }

        else {

            double prob = 0; // final prob to return

            // if bigram has never been encountered, then calculate bigramprob differently
            NGram n_1gram = new_ng.getN_1Gram();
            double unique_bigrams = ngm.n_1gram_map.get(n_1gram).size(); // how many unique words start with the n_1gram
            double n_1gram_count = ngm.getN_1GramCount(new_ng); 
            double reserved_mass = unique_bigrams * discount / n_1gram_count;

            double denominator = 1.0;
            
            // if the ng is a bigram, ba then calculate alpha * (prob(a) / of number of tokens)
            if (new_ng_list.size() ==  2) {
            
                 // count tokens in the training text
                int total_tokens_count = 0;
                for (String unigram_word : ngm.unigram_map.keySet()) {
                    total_tokens_count += ngm.unigram_map.get(unigram_word);
                }

                // count the sum of unigram probabilities of words we saw in a bigram starting with the first letter of the ngram
                for (NGram last_word_as_ngram : ngm.n_1gram_map.get(n_1gram).keySet()) {
                    String last_word = last_word_as_ngram.getNGramArrayList().get(0);
                    denominator -= ngm.unigram_map.get(last_word) / total_tokens_count;
                } 
                double alpha = reserved_mass / denominator;

                String second_word = new_ng_list.get(1); // get second word in bigram
                double prob_next_word = ngm.unigram_map.get(second_word) / total_tokens_count;  
                prob = alpha * prob_next_word; 

            }
            else if (new_ng_list.size() ==  3) {

                ArrayList<String> new_ng_al = new_ng.getNGramArrayList();
                ArrayList<String> middle_ng_al = (ArrayList<String>) new_ng_al.subList(1, new_ng_al.size()-2);
                NGram middle_ng = new NGram(middle_ng_al);

                for (NGram last_word_as_ngram : ngm.n_1gram_map.get(n_1gram).keySet()) {
                    // denom -= bigram probability of last word given middle_ngram
                    String last_word_as_str = last_word_as_ngram.getNGramArrayList().get(0);
                    ArrayList<String> shifted_n_1_gram = (ArrayList<String>) middle_ng_al.clone();
                    shifted_n_1_gram.add(last_word_as_str);
                    shifted_n_1_gram.add(last_word_as_str); // add it a second time since getN_1GramCount automatically removes the last word in the ngram
                    NGram cur_ng = new NGram(shifted_n_1_gram);

                    double cur_ng_count = ngm.getN_1GramCount(cur_ng);
                    double middle_ng_count = ngm.unigram_map.get(middle_ng);

                    denominator -= cur_ng_count / middle_ng_count;
                }

                double alpha = reserved_mass / denominator;


                //for trigram "bac", we will mutltiply alpha * p(c | a) = count(a,c)/ count(a)
                List<String> list_words = new_ng_list.subList(1, 2);
                ArrayList<String> ngram_words = new ArrayList<String>();
                for (String word : list_words) {
                    ngram_words.add(word);
                }
                
                HashMap<NGram, Double> lasttwoword_map = ngm.n_1gram_map.get(ngram_words); 
                double sum = 0.0; // count(a,c)
                for(NGram key : lasttwoword_map.keySet()) {
                    
                    sum += lasttwoword_map.get(key);
                }

                String second_word = new_ng_list.get(1);
                double count_secondword = ngm.unigram_map.get(second_word); // count(a)

                prob = alpha * (sum/count_secondword);

            }

            return prob;

        }
    
    }


    public static void main(String[] args) {
        BigramModel model = new BigramModel("data/test2");
        for (NGram key : model.n_1gram_map.keySet()) {
            for (NGram innerKey : model.n_1gram_map.get(key).keySet()) {
                System.out.println(key.getNGramArrayList() + " " + innerKey.getNGramArrayList() + " " + model.n_1gram_map.get(key).get(innerKey));
            }
        }
        ArrayList<String> test_words = new ArrayList<String>();
        test_words.add("a");
        test_words.add("b");
        test_words.add("a");
        Smoothing smoother = new Smoothing();

        
        
        TrigramModel tm = new TrigramModel("data/test2");
        for (NGram key : tm.n_1gram_map.keySet()) {
            for(NGram innerKey : tm.n_1gram_map.get(key).keySet()) {
                // System.out.println("* " + innerKey);
                System.out.println(key.getNGramArrayList() + " " + innerKey.getNGramArrayList() + " " + tm.n_1gram_map.get(key).get(innerKey));
            }
        }
        System.out.println(tm.n_1gram_map + "n_1gram_map hello");  
        System.out.println(smoother.getNGramProbLambda(tm, new NGram(test_words), 1.0));    

    }

}
