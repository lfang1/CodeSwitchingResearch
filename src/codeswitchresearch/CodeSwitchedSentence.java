/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import codeswitchresearch.CodeSwitchPair;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 *
 * @author Le
 */
public class CodeSwitchedSentence {

    private int sentenceId;
    private String sentenceIdString;
    private String unmarkedWordsInSentence;
    private String codeSwitchedSentence;
    private String translatedSentence;
    private ArrayList<String> untranslatedSentence = new ArrayList<>();
    private ArrayList<String> wordsInSentence = new ArrayList<>();
    private ArrayList<String> newWordsInSentence = new ArrayList<>();
    private ArrayList<String> wordsInTranslatedSentence = new ArrayList<>();
    private int numberOfCodeSwitching;
    private ArrayList<Integer> indicesOfCodeSwitchedWord = new ArrayList<>();
    private ArrayList<Integer> indicesOfPunctuation = new ArrayList<>();
    private ArrayList<Integer> indicesOfCodeSwitchedWordInTranslation = new ArrayList<>();
    private ArrayList<Integer> indicesOfPunctuationInTranslation = new ArrayList<>();
    private LinkedHashMap<String, String> codeSwitchPairs = new LinkedHashMap<>();
    private LinkedHashMap<String, String> mergedCodeSwitchPairs = new LinkedHashMap<>();
    private ArrayList<CodeSwitchPair> codeSwitchPairsList = new ArrayList<>();
    private ArrayList<CodeSwitchPair> mergedCodeSwitchPairsList = new ArrayList<>();
    private ArrayList<String> addedWordList = new ArrayList<>();

    public CodeSwitchedSentence(int sentenceId,
            ArrayList<String> wordsInSentence,
            int numberOfCodeSwitching,
            ArrayList<Integer> indicesOfCodeSwitchedWord,
            ArrayList<Integer> indicesOfPunctuation,
            ArrayList<CodeSwitchPair> codeSwitchPairsList,
            String translatedSentence,
            ArrayList<String> addedWordList) {
        this.sentenceId = sentenceId;
        this.wordsInSentence = wordsInSentence;
        setUnmarkedWordsInSentence(wordsInSentence);
        this.numberOfCodeSwitching = numberOfCodeSwitching;
        this.indicesOfCodeSwitchedWord = indicesOfCodeSwitchedWord;
        this.indicesOfPunctuation = indicesOfPunctuation;
        this.codeSwitchPairs = codeSwitchPairs;
        this.codeSwitchPairsList = codeSwitchPairsList;
        //MergeCodeSwitchPairs merger = new MergeCodeSwitchPairs(this);
        //this.mergedCodeSwitchPairsList = merger.getMergedCodeSwitchPairsList();
        this.translatedSentence = translatedSentence;
        this.addedWordList = addedWordList;
    }

    public CodeSwitchedSentence(int sentenceId, String unmarkedWordsInSentence,
            String translatedSentence) {
        this.sentenceId = sentenceId;
        this.unmarkedWordsInSentence = unmarkedWordsInSentence;
        this.translatedSentence = translatedSentence;
    }

    public CodeSwitchedSentence(int sentenceId, ArrayList<String> newWordsInSentence,
            String translatedSentence,
            ArrayList<Integer> indicesOfCodeSwitchedWord,
            ArrayList<Integer> indicesOfPunctuation,
            ArrayList<String> addedWordList) {
        this.sentenceId = sentenceId;
        this.newWordsInSentence = newWordsInSentence;
        this.translatedSentence = translatedSentence;
        this.indicesOfCodeSwitchedWord = indicesOfCodeSwitchedWord;
        this.indicesOfPunctuation = indicesOfPunctuation;
        this.addedWordList = addedWordList;
    }
    
    public CodeSwitchedSentence(int sentenceId, String codeSwitchedSentence,
            String translatedSentence, 
            ArrayList<String> untranslatedSentence,
            ArrayList<Integer> indicesOfCodeSwitchedWord,
            ArrayList<Integer> indicesOfCodeSwitchedWordInTranslation,
            ArrayList<Integer> indicesOfPunctuation,           
            ArrayList<Integer> indicesOfPunctuationInTranslation) {
        this.sentenceId = sentenceId;
        this.codeSwitchedSentence = codeSwitchedSentence;
        this.translatedSentence = translatedSentence;
        this.untranslatedSentence = untranslatedSentence;
        this.indicesOfCodeSwitchedWord = indicesOfCodeSwitchedWord;
        this.indicesOfCodeSwitchedWordInTranslation = indicesOfCodeSwitchedWordInTranslation;
        this.indicesOfPunctuation = indicesOfPunctuation;
        this.indicesOfPunctuationInTranslation = indicesOfPunctuationInTranslation;
    }
    
