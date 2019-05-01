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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class MergeCSPosTagAndSurprisal {

    private static LinkedList<LinkedList> posTagList = new LinkedList<>();
    private static LinkedList<LinkedList> surprisalList = new LinkedList<>();
    private static LinkedList<LinkedList> mergedList = new LinkedList<>();

    private static LinkedList<String> posTagSentenceIDList = new LinkedList<>();
    private static LinkedList<String> surprisalSentenceIDList = new LinkedList<>();
    private static LinkedList<String> mergedSentenceIDList = new LinkedList<>();

    private static LinkedList<LinkedList> cleanSentenceList = new LinkedList<>();
//    private static LinkedList<LinkedList> translationSentenceList = new LinkedList<>();
    private static LinkedList<String> cleanSentenceIDList = new LinkedList<>();
//    private static LinkedList<String> translationSentenceIDList = new LinkedList<>();
    private static HashMap<String, Double> chineseFreqMap = new HashMap<>();
    private static HashMap<String, Double> englishFreqMap = new HashMap<>();
    private static LinkedList<LinkedList> translationCSTypeAndPunctSwitchSentenceList = new LinkedList<>();

    public static void main(String[] args) {
        chineseFreqMap = IndependentProbabilityDatabase.getIndependentProbabilities("55k-chinese-word-independent-probabilities.txt");
        englishFreqMap = IndependentProbabilityDatabase.getIndependentProbabilities("55k-english-word-independent-probabilities.txt");

        readConllCSVFile();
        readSurprisalCSVFile();
        readCleanCorpusFile();
        if (checkAlignment(posTagSentenceIDList, surprisalSentenceIDList)) {
            mergeConllAndSurprisalList();
            System.out.println(mergedList.size() + " list have been merged.");
            if (checkAlignment(cleanSentenceIDList, mergedSentenceIDList)) {
                saveToCSVFile();
            } else {
                findDifferentID(cleanSentenceIDList, mergedSentenceIDList);
            }
        }
    }

    private static void readCleanCorpusFile() {
        BufferedReader br = null;
        String cleanSentenceFilename = "all_clean_and_pns_ver";
        try {
            //Reading the text file
            File fileDir = new File("data/database/input/"
                    + cleanSentenceFilename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            br.readLine();
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
                        csType = typesOfCS[indicesOfCSList.indexOf(String.valueOf(i))];
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

    private static void mergeConllAndSurprisalList() {
        for (int i = 0; i < posTagList.size(); i++) {
            LinkedList<String[]> conllSentenceList = posTagList.get(i);
            LinkedList<String[]> surprisalSentenceList = surprisalList.get(i);
            LinkedList<String[]> mergedSentenceList = new LinkedList<>();
            for (int w = 0; w < conllSentenceList.size(); w++) {
                String[] appended = new String[9];
                if (conllSentenceList.get(w).length != 8) {
                    System.out.println("Wrong conll list item String[] length is not 8: " + conllSentenceList.get(w).length);
                    System.exit(1);
                }
                if (surprisalSentenceList.get(w).length != 5) {
                    System.out.println("Wrong conll list item String[] length is not 5: " + surprisalSentenceList.get(w).length);
                    System.exit(1);
                }
                System.arraycopy(conllSentenceList.get(w), 0, appended, 0, conllSentenceList.get(w).length);
                appended[8] = surprisalSentenceList.get(w)[4];
                mergedSentenceList.add(appended);

//                System.out.println(appended[0] + appended[1]);
////                for(String s : appended) {
////                    System.out.println(s);
////                }
//                System.out.println();
//                if(i == 10) {
//                    System.exit(0);
//                }
            }
            mergedSentenceIDList.add(mergedSentenceList.get(0)[0] + mergedSentenceList.get(0)[1]);
            mergedList.add(mergedSentenceList);
        }
    }

    private static boolean checkAlignment(LinkedList<String> listA, LinkedList<String> listB) {
        if (listA.size() != listB.size()) {
            System.out.println("The two ID lists have different size!");
            System.out.println("listA: " + listA.size());
            System.out.println("listB: " + listB.size());
            return false;
        }
        System.out.println("listA size: " + listA.size());
        System.out.println("listB size: " + listB.size());
       
        for (int i = 0; i < listA.size(); i++) {
            if (!listA.get(i).equals(listB.get(i))) {
                System.out.println(i);
                System.out.println("The two IDs are not the same!");
                System.out.println("listA: " + listA.get(i) + "|");
                System.out.println("listB: " + listB.get(i) + "|");
//                return false;
            }
        }
        return true;
    }

    private static void findDifferentID(LinkedList<String> listA, LinkedList<String> listB) {
        for (String s : listA) {
            if (!listB.contains(s)) {
                System.out.println(s + " is not exist in listB");
            }
        }
    }

    private static void readConllCSVFile() {
        BufferedReader br = null;
        String filename = "cs/03022019_conll2007_all_clean_and_pns_ver_id_line_added_output";
        try {
            //Reading the text file
            File fileDir = new File("data/conll2007/"
                    + filename
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
                if (columns.length != 1) {
//                    System.out.println("line: " + line + " with length " + columns.length);
                    if (isWord) {
                        String[] wordDetails = new String[8];
                        wordDetails[0] = source;
                        wordDetails[1] = sentenceID;
                        wordDetails[2] = String.valueOf(Integer.parseInt(columns[0]));
                        for (int i = 1; i < columns.length; i++) {
                            wordDetails[i + 2] = columns[i];
                        }
                        if (wordDetails[7] == null) {
                            wordDetails[7] = "";
                        }
                        wordList.add(wordDetails);
                    } else {
                        source = columns[0];
                        sentenceID = columns[1];
                        sentenceCounter++;
                        isWord = true;
                    }
                } else {
                    posTagList.add(wordList);
                    posTagSentenceIDList.add(source + sentenceID);
                    wordList = new LinkedList<>();
                    isWord = false;
                }
            }
            System.out.println(sentenceCounter + " conll sentences added.");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MergeNonCSPosTagAndSurprisal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MergeNonCSPosTagAndSurprisal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MergeNonCSPosTagAndSurprisal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void readSurprisalCSVFile() {
        BufferedReader br = null;
        String filename = "cs/03022019_all_clean_cs_id_line_added_surp_output";
        try {
            //Reading the text file
            File fileDir = new File("data/surprisal/output/"
                    + filename
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
                if (columns.length != 1) {
//                    System.out.println("line: " + line + " with length " + columns.length);
                    if (isWord) {
                        String[] wordDetails = new String[5];
                        wordDetails[0] = source;
                        wordDetails[1] = sentenceID;
                        wordDetails[2] = String.valueOf(Integer.parseInt(columns[0]));
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
                    surprisalList.add(wordList);
                    surprisalSentenceIDList.add(source + sentenceID);
                    wordList = new LinkedList<>();
                    isWord = false;
                }
            }
            System.out.println(sentenceCounter + " surprisal sentences added.");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MergeNonCSPosTagAndSurprisal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MergeNonCSPosTagAndSurprisal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MergeNonCSPosTagAndSurprisal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void saveToCSVFile() {
        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        final String outFilename = "03022019_bilingual_cs_database_v1";
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

                LinkedList<String[]> translationWordList = mergedList.get(i);
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
}
