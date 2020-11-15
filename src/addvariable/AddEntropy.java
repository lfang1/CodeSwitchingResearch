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
import java.util.LinkedList;

/**
 *
 * @author Le
 */
public class AddEntropy {

    private static LinkedList<String> sentenceList = new LinkedList<>();
    private static LinkedList<String> entropyList = new LinkedList<>();

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        readMainCSVFile();
        readEntropyFile();
        saveToCSVFile();
    }

    private static void readMainCSVFile() {
        BufferedReader br = null;

        String filename = "11152019_entropy_appended_input_R_v4";
        try {
            //Read CSV file
            File fileDir = new File("data/add_entropy/input/"
                    + filename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    sentenceList.add(line);
                }
            }
            System.out.println(sentenceList.size() + " lines have been read");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.err.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    }

    private static void readEntropyFile() {
        BufferedReader br = null;

        try {
            //Read CSV file            
            File fileDir = new File("D:/App/Dropbox/Fred/research/short_paper/add_entropy/output/entropy_result/11152019_55k_one_word_after_cs_point_entropy.csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    entropyList.add(line);
                }
            }
            System.out.println(entropyList.size() + " lines have been read");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.err.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }

    }
    
    public static void saveToCSVFile()
            throws FileNotFoundException, UnsupportedEncodingException {

        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header
        final String FILE_HEADER = sentenceList.get(0) +
                COMMA_DELIMITER
                //+ "entropy_at_cs_point";
                + "entropy_one_word_after_cs_point";

        System.out.println("FILE_HEADER: " + FILE_HEADER);
        String outputFilename = "D:/App/Dropbox/Fred/research/short_paper/add_entropy/output/11152019_two_entropies_appended_input_R_v4.csv";
        //String outputFilename = "D:/App/Dropbox/Fred/research/short_paper/add_entropy/output/11152019_input_R_v6.csv";

        try {
            File outputfileName = new File(outputFilename);
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);

            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            if(sentenceList.size() != entropyList.size()) {
                System.err.println("sentenceLIst and entropyList don't have the same size!");
                System.err.println(sentenceList.size() + "; " + entropyList.size());
                return;
            } else {
                for(int i = 1; i < sentenceList.size(); i++) {
                    w.append(sentenceList.get(i) + COMMA_DELIMITER + entropyList.get(i).split(COMMA_DELIMITER)[1]);
                    w.append(NEW_LINE_SEPARATOR);
                }
            }          

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + outputFilename);
        }
    }  

}
