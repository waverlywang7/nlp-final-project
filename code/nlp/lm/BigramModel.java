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
  HashMap<NGram, Integer> ngram_map = new HashMap<NGram, Integer>(); //bigram map
  //HashMap<String, Double> unigram_map = new HashMap <String, Double>(); // includes words with 0 count

  ArrayList<String> words_encountered = new ArrayList<String>();
  ArrayList<String> unigram_vocab = new ArrayList<String>(); // num of words encountered two times or more, the number of unique words including UNK. 
  HashMap<NGram, HashMap<NGram, Double>> n_1gram_map = new HashMap<NGram, HashMap<NGram, Double>>(); // nested map with has first word then nested second word hashmap

  public BigramModel(String filename) {
    try {
      File myObj = new File(filename);
      Scanner myReader = new Scanner(myObj);

      // ArrayList<String> unk_list = new ArrayList<String>();
      // unk_list.add("<UNK>");
      // NGram UNK = new NGram(unk_list);


      // ArrayList<String> s_list = new ArrayList<String>();
      // s_list.add("<s>");
      // NGram s = new NGram(s_list);


      // ArrayList<String> sback_list = new ArrayList<String>();
      // sback_list.add("</s>");
      // NGram sback = new NGram(sback_list);

      // initialize <UNK> , </s>, <s> in the unigram map
      // n_1gram_map.put(UNK, 0);
      // n_1gram_map.put(s, 0);
      // n_1gram_map.put(sback, 0);

      // initialize <UNK> , </s>, <s> in the unigram map

      // unigram_map.put("<UNK>", 0.0);
      //unigram_map.put("<s>", 0.0);
      //unigram_map.put("</s>", 0.0);
      

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

          if (words_encountered.contains(word)) {
            //unigram_map.put(word, unigram_map.get(word) + 1.0); // if word is already in hashmap, increment count

            if (!unigram_vocab.contains(word)){ // word has been encountered at least two times
              unigram_vocab.add(word);
            }
            new_data.add(word); // add word to new_data
            
            
          } else { // if word is not in hashmap, add to hashmap but set count to 0, and replace it
                   // with <UNK>.
            //unigram_map.put(word, 0.0); // keep track if we encountered word
            if (!unigram_vocab.contains("<UNK>")){
              unigram_vocab.add("<UNK>");
            }
            words_encountered.add(word);
            new_data.add("<UNK>");
            //unigram_map.put("<UNK>", unigram_map.get("<UNK>") + 1);// word is already in hashmap add to count

          }
        }

      }


      // // Using for-each loop to get vocab, filtering out any word that has count 0
      // for (Map.Entry<String, Double> mapElement : unigram_map.entrySet()) {
      //   String key = mapElement.getKey();

      //   // Adding some bonus marks to all the students
      //   Double value = mapElement.getValue();
      //   // check if value greater than 0
      //   if (value > 0) {
      //     unigram_vocab_map.put(key, value);

      //   }
      // }
      

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
          ngram_map.put(bigram, 1);
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
        if (!n_1gram_map.containsKey(first_letter_ngram)) {
          HashMap<NGram, Double> second_letter_map = new HashMap<>();
          second_letter_map.put(second_letter_ngram, 1.0);
          n_1gram_map.put(first_letter_ngram, second_letter_map);

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
      //System.out.println(n_1gram_map);
      //System.out.println(ngram_map);
    
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
      
    }

  }


  public static void main(String[] args) {
    BigramModel model = new BigramModel("data/training.txt");
  }

  @Override
  public void trainModel() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'trainModel'");
  }
}

