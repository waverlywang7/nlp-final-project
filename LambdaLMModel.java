// Names: Christy Marchese, Waverly Wang
package nlp.lm;

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

public class LambdaLMModel implements LMModel {
  HashMap<String, Double> unigram_map = new HashMap<>(); // set up unigram hashmap, includes words with count of 0
  HashMap<String, Double> unigram_vocab_map = new HashMap<>(); // same as unigram, but doesn't include words with count
                                                               // of 0
  HashMap<String, HashMap<String, Double>> bigram_map = new HashMap<>();

  double lambda = .1;

  public LambdaLMModel(String filename, double lambda) {
    this.lambda = lambda;
    try {
      File myObj = new File(filename);
      Scanner myReader = new Scanner(myObj);

      unigram_map.put("<UNK>", 0.0);
      unigram_map.put("<s>", 0.0);
      unigram_map.put("</s>", 0.0);

      ArrayList<String> new_data = new ArrayList<>();
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

      for (int i = 1; i < new_data.size(); ++i) {
        // create new hashmap for each first occurence
        String first_letter = new_data.get(i - 1);
        String second_letter = new_data.get(i);
        // if bigram map doesn't contain first letter, update hashmap
        if (!bigram_map.containsKey(first_letter)) {
          HashMap<String, Double> second_letter_map = new HashMap<>();
          second_letter_map.put(second_letter, 1.0);
          bigram_map.put(first_letter, second_letter_map);

        } else {
          // if the first letter is there check if second letter exists in nested map
          if (bigram_map.get(first_letter).containsKey(second_letter)) {
            // update the count
            bigram_map.get(first_letter).put(second_letter, bigram_map.get(first_letter).get(second_letter) + 1);
          } else {
            // make new nested second letter key and update
            bigram_map.get(first_letter).put(second_letter, 1.0);
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
    System.out.println("im here");
    double l = .1;
    while (l != 1 * Math.pow(10, -10)) {
      System.out.println(l);
      LambdaLMModel model = new LambdaLMModel("data/training.txt", l);
      System.out.println(model.getPerplexity("data/testing.txt"));
      l = l / 10;
      if (l == 1 * Math.pow(10, -10)) {
        break;

      }

    }

  }

  @Override
  public double logProb(ArrayList<String> sentWords) {

    String current_word = "<s>";
    sentWords.add("</s>");

    double total = 0;
    double probability = 0;
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
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        ArrayList<String> new_data = new ArrayList<>();

        String[] new_data_split = data.split("\\s+");
        Collections.addAll(new_data, new_data_split);

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

    double unigram_count = this.unigram_map.get(current_word); // get unigram count

    if (this.bigram_map.get(current_word).containsKey(word)) {
      double bigram_count = this.bigram_map.get(current_word).get(word); // get bigram count
      return (bigram_count + this.lambda) / (unigram_count + (this.lambda * unigram_vocab_map.size())); // added lambda
    } else {
      return this.lambda / (unigram_count + (this.lambda * unigram_vocab_map.size())); // change the thing next to +
                                                                                       // because the vocab is not
                                                                                       // that...
    }

  }
}
