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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class MergeNewCTBSegmentedTranslationWithOriginalCSSentence {

    //two linked hash maps to store sentence id as key, sentnece as value
    private static LinkedHashMap<String, String> idOriginalMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> idTranslationMap = new LinkedHashMap<>();

    public static void main(String[] args) {
        //Initialize idOriginalMap
        initializeOriginalMap();
        //Initialize idTranslationMap
        initializeTranslationMap();
        //Compare sentenceId
        compareSentenceId();
        //write a csv file
        saveToCSVFile();
    }

    private static void initializeOriginalMap() {
        //Initialize input filename
        String inputFilename = "all_clean_and_pns_ver";
        //Initialize a BufferedReader 
        BufferedReader br = null;
        //Set file path of input
        File inputFile = new File("data/database/input/"
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
            //Read to skip header
            br.readLine();
            //Read all lines until reaching the end of file
            while ((line = br.readLine()) != null) {
                //Check if there are only 1 column in one line split by ","
                String[] columns = line.split(",");
                if (columns.length != 11) {
                    System.out.println("The line doesn't have only 11 column but " + columns.length + " column(s)");
                }
                //System.out.println(columns[10] + "_" + columns[0] + ": " + columns[1]);
                idOriginalMap.put(columns[10] + "_" + columns[0], columns[1]);
            }
            br.close();

            //Show the size of the list
            System.out.println("The size of idOriginalMap: " + idOriginalMap.size());
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(MergeNewCTBSegmentedTranslationWithOriginalCSSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MergeNewCTBSegmentedTranslationWithOriginalCSSentence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void initializeTranslationMap() {
        //Initialize input filename
        String inputFilename = "10262019_cs_ctb_segmentation_output";
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
            String segmentedSent = "";
            while ((line = br.readLine()) != null) {
                //Check if there are only 1 column in one line split by ","
                String[] columns = line.split(",");
                if (columns.length != 1) {
                    System.out.println("The line doesn't have only 1 column but " + columns.length + " column(s)");
                }
                if (lineCounter % 2 != 0) {
                    sentenceId = line;
                } else {
                    segmentedSent = line;
                    //System.out.println(sentenceId + ": " + segmentedSent);
                    idTranslationMap.put(sentenceId, segmentedSent);
                }
                lineCounter++;
            }
            br.close();
            
            //Show the size of the list
            System.out.println("The size of idTranslationMap: " + idTranslationMap.size());
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(MergeNewCTBSegmentedTranslationWithOriginalCSSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MergeNewCTBSegmentedTranslationWithOriginalCSSentence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void compareSentenceId() {
        if (idOriginalMap.size() != idTranslationMap.size()) {
            System.out.println("two maps do not have the same size.");
        }
        LinkedList<String> idOriginalMapSets = new LinkedList(idOriginalMap.keySet());
        LinkedList<String> idTranslationMapSets = new LinkedList(idTranslationMap.keySet());
        for (int i = 0; i < idOriginalMapSets.size(); i++) {
            if (!idOriginalMapSets.get(i).equals(idTranslationMapSets.get(i))) {
                System.out.println(idOriginalMapSets.get(i));
                System.out.println(idTranslationMapSets.get(i));
                System.out.println("These two keys are not equal!\n");
            }
        }
    }

    private static void saveToCSVFile() {
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        final String outputFilename = "11032019_cs_index_input";
        BufferedWriter bw = null;

        //Intialize and assign the output file path
        File outputFile = new File("data/validation/"
                + outputFilename
                + ".csv");
        System.out.println("The file will be saved in: "
                + outputFile.getPath());
        LinkedList<String> idOriginalMapSets = new LinkedList(idOriginalMap.keySet());
        LinkedList<String> idOriginalMapValues = new LinkedList(idOriginalMap.values());
        LinkedList<String> idTranslationMapValues = new LinkedList(idTranslationMap.values());
        try {
            //open output stream output txt file for writing purpose.
            //create new output stream writer
            //create new buffered writer 
            bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(outputFile), "UTF-8"));

            for (int i = 0; i < idOriginalMap.size(); i++) {
                bw.append(idOriginalMapSets.get(i));
                bw.append(COMMA_DELIMITER);
                bw.append(idOriginalMapValues.get(i));
                bw.append(COMMA_DELIMITER);
                bw.append(idTranslationMapValues.get(i));
                bw.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("The file was created successfully !!!");

            bw.flush();
            bw.close();
            System.out.println("The file has been saved.");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(MergeNewCTBSegmentedTranslationWithOriginalCSSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MergeNewCTBSegmentedTranslationWithOriginalCSSentence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
