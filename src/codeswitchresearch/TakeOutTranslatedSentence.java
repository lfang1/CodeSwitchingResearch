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
public class TakeOutTranslatedSentence {
    private static LinkedList<String> lineWithoutSpaceList = new LinkedList<>();
    
    public static void main(String[] args) {
        readInputFile();
        saveToFile();
    }
    
    public static void readInputFile(){
        String inputFilename = "03022019_all_clean_and_pns_ver_id_line_added";
        BufferedReader br = null;
        //Read the input file
        File fileDir = new File("data/prepare_file_for_segmentation/input/"
                + inputFilename
                + ".txt");
        
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));
            
            String line = "";
            
            while((line = br.readLine()) != null) {
                //split a line into column(s) by ","
                String[] sentenceDetails = line.split(",");
                //check if a line has only one column
                if(sentenceDetails.length != 1) {
                    System.err.println("The line doesn't have only 1 column, but "
                            + sentenceDetails.length);
                    System.err.println("Line: " + line);
                    System.exit(0);
                }
                //Remove all whitespace(s) in each line
                String lineWithoutSpace = line.replaceAll("\\s+", "");
                //add the new line into a list
                lineWithoutSpaceList.add(lineWithoutSpace);
            }
            //There are 3023 sentence ids, 3023 cs-sentences
            System.out.println(lineWithoutSpaceList.size() + " sentences have been added");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TakeOutTranslatedSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TakeOutTranslatedSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TakeOutTranslatedSentence.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(TakeOutTranslatedSentence.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Error occured while closing the BufferedReader");
            }
        }
    }
    
    public static void saveToFile() {
        final String NEW_LINE_SEPARATOR = "\n";       
        final String outputFilename = "10262019_cs_segmentation_input";
        BufferedWriter bw = null;
        
        File outputFileDir = new File("data/prepare_file_for_segmentation/output/"
                    + outputFilename
                    + ".txt");
            System.out.println("The file will be saved in: "
                    + outputFileDir.getPath());
            
        try {
            bw = new BufferedWriter(
                    new OutputStreamWriter (
                            new FileOutputStream(outputFileDir), "UTF-8"));
            
            for(String line : lineWithoutSpaceList) {
                bw.append(line);
                bw.append(NEW_LINE_SEPARATOR);
            }
            
            System.out.println("The file was created successfully !!!");

            bw.flush();
            bw.close();
            System.out.println("The file has been saved.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TakeOutTranslatedSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TakeOutTranslatedSentence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TakeOutTranslatedSentence.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Problem writing to the "
                    + "data/prepare_file_for_segmentation/output/"
                    + outputFilename
                    + ".txt");
        }
            
        
    }
}
