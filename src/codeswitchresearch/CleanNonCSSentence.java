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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

/**
 *
 * @author Le
 */
public class CleanNonCSSentence {

    private static LinkedList<String[]> sentenceList = new LinkedList<>();
    private static LinkedList<String> originalSentenceList = new LinkedList<>();
    //private static String filename = "01292019_all_non_cs";

    public static void main(String args[]) {
        readCSVFile();
        saveToCSVFile();
    }

    private static void readCSVFile() {
        BufferedReader br = null;

        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";

        try {
            //Reading the csv file
            File fileDir = new File("data/add-id-line/input/"
                    + "01292019_all_non_cs"
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            //Read to skip the header
            br.readLine();
            //Reading from the second line
            while ((line = br.readLine()) != null) {
                String[] sentenceDetails = line.split(COMMA_DELIMITER);
                if (sentenceDetails.length != 5) {
                    System.out.println("Error! Sentence "
                            + sentenceDetails[0] + " " + sentenceDetails[4] + " have "
                            + sentenceDetails.length + " items.");
                    System.out.println(line);
                }
                if (sentenceDetails[3].isEmpty()) {
                    sentenceDetails[3] = "[]";
                }
                //Check if a sentence is duplicated. 
                //Only add a new sentence to the list
                addNewSentence(sentenceDetails);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.out.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    }

    /**
     * *
     * Check if a sentence is duplicated. Only add a new sentence to the list.
     *
     * @param sentenceDetails a list to store sentence details
     */
    private static void addNewSentence(String[] sentenceDetails) {
        if (!originalSentenceList.contains(sentenceDetails[1])) {
            originalSentenceList.add(sentenceDetails[1]);
            //System.out.println(sentenceDetails[1]);
            sentenceList.add(sentenceDetails);
        }
    }

    private static String removeExtraSpace(String sentence) {
        String newSentence = String.join(" ", sentence.split("\\s+"));
        return newSentence;
    }

    private static void saveToCSVFile() {
        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header
        final String FILE_HEADER = "sentence id,"
                + "sentence,"
                + "chinese word index,"
                + "punct index,"
                + "source";

        try {
            File outputfileName = new File("data/add-id-line/input/"
                    + "01292019_all_clean_non_cs"
                    + ".csv");
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);

            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            for (String[] s : sentenceList) {
                try {
                    int length = s.length;
                    w.append(s[0]);
                    w.append(COMMA_DELIMITER);
                    w.append(removeExtraSpace(s[1]));
                    w.append(COMMA_DELIMITER);
                    for (int i = 2; i < length - 1; i++) {
                        w.append(s[i]);
                        w.append(COMMA_DELIMITER);
                    }
                    w.append(s[length - 1]);
                    w.append(NEW_LINE_SEPARATOR);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            System.out.println("CSV file was created successfully !!!");
            w.flush();
            w.close();
            System.out.println("The file has been saved.");
        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + "data/add-id-line/input/"
                    + "01292019_all_clean_non_cs"
                    + ".csv");
            e.printStackTrace();
        }
    }
}
