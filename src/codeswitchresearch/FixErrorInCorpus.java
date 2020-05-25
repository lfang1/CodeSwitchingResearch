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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Le
 */
public class FixErrorInCorpus {

    private static PunctuationList punctList = new PunctuationList();
    private static LinkedList<String> lineList = new LinkedList<>();

    public static void main(String[] args) {
        readCSVFile();
        saveToCSVFile();
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
                String translationSent = columns[2];
                translationSent = convertEnglishPunctToChinesePunct(translationSent);
                columns[2] = translationSent;
                String newLine = String.join(",", columns);
                lineList.add(newLine);
            }
            //close buffered reader
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FixErrorInCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FixErrorInCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FixErrorInCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("The size of lineList: " + lineList.size());
    }

    public static String convertEnglishPunctToChinesePunct(String translationSent) {
        String newTranslationSent = translationSent;

        //boolean isConverted = false;
        ArrayList<String> englishPunctList = new ArrayList<>();
        for (String w : translationSent.split(" ")) {
            if (punctList.isInConversionList(w)) {
                newTranslationSent = punctList.convertToChinesePunctuation(w, newTranslationSent);
                //isConverted = true;
                englishPunctList.add(w);
            }
        }
//        if (isConverted) {
//            System.out.println("English punctuations: " + englishPunctList.toString());
//            System.out.println("Before:" + translationSent);
//            System.out.println("After: " + newTranslationSent);
//        }
        return newTranslationSent;
    }

    private static void saveToCSVFile() {
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";

        //CSV file header
        final String FILE_HEADER = "sentence ID,"
                + "original,"
                + "translation,"
                + "untranslated,"
                + "indicesOfCS,"
                + "IndicesOfCSInTranslation,"
                + "punctOriginal,"
                + "punctTranslation,"
                + "cs type,"
                + "sentence type,"
                + "source";

        final String outputFilename = "11132019_all_clean_and_pns_ver";
        BufferedWriter bw = null;
        //Intialize and assign the output file path
        File outputFile = new File("data/validation/"
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

            bw.append(FILE_HEADER);
            bw.append(NEW_LINE_SEPARATOR);
            for(String line : lineList) {
                bw.append(line);
                bw.append(NEW_LINE_SEPARATOR);
            }
            System.out.println("The file was created successfully !!!");

            bw.flush();
            bw.close();
            System.out.println("The file has been saved.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FixErrorInCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FixErrorInCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FixErrorInCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
