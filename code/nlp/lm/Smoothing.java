package code.nlp.lm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import javax.swing.UIManager;

public class Smoothing {
    
    /**
	 *  Get the ngram probability using lambda smoothing
	 * @param ngm ngram model
     * @param ng ngram
     * @param lambda lambda value
     * @return ngram probability 
     *
	 */
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

    /**
	 *  Get the ngram probability using backoff discounting
	 * @param ngm ngram model
     * @param ng ngram
     * @param lambda lambda value
     * @return ngram probability 
     *
	 */
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
            System.out.println("here instead");
            System.out.println(ngram_count);
            System.out.println(n_1gram_count);
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
                System.out.println("here");
            

                ArrayList<String> new_ng_al = new_ng.getNGramArrayList();
                List<String> middle_ng_list = new_ng_al.subList(1, new_ng_al.size()-2);

                ArrayList<String> middle_ng_al = new ArrayList<String>(middle_ng_list);
                System.out.println(middle_ng_al + " middle");
                NGram middle_ng = new NGram(middle_ng_al);

                for (NGram last_word_as_ngram : ngm.n_1gram_map.get(n_1gram).keySet()) {
                    // denom -= bigram probability of last word given middle_ngram
                    //String last_word_as_str = last_word_as_ngram.getNGramArrayList().get(0);

                    List<String> shifted_n_1_gram = (List<String>) middle_ng_al.clone();
                    shifted_n_1_gram.add(new_ng_list.get(0)); // first and second letter
                    shifted_n_1_gram.add(new_ng_list.get(1)); 
                    shifted_n_1_gram.add("w"); // add it a second time since getN_1GramCount automatically removes the last word in the ngram
                    NGram cur_ng = new NGram(shifted_n_1_gram);
                    System.out.println(cur_ng.toString() +" curr");
                    double cur_ng_count = ngm.getN_1GramCount(cur_ng);
                    // double middle_ng_count = ngm.unigram_map.get(middle_ng);
                    double middle_ng_count = ngm.unigram_map.get(new_ng_list.get(1));

                    denominator -= cur_ng_count / middle_ng_count;
                }

                double alpha = reserved_mass / denominator;


                //for trigram "bac", we will mutltiply alpha * p(c | a) = count(a,c)/ count(a)
                List<String> list_words = new_ng_list.subList(1, 3);
                ArrayList<String> ngram_words = new ArrayList<String>();
                for (String word : list_words) {
                    ngram_words.add(word);
                }
                ngram_words.add("w");
                System.out.println(ngram_words + "this");

                NGram count_y_z = new NGram(ngram_words);
                System.out.println(ngm.n_1gram_map + "lasttwoword");
                double sum_count_y_z = ngm.getN_1GramCount(count_y_z); 


