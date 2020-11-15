/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentenceprocesser;

/**
 *
 * @author Le
 */
public class SentenceBoundaryMarker {

    public SentenceBoundaryMarker() {
    }

    public static String markSentenceBoundary(String sentence) {       
        String markedSentence = sentence;
        markedSentence = markedSentence.toLowerCase();
        //add star symbol after puntucation to mark the sentence boundary
        //unicode:\u2605 ★

        markedSentence = markedSentence.
                replace("？", "？" + "\u2605").
                replace("?", "?" + "\u2605").
                replace("！", "！" + "\u2605").
                replace("!", "!" + "\u2605").
                replace("；", "；" + "\u2605").
                replace(";", ";" + "\u2605").
                replace("。", "。" + "\u2605").
                replace(".", "." + "\u2605").
                replace("…… ", "…… " + "\u2605").
                replace("......", "......" + "\u2605").
                replace("﹏﹏", "﹏﹏" + "\u2605");
        return markedSentence;

    }
    
}
