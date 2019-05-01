/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 *
 * @author Le
 */
public class Alignment {

    static String filename = "alignment2";
    static LinkedList<String[]> pairsList = new LinkedList<>();

    public static void main(String[] args) {
        readCSVFile();
    }

    public static void readCSVFile() {
        BufferedReader br = null;

        try {
            //Reading the text file
            File fileDir = new File("data/alignment/"
                    + filename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            String[] pairs = new String[6];
            int pointer = 0;
            while ((line = br.readLine()) != null) {
                if (line.contains("143")) {
                    System.out.println("Reach the end of the file.");
                    System.out.println(pairsList.get(0)[0]);
                    System.out.println(pairsList.get(pairsList.size()-1)[0]);
                    break;
                }
                //System.out.println(line);
                if (pointer == 6) {
                    pointer = 0;
                    pairsList.add(pairs);
                    pairs = new String[6];
                    continue;
                } else if (pointer == 2) {
                    /*
                        if (!line.substring(0, 12).equals("code_switch:")) {
                            System.out.println(pairs[0] + "\n" + pairs[1] + "\n" + line);
                            System.out.println("line.substring(0,12): " + line.substring(0, 12));
                        }
                        if(line.split(",").length != 2) {
                            System.out.println("Invalid length");
                        }
                     */
                    line = line.substring(12);
                }
                pairs[pointer] = line;
                pointer++;
            }

            System.out.println(pairsList.size() + "sentences added.");

        } catch (IOException ie) {
            System.err.println("Problem occurs when reading the csv file");
        }

    }
}
