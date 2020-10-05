/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentenceprocesser;

import java.util.ArrayList;

/**
 * A sentence that has all the punctuation been surrounded by star marks.
 * This class contains fields that including indices of code-switched words (English 
 * word), Chinese words, and punctuation.
 * @author Le
 */
public class MarkedSentence {

    private int sentenceID = 0;
    private boolean codeSwitchOccur = false;
    private ArrayList<Integer> indicesOfCodeSwitchedWord = new ArrayList<>();
    private ArrayList<Integer> indicesOfPunctuation = new ArrayList<>();
    private ArrayList<Integer> indicesOfChineseWord = new ArrayList<>();
    private ArrayList<String> wordsInSentence = new ArrayList<>();
    private ArrayList<String> unmarkedWordsInSentence = new ArrayList<>();
    private ArrayList<Double> independentProbabilities = new ArrayList<>();
//    private ArrayList<Double> bilingualCorpusProbabilities = new ArrayList<>();
    private ArrayList<String> codeSwitchedPhrases = new ArrayList<>();
    private int chineseWordCount = 0;
    private int englishWordCount = 0;
    private int unknownWordCount = 0;

    public MarkedSentence(int sentenceID, boolean codeSwitchOccur, ArrayList<Integer> indicesOfCodeSwitchedWord, ArrayList<Integer> indicesOfPunctuation, ArrayList<Integer> indicesOfChineseWord, ArrayList<String> wordsInSentence,
            int chineseWordCount, int englishWordCount, int unknownWordCount, ArrayList<Double> independentProbabilities) {
        this.sentenceID = sentenceID;
        this.codeSwitchOccur = codeSwitchOccur;
        this.indicesOfCodeSwitchedWord = indicesOfCodeSwitchedWord;
        this.indicesOfPunctuation = indicesOfPunctuation;
        this.indicesOfChineseWord = indicesOfChineseWord;
        this.wordsInSentence = wordsInSentence;
        this.chineseWordCount = chineseWordCount;
        this.englishWordCount = englishWordCount;
        this.unknownWordCount = unknownWordCount;
        this.independentProbabilities = independentProbabilities;
        setUnmarkedWordsInSentence();
        setCodeSwitchedPhrases();
    }

    public String getWholeSentence() {
        String wholeSentence = "";
        for (int i = 0; i < wordsInSentence.size(); i++) {
            wholeSentence += wordsInSentence.get(i) + " ";
        }
        wholeSentence = wholeSentence.replace("\u2022", "");
        wholeSentence = wholeSentence.replace(",", "，");
        wholeSentence = wholeSentence.trim();
        return wholeSentence;
    }

    /**
     * @return the sentenceID
     */
    public int getSentenceID() {
        return sentenceID;
    }

    /**
     * @param sentenceID the sentenceID to set
     */
    public void setSentenceID(int sentenceID) {
        this.sentenceID = sentenceID;
    }

    /**
     * @return the codeSwitchOccur
     */
    public boolean ifCodeSwitchOccur() {
        return codeSwitchOccur;
    }

    /**
     * @param codeSwitchOccur the codeSwitchOccur to set
     */
    public void setCodeSwitchOccur(boolean codeSwitchOccur) {
        this.codeSwitchOccur = codeSwitchOccur;
    }

    /**
     * @return the indicesOfCodeSwitchedWord
     */
    public ArrayList<Integer> getIndicesOfCodeSwitchedWord() {
        return indicesOfCodeSwitchedWord;
    }

    /**
     * @param indicesOfCodeSwitchedWord the indicesOfCodeSwitchedWord to set
     */
    public void setIndicesOfCodeSwitchedWord(ArrayList<Integer> indicesOfCodeSwitchedWord) {
        this.indicesOfCodeSwitchedWord = indicesOfCodeSwitchedWord;
    }

    /**
     * @return the indicesOfPunctuation
     */
    public ArrayList<Integer> getIndicesOfPunctuation() {
        return indicesOfPunctuation;
    }

    /**
     * @param indicedsOfPunctuation the indicesOfPunctuation to set
     */
    public void setIndicesOfPunctuation(ArrayList<Integer> indicesOfPunctuation) {
        this.indicesOfPunctuation = indicesOfPunctuation;
    }

    /**
     * @return the indicesOfChineseWord
     */
    public ArrayList<Integer> getIndicesOfChineseWord() {
        return indicesOfChineseWord;
    }

