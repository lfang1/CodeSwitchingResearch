/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class BilingualCorpusProbabilityDatabase {

    private int totalWordCount;
    private HashMap<String, Integer> bilingualCorpusWordFrequencies
            = new HashMap<>();
    HashMap<String, Double> bilingualCorpusWordProbabilities
            = new HashMap<>();
    HashMap<Integer, ArrayList<Double>> bilingualCorpusProbabilitiesOfSentenceAsStrings = new HashMap<>();
    HashMap<Integer, ArrayList<String>> unmarkedSentencesAsStrings = new HashMap<>();

    public BilingualCorpusProbabilityDatabase(int totalWordCount,
            HashMap bilingualCorpusWordFrequencies, HashMap unmarkedSentencesAsStrings) {
        this.totalWordCount = totalWordCount;
        this.bilingualCorpusWordFrequencies = bilingualCorpusWordFrequencies;
        this.unmarkedSentencesAsStrings = unmarkedSentencesAsStrings;
        getBilingualCorpusProbabilities();
    }

    public void getBilingualCorpusProbabilities() {
        bilingualCorpusWordFrequencies.forEach((word, frequency)
                -> bilingualCorpusWordProbabilities.put(
                        word, ((double) frequency / totalWordCount)));
    }

    public HashMap getProbabilitiesOfWordsInASentence() {
        unmarkedSentencesAsStrings.forEach((id, words) -> {
            ArrayList<Double> probabilities = new ArrayList<>();
            words.forEach(word -> probabilities.add(bilingualCorpusWordProbabilities.get(word)));
            bilingualCorpusProbabilitiesOfSentenceAsStrings.put(id, probabilities);
        });
        return bilingualCorpusProbabilitiesOfSentenceAsStrings;
    }
    
    public void saveToCSVFile() {
        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header

        final String FILE_HEADER = "Word,"
                + "Probability,"
                + "Frequency";
        
        //psucssa
        //cmucssa
        //pittcssa
        try {
            File outputfileName = new File("data/bilingual-corpus-probability/"
                    + "09282018-bilingual-corpus-psucssa.csv");
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);

            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            bilingualCorpusWordProbabilities.forEach((word, probability) -> {       
                try {
                    w.append(word);
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(probability));
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(bilingualCorpusWordFrequencies.get(word)));
                    w.append(NEW_LINE_SEPARATOR);
                } catch (IOException ex) {
                    Logger.getLogger(BilingualCorpusProbabilityDatabase.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            System.out.println("CSV file was created successfully !!!");
            
            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + "data/bilingual-corpus-probability/"
                    + "09282018-bilingual-corpus-psucssa.csv");
        }

    }
}
