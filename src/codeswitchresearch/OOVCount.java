/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import database.IndependentProbabilityDatabase;
import infodetector.PunctuationDetector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class OOVCount {

    private static ArrayList<String> wordList = IndependentProbabilityDatabase.getWordList("55k-chinese-word-independent-probabilities.txt");
    private static PunctuationDetector detector = new PunctuationDetector();
    private static LinkedList<Integer> cleanCSLineIDList = new LinkedList<>();

    public static void main(String[] args) {
        readCleanCSSentIDList();
        readInputFile();
    }

    private static void readInputFile() {
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
        //Initialize a line counter and set 1 to the variable
        int lineCounter = 1;
        int oovWordCounter = 0;
        int oovCleanCSSentCounter = 0;
        int cleanCSSentCounter = 0;
        boolean oovFoundInLine = false;
        try {
            //create new buffered reader
            //create new input stream reader
            //open input stream input txt file for reading purpose
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
                if((lineCounter % 2) == 0) {
                    //System.out.println(line);
                    if(!cleanCSLineIDList.contains(lineCounter/2)) {
                        lineCounter++;
                        continue;
                    }
                    cleanCSSentCounter++;
                    String[] words = line.split("\\s+");
                    for(String w : words) {
                        //System.out.println(w);
                        if(!wordList.contains(w) && !detector.isPunctuation(w)) {
                            //System.out.println("OOV word: " + w);
                            oovWordCounter++;
                            oovFoundInLine = true;
                        }
                        if(oovFoundInLine) {
                            oovCleanCSSentCounter++;
                            oovFoundInLine = false;
                        }
                    }
                }
                lineCounter++;
            }
            System.out.println("There are " + oovWordCounter + " oov words in " + oovCleanCSSentCounter + " clean cs-sentences out of " + cleanCSSentCounter + " clean cs-sentences"+ " in " + lineCounter/2 + " sentences.");

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(OOVCount.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OOVCount.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(OOVCount.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Error occured while closing the BufferedReader");
            }
        }
    }

    private static void readCleanCSSentIDList() {
        //Initialize input filename
        String inputFilename = "line_id_all_clean_and_pns_ver";
        //Initialize a BufferedReader 
        BufferedReader br = null;
        //Set file path of input
        File inputFile = new File("data/validation/"
                + inputFilename
                + ".txt");
        
        try {
            //create new buffered reader
            //create new input stream reader
            //open input stream input txt file for reading purpose
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(inputFile), "UTF-8"));
            String line = "";
            while((line = br.readLine()) != null) {
                cleanCSLineIDList.add(Integer.parseInt(line));
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(OOVCount.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OOVCount.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(OOVCount.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Error occured while closing the BufferedReader");
            }
        }
    }
}
