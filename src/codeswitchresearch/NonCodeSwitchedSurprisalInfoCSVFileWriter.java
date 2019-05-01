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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class NonCodeSwitchedSurprisalInfoCSVFileWriter {
    //psucssa
    //cmucssa
    //pittcssa
    private static String corpusName = "psucssa";
    private static ArrayList<SurprisalsOfASentence> soasList = new ArrayList<>();

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        readSurprisalCSVFile();
        saveToCSVFile(corpusName, soasList);
    }

    private static void readSurprisalCSVFile() {
        BufferedReader br = null;
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";

        try {
            //Reading the csv file
            //Before run this file,  please update the path of the csv file
            File fileDir = new File("data/surprisal/surprisal-non-cs/formated/"
                    + "01072019-"
                    + corpusName
                    + "-non-cs-surprisal-only.csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            //Read to skip the header
            br.readLine();
            //Reading from the second line
            while ((line = br.readLine()) != null) {
                String[] sentenceDetails = line.split(COMMA_DELIMITER);
                //true, if it is an empty row
                if (sentenceDetails.length != 64) {
                    System.out.println("Sentence " + sentenceDetails[0] + " does not have 64 entries, but " + sentenceDetails.length);
                    return;
                } else {
                    int sentenceId = Integer.parseInt(sentenceDetails[0]);
                    int numberOfWords = Integer.parseInt(sentenceDetails[2]);

                    ArrayList<Double> surprisals = new ArrayList<>();
                    //Add all surprisals, except the surprisal of </s> (end of sentence tag)
                    boolean hasNegativeInf = false;
                    for (int i = 3; i < 3 + numberOfWords; i++) {
                        double oneSurprisal;
                        if (sentenceDetails[i].equals("#NAME?")) {
                            hasNegativeInf = true;
                            oneSurprisal = -Double.NEGATIVE_INFINITY;
                        } else {
                            oneSurprisal = -Double.parseDouble(sentenceDetails[i]);
                        }
                        surprisals.add(oneSurprisal);
                    }
                    if(hasNegativeInf) {
                        continue;
                    }                   
                                    
                    double averageSurprisalWithPunct = getAverageSurprisalWithPunct(surprisals);                                  

                    SurprisalsOfASentence soas = new SurprisalsOfASentence(
                            sentenceId,
                            numberOfWords,
                            averageSurprisalWithPunct,                         
                            corpusName);

                    soasList.add(soas);
                    /*
                    System.out.println("SentenceId: " + sentenceId
                            + " " + "numberOfWords: " + numberOfWords
                            + " " + "averageSurprisal: " + averageSurprisal 
                            + " " + "averageSurprisalWithPunct: " + averageSurprisalWithPunct                          
                            + " " + "corpusName: " + corpusName);
                */
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static double getAverageSurprisal(ArrayList<Integer> indicesOfPunctuationList, ArrayList<Double> surprisals) {
        double sum = 0.0;
        for (int i = 0; i < surprisals.size(); i++) {
            if (indicesOfPunctuationList.contains(i)) {
                continue;
            }
            sum += surprisals.get(i);
        }
        double average = sum / (surprisals.size() - indicesOfPunctuationList.size());
        return average;
    }

    public static double getAverageSurprisalWithPunct(ArrayList<Double> surprisals) {
        double sum = 0.0;
        for (int i = 0; i < surprisals.size(); i++) {
            sum += surprisals.get(i);
        }
        double average = sum / surprisals.size();
        return average;
    }

    public static void saveToCSVFile(String corpusName, ArrayList<SurprisalsOfASentence> soasList)
            throws FileNotFoundException, UnsupportedEncodingException {

        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header
        final String FILE_HEADER = "SentenceID,"
                + "NumberOfWords,"
                + "AverageSurprisalWithPunct,"
                + "CorpusName";

        try {
            File outputfileName = new File("data/surprisal/"
                    + "surprisal-from-5grams-chinese-lm-with-punct/"
                    + "01102019-surprisal-with-punct-"
                    + corpusName
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

            for (SurprisalsOfASentence soas : soasList) {
                try {
                    w.append(String.valueOf(soas.getSentenceId()));
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(soas.getNumberOfWords()));
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(soas.getAverageSurprisalWithPunct()));
                    w.append(COMMA_DELIMITER);                  
                    w.append(String.valueOf(soas.getCorpusName()));                    
                    w.append(NEW_LINE_SEPARATOR);

                } catch (IOException ex) {
                    Logger.getLogger(SurprisalInfoCSVFileWriter.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            };

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + "data/surprisal/"
                    + "surprisal-from-5grams-chinese-lm-with-punct/"
                    + "01102019-surprisal-with-punct"
                    + corpusName
                    + ".csv");
        }
    }
}
