/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Le
 */
public class PunctuationList {

    private ArrayList<String> chinesePunctuationList;
    private ArrayList<String> englishPunctuationList;
    private HashMap<String, String> englishToChinesePunctMap;
    //English punctuation list: '!', '"', '#', '$', '%', '&', "'", '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'
    //Chinese punctuation list: "？","，",",","！","；","：","（","）","［","］","【","】","。","「","」","﹁","﹂","“","”","‘","’","、","‧","《","》","〈","〉","……","——","—","～","__","﹏﹏","·", "•"

    public PunctuationList() {
        initializeChinesePunctuationList();
        initializeEnglishPunctuationList();
        initializeEnglishToChinesePuntMap();
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

    private void initializeEnglishPunctuationList() {
        englishPunctuationList = new ArrayList<>();
        englishPunctuationList.add("!");
        englishPunctuationList.add("\"");
        englishPunctuationList.add("#");
        englishPunctuationList.add("$");
        englishPunctuationList.add("%");
        englishPunctuationList.add("&");
        englishPunctuationList.add("'");
        englishPunctuationList.add("(");
        englishPunctuationList.add(")");
        englishPunctuationList.add("*");
        englishPunctuationList.add("+");
        englishPunctuationList.add(",");
        englishPunctuationList.add("-");
        englishPunctuationList.add(".");
        englishPunctuationList.add("/");
        englishPunctuationList.add(":");
        englishPunctuationList.add(";");
        englishPunctuationList.add("<");
        englishPunctuationList.add("=");
        englishPunctuationList.add(">");
        englishPunctuationList.add("?");
        englishPunctuationList.add("@");
        englishPunctuationList.add("[");
        englishPunctuationList.add("\\");
        englishPunctuationList.add("]");
        englishPunctuationList.add("^");
        englishPunctuationList.add("_");
        englishPunctuationList.add("`");
        englishPunctuationList.add("{");
        englishPunctuationList.add("|");
        englishPunctuationList.add("}");
        englishPunctuationList.add("~");
    }

    public boolean isChinesePunctuation(String input) {
        return chinesePunctuationList.contains(input);
    }

    public boolean isEnglishPunctuation(String input) {
        return englishPunctuationList.contains(input);
    }
    
    public boolean isInConversionList(String input) {
        return englishToChinesePunctMap.containsKey(input);
    }
    
    public String convertToChinesePunctuation(String englishPunct, String sentence) {
        if(englishToChinesePunctMap.containsKey(englishPunct)) {
            String correspondingChinesePunct = englishToChinesePunctMap.get(englishPunct);
            return sentence.replace(englishPunct, correspondingChinesePunct);
        } else {
            return sentence;
        }
    }

    private void initializeSingleEnglishPunctuationPattern() {
        String singleEnglishPunctuationRegex = "\\p{Punct}{1}";
    }

    private void initializeEnglishToChinesePuntMap() {
        englishToChinesePunctMap = new HashMap<>();
        englishToChinesePunctMap.put("!", "！");
        //'...' and "…" are known as neutral, vertical, straight, typewriter, dumb, or ASCII quotation marks. The left and right marks are identical. 
        //‘…’ and “…” are known as typographic, curly, curved, book, or smart quotation marks. The beginning marks are commas raised to the top of the line and rotated 180 degrees. The ending marks are commas raised to the top of the line.
        //englishToChinesePunctMap.put("\"", "”");
        //englishToChinesePunctMap.put("#", "#");
        //englishToChinesePunctMap.put("$","￥");
        //englishToChinesePunctMap.put("%","%");
        //englishToChinesePunctMap.put("&", "&");
        //englishToChinesePunctMap.put("'", "’");
        englishToChinesePunctMap.put("(", "（");
        englishToChinesePunctMap.put(")", "）");
        //englishToChinesePunctMap.put("*", "*");
        //englishToChinesePunctMap.put("+", "+");
        englishToChinesePunctMap.put(",", "，");
        //englishToChinesePunctMap.put("-", "-");
        englishToChinesePunctMap.put(".", "。");
        //englishToChinesePunctMap.put("/", "/");
        englishToChinesePunctMap.put(":", "：");
        englishToChinesePunctMap.put(";", "；");
        englishToChinesePunctMap.put("<", "《");
        //englishToChinesePunctMap.put("=", "=");
        englishToChinesePunctMap.put(">", "》");
        englishToChinesePunctMap.put("?", "？");
        //englishToChinesePunctMap.put("@", "@");
        englishToChinesePunctMap.put("[", "【");
        englishToChinesePunctMap.put("\\", "、");
        englishToChinesePunctMap.put("]", "】");
        //no corresponding of "^" in Chinese punctuation
        //englishToChinesePunctMap.put("^", "");
        //no corresponding of "_" in Chinese punctuation
        //englishToChinesePunctMap.put("_", "");
        //no corresponding of "`" in Chinese punctuation
        //englishToChinesePunctMap.put("`", "");
        //englishToChinesePunctMap.put("{", "{");
        //englishToChinesePunctMap.put("|", "|");
        //englishToChinesePunctMap.put("}", "}");
        //englishToChinesePunctMap.put("~", "~");
    }

}
