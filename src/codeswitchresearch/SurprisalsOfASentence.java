/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import java.util.ArrayList;

/**
 *
 * @author Le
 */
public class SurprisalsOfASentence {

    private String source = "";
    private int sentenceId = -1;
    private ArrayList<String> wordsInSentence = new ArrayList<>();
    private ArrayList<Double> surprisalsInSentence = new ArrayList<>();
    private int numberOfSentences = -1;
    private int numberOfWords = -1;
    private int numberOfOOVs = -1;
    private double zeroprobs = -1.0;
    private double logprob = -1.0;
    private double ppl = -1.0;
    private double ppl1 = -1.0;

    private double averageSurprisal = 1.0;
    private double averageSurprisalWithPunct = 1.0;
    private double surprisalOfFirstCodeSwitchedWord = 1.0;
    private double surprisalOfWordBeforeCS = 1.0;
    private double averageSurprisalOfSwitchPointWords = 1.0;
    //private double averageSurprisalOfCodeSwitchedWords = 1.0;
    //private double averageSurprisalOfNonCodeSwitchedWords = 1.0;
    private double averageSurprisalOfCodeSwitchedWordsWithPunct = 1.0;
    private double averageSurprisalOfNonCodeSwitchedWordsWithPunct = 1.0;
    private String corpusName = "";

    public SurprisalsOfASentence(int sentenceId,
            ArrayList<String> wordsInSentence,
            ArrayList<Double> surprisalsInSentence,
            int numberOfSentences,
            int numberOfWords,
            int numberOfOOVs) {
        this.sentenceId = sentenceId;
        this.wordsInSentence = wordsInSentence;
        this.surprisalsInSentence = surprisalsInSentence;
        this.numberOfSentences = numberOfSentences;
        this.numberOfWords = numberOfWords;
        this.numberOfOOVs = numberOfOOVs;
    }

    public SurprisalsOfASentence(int sentenceId,
            int numberOfWords,
            double averageSurprisalWithPunct,
            double surprisalOfFirstCodeSwitchedWord,
            double averageSurprisalOfSwitchPointWords,
            double averageSurprisalOfCodeSwitchedWordsWithPunct,
            double averageSurprisalOfNonCodeSwitchedWordsWithPunct,
            String corpusName) {
        this.sentenceId = sentenceId;
        this.numberOfWords = numberOfWords;
        this.averageSurprisalWithPunct = averageSurprisalWithPunct;
        this.surprisalOfFirstCodeSwitchedWord = surprisalOfFirstCodeSwitchedWord;
        this.averageSurprisalOfSwitchPointWords = averageSurprisalOfSwitchPointWords;
        this.averageSurprisalOfCodeSwitchedWordsWithPunct = averageSurprisalOfCodeSwitchedWordsWithPunct;
        this.averageSurprisalOfNonCodeSwitchedWordsWithPunct = averageSurprisalOfNonCodeSwitchedWordsWithPunct;
        this.corpusName = corpusName;
    }

    public SurprisalsOfASentence(int sentenceId,
            int numberOfWords,
            double averageSurprisalWithPunct,
            String corpusName) {
        this.sentenceId = sentenceId;
        this.numberOfWords = numberOfWords;
        this.averageSurprisalWithPunct = averageSurprisalWithPunct;
        this.corpusName = corpusName;
    }

