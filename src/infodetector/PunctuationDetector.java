/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infodetector;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 *
 * @author Le
 */
public class PunctuationDetector {

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        test("a,b,c,d");
    }
    public static String[] splitWordByPunctuation(String chars) {
    	String markedChars = chars;
        markedChars = markedChars.replaceAll("\\p{Punct}", "\u2605" + "$0" + "\u2605");
        markedChars = markedChars.
                replace("？", "\u2605" + "？" + "\u2605").
                replace("，", "\u2605" + "，" + "\u2605").
                replace(",", "\u2605" + "，" + "\u2605").
                replace("！", "\u2605" + "！" + "\u2605").
                replace("；", "\u2605" + "；" + "\u2605").
                replace("：", "\u2605" + "：" + "\u2605").
                replace("（", "\u2605" + "（" + "\u2605").
                replace("）", "\u2605" + "）" + "\u2605").
                replace("［", "\u2605" + "［" + "\u2605").
                replace("］", "\u2605" + "］" + "\u2605").
                replace("【", "\u2605" + "【" + "\u2605").
                replace("】", "\u2605" + "】" + "\u2605").
                replace("。", "\u2605" + "。" + "\u2605").
                replace("「", "\u2605" + "「" + "\u2605").
                replace("」", "\u2605" + "」" + "\u2605").
                replace("﹁", "\u2605" + "﹁" + "\u2605").
                replace("﹂", "\u2605" + "﹂" + "\u2605").
                replace("“", "\u2605" + "“" + "\u2605").
                replace("”", "\u2605" + "”" + "\u2605").
                replace("‘", "\u2605" + "‘" + "\u2605").
                replace("’", "\u2605" + "’" + "\u2605").
                replace("、", "\u2605" + "、" + "\u2605").
                replace("‧", "\u2605" + "‧" + "\u2605").
                replace("《", "\u2605" + "《" + "\u2605").
                replace("》", "\u2605" + "》" + "\u2605").
                replace("〈", "\u2605" + "〈" + "\u2605").
                replace("〉", "\u2605" + "〉" + "\u2605").
                replace("…… ", "\u2605" + "…… " + "\u2605").
                replace("——", "\u2605" + "——" + "\u2605").
                replace("—", "\u2605" + "—" + "\u2605").
                replace("～", "\u2605" + "～" + "\u2605").
                replace("__", "\u2605" + "__" + "\u2605").
                replace("﹏﹏", "\u2605" + "﹏﹏" + "\u2605").    
                replace("·", "\u2605" + "·" + "\u2605").
                replace("•", "\u2605" + "•" + "\u2605").
                replace("\u2605\u2605", "\u2605");
        markedChars = markedChars.trim();
        
  
              
        return markedChars.split("\u2605");
    }
	
	public static String markPunctuation(String chars) {        
        String markedChars = chars;
        markedChars = markedChars.replaceAll("\\p{Punct}", "$0" + "\u2605");
        markedChars = markedChars.
                replace("？", "？" + "\u2605").
                replace("，", "，" + "\u2605").
                replace(",", "，" + "\u2605").
                replace("！", "！" + "\u2605").
                replace("；", "；" + "\u2605").
                replace("：", "：" + "\u2605").
                replace("（", "（" + "\u2605").
                replace("）", "）" + "\u2605").
                replace("［", "［" + "\u2605").
                replace("］", "］" + "\u2605").
                replace("【", "【" + "\u2605").
                replace("】", "】" + "\u2605").
                replace("。", "。" + "\u2605").
                replace("「", "「" + "\u2605").
                replace("」", "」" + "\u2605").
                replace("﹁", "﹁" + "\u2605").
                replace("﹂", "﹂" + "\u2605").
                replace("“", "“" + "\u2605").
                replace("”", "”" + "\u2605").
                replace("‘", "‘" + "\u2605").
                replace("’", "’" + "\u2605").
                replace("、", "、" + "\u2605").
                replace("‧", "‧" + "\u2605").
                replace("《", "《" + "\u2605").
                replace("》", "》" + "\u2605").
                replace("〈", "〈" + "\u2605").
                replace("〉", "〉" + "\u2605").
                replace("……", "……" + "\u2605").
                replace("——", "——" + "\u2605").
                replace("—", "—" + "\u2605").
                replace("～", "～" + "\u2605").
                replace("__", "__" + "\u2605").
                replace("﹏﹏", "﹏﹏" + "\u2605").    
                replace("·", "·" + "\u2605").
                replace("•", "•" + "\u2605");
        
        return markedChars;
    }
    
    public static boolean isPunctuation(String chars) {
    	String newChars = markPunctuation(chars);
        if(newChars.contains("\u2605")) {
            return true;
        } else {
            return false;
        }
    }
   
    public static ArrayList<String> splitPunctuationFromWord(String word) {
    	ArrayList<String> wordWithPunctuation = new ArrayList<>();
    	for(String w : word.split("\u2605")) {
    		wordWithPunctuation.add(w);
    	}
    	return wordWithPunctuation;
    }
    
    public static int numberOfPunctuation(String chars) {        
        return splitWordByPunctuation(chars).length - 1;
    }

    public static void test(String testS) {
    	System.out.println("TestString: " + testS);
    	System.out.println("Output: ");
    	for (String s : splitWordByPunctuation(testS)) {
    		System.out.println(s);
    	}
    }
    
    public static boolean isOnlyHyphenInWord(String word) {
    	for(String s : splitWordByPunctuation(word)) {
    		if(isPunctuation(s)&&!s.equals("-")) {
    			return false;
    		}
    	}
    	return true;
    }
}
