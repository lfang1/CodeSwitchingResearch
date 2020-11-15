/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import database.IndependentProbabilityDatabase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Le
 */
public class PhraseAfterCSPointReader {

    private static LinkedList<LinkedList> sentenceList = new LinkedList<>();
    private static String filename = "11152019_location_appended_input_R_v4_new_15_11_2019";
    private static String[] wordArray;
    //line
    private static LinkedList<String> phraseBaseArray = new LinkedList<String>();
    //1690 cs; 1686 nonCS; total 3376 lines

    public static void main(String[] args) {
        HashMap<String, Double> frequencyMap = IndependentProbabilityDatabase.getIndependentProbabilities("55k-chinese-word-independent-probabilities.txt");
        frequencyMap.remove("UNKNOWN_WORD");
        wordArray = frequencyMap.keySet().stream().toArray(String[]::new);
        //copy the first 10k out of 55k
        //String[] topTenKFrequencyWord = new String[10000];
        //System.arraycopy(wordArray, 0, topTenKFrequencyWord, 0, 10000);
        readCSVFile();
        saveToTxtFile();
    }

    private static void readCSVFile() {
        BufferedReader br = null;
        try {
            //Read CSV file
            File fileDir = new File("data/add_entropy/input/"
                    + filename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            //Read one line to skip header
            line = br.readLine();

            int sentenceID = 0;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] columns = line.split(",");
                    String[] words = columns[5].split("\\s+");
                    int wordIndex = Integer.parseInt(columns[6]) - 1;
                    String phraseBase = "";
                    //if the cs point is the last word, add the columns[5]: translation directly
                    if (wordIndex == words.length - 1) {
                        phraseBase = columns[5];
                    } else {
                        for (int i = 0; i < wordIndex + 1; i++) {
                            phraseBase += words[i] + " ";
                        }
                    }
                    phraseBase = phraseBase.trim();
                    //System.out.println(phraseBase);
                    phraseBaseArray.add(phraseBase);
                    sentenceID++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.err.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    }

    private static void saveToTxtFile() {
        final String NEW_LINE_SEPARATOR = "\n";

        try {
            String outputFilePath = "d:/App/Dropbox/Fred/research/short_paper/add_entropy/input/55k_one_word_after_cs_point/11152019/";
            int fileCount = 0;
            for (int i = 0; i < phraseBaseArray.size(); i++) {
                fileCount++;
                File outputfileName = new File(outputFilePath
                        + "11152019_55k_new_phrases_one_word_after_cs_point"
                        + "_line_"
                        + i
                        + ".txt");
//                System.out.println("The file will be saved in: "
//                        + outputfileName.getPath());
                FileOutputStream is = new FileOutputStream(outputfileName);
                OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
                BufferedWriter w = new BufferedWriter(osw);

                for (String word : wordArray) {
                    String newPhrase = phraseBaseArray.get(i) + " " + word;
                    newPhrase = newPhrase.trim();
                    w.append(newPhrase);
                    w.append(NEW_LINE_SEPARATOR);
                }

                //System.out.println("line " + (i + 1) + " added");
                //System.out.println("CSV file was created successfully !!!");
                w.flush();
                w.close();
                //System.out.println("The file has been saved.");
            }
            System.out.println(fileCount + " files have been saved successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Problem  writing to the file");
        }
    }
}
