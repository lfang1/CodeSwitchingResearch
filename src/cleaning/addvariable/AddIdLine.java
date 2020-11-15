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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

/**
 *
 * @author Le
 */
public class AddIdLine {

    private static String filename = "03022019_all_clean_and_pns_ver";
    private static LinkedList<String[]> sentenceList = new LinkedList<>();

    public static void main(String[] args) {
        readCSVFile();
        saveToFile();
    }

    private static void readCSVFile() {
        BufferedReader br = null;
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";
        try {
            //Reading the csv file
            File fileDir = new File("data/add-id-line/input/"
                    + filename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
//            //Read to skip the header
//            br.readLine();
//            //Reading from the second line
            while ((line = br.readLine()) != null) {
                String[] sentenceDetails = line.split(COMMA_DELIMITER);
                if (sentenceDetails.length != 11) {
                    System.out.println("the line doesn't have 5 columns, but " + sentenceDetails.length);
                    break;
                }
                String id = sentenceDetails[10] + "_" + sentenceDetails[0];
                String translation = sentenceDetails[2];
                sentenceList.add(new String[]{id, translation});
//               System.out.println("id: " + id + "\n");
//                System.out.println("translation: " + translation);
//                System.out.println();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.err.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    }

    private static void saveToFile() {
        try {
            File outputfileName = new File("data/add-id-line/output/"
                    + filename
                    + "_id_line_added"
                    + ".txt");
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            for (String[] s : sentenceList) {
                if (s.length != 2) {
                    System.out.println("The length is not 2, but " + s.length);
                    System.exit(0);
                }
                w.append(s[0]);
                w.append("\n");
                w.append(s[1]);
                w.append("\n");
            }

            System.out.println("The file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");
        } catch (IOException ie) {
            ie.printStackTrace();
            System.err.println("Problem writing to the "
                    + "data/add-id-line/output/"
                    + filename
                    + "_id_line_added"
                    + ".txt");
        }
    }

}
