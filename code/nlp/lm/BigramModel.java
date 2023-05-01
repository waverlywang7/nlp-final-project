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

public class BigramModel extends NGramModel {
  // HashMap<NGram, Integer> ngram_map = new HashMap<NGram, Integer>(); //bigram map
  // //HashMap<String, Double> unigram_map = new HashMap <String, Double>(); // includes words with 0 count

  // // ArrayList<String> words_encountered = new ArrayList<String>();
  // HashMap<String, Double> unigram_map = new HashMap<String, Double>(); // num of words encountered two times or more, the number of unique words including UNK. 
  // HashMap<NGram, HashMap<NGram, Double>> n_1gram_map = new HashMap<NGram, HashMap<NGram, Double>>(); // nested map with has first word then nested second word hashmap

  public BigramModel(String filename) {
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

          if (unigram_map.containsKey(word)) {

            unigram_map.replace(word, unigram_map.get(word) + 1.0);
            new_data.add(word);
            
            
          } else { // if word is not in hashmap, add to hashmap but set count to 0, and replace it with <UNK>.
            
            unigram_map.put(word, 0.0);
            unigram_map.replace("<UNK>", unigram_map.get("<UNK>") + 1.0);
            new_data.add("<UNK>");

          }
        }
      }

      // Populatng the bigram map
      for (int i = 1; i < new_data.size(); ++i) {
        // create new hashmap for each first occurence
        String first_letter = new_data.get(i - 1);
        String second_letter = new_data.get(i);

        // if bigram map doesn't contain first letter, update hashmap
        // make the ngram object that holds bigram
        ArrayList<String> word_list = new ArrayList<String>();
        word_list.add(first_letter);
        word_list.add(second_letter);
        NGram bigram = new NGram(word_list);


        if (!ngram_map.containsKey(bigram)) { // TODO: REPLACE WITH n-GRAM OBJECT. 
          ngram_map.put(bigram, 1.0);
        } else {
          ngram_map.put(bigram, (ngram_map.get(bigram) + 1));

        }

        // first_letter
        ArrayList<String> first_list = new ArrayList<String>();
        first_list.add(first_letter);
        NGram first_letter_ngram = new NGram(first_list);

        // second_letter
        ArrayList<String> second_list = new ArrayList<String>();
        second_list.add(second_letter);
        NGram second_letter_ngram= new NGram(second_list);


        // Populate an n_1grammap
        // System.out.println(first_letter_ngram + "first_letter");
        // System.out.println(n_1gram_map + "n_1gram_map");
        if (!n_1gram_map.containsKey(first_letter_ngram)) {
          //System.out.println("I DONT CONTAIN THE WORD");
          HashMap<NGram, Double> second_letter_map = new HashMap<>();
          second_letter_map.put(second_letter_ngram, 1.0);
          n_1gram_map.put(first_letter_ngram, second_letter_map);
          // ArrayList<String> randolist = new ArrayList<String>();
          // randolist.add(first_letter_ngram.toString());
          // NGram new_guy = new NGram(randolist);
          // System.out.println(first_letter_ngram.toString().equals(new_guy.toString()));
          // System.out.println(n_1gram_map.containsKey(new_guy));
          //System.out.println("I GOT HERE AHHH BSHFJGBKSJDYGF");
        } else {
          // if the first letter is there check if second letter exists in nested map
          if (n_1gram_map.get(first_letter_ngram).containsKey(second_letter_ngram)) {
            // update the count
   
            n_1gram_map.get(first_letter_ngram).put(second_letter_ngram, n_1gram_map.get(first_letter_ngram).get(second_letter_ngram) + 1);
          } else {
         
            // make new nested second letter key and update
            n_1gram_map.get(first_letter_ngram).put(second_letter_ngram, 1.0);
          }
        }
      }
    
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }


  public static void main(String[] args) {
    BigramModel model = new BigramModel("data/training.txt");
  }
}

