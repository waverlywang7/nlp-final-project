// Names: Waverly Wang, Adam Cohan, Noah Nevens
package code.nlp.lm;


import java.io.File; // Import the File class
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;
// Importing HashMap class
import java.util.HashMap;

/**
 *  takes in a training set and populates trigram map (ngram_map) which holds counts of 
 * trigrams and n_1gram_map which is a nested hashmap where keys are bigrams which hash to the third word in trigram
 * @param filename test set
 */
public class TrigramModel extends NGramModel {


 HashMap<String, HashMap<String, Double>> bigram_map = new HashMap<>();
 ArrayList<String> words_encountered = new ArrayList<String>();




 public TrigramModel(String filename) {
  n=3;
  unigram_map.put("<UNK>", 0.0);
  unigram_map.put("<s>", 0.0); // do this because we don't want to replace the first instance of <s> and </s>
  unigram_map.put("</s>", 0.0);
   try {
     File myObj = new File(filename);
     Scanner myReader = new Scanner(myObj);

     // adding UNK to the file and populating unigram map with vocabulary
     while (myReader.hasNextLine()) {
       ArrayList<String> new_data = new ArrayList<>(); // will contain <s> </s> and <UNK>
       String data = myReader.nextLine();
       data = "<s> " + data + " </s>";
       for (String word : data.split("\\s+")) {

        if (unigram_map.containsKey(word)) {
   
          unigram_map.replace(word, unigram_map.get(word) + 1.0);
          new_data.add(word); // add word to new_data
          
          
        } else { // if word is not in hashmap, add to hashmap but set count to 0, and replace it
                 // with <UNK>.
  
          unigram_map.put(word, 0.0);
          unigram_map.replace("<UNK>", unigram_map.get("<UNK>") + 1.0);
          new_data.add("<UNK>");
 
        }
      }
      System.out.println(new_data);


       for (int i = 0; i < new_data.size()-2; i++) {
         ArrayList<String> ngram_words = new ArrayList<String>();
         ngram_words.add(new_data.get(i));
         ngram_words.add(new_data.get(i+1));
         NGram bigram = new NGram(ngram_words);
        
         //the third word is also an ngram
         ArrayList<String> third_word = new ArrayList<String>();
         third_word.add(new_data.get(i+2));
         NGram last_word = new NGram(third_word);
         if (!n_1gram_map.containsKey(bigram)) {
           n_1gram_map.put(bigram, new HashMap<NGram, Double>()); // if word is already in hashmap, increment count
         }


         //iterate the count of the n-1gram map
         HashMap<NGram, Double> bigram_map = n_1gram_map.get(bigram);
         if (!bigram_map.containsKey(last_word)) {
           bigram_map.put(last_word, 1.0);
         } else {
           bigram_map.put(last_word, bigram_map.get(last_word) + 1);
         }

         ngram_words.add(new_data.get(i+2));
         NGram trigram = new NGram(ngram_words);
         if (!ngram_map.containsKey(trigram)) {
           ngram_map.put(trigram, 0.0);
         }
         ngram_map.put(trigram, ngram_map.get(trigram) + 1);
       }


     }
  
     for (String key : unigram_map.keySet()) {
      if (unigram_map.get(key) >= 1) {
          vocab_size += 1.0;
      }
  }

     myReader.close();
   } catch (FileNotFoundException e) {
     System.out.println("An error occurred.");
     e.printStackTrace();
   }


 }




 public static void main(String[] args) {


   TrigramModel model = new TrigramModel("data/training.txt");

 }
}

