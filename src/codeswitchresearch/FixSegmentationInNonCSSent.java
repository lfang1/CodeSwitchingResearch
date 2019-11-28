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
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Le
 */
public class FixSegmentationInNonCSSent {

    private static LinkedList<String> sentenceIdList = new LinkedList<>();
    private static LinkedList<String> sentenceList = new LinkedList<>();

    public static void main(String[] args) {
        readCSVFile();
        fixSegmentationForDollarSignAndDigital();
        saveCSVFile();
    }

    private static void readCSVFile() {
        //initialize input filename
        String inputFilename = "10312019_noncs_ctb_segmentation_output";
        //initialize a BufferedReader 
        BufferedReader br = null;
        //set file path of input
        File inputFile = new File("data/validation/"
                + inputFilename
                + ".txt");

        //intialize a string variable to store new input line
        String line = "";

        try {
            //open input stream input csv file for reading purpose.
            //create new input stream reader
            //create new buffered reader
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(inputFile), "UTF-8"));
            //initialize a boolean
            boolean isSentence = false;
            //intiailize a sentenceId
            String sentenceId = "";
            String sentence = "";
            //Read all lines until reaching the end of file
            while ((line = br.readLine()) != null) {
                //Check if the current line is sentence line or id line
                if (isSentence) {
                    sentence = line;
                    sentenceList.add(sentence);
                    isSentence = false;
                } else {
                    sentenceId = line;
                    sentenceIdList.add(sentenceId);
                    isSentence = true;
                }
            }
            System.out.println("The size of id list: " + sentenceIdList.size());
            System.out.println("The size of sentence list: " + sentenceList.size());

            //close buffered reader
            br.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FixSegmentationInNonCSSent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FixSegmentationInNonCSSent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FixSegmentationInNonCSSent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void fixSegmentationForDollarSignAndDigital() {
       
        //PSU_4672: $ 5500
        String regexOne = "\\$\\s\\d+";
        Pattern patternOne = Pattern.compile(regexOne);
        //PIT_3914: $795/month
        String regexTwo = "\\$\\d+/\\w+";
        Pattern patternTwo = Pattern.compile(regexTwo);
        //PSU_3510: 佳能 彩色 照片 打印机 10$27 .
        String regexThree = "\\w+\\$\\d+";
        Pattern patternThree = Pattern.compile(regexThree);
        //CMU_3870: 房屋 情况 ： 房租 825$/month 。
        String regexFour = "\\d+\\$/\\w+";
        Pattern patternFour = Pattern.compile(regexFour);
        //PSU_1674: 另外 有 椅凳$2一 把 。
        String regexFive = "\\$\\d+[\\p{IsHan}]+";
        Pattern patternFive = Pattern.compile(regexFive);      
        //PSU_6663:老板 台$403 .
        String regexSix = "[\\p{IsHan}]+\\$\\d+";
        Pattern patternSix = Pattern.compile(regexSix);
        
        for (int i = 0; i < sentenceList.size(); i++) {
            String id = sentenceIdList.get(i);
            String sentence = sentenceList.get(i);

            Matcher sentenceMatcher = patternOne.matcher(sentence);
            //Before replace: PSU_4672: $ 5500
            while (sentenceMatcher.find()) {
                String targetString = sentenceMatcher.group();
                //System.out.println("Before replace: " + id + ": "+ targetString);
                sentence = sentence.replace(targetString, targetString.replaceAll("\\s", ""));
                sentenceList.set(i, sentence);
                //System.out.println("After replace: " + sentenceList.get(i));
            }
            //After replace: 要价 $5500 .
            sentenceMatcher = patternTwo.matcher(sentence);
            
            //Before replace: PIT_3914: $795/month
            while (sentenceMatcher.find()) {        
                String targetString = sentenceMatcher.group();
                //System.out.println("Before replace: " + id + ": "+ targetString);
                sentence = sentence.replace(targetString, targetString.replaceAll("\\s", "")).replaceAll("/", " / ");
                sentenceList.set(i, sentence);
                //System.out.println("After replace: " + sentenceList.get(i));
            }
            sentenceMatcher = patternThree.matcher(sentence);
            //before replace: PSU_3510: 佳能 彩色 照片 打印机 10$27 .
            while (sentenceMatcher.find()) {
                //System.out.println("patternThree found: " + id + ": " + sentence);             
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                sentence = sentence.replace(targetString, targetString.replaceAll("\\$", " \\$"));
                sentenceList.set(i, sentence);
                //System.out.println("After replace: " + sentence + "\n");
            }
            //After replace: 佳能 彩色 照片 打印机 10 $27 .
            sentenceMatcher = patternFour.matcher(sentence);
            //Before replace: CMU_3870: 房屋 情况 ： 房租 825$/month 。
            while (sentenceMatcher.find()) {
                //System.out.println("patternFour found: " + id + ": " + sentence);             
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                sentence = sentence.replace(targetString, targetString.replaceAll("/", " / "));
                sentenceList.set(i, sentence);
                //System.out.println("After replace: " + sentence + "\n");
            }
            //After replace: 房屋 情况 ： 房租 825$ / month 。
            sentenceMatcher = patternFive.matcher(sentence);
            //before replace: PSU_1674: 另外 有 椅凳$2一 把 。
            while (sentenceMatcher.find()) {
                //System.out.println("patternFive found: " + id + ": " + sentence);             
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                sentence = sentence.replace(targetString, targetString.replaceAll("[\\p{IsHan}]+", " $0"));
                sentenceList.set(i, sentence);
                //System.out.println("After replace: " + sentence + "\n");
            }
            //After replace: 另外 有 椅凳$2 一 把 。                   
            sentenceMatcher = patternSix.matcher(sentence);           
            //Before replace: PSU_6663:老板 台$403 .
            while (sentenceMatcher.find()) {
                //System.out.println("patternSix found " + id + ":" + sentence);
                String targetString = sentenceMatcher.group();
                //System.out.println(targetString);
                sentence = sentence.replace(targetString, targetString.replace("$", " $"));
                sentenceList.set(i, sentence);
                //System.out.println("After replace: " + sentence + "\n");
            }
            //After replace: 老板 台 $403 .
        }
    }

    private static void saveCSVFile() {
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        
        final String outputFilename = "10312019_noncs_ctb_segmentation_output_fixed";
        BufferedWriter bw = null;
        //Intialize and assign the output file path
        File outputFile = new File("data/validation/"
                + outputFilename
                + ".txt");
        System.out.println("The file will be saved in: "
                + outputFile.getPath());
        
        try {
            //open output stream output txt file for writing purpose.
            //create new output stream writer
            //create new buffered writer 
            bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(outputFile), "UTF-8"));

            for(int i = 0; i < sentenceList.size(); i++) {
                bw.append(sentenceIdList.get(i));
                bw.append(NEW_LINE_SEPARATOR);
                bw.append(sentenceList.get(i));
                bw.append(NEW_LINE_SEPARATOR);
            }
            System.out.println("The file was created successfully !!!");

            bw.flush();
            bw.close();
            System.out.println("The file has been saved.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FixSegmentationInNonCSSent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FixSegmentationInNonCSSent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FixSegmentationInNonCSSent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