                String second_word = new_ng_list.get(1);
                double count_secondword = ngm.unigram_map.get(second_word); // count(a)
                System.out.println(alpha + "alpha");
                System.out.println((sum_count_y_z /count_secondword));
                prob = alpha * (sum_count_y_z /count_secondword);

            }

            return prob;

        }
    
    }

    /**
	 *  predict the next word in the sentence by splicing the sentence a random place and having the 
     * model predict the best word given an n-1 gram word 
	 * @param sentence ngram model
     * @param ngm ngram
     * @param lambda lambda value
     * @return the best predicted word
     *
	 */
    public Boolean predict_next_word(ArrayList<String> sentence, NGramModel ngm, double lambda){
        int ngram_length = ngm.getLength();
        if(ngram_length-1 >= sentence.size()) {
            System.out.println("sentence too short");
            return false;
        }
        else {
            // System.out.println(ngram_length-1 + " " + sentence.size());
            int randomNum = ThreadLocalRandom.current().nextInt(ngram_length-1, sentence.size());

            String last_word = sentence.get(randomNum);

            double max = 0.0; 
            double count_max = 0;
            String best_word = "";
            ArrayList<String> predictor_words = new ArrayList<String>();
    
            
            for (int i = (randomNum - ngram_length)+1; i < randomNum; i++) {
                System.out.println("added" + i );
                predictor_words.add(sentence.get(i));
            }

            NGram predictor_ngram = new NGram(predictor_words);

            // confirm that model length is 1 more than the predictor ngram (which is treated as an n-1gram)
            System.out.println("predictor: " + predictor_ngram.toString());
            // if predictor_ngram doesn't exist
            if (!ngm.n_1gram_map.containsKey(predictor_ngram)){
                // choose most popular word by unigram
                // maybe take the words in the ngram and see what other ngrams theyre in and pick the most popular one of those
                System.out.println("DON'T HAVE");

                ArrayList<String> words = predictor_ngram.getNGramArrayList();
                double count_min;
                String rarest_word;
                if (ngm.unigram_map.containsKey(words.get(0))) {
                    rarest_word = words.get(0);
                }
                else {
                    rarest_word = "<UNK>";
                }
                count_min = ngm.unigram_map.get(rarest_word);

                
                for (String word : words) {
                    if(ngm.unigram_map.containsKey(word) && ngm.unigram_map.get(word) < count_min) {
                        count_min = ngm.unigram_map.get(word);
                        rarest_word = word;
                    }
                }
                // find most common last word for n_1gram containing rarest_word
                HashSet<NGram> ngrams_with_rarest_word = new HashSet<NGram>();
                for (NGram n_1gram : ngm.n_1gram_map.keySet()) {
                    ArrayList<String> cur_n_1gram_al = n_1gram.getNGramArrayList();
                    if(cur_n_1gram_al.contains(rarest_word)) {
                        ngrams_with_rarest_word.add(n_1gram);
                    }
                }

                double highest_last_word_count = 0;
                String most_common_last_word = "";
                for(NGram n_1gram : ngrams_with_rarest_word) {
                    HashMap<NGram, Double> cur_nested_map = ngm.n_1gram_map.get(n_1gram);
                    for(NGram last_word_as_ngram : cur_nested_map.keySet()) {
                        if(cur_nested_map.get(last_word_as_ngram) > highest_last_word_count) {
                            highest_last_word_count = cur_nested_map.get(last_word_as_ngram);
                            most_common_last_word = last_word_as_ngram.getNGramArrayList().get(0);
                        }
                    }
                }

                best_word = most_common_last_word;

                // for (String word : ngm.unigram_map.keySet()) {
                //     if (ngm.unigram_map.get(word) > count_max) {
                //         count_max = ngm.unigram_map.get(word);
                //         best_word = word;
                //     }
                // }
            } else {
                System.out.println("YES HAVE");
                HashMap<NGram, Double> map = ngm.n_1gram_map.get(predictor_ngram);
                for (NGram word : map.keySet()) {
                    predictor_words.add(word.toString());
                    NGram new_predictor_ngram = new NGram(predictor_words);
                    double probability = this.getNGramProbLambda(ngm, new_predictor_ngram, lambda);
                    if (probability > max) {
                        max = probability;
                        best_word = word.toString();
                    }
                }
            }
            
            System.out.println("actual word: " + last_word);
            System.out.println("predicted word: " + best_word);
            return best_word.equals(last_word); 
        }
    }


    public static void main(String[] args) {
        // BigramModel model = new BigramModel("data/sentences");
        BigramModel tm = new BigramModel("nlp-final-project/data/point9pct.txt");
        // for (NGram key : model.n_1gram_map.keySet()) {
        //     for (NGram innerKey : model.n_1gram_map.get(key).keySet()) {
        //         System.out.println(key.getNGramArrayList() + " " + innerKey.getNGramArrayList() + " " + model.n_1gram_map.get(key).get(innerKey));
        //     }
        // }
        Smoothing smoother = new Smoothing();
        
        
        // TrigramModel tm = new TrigramModel("data/test2");
        // for (NGram key : tm.n_1gram_map.keySet()) {
        //     for(NGram innerKey : tm.n_1gram_map.get(key).keySet()) {
        //         // System.out.println("* " + innerKey);
        //         System.out.println(key.getNGramArrayList() + " " + innerKey.getNGramArrayList() + " " + tm.n_1gram_map.get(key).get(innerKey));
        //     }
        // }
        //System.out.println(tm.n_1gram_map + "n_1gram_map hello");  
        //System.out.println(smoother.getNGramProbLambda(tm, new NGram(test_words), 1.0));   
        //System.out.println(smoother.getNGramProbDiscount(tm, new NGram(test_words1), .2) + "WOAH");  
        
        int correct_count0 = 0;
        int correct_count2 = 0;
        int correct_count4 = 0;
        int correct_count6 = 0;
        int correct_count8 = 0;
        int correct_count10 = 0;
        int total_count = 0;
        try {
            File myObj = new File("nlp-final-project/data/point01pct.txt");
            
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                ArrayList<String> sentence = new ArrayList<>(); // will contain <s> </s> and <UNK>
                String data = myReader.nextLine();
                data = "<s> " + data + " </s>";
                for (String word : data.split("\\s+")) {
                    sentence.add(word);
                }
                if (smoother.predict_next_word(sentence, tm, 0.0)) {
                    correct_count0 += 1;
                }
                if (smoother.predict_next_word(sentence, tm, 0.2)) {
                    correct_count2 += 1;
                }
                if (smoother.predict_next_word(sentence, tm, 0.4)) {
                    correct_count4 += 1;
                }
                if (smoother.predict_next_word(sentence, tm, 0.6)) {
                    correct_count6 += 1;
                }
                if (smoother.predict_next_word(sentence, tm, 0.8)) {
                    correct_count8 += 1;
                }
                if (smoother.predict_next_word(sentence, tm, 1.0)) {
                    correct_count10 += 1;
                }
                total_count += 1;
                System.out.println();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        System.out.println("correct count 0.0: " + correct_count0);
        System.out.println("correct count 0.2: " + correct_count2);
        System.out.println("correct count 0.4: " + correct_count4);
        System.out.println("correct count 0.6: " + correct_count6);
        System.out.println("correct count 0.8: " + correct_count8);
        System.out.println("correct count 1.0: " + correct_count10);
        System.out.println("total count: " + total_count);


    }

}
