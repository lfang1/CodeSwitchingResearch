/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import codeswitchresearch.IndependentProbabilityDatabase;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class CSDatabaseWriter {

    private static LinkedList<LinkedList> cleanSentenceList = new LinkedList<>();
    private static LinkedList<LinkedList> translationSentenceList = new LinkedList<>();
    private static LinkedList<String> cleanSentenceIDList = new LinkedList<>();
    private static LinkedList<String> translationSentenceIDList = new LinkedList<>();
    //private static LinkedList<LinkedList> mergeList = new LinkedList<>();
    private static HashMap<String, Double> chineseFreqMap = new HashMap<>();
    private static HashMap<String, Double> englishFreqMap = new HashMap<>();
    private static LinkedList<LinkedList> translationCSTypeAndPunctSwitchSentenceList = new LinkedList<>();

    public static void main(String[] args) {
        chineseFreqMap = IndependentProbabilityDatabase.getIndependentProbabilities("55k-chinese-word-independent-probabilities.txt");
        englishFreqMap = IndependentProbabilityDatabase.getIndependentProbabilities("55k-english-word-independent-probabilities.txt");

        readCleanCorpusFile();
        readTranslationCorpusFile();
        //checkAlignment();
        //saveToCSVFile();
    }

    private static void readCleanCorpusFile() {
        BufferedReader br = null;
        String cleanSentenceFilename = "11132019_all_clean_and_pns_ver_new";
        try {
            //Reading the text file
            File fileDir = new File("data/database/input/"
                    + cleanSentenceFilename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            
            while ((line = br.readLine()) != null) {
                String[] sentenceDetails = line.split(",");
                if (sentenceDetails.length != 11) {
                    System.err.println("The line doesn't have 11 columns, but "
                            + sentenceDetails.length);
                    System.err.println("Line: " + line);
                    System.exit(0);
                }

                LinkedList<String[]> wordList = new LinkedList<>();
                String[] wordsInSentence = sentenceDetails[1].split("\\s+");
                String[] indicesOfCS = sentenceDetails[4].substring(1, sentenceDetails[4].length() - 1).split("_");
                LinkedList<String> indicesOfCSList = new LinkedList<>(Arrays.asList(indicesOfCS));
                String[] typesOfCS = sentenceDetails[8].split("_");

                String[] indicesOfPunct = sentenceDetails[6].substring(1, sentenceDetails[6].length() - 1).split("_");
                LinkedList<String> indicesOfPunctList = new LinkedList<>(Arrays.asList(indicesOfPunct));

                LinkedList<String[]> translationCSTypeAndPunctSwitchWordList = new LinkedList<>();
                String[] wordsInTranslation = sentenceDetails[2].split("\\s+");
                //System.out.println(sentenceDetails[2]);
                String[] indicesOfTranslationCS = sentenceDetails[5].substring(1, sentenceDetails[5].length() - 1).split("_");
                //System.out.println(sentenceDetails[5]);
                LinkedList<String> indicesOfTranslationCSList = new LinkedList<>(Arrays.asList(indicesOfTranslationCS));
                //System.out.println(indicesOfTranslationCSList.size());
                String[] indicesOfTranslationPunct = sentenceDetails[7].substring(1, sentenceDetails[7].length() - 1).split("_");
                LinkedList<String> indicesOfTranslationPunctList = new LinkedList<>(Arrays.asList(indicesOfTranslationPunct));
                for (int i = 0; i < wordsInTranslation.length; i++) {
                    String type = "";
                    String pSwitch = "";
                    if (indicesOfTranslationCSList.contains(String.valueOf(i))) {
                        type = "1";
                    } else {
                        type = "0";
                    }
                    if (indicesOfTranslationPunctList.contains(String.valueOf(i))) {
                        pSwitch = "1";
                    } else {
                        pSwitch = "0";
                    }
                    translationCSTypeAndPunctSwitchWordList.add(new String[]{type, pSwitch});
                }
                translationCSTypeAndPunctSwitchSentenceList.add(translationCSTypeAndPunctSwitchWordList);
                translationCSTypeAndPunctSwitchWordList = new LinkedList<>();

                String source = sentenceDetails[10];
                String sentenceID = sentenceDetails[0];
                String sentenceType = sentenceDetails[9];

                String wordID = "";
                String wordForm = "";
                String csType = "";
                String punctSwitch = "";
                String freq = "";
                for (int i = 0; i < wordsInSentence.length; i++) {
                    wordID = String.valueOf(i + 1);
                    wordForm = wordsInSentence[i];
                    if (!indicesOfCSList.contains(String.valueOf(i))) {
                        csType = "0";
                    } else {
                        if(indicesOfCSList.indexOf(String.valueOf(i)) > typesOfCS.length-1) {
                            System.out.println(sentenceDetails[10] + "_" + sentenceDetails[0]);
                            System.out.println(indicesOfCSList.toString());
                        } else {
                            csType = typesOfCS[indicesOfCSList.indexOf(String.valueOf(i))];
                        }
                        //csType = typesOfCS[indicesOfCSList.indexOf(String.valueOf(i))];
                    }

                    if (!indicesOfPunctList.contains(String.valueOf(i))) {
                        punctSwitch = "0";
                    } else {
                        punctSwitch = "1";
                    }

                    if (chineseFreqMap.containsKey(wordForm)) {
                        freq = String.valueOf(chineseFreqMap.get(wordForm));
                    } else if (englishFreqMap.containsKey(wordForm)) {
                        freq = String.valueOf(englishFreqMap.get(wordForm));
                    } else {
                        freq = "";
                    }

                    String[] wordDetails = new String[]{source, sentenceID,
                        sentenceType, wordID, wordForm,
                        csType, punctSwitch, freq};
                    wordList.add(wordDetails);
                }
                cleanSentenceList.add(wordList);
                cleanSentenceIDList.add(source + sentenceID);
                wordList = new LinkedList<>();
            }
            System.out.println(cleanSentenceList.size() + " clean sentences added.");
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

    private static void readTranslationCorpusFile() {
        BufferedReader br = null;
        String translationFilename = "11142019_translation_sent_parser_output";
        try {
            //Reading the text file
            File fileDir = new File("data/database/input/"
                    + translationFilename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            int sentenceCounter = 0;
            LinkedList<String[]> wordList = new LinkedList<>();
            String source = "";
            String sentenceID = "";
            boolean isWord = false;
            //Read to skip the header
            br.readLine();
            //Reading from the second line
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");

                if (line.contains("END") && columns.length == 1) {
                    System.out.println("Finishing reading translation file");
                    break;
                }

                if (columns.length != 0) {
                    if (isWord) {
                        String[] wordDetails = new String[9];
                        wordDetails[0] = source;
                        wordDetails[1] = sentenceID;
                        wordDetails[2] = String.valueOf(Integer.parseInt(columns[0]) + 1);
                        //System.out.println("Word ID: " + wordDetails[2]);
                        for (int i = 1; i < columns.length; i++) {
                            //System.out.println(source + " " + sentenceID);
                            wordDetails[i + 2] = columns[i];
                        }
                        wordList.add(wordDetails);
                    } else {
                        //sentenceCounter++;
                        //System.out.println(SentenceCounter + " " + line);
                        source = columns[0];
                        sentenceID = columns[1];
                        isWord = true;
                    }
                } else {
                    //String currentID = source + sentenceID;

                    translationSentenceList.add(wordList);
                    translationSentenceIDList.add(source + sentenceID);
                    wordList = new LinkedList<>();
                    isWord = false;
                }
            }
            System.out.println(translationSentenceList.size() + " translation sentences added.");
        } catch (IOException ie) {
            System.err.println("Error occured while reading the translation file");
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
        final String outFilename = "psu_need_fix_02052019_v1";
        //CSV file header
        final String FILE_HEADER = "source,"
                + "sentence id,"
                + "sentence type,"
                + "word id,"
                + "word form,"
                + "cs type,"
                + "punct switch,"
                + "freq,"
                + "surp,"
                + "pos tag,"
                + "head,"
                + "dep rel";

        try {
            File outputfileName = new File("data/database/output/"
                    + "02052019_"
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
            String sentenceType = "";
            for (int i = 0; i < cleanSentenceList.size(); i++) {
                LinkedList<String[]> wordList = cleanSentenceList.get(i);
                w.append(wordList.get(0)[0]);
                w.append(COMMA_DELIMITER);
                w.append(wordList.get(0)[1]);
                w.append(COMMA_DELIMITER);
                w.append(wordList.get(0)[2]);
                w.append(NEW_LINE_SEPARATOR);
                for (String[] wordDetails : wordList) {
                    w.append("");
                    w.append(COMMA_DELIMITER);
                    w.append("");
                    w.append(COMMA_DELIMITER);
                    w.append("");
                    w.append(COMMA_DELIMITER);
                    for (int wi = 3; wi < wordDetails.length - 1; wi++) {
                        w.append(wordDetails[wi]);
                        w.append(COMMA_DELIMITER);
                    }
                    w.append(wordDetails[wordDetails.length - 1]);
                    w.append(NEW_LINE_SEPARATOR);
                }
                w.append("");
                w.append(COMMA_DELIMITER);
                w.append("");
                w.append(COMMA_DELIMITER);
                w.append("");
                w.append(COMMA_DELIMITER);

                w.append("Translation");
                w.append(NEW_LINE_SEPARATOR);

                LinkedList<String[]> translationWordList = translationSentenceList.get(i);
                LinkedList<String[]> csIndicesAndPunctWordList = translationCSTypeAndPunctSwitchSentenceList.get(i);
                for (int ii = 0; ii < translationWordList.size(); ii++) {
                    String[] wordDetails = translationWordList.get(ii);
                    w.append("");
                    w.append(COMMA_DELIMITER);
                    w.append("");
                    w.append(COMMA_DELIMITER);
                    w.append("");
                    w.append(COMMA_DELIMITER);

                    w.append(wordDetails[2]);
                    w.append(COMMA_DELIMITER);
                    w.append(wordDetails[3]);
                    w.append(COMMA_DELIMITER);
                    w.append(csIndicesAndPunctWordList.get(ii)[0]);
                    w.append(COMMA_DELIMITER);
                    w.append(csIndicesAndPunctWordList.get(ii)[1]);
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
                    + "02052019_"
                    + outFilename
                    + ".csv");
        }

    }

    private static void checkAlignment() {
        if (cleanSentenceIDList.size() != translationSentenceIDList.size()) {
            System.out.println("The two ID lists have different size!");
            System.out.println("Clean: " + cleanSentenceIDList.size());
            System.out.println("Translation: " + translationSentenceIDList.size());
        }

        for (int i = 0; i < cleanSentenceIDList.size(); i++) {
            if (!cleanSentenceIDList.get(i).equals(translationSentenceIDList.get(i))) {
                System.out.println("The two IDs are not the same!");
                System.out.println("cleanSentenceID: " + cleanSentenceIDList.get(i));
                System.out.println("translationSentenceID: " + translationSentenceIDList.get(i));
            }
        }

    }
}
