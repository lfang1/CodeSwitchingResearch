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
public class TakeOutNonCSSentenceForSegmentation {
    //Initialize a list to store Chinese sentences that were never code-switched (noncs-sents) without whitespace 
    private static LinkedList<String> lineWithNoSpaceList = new LinkedList<>();

    public static void main(String[] args) {
        readInputFile();
        saveToCSVFile();
    }

    private static void readInputFile() {
        //Initialize input filename
        String inputFilename = "01292019_all_clean_non_cs";
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
                //Check if there are only 5 column in one line split by ","
                String[] columns = line.split(",");
                if (columns.length != 5) {
                    System.out.println("The line doesn't have only 5 column but " + columns.length + " column(s)");
                }
                //intialize sentence id
                String sentenceId = columns[4] + "_" + columns[0];
                //System.out.println(sentenceId);
                //intialize original sentence
                String originalSentence = columns[1].replaceAll("\\s+", "");
                //System.out.println(originalSentence);
                String newLine = sentenceId + "," + originalSentence;
                //System.out.println(newLine);
                lineWithNoSpaceList.add(newLine);
            }
            br.close();
            //Show the size of the list
            System.out.println("The size of lineWithNoSpaceList: " + lineWithNoSpaceList.size());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TakeOutNonCSSentenceForSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TakeOutNonCSSentenceForSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TakeOutNonCSSentenceForSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void saveToCSVFile() {
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        final String outputFilename = "10312019_noncs_segmentation_input";
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
                    new OutputStreamWriter(
                            new FileOutputStream(outputFile), "UTF-8"));

            for (String line : lineWithNoSpaceList) {
                String[] columns = line.split(",");
                if (columns.length != 2) {
                    System.out.println("The current line does not have only 2 columns but " + columns.length);
                }
                bw.append(columns[0]);
                bw.append(NEW_LINE_SEPARATOR);
                bw.append(columns[1]);
                bw.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("The file was created successfully !!!");

            bw.flush();
            bw.close();
            System.out.println("The file has been saved.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TakeOutNonCSSentenceForSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TakeOutNonCSSentenceForSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TakeOutNonCSSentenceForSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
