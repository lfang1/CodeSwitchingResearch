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
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class CompareOriginalSentence {

    private static LinkedHashMap<String, String> databaseInput = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> addIdLineInput = new LinkedHashMap<>();

    public static void main(String[] args) {
        //initialize databaseInput map
        intializeDatabaseMap();
        //initailize addIdLineInput map
        intializeAddIdMap();
        //compare two maps
        compareOriginalSentence();
    }

    //Read input to initialize databaseInput map
    private static void intializeDatabaseMap() {
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
                databaseInput.put(columns[10] + "_" + columns[0], columns[1]);
            }

            //Show the size of the list
            System.out.println("The size of databaseInput: " + databaseInput.size());
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(CompareOriginalSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CompareOriginalSentence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void intializeAddIdMap() {
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
            //Read all lines until reaching the end of file
            while ((line = br.readLine()) != null) {
                //Check if there are only 1 column in one line split by ","
                String[] columns = line.split(",");
                if (columns.length != 11) {
                    System.out.println("The line doesn't have only 11 column but " + columns.length + " column(s)");
                }
                //System.out.println(columns[10] + "_" + columns[0] + ": " + columns[1]);
                addIdLineInput.put(columns[10] + "_" + columns[0], columns[1]);
            }

            //Show the size of the list
            System.out.println("The size of addIdLineInput: " + addIdLineInput.size());
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(CompareOriginalSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CompareOriginalSentence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void compareOriginalSentence() {
        if(databaseInput.size() != addIdLineInput.size()) {
            System.out.println("two maps do not have the same size.");
        }
        LinkedList<String> databaseSets = new LinkedList(databaseInput.keySet());
        LinkedList<String> addIdLineSets = new LinkedList(addIdLineInput.keySet());
        for(int i = 0; i < databaseSets.size(); i++) {
            if(!databaseSets.get(i).equals(addIdLineSets.get(i))){
                System.out.println(databaseSets.get(i));
                System.out.println(addIdLineSets.get(i));
                System.out.println("These two keys are not equal!\n");
            }
        }
        
//        LinkedList<String> databaseValues = new LinkedList(databaseInput.values());
//        LinkedList<String> addIdLineValues = new LinkedList(addIdLineInput.values());
//        for(int i = 0; i < databaseValues.size(); i++) {
//            if(!databaseValues.get(i).equals(addIdLineValues.get(i))){
//                System.out.println(databaseValues.get(i));
//                System.out.println(addIdLineValues.get(i));
//                System.out.println("These two values are not equal!\n");
//            }
//        }
        
    }
}
