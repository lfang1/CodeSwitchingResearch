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
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class MergeNonCSPosTagAndSurprisal {

    private static LinkedList<LinkedList> posTagList = new LinkedList<>();
    private static LinkedList<LinkedList> surprisalList = new LinkedList<>();
    private static LinkedList<LinkedList> mergedList = new LinkedList<>();
    private static LinkedList<LinkedList> cleanNonCSSentenceList = new LinkedList<>();
    private static LinkedList<LinkedList> punctSwitchList = new LinkedList<>();
    private static LinkedList<String> posTagSentenceIDList = new LinkedList<>();
    private static LinkedList<String> surprisalSentenceIDList = new LinkedList<>();
    private static LinkedList<String> mergedSentenceIDList = new LinkedList<>();
    private static LinkedList<String> cleanNonCSSentenceIDList = new LinkedList<>();

    public static void main(String[] args) {
        readConllCSVFile();
        readSurprisalCSVFile();
        readCTBSegmentedCorpusFile();
        if (checkAlignment(posTagSentenceIDList, surprisalSentenceIDList)) {
            mergeConllAndSurprisalList();
            System.out.println(mergedList.size() + " list have been merged.");
            if (checkAlignment(cleanNonCSSentenceIDList, mergedSentenceIDList)) {
                saveToCSVFile();
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

        for (int i = 0; i < listA.size(); i++) {
            if (!listA.get(i).equals(listB.get(i))) {
                System.out.println("The two IDs are not the same!");
                System.out.println("listA: " + listA.get(i));
                System.out.println("listB: " + listB.get(i));
                return false;
            }
        }
        return true;
    }

    private static void readConllCSVFile() {
        BufferedReader br = null;
        String filename = "10312019_noncs_ctb_segmentation_fixed_parser_output_conll2007";
        try {
            //Reading the text file
            File fileDir = new File("data/conll2007/non-cs/output/"
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
//                if (line.contains("END") && columns.length == 1) {
//                    System.out.println("Reach the end of the file.");
//                    break;
//                }
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
        String filename = "10312019_noncs_ctb_segmentation_fixed_surprisal_ngrams";
        try {
            //Reading the text file
            File fileDir = new File("data/surprisal/output/non-cs/"
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
//                if (line.contains("END") && columns.length == 1) {
//                    System.out.println("Reach the end of the file.");
//                    break;
//                }
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

    private static void readCTBSegmentedCorpusFile() {
        BufferedReader br = null;
        String ctbSegmentedNonCSSentenceFilename = "10312019_noncs_ctb_segmentation_output_fixed";
        try {
            //Reading the text file
            File fileDir = new File("data/validation/"
                    + ctbSegmentedNonCSSentenceFilename
                    + ".txt");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            boolean isSentence = false;
            String sentenceID = "";
            String source = "";
            PunctuationList punctList = new PunctuationList();
            //read until the end of file is reached
            while ((line = br.readLine()) != null) {
                if (isSentence) {
                    String wordID = "";
                    String wordForm = "";
                    String punctSwitch = "";
                    LinkedList<String[]> wordList = new LinkedList<>();
                    String[] wordsInSentence = line.split("\\s+");
                    LinkedList<String> punctSwitchWordList = new LinkedList<>();
                    for (int i = 0; i < wordsInSentence.length; i++) {
                        wordID = String.valueOf(i + 1);
                        wordForm = wordsInSentence[i];
                        if (punctList.isChinesePunctuation(wordForm)
                                || punctList.isEnglishPunctuation(wordForm)) {
                            punctSwitch = "1";
                        } else {
                            punctSwitch = "0";
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

                    isSentence = false;
                    source = "";
                    sentenceID = "";

                } else {
                    String[] sentenceInfo = line.split("_");
                    sentenceID = sentenceInfo[1];
                    source = sentenceInfo[0];
                    isSentence = true;
                }
            }
            System.out.println(cleanNonCSSentenceList.size() + " clean non-cs sentences added.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MergeNonCSPosTagAndSurprisal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MergeNonCSPosTagAndSurprisal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MergeNonCSPosTagAndSurprisal.class.getName()).log(Level.SEVERE, null, ex);
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
        final String outFilename = "11152019_bilingual_non_cs_database_v1";
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
            for (int i = 0; i < mergedList.size(); i++) {
                LinkedList<String[]> wordList = mergedList.get(i);
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
                    w.append(String.valueOf(Integer.parseInt(wordDetails[2]) + 1));
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
                    + outFilename
                    + ".csv");
        }
    }
}
