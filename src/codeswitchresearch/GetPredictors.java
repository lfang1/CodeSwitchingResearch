/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import codeswitchresearch.*;
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
public class GetPredictors {

    static String inputFilename = "input_R_v2";
    static LinkedList<String[]> sentenceList = new LinkedList<>();
    static ArrayList<String> chinesePunctuationList = new ArrayList<>();
    static LinkedHashMap<String, String> bilingualCorpusFrequencyDictionary = new LinkedHashMap<>();

    public static void main(String[] args) {
        initalizeChinesePunctuationList();
        initializeBilingualCorpusDictionary();
        readCSVFile();
        writeCSVFile();
    }

    public static void initalizeChinesePunctuationList() {
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

    public static boolean isPunctuation(String chars) {
        if (chars.matches("\\p{Punct}+")
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
            Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
                System.out.println("bilingual corpus frequency dictionary has been initialized successfully.");
            } catch (IOException ex) {
                Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void readCSVFile() {
        BufferedReader br = null;
        String line = "";
        int invalidPairCount = 0;
//        double maxSurprisal = 0.0;
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
                if (sentenceDetails.length != 14) {
                    System.out.println("line: " + line + " " + sentenceDetails.length);
                } else {
                    String[] appended = new String[23];
                    String englishWordLength = "";
                    String chineseWordLength = "";
                    String ifPreviousWordIsPunctuation = "";
                    String surprisalOfPreviousWord = "";
                    String bilingualCorpusFrequencyNegativeLn = "";
                    String translationBilingualCorpusFrequencyNegativeLn = "";
                    if (sentenceDetails[0].equals("code-switch")) {
                        bilingualCorpusFrequencyNegativeLn = bilingualCorpusFrequencyDictionary.get(sentenceDetails[6]);
                        try {
                            if (!bilingualCorpusFrequencyNegativeLn.isEmpty()) {
                                bilingualCorpusFrequencyNegativeLn = String.valueOf(-Math.log(Double.parseDouble(bilingualCorpusFrequencyNegativeLn)));
                            }
                            englishWordLength = String.valueOf(sentenceDetails[6].length());
                        } catch (NullPointerException ne) {
                            Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ne);
                            System.out.println("line: " + line);
                            System.out.println("bilingualCorpusFrequencyNegativeLn: " + bilingualCorpusFrequencyNegativeLn);
                            for (int i = 0; i < 14; i++) {
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
                    String[] wordsInTranslation = sentenceDetails[4].split("\\s+");
                    int csWordIndex = Integer.valueOf(sentenceDetails[5]) - 1;
                    String[] surprisals = sentenceDetails[13].split("\\s+");

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
                        if (surprisals[i].equals("Infinity")) {
                            surprisals[i] = "10.51329";
                        }
                        sum += Double.parseDouble(surprisals[i]);
//                        if((Double.parseDouble(surprisals[i]) - maxSurprisal) > 0) {
//                            maxSurprisal = Double.parseDouble(surprisals[i]);
//                        }
                    }
                    sentenceDetails[13] = String.join(" ", surprisals);
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

                    translationBilingualCorpusFrequencyNegativeLn = bilingualCorpusFrequencyDictionary.get(sentenceDetails[7]);
                    if (translationBilingualCorpusFrequencyNegativeLn == null) {
                        translationBilingualCorpusFrequencyNegativeLn = "";
                    } else if (!translationBilingualCorpusFrequencyNegativeLn.isEmpty()) {
                        translationBilingualCorpusFrequencyNegativeLn = String.valueOf(-Math.log(Double.parseDouble(translationBilingualCorpusFrequencyNegativeLn)));
                    }
                    chineseWordLength = String.valueOf(sentenceDetails[7].length());

                    //check if cs word is root "0" for false; "1" for true
                    String ifItIsRoot = "";
                    //Distancee between the word and its parent node, 
                    String depDistance = "";
                    if (!sentenceDetails[12].equals("root")) {
                        ifItIsRoot = "0";
                        depDistance = String.valueOf(Math.abs(Integer.parseInt(sentenceDetails[11]) - Integer.parseInt(sentenceDetails[5])));
                    } else {
                        ifItIsRoot = "1";
                        depDistance = "0";
                    }

                    if (sentenceDetails[9].equals("Infinity")) {
//                        if(Double.parseDouble(sentenceDetails[9]) - maxSurprisal > 0)
//                        maxSurprisal = Double.parseDouble(sentenceDetails[9]);
                        sentenceDetails[9] = "10.16642";
                    }

                    if (!sentenceDetails[8].isEmpty()) {
                        sentenceDetails[8] = String.valueOf(-Math.log(Double.parseDouble(sentenceDetails[8])));
                    }

                    System.arraycopy(sentenceDetails, 0, appended, 0, 14);
                    appended[14] = averageSurprisal;
                    appended[15] = bilingualCorpusFrequencyNegativeLn;
                    appended[16] = translationBilingualCorpusFrequencyNegativeLn;
                    appended[17] = englishWordLength;
                    appended[18] = chineseWordLength;
                    appended[19] = depDistance;
                    appended[20] = ifItIsRoot;
                    appended[21] = ifPreviousWordIsPunctuation;
                    appended[22] = surprisalOfPreviousWord;
                    sentenceList.add(appended);
                }

            }
            System.out.println(sentenceList.size() + " sentences have been added.");
            System.out.println("invalid pair count: " + invalidPairCount);
//            System.out.println(maxSurprisal);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
                System.out.println("input file has been read successfully.");
            } catch (IOException ex) {
                Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void writeCSVFile() {
        try {
            //Delimiter used in CSV file
            final String COMMA_DELIMITER = ",";
            final String NEW_LINE_SEPARATOR = "\n";
            final String outputFilename = "03082019_" + "appended_" + inputFilename;
            //CSV file header
            final String FILE_HEADER = "sent_type,"
                    + "university,"
                    + "sent_id,"
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
            Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetPredictors.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
