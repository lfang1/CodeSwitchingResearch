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
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Le
 */
public class FindChineseEnglishMixedToken {

    //intiailize linked hash map to store sentence id key with original sentence value
    private static LinkedHashMap<String, String> idOriginal = new LinkedHashMap<>();
    //intiailize linked hash map to store sentence id key with translation sentence value
    private static LinkedHashMap<String, String> idTranslation = new LinkedHashMap<>();

    public static void main(String[] args) {
        //read input to intialize two linked hash maps
        initializeTwoMaps();
        //fixSegmentation
        fixSegmentation();
    }

    private static void initializeTwoMaps() {
        //initialize input filename
        String inputFilename = "11062019_cs_index_input";
        //initialize a BufferedReader 
        BufferedReader br = null;
        //set file path of input
        File inputFile = new File("data/validation/"
                + inputFilename
                + ".csv");

        //intialize a string variable to store new input line
        String line = "";

        try {
            //open input stream input csv file for reading purpose.
            //create new input stream reader
            //create new buffered reader
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(inputFile), "UTF-8"));
            //Read all lines until reaching the end of file
            while ((line = br.readLine()) != null) {
                //Check if there are only 3 column in one line split by ","
                String[] columns = line.split(",");
                if (columns.length != 3) {
                    System.out.println("The line doesn't have only 3 column but " + columns.length + " column(s)");
                }
                //initialize sentenceId
                String sentenceId = columns[0];
                //initialize original sentence
                String originalSent = columns[1];
                idOriginal.put(sentenceId, originalSent);
                //initialize translation sentence
                String translationSent = columns[2];
                idTranslation.put(sentenceId, translationSent);
            }
            System.out.println("Size of idOriginal: " + idOriginal.size());
            System.out.println("Size of idTranslation: " + idTranslation.size());
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FindChineseEnglishMixedToken.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FindChineseEnglishMixedToken.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FindChineseEnglishMixedToken.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void fixSegmentation() {
        //FIXME: PSU_1680,现 at & tfamilyplan 空余 两 条 线 。,现 at & t 家庭 套餐 空余 两 条 线 。
        //      at & tfamilyplan to at&t family plan
        //FIXME: PIT_826,小区 全部 是 meyer'smanagement 管理 ， 安全 省心 ， 维修 即时 。,小区 全部 是 迈耶斯 物业 管理 ， 安全 省心 ， 维修 即时 。
        //   meyer'smanagement to meyer's management

        //intiailize regex CMU_534: 租约 ： 整套 公寓 一 室 一 厅 1005$/month （ 不 包 网 电 ） ， 420$/monthforlivingroom ， 585$/month for bedroom 。
        //FIXME: PSU_1688,四 层 小 书 架 small bookcase$83 .,四 层 小 书架 小 bookcase$83 。
        //PSU_1688,四 层 小书 架 smallbookcase $83 .,四 层 小 书架 小 bookcase $83 。
        String regexOne = "\\b\\w{10,}+\\b";
        Pattern patternOne = Pattern.compile(regexOne);

        //Original CMU_1774:目前 匹兹堡 选择 at & t 的 用户 最多 ] 五 人 at & t 手机 plan ， 因为 一 个 成员 离开 匹兹堡 ， 现在 招 一 名 新生 加入 !
        //at & t
        String regexTwo = "\\bat & t\\b";
        Pattern patternTwo = Pattern.compile(regexTwo);

        //Original PSU_992:本人 为 at & tfamilyplan 的 holder ， 邀请 新 成员 加入 。
        //at & tfamilyplan
        String regexThree = "\\bat & t\\w+\\b";
        Pattern patternThree = Pattern.compile(regexThree);
        
        //PSU_772,有意 帮 付$450securitydeposit,有意 帮 付$450 保证金
        String regexFour = "[\\p{IsHan}]+\\$\\d+";
        Pattern patternFour = Pattern.compile(regexFour);

        idOriginal.forEach((k, v) -> {

            //                Matcher originalMatcher = patternOne.matcher(v);
//                while(originalMatcher.find()) {
//                    System.out.println("Original " + sentenceId + ":");
//                    System.out.println(originalMatcher.group());
//                }
            Matcher originalMatcher = patternTwo.matcher(v);
            boolean isUpdated = false;
//            while (originalMatcher.find()) {
//                System.out.println("patternTwo found in Original " + k + ":" + v);
//                System.out.println(originalMatcher.group());
//                String newOriginalSent = v.replace("at & t", "at&t");
//                idOriginal.put(k, newOriginalSent);
//                isUpdated = true;
//            }
//            if(isUpdated) {
//                 System.out.println("After replace: " + idOriginal.get(k));
//                 isUpdated = false;
//            }
           
            originalMatcher = patternFour.matcher(v);
            while (originalMatcher.find()) {
                System.out.println("patternFour found in Original " + k + ":" + v);
                System.out.println(originalMatcher.group());
//                String newOriginalSent = v.replace("at & t", "at&t ");
//                idOriginal.put(k, newOriginalSent);
//                isUpdated = true;
            }
//            if(isUpdated) {
//                 System.out.println("After replace: " + idOriginal.get(k));
//                 isUpdated = false;
//            }
        });

        idTranslation.forEach((k, v) -> {

            //translation
//                Matcher translationMatcher = patternOne.matcher(v);
//                while(translationMatcher.find()) {
//                    System.out.println("Translation " + k + ":");
//                    System.out.println(translationMatcher.group());
//                }     
//            Matcher translationMatcher = patternTwo.matcher(v);
//            while (translationMatcher.find()) {
//                System.out.println("patternTwo found in translation " + k + ":" + v);
//                System.out.println(translationMatcher.group());
//            }
//
//            translationMatcher = patternThree.matcher(v);
//            while (translationMatcher.find()) {
//                System.out.println("patternThree found translation " + k + ":" + v);
//                System.out.println(translationMatcher.group());
//            }

        });

    }

}
