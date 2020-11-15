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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Le
 */
public class NonCSDatabaseWriter {

    //private static LinkedList<LinkedList> conllList = new LinkedList<>();
    //private static LinkedList<LinkedList> surpList = new LinkedList<>();
    private static LinkedList<LinkedList> mergeList = new LinkedList<>();
    private static LinkedList<LinkedList> cleanNonCSSentenceList = new LinkedList<>();
    private static LinkedList<LinkedList> punctSwitchList = new LinkedList<>();
    private static HashMap<String, Double> chineseFreqMap = new HashMap<>();
    private static HashMap<String, Double> englishFreqMap = new HashMap<>();

    //private static LinkedList<String> conllSentenceIDList = new LinkedList<>();
    //private static LinkedList<String> surpSentenceIDList = new LinkedList<>();
    private static LinkedList<String> cleanNonCSSentenceIDList = new LinkedList<>();

    private static LinkedList<String> mergeSentenceIDList = new LinkedList<>();
    //all_clean_non_cs_surprisal_02022019
    //conll2007_all_clean_non_cs_01302019
    //02022019_all_clean_non_cs
    //02022019_non_cs_corpus

    public static void main(String[] args) {
        //readConllFile();
        //readSurprisalFile();
        chineseFreqMap = IndependentProbabilityDatabase.getIndependentProbabilities("55k-chinese-word-independent-probabilities.txt");
        //englishFreqMap = IndependentProbabilityDatabase.getIndependentProbabilities("55k-english-word-independent-probabilities.txt");

        readMergeFile();
        readCleanCorpusFile();
        /*
        for(String s : cleanNonCSSentenceIDList) {
            if(!mergeSentenceIDList.contains(s)) {
                System.out.println(s);
            }
        }
         */
        checkAlignment();
        saveToCSVFile();
    }

    private static void checkAlignment() {
        if (cleanNonCSSentenceIDList.size() != mergeSentenceIDList.size()) {
            System.out.println("The two ID lists have different size!");
            System.out.println("Clean: " + cleanNonCSSentenceIDList.size());
            System.out.println("Merge: " + mergeSentenceIDList.size());
        }

        for (int i = 0; i < cleanNonCSSentenceIDList.size(); i++) {
            if (!cleanNonCSSentenceIDList.get(i).equals(mergeSentenceIDList.get(i))) {
                System.out.println("The two IDs are not the same!");
                System.out.println("cleanNonCSSentenceID: " + cleanNonCSSentenceIDList.get(i));
                System.out.println("mergeSentenceID: " + mergeSentenceIDList.get(i));
            }
        }

    }

