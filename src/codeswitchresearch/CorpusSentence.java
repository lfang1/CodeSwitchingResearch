/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

/**
 *
 * @author Le
 */
public class CorpusSentence {
    
    private static int lastAssignedSentenceID = 0;
    private String source;
    private int sentenceID;
    private int[] wordIndices;
    private String[] words;
    private String[] posTags;
    private double[] surprisals;
    private int[] csStatuses;
    private String[] translations;
    
    public CorpusSentence() {
        source = "";
        sentenceID = 0;
        setSentenceID();
        wordIndices = new int[10];
        words = new String[10];
        posTags = new String[10];
        surprisals = new double[10];
        csStatuses = new int[10];
        translations  = new String[10];
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
     * @return the sentenceID
     */
    public int getSentenceID() {
        return sentenceID;
    }

    /**
     * lastAssignedSentenceID the sentenceID to set
     */
    public void setSentenceID() {
        this.sentenceID = lastAssignedSentenceID;
        lastAssignedSentenceID++;
    }

    /**
     * @return the wordIndices
     */
    public int[] getWordIndices() {
        return wordIndices;
    }

    /**
     * assign the indices to the wordIndices
     */
    public void setWordIndices() {
        int length = words.length;
        int[] wordIndices = new int[length];
        for(int i = 0; i < length; i++) {
            wordIndices[i] = i;
        }
        this.wordIndices = wordIndices;
    }

    /**
     * @return the words
     */
    public String[] getWords() {
        return words;
    }

    /**
     * @param words the words to set
     */
    public void setWords(String[] words) {
        this.words = words;
    }

    /**
     * @return the posTags
     */
    public String[] getPosTags() {
        return posTags;
    }

    /**
     * @param posTags the posTags to set
     */
    public void setPosTags(String[] posTags) {
        this.posTags = posTags;
    }

    /**
     * @return the surprisals
     */
    public double[] getSurprisals() {
        return surprisals;
    }

    /**
     * @param surprisals the surprisals to set
     */
    public void setSurprisals(double[] surprisals) {
        this.surprisals = surprisals;
    }

    /**
     * @return the csStatuses
     */
    public int[] getCsStatuses() {
        return csStatuses;
    }

    /**
     * @param csStatuses the csStatuses to set
     */
    public void setCsStatuses(int[] csStatuses) {
        this.csStatuses = csStatuses;
    }

    /**
     * @return the translations
     */
    public String[] getTranslations() {
        return translations;
    }

    /**
     * @param translations the translations to set
     */
    public void setTranslations(String[] translations) {
        this.translations = translations;
    }
    
    public void setCSToZero() {
        int length = words.length;
        int[] csStatuses = new int[length];
        for(int i = 0; i < length; i++) {
            csStatuses[i] = 0;
        }
        this.csStatuses = csStatuses;
    }   
    
    
}
