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
 * Read CoNLL2007 tsv file and write into csv file.
 *
 * @author Le
 */
public class ConllCSVWriter {

    private static LinkedList<LinkedList> sentenceList = new LinkedList<>();
    //private static String filename = "03022019_conll2007_all_clean_and_pns_ver_id_line_added";
    //private static String filename = "11142019_translation_sent_parser_output";
    private static String filename = "10312019_noncs_ctb_segmentation_fixed_parser_output";
    private static HashMap<String, Double> chineseFrequencyMap = new HashMap<>();
    private static HashMap<String, Double> englishFrequencyMap = new HashMap<>();

    public static void main(String[] args) {
        chineseFrequencyMap = IndependentProbabilityDatabase.getIndependentProbabilities("55k-chinese-word-independent-probabilities.txt");
        englishFrequencyMap = IndependentProbabilityDatabase.getIndependentProbabilities("55k-english-word-independent-probabilities.txt");
        readTSVFile();
        saveToCSVFile();
    }

    private static void readTSVFile() {
        BufferedReader br = null;

        try {
            //Reading the text file
            File fileDir = new File("data/conll2007/non-cs/input/"
                    + filename
                    + ".txt");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            boolean isWord = false;
            LinkedList<String[]> wordList = new LinkedList<>();

            //SOURCE: the corpus source
            String source = "";

            //Sentence ID: the sentence id
            String sentenceID = "";

            //ID:Word index, starting at 0 for each new 
            //sentence.
            String wordIndex = "";

            //FORM:Word form or punctuation symbol.
            String wordForm = "";

            //POSTAG:Fine-grained part-of-speech tag,
            //where the tagset depends on the language, or
            //identical to the coarse-grained part-of-speech
            //tag if not available.
            String posTag = "";

            //HEAD: Head of the current token, which is
            //either a value of ID or zero (0). Note that,
            //depending on the original treebank annotation,
            //there may be multiple tokens with HEAD=0.
            String head = "";

            //DEPREL: Dependency relation to the HEAD.
            //The set of dependency relations depends on
            //the particular language. Note that, depending
            //on the original treebank annotation, the dependency
            //relation when HEAD=0 may be meaningful
            //or simply ROOT.
            String depRel = "";

            int emptyLineCounter = 0;
            int sentenceCounter = 0;

            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] columns = line.split("\t");
                    if (isWord) {
                        wordIndex = String.valueOf(Integer.parseInt(columns[0]) - 1);
                        wordForm = columns[1];
                        posTag = columns[4];
                        head = columns[6];
                        depRel = columns[7];
                        String[] wordDetails = new String[]{source, sentenceID, wordIndex, wordForm, posTag, head, depRel};
                        wordList.add(wordDetails);
                    } else {
                        sentenceCounter++;
                        source = columns[1].substring(0, 3);
                        sentenceID = columns[1].substring(4);
                    }
                } else {
                    if (isWord) {
                        isWord = false;
                        sentenceList.add(wordList);

                        wordList = new LinkedList<>();
                    } else {
                        isWord = true;
                    }
                }
            }
            System.out.println(sentenceCounter + " sentences have been read.");

        } catch (Exception ex) {
            ex.printStackTrace();
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
        //CSV file header
        final String FILE_HEADER = "word index,"
                + "word form,"
                + "pos-tag,"
                + "head,"
                + "dep rel,"
                + "frequency";

        try {
            File outputfileName = new File("data/conll2007/non-cs/output/"
                    + filename
                    + "_conll2007"
                    + ".csv");
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);

            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            for (LinkedList<String[]> wordList : sentenceList) {
                w.append(wordList.get(0)[0]);
                w.append(COMMA_DELIMITER);
                w.append(wordList.get(0)[1]);
                w.append(NEW_LINE_SEPARATOR);
                for (String[] wordDetails : wordList) {
                    for (int i = 2; i < 7; i++) {
                        w.append(wordDetails[i]);
                        w.append(COMMA_DELIMITER);
                    }
//                    if (chineseFrequencyMap.get(wordDetails[3]) != null) {
//                        w.append(String.valueOf(chineseFrequencyMap.get(wordDetails[3])));
//                    } else {
//                        w.append("");
//                    } 
                    if (chineseFrequencyMap.get(wordDetails[3]) != null) {
                        w.append(String.valueOf(chineseFrequencyMap.get(wordDetails[3])));
                    } else if (englishFrequencyMap.get(wordDetails[3]) != null) {
                        w.append(String.valueOf(chineseFrequencyMap.get(wordDetails[3])));
                    } else {
                        w.append("");
                    }
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
                    + "data/conll2007/cs/"
                    + filename
                    + "_conll2007"
                    + ".csv");
        }
    }
}
