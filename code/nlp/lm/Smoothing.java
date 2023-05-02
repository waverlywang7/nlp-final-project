package code.nlp.lm;

import java.util.*;

public class Smoothing {
    

    public double getNGramProb(NGramModel ngm, NGram ng, double lambda) {
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
            // check if word has not been seen in training (in unigram_vocab)
            if (!ngm.unigram_map.containsKey(word)){
                // replace the word with UNK
                new_ng_list.add("<UNK>");
            }else{
                new_ng_list.add(word);
            }
        }

        NGram new_ng = new NGram(new_ng_list); 


        // if bigram exists, calculate the bigram prob using count of bigram and count
        // of bigrams that start with first word
        if (ngm.ngram_map.containsKey(new_ng))  {
        
        double ngram_count = ngm.ngram_map.get(new_ng); // for trigram 'bac' find count(ba) get how many times the bigram shows up // TODO: replace with getNgramCount(ng) later... once working..


        // TODO: replace this with double sum = getn_1gram(ng) later once we get it working. For trigram, count(bax)
        NGram n_1gram = new_ng.getN_1Gram();
        HashMap<NGram, Double> nestedMap = ngm.n_1gram_map.get(n_1gram);
        System.out.println(ngm.n_1gram_map + "n_1gram_map");
        System.out.println(n_1gram.getClass()+ "n_1gram");
        
        double sum = 0.0; // n_1gram sum
        for(NGram key : nestedMap.keySet()) {
            
            sum += nestedMap.get(key);
        }
        // TODO: above

        return (ngram_count - discount)/ sum; // add discount

        } else {

        double prob = 0; // final prob

        // if bigram has never been encountered, then calculate bigramprob differently
        NGram n_1gram = new_ng.getN_1Gram();
        double unique_bigrams = ngm.n_1gram_map.get(n_1gram).size(); // how many unique words start with b 
        
        double total_n_1gram_count = 0.0; 
        double denominator = 1.0;
        // count tokens in the training text
        int total_tokens_count = 0;
        for (String unigram_word : ngm.unigram_map.keySet()) {
            total_tokens_count += ngm.unigram_map.get(unigram_word);
        }

        // count the total times a bigram start with first word in bigram. For "bac", count(ba)
        for (NGram next_word : ngm.n_1gram_map.get(n_1gram).keySet()) {
            total_n_1gram_count += ngm.n_1gram_map.get(n_1gram).get(next_word);  // TODO: replace with total_n_1gram_count with ngm.getn_1gramcount(ng) once working
            
            ArrayList<String> word_list = next_word.getNGramArrayList(); // extract string in next_word
            
            denominator -= ngm.unigram_map.get(word_list.get(0)) / total_tokens_count; // calculate the deoniminator of alpha
        } 
        double reserved_mass = unique_bigrams * discount / total_n_1gram_count;
        double alpha = reserved_mass / denominator;

        
        // if the ng is a bigram, ba then calculate alpha * (prob(a) / of number of tokens)
        if (new_ng_list.size() ==  2) {
        
            String last_word = new_ng_list.get(-1); // get last_word in n_gram
            double prob_next_word = ngm.unigram_map.get(last_word) / total_tokens_count;  
            prob = alpha * prob_next_word; 

        } else if (new_ng_list.size() ==  3) {
        //for trigram "bac", we will mutltiply alpha * p(c | a) = count(a,c)/ count(a)

            // get (a, c) out of the new_ng_list (b, a, c)
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

            prob = alpha * (sum/count_secondword); //for trigram "bac", we will mutltiply alpha * p(c | a) = count(a,c)/ count(a)



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
        System.out.println(smoother.getNGramProb(tm, new NGram(test_words), 1.0));    

    }

}
