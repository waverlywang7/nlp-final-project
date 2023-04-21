package code.nlp.lm;


// Names: Christy Marchese, Waverly Wang
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

public class BigramModel implements LMModel {
  HashMap<String, Double> unigram_map = new HashMap<>(); // set up unigram hashmap, includes words with count of 0
  HashMap<String, Double> unigram_vocab_map = new HashMap<>(); // same as unigram, but doesn't include words with count
                                                               // of 0


  //HashMap<String, HashMap<String, Double>> bigram_map = new HashMap<>();

HashMap<String, Double> bigram_map = new HashMap<>(); // now a single hashmap

  double discount = .9;

  public BigramModel(String filename, double discount) {
    this.discount = discount;
    try {
      File myObj = new File(filename);
      Scanner myReader = new Scanner(myObj);

      unigram_map.put("<UNK>", 0.0);
      unigram_map.put("<s>", 0.0);
      unigram_map.put("</s>", 0.0);

      ArrayList<String> new_data = new ArrayList<>();

      // adding UNK to the file and populating unigram map with vocabulary
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        data = "<s> " + data + " </s>";

        for (String word : data.split("\\s+")) {
          if (unigram_map.containsKey(word)) {
            unigram_map.put(word, unigram_map.get(word) + 1); // if word is already in hashmap, increment count
            new_data.add(word);

          } else { // if word is not in hashmap, add to hashmap but set count to 0, and replace it
                   // with <UNK>.
            unigram_map.put(word, 0.0); // keep track if we encountered word

            new_data.add("<UNK>");
            unigram_map.put("<UNK>", unigram_map.get("<UNK>") + 1);// word is already in hashmap add to count

          }
        }

      }

      // Using for-each loop to get vocab, filtering out any word that has count 0
      for (Map.Entry<String, Double> mapElement : unigram_map.entrySet()) {
        String key = mapElement.getKey();

        // Adding some bonus marks to all the students
        Double value = mapElement.getValue();
        // check if value greater than 0
        if (value > 0) {
          unigram_vocab_map.put(key, value);

        }

      }
      // Populatng the bigram map
      for (int i = 1; i < new_data.size(); ++i) {
        // create new hashmap for each first occurence
        String first_letter = new_data.get(i - 1);
        String second_letter = new_data.get(i);

        // if bigram map doesn't contain first letter, update hashmap

        if (!bigram_map.containsKey(first_letter+second_letter)) { // TODO: REPLACE WITH n-GRAM OBJECT. 
          bigram_map.put(first_letter+second_letter, 1.0);
        } else {
          bigram_map.put(first_letter, (bigram_map.get(first_letter+second_letter) + 1));

        }

        // if (!bigram_map.containsKey(first_letter)) {
        //   HashMap<String, Double> second_letter_map = new HashMap<>();
        //   second_letter_map.put(second_letter, 1.0);
        //   bigram_map.put(first_letter, second_letter_map);

        // } else {
        //   // if the first letter is there check if second letter exists in nested map
        //   if (bigram_map.get(first_letter).containsKey(second_letter)) {
        //     // update the count
        //     bigram_map.get(first_letter).put(second_letter, bigram_map.get(first_letter).get(second_letter) + 1);
        //   } else {
        //     // make new nested second letter key and update
        //     bigram_map.get(first_letter).put(second_letter, 1.0);
        //   }
        // }

      }
      System.out.println(unigram_map);

      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {

    List<Double> list = new ArrayList<Double>();
    list.add(.99);
    list.add(.9);
    list.add(.75);
    list.add(.5);
    list.add(.25);
    list.add(.1);
    for (double item : list) {
      System.out.println(item);
      LambdaLMModel model = new LambdaLMModel("data/training.txt", item);
      System.out.println(model.getPerplexity("data/development.txt"));
    }
  }

  @Override
  public double logProb(ArrayList<String> sentWords) {

    String current_word = "<s>";
    sentWords.add("</s>");

    double total = 0;
    double probability = 0;

    // calculate the log prob of a sentence.
    for (String word : sentWords) {

      probability = getBigramProb(current_word, word);

      total += Math.log10(probability); // add log prob
      current_word = word;
    }
    return total;
  }

  @Override
  public double getPerplexity(String filename) {
    try {
      File myObj = new File(filename);
      Scanner myReader = new Scanner(myObj);

      double perplexity = 0;
      double total_log_prob = 0;
      int word_count = 0;

      // read through each line of text
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        ArrayList<String> new_data = new ArrayList<>();

        // split the text by white space
        String[] new_data_split = data.split("\\s+");
        Collections.addAll(new_data, new_data_split);

        // calculate log prob of sentence
        total_log_prob += logProb(new_data);
        word_count += new_data_split.length + 2; // we are accounting for sentence tags
      }

      perplexity = Math.pow(10, -1 * (total_log_prob / word_count));

      myReader.close();
      return perplexity;

    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }

    return 0;
  }

  @Override
  public double getBigramProb(String first, String second) {

    String current_word = first;
    String word = second;

    if ((!unigram_map.containsKey(word)) || unigram_map.get(word) == 0) {
      word = "<UNK>";
    }

    if ((!unigram_map.containsKey(current_word)) || unigram_map.get(current_word) == 0) {
      current_word = "<UNK>";
    }

    // if bigram exists, calculate the bigram prob using count of bigram and count
    // of bigrams that start with first word
    if (this.bigram_map.get(current_word).containsKey(word)) {
      double bigram_count = this.bigram_map.get(current_word).get(word); // get how many times the bigram shows up

      double total_bigram_count = 0.0;
      // count up the number of bigrams that start with the first word of the bigram
      for (String second_word : this.bigram_map.get(current_word).keySet()) {
        total_bigram_count += this.bigram_map.get(current_word).get(second_word);

      }

      return (bigram_count - this.discount) / total_bigram_count; // add discount

    } else {
      // if bigram has never been encountered, then calculate bigramprob differently

      double unique_bigrams = this.bigram_map.get(current_word).size();
      double total_bigram_count = 0.0;
      double denominator = 1.0;

      // count tokens in the training text
      int total_tokens_count = 0;
      for (String unigram_word : this.unigram_map.keySet()) {
        total_tokens_count += this.unigram_map.get(unigram_word);
      }

      // count the total times a bigram start with first word in bigram
      for (String second_word : this.bigram_map.get(current_word).keySet()) {
        total_bigram_count += this.bigram_map.get(current_word).get(second_word);
        denominator -= this.unigram_map.get(second_word) / total_tokens_count; // calculate the deoniminator of alpha
      }
      double reserved_mass = unique_bigrams * this.discount / total_bigram_count;
      double alpha = reserved_mass / denominator;

      double prob_second_word = unigram_map.get(word) / total_tokens_count;
      return alpha * prob_second_word;
    }

  }
}
