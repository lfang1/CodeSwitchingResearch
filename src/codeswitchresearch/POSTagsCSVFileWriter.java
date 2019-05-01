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
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class POSTagsCSVFileWriter {

    private static LinkedList<CorpusSentence> sentenceList = new LinkedList<>();
    //cmucssa
    //psucssa
    //pittcssa
    private static String corpusName = "cmucssa";
    private static TreebankPosTags treebankPosTags = new TreebankPosTags();

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        readTagsFile();
        saveToCSVFile();
        saveSentenceToCSVFile(corpusName, sentenceList);
    }

    private static void readTagsFile() {
        BufferedReader br = null;

        try {
            //Reading the text file
            File fileDir = new File("data/part-of-speech-tags/cs/"
                    + "11112018-"
                    + corpusName
                    + "-cs-output-10g"
                    + ".txt");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            int lineIndex = 0;
            boolean isSentence = true;
            while ((line = br.readLine()) != null) {
                lineIndex++;
                if (!line.isEmpty()) {
                    if (isSentence) {
                        //System.out.println("Line " + lineIndex + ": " + line);
                        String[] wordsAndTags = extractWordsAndPosTags(line);
                        if (wordsAndTags.length == 0) {
                            //System.out.println("Line " + lineIndex + " is empty!");
                        } else {
                            //System.out.println("Line " + lineIndex + " words: " + wordsAndTags[0] + " \n pos tags: " + wordsAndTags[1]);
                            CorpusSentence aSentence = new CorpusSentence();
                            String[] words = wordsAndTags[0].split(" ");
                            String[] tags = wordsAndTags[1].split(" ");
                            if (words.length != tags.length) {
                                System.err.println("The words and tags don't have the same size! " + "words: " + words.length + " / tags: " + tags.length);
                            } else {
                                aSentence.setSource(corpusName);
                                aSentence.setWords(words);
                                aSentence.setPosTags(tags);
                                aSentence.setWordIndices();
                                sentenceList.add(aSentence);
                            }
                        }
                    }
                } else {
                    if (isSentence) {
                        isSentence = false;
                    } else {
                        isSentence = true;
                    }
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.out.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }

    }
    
    private static void saveSentenceToCSVFile(String corpusName, LinkedList<CorpusSentence> sentenceList) {
        //Delimiter used in CSV file
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header
        final String COMMA_DELIMITER = ",";
        final String FILE_HEADER = "sentence ID,"
                + "sentence";
        
        try {
            File outputfileName = new File("data/corpus/"
                    + "01212019-cs-sentence-only-"
                    + corpusName
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

            for (CorpusSentence aSentence : sentenceList) {
                try {                    
                    int sentenceId = aSentence.getSentenceID();
                    w.append(String.valueOf(sentenceId));
                    w.append(COMMA_DELIMITER);
                    String[] words = aSentence.getWords();
                    int length = words.length;
                    for (int i = 0; i < length - 1; i++) {
                        w.append(words[i]);
                        w.append(" ");
                    }
                    w.append(words[length - 1]);
                    w.append(NEW_LINE_SEPARATOR);

                } catch (IOException ex) {
                    Logger.getLogger(POSTagsCSVFileWriter.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            };

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + "data/corpus/"
                    + "01212019-cs-sentence-only-"
                    + corpusName
                    + ".csv");
        }
        
    }

    private static void saveToCSVFile() {
        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header
        final String FILE_HEADER = "source,"
                + "sentence ID,"
                + "word index,"
                + "word,"
                + "pos-tag,"
                + "surprisal,"
                + "cs,"
                + "translation";

        try {
            File outputfileName = new File("data/corpus/"
                    + "01212019-cs-"
                    + corpusName
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

            for (CorpusSentence aSentence : sentenceList) {
                try {
                    String source = aSentence.getSource();
                    int sentenceID = aSentence.getSentenceID();
                    int[] wordIndices = aSentence.getWordIndices();
                    String[] words = aSentence.getWords();
                    String[] posTags = aSentence.getPosTags();
                    int[] csStatuses = aSentence.getCsStatuses();
                    int length = words.length;
                    for (int i = 0; i < length; i++) {
                        w.append(source);
                        w.append(COMMA_DELIMITER);
                        w.append(String.valueOf(sentenceID));
                        w.append(COMMA_DELIMITER);
                        w.append(String.valueOf(wordIndices[i]));
                        w.append(COMMA_DELIMITER);
                        w.append(String.valueOf(words[i]));
                        w.append(COMMA_DELIMITER);
                        w.append(String.valueOf(posTags[i]));
                        w.append(COMMA_DELIMITER);
                        w.append("");
                        w.append(COMMA_DELIMITER);
                        w.append("");
                        w.append(COMMA_DELIMITER);
                        w.append("");
                        w.append(NEW_LINE_SEPARATOR);
                    }
                    w.append(NEW_LINE_SEPARATOR);

                } catch (IOException ex) {
                    Logger.getLogger(POSTagsCSVFileWriter.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            };

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + "data/corpus/"
                    + "01212019-cs-"
                    + corpusName
                    + ".csv");
        }

    }

    private static String[] extractWordsAndPosTags(String line) {
        String[] wordsInSentence = line.split("\\s+");
        String words = "";
        String tags = "";
        String[] wordsAndTags = new String[2];
        for (String wordWithTag : wordsInSentence) {
            if (treebankPosTags.isTagValid(wordWithTag)) {
                wordsAndTags = treebankPosTags.getWordAndTag(wordWithTag);
                words += wordsAndTags[0] + " ";
                tags += wordsAndTags[1] + " ";
            } else {
                //System.out.println("There are " + wordWithTag.split("/").length + " tokens");
                String[] testArray = wordWithTag.split("/");
                /*
                for(int i = 0; i < testArray.length; i++) {
                    System.out.println(testArray[i]);
                } 
                 */
                System.out.println("Invalid tag exist: " + wordWithTag);
                return new String[0];
            }
        }

        return new String[]{words, tags};
    }
}
