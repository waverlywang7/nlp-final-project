// Names: Waverly Wang, Adam Cohan, Noah Nevens
package code.nlp.lm;


import java.io.File; // Import the File class
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;
import java.util.List;
import java.util.Collections;
import java.util.*;
// Importing HashMap class
import java.util.HashMap;


public class TrigramModel extends NGramModel {
  //static HashMap<NGram, Double> ngram_map = new HashMap<NGram, Double>(); //bigram map
  //HashMap<NGram, HashMap<NGram, Double>> n_1gram_map = new HashMap<NGram, HashMap<NGram, Double>>(); // unigram map


 HashMap<String, HashMap<String, Double>> bigram_map = new HashMap<>();
 ArrayList<String> words_encountered = new ArrayList<String>();
 HashMap<String, Double> unigram_map = new HashMap<String, Double>(); // num of words encountered two times or more, the number of unique words including UNK. 




 public TrigramModel(String filename) {
  unigram_map.put("<UNK>", 0.0);
  unigram_map.put("<s>", 0.0); // do this because we don't want to replace the first instance of <s> and </s>
  unigram_map.put("</s>", 0.0);
   try {
     File myObj = new File(filename);
     Scanner myReader = new Scanner(myObj);


     ArrayList<String> new_data = new ArrayList<>(); // will contain <s> </s> and <UNK>


     // adding UNK to the file and populating unigram map with vocabulary
     while (myReader.hasNextLine()) {
       String data = myReader.nextLine();
       data = "<s> " + data + " </s>";
       for (String word : data.split("\\s+")) {
        // create ngram object
        // ArrayList<String> word_list = new ArrayList<String>(); // a word list that just contains one word
        // word_list.add(word);
        // NGram unigram = new NGram(word_list);

        if (unigram_map.containsKey(word)) {
          //unigram_map.put(word, unigram_map.get(word) + 1.0); // if word is already in hashmap, increment count

          unigram_map.replace(word, unigram_map.get(word) + 1.0);
          new_data.add(word); // add word to new_data
          
          
        } else { // if word is not in hashmap, add to hashmap but set count to 0, and replace it
                 // with <UNK>.
          //unigram_map.put(word, 0.0); // keep track if we encountered word
          
          unigram_map.put(word, 0.0);
          unigram_map.replace("<UNK>", unigram_map.get("<UNK>>") + 1.0);
          new_data.add("<UNK>");
          //unigram_map.put("<UNK>", unigram_map.get("<UNK>") + 1);// word is already in hashmap add to count

        }
      }


       for (int i = 0; i < new_data.size()-2; i++) {
         ArrayList<String> ngram_words = new ArrayList<String>();
         ngram_words.add(new_data.get(i));
         ngram_words.add(new_data.get(i+1));
         NGram bigram = new NGram(ngram_words);
        
         //the third word is also an ngram
         ArrayList<String> third_word = new ArrayList<String>();
         NGram last_word = new NGram(third_word);
         third_word.add(new_data.get(i+2));
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


         n_1gram_map.put(bigram, bigram_map);


         ngram_words.add(new_data.get(i+2));
         NGram trigram = new NGram(ngram_words);
         if (!ngram_map.containsKey(trigram)) {
           ngram_map.put(trigram, 0.0);
         }
         ngram_map.put(trigram, ngram_map.get(trigram) + 1);
       }


     }
  
     //System.out.println(n_1gram_map);
     //System.out.println(ngram_map);


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

