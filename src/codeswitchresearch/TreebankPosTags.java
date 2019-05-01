/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Le
 */
public class TreebankPosTags {

    String tags[];
    ArrayList<String> tagList;

    public TreebankPosTags() {
        /*
        Tag	Description                     Example
        AD	adverb                          也
        AS	aspect marker                   着
        BA	in ba-construction              把
        CC	coordinating conjunction	和
        CD	cardinal number                 一百
        CS	subordinating conjunction	虽然
        DEC	in a relative-clause            的
        DEG	associative                     的
        DER	in V-de const. and V-de-R	得
        DEV	before VP                       地
        DT	determiner                      这
        ETC	for words                       等, 等等
        FW	foreign words                   A
        IJ	interjection                    哈哈
        JJ	other noun-modifer              新
        LB	被 in long bei-const            被
        LC	localizer                       里
        M	measure word                    个
        MSP	other particle                  所
        NN	common noun                     工作
        NR	proper noun                     中国
        NT	temporal noun                   目前
        OD	ordinal number                  第一
        ON	onomatopoeia                    刷，哗啦啦
        P	Prepositions (excluding 把 and 被)	在
        PN	pronoun                         我
        PU	punctuation                     标点
        SB	in short bei-const              被
        SP	sentence-final particle         吗
        URL     an URL link
        VA	predicative adjective           好
        VC	copula                          是
        VE	as the main verb                有
        VV	other verbs                     要
        X	numbers and units, mathematical sign	59mm
         */
        tags = new String[]{
            "AD",
            "AS",
            "BA",
            "CC",
            "CD",
            "CS",
            "DEC",
            "DEG",
            "DER",
            "DEV",
            "DT",
            "ETC",
            "FW",
            "IJ",
            "JJ",
            "LB",
            "LC",
            "M",
            "MSP",
            "NN",
            "NR",
            "NT",
            "OD",
            "ON",
            "P",
            "PN",
            "PU",
            "SB",
            "SP",
            "URL",
            "VA",
            "VC",
            "VE",
            "VV",
            "X"
        };

        tagList = new ArrayList<>(Arrays.asList(tags));
    }

    boolean isTagValid(String wordWithTag) {
        String[] tokens = wordWithTag.split("/");
        String tag = tokens[tokens.length-1];
        return tagList.contains(tag);
    }

    String[] getWordAndTag(String wordWithTag) {
        String[] tokens = wordWithTag.split("/");
        String word = "";       
        for(int i = 0; i < tokens.length - 1; i++) {
            word += tokens[i];          
        }
        
        String tag = tokens[tokens.length-1];
        return new String[] {word, tag};
    }
 
}
