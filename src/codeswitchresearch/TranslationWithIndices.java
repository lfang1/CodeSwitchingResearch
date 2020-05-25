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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Get indices of code-switched word,code-switched word in translation, 
 * punctuation,and punctuation in translation.
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
        //saveToCSVFile(corpusName, csSentences);
        saveToCSVFile(csSentences);
        //test();
        //testFindClosestIndices();
    }

    public static void readTranslationCSVFile() {
        BufferedReader br = null;
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";
        IdentifyWord identifier = new IdentifyWord();

        try {
            //Reading the csv file
//            File fileDir = new File("data/cs-sentences-with-translated-sentences/ctb-segmented-11082018-"
//                    + corpusName
//                    + ".csv");
            File fileDir = new File("data/validation/11112019_cs_index_input"
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
//            //Read to skip the header
//            br.readLine();

            //Reading from the second line
            while ((line = br.readLine()) != null) {
                //FIXME: debug
//                if (csSentences.size() == 1) {
//                    return;
//                }
                String[] sentenceDetails = line.split(COMMA_DELIMITER);

                if (sentenceDetails.length > 0) {
                    if (sentenceDetails.length != 3) {
                        System.err.println("The sentenceDetails length is not 3, but " + sentenceDetails.length);
                        for (String s : sentenceDetails) {
                            System.out.println(s);
                        }
                    }
//                    int sentenceId = Integer.parseInt(sentenceDetails[0]);
                    String sentenceIdString = sentenceDetails[0];
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

                    int lastCodeSwitchIndex = -2;

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
                            //FIXME: In CMU_1 "气垫床" is unknow word, will fall into case 2
                            //房间 ： 房间 为 三 层 一 整 层 ， 由 阁楼 装修 而 成 ， 空间 很大 ， 家具 齐全 ， 有 一 张 大号 的 床 和 一 个 气垫床 ；
                            case 2:
                                //in CMU_1 word "气垫床" falls into case 2:
                                //pattern for Chinese characters
                                String chineseRegex = "[\\p{IsHan}]+";
                                Pattern chinesePattern = Pattern.compile(chineseRegex);
                                Matcher chineseMatcher = chinesePattern.matcher(word);

                                //FIXME: digitRegex is not working
                                String digitRegex = "^\\d+$";
                                Pattern digitPattern = Pattern.compile(digitRegex);
                                Matcher digitMatcher = chinesePattern.matcher(word);

                                //check if it is not a chinese word found
                                if (!chineseMatcher.matches()) {
                                    //If the previous word is CS word, then this is also considered as English
                                    if (i - lastCodeSwitchIndex == 1) {
                                        indicesOfCodeSwitchedWord.add(i);
                                        lastCodeSwitchIndex = i;
                                    }
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
                    ArrayList<String> repeatedCSWord = new ArrayList<>();
                    int lastTIndex = -2;
                    for (int i = 0; i < wordsInTranslatedSentence.size(); i++) {
                        String word = wordsInTranslatedSentence.get(i);
                        int type = identifier.checkTheWord(word);
                        //FIXME: When the translation of code-switch word already exists in the orignal sentence, it will not be considered as a translated code-switched word.
                        //check if the word in in original cs-sentence, it is not single punctation or email id and phone number id
                        // and it is a Chinese word in the dictionary
              
                        if (wordsInCSSentence.contains(word) && type != 3 && type != 4) {
                            //check if the repeated word  is already added
                            //FIXME repeate cs-word for multiple cs-segments cannot be identified 
                            if (repeatedCSWord.contains(word)) {
                                continue;
                            }
                            int occurCounterInOriginal = 0;
                            int occurCounterInTranslation = 0;
                            //count occurence of translation of cs-word in the orignal sentence
                            for (String w : wordsInCSSentence) {
                                if (w.equals(word)) {
                                    occurCounterInOriginal++;
                                }
                            }
                            //count occurence of translation of cs-word in the translation sentence
                            for (String w : wordsInTranslatedSentence) {
                                if (w.equals(word)) {
                                    occurCounterInTranslation++;
                                }
                            }
                            //check if translation of code-switch word occurs more times than it occurs in original sentence 
                            if (occurCounterInTranslation > occurCounterInOriginal) {
                                CSPhraseFinder cspf = new CSPhraseFinder();
                                //get cs-segment
                                ArrayList<ArrayList<Integer>> indicesOfCSSegment = cspf.findIndicesOfCSSegment(indicesOfCodeSwitchedWord, indicesOfPunctuation);
                                //check if there is only one cs-segment
                                if (indicesOfCSSegment.size() == 1) {
                                    //get the index of the first cs-word in this cs-segment
                                    int firstCSWordIndex = indicesOfCSSegment.get(0).get(0);
                                    int correctIndex = findCorrectIndex(word, firstCSWordIndex, wordsInCSSentence, wordsInTranslatedSentence);
                                    if (correctIndex == -1) {
                                        System.out.println("correct index not found for " + word + " in " + sentenceIdString);
                                        System.out.println(codeSwitchedSentence);
                                        System.out.println(translatedSentence);
                                    }
                                    indicesOfCodeSwitchedWordInTranslation.add(correctIndex);
                                    lastTIndex = correctIndex;
                                    repeatedCSWord.add(word);
                                } else {
                                    System.out.println("More than 1 repeated cs-segment: " + sentenceIdString);
                                }
                            }
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
                                //Unknown word (Chinese, English, or mixed words) with no punctuation inside;
                                case 2:
                                    //Note: in CMU_1 word "气垫床" falls into case 2:
                                    //pattern for Chinese characters
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

                    //check if a the item is not in ascending order.
//                    if(indicesOfCodeSwitchedWordInTranslation.size() > 1 && indicesOfCodeSwitchedWordInTranslation.get(0) > indicesOfCodeSwitchedWordInTranslation.get(1)) {
//                        System.out.println(indicesOfCodeSwitchedWordInTranslation.toString());
//                    }
                    Collections.sort(indicesOfCodeSwitchedWordInTranslation);
                    
                    //Save the CSS details in CSS object
                    CodeSwitchedSentence s = new CodeSwitchedSentence(sentenceIdString,
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

    public static void testFindCorrectIndex() {
        String repeatedTranslatedCSWord = "房子";
        ArrayList<String> wordsInCodeSwitchedSentence = new ArrayList<>(Arrays.asList("其他", "附近", "的", "房子", "可能", "有", "个别", "卧室", "或者", "house", "也", "可以", "夏天", "短", "租", "的", "。"));
        ArrayList<String> wordsInTranslatedSentence = new ArrayList<>(Arrays.asList("其他", "附近", "的", "房子", "可能", "有", "个别", "卧室", "或者", "整", "套", "房子", "也", "可以", "夏天", "短", "租", "的", "。"));
        //int result = findClosestIndices(repeatedTranslatedCSWord, indicesOfCodeSwitchedWord, indicesOfPunctuation, wordsInCodeSwitchedSentence, wordsInTranslatedSentence);
        //System.out.println(result);
    }

    //find the translated cs-word in translation
    public static int findCorrectIndex(String repeatedChineseWord, int firstCSWordIndex, ArrayList<String> wordsInCodeSwitchedSentence, ArrayList<String> wordsInTranslatedSentence) {
        int result = -1;
        //ArrayList<Integer> indicesOfRepeatedWordInOriginal = new ArrayList<>();
        ArrayList<Integer> indicesOfRepeatedWordInTranslation = new ArrayList<>();
        boolean repeatedChineseWordIsBeforeCSWord = false;
        for (int i = 0; i < wordsInCodeSwitchedSentence.size(); i++) {
            //get indices of the repeated word
            if (wordsInCodeSwitchedSentence.get(i).equals(repeatedChineseWord)) {
                if (i < firstCSWordIndex) {
                    repeatedChineseWordIsBeforeCSWord = true;
                } else {
                    repeatedChineseWordIsBeforeCSWord = false;
                }
            }
        }
        //fill indicesOfRepeatedWordInTranslation list
        for (int i = 0; i < wordsInTranslatedSentence.size(); i++) {
            if (wordsInTranslatedSentence.get(i).equals(repeatedChineseWord)) {
                indicesOfRepeatedWordInTranslation.add(i);
            }
        }
        //check if indicesOfRepeatedWordInTranslation is empty
        if (indicesOfRepeatedWordInTranslation.isEmpty()) {
            return result;
        }
        //check if repeated Chinese word is before cs-word
        if (repeatedChineseWordIsBeforeCSWord) {
            //assign result to the last index
            result = indicesOfRepeatedWordInTranslation.get(indicesOfRepeatedWordInTranslation.size() - 1);
        } else {
            //assign result to the first index
            result = indicesOfRepeatedWordInTranslation.get(0);
        }
        return result;
    }

    public static void saveToCSVFile(List<CodeSwitchedSentence> cssList)
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
//            File outputfileName = new File("data/translation-with-indices/"
//                    + "11102018-"
//                    + corpusName
//                    + ".csv");
            File outputfileName = new File("data/translation-with-indices/"
                    + "11112019_cs_index_added"
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
                    w.append(String.valueOf(css.getSentenceIdString()));
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
                        w.append("[]");
                        w.append(COMMA_DELIMITER);
                    } else {
                        w.append(css.getIndicesOfPunctuation().toString().replace(",", "_").replace(" ", ""));
                        w.append(COMMA_DELIMITER);
                    }
                    if (css.getIndicesOfPunctuationInTranslation().isEmpty()) {
                        w.append("[]");
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
                    + "11112019_cs_index_added"
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

    //    public static void saveToCSVFile(String corpusName, List<CodeSwitchedSentence> cssList)
//            throws FileNotFoundException, UnsupportedEncodingException {
//
//        //Delimiter used in CSV file
//        final String COMMA_DELIMITER = ",";
//        final String NEW_LINE_SEPARATOR = "\n";
//        //CSV file header
//
//        final String FILE_HEADER = "SentenceID,"
//                + "CodeSwitchedSentence,"
//                + "TranslatedSentence,"
//                + "UntranslatedWord,"
//                + "IndicesOfCodeSwitchedWord,"
//                + "IndicesOfCodeSwitchedWordInTranslation,"
//                + "IndicesOfPunctuation,"
//                + "IndicesOfPunctuationInTranslation";
//
//        try {
//            File outputfileName = new File("data/translation-with-indices/"
//                    + "11102018-"
//                    + corpusName
//                    + ".csv");
//            System.out.println("The file will be saved in: "
//                    + outputfileName.getPath());
//            FileOutputStream is = new FileOutputStream(outputfileName);
//            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
//            BufferedWriter w = new BufferedWriter(osw);
//
//            //Write the CSV file header
//            w.append(FILE_HEADER);
//
//            //Add a new line separator after the header
//            w.append(NEW_LINE_SEPARATOR);
//
//            for (CodeSwitchedSentence css : cssList) {
//                try {
//                    w.append(String.valueOf(css.getSentenceId()));
//                    w.append(COMMA_DELIMITER);
//                    String newSentence = css.getCodeSwitchedSentence();
//                    newSentence = newSentence.trim();
//                    w.append(newSentence);
//                    w.append(COMMA_DELIMITER);
//                    w.append(css.getTranslatedSentence());
//                    w.append(COMMA_DELIMITER);
//                    w.append(css.getUntranslatedSentence().toString().replace(",", "_").replace(" ", ""));
//                    w.append(COMMA_DELIMITER);
//                    w.append(css.getIndicesOfCodeSwitchedWord().toString().replace(",", "_").replace(" ", ""));
//                    w.append(COMMA_DELIMITER);
//                    w.append(css.getIndicesOfCodeSwitchedWordInTranslation().toString().replace(",", "_").replace(" ", ""));
//                    w.append(COMMA_DELIMITER);
//                    if (css.getIndicesOfPunctuation().isEmpty()) {
//                        w.append("[]");
//                        w.append(COMMA_DELIMITER);
//                    } else {
//                        w.append(css.getIndicesOfPunctuation().toString().replace(",", "_").replace(" ", ""));
//                        w.append(COMMA_DELIMITER);
//                    }
//                    if (css.getIndicesOfPunctuationInTranslation().isEmpty()) {
//                        w.append("[]");
//                    } else {
//                        w.append(css.getIndicesOfPunctuationInTranslation().toString().replace(",", "_").replace(" ", ""));
//                    }
//                    w.append(NEW_LINE_SEPARATOR);
//
//                } catch (IOException ex) {
//                    Logger.getLogger(TranslationWithIndices.class
//                            .getName()).log(Level.SEVERE, null, ex);
//                }
//            };
//
//            System.out.println("CSV file was created successfully !!!");
//
//            w.flush();
//            w.close();
//            System.out.println("The file has been saved.");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Problem writing to the "
//                    + "data/translation-with-indices/"
//                    + "11102018-"
//                    + corpusName
//                    + ".csv");
//        }
//    }
}
