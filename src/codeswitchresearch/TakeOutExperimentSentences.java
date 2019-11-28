/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Le
 */
public class TakeOutExperimentSentences {

    private static LinkedList<String> sentenceList = new LinkedList<>();
    private static LinkedList<String> replaceWordList = new LinkedList<>();
    private static LinkedList<Integer> replacedLineIDList = new LinkedList<>();
    private static LinkedList<String> wordUsedToReplaceList = new LinkedList<>();
    private static LinkedList<String> wordBeReplacedList = new LinkedList<>();

    public static void main(String[] args) {
        readCSVFile();
        saveCSVFile();
        saveReplacedWordListCSVFile();
    }

    public static void readCSVFile() {
        BufferedReader br = null;
        String filename = "08282019_input_R_v4";
        try {
            //Read CSV file
            File fileDir = new File("data/get_experiment_sentences/input/"
                    + filename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            int lineID = 0;
            //Read to skip header
            br.readLine();

            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] tokens = line.split(",");

                    if (lineID < 1690) {
                        //tokens[3] original sentence, tokens[4] tranlsation, tokens[5] word_id (starts at 1), tokens[7] first_cs_word_translation
                        String originalSentAndTranslation = lineID + "," + tokens[3] + "," + tokens[4] + "," + tokens[5] + "," + tokens[7];
                        String[] wordsInTranslation = tokens[4].split(" ");

                        //Check if the cs-word and translated word are at the same position.
                        //After test, cs-word and translated word are at the same position in all the 1690 cs-sentences 
                        if (!wordsInTranslation[Integer.parseInt(tokens[5]) - 1].equals(tokens[7])) {
                            System.out.println("Line" + lineID + " has different word.");
                            System.out.println("first_cs_word_translation: " + tokens[7]);
                            System.out.println("The word at " + tokens[5] + ": " + wordsInTranslation[Integer.parseInt(tokens[5]) - 1]);
                        }

                        sentenceList.add(originalSentAndTranslation);
                        System.out.println(originalSentAndTranslation);

                    } else {
                        replaceWordList.add(tokens[7]);
                        //System.out.println(lineID + "," + tokens[4] + "," + tokens[7]);
                    }

                    //Increase lineID before adding a new pair of original sentence and translation
                    //The first stored value of lineID is 0
                    lineID++;

                }
            }
            System.out.println(sentenceList.size() + " lines have been added");
            System.out.println(replaceWordList.size() + " words have been added to repalceWordList");
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

