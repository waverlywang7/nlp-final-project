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
 ArrayList<String> unigram_vocab = new ArrayList<String>(); //TODO: num of words that occur at least 2 times, which will be the number of unique words including UNK. 




 public TrigramModel(String filename) {
   try {
     File myObj = new File(filename);
     Scanner myReader = new Scanner(myObj);


     ArrayList<String> new_data = new ArrayList<>(); // will contain <s> </s> and <UNK>


     // adding UNK to the file and populating unigram map with vocabulary
     while (myReader.hasNextLine()) {
       String data = myReader.nextLine();
       data = "<s> " + data + " </s>";
       String[] sentence = data.split("\\s+");
       ArrayList<String> word_list = new ArrayList<String>();
       for (String word : sentence) {
           if (!words_encountered.contains(word)) {
               word_list.add("<UNK>");
               // TODO: if unigram_vocab does not contain UNK
                  // add UNK
               words_encountered.add(word);
           } else {
               word_list.add(word);
              // TODO:
              // if unigram_vocab does not contains word
              // add word to unigram_vocab
           }
       }


       for (int i = 0; i < word_list.size()-2; i++) {
         ArrayList<String> ngram_words = new ArrayList<String>();
         ngram_words.add(word_list.get(i));
         ngram_words.add(word_list.get(i+1));
         NGram bigram = new NGram(ngram_words);
        
         //the third word is also an ngram
         ArrayList<String> third_word = new ArrayList<String>();
         NGram last_word = new NGram(third_word);
         third_word.add(word_list.get(i+2));
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


         ngram_words.add(word_list.get(i+2));
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


   // null poiter execption maybe because i crate a model like this.
   TrigramModel model = new TrigramModel("data/training.txt");
   //System.out.println(ngram_map);
   //System.out.println(n_1gram_map);


   // List<Double> list = new ArrayList<Double>();
   // list.add(.99);
   // list.add(.9);
   // list.add(.75);
   // list.add(.5);
   // list.add(.25);
   // list.add(.1);
   // for (double item : list) {
   //   System.out.println(item);
   //   LambdaLMModel model = new LambdaLMModel("data/training.txt", item);


   //   System.out.println(model.getPerplexity("data/development.txt"));
   // }
 }


 // @Override
 // public double logProb(ArrayList<String> sentWords) {


 //   String current_word = "<s>";
 //   sentWords.add("</s>");


 //   double total = 0;
 //   double probability = 0;


 //   // calculate the log prob of a sentence.
 //   for (String word : sentWords) {


 //     probability = getBigramProb(current_word, word);


 //     total += Math.log10(probability); // add log prob
 //     current_word = word;
 //   }
 //   return total;
 // }


 // @Override
 // public double getPerplexity(String filename) {
 //   try {
 //     File myObj = new File(filename);
 //     Scanner myReader = new Scanner(myObj);


 //     double perplexity = 0;
 //     double total_log_prob = 0;
 //     int word_count = 0;


 //     // read through each line of text
 //     while (myReader.hasNextLine()) {
 //       String data = myReader.nextLine();
 //       ArrayList<String> new_data = new ArrayList<>();


 //       // split the text by white space
 //       String[] new_data_split = data.split("\\s+");
 //       Collections.addAll(new_data, new_data_split);


 //       // calculate log prob of sentence
 //       total_log_prob += logProb(new_data);
 //       word_count += new_data_split.length + 2; // we are accounting for sentence tags
 //     }


 //     perplexity = Math.pow(10, -1 * (total_log_prob / word_count));


 //     myReader.close();
 //     return perplexity;


 //   } catch (FileNotFoundException e) {
 //     System.out.println("An error occurred.");
 //     e.printStackTrace();
 //   }


 //   return 0;
 // }




 @Override
 public void trainModel() {
   // TODO Auto-generated method stub
   throw new UnsupportedOperationException("Unimplemented method 'trainModel'");
 }
}

