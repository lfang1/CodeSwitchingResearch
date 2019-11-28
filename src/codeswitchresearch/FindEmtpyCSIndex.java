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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class FindEmtpyCSIndex {

    public static void main(String[] args) {
        readCSVFile();
    }

    private static void readCSVFile() {
        //initialize input file name
        String inputFilename = "11112019_all_clean_and_pns_ver";
        //initialize buffered reader
        BufferedReader br = null;
        //set the file path of input file
        File inputFile = new File("data/validation/"
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
            //read to skip header
            br.readLine();

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

                //initialize a string to store indices of cs words
                String indicesOfCS = columns[4];
                //substring to remove the surround "[]"
                indicesOfCS = indicesOfCS.substring(1, indicesOfCS.length() - 1);
                //split the string by "_" to get a string array of cs indices
                String[] csIndices = indicesOfCS.split("_");
                
//                //initialize a string to store cs type
//                String typeOfCS = columns[8];
//                //split the string by "_" to get a string array of cs indices
//                String[] csType = typeOfCS.split("_");
//
//                if (csType.length < csIndices.length) {
//                    //System.out.println(sentenceId + "has less cs type than cs indices");
//                    lessCsTypeCounter++;
//                } else if (csType.length > csIndices.length) {
//                    System.out.println(sentenceId + " has more cs type than cs indices");
//                    moreCsTypeCounter++;
//                }
                if(indicesOfCS.equals("[]")) {
                    System.out.println(sentenceId);
                    System.out.println(line + "\n");
                }
            }
            //close buffered reader
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FindEmtpyCSIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FindEmtpyCSIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FindEmtpyCSIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
