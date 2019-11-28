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
public class GetInputFile {

    private static LinkedList<String> sentenceIdList = new LinkedList<>();
    private static LinkedList<String> originalSentList = new LinkedList<>();
    private static LinkedList<String> translationSentList = new LinkedList<>();

    public static void main(String[] args) {
        readCSVFile();
        saveOriginalSentToCSVFile();
        saveTranslationSentToCSVFile();
    }

    private static void readCSVFile() {
        //initialize input file name
        String inputFilename = "11132019_all_clean_and_pns_ver_new";
        //initialize buffered reader
        BufferedReader br = null;
        //set the file path of input file
        File inputFile = new File("data/get_input_file/input/"
                + inputFilename
                + ".csv");
        //initialize a string to store a line of input
        String line = "";

        try {
            //open new buffered reader
            //create new input stream reader
            //open input stream for reading purpose
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(inputFile), "UTF-8"));

            //read each line in the file until the end of file
            while ((line = br.readLine()) != null) {
                //initialize a string array that stores each items in the line of the csv file
                String[] columns = line.split(",");
                //check if there is 11 columns in the line
                if (columns.length != 11) {
                    System.out.println("The amount of columns is not 11, but " + columns.length);
                }
                //initialize a string to store sentence id
                String sentenceId = columns[10] + "_" + columns[0];
                //initialize a string to store original sentence
                String originalSent = columns[1];
                //initialize a string to store translation sentence
                String translationSent = columns[2];
                //System.out.println(sentenceId + "\n" + originalSent + "\n" + translationSent);
                /*if(sentenceId.isEmpty() || originalSent.isEmpty() || translationSent.isEmpty()) {
                    System.out.println(sentenceId + " has emtpy items");
                }*/
                sentenceIdList.add(sentenceId);
                originalSentList.add(originalSent);
                translationSentList.add(translationSent);
                            
            }
            System.out.println("Size of sentenceIdList: " + sentenceIdList.size());
            System.out.println("Size of originalSentList: " + originalSentList.size());
            System.out.println("Size of translationSentList: " + translationSentList.size());
           
            //close buffered reader
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetInputFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GetInputFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetInputFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void saveOriginalSentToCSVFile() {
        //Delimiters used in the CSV file
        final String NEW_LINE_SEPARATOR = "\n";
        final String outputFilename = "11142019_original_sent";
        BufferedWriter bw = null;
        //Intialize and assign the output file path
        File outputFile = new File("data/get_input_file/output/"
                + outputFilename
                + ".csv");
        System.out.println("The file will be saved in: "
                + outputFile.getPath());
        try {
            //open output stream output txt file for writing purpose.
            //create new output stream writer
            //create new buffered writer 
            bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(outputFile), "UTF-8"));

            for(int i = 0; i < originalSentList.size(); i++) {
                bw.append(sentenceIdList.get(i));
                bw.append(NEW_LINE_SEPARATOR);
                bw.append(originalSentList.get(i));
                bw.append(NEW_LINE_SEPARATOR);
            }
            System.out.println("The file was created successfully !!!");

            bw.flush();
            bw.close();
            System.out.println("The file has been saved.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetInputFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GetInputFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetInputFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void saveTranslationSentToCSVFile() {
        //Delimiters used in the CSV file
        final String NEW_LINE_SEPARATOR = "\n";
        final String outputFilename = "11142019_translation_sent";
        BufferedWriter bw = null;
        //Intialize and assign the output file path
        File outputFile = new File("data/get_input_file/output/"
                + outputFilename
                + ".csv");
        System.out.println("The file will be saved in: "
                + outputFile.getPath());
        try {
            //open output stream output txt file for writing purpose.
            //create new output stream writer
            //create new buffered writer 
            bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(outputFile), "UTF-8"));

            for(int i = 0; i < translationSentList.size(); i++) {
                bw.append(sentenceIdList.get(i));
                bw.append(NEW_LINE_SEPARATOR);
                bw.append(translationSentList.get(i));
                bw.append(NEW_LINE_SEPARATOR);
            }
            System.out.println("The file was created successfully !!!");

            bw.flush();
            bw.close();
            System.out.println("The file has been saved.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetInputFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GetInputFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetInputFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
