/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import codeswitchresearch.IndependentProbabilityDatabase;
import codeswitchresearch.PunctuationDetector;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Identify the type of a String.
 * @author Le
 */
public class IdentifyWord {

    private ArrayList<String> chineseRelativeFrequencyList = new ArrayList<>();
    private ArrayList<String> englishRelativeFrequencyList = new ArrayList<>();
    private ArrayList<String> chinesePunctuationList = new ArrayList<>();

    public IdentifyWord() {
        initializeDictionary();
    }

    public void initializeDictionary() {
        chineseRelativeFrequencyList
                = IndependentProbabilityDatabase.getWordList(
                        "55k-chinese-word-independent-probabilities.txt");
        //Note: ● in 55k-english-word-probabilities.txt has been excluded 
        //by changing from ● to "●"
        englishRelativeFrequencyList
                = IndependentProbabilityDatabase.getWordList(
                        "55k-english-word-independent-probabilities.txt");
        initializeChinesePunctuationList();
    }

    //Return 0 if it is Chinese word
    //Return 1 if it is English word
    //Return 2 if it is Punctuation
    //Return 3 if it is none of above
    public int identifyTheWord(String word) {
        if (chineseRelativeFrequencyList.contains(word)) {
            return 0;
        } else if (englishRelativeFrequencyList.contains(word)) {
            return 1;
        } else if (!PunctuationDetector.isPunctuation(word)) {
            return 2; //token that doesn't contain punctuation and that is also not in the dictionaries
        } else if (Pattern.matches("\\p{Punct}{1}", word) || chinesePunctuationList.contains(word)) {
            //toke that is a single punctuation
            //Note: all ","(English comma) is replaced by "，" (Chinese comma) for using csv format
            return 3;
        } else if (word.contains("em@") || word.contains("pn-")) {
            return 4;
        } else {
            String[] wordChunks = PunctuationDetector.splitWordByPunctuation(word);
            int numberOfChunks = wordChunks.length;
            if (numberOfChunks == 2) {
                if (!PunctuationDetector.isPunctuation(wordChunks[0])) {
                    if (chineseRelativeFrequencyList.contains(wordChunks[0])) {
                        return 5;
                    } else if (englishRelativeFrequencyList.contains(wordChunks[0])) {
                        return 6;
                    } else if (wordChunks[0].matches("\\d+")) {
                        return 7;
                    } else {
                        return 8;
                    }
                } else {
                    return 11;
                }
            } else if (PunctuationDetector.isOnlyHyphenInWord(word)) {
                return 9;
            } else if (wordChunks[wordChunks.length - 1].equals(".")) {
                return 10;
            } else {
                return 11;
            }
        }
    }

    public int checkTheWord(String word) {
        if (chineseRelativeFrequencyList.contains(word)) {
            //token that is in Chinese Dictionary
            return 0;
        } else if (englishRelativeFrequencyList.contains(word)) {
            //token that is in English Dictionary
            return 1;
        } else if (!PunctuationDetector.isPunctuation(word)) {
            //token that doesn't contain punctuation and that is also not in the dictionaries
            return 2;
        } else if (Pattern.matches("\\p{Punct}{1}", word) || chinesePunctuationList.contains(word)) {
            //token that is a single punctuation
            //Note: all ","(English comma) is replaced by "，" (Chinese comma) for using csv format
            return 3;
        } else if (word.contains("em@") || word.contains("pn-")) {
            //token than is an email id or phone number id
            return 4;
        } else {
            //token than is an unknown word with punctuation
            return 5;
        }
    }

    private void initializeChinesePunctuationList() {
        chinesePunctuationList = new ArrayList<>();
        chinesePunctuationList.add("？");
        chinesePunctuationList.add("！");
        chinesePunctuationList.add("，");
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
        chinesePunctuationList.add("…… ");
        chinesePunctuationList.add("——");
        chinesePunctuationList.add("—");
        chinesePunctuationList.add("～");
        chinesePunctuationList.add("__");
        chinesePunctuationList.add("﹏﹏");
        chinesePunctuationList.add("·");
        chinesePunctuationList.add("•");
    }

}