    public SurprisalsOfASentence(String source, int sentenceId,
            ArrayList<String> wordsInSentence,
            ArrayList<Double> surprisalsInSentence,
            int numberOfSentences,
            int numberOfWords,
            int numberOfOOVs) {
        this.source = source;
        this.sentenceId = sentenceId;
        this.wordsInSentence = wordsInSentence;
        this.surprisalsInSentence = surprisalsInSentence;
        this.numberOfSentences = numberOfSentences;
        this.numberOfWords = numberOfWords;
        this.numberOfOOVs = numberOfOOVs;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the sentenceId
     */
    public int getSentenceId() {
        return sentenceId;
    }

    /**
     * @param sentenceId the sentenceId to set
     */
    public void setSentenceId(int sentenceId) {
        this.sentenceId = sentenceId;
    }

    /**
     * @return the wordsInSentence
     */
    public ArrayList<String> getWordsInSentence() {
        return wordsInSentence;
    }

    /**
     * @param wordsInSentence the wordsInSentence to set
     */
    public void setWordsInSentence(ArrayList<String> wordsInSentence) {
        this.wordsInSentence = wordsInSentence;
    }

    /**
     * @return the surprisalsInSentence
     */
    public ArrayList<Double> getSurprisalsInSentence() {
        return surprisalsInSentence;
    }

    /**
     * @param surprisalsInSentence the surprisalsInSentence to set
     */
    public void setSurprisalsInSentence(ArrayList<Double> surprisalsInSentence) {
        this.surprisalsInSentence = surprisalsInSentence;
    }

    /**
     * @return the numberOfSentences
     */
    public int getNumberOfSentences() {
        return numberOfSentences;
    }

    /**
     * @param numberOfSentences the numberOfSentences to set
     */
    public void setNumberOfSentences(int numberOfSentences) {
        this.numberOfSentences = numberOfSentences;
    }

    /**
     * @return the numberOfWords
     */
    public int getNumberOfWords() {
        return numberOfWords;
    }

    /**
     * @param numberOfWords the numberOfWords to set
     */
    public void setNumberOfWords(int numberOfWords) {
        this.numberOfWords = numberOfWords;
    }

    /**
     * @return the numberOfOOVs
     */
    public int getNumberOfOOVs() {
        return numberOfOOVs;
    }

    /**
     * @param numberOfOOVs the numberOfOOVs to set
     */
    public void setNumberOfOOVs(int numberOfOOVs) {
        this.numberOfOOVs = numberOfOOVs;
    }

    /**
     * @return the zeroprobs
     */
    public double getZeroprobs() {
        return zeroprobs;
    }

    /**
     * @param zeroprob the zeroprobs to set
     */
    public void setZeroprobs(double zeroprobs) {
        this.zeroprobs = zeroprobs;
    }

    /**
     * @return the logprob
     */
    public double getLogprob() {
        return logprob;
    }

    /**
     * @param logprob the logprob to set
     */
    public void setLogprob(double logprob) {
        this.logprob = logprob;
    }

    /**
     * @return the ppl
     */
    public double getPpl() {
        return ppl;
    }

    /**
     * @param ppl the ppl to set
     */
    public void setPpl(double ppl) {
        this.ppl = ppl;
    }

    /**
     * @return the ppl1
     */
    public double getPpl1() {
        return ppl1;
    }

    /**
     * @param ppl1 the ppl1 to set
     */
    public void setPpl1(double ppl1) {
        this.ppl1 = ppl1;
    }

    /**
     * @return the averageSurprisal
     */
    public double getAverageSurprisal() {
        return averageSurprisal;
    }

    /**
     * @param averageSurprisal the averageSurprisal to set
     */
    public void setAverageSurprisal(double averageSurprisal) {
        this.averageSurprisal = averageSurprisal;
    }

    /**
     * @return the averageSurprisalWithPunct
     */
    public double getAverageSurprisalWithPunct() {
        return averageSurprisalWithPunct;
    }

    /**
     * @param averageSurprisalWithPunct the averageSurprisalWithPunct to set
     */
    public void setAverageSurprisalWithPunct(double averageSurprisalWithPunct) {
        this.averageSurprisalWithPunct = averageSurprisalWithPunct;
    }

    /**
     * @return the surprisalOfFirstCodeSwitchedWord
     */
    public double getSurprisalOfFirstCodeSwitchedWord() {
        return surprisalOfFirstCodeSwitchedWord;
    }

    /**
     * @param surprisalOfFirstCodeSwitchedWord the
     * surprisalOfFirstCodeSwitchedWord to set
     */
    public void setSurprisalOfFirstCodeSwitchedWord(double surprisalOfFirstCodeSwitchedWord) {
        this.surprisalOfFirstCodeSwitchedWord = surprisalOfFirstCodeSwitchedWord;
    }

    /**
     * @return the averageSurprisalOfSwitchPointWords
     */
    public double getAverageSurprisalOfSwitchPointWords() {
        return averageSurprisalOfSwitchPointWords;
    }

    /**
     * @param averageSurprisalOfSwitchPointWords the
     * averageSurprisalOfSwitchPointWords to set
     */
    public void setAverageSurprisalOfSwitchPointWords(double averageSurprisalOfSwitchPointWords) {
        this.averageSurprisalOfSwitchPointWords = averageSurprisalOfSwitchPointWords;
    }

    /**
     * @return the averageSurprisalOfCodeSwitchedWordsWithPunct
     */
    public double getAverageSurprisalOfCodeSwitchedWordsWithPunct() {
        return averageSurprisalOfCodeSwitchedWordsWithPunct;
    }

    /**
     * @param averageSurprisalOfCodeSwitchedWordsWithPunct the
     * averageSurprisalOfCodeSwitchedWordsWithPunct to set
     */
    public void setAverageSurprisalOfCodeSwitchedWordsWithPunct(double averageSurprisalOfCodeSwitchedWordsWithPunct) {
        this.averageSurprisalOfCodeSwitchedWordsWithPunct = averageSurprisalOfCodeSwitchedWordsWithPunct;
    }

    /**
     * @return the averageSurprisalOfNonCodeSwitchedWordsWithPunct
     */
    public double getAverageSurprisalOfNonCodeSwitchedWordsWithPunct() {
        return averageSurprisalOfNonCodeSwitchedWordsWithPunct;
    }

    /**
     * @param averageSurprisalOfNonCodeSwitchedWordsWithPunct the
     * averageSurprisalOfNonCodeSwitchedWordsWithPunct to set
     */
    public void setAverageSurprisalOfNonCodeSwitchedWordsWithPunct(double averageSurprisalOfNonCodeSwitchedWordsWithPunct) {
        this.averageSurprisalOfNonCodeSwitchedWordsWithPunct = averageSurprisalOfNonCodeSwitchedWordsWithPunct;
    }

    /**
     * @return the corpusName
     */
    public String getCorpusName() {
        return corpusName;
    }

    /**
     * @param corpusName the corpusName to set
     */
    public void setCorpusName(String corpusName) {
        this.corpusName = corpusName;
    }

}