    public CodeSwitchedSentence(String sentenceIdString, String codeSwitchedSentence,
            String translatedSentence, 
            ArrayList<String> untranslatedSentence,
            ArrayList<Integer> indicesOfCodeSwitchedWord,
            ArrayList<Integer> indicesOfCodeSwitchedWordInTranslation,
            ArrayList<Integer> indicesOfPunctuation,           
            ArrayList<Integer> indicesOfPunctuationInTranslation) {
        this.sentenceIdString = sentenceIdString;
        this.codeSwitchedSentence = codeSwitchedSentence;
        this.translatedSentence = translatedSentence;
        this.untranslatedSentence = untranslatedSentence;
        this.indicesOfCodeSwitchedWord = indicesOfCodeSwitchedWord;
        this.indicesOfCodeSwitchedWordInTranslation = indicesOfCodeSwitchedWordInTranslation;
        this.indicesOfPunctuation = indicesOfPunctuation;
        this.indicesOfPunctuationInTranslation = indicesOfPunctuationInTranslation;
    }

    public CodeSwitchedSentence() {
        this.sentenceId = -1;
        this.unmarkedWordsInSentence = "";
        this.translatedSentence = "";
    }

    /**
     * @return the sentenceId
     */
    public int getSentenceId() {
        return sentenceId;
    }
    
    /**
     * @return the sentenceIdString
     */
    public String getSentenceIdString() {
        return sentenceIdString;
    }

    /**
     * @param unmarkedWordsInSentence the unmarkedWordsInSentence to set
     */
    public void setUnmarkedWordsInSentence(ArrayList<String> wordsInSentence) {
        String sentence = "";
        for (String word : wordsInSentence) {
            sentence = sentence + " " + word;
        }
        sentence = sentence.trim();
        unmarkedWordsInSentence = sentence;
    }

    /**
     * @return the unmarkedWordsInSentence
     */
    public String getUnmarkedWordsInSentence() {
        return unmarkedWordsInSentence;
    }

    /**
     * @return the codeSwitchedSentence
     */
    public String getCodeSwitchedSentence() {
        return codeSwitchedSentence;
    }

    /**
     * @param codeSwitchedSentence the codeSwitchedSentence to set
     */
    public void setCodeSwitchedSentence(String codeSwitchedSentence) {
        this.codeSwitchedSentence = codeSwitchedSentence;
    }

    /**
     * @return the wordsInSentence
     */
    public ArrayList<String> getWordsInSentence() {
        return wordsInSentence;
    }

    /**
     * @return the newWordsInSentence
     */
    public ArrayList<String> getNewWordsInSentence() {
        return newWordsInSentence;
    }

    /**
     * @param newWordsInSentence the newWordsInSentence to set
     */
    public void setNewWordsInSentence(ArrayList<String> newWordsInSentence) {
        this.newWordsInSentence = newWordsInSentence;
    }

    /**
     * @return the wordsInTranslatedSentence
     */
    public ArrayList<String> getWordsInTranslatedSentence() {
        return wordsInTranslatedSentence;
    }

    /**
     * @param wordsInTranslatedSentence the wordsInTranslatedSentence to set
     */
    public void setWordsInTranslatedSentence(ArrayList<String> wordsInTranslatedSentence) {
        this.wordsInTranslatedSentence = wordsInTranslatedSentence;
    }

    /**
     * @return the numberOfCodeSwitching
     */
    public int getNumberOfCodeSwitching() {
        return numberOfCodeSwitching;
    }

    /**
     * @param numberOfCodeSwitching the numberOfCodeSwitching to set
     */
    public void setNumberOfCodeSwitching(int numberOfCodeSwitching) {
        this.numberOfCodeSwitching = numberOfCodeSwitching;
    }

    /**
     * @param indicesOfCodeSwitchedWord the indicesOfCodeSwitchedWord to set
     */
    public void setIndicesOfCodeSwitchedWord(ArrayList<Integer> indicesOfCodeSwitchedWord) {
        this.indicesOfCodeSwitchedWord = indicesOfCodeSwitchedWord;
    }

    /**
     * @return the indicesOfCodeSwitchedWord
     */
    public ArrayList<Integer> getIndicesOfCodeSwitchedWord() {
        return indicesOfCodeSwitchedWord;
    }

