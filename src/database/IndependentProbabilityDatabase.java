/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class IndependentProbabilityDatabase {

    public IndependentProbabilityDatabase() {

    }

    public static ArrayList<String> getWordList(String filename) {
        ArrayList<String> wordList = new ArrayList<>();
        try {
            File fileDir = new File("data/independent-probability-txt/"
                    + filename);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            //read an empty line to get rid of the leading "?"
            //skip the first line
            String input = in.readLine();

            // Split probability and word
            while ((input = in.readLine()) != null) {
                String[] value = input.split("\\t");
                if (value.length != 2) {
                    continue;
                }              
                String word = value[1];
                wordList.add(word);
            }

            in.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }

        return wordList;
    }

    public static HashMap getIndependentProbabilities(String filename) {
        HashMap<String, Double> independentProbabilities
                = new HashMap<>();

        try {
            File fileDir = new File("data/independent-probability-txt/"
                    + filename);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            //read an empty line to get rid of the leading "?"
            //skip the first line
            String input = in.readLine();

            // Split probability and word
            while ((input = in.readLine()) != null) {
                String[] value = input.split("\\t");
                if (value.length != 2) {
                    continue;
                }
                double probability = Double.parseDouble(value[0]);
                String word = value[1];
                independentProbabilities.put(word, probability);
            }

            in.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }

        return independentProbabilities;
    }

    public static void saveToCSVFile(int type, HashMap<String, Double> independentProbabilities) {
        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header

        final String FILE_HEADER = "Word,"
                + "Probability";

        String pathname = "";
        try {

            if (type == 0) {
                pathname = "data/independent-probability-csv/"
                        + "chinese-independent-probability.csv";
            } else if (type == 1) {
                pathname = "data/independent-probability-csv/"
                        + "english-independent-probability.csv";
            }

            File outputfileName = new File(pathname);
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);

            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            independentProbabilities.forEach((word, probability) -> {
                try {
                    w.append(word);
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(probability));
                    w.append(NEW_LINE_SEPARATOR);
                } catch (IOException ex) {
                    Logger.getLogger(IndependentProbabilityDatabase.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + pathname);
        }

    }

}
