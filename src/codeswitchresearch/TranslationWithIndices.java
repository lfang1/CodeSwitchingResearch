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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class TranslationWithIndices {

    private static LinkedList<CodeSwitchedSentence> csSentences = new LinkedList<>();
    //psucssa
    //cmucssa
    //pittcssa
    private static String corpusName = "pittcssa";

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        readTranslationCSVFile();
        saveToCSVFile(corpusName, csSentences);
        //test();
    }

    public static void readTranslationCSVFile() {
        BufferedReader br = null;
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";
        IdentifyWord identifier = new IdentifyWord();

        try {
            //Reading the csv file
            File fileDir = new File("data/cs-sentences-with-translated-sentences/ctb-segmented-11082018-"
                    + corpusName
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            //Read to skip the header
            br.readLine();
            //Reading from the second line
            while ((line = br.readLine()) != null) {
                String[] sentenceDetails = line.split(COMMA_DELIMITER);

                if (sentenceDetails.length > 0) {
                    if (sentenceDetails.length != 3) {
                        System.err.println("The sentenceDetails length is not 3, but " + sentenceDetails.length);
                        for (String s : sentenceDetails) {
                            System.out.println(s);
                        }
                    }
                    int sentenceId = Integer.parseInt(sentenceDetails[0]);
                    String codeSwitchedSentence = sentenceDetails[1];
                    codeSwitchedSentence = codeSwitchedSentence.trim();
                    ArrayList<String> wordsInCSSentence = new ArrayList<>();
                    for (String word : codeSwitchedSentence.split("\\s+")) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        wordsInCSSentence.add(word);
                    }

                    ArrayList<Integer> indicesOfCodeSwitchedWord = new ArrayList<>();
                    ArrayList<Integer> indicesOfPunctuation = new ArrayList<>();

                    int lastCodeSwitchIndex = -1;

                    for (int i = 0; i < wordsInCSSentence.size(); i++) {
                        String word = wordsInCSSentence.get(i);
                        int type = identifier.checkTheWord(word);
                        switch (type) {
                            //Chinese word found
                            case 0:
                                break;
                            //English word found
                            case 1:
                                indicesOfCodeSwitchedWord.add(i);
                                lastCodeSwitchIndex = i;
                                break;
                            //Unknown word with no punctuation inside found;
                            case 2:
                                if (i - lastCodeSwitchIndex == 1) {  //If the previous word is CS word, then this is also considered English
                                    indicesOfCodeSwitchedWord.add(i);
                                    lastCodeSwitchIndex = i;
                                }
                                break;
                            //Single punctuation or email id or phone number id
                            //Note: all ","(English comma) is replaced by "，" (Chinese comma) for using csv format
                            case 3:
                            case 4:
                                if (i - lastCodeSwitchIndex == 1) {  //If the previous word is CS word, then this is also considered English
                                    indicesOfCodeSwitchedWord.add(i);
                                    lastCodeSwitchIndex = i;
                                }
                                indicesOfPunctuation.add(i);
                                break;
                            //Unkownword found
                            case 5:
                                if (i - lastCodeSwitchIndex == 1) {  //If the previous word is CS word, then this is also considered English
                                    indicesOfCodeSwitchedWord.add(i);
                                    lastCodeSwitchIndex = i;
                                }
                                break;
                            // Unknown type of word found
                            default:
                                System.err.println("Unknown type of word in default switch: " + word);
                                break;
                        }
                    }
                    /*
                    ArrayList<ArrayList<String>> codeSwitchedPhrase = new ArrayList<>();
                    int lastCSIndex = -1;
                    ArrayList<String> onePhrase = new ArrayList<>();
                    for (Integer i : indicesOfCodeSwitchedWord) {
                        if (i == indicesOfCodeSwitchedWord.get(0)) {
                            onePhrase.add(wordsInCSSentence.get(i));
                            lastCSIndex = i;
                            if (indicesOfCodeSwitchedWord.size() == 1) {
                                codeSwitchedPhrase.add(onePhrase);
                                onePhrase = new ArrayList<>();
                            }
                            continue;
                        }
                        if (i - lastCSIndex == 1) {
                            onePhrase.add(wordsInCSSentence.get(i));
                            lastCSIndex = i;
                        } else {
                            codeSwitchedPhrase.add(onePhrase);
                            onePhrase = new ArrayList<>();
                            onePhrase.add(wordsInCSSentence.get(i));
                            lastCSIndex = i;
                        }
                        if (i == indicesOfCodeSwitchedWord.get(indicesOfCodeSwitchedWord.size() - 1)) {
                            codeSwitchedPhrase.add(onePhrase);
                            onePhrase = new ArrayList<>();
                        }
                    }
                     */
                    String translatedSentence = sentenceDetails[2];
                    translatedSentence = translatedSentence.trim();
                    ArrayList<String> wordsInTranslatedSentence = new ArrayList<>();
                    for (String word : translatedSentence.split("\\s+")) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        wordsInTranslatedSentence.add(word);
                    }

                    ArrayList<Integer> indicesOfCodeSwitchedWordInTranslation = new ArrayList<>();
                    ArrayList<Integer> indicesOfPunctuationInTranslation = new ArrayList<>();
                    ArrayList<String> untranslatedWord = new ArrayList<>();
                    int lastTIndex = -1;
                    for (int i = 0; i < wordsInTranslatedSentence.size(); i++) {
                        String word = wordsInTranslatedSentence.get(i);
                        int type = identifier.checkTheWord(word);

                        //When the translated code-switched word exists more than once in the orignal sentence, it will not be considered as a translated code-switched word.
                        if (wordsInCSSentence.contains(word) && type == 0) {
                            continue;
                        } else {
                            switch (type) {
                                //Chinese word found
                                case 0:
                                    indicesOfCodeSwitchedWordInTranslation.add(i);
                                    lastTIndex = i;
                                    break;
                                //English word found
                                case 1:
                                    untranslatedWord.add(word);
                                    indicesOfCodeSwitchedWordInTranslation.add(i);
                                    lastTIndex = i;
                                    break;
                                //Unknown word with no punctuation inside;
                                case 2:                               
                                        indicesOfCodeSwitchedWordInTranslation.add(i);
                                        lastTIndex = i;                                  
                                    break;
                                //Single punctuation or email id or phone number id or unknown word with punctuation
                                //Note: all ","(English comma) is replaced by "，" (Chinese comma) for using csv format
                                case 3:
                                case 4:
                                case 5:
                                    if (i - lastTIndex == 1) {  //If the previous word is CS word, then this is also considered English
                                        indicesOfCodeSwitchedWordInTranslation.add(i);
                                        lastTIndex = i;
                                    }
                                    indicesOfPunctuationInTranslation.add(i);
                                    break;
                                // Unknown type of word found
                                default:
                                    System.err.println("Unknown type of word in default switch: " + word);
                                    break;
                            }
                        }

                    }

                    //Save the CSS details in CSS object
                    CodeSwitchedSentence s = new CodeSwitchedSentence(sentenceId,
                            codeSwitchedSentence,
                            translatedSentence,
                            untranslatedWord,
                            indicesOfCodeSwitchedWord,
                            indicesOfCodeSwitchedWordInTranslation,
                            indicesOfPunctuation,
                            indicesOfPunctuationInTranslation);
                    csSentences.add(s);
                } else {
                    System.err.println("An error occur at sentence" + sentenceDetails[0] + ", the sentenceDetails length is " + sentenceDetails.length);
                }
            }
            /*            
            //Lets print the CodeSwitchedSentence list
            for (CodeSwitchedSentence s : csSentences) {
                System.out.println(s.getSentenceId() + "   " + s.getCodeSwitchedSentence() + "  " + s.getTranslatedSentence());             
            }
             */
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.out.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    }

    public static void saveToCSVFile(String corpusName, List<CodeSwitchedSentence> cssList)
            throws FileNotFoundException, UnsupportedEncodingException {

        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header

        final String FILE_HEADER = "SentenceID,"
                + "CodeSwitchedSentence,"
                + "TranslatedSentence,"
                + "UntranslatedWord,"
                + "IndicesOfCodeSwitchedWord,"
                + "IndicesOfCodeSwitchedWordInTranslation,"
                + "IndicesOfPunctuation,"
                + "IndicesOfPunctuationInTranslation";

        try {
            File outputfileName = new File("data/translation-with-indices/"
                    + "11102018-"
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

            for (CodeSwitchedSentence css : cssList) {
                try {
                    w.append(String.valueOf(css.getSentenceId()));
                    w.append(COMMA_DELIMITER);
                    String newSentence = css.getCodeSwitchedSentence();
                    newSentence = newSentence.trim();
                    w.append(newSentence);
                    w.append(COMMA_DELIMITER);
                    w.append(css.getTranslatedSentence());
                    w.append(COMMA_DELIMITER);
                    w.append(css.getUntranslatedSentence().toString().replace(",", "_").replace(" ", ""));
                    w.append(COMMA_DELIMITER);
                    w.append(css.getIndicesOfCodeSwitchedWord().toString().replace(",", "_").replace(" ", ""));
                    w.append(COMMA_DELIMITER);
                    w.append(css.getIndicesOfCodeSwitchedWordInTranslation().toString().replace(",", "_").replace(" ", ""));
                    w.append(COMMA_DELIMITER);
                    if (css.getIndicesOfPunctuation().isEmpty()) {
                        w.append("");
                        w.append(COMMA_DELIMITER);
                    } else {
                        w.append(css.getIndicesOfPunctuation().toString().replace(",", "_").replace(" ", ""));
                        w.append(COMMA_DELIMITER);
                    }
                    if (css.getIndicesOfPunctuationInTranslation().isEmpty()) {
                        w.append("");
                    } else {
                        w.append(css.getIndicesOfPunctuationInTranslation().toString().replace(",", "_").replace(" ", ""));
                    }
                    w.append(NEW_LINE_SEPARATOR);

                } catch (IOException ex) {
                    Logger.getLogger(TranslationWithIndices.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            };

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problem writing to the "
                    + "data/translation-with-indices/"
                    + "11102018-"
                    + corpusName
                    + ".csv");
        }
    }

    public static void test() {
        String csSentence = "北面 五 分钟 是 shady 最 繁华 的 walnut street ， 再 过去 就是 giant eagle 等 超市  。";
        String translatedSentence = "北面 五 分钟 是 沙地区 最 繁华 的 沃尔纳特 街 ， 再 过去 就是 巨鹰 超市 等 超市  。";
        ArrayList<String> wordsInCSSentence = new ArrayList();
        for (String s : csSentence.split("\\s+")) {
            if (s.isEmpty()) {
                continue;
            }
            wordsInCSSentence.add(s);
        }
        ArrayList<String> wordsInTranslatedSentence = new ArrayList<>();
        for (String s : translatedSentence.split("\\s+")) {
            if (s.isEmpty()) {
                continue;
            }
            wordsInTranslatedSentence.add(s);
        }

        ArrayList<String> copyOfWordsInTranslatedSentence = (ArrayList<String>) wordsInTranslatedSentence.clone();
        copyOfWordsInTranslatedSentence.remove(1);
        System.out.println("Size of copy list:" + copyOfWordsInTranslatedSentence.size());
        System.out.println("Size of original list:" + wordsInTranslatedSentence.size());

    }
}
