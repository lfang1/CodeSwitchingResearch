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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class AddNewVariables {

    private static String inputFilename = "input_R_v4_new_15_11_2019";
    private static LinkedList<String[]> sentenceList = new LinkedList<>();
    private static ArrayList<String> chinesePunctuationList = new ArrayList<>();
    private static ArrayList<String> englishPunctuationList = new ArrayList<>();
    private static LinkedHashMap<String, String> bilingualCorpusFrequencyDictionary = new LinkedHashMap<>();

    public static void main(String[] args) {
        initializeChinesePunctuationList();
        initializeEnglishPunctuationList();
        initializeBilingualCorpusDictionary();
        readCSVFile();
        writeCSVFile();
    }

    public static void initializeChinesePunctuationList() {
        chinesePunctuationList.add("？");
        chinesePunctuationList.add("，");
        chinesePunctuationList.add(",");
        chinesePunctuationList.add("！");
        chinesePunctuationList.add("；");
        chinesePunctuationList.add("：");
        chinesePunctuationList.add("（");
        chinesePunctuationList.add("）");
        chinesePunctuationList.add("［");
        chinesePunctuationList.add("］");
        chinesePunctuationList.add("【");
        chinesePunctuationList.add("】");
        chinesePunctuationList.add("。");
        chinesePunctuationList.add("「");
        chinesePunctuationList.add("」");
        chinesePunctuationList.add("﹁");
        chinesePunctuationList.add("﹂");
        chinesePunctuationList.add("“");
        chinesePunctuationList.add("”");
        chinesePunctuationList.add("‘");
        chinesePunctuationList.add("’");
        chinesePunctuationList.add("、");
        chinesePunctuationList.add("‧");
        chinesePunctuationList.add("《");
        chinesePunctuationList.add("》");
        chinesePunctuationList.add("〈");
        chinesePunctuationList.add("〉");
        chinesePunctuationList.add("……");
        chinesePunctuationList.add("——");
        chinesePunctuationList.add("—");
        chinesePunctuationList.add("～");
        chinesePunctuationList.add("__");
        chinesePunctuationList.add("﹏﹏");
        chinesePunctuationList.add("·");
        chinesePunctuationList.add("•");
    }

    private static void initializeEnglishPunctuationList() {
        englishPunctuationList = new ArrayList<>();
        englishPunctuationList.add("!");
        englishPunctuationList.add("\"");
        englishPunctuationList.add("#");
        englishPunctuationList.add("$");
        englishPunctuationList.add("%");
        englishPunctuationList.add("&");
        englishPunctuationList.add("'");
        englishPunctuationList.add("(");
        englishPunctuationList.add(")");
        englishPunctuationList.add("*");
        englishPunctuationList.add("+");
        englishPunctuationList.add(",");
        englishPunctuationList.add("-");
        englishPunctuationList.add(".");
        englishPunctuationList.add("/");
        englishPunctuationList.add(":");
        englishPunctuationList.add(";");
        englishPunctuationList.add("<");
        englishPunctuationList.add("=");
        englishPunctuationList.add(">");
        englishPunctuationList.add("?");
        englishPunctuationList.add("@");
        englishPunctuationList.add("[");
        englishPunctuationList.add("\\");
        englishPunctuationList.add("]");
        englishPunctuationList.add("^");
        englishPunctuationList.add("_");
        englishPunctuationList.add("`");
        englishPunctuationList.add("{");
        englishPunctuationList.add("|");
        englishPunctuationList.add("}");
        englishPunctuationList.add("~");
    }

    public static boolean isPunctuation(String chars) {
        if (englishPunctuationList.contains(chars)
                || chinesePunctuationList.contains(chars)) {
            return true;
        } else {
            return false;
        }
    }

    public static void initializeBilingualCorpusDictionary() {
        BufferedReader br = null;
        String line = "";
        String filename = "03082019_sorted_word_frequency_dictionary_in_bilingual_corpus";
        File fileDir = new File("data/add_new_variables/input/"
                + filename
                + ".csv");

        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            //Read to skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                String[] columns = line.split(",");
                bilingualCorpusFrequencyDictionary.put(columns[0], columns[1]);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
                System.out.println("bilingual corpus frequency dictionary has been initialized successfully.");
            } catch (IOException ex) {
                Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void readCSVFile() {
        BufferedReader br = null;
        String line = "";
        int invalidPairCount = 0;
        //double maxSurprisal = 0.0;
        File fileDir = new File("data/add_new_variables/input/"
                + inputFilename
                + ".csv");
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            //Read to skip header
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] sentenceDetails = line.split(",");
                //The previous length is 14, now is 15
                if (sentenceDetails.length != 15) {
                    System.out.println("line: " + sentenceDetails.length);
                } else {

                    //NOTE: the new string[] has 24 items, the previous one has 23 items
                    String[] appended = new String[24];
                    String englishWordLength = "";
                    String chineseWordLength = "";
                    String ifPreviousWordIsPunctuation = "";
                    String surprisalOfPreviousWord = "";
                    String bilingualCorpusFrequencyNegativeLn = "";
                    String translationBilingualCorpusFrequencyNegativeLn = "";
                    if (sentenceDetails[0].equals("code-switch")) {
                        bilingualCorpusFrequencyNegativeLn = bilingualCorpusFrequencyDictionary.get(sentenceDetails[7]);
                        //System.out.println("bilingualCorpusFrequencyNegativeLn: " + bilingualCorpusFrequencyNegativeLn);
                        try {
                            if (bilingualCorpusFrequencyNegativeLn == null) {
                                bilingualCorpusFrequencyNegativeLn = "";
                                //System.out.println(sentenceDetails[7] + " with null frequency negative Ln in : " + line);                               
                            } else if (!bilingualCorpusFrequencyNegativeLn.isEmpty()) {
                                bilingualCorpusFrequencyNegativeLn = String.valueOf(-Math.log(Double.parseDouble(bilingualCorpusFrequencyNegativeLn)));
                            }

//                            if (!bilingualCorpusFrequencyNegativeLn.isEmpty()) {
//                                bilingualCorpusFrequencyNegativeLn = String.valueOf(-Math.log(Double.parseDouble(bilingualCorpusFrequencyNegativeLn)));
//                            }
                            englishWordLength = String.valueOf(sentenceDetails[7].length());
                        } catch (NullPointerException ne) {
                            Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ne);
                            System.out.println("line: " + line);
                            System.out.println("bilingualCorpusFrequencyNegativeLn: " + bilingualCorpusFrequencyNegativeLn);
                            for (int i = 0; i < 15; i++) {
                                System.out.println(i + " " + sentenceDetails[i]);
                            }
                            System.exit(1);
                        }
//                        if (!bilingualCorpusFrequencyNegativeLn.isEmpty()) {
//                            bilingualCorpusFrequencyNegativeLn = String.valueOf(-Math.log(Double.parseDouble(bilingualCorpusFrequencyNegativeLn)));
//                        }
//                        englishWordLength = String.valueOf(sentenceDetails[6].length());
//                        if (!bilingualCorpusFrequencyNegativeLn.isEmpty()) {
//                            bilingualCorpusFrequencyNegativeLn = String.valueOf(-Math.log(Double.parseDouble(bilingualCorpusFrequencyNegativeLn)));
//                        }
//                        englishWordLength = String.valueOf(sentenceDetails[6].length());
                    }
                    String[] wordsInTranslation = sentenceDetails[5].split("\\s+");
                    int csWordIndex = Integer.valueOf(sentenceDetails[6]) - 1;
                    String[] surprisals = sentenceDetails[14].split("\\s+");

                    if (csWordIndex > 0) {
                        if (!isPunctuation(wordsInTranslation[csWordIndex - 1])) {
                            ifPreviousWordIsPunctuation = "0";
                        } else {
                            ifPreviousWordIsPunctuation = "1";
                        }
                    } else {
//                        System.out.println("Reach the beginning of the sentence!");
//                        System.out.println("Current index: " + csWordIndex);
                        ifPreviousWordIsPunctuation = "";
                    }

                    double sum = 0.0;
                    for (int i = 0; i < surprisals.length; i++) {
                        //replace the infinity by the max surprisal
                        if (surprisals[i].equals("Infinity")) {
                            surprisals[i] = "10.51329";
                        }
                        sum += Double.parseDouble(surprisals[i]);
//                        if((Double.parseDouble(surprisals[i]) - maxSurprisal) > 0) {
//                            maxSurprisal = Double.parseDouble(surprisals[i]);
//                        }
                    }

                    sentenceDetails[14] = String.join(" ", surprisals);
                    String averageSurprisal = String.valueOf(sum / surprisals.length);
                    if (surprisals.length != wordsInTranslation.length) {
//                        System.out.println("Sentence " + sentenceDetails[0] + "_" + sentenceDetails[1] + "_" + sentenceDetails[2]);
//                        System.out.println("surprisals and wordsInTranslation don't have the same size!");
//                        System.out.println("translaton: " + sentenceDetails[4]);
//                        System.out.println("surprisals: " + sentenceDetails[13]);
//                        System.out.println("surprisals length: " + surprisals.length);
//                        System.out.println("wordsInTranslation length: " + wordsInTranslation.length);
                        invalidPairCount++;
                        surprisalOfPreviousWord = "";
                    } else if (ifPreviousWordIsPunctuation.equals("0")) {
                        surprisalOfPreviousWord = surprisals[csWordIndex - 1];
                    } else if (ifPreviousWordIsPunctuation.equals("1")) {
                        if ((csWordIndex - 1) > 0) {
                            if (isPunctuation(wordsInTranslation[csWordIndex - 2])) {
                                surprisalOfPreviousWord = "";
//                                System.out.println("The token is still punctuation: " + wordsInTranslation[csWordIndex - 2]);
                            } else {
                                surprisalOfPreviousWord = surprisals[csWordIndex - 2];
                            }
                        } else {
                            surprisalOfPreviousWord = "";
//                            System.out.println("Reach the beginning of the sentence, " + "current index: " + (csWordIndex -1) + " " + sentenceDetails[4]);
                        }
                    } else {
                        surprisalOfPreviousWord = "";
                    }

                    translationBilingualCorpusFrequencyNegativeLn = bilingualCorpusFrequencyDictionary.get(sentenceDetails[8]);
                    if (translationBilingualCorpusFrequencyNegativeLn == null) {
                        translationBilingualCorpusFrequencyNegativeLn = "";
                    } else if (!translationBilingualCorpusFrequencyNegativeLn.isEmpty()) {
                        translationBilingualCorpusFrequencyNegativeLn = String.valueOf(-Math.log(Double.parseDouble(translationBilingualCorpusFrequencyNegativeLn)));
                    }
                    chineseWordLength = String.valueOf(sentenceDetails[8].length());

                    //check if cs word is root "0" for false; "1" for true
                    String ifItIsRoot = "";
                    //Distancee between the word and its parent node, 
                    String depDistance = "";
                    if (!sentenceDetails[13].equals("root")) {
                        ifItIsRoot = "0";
                        depDistance = String.valueOf(Math.abs(Integer.parseInt(sentenceDetails[12]) - Integer.parseInt(sentenceDetails[6])));
                    } else {
                        ifItIsRoot = "1";
                        depDistance = "0";
                    }

                    if (sentenceDetails[10].equals("Infinity")) {
//                        if (Double.parseDouble(sentenceDetails[10]) - maxSurprisal > 0) {
//                            maxSurprisal = Double.parseDouble(sentenceDetails[10]);
//                        }
                        //replace infinity to a max surprisal value
                        sentenceDetails[10] = "10.16642";
                    }

                    
                    if (!sentenceDetails[9].isEmpty()) {
                        sentenceDetails[9] = String.valueOf(-Math.log(Double.parseDouble(sentenceDetails[9])));
                    }

                    System.arraycopy(sentenceDetails, 0, appended, 0, 15);
                    appended[15] = averageSurprisal;
                    appended[16] = bilingualCorpusFrequencyNegativeLn;
                    appended[17] = translationBilingualCorpusFrequencyNegativeLn;
                    appended[18] = englishWordLength;
                    appended[19] = chineseWordLength;
                    appended[20] = depDistance;
                    appended[21] = ifItIsRoot;
                    appended[22] = ifPreviousWordIsPunctuation;
                    appended[23] = surprisalOfPreviousWord;
                    sentenceList.add(appended);
                     
                }

            }

            System.out.println(sentenceList.size() + " sentences have been added.");
            System.out.println("invalid pair count: " + invalidPairCount);
            //System.out.println(maxSurprisal);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
                System.out.println("input file has been read successfully.");
            } catch (IOException ex) {
                Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void writeCSVFile() {
        try {
            //Delimiter used in CSV file
            final String COMMA_DELIMITER = ",";
            final String NEW_LINE_SEPARATOR = "\n";
            //final String outputFilename = "03082019_" + "appended_" + inputFilename;
            final String outputFilename = "11152019_" + "appended_" + inputFilename;
            //CSV file header
            final String FILE_HEADER = "sent_type,"
                    + "university,"
                    + "sent_id,"
                    + "aligned_to,"
                    + "original_sentence,"
                    + "translation,"
                    + "word_id,"
                    + "first_cs_word_form,"
                    + "first_cs_word_translation,"
                    + "frequency_negative_ln_first_cs_word_trans,"
                    + "surprisal_first_cs_word_trans,"
                    + "pos_tag_first_cs_word_trans,"
                    + "governor_first_cs_word_trans,"
                    + "deprel_first_cs_word_trans,"
                    + "surprisal_values,"
                    + "average_surprisal,"
                    + "bilingual_corpus_frequency_negative_log_first_cs_word,"
                    + "bilingual_corpus_frequency_negative_log_first_cs_word_trans,"
                    + "length_first_cs_word_form,"
                    + "length_first_cs_word_trans,"
                    + "dependency_distance,"
                    + "if_it_is_root,"
                    + "if_previous_word_is_punctuation,"
                    + "surprisal_of_previous_word";
            File outputDir = new File("data/add_new_variables/output/"
                    + outputFilename
                    + ".csv");
            System.out.println("The file will be saved in: "
                    + outputDir.getPath());
            FileOutputStream is = new FileOutputStream(outputDir);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            w.append(FILE_HEADER);
            w.append(NEW_LINE_SEPARATOR);
            for (String[] sentenceDetails : sentenceList) {
                for (int i = 0; i < sentenceDetails.length - 1; i++) {
                    w.append(sentenceDetails[i]);
                    w.append(COMMA_DELIMITER);
                }
                w.append(sentenceDetails[sentenceDetails.length - 1]);
                w.append(NEW_LINE_SEPARATOR);
            }

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddNewVariables.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