    /**
     * @param indicesOfChineseWord the indicesOfChineseWord to set
     */
    public void setIndicesOfChineseWord(ArrayList<Integer> indicesOfChineseWord) {
        this.indicesOfChineseWord = indicesOfChineseWord;
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
    public void setWords(ArrayList<String> wordsInSentence) {
        this.wordsInSentence = wordsInSentence;
    }

    /**
     * @return the unmarkedWordsInSentence
     */
    public ArrayList<String> getUnmarkedWordsInSentence() {
        return unmarkedWordsInSentence;
    }

    /**
     */
    public void setUnmarkedWordsInSentence() {
        unmarkedWordsInSentence = (ArrayList<String>) wordsInSentence.clone();
        indicesOfCodeSwitchedWord.forEach(i -> {
            String unmarkedWord = unmarkedWordsInSentence.get(i);
            unmarkedWord = unmarkedWord.replaceAll("\u2022", "");
            unmarkedWord = unmarkedWord.trim();
            unmarkedWordsInSentence.set(i, unmarkedWord);
        });
    }

    /**
     * @return the chineseWordCount
     */
    public int getChineseWordCount() {
        return chineseWordCount;
    }

    /**
     * @param chineseWordCount the chineseWordCount to set
     */
    public void setChineseWordCount(int chineseWordCount) {
        this.chineseWordCount = chineseWordCount;
    }

    /**
     * @return the englishWordCount
     */
    public int getEnglishWordCount() {
        return englishWordCount;
    }

    /**
     * @param englishWordCount the englishWordCount to set
     */
    public void setEnglishWordCount(int englishWordCount) {
        this.englishWordCount = englishWordCount;
    }

    /**
     * @return the unknownWordCount
     */
    public int getUnknownWordCount() {
        return unknownWordCount;
    }

    /**
     * @param unknownWordCount the unknownWordCount to set
     */
    public void setUnknownWordCount(int unknownWordCount) {
        this.unknownWordCount = unknownWordCount;
    }

    /**
     * @return the independentProbabilities
     */
    public ArrayList<Double> getIndependentProbabilities() {
        return independentProbabilities;
    }

    /**
     * @param independentProbabilities the independentProbabilities to set
     */
    public void setIndependentProbabilities(ArrayList<Double> independentProbabilities) {
        this.independentProbabilities = independentProbabilities;
    }

//    /**
//     * @return the bilingualCorpusProbabilities
//     */
//    public ArrayList<Double> getBilingualCorpusProbabilities() {
//        return bilingualCorpusProbabilities;
//    }
//   /**
//     * @param bilingualCorpusProbabilities the bilingualCorpusProbabilities to
//     * set
//     */
//    public void setBilingualCorpusProbabilities(ArrayList<Double> bilingualCorpusProbabilities) {
//        this.bilingualCorpusProbabilities = bilingualCorpusProbabilities;
//    }
    private void setCodeSwitchedPhrases() {
        int lastCodeSwitchedWordPosition = -2;
        String csPhrase = "";
        for (int i = 0; i < indicesOfCodeSwitchedWord.size(); i++) {
            if (indicesOfCodeSwitchedWord.size() == 1) {
                csPhrase = unmarkedWordsInSentence.get(indicesOfCodeSwitchedWord.get(i));
                csPhrase = csPhrase.trim();
                codeSwitchedPhrases.add(csPhrase);
                break;
            } else {
                if (i == 0) {
                    csPhrase = unmarkedWordsInSentence.get(indicesOfCodeSwitchedWord.get(i));
                    lastCodeSwitchedWordPosition = indicesOfCodeSwitchedWord.get(i);
                    continue;
                }
//FIX when the punctuation is between the code switched phrase, such as 'apple, banana, pear' as three code-switched phrase instead of one
                if (lastCodeSwitchedWordPosition == (indicesOfCodeSwitchedWord.get(i) - 1)) {
                    csPhrase = csPhrase + " " + unmarkedWordsInSentence.get(indicesOfCodeSwitchedWord.get(i));
                } else if(lastCodeSwitchedWordPosition == (indicesOfCodeSwitchedWord.get(i) - 2) && indicesOfPunctuation.contains(indicesOfCodeSwitchedWord.get(i) - 1))  {
                    csPhrase = csPhrase + " " + unmarkedWordsInSentence.get(indicesOfCodeSwitchedWord.get(i)-1) + " " + unmarkedWordsInSentence.get(indicesOfCodeSwitchedWord.get(i));
                    //DEBUG
                    /*
                    if (sentenceID == 107) {
                        System.out.println("Id:107");
                        System.out.println(csPhrase);
                        System.out.println(unmarkedWordsInSentence.toString());
                        System.out.println(indicesOfPunctuation.toString());
                    }
                    */
//                    csPhrase = csPhrase.replace("，", "_");
//                    csPhrase = csPhrase.replace(",", "_");
                } else {
                    csPhrase = csPhrase.trim();
                    codeSwitchedPhrases.add(csPhrase);
                    csPhrase = unmarkedWordsInSentence.get(indicesOfCodeSwitchedWord.get(i));
                }
                lastCodeSwitchedWordPosition = indicesOfCodeSwitchedWord.get(i);
            }
            if (i == indicesOfCodeSwitchedWord.size() - 1) {
                csPhrase = csPhrase.trim();
                codeSwitchedPhrases.add(csPhrase);
            }
        }
    }

    public ArrayList<String> getCodeSwitchedPhrases() {
        return codeSwitchedPhrases;
    }

    public int getPunctuationCount() {
        return indicesOfPunctuation.size();
    }

}
