/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package addvariable;

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
public class AddCSTypeAndSentenceType {

    private static LinkedHashMap<String, String> idToLineMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> idToTypeMap = new LinkedHashMap<>();

    public static void main(String[] args) {
        initializeIdToLineMap();
        initializeIdToTypeMap();
        saveToCSVFile();
    }

    private static void initializeIdToLineMap() {
        //Initialize input filename
        String inputFilename = "11112019_cs_index_added";
        //Initialize a BufferedReader 
        BufferedReader br = null;
        //Set file path of input
        File inputFile = new File("data/translation-with-indices/"
                + inputFilename
                + ".csv");

        //Intialize a string variable to store new input line
        String line = "";

        try {
            //create new buffered reader
            //create new input stream reader
            //open input stream input csv file for reading purpose.
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(inputFile), "UTF-8"));
            //read to skip header
            br.readLine();

            //Read all lines until reaching the end of file
            while ((line = br.readLine()) != null) {
                //Check if there are only 8 column in one line split by ","
                String[] columns = line.split(",");
                if (columns.length != 8) {
                    System.out.println("The line doesn't have only 8 column but " + columns.length + " column(s)");
                }
                String[] newColumns = new String[9];
                String[] idAndSource = columns[0].split("_");
                if (idAndSource.length != 2) {
                    System.out.println("The idAndSource doesn't have only 2 items but " + idAndSource.length + " items");
                }
                //assign id 
                newColumns[0] = idAndSource[1];
                //assign source
                newColumns[8] = idAndSource[0];
                for (int i = 1; i < 8; i++) {
                    newColumns[i] = columns[i];
                }
                String newLine = String.join(",", newColumns);
                idToLineMap.put(columns[0], newLine);
            }
            System.out.println("The size of idToLineMap: " + idToLineMap.size());
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddCSTypeAndSentenceType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AddCSTypeAndSentenceType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddCSTypeAndSentenceType.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void initializeIdToTypeMap() {
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
            //read to skip header
            br.readLine();

            //Read all lines until reaching the end of file
            while ((line = br.readLine()) != null) {
                //Check if there are only 11 column in one line split by ","
                String[] columns = line.split(",");
                if (columns.length != 11) {
                    System.out.println("The line doesn't have only 11 column but " + columns.length + " column(s)");
                }
                //intialize sentence id
                String sentenceId = columns[10] + "_" + columns[0];
                //intialize cs type
                String csType = columns[8];
                //intialize sentence type
                String sentenceType = columns[9];
                String csAndSentenceType = csType + "," + sentenceType;
                idToTypeMap.put(sentenceId, csAndSentenceType);
            }
            System.out.println("The size of idToTypeMap: " + idToTypeMap.size());
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddCSTypeAndSentenceType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AddCSTypeAndSentenceType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddCSTypeAndSentenceType.class.getName()).log(Level.SEVERE, null, ex);
        }
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

        final String outputFilename = "11112019_all_clean_and_pns_ver";
        BufferedWriter bw = null;

        //Intialize and assign the output file path
        File outputFile = new File("data/validation/"
                + outputFilename
                + ".csv");
        System.out.println("The file will be saved in: "
                + outputFile.getPath());
        LinkedList<String> idList = new LinkedList(idToLineMap.keySet());
        LinkedList<String> lineList = new LinkedList(idToLineMap.values());
        LinkedList<String> typeList = new LinkedList(idToTypeMap.values());

        try {
            //open output stream output txt file for writing purpose.
            //create new output stream writer
            //create new buffered writer 
            bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(outputFile), "UTF-8"));

            bw.append(FILE_HEADER);
            bw.append(NEW_LINE_SEPARATOR);

            for (int i = 0; i < idList.size(); i++) {
                String[] newColumns = new String[11];
                String[] lineItems = lineList.get(i).split(",");
                String[] typeItems = typeList.get(i).split(",");
                newColumns[0] = lineItems[0];
                newColumns[10] = lineItems[8];
                for (int x = 1; x < 8; x++) {
                    newColumns[x] = lineItems[x];
                }
                newColumns[8] = typeItems[0];
                newColumns[9] = typeItems[1];
                String newLine = String.join(COMMA_DELIMITER, newColumns);
                bw.append(newLine);
                bw.append(NEW_LINE_SEPARATOR);
            }
            System.out.println("The file was created successfully !!!");

            bw.flush();
            bw.close();
            System.out.println("The file has been saved.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddCSTypeAndSentenceType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AddCSTypeAndSentenceType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddCSTypeAndSentenceType.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
