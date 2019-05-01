/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 *
 * @author Le
 */
public class MergeCodeSwitchPairs {

    CodeSwitchedSentence css;
    private ArrayList<String> wordsInSentence = new ArrayList<>();
    private ArrayList<Integer> indicesOfCodeSwitchedWord = new ArrayList<>();
    private ArrayList<Integer> indicesOfPunctuation = new ArrayList<>();
    private ArrayList<String> codeSwitchedPhrases = new ArrayList<>();
    private ArrayList<String> codeSwitchedWords = new ArrayList<>();
    private ArrayList<CodeSwitchPair> codeSwitchPairsList = new ArrayList<>();
    private ArrayList<CodeSwitchPair> mergedCodeSwitchPairsList = new ArrayList<>();
    private ArrayList<String> codeSwitches = new ArrayList<>();
    private ArrayList<String> translations = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> csIndicesList = new ArrayList<>();

    public MergeCodeSwitchPairs(CodeSwitchedSentence css) {
        this.css = css;
        this.wordsInSentence = css.getWordsInSentence();
        this.indicesOfCodeSwitchedWord = css.getIndicesOfCodeSwitchedWord();
        this.indicesOfPunctuation = css.getIndicesOfPunctuation();
        this.codeSwitchPairsList = css.getCodeSwitchPairsList();
        setCSIndicesList();
        setCodeSwitchesAndTranslation();
        setMergedCodeSwitchPairsList();       
    }

    public ArrayList<String> getCodeSwitchedPhrases() {
        return codeSwitchedPhrases;
    }

    public void setCSIndicesList() {
        if (codeSwitchPairsList.size() == 1) {
            csIndicesList.add(indicesOfCodeSwitchedWord);
            return;
        }
        int lastIndexOfCSWord = -2;
        ArrayList<Integer> csIndices = new ArrayList<>();
        for (int csIndex : indicesOfCodeSwitchedWord) {
            //Check if it is the first index
            if (lastIndexOfCSWord == -2) {
                csIndices.add(csIndex);
                lastIndexOfCSWord = csIndex;
                continue;
            }
            //Check if the two csIndex are continous
            if (csIndex != lastIndexOfCSWord + 1) {
                if (indicesOfPunctuation.contains(csIndex - 1) && lastIndexOfCSWord == csIndex - 2) {
                    csIndices.add(csIndex - 1);
                    csIndices.add(csIndex);
                } else {
                    csIndicesList.add(csIndices);
                    csIndices = new ArrayList<>();
                    csIndices.add(csIndex);
                }
            } else {
                csIndices.add(csIndex);
            }
            //check if csIndex is the last index
            if (csIndex == indicesOfCodeSwitchedWord.get(indicesOfCodeSwitchedWord.size() - 1)) {
                csIndicesList.add(csIndices);
            }
            lastIndexOfCSWord = csIndex;
        }
    }

    public ArrayList<ArrayList<Integer>> getCSIndicesList() {
        return csIndicesList;
    }
    
    public ArrayList<String> getCodeSwitches() {
        return codeSwitches;
    }

    public ArrayList<String> getTranslations() {
        return translations;
    }
    
    public void setMergedCodeSwitchPairsList() {
        //Get the original numbers of codeSwitch pairs
        int numberOfCodeSwitchPairs = codeSwitchPairsList.size();
        //Initialize the mergePoint
        int mergePoint = numberOfCodeSwitchPairs - 2;
        //Check if the number of codeSwith pairs is one
        if(numberOfCodeSwitchPairs == 1) {
            mergedCodeSwitchPairsList = codeSwitchPairsList;
            return;
        }
        //Loop from the last indices list to the first in the top-list
        for (int i = csIndicesList.size() - 1; i >= 0; i--) {
            ArrayList<Integer> il = new ArrayList<>();
            il = csIndicesList.get(i);
            //Loop from the last index to the first in the sub-list
            for (int index = il.size() - 1; index >= 0; index--) {
                int location = il.get(index);
                
                if (indicesOfPunctuation.contains(location)) {
                    String punctuation = wordsInSentence.get(location);
                    mergeTwoCodeSwitchPairsInList(mergePoint, punctuation);
                    mergePoint--;
                }
            }
            mergePoint--;
        }
        if (codeSwitches.size() != translations.size()) {
            System.err.println("codeSwitches and translations don't have the same size!");
            System.err.println("codeSwitches size: " + codeSwitches.size());
            System.err.println("translations size: " + translations.size());
        }
        for (int i = 0; i < codeSwitches.size(); i++) {
            CodeSwitchPair pair = new CodeSwitchPair(i, codeSwitches.get(i), translations.get(i));
            mergedCodeSwitchPairsList.add(pair);
        }
    }
    
    public ArrayList<CodeSwitchPair> getMergedCodeSwitchPairsList() {
        return mergedCodeSwitchPairsList;
    }
    
    public void mergeTwoCodeSwitchPairsInList(int mergePoint, String punctuation) {
        //Check if the codeSwitches and translations lists are the same size
        if (codeSwitches.size() != translations.size()) {
            System.err.println("codeSwitches and translations don't have the same size!");
            System.err.println("codeSwitches size: " + codeSwitches.size());
            System.err.println("translations size: " + translations.size());
            return;
        }
        try {
            //Merge the codeSwitches and translations
            String mergedCodeSwitch = codeSwitches.get(mergePoint) + " " + punctuation + " " + codeSwitches.get(mergePoint + 1);
            String mergedTranslation = translations.get(mergePoint) + " " + punctuation + " " + translations.get(mergePoint + 1);
            //Remove the items be merged
            codeSwitches.remove(mergePoint + 1);
            translations.remove(mergePoint + 1);
            codeSwitches.set(mergePoint, mergedCodeSwitch);
            translations.set(mergePoint, mergedTranslation);
        } catch (ArrayIndexOutOfBoundsException ae){
            System.err.println("An error occur at sentence" + css.getSentenceId());
            System.err.println("The size of codeSwitches: " + codeSwitches.size());
            System.err.println("The size of translations: " + translations.size());
            System.err.println("The mergePoint: " + mergePoint);
            ae.printStackTrace();
        }

    }

    private void setCodeSwitchesAndTranslation() {
        for(CodeSwitchPair pair : codeSwitchPairsList) {
            codeSwitches.add(pair.getCodeSwitch());
            translations.add(pair.getTranslation());
        }
    }

}