    public static void saveCSVFile() {
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header
        final String FILE_HEADER = "line_id"
                + COMMA_DELIMITER
                + "original_sentence"
                + COMMA_DELIMITER
                + "translated_sentence";

        Random rand = new Random();
        //randomly get 100 lineID from 1690 cs-sentences
        //lineID range [0,1689]
        int[] intArray = rand.ints(100, 0, 1690).toArray();
        System.out.println("Size of intArray is " + intArray.length);

        System.out.println("FILE_HEADER: " + FILE_HEADER);

        String outputFileFolder = "data/get_experiment_sentences/output/10192019_set_";

        for (int i = 0; i < 96; i += 5) {
            String outputFilename = outputFileFolder + i / 5 + ".csv";
            try {
                File outputfileName = new File(outputFilename);
                System.out.println("The file will be saved in: "
                        + outputfileName.getPath());
                FileOutputStream is = new FileOutputStream(outputfileName);
                OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
                BufferedWriter w = new BufferedWriter(osw);

                //Write the CSV file header
                w.append(FILE_HEADER);

                //Add a new line separator after the header
                w.append(NEW_LINE_SEPARATOR);

                int lowerLimit = i;
                int upperLimit = i + 4;
                //Randomly choose 1 cs-sentence to replace first-cs word out of 5 cs-sentences.
                int randomIndex = rand.nextInt((upperLimit - lowerLimit) + 1) + lowerLimit;
                if (randomIndex < lowerLimit || randomIndex > upperLimit) {
                    System.err.println("wrong");
                }
                //Randomly choose 20 potentially cs word from 1686 non-cs-sentences.
                //Range [0, 1685]
                int[] replacedWordIndices = rand.ints(20, 0, 1686).toArray();

                for (int a = lowerLimit; a <= upperLimit; a++) {
                    String[] parts = sentenceList.get(intArray[a]).split(",");
                    if (a == randomIndex) {
                        int replacedLineID = intArray[a];
                        replacedLineIDList.add(replacedLineID);
                        String replacedWord = replaceWordList.get(replacedWordIndices[replacedLineIDList.size() - 1]);
                        wordUsedToReplaceList.add(replacedWord);
                        //System.out.println(replacedWord);
                        //String[] parts = sentenceList.get(intArray[a]).split(",");
                        //System.out.println(parts[0] + "," + "original sentence: " + parts[1]);
                        String oldTranslation = parts[2];
                        //System.out.println("old: " + oldTranslation);
                        String[] wordsInOldTranslation = oldTranslation.split(" ");
                        String wordBeReplaced = wordsInOldTranslation[Integer.parseInt(parts[3]) - 1];
                        if(wordBeReplaced.equals(replacedWord)) {
                            System.out.println("The word to be replaced is the same with the replacing word!");
                        }
                        wordBeReplacedList.add(wordBeReplaced);
                        wordsInOldTranslation[Integer.parseInt(parts[3]) - 1] = replacedWord;
                        String newTranslation = String.join(" ", wordsInOldTranslation);
                        parts[2] = newTranslation;
                        //System.out.println("new: " + parts[2]);
                        //String newSentence = String.join(",", parts);                                              
                        //w.append(newSentence);
//                        w.append(parts[0]);
//                        w.append(NEW_LINE_SEPARATOR);
//                        w.append("");
//                        w.append(COMMA_DELIMITER);
//                        w.append(parts[1]);
//                        w.append(NEW_LINE_SEPARATOR);
//                        w.append("");
//                        w.append(COMMA_DELIMITER);
//                        w.append(parts[2]);
//                        w.append(NEW_LINE_SEPARATOR);                       
                    } 
//                    else {
//                        //w.append(sentenceList.get(intArray[a]));
//                    }

                    w.append(parts[0]);
                    w.append(COMMA_DELIMITER);
                    w.append(parts[1]);
                    w.append(COMMA_DELIMITER);
                    w.append(parts[2]);
                    w.append(NEW_LINE_SEPARATOR);
                }

                //System.out.println("CSV file was created successfully !!!");
                w.flush();
                w.close();
                //System.out.println("The file has been saved.");

            } catch (IOException e) {
                System.err.println("Problem writing to the "
                        + outputFilename);
            }
        }
        System.out.println(replacedLineIDList.size() + " lines have replaced a word");
    }

    public static void saveReplacedWordListCSVFile() {
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header
        final String FILE_HEADER = "line_id"
                + COMMA_DELIMITER
                + "replaced_word"
                + COMMA_DELIMITER
                + "word_be_replaced";

        System.out.println("FILE_HEADER: " + FILE_HEADER);
        String outputFilename = "data/get_experiment_sentences/output/replaced_word_list.csv";

        try {
            File outputfileName = new File(outputFilename);
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);

            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            if (replacedLineIDList.size() != wordUsedToReplaceList.size() || replacedLineIDList.size() != wordBeReplacedList.size()) {
                System.out.println("replacedLineIDList'size, wordUsedToReplaceList's size or wordBeReplacedList's size are not the same!");
                System.out.println("replacedLineIDList size: " + replacedLineIDList.size());
                System.out.println("wordUsedToReplaceList size: " + wordUsedToReplaceList.size());
                System.out.println("wordBeReplacedList size: " + wordBeReplacedList.size());
                return;
            } else {
                for (int i = 0; i < replacedLineIDList.size(); i++) {
                    w.append(String.valueOf(replacedLineIDList.get(i)));
                    w.append(COMMA_DELIMITER);
                    w.append(wordUsedToReplaceList.get(i));
                    w.append(COMMA_DELIMITER);
                    w.append(wordBeReplacedList.get(i));
                    w.append(NEW_LINE_SEPARATOR);
                }
            }

            //System.out.println("CSV file was created successfully !!!");
            w.flush();
            w.close();
            //System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + outputFilename);
        }

    }
}
