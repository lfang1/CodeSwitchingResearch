/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Le
 */
public class RetrieveCodeSwitchSegmentation {

    //initialize a linked hash map to store sentence id to original cs-sentence with ctb segmentation
    private static LinkedHashMap<String, String> idToCTBOriginalSentenceMap = new LinkedHashMap<>();
    //initialize a linked hash map to store sentence id to translation of original cs-sentence with ctb segmentation
    private static LinkedHashMap<String, String> idToCTBTranslationSentenceMap = new LinkedHashMap<>();
    //initialize a linked hash map to store sentence id to cs-phrases in original cs-sentence with original segmentation
    private static LinkedHashMap<String, String> idToCSPhraseOriginalSentenceMap = new LinkedHashMap<>();
    //initialize a linked hash map to store sentence id to cs-phrases in translation of cs-sentence with original segmentation
    private static LinkedHashMap<String, String> idToCSPhraseTranslationSentenceMap = new LinkedHashMap<>();
    //initialize a linked hash map to store sentence id to origianl cs-sentence with recovered original segmentation of cs-phrase(s)
    private static LinkedHashMap<String, String> idToCSPhraseSegmentationRecoveredMap = new LinkedHashMap<>();
    //initialize a linked hash map to store sentence id to untranslated words in translation of cs-sentence
    private static LinkedHashMap<String, String> idToUntranslatedWordMap = new LinkedHashMap<>();
    //initialize a ArrayList to store code-switch type
    private static ArrayList<String> csTypeList = new ArrayList<>();
    //initialize cs-phrase finder
    private static CSPhraseFinder csPhraseFinder = new CSPhraseFinder();

    private static IdentifyWord identifier = new IdentifyWord();

    public static void main(String[] args) {
        initializeIdToCTBSentenceMap();
        initializeIdToCSPhraseMap();
        recoverSegmentationForCSPhraseInOriginalSentAndTranslation();
        removeSegmentationForDollarSignAndDigital();
        separateLongWords();
        
        //removeSegmentationForDollarSignAndDigital();
        saveToCSVFile();
    }

    //Read input csv file to initialize idToCSPhraseOriginalSentenceMap
    private static void initializeIdToCSPhraseMap() {
        //Initialize input filename
        String inputFilename = "03022019_all_clean_and_pns_ver";
        //Initialize a BufferedReader 
        BufferedReader br = null;
        //Set file path of input
        File inputFile = new File("data/add-id-line/input/"
                + inputFilename
                + ".csv");

        //Intialize a string variable to store new input line
        String line = "";

        try {
            //open input stream input csv file for reading purpose.
            //create new input stream reader
            //create new buffered reader
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(inputFile), "UTF-8"));
            //read to skip header
            br.readLine();

