/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import database.IndependentProbabilityDatabase;
import infodetector.PunctuationDetector;
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
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class CountCleanCodeSwitchSentence {
    private static LinkedList<Integer> cleanCSSentlineIDList = new LinkedList<>();
    public static void main(String[] args) {
        //call read input file method
        readInputFile();
        //call write output file method to store line id of clean-code-switch sentences
        saveCleanCSLineIDFile();
    }

    private static void readInputFile() {
        //intialize a Chinese word list from Chinese dictionary
        ArrayList<String> wordList = IndependentProbabilityDatabase.getWordList("55k-chinese-word-independent-probabilities.txt");
        //intialize a string and assign input file name
        String inputFilename = "all_clean_and_pns_ver";
        //initalize a buffered reader
        BufferedReader br = null;
        //set input file path
        File inputFile = new File("data/database/input/"
            + inputFilename
            + ".csv");
        
        //Intialize a string variable to store new input line
        String line = "";
        
        try {
            //create a new buffered reader
            //create a new input stream reader
            //open a new input stream csv file for read purpose
            br = new BufferedReader(
                    new InputStreamReader (
                            new FileInputStream(inputFile), "UTF-8"));
            //read to skip the header
            br.readLine();
            int lineCounter = 0;
            int cleanCSSentCounter = 0;
            int otherCSSentCounter = 0;
            int oovWordCounter = 0;
            boolean foundOOVInCleanCSSent = false;
            int oovCleanCSSentCounter = 0;
            
            int oovCSWordCounter = 0;
            int oovCSWordCleanCSSentCounter = 0;
            boolean foundOOVCSWordInCleanCSSent = false;
            //read all lines until reach the end of file
            while((line = br.readLine()) != null) {
                //Check if a line contains 11 columns split by ","
                String[] columns = line.split(",");
                if(columns.length != 11) {
                    System.out.println("This line does not have 11 columns, but " + columns.length);
                    System.out.println(line);
                }
                lineCounter++;
                if(columns[9].equalsIgnoreCase("clean_code_switch")) {
                    cleanCSSentlineIDList.add(lineCounter);
                    cleanCSSentCounter++;
                    String[] words = columns[2].split("\\s+");
                    String[] csStringIndicesTranslation = columns[5].substring(1, columns[5].length()-1).split("_");
                    LinkedList<Integer> csIndicesTranslation = new LinkedList<>();
                    for(int i = 0; i < csStringIndicesTranslation.length; i++) {
                        csIndicesTranslation.add(Integer.parseInt(csStringIndicesTranslation[i]));
                    }
                    for(int i = 0; i < words.length; i++) {
                        if(!wordList.contains(words[i]) && !PunctuationDetector.isPunctuation(words[i])) {
                            oovWordCounter++;
                            foundOOVInCleanCSSent = true;
                            if(csIndicesTranslation.contains(i)) {
                                System.out.println("oov cs word: " + words[i]);
                                oovCSWordCounter++;
                                foundOOVCSWordInCleanCSSent = true;
                            }
                        }
                    }
                    if(foundOOVInCleanCSSent) {
                        oovCleanCSSentCounter++;
                        foundOOVInCleanCSSent = false;
                    }
                    if(foundOOVCSWordInCleanCSSent) {
                        oovCSWordCleanCSSentCounter++;
                        foundOOVCSWordInCleanCSSent = false;
                    }
                } else {
                    otherCSSentCounter++;
                }               
            }
            System.out.println("There are " + cleanCSSentCounter + " clean code switch sentences");
            System.out.println("There are " + otherCSSentCounter + " other code switch sentences");
            System.out.println("There are " + oovWordCounter + " OOV words in " + oovCleanCSSentCounter + " clean code switch sentences");
            System.out.println("There are " + oovCSWordCounter + " OOV cs words in " + oovCSWordCleanCSSentCounter + " clean code switch sentences");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(CountCleanCodeSwitchSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CountCleanCodeSwitchSentence.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(CountCleanCodeSwitchSentence.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void saveCleanCSLineIDFile() {
        final String NEW_LINE_SEPARATOR = "\n";
        final String outputFilename = "line_id_all_clean_and_pns_ver";
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
                    new OutputStreamWriter (
                            new FileOutputStream(outputFile), "UTF-8"));
            
            for(int id : cleanCSSentlineIDList) {
                bw.append(String.valueOf(id));
                bw.append(NEW_LINE_SEPARATOR);
            }
            
            System.out.println("The file was created successfully !!!");

            bw.flush();
            bw.close();
            System.out.println("The file has been saved.");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(CountCleanCodeSwitchSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CountCleanCodeSwitchSentence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
