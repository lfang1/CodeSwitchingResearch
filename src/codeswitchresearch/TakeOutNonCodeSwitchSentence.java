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

/**
 *
 * @author Le
 */
public class TakeOutNonCodeSwitchSentence {

    //Initialize a list to store Chinese sentences that were never code-switched (noncs-sents) without whitespace 
    private static LinkedList<String> lineWithNoSpaceList = new LinkedList<>();

    public static void main(String[] args) {
        readInputFile();
        saveToTxtFile();
    }

    //Read clean noncs-sentences from 01292019_all_clean_non_cs_id_line_added.txt
    private static void readInputFile() {
        //Initialize input filename
        String inputFilename = "01292019_all_clean_non_cs_id_line_added";
        //Initialize a BufferedReader 
        BufferedReader br = null;
        //Set file path of input
        File inputFile = new File("data/prepare_file_for_segmentation/input/"
                + inputFilename
                + ".txt");

        //Intialize a string variable to store new input line
        String line = "";
        try {
            //open input stream input txt file for reading purpose.
            //create new input stream reader
            //create new buffered reader
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(inputFile), "UTF-8"));
            //Read all lines until reaching the end of file
            while ((line = br.readLine()) != null) {
                //Check if there are only 1 column in one line split by ","
                String[] lineColumns = line.split(",");
                if (lineColumns.length != 1) {
                    System.out.println("The line doesn't have only 1 column but " + lineColumns.length + " column(s)");
                }
                //Remove the whitespace(s) in the current line
                String lineWithNoSpace = line.replaceAll("\\s+", "");

                //Debug: print all line with no space
                //System.out.println(lineWithNoSpace);
                //add the line with no space to the list
                lineWithNoSpaceList.add(lineWithNoSpace);
            }
            
            br.close();

            //Show the size of the list
            System.out.println("The size of lineWithNoSpaceList: " + lineWithNoSpaceList.size());
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(TakeOutNonCodeSwitchSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TakeOutNonCodeSwitchSentence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void saveToTxtFile() {
        final String NEW_LINE_SEPARATOR = "\n";
        final String outputFilename = "10282019_noncs_sentences";
        BufferedWriter bw = null;

        //Intialize and assign the output file path
        File outputFile = new File("data/prepare_file_for_segmentation/output/"
                + outputFilename
                + ".txt");
        System.out.println("The file will be saved in: "
                + outputFile.getPath());
        try {
            //open output stream output txt file for writing purpose.
            //create new output stream writer
            //create new buffered writer 
            bw = new BufferedWriter(
                    new OutputStreamWriter (
                            new FileOutputStream(outputFile), "UTF-8"));
            
            for(String line : lineWithNoSpaceList) {
                bw.append(line);
                bw.append(NEW_LINE_SEPARATOR);
            }
            
            System.out.println("The file was created successfully !!!");

            bw.flush();
            bw.close();
            System.out.println("The file has been saved.");
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(TakeOutNonCodeSwitchSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TakeOutNonCodeSwitchSentence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