            //Read all lines until reaching the end of file
            while ((line = br.readLine()) != null) {
                //Check if there are only 11 column in one line split by ","
                String[] columns = line.split(",");
                if (columns.length != 11) {
                    System.out.println("The line doesn't have only 11 column but " + columns.length + " column(s)");
                }
                //intialize sentence id
                String sentenceId = columns[10] + "_" + columns[0];
                //System.out.println(sentenceId);
                //intialize original sentence
                String originalSentence = columns[1];
                //System.out.println(originalSentence);
                String[] wordsInOriginalSent = originalSentence.split("\\s+");
                //remove the "[]" surround the String of cs-indices of original sentence
                String csIndicesString = columns[4].substring(1, columns[4].length() - 1);
                String[] csStringIndices = csIndicesString.split("_");
                //remove the "[]" surround the string of punctation indices of original sentence
                String csPunctIndicesStringOriginal = columns[6].substring(1, columns[6].length() - 1);
                String[] csPunctStringIndicesOriginal = csPunctIndicesStringOriginal.split("_");

                LinkedList<LinkedList<Integer>> csPhraseIndices = csPhraseFinder.findCSPhraseIndices(csStringIndices, csPunctStringIndicesOriginal);
                LinkedList<String> csPhrases = csPhraseFinder.findCSPhrase(csPhraseIndices, wordsInOriginalSent);
                String csPhrasesString = csPhraseFinder.listToString(csPhrases);
                idToCSPhraseOriginalSentenceMap.put(sentenceId, csPhrasesString);

                //initialize translation sentence
                String translationSentence = columns[2];
                //System.out.println(translationSentence);
                String[] wordsInTranslationSent = translationSentence.split("\\s+");
                //remove the "[]" surround the String of cs-indices of translation sentence
                String csIndicesStringTranslation = columns[5].substring(1, columns[5].length() - 1);
                String[] csStringIndicesTranslation = csIndicesStringTranslation.split("_");
                //remove the "[]" surround the string of punctation indices of translation sentence
                String csPunctIndicesStringTranslation = columns[7].substring(1, columns[7].length() - 1);
                String[] csPunctStringIndicesTranslation = csPunctIndicesStringTranslation.split("_");
                LinkedList<LinkedList<Integer>> csPhraseIndicesTranslation = csPhraseFinder.findCSPhraseIndices(csStringIndicesTranslation, csPunctStringIndicesTranslation);

                //intiailize code-switch type of the orignal cs-sentence
                String csType = columns[9];
                if (!csTypeList.contains(csType)) {
                    csTypeList.add(csType);
                }

                //initialize untranslated words in translation sentence
                String untranslated = columns[3];
                //System.out.println(untranslatedWord);
                if (!untranslated.equalsIgnoreCase("[]")) {
                    //System.out.println(sentenceId + ": " + untranslated + " " + csType);
                    //System.out.println("original sentence:   " + originalSentence);
                    //System.out.println("translation sentence:" + translationSentence + "\n");
                }
            }
            //Show the size of the list
            System.out.println("The size of idToCSPhraseOriginalSentenceMap: " + idToCSPhraseOriginalSentenceMap.size());
            br.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(RetrieveCodeSwitchSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RetrieveCodeSwitchSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RetrieveCodeSwitchSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void initializeIdToCTBSentenceMap() {
        //Initialize input filename
        String inputFilename = "10312019_cs_ctb_segmentation_output";
        //Initialize a BufferedReader 
        BufferedReader br = null;
        //Set file path of input
        File inputFile = new File("data/validation/"
                + inputFilename
                + ".txt");

        //Intialize a string variable to store new input line
        String line = "";
        //Initialize a lineCounter
        int lineCounter = 1;
        try {
            //open input stream input csv file for reading purpose.
            //create new input stream reader
            //create new buffered reader
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(inputFile), "UTF-8"));
            //Read all lines until reaching the end of file
            String sentenceId = "";
            String segmentedOriginalSent = "";
            String segmentedTranslationSent = "";
            while ((line = br.readLine()) != null) {
                //Check if there are only 1 column in one line split by ","
                String[] columns = line.split(",");
                if (columns.length != 1) {
                    System.out.println("The line doesn't have only 1 column but " + columns.length + " column(s)");
                }
                switch (lineCounter % 3) {
                    //it is sentence id line
                    case 1:
                        sentenceId = line;
                        break;
                    //it is segmented original sentence
                    case 2:
                        segmentedOriginalSent = line;
                        //System.out.println(sentenceId + ": " + segmentedOriginalSent);
                        idToCTBOriginalSentenceMap.put(sentenceId, segmentedOriginalSent);
                        break;
                    //it is segmented translation sentence
                    case 0:
                        segmentedTranslationSent = line;
                        //System.out.println(sentenceId + ": " + segmentedTranslationSent);
                        idToCTBTranslationSentenceMap.put(sentenceId, segmentedTranslationSent);
                        break;
                    default:
                        break;
                }
                lineCounter++;
            }
            br.close();

            //Show the size of the list
            System.out.println("The size of idToCTBOriginalSentenceMap: " + idToCTBOriginalSentenceMap.size());
            System.out.println("The size of idToCTBTranslationSentenceMap: " + idToCTBTranslationSentenceMap.size());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RetrieveCodeSwitchSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RetrieveCodeSwitchSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RetrieveCodeSwitchSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //remove segmentation for $ and digitials. E.g., change $ 1 to $1
    private static void removeSegmentationForDollarSignAndDigital() {
        //PSU_658: 卖 bus token ， $ 1 .
        String regexOne = "\\$\\s\\d+";
        Pattern patternOne = Pattern.compile(regexOne);
        //CMU_585: $410/monthplussharedutilities
        String regexTwo = "\\$\\d+/\\w+";
        Pattern patternTwo = Pattern.compile(regexTwo);
        //CMU_1395: $ 550 plusutilities ， pluslastmonthrentand$100 utilitysecuritydeposit 有 兴趣 请 联系 ： 我 （ em@77
        //PSU_1851: 所有 utility 我 都 会 交 ， ask for$450 / person / month .
        String regexThree = "\\w+\\$\\d+";
        Pattern patternThree = Pattern.compile(regexThree);
        //CMU_534: 租约 ： 整套 公寓 一 室 一 厅 1005$/month （ 不 包 网 电 ） ， 420$/monthforlivingroom ， 585$/month for bedroom 。
        String regexFour = "\\d+\\$/\\w+";
        Pattern patternFour = Pattern.compile(regexFour);
        //CMU_502: 价格 : $600一 个 月 ， 包 水 煤气 暖气 。
        String regexFive = "\\$\\d+[\\p{IsHan}]+";
        Pattern patternFive = Pattern.compile(regexFive);

        //before this while loop CMU_585: 房租 ： $ 410/monthplussharedutilities ；
        idToCTBOriginalSentenceMap.forEach((k, v) -> {

            Matcher sentenceMatcher = patternOne.matcher(v);
            while (sentenceMatcher.find()) {
                String targetString = sentenceMatcher.group();
                //System.out.println("Before replace: " + targetString);
                v = v.replace(targetString, targetString.replaceAll("\\s", ""));
                idToCTBOriginalSentenceMap.put(k, v);
                //System.out.println("After replace: " + v);
            }

            sentenceMatcher = patternTwo.matcher(v);
            //after the above while loop CMU_585: 房租 ： $410/monthplussharedutilities ；
            //before this while loop PSU_1851: 所有 utility 我 都 会 交 ， ask for$450/person/month .
            while (sentenceMatcher.find()) {
                //System.out.println("patternTwo finded: " + k + ": " + v);             
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                v = v.replace(targetString, targetString.replaceAll("\\s", "")).replaceAll("/", " / ");
                idToCTBOriginalSentenceMap.put(k, v);
                //System.out.println("After replace: " + v + "\n");
            }

            sentenceMatcher = patternThree.matcher(v);
            //After the above while loop PSU_1851: 所有 utility 我 都 会 交 ， ask for$450 / person / month .
            //After the above while loop CMU_585: 房租 ： $410 / monthplussharedutilities ；
            while (sentenceMatcher.find()) {
                //System.out.println("patternThree finded: " + k + ": " + v);             
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                v = v.replace(targetString, targetString.replaceAll("\\$", " \\$"));
                idToCTBOriginalSentenceMap.put(k, v);
                //System.out.println("After replace: " + v + "\n");
            }

            sentenceMatcher = patternFour.matcher(v);
            //After the above while loop PSU_1851: 所有 utility 我 都 会 交 ， ask for $450 / person / month .
            //before this loop CMU_534: 租约 ： 整套 公寓 一 室 一 厅 1005$/month （ 不 包 网 电 ） ， 420$/monthforlivingroom ， 585$/month for bedroom 。
            while (sentenceMatcher.find()) {
                //System.out.println("patternThree finded: " + k + ": " + v);             
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                v = v.replace(targetString, targetString.replaceAll("/", " / "));
                idToCTBOriginalSentenceMap.put(k, v);
                //System.out.println("After replace: " + v + "\n");
            }
            //After the above loop CMU_534: 租约 ： 整套 公寓 一 室 一 厅 1005$ / month （ 不 包 网 电 ） ， 420$ / monthforlivingroom ， 585$ / month for bedroom 。

            //After the above loop CMU_534: 租约 ： 整套 公寓 一 室 一 厅 1005$/ 每 月 （ 不 包 网 电 ） ， 客厅 420$ / 每 月 ， 卧室 585$/ 每 月 。
            sentenceMatcher = patternFive.matcher(v);
            //before the loop CMU_502: the price : $600一 个 月 ， 包 水 煤气 暖气 。
            while (sentenceMatcher.find()) {
                //System.out.println("patternFive finded: " + k + ": " + v);             
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                v = v.replace(targetString, targetString.replaceAll("[\\p{IsHan}]+", " $0"));
                idToCTBOriginalSentenceMap.put(k, v);
                //System.out.println("After replace: " + v + "\n");
            }
            //after the loop CMU_534: the price : $600 一 个 月 ， 包 水 煤气 暖气 。

        });

        //check the output
//        idToCTBOriginalSentenceMap.forEach((k, v) -> {
//            String regex = "\\$";
//            Pattern pattern = Pattern.compile(regex);
//            Matcher sentenceMatcher = pattern.matcher(v);
//            if (sentenceMatcher.find()) {
//                //System.out.println(k + ": " + v);
//            }
//        });
        //before this while loop CMU_585: $ 410
        idToCTBTranslationSentenceMap.forEach((k, v) -> {
            Matcher sentenceMatcher = patternOne.matcher(v);
            //Before replace: CMU_502: $ 600
            while (sentenceMatcher.find()) {
                String targetString = sentenceMatcher.group();
                //System.out.println("Before replace: " + k + ": "+ targetString);
                v = v.replace(targetString, targetString.replaceAll("\\s", ""));
                idToCTBTranslationSentenceMap.put(k, v);
                //System.out.println("After replace: " + v);
            }
            //After replace: CMU_502: 价格 : $600一 个 月 ， 包 水 煤气 暖气 。
            sentenceMatcher = patternTwo.matcher(v);
            //after the above while loop CMU_585: 房租 ： $410 / 每 月 ， 外加 均摊 服务费
            //before this loop CMU_1225: 只 需 $280/mo ， 本人 承担 使用费 和 网络 费用 。
            while (sentenceMatcher.find()) {
                //System.out.println("patternTwo finded: " + k + ": " + v);             
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                v = v.replace(targetString, targetString.replaceAll("\\s", "")).replaceAll("/", " / ");
                idToCTBTranslationSentenceMap.put(k, v);
                //System.out.println("After replace: " + v + "\n");
            }

            sentenceMatcher = patternThree.matcher(v);
            //After the above loop: CMU_1225: 只 需 $280 / mo ， 本人 承担 使用费 和 网络 费用 。
            //before this loop CMU_1563: 亚马逊 全新 价格 $109 ， 二手 from$85 。
            while (sentenceMatcher.find()) {
                //System.out.println("patternThree finded: " + k + ": " + v);             
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                v = v.replace(targetString, targetString.replaceAll("\\$", " \\$"));
                idToCTBTranslationSentenceMap.put(k, v);
                //System.out.println("After replace: " + v + "\n");
            }
            //after the above loop CMU_1563: 亚马逊 全新 价格 $109 ， 二手 from $85 。
            sentenceMatcher = patternFour.matcher(v);
            //After the above while loop PSU_1851: 所有 设施 我 都 会 交 ， 询问 for $450 / person / month
            while (sentenceMatcher.find()) {
                //System.out.println("patternThree finded: " + k + ": " + v);             
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                v = v.replace(targetString, targetString.replaceAll("/", " / "));
                idToCTBTranslationSentenceMap.put(k, v);
                //System.out.println("After replace: " + v + "\n");
            }
            //After the above loop CMU_534: 租约 ： 整套 公寓 一 室 一 厅 1005$/ 每 月 （ 不 包 网 电 ） ， 客厅 420$ / 每 月 ， 卧室 585$/ 每 月 。
            sentenceMatcher = patternFive.matcher(v);
            //before the loop CMU_502: 价格 : $600一 个 月 ， 包 水 煤气 暖气 。
            while (sentenceMatcher.find()) {
                //System.out.println("patternFive finded: " + k + ": " + v);             
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                v = v.replace(targetString, targetString.replaceAll("[\\p{IsHan}]+", " $0"));
                idToCTBTranslationSentenceMap.put(k, v);
                //System.out.println("After replace: " + v + "\n");
            }
            ///After the loop CMU_502 价格 : $600 一 个 月 ， 包 水 煤气 暖气 。
        });

        //check the output
//        idToCTBTranslationSentenceMap.forEach((k,v) -> {
//            String regex = "\\$";
//            Pattern pattern = Pattern.compile(regex);
//            Matcher sentenceMatcher = pattern.matcher(v);
//            if(sentenceMatcher.find()) {
//                System.out.println(k + ": " + v);
//            }
//        });
    }

    //FIXME: 
    //PSU_1439 Chinese translation ("登记卡") is recover to old segmentation ("登记 卡"). It should keep the new ctb segmentation instead of the old segmetnation
    //PSU_830 & PSU_992 not receovered segmetnation
    private static void recoverSegmentationForCSPhraseInOriginalSentAndTranslation() {
        LinkedList<String> sentenceIdList = new LinkedList<>(idToCTBOriginalSentenceMap.keySet());
        LinkedList<String> originalSentenceList = new LinkedList<>(idToCTBOriginalSentenceMap.values());
        LinkedList<String> originalCSPhraseList = new LinkedList<>(idToCSPhraseOriginalSentenceMap.values());
        LinkedList<String> translationSentenceList = new LinkedList<>(idToCTBTranslationSentenceMap.values());
        for (int i = 0; i < originalSentenceList.size(); i++) {
            //initialize sentence id
            String sentenceId = sentenceIdList.get(i);
            //initialize a boolean of if cs-phrase is updated
            boolean csPhraseUpdated = false;
            //initialize a boolean of if a non-translated cs-phrase in translation is updated
            boolean nonTranslatedCSPhraseUpdated = false;
            //initialize a string array of words in the ctb-segmented cs-sentences
            String[] wordsInOriginalSentence = originalSentenceList.get(i).split("\\s+");
            //initialize a string array of the cs-phrase(s) in the original cs-sentences
            String[] wordsInOriginalCSPhrase = originalCSPhraseList.get(i).split(",");
            //initialize a string array of the cs-phrase(s) with no whitespace in the original cs-sentences
            String[] wordsInOriginalCSPhraseWithNoWhiteSpace = originalCSPhraseList.get(i).replaceAll("\\s+", "").split(",");
            //initialize a string array of words in the ctb-segmented tranlsation of cs-sentences in which non-translated cs-phrase are not segmented
            String[] wordsInTranslationSentence = translationSentenceList.get(i).split("\\s+");
            //convert the arrays to the linked lists
            LinkedList<String> wordsInSentenceList = convertStringArrayToStringList(wordsInOriginalSentence);
            LinkedList<String> wordsInCSPhraseList = convertStringArrayToStringList(wordsInOriginalCSPhrase);
            LinkedList<String> wordsInCSPhraseWithNoWhiteSpaceList = convertStringArrayToStringList(wordsInOriginalCSPhraseWithNoWhiteSpace);
            LinkedList<String> wordsInTranslationSentenceList = convertStringArrayToStringList(wordsInTranslationSentence);

            for (int x = 0; x < wordsInSentenceList.size(); x++) {
                String word = wordsInSentenceList.get(x);
                //check if a word is a cs-phrase with no whitespace
                if (wordsInCSPhraseWithNoWhiteSpaceList.contains(word)) {
                    //check if a cs-phrase has more than one word
                    if (wordsInCSPhraseList.get(wordsInCSPhraseWithNoWhiteSpaceList.indexOf(word)).split("\\s+").length > 1) {
                        //initialize the index of the cs-phrase in wordsInCSphrase list
                        int indexOfCSPhrase = wordsInCSPhraseWithNoWhiteSpaceList.indexOf(word);
                        //initialize the cs-phrase with original segmentation
                        String csPhraseSegmentationRecovered = wordsInCSPhraseList.get(indexOfCSPhrase);
                        //update cs-phrase with recovered segmentation in the ctb-segmented cs-sentences
                        wordsInSentenceList.set(x, csPhraseSegmentationRecovered);
                        csPhraseUpdated = true;
                        //check if a tranlsation sentence contains any non-translated cs-phrase with no whitespace
                        if (wordsInTranslationSentenceList.contains(word)) {
                            //initialize the English phrase with original segmentation
                            String englishPhraseSegmentationRecovered = csPhraseSegmentationRecovered;
                            int indexOfEnglishPhrase = wordsInTranslationSentenceList.indexOf(word);
                            //update english phrase with recovered segmentation in the ctb-segmented translation of cs-sentences
                            wordsInTranslationSentenceList.set(indexOfEnglishPhrase, englishPhraseSegmentationRecovered);
                            nonTranslatedCSPhraseUpdated = true;
                            //System.out.println("Non-translated cs-phrase in translation in " + sentenceId);
                            //System.out.println("Before recover: " + word);
                            //System.out.println("After recover: " + englishPhraseSegmentationRecovered);
                        }
                    }
                }
            }
            //check if segmentation of a cs-phrase updated
            if (csPhraseUpdated) {
                //initialize a String to store the ctb-segmented cs-sentence with cs-phrase segmentation recovered.
                //  use substring to remove surround "[]" and use replaceAll to replace all ", "(comma and space) by one " "(space)
                String ctbCSSentWithCSPhraseSegmentationRecovered = wordsInSentenceList.toString().substring(1, wordsInSentenceList.toString().length() - 1).replaceAll(", ", " ");
                //System.out.println(sentenceId);
                //System.out.println("original cs-phrase not segmented: " + originalSentenceList.get(i));
                //System.out.println("original cs-phrase recovered:     " + ctbCSSentWithCSPhraseSegmentationRecovered + "\n");
                //update sentence value in idToCTBOriginalSentenceMap
                idToCTBOriginalSentenceMap.put(sentenceId, ctbCSSentWithCSPhraseSegmentationRecovered);
                //reset csPhraseUpdated to false;
                csPhraseUpdated = false;
                //check if segmentation of a non-translated cs-phrase in translation is updated
                if (nonTranslatedCSPhraseUpdated) {
                    //initialize a String to store the ctb-segmented translation of cs-sentence with English phrase segmentation recovered.
                    //  use substring to remove surround "[]" and use replaceAll to replace all ", "(comma and space) by one " "(space)
                    String ctbCSSentTranslationWithEnglishPhraseSegmentationRecovered = wordsInTranslationSentenceList.toString().substring(1, wordsInTranslationSentenceList.toString().length() - 1).replaceAll(", ", " ");
                    //System.out.println(sentenceId);
                    //System.out.println("English phrase not segmented: " + translationSentenceList.get(i));
                    //System.out.println("English phrase segmented    : " + ctbCSSentTranslationWithEnglishPhraseSegmentationRecovered + "\n");
                    //update sentence value in idToCTBTranslationSentenceMap
                    idToCTBTranslationSentenceMap.put(sentenceId, ctbCSSentTranslationWithEnglishPhraseSegmentationRecovered);
                    //reset nonTranslatedCSPhraseUpdated to false;
                    nonTranslatedCSPhraseUpdated = false;
                }
            }
        }
    }

    private static LinkedList<String> convertStringArrayToStringList(String[] strings) {
        LinkedList<String> result = new LinkedList<>();
        for (String s : strings) {
            result.add(s);
        }
        return result;
    }

    private static void saveToCSVFile() {
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        final String outputFilename = "11112019_cs_index_input";
        BufferedWriter bw = null;

        //Intialize and assign the output file path
        File outputFile = new File("data/validation/"
                + outputFilename
                + ".csv");
        System.out.println("The file will be saved in: "
                + outputFile.getPath());
        LinkedList<String> idOriginalList = new LinkedList(idToCTBOriginalSentenceMap.keySet());
        LinkedList<String> idOriginalSentenceList = new LinkedList(idToCTBOriginalSentenceMap.values());
        LinkedList<String> idTranslationSentenceList = new LinkedList(idToCTBTranslationSentenceMap.values());
        try {
            //open output stream output txt file for writing purpose.
            //create new output stream writer
            //create new buffered writer 
            bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(outputFile), "UTF-8"));

            for (int i = 0; i < idOriginalList.size(); i++) {
                bw.append(idOriginalList.get(i));
                bw.append(COMMA_DELIMITER);
                bw.append(idOriginalSentenceList.get(i));
                bw.append(COMMA_DELIMITER);
                String translationSentence = idTranslationSentenceList.get(i);
                if (translationSentence.contains("!") || translationSentence.contains("?") || translationSentence.contains(".")) {
                    translationSentence = translationSentence.replaceAll("!", "！").replaceAll("\\.", "。").replaceAll("\\?", "？");
                    //System.out.println(translationSentence);
                }
                bw.append(translationSentence);
                bw.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("The file was created successfully !!!");

            bw.flush();
            bw.close();
            System.out.println("The file has been saved.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RetrieveCodeSwitchSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RetrieveCodeSwitchSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RetrieveCodeSwitchSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void separateLongWords() {
        //PSU_830: youcanstarttherentassoonaspossible四月
        String regexOne = "\\w+[\\p{IsHan}]+";
        Pattern patternOne = Pattern.compile(regexOne);
        //CMU_1152: bayard&craig一 室 两 厅 中 的 一 厅 出租 ， 10/01/2013 起 空闲 ， 女生 优先 。
        String regexTwo = "\\w+&\\w+";
        Pattern patternTwo = Pattern.compile(regexTwo);
        //PSU_836: 生活 便捷 ： 公寓 提供 buspass ， 可坐v ， ve ， n ， nv .
        String regexThree = "[\\p{IsHan}]+\\w+";
        Pattern patternThree = Pattern.compile(regexThree);

        //before this while loop PSU_830:youcanstarttherentassoonaspossible四月 底 五月 初 即 可 入住 ， 公寓 位于 一 楼 ， 门口 就 有 免费 停车场 。
        idToCTBOriginalSentenceMap.forEach((k, v) -> {
            Matcher sentenceMatcher = patternOne.matcher(v);
            while (sentenceMatcher.find()) {
                String targetString = sentenceMatcher.group();
                //System.out.println("Before replace: " + targetString);
                v = v.replace(targetString, targetString.replaceAll("\\w+", "$0 "));
                idToCTBOriginalSentenceMap.put(k, v);
                //System.out.println("After replace: " + v);
            }
            //After the loop PSU_830: youcanstarttherentassoonaspossible 四月 底 五月 初 即 可 入住 ， 公寓 位于 一 楼 ， 门口 就 有 免费 停车场 。
            //Before the lpop CMU_1152: bayard&craig
            sentenceMatcher = patternTwo.matcher(v);
            while (sentenceMatcher.find()) {
                String targetString = sentenceMatcher.group();
                //System.out.println("Before replace: " + k + ": " + targetString);
                v = v.replace(targetString, targetString.replaceAll("&", " & "));
                idToCTBOriginalSentenceMap.put(k, v);
                //System.out.println("After replace: " + v);
            }
            //After the loop CMU_1152: bayard & craig

            sentenceMatcher = patternThree.matcher(v);
            //before the loop PSU_836: 可坐v
            while (sentenceMatcher.find()) {
                String targetString = sentenceMatcher.group();
                //System.out.println("Before replace: " + k + ": " + targetString);
                v = v.replace(targetString, targetString.replaceAll("\\w+", " $0"));
                idToCTBOriginalSentenceMap.put(k, v);
                //System.out.println("After replace: " + v);
            }
            //after the loop PSU_836 可坐 v
        });

        //before this while loop CMU_1021:租约 也 和 这 家 公司 签 l合法 租约 ， 不 是 和 我们 签 。
        idToCTBTranslationSentenceMap.forEach((k, v) -> {
            Matcher sentenceMatcher = patternOne.matcher(v);
            while (sentenceMatcher.find()) {
                String targetString = sentenceMatcher.group();
                //System.out.println("Before replace: " + k + ": " + v + "\n" + targetString);
                v = v.replace(targetString, targetString.replaceAll("\\w+", "$0 "));
                idToCTBTranslationSentenceMap.put(k, v);
                //System.out.println("After replace: " + v);
            }
            //After the loop CMU_1021: 租约 也 和 这 家 公司 签 l 合法 租约 ， 不 是 和 我们 签 。

            //Before the lpop CMU_1152: bayard&craig
            sentenceMatcher = patternTwo.matcher(v);
            while (sentenceMatcher.find()) {
                String targetString = sentenceMatcher.group();
                //System.out.println("Before replace: " + k + ": " + targetString);
                v = v.replace(targetString, targetString.replaceAll("&", " & "));
                idToCTBTranslationSentenceMap.put(k, v);
                //System.out.println("After replace: " + v);
            }
            //After the loop CMU_1152: bayard & craig

            sentenceMatcher = patternThree.matcher(v);
            //before the loop PSU_836:  可坐v
            while (sentenceMatcher.find()) {
                String targetString = sentenceMatcher.group();
                //System.out.println("Before replace: " + k + ": " + targetString);
                v = v.replace(targetString, targetString.replaceAll("\\w+", " $0"));
                idToCTBTranslationSentenceMap.put(k, v);
                //System.out.println("After replace: " + v);
            }
            //after the loop PSU_836: 可坐 v
        });
    }

}
