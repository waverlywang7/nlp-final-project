package code.nlp.lm;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class NGramModel {
    
   // HashMap<String, Double> unigram_map = new HashMap<String, Double>(); // includes all words that show up 0 times or more
    int n; 
    HashMap<String, Double> unigram_map = new HashMap<String, Double>(); //the words that occur at least 2 times,(the number of unique words including UNK)
    HashMap<NGram, Double> ngram_map = new HashMap<NGram, Double>();
    HashMap<NGram, HashMap<NGram, Double>> n_1gram_map = new HashMap<NGram, HashMap<NGram, Double>>();
    Double vocab_size = 0.0;


    /**
	 *  gets length of the ngram
	 * 
	 * @return length of ngram
	 */
    public int getLength(){
        return this.n;
    }

    /**
	 *  returns the count of ngram in training set
	 * @param ng takes in a ngram and gets the count of ngram in the training set
	 * @return the ngram count
	 */
    public double getNGramCount(NGram ng) {

        if(ngram_map.keySet().contains(ng)) { // TODO: fix the fact that this prints out nothing in ngram_map, it seems like the model's ngram map is not passed in... perhaps, ngm needs to be passed
            return ngram_map.get(ng);
        }
        else {
            return 0.0;
        }
        
    }

     /**
	 *  returns the count of n_1gram in training set, for instance, for a trigram "aad", pass in to return count(aa)  
	 * @param ng takes in a ngram and gets the count of ngram in the training set
	 * @return the ngram count
	 */
    public double getN_1GramCount(NGram ng) { 
        NGram n_1gram = ng.getN_1Gram();

        if (!n_1gram_map.containsKey(n_1gram)) {
            return 0.0;
        }
        
        HashMap<NGram, Double> nestedMap = n_1gram_map.get(n_1gram);
        double sum = 0.0;
       
        for(NGram key : nestedMap.keySet()) {
            
            sum += nestedMap.get(key);
        }
        return sum;
    }

     /**
	 *  return the size of the unigram vocab 
	 * @return the unique vocab size of training set
	 */
    public double getVocabSize() {
        return vocab_size;
    }

}
