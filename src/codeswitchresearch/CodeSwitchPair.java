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
public class CodeSwitchPair {
    private int pairId;
    private String codeSwitch;
    private String translation;
    
    public CodeSwitchPair(int pairId, String codeSwitch, String translation) {
        this.pairId = pairId;
        this.codeSwitch = codeSwitch;
        this.translation = translation;
    }

    /**
     * @return the pairId
     */
    public int getPairId() {
        return pairId;
    }

    /**
     * @param pairId the pairId to set
     */
    public void setPairId(int pairId) {
        this.pairId = pairId;
    }

    /**
     * @return the codeSwitch
     */
    public String getCodeSwitch() {
        return codeSwitch;
    }

    /**
     * @param codeSwitch the codeSwitch to set
     */
    public void setCodeSwitch(String codeSwitch) {
        this.codeSwitch = codeSwitch;
    }

    /**
     * @return the translation
     */
    public String getTranslation() {
        return translation;
    }

    /**
     * @param translation the translation to set
     */
    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