    private static void readMergeFile() {
        BufferedReader br = null;
        String mergeFilename = "02022019_non_cs_corpus";
        try {
            //Reading the text file
            File fileDir = new File("data/database/input/"
                    + mergeFilename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";

            String source = "";
            String sentenceID = "";
            boolean isWord = false;
            //LinkedList<String> sourceList = new LinkedList<>(Arrays.asList("PSU", "CMU", "PIT"));
            LinkedList<String[]> wordList = new LinkedList<>();
            int sentenceCounter = 0;
            //Read to skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (line.contains("END") && columns.length == 1) {
                    System.out.println("Reach the end of the file.");
                    break;
                }
                //If the line is not empty
                if (columns.length != 0) {
                    if (isWord) {
                        String[] wordDetails = new String[9];
                        wordDetails[0] = source;
                        wordDetails[1] = sentenceID;
                        wordDetails[2] = String.valueOf(Integer.parseInt(columns[0]) + 1);
                        for (int i = 1; i < columns.length; i++) {
                            wordDetails[i + 2] = columns[i];
                        }
                        wordList.add(wordDetails);
                    } else {
                        source = columns[0];
                        sentenceID = columns[1];
                        sentenceCounter++;
                        isWord = true;
                    }
                } else {
                    mergeList.add(wordList);
                    mergeSentenceIDList.add(source + sentenceID);
                    wordList = new LinkedList<>();
                    isWord = false;
                }
            }
            System.out.println(sentenceCounter + " merged sentences added.");

        } catch (IOException ie) {
            System.err.println("Error occured while reading the conll file");
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.err.println("Error occured while closing the BufferedReader");
            }
        }
    }

    private static void readCleanCorpusFile() {
        BufferedReader br = null;
        String cleanNonCSSentenceFilename = "02022019_all_clean_non_cs";
        try {
            //Reading the text file
            File fileDir = new File("data/database/input/"
                    + cleanNonCSSentenceFilename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] sentenceDetails = line.split(",");
                if (sentenceDetails.length != 5) {
                    System.err.println("The line doesn't have 5 columns, but "
                            + sentenceDetails.length);
                    System.err.println("Line: " + line);
                    System.exit(-1);
                }

                LinkedList<String[]> wordList = new LinkedList<>();
                String[] wordsInSentence = sentenceDetails[1].split("\\s+");

                String[] indicesOfPunct = sentenceDetails[3].substring(1, sentenceDetails[3].length() - 1).split("_");
                LinkedList<String> indicesOfPunctList = new LinkedList<>(Arrays.asList(indicesOfPunct));
                LinkedList<String> punctSwitchWordList = new LinkedList<>();

                String source = sentenceDetails[4];
                String sentenceID = sentenceDetails[0];

                String wordID = "";
                String wordForm = "";
                String punctSwitch = "";
                for (int i = 0; i < wordsInSentence.length; i++) {
                    wordID = String.valueOf(i + 1);
                    wordForm = wordsInSentence[i];
                    if (!indicesOfPunctList.contains(String.valueOf(i))) {
                        punctSwitch = "0";
                    } else {
                        punctSwitch = "1";
                    }
                    String[] wordDetails = new String[]{source, sentenceID,
                        wordID, wordForm, punctSwitch};
                    wordList.add(wordDetails);
                    punctSwitchWordList.add(punctSwitch);
                }
                cleanNonCSSentenceList.add(wordList);
                cleanNonCSSentenceIDList.add(source + sentenceID);
                punctSwitchList.add(punctSwitchWordList);
                wordList = new LinkedList<>();
                punctSwitchWordList = new LinkedList<>();
            }
            System.out.println(cleanNonCSSentenceList.size() + " clean non-cs sentences added.");
        } catch (IOException ie) {
            System.err.println("Error occured while reading clean corpus file");
            ie.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.err.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    }

    private static void saveToCSVFile() {
        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        final String outFilename = "bilingual_non_cs_database_v1";
        //CSV file header
        final String FILE_HEADER = "source,"
                + "sentence id,"
                + "word id,"
                + "word form,"
                + "punct switch,"
                + "freq,"
                + "surp,"
                + "pos tag,"
                + "head,"
                + "dep rel";

        try {
            File outputfileName = new File("data/database/output/"
                    + "02022019_"
                    + outFilename
                    + ".csv");
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            w.append(FILE_HEADER);
            w.append(NEW_LINE_SEPARATOR);
            String source = "";
            String sentenceID = "";
            for (int i = 0; i < mergeList.size(); i++) {
                LinkedList<String[]> wordList = mergeList.get(i);
                LinkedList<String> punctSwitchs = punctSwitchList.get(i);

                w.append(wordList.get(0)[0]);
                w.append(COMMA_DELIMITER);
                w.append(wordList.get(0)[1]);
                w.append(NEW_LINE_SEPARATOR);
                for (int ii = 0; ii < wordList.size(); ii++) {
                    String[] wordDetails = wordList.get(ii);
                    w.append("");
                    w.append(COMMA_DELIMITER);
                    w.append("");
                    w.append(COMMA_DELIMITER);
                    w.append(wordDetails[2]);
                    w.append(COMMA_DELIMITER);
                    w.append(wordDetails[3]);
                    w.append(COMMA_DELIMITER);
                    w.append(punctSwitchs.get(ii));
                    w.append(COMMA_DELIMITER);
                    w.append(wordDetails[7]);
                    w.append(COMMA_DELIMITER);
                    w.append(wordDetails[8]);
                    w.append(COMMA_DELIMITER);
                    w.append(wordDetails[4]);
                    w.append(COMMA_DELIMITER);
                    w.append(wordDetails[5]);
                    w.append(COMMA_DELIMITER);
                    w.append(wordDetails[6]);
                    w.append(NEW_LINE_SEPARATOR);
                }

                w.append(NEW_LINE_SEPARATOR);

            }

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException ie) {
            ie.printStackTrace();
            System.err.println("Problem writing to the "
                    + "data/database/output/"
                    + "02022019_"
                    + outFilename
                    + ".csv");
        }

    }

    /*
    private static void readConllFile() {
        BufferedReader br = null;
        String conllFilename = "conll2007_all_clean_non_cs_01302019";
        try {
            //Reading the text file
            File fileDir = new File("data/database/input/"
                    + conllFilename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";

            String source = "";
            String sentenceID = "";
            boolean isWord = false;
            //LinkedList<String> sourceList = new LinkedList<>(Arrays.asList("PSU", "CMU", "PIT"));
            LinkedList<String[]> wordList = new LinkedList<>();
            int sentenceCounter = 0;
            //Read to skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (line.contains("END") && columns.length == 1) {
                    System.out.println("Reach the end of the file.");
                    break;
                }
                //If the line is not empty
                if (columns.length != 0) {
                    if (isWord) {
                        String[] wordDetails = new String[8];
                        wordDetails[0] = source;
                        wordDetails[1] = sentenceID;
                        wordDetails[2] = String.valueOf(Integer.parseInt(columns[0]) + 1);
                        for (int i = 1; i < columns.length; i++) {
                            wordDetails[i + 2] = columns[i];
                        }
                        wordList.add(wordDetails);
                    } else {
                        source = columns[0];
                        sentenceID = columns[1];
                        sentenceCounter++;
                        isWord = true;
                    }
                } else {
                    conllList.add(wordList);
                    conllSentenceIDList.add(source + sentenceID);
                    wordList = new LinkedList<>();
                    isWord = false;
                }
            }
            System.out.println(sentenceCounter + " conll sentences added.");

        } catch (IOException ie) {
            System.err.println("Error occured while reading the conll file");
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.err.println("Error occured while closing the BufferedReader");
            }
        }
    }
    
    private static void readSurprisalFile() {
        BufferedReader br = null;
        String conllFilename = "all_clean_non_cs_surprisal_02022019";
        try {
            //Reading the text file
            File fileDir = new File("data/database/input/"
                    + conllFilename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";

            String source = "";
            String sentenceID = "";
            boolean isWord = false;
            LinkedList<String> sourceList = new LinkedList<>(Arrays.asList("PSU", "CMU", "PIT"));
            LinkedList<String[]> wordList = new LinkedList<>();
            int sentenceCounter = 0;
            //Read to skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (line.contains("END") && columns.length == 1) {
                    System.out.println("Reach the end of the file.");
                    break;
                }
                //If the line is not empty
                if (!line.isEmpty()) {
                    if (isWord) {
                        String[] wordDetails = new String[5];
                        wordDetails[0] = source;
                        wordDetails[1] = sentenceID;
                        try {
                            Integer.parseInt(columns[0]);
                        } catch (NumberFormatException nfe) {
                            System.err.println("Line: " + line + "length: " + columns.length);
                            System.exit(-1);
                        }
                        wordDetails[2] = String.valueOf(Integer.parseInt(columns[0]) + 1);
                        for (int i = 1; i < columns.length; i++) {
                            wordDetails[i + 2] = columns[i];
                        }
                        wordList.add(wordDetails);
                    } else {
                        source = columns[0];
                        sentenceID = columns[1];
                        sentenceCounter++;
                        isWord = true;
                    }
                } else {
                    surpList.add(wordList);
                    surpSentenceIDList.add(source + sentenceID);
                    wordList = new LinkedList<>();
                    isWord = false;
                }
            }
            System.out.println(sentenceCounter + " surprisal sentences added.");

        } catch (IOException ie) {
            System.err.println("Error occured while reading the conll file");
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.err.println("Error occured while closing the BufferedReader");
            }
        }
    }
     */
}
