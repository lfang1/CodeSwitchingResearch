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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class BilingualFrequency {

    static String codeSwitchDatabaseFilename = "03022019_bilingual_cs_database_v1";
    static String nonCodeSwitchDatbaseFilename = "03022019_bilingual_non_cs_database_v1";
    static LinkedList<LinkedList<String[]>> nonCodeSwitchSentenceList = new LinkedList<>();
    static LinkedList<LinkedList<String[]>> codeSwitchSentenceList = new LinkedList<>();
    static HashMap<String, Integer> wordCountMap = new HashMap<>();
    static HashMap<String, Double> wordFrequencyMap = new HashMap<>();
    static LinkedHashMap<String, Double> sortedWordFrequencyMap = new LinkedHashMap<>();
    static HashMap<String, Integer> firstCodeSwitchWordCountMap = new HashMap<>();
    static LinkedHashMap<String, Double> sortedFirstCodeSwitchWordFrequencyMap = new LinkedHashMap<>();
    static HashMap<String, Double> firstCodeSwitchWordFrequencyMap = new HashMap<>();
    static int totalWordCount = 0;

    public static void main(String[] args) {
        readNonCodeSwitchDatabase();
        readCodeSwitchDatabase();
        getFrequencyMap();
        getSortedFrequencyDictionary();
        writeFirstCodeSwitchWordFrequencyDictionary();
        writeBilingualFrequencyDictionary();

    }

    private static void getSortedFrequencyDictionary() {
//            firstCodeSwitchWordFrequencyMap.entrySet().stream()
//                        .sorted(Map.Entry
//                                .<String, Double>comparingByValue().reversed()) 
//                        .limit(firstCodeSwitchWordFrequencyMap.size()) 
//                        .forEach(System.out::println);

        firstCodeSwitchWordFrequencyMap.entrySet().stream()
                .sorted(
                        Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(firstCodeSwitchWordFrequencyMap.size())
                .forEach(
                        x -> sortedFirstCodeSwitchWordFrequencyMap.put(
                                x.getKey(), (x.getValue())));

        wordFrequencyMap.entrySet().stream()
                .sorted(
                        Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(wordFrequencyMap.size())
                .forEach(
                        x -> sortedWordFrequencyMap.put(
                                x.getKey(), (x.getValue())));

    }

    private static void getFrequencyMap() {
        wordCountMap.forEach((word, count) -> {
            wordFrequencyMap.put(
                    word, ((double) count / totalWordCount));
        });

        firstCodeSwitchWordCountMap.forEach((word, count) -> {
            firstCodeSwitchWordFrequencyMap.put(
                    word, ((double) count / totalWordCount));
        });
    }

    private static void writeBilingualFrequencyDictionary() {
        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        final String wordFrequencyInBilingualCorpusFileName
                = "sorted_word_frequency_dictionary_in_bilingual_corpus";

        //CSV file header
        final String FILE_HEADER = "word,"
                + "frequency";

        try {
            File outputfileName = new File("data/bilingual_frequency/output/"
                    + "03082019_"
                    + wordFrequencyInBilingualCorpusFileName
                    + ".csv");
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            w.append(FILE_HEADER);
            w.append(NEW_LINE_SEPARATOR);

            sortedWordFrequencyMap.forEach((word, frequency) -> {
                try {
                    w.append(word);
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(frequency));
                    w.append(NEW_LINE_SEPARATOR);
                } catch (IOException ex) {
                    System.err.println("Problem occurs while writing "
                            + wordFrequencyInBilingualCorpusFileName);
                    ex.printStackTrace();
                }
            });
            w.append(NEW_LINE_SEPARATOR);
            w.append("TotalWordCount");
            w.append(COMMA_DELIMITER);
            w.append(String.valueOf(totalWordCount));
            w.append(NEW_LINE_SEPARATOR);

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException ie) {
            System.err.println("Problem occurs while writing "
                    + wordFrequencyInBilingualCorpusFileName);
            ie.printStackTrace();
        }
    }

    private static void writeFirstCodeSwitchWordFrequencyDictionary() {
        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        final String firstCodeSwitchWordFrequencyOutputFilename
                = "sorted_first_cs_word_frequency_dictionary_in_bilingual_corpus";

        //CSV file header
        final String FILE_HEADER = "word,"
                + "frequency";

        try {
            File outputfileName = new File("data/bilingual_frequency/output/"
                    + "03082019_"
                    + firstCodeSwitchWordFrequencyOutputFilename
                    + ".csv");
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            w.append(FILE_HEADER);
            w.append(NEW_LINE_SEPARATOR);

            sortedFirstCodeSwitchWordFrequencyMap.forEach((word, frequency) -> {
                try {
                    w.append(word);
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(frequency));
                    w.append(NEW_LINE_SEPARATOR);
                } catch (IOException ex) {
                    System.err.println("Problem occurs while writing "
                            + firstCodeSwitchWordFrequencyOutputFilename);
                    ex.printStackTrace();
                }
            });
            
            w.append(NEW_LINE_SEPARATOR);
            w.append("TotalWordCount");
            w.append(COMMA_DELIMITER);
            w.append(String.valueOf(totalWordCount));
            w.append(NEW_LINE_SEPARATOR);
            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException ie) {
            System.err.println("Problem occurs while writing "
                    + firstCodeSwitchWordFrequencyOutputFilename);
            ie.printStackTrace();
        }
    }

    private static void readNonCodeSwitchDatabase() {
        BufferedReader br = null;

        try {
            File fileDir = new File("data/bilingual_frequency/input/"
                    + nonCodeSwitchDatbaseFilename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));
            String line = "";
            String[] wordDetails = new String[10];
            LinkedList<String[]> wordList = new LinkedList<>();
            String source = "";
            String id = "";
            boolean isWord = false;
            int rowNumber = 1;
            //read to skip header
            line = br.readLine();

            while ((line = br.readLine()) != null) {
                rowNumber++;
                if (line.isEmpty()) {
                    nonCodeSwitchSentenceList.add(wordList);
                    isWord = false;
                    wordDetails = new String[10];
                    wordList = new LinkedList<>();
                    source = "";
                    id = "";
                } else {
                    String[] columns = line.split(",");
                    if (isWord) {
                        totalWordCount++;
                        wordDetails[0] = source;
                        wordDetails[1] = id;
                        for (int i = 2; i < 10; i++) {
                            wordDetails[i] = columns[i];
                        }
                        wordList.add(wordDetails);

                        if (wordCountMap.containsKey(columns[3])) {
                            int currentCounter = wordCountMap.get(columns[3]);
                            wordCountMap.put(columns[3], currentCounter++);
                        } else {
                            wordCountMap.put(columns[3], 1);
                        }

                    } else {
                        source = columns[0];
                        id = columns[1];
                        isWord = true;
                    }
                }
            }
            System.out.println("nonCodeSwitchSentenceList size" + " " + nonCodeSwitchSentenceList.size());
            System.out.println("There are " + rowNumber + " rows.");
        } catch (IOException ie) {
            System.err.println("Problem occurs during reading non-code-switched database.");
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

    private static void readCodeSwitchDatabase() {
        BufferedReader br = null;

        try {
            File fileDir = new File("data/bilingual_frequency/input/"
                    + codeSwitchDatabaseFilename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));
            String line = "";
            boolean isWord = false;
            boolean isTranslation = false;
            int rowNumber = 1;
            //read to skip header
            line = br.readLine();

            while ((line = br.readLine()) != null) {
                rowNumber++;
                if (line.isEmpty()) {
                    isWord = false;
                    isTranslation = false;
                } else {
                    if (isWord) {
                        String[] columns = line.split(",");
                        if (columns.length == 4) {
                            isTranslation = true;
                        }
                        if (isTranslation) {

                        } else {
                            totalWordCount++;
                            if (wordCountMap.containsKey(columns[4])) {
                                wordCountMap.put(columns[4], wordCountMap.get(columns[4]) + 1);
                            } else {
                                wordCountMap.put(columns[4], 1);
                            }

                            if (columns[5].equals("CS") && columns[6].equals("0")) {
                                if (firstCodeSwitchWordCountMap.containsKey(
                                        columns[4])) {
                                    firstCodeSwitchWordCountMap.put(
                                            columns[4],
                                            firstCodeSwitchWordCountMap.get(
                                                    columns[4]) + 1);
                                } else {
                                    firstCodeSwitchWordCountMap.put(
                                            columns[4], 1);
                                }
                            }
                        }
                    } else {
                        isWord = true;
                    }
                }

            }
//            System.out.println("codeSwitchSentenceList size" + " " + codeSwitchSentenceList.size());
//            System.out.println("There are " + rowNumber + " rows.");
        } catch (IOException ie) {
            System.err.println("Problem occurs during reading code-switched database.");
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
}