    /**
     * @param indicesOfPunctuation the indicesOfPunctuation to set
     */
    public void setIndicesOfPunctuation(ArrayList<Integer> indicesOfPunctuation) {
        this.indicesOfPunctuation = indicesOfPunctuation;
    }

    /**
     * @return the indicesOfPunctuation
     */
    public ArrayList<Integer> getIndicesOfPunctuation() {
        return indicesOfPunctuation;
    }

    /**
     * @return the indicesOfCodeSwitchedWordInTranslation
     */
    public ArrayList<Integer> getIndicesOfCodeSwitchedWordInTranslation() {
        return indicesOfCodeSwitchedWordInTranslation;
    }

    /**
     * @param indicesOfCodeSwitchedWordInTranslation the indicesOfCodeSwitchedWordInTranslation to set
     */
    public void setIndicesOfCodeSwitchedWordInTranslation(ArrayList<Integer> indicesOfCodeSwitchedWordInTranslation) {
        this.indicesOfCodeSwitchedWordInTranslation = indicesOfCodeSwitchedWordInTranslation;
    }

    /**
     * @return the indicesOfPunctuationInTranslation
     */
    public ArrayList<Integer> getIndicesOfPunctuationInTranslation() {
        return indicesOfPunctuationInTranslation;
    }

    /**
     * @param indicesOfPunctuationInTranslation the indicesOfPunctuationInTranslation to set
     */
    public void setIndicesOfPunctuationInTranslation(ArrayList<Integer> indicesOfPunctuationInTranslation) {
        this.indicesOfPunctuationInTranslation = indicesOfPunctuationInTranslation;
    }

    /**
     * @return the translatedSentence
     */
    public String getTranslatedSentence() {
        return translatedSentence;
    }

    /**
     * @param translatedSentence the translatedSentence to set
     */
    public void setTranslatedSentence(String translatedSentence) {
        this.translatedSentence = translatedSentence;
    }

    /**
     * @return the untranslatedSentence
     */
    public ArrayList<String> getUntranslatedSentence() {
        return untranslatedSentence;
    }

    /**
     * @param untranslatedSentence the untranslatedSentence to set
     */
    public void setUntranslatedSentence(ArrayList<String> untranslatedSentence) {
        this.untranslatedSentence = untranslatedSentence;
    }

    /**
     * @return the codeSwitchPairs
     */
    public LinkedHashMap<String, String> getCodeSwitchPairs() {
        return codeSwitchPairs;
    }

    /**
     * @param codeSwitchPairs the codeSwitchPairs to set
     */
    public void setCodeSwitchPairs(LinkedHashMap<String, String> codeSwitchPairs) {
        this.codeSwitchPairs = codeSwitchPairs;
    }

    /**
     * @return the mergedCodeSwitchPairs
     */
    public LinkedHashMap<String, String> getMergedCodeSwitchPairs() {
        return mergedCodeSwitchPairs;
    }

    /**
     * @param mergedCodeSwitchPairs the mergedCodeSwitchPairs to set
     */
    public void setMergedCodeSwitchPairs(LinkedHashMap<String, String> mergedCodeSwitchPairs) {
        this.mergedCodeSwitchPairs = mergedCodeSwitchPairs;
    }

    /**
     * @return the codeSwitchPairsList
     */
    public ArrayList<CodeSwitchPair> getCodeSwitchPairsList() {
        return codeSwitchPairsList;
    }

    /**
     * @param codeSwitchPairsList the codeSwitchPairsList to set
     */
    public void setCodeSwitchPairsList(ArrayList<CodeSwitchPair> codeSwitchPairsList) {
        this.codeSwitchPairsList = codeSwitchPairsList;
    }

    /**
     * @return the mergedCodeSwitchPairsList
     */
    public ArrayList<CodeSwitchPair> getMergedCodeSwitchPairsList() {
        return mergedCodeSwitchPairsList;
    }

    /**
     * @param mergedCodeSwitchPairsList the mergedCodeSwitchPairsList to set
     */
    public void setMergedCodeSwitchPairsList(ArrayList<CodeSwitchPair> mergedCodeSwitchPairsList) {
        this.mergedCodeSwitchPairsList = mergedCodeSwitchPairsList;
    }

    /**
     * @return the addedWordList
     */
    public ArrayList<String> getAddedWordList() {
        return addedWordList;
    }

    /**
     * @param addedCodeSwitchList the addedCodeSwitchList to set
     */
    public void setAddedWordList(ArrayList<String> addedWordList) {
        this.addedWordList = addedWordList;
    }

}
