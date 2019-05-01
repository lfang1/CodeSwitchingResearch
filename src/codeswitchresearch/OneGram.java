package codeswitchresearch;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.HashMap;


/**
 *
 * @author Le
 */
public class OneGram {

    private static int lastAssginedID = 0;
    private int assignedID;
    private String word;
    private double independentFrequency = 0.0;
    private double datasetFrequency = 0.0;
    //1 means code-switched, 0 means not
    private int codeSwitched = 0;
    private int occurCounter = 0;;
    
    public OneGram(String word, double independentFrequency, 
            int codeSwitched) {
        lastAssginedID++;
        assignedID = lastAssginedID;
        this.word = word;
        this.independentFrequency = independentFrequency;
        //1 means code-switched, 0 means not
        this.codeSwitched = codeSwitched;
        occurCounter = 1;
        
    }    
    
    /**
     * @return the assignedID
     */
    public int getAssignedID() {
        return assignedID;
    }

    /**
     * @param assignedID the assignedID to set
     */
    public void setAssignedID(int assignedID) {
        this.assignedID = assignedID;
    }
    
    /**
     * @return the word
     */
    public String getWord() {
        return word;
    }

    /**
     * @param word the word to set
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * @return the independent frequency
     */
    public double getIndependentFrequency() {
        return independentFrequency;
    }   

    /**
     * @param independentFrequency
     */
    public void setIndependentFrequency(double independentFrequency) {
        this.independentFrequency = independentFrequency;
    }    

    /**
     * @return the codeSwitched, 1 means code-switched, 0 means not
     */
    public int ifItIsCodeSwitched() {
        return codeSwitched;
    }

    /**
     * @param codeSwitched the codeSwitched to set
     */
    public void setCodeSwitched(int codeSwitched) {
        this.codeSwitched = codeSwitched;
    }
    
    /**
     * @return the occurCounter
     */
    public int getOccurCounter() {
        return occurCounter;
    }

    /**
     * @param occurCounter the occurCounter to set
     */
    public void setOccurCounter(int occurCounter) {
        this.occurCounter = occurCounter;
    }
    
    public void addOccurCount() {
        occurCounter++;
    }
    
    /**
     * @param totalOneGramCount
     * @return the datasetFrequency
     */
    public double getDatasetFrequency(int totalOneGramCount) {
        return datasetFrequency = (double) occurCounter / totalOneGramCount;
    }

}
