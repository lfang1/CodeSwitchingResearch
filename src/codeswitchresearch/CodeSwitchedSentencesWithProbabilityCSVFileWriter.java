/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import sentenceprocesser.MarkedSentence;
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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class CodeSwitchedSentencesWithProbabilityCSVFileWriter {

    private HashMap<String, Double> chineseIndependentProbabilities;
    private HashMap<String, Double> englishIndependentProbabilities;
    private HashMap<String, Double> bilingualCorpusProbabilities;
    private HashMap<Integer, MarkedSentence> markedSentencesMap;
    private HashMap<Integer, ArrayList<String>> codeSwitchedSentencesWithProbability;

    public CodeSwitchedSentencesWithProbabilityCSVFileWriter(HashMap<String, Double> chineseIndependentProbabilities,
            HashMap<String, Double> englishIndependentProbabilities,
            HashMap<String, Double> bilingualCorpusProbabilities,
            HashMap<Integer, MarkedSentence> markedSentencesMap) {
        this.chineseIndependentProbabilities = chineseIndependentProbabilities;
        this.englishIndependentProbabilities = englishIndependentProbabilities;
        this.bilingualCorpusProbabilities = bilingualCorpusProbabilities;
        this.markedSentencesMap = markedSentencesMap;
        this.codeSwitchedSentencesWithProbability = new HashMap<>();
    }

    public double getAverageIndependentProbabilityOfWords(Boolean isCodeSwitched, ArrayList<String> words) {
        if (words.isEmpty() || words == null) {
            System.err.println("words is empty or null: " + words.toString());
            return 0.0;
        }
        ArrayList<Double> wordsIndependentProbabilities = new ArrayList<>();
        for (String phrase : words) {
            boolean allWordsAreIdentified = true;
            for (String word : phrase.split("\\s")) {
                if (isCodeSwitched) {
                    if (englishIndependentProbabilities.containsKey(word)) {
                        wordsIndependentProbabilities.add(englishIndependentProbabilities.get(word));
                    } else {
                        allWordsAreIdentified = false;
                        System.err.println("Unknown code-switched word: " + word);
                        wordsIndependentProbabilities = new ArrayList<>();
                    }
                } else {
                    if (chineseIndependentProbabilities.containsKey(word)) {
                        wordsIndependentProbabilities.add(chineseIndependentProbabilities.get(word));
                    } else {
                        allWordsAreIdentified = false;
                        System.err.println("Unknown non code-switched word: " + word);
                        wordsIndependentProbabilities = new ArrayList<>();
                    }
                }
            }
        }
        double averageIndependentProbabilityOfWords = getAverage(wordsIndependentProbabilities);
        System.out.println("AverageIndependentProbability: " + averageIndependentProbabilityOfWords);
        return averageIndependentProbabilityOfWords;
    }

    public double getAverageBilingualProbabilityOfWords(ArrayList<String> words) {
        if (words.isEmpty() || words == null) {
            System.err.println("words is empty or null: " + words.toString());
            return 0.0;
        }
        ArrayList<Double> wordsBilingualProbabilities = new ArrayList<>();
        for (String phrase : words) {
            boolean allWordsAreIdentified = true;
            for (String word : phrase.split("\\s")) {
                if (bilingualCorpusProbabilities.containsKey(word)) {
                    wordsBilingualProbabilities.add(bilingualCorpusProbabilities.get(word));
                } else {
                    allWordsAreIdentified = false;
                    System.err.println("Unknown bilingual word: " + word);
                    wordsBilingualProbabilities = new ArrayList<>();
                }
            }
        }
        double averageBilingualProbabilityOfWords = getAverage(wordsBilingualProbabilities);
        System.out.println("AverageBilingualProbabilityOfWords: " + averageBilingualProbabilityOfWords);
        return averageBilingualProbabilityOfWords;
    }

    public double getAverage(ArrayList<Double> probabilities) {
        if (probabilities.isEmpty() || probabilities == null) {
            System.err.println("Probabilities is empty or null: " + probabilities.toString());
            return 0.0;
        }
        double sum = 0.0;
        for (double p : probabilities) {
            sum += p;
        }
        double averageProbability = sum / probabilities.size();
        return averageProbability;
    }

    public void readSegmentedTranslatedSentenceCSVFile() {
        //data/translated-words/segmente-translation-0-199-psucssa-bilingual-corpus.csv
        String filename = "data/translated-words/segmented-translation-0-199-psucssa-bilingual-corpus.csv";
        try {
            File fileDir = new File(filename);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            //Get the title line
            String input = in.readLine();
            //Input format: SentenceID , CodeSwitchedSentence , NumberOfCodeSwitching , CodeSwitch1 , Translation1 ,  CodeSwitch2 , Translation2 , 
            //CodeSwitch3 , Translation3 , CodeSwitch4 , Translation4 , CodeSwitch5 , Translation5 , TranslatedSentence, NEWLINE
            System.out.println("The input format: " + "[0]SentenceID , "
                    + "[1]CodeSwitchedSentence , [2]NumberOfCodeSwitching , "
                    + "[3]CodeSwitch1 , [4]Translation1 ,  "
                    + "[5]CodeSwitch2 , [6]Translation2 , "
                    + "[7]CodeSwitch3 , [8]Translation3 , "
                    + "[9]CodeSwitch4 , [10]Translation4 ,"
                    + "[11]CodeSwitch5 , [12]Translation5 , "
                    + "[13]TranslatedSentence, NEWLINE");
            /*
                "SentenceID,"
                + "AverageIndependentProbabilityOfCodeSwitchedWord,"
                + "AverageIndependentProbabilityOfTranslatedWord,"
                + "AverageIndependentProbabilityOfWholeSentence,"
                + "AverageBilingualCorpusProbabilityOfCodeSwitchedWord,"
                + "AverageBilingualCorpusProbabilityOfTranslatedWord,"
                + "AverageIBilingualCorpusProbabilityOfWholeSentence";
             */
            while ((input = in.readLine()) != null) {
                String[] value = input.split(",");
                if (value.length != 14) {
                    continue;
                }
                int sentenceID = Integer.parseInt(value[0].trim());
                int codeSwitchCount = Integer.parseInt(value[2].trim());
                ArrayList<String> codeSwitchedWords = new ArrayList<>();
                ArrayList<String> translatedWords = new ArrayList<>();
                for (int i = 3; i < (codeSwitchCount * 2 + 3); i += 2) {
                    codeSwitchedWords.add(value[i].trim());
                    translatedWords.add(value[i + 1].trim());
                }
                double averageIndependentProbabilityOfCodeSwitchedWord = getAverageIndependentProbabilityOfWords(true, codeSwitchedWords);
                double averageIndependentProbabilityOfTranslatedWord = getAverageIndependentProbabilityOfWords(false, translatedWords);
//                double averageBilingualCorpusProbabilityOfCodeSwitchedWord = getAverageBilingualProbabilityOfWords(codeSwitchedWords);
//                double averageBilingualCorpusProbabilityOfTranslatedWord = getAverageBilingualProbabilityOfWords(translatedWords);

                MarkedSentence markedSentence = markedSentencesMap.get(sentenceID);

                ArrayList<Double> independentProbabilities = markedSentence.getIndependentProbabilities();
                double averageIndependentProbabilityOfWholeSentence = getAverage(independentProbabilities);
//                ArrayList<Double> bilingualCorpusProbabilities = markedSentence.getBilingualCorpusProbabilities();
//                double averageIBilingualCorpusProbabilityOfWholeSentence = getAverage(bilingualCorpusProbabilities);

                ArrayList<String> entries = new ArrayList<>();
                entries.add(value[0]);
                entries.add(String.valueOf(averageIndependentProbabilityOfCodeSwitchedWord));
                entries.add(String.valueOf(averageIndependentProbabilityOfTranslatedWord));
                entries.add(String.valueOf(averageIndependentProbabilityOfWholeSentence));
                //               entries.add(String.valueOf(averageBilingualCorpusProbabilityOfCodeSwitchedWord));
                //               entries.add(String.valueOf(averageBilingualCorpusProbabilityOfTranslatedWord));
                //               entries.add(String.valueOf(averageIBilingualCorpusProbabilityOfWholeSentence));

                Boolean allEntriesIsValid = true;
                int index = 0;
                for (String s : entries) {
                    if (s.isEmpty() || s == null) {
                        allEntriesIsValid = false;
                        System.err.println("Index of the emply or null entry: " + index);
                        break;
                    } else if (index != 0) {
                        if (Double.parseDouble(s) - 0.0 == 0) {
                            System.err.println("Index of zero probability entry: " + index);
                            allEntriesIsValid = false;
                        }
                    }
                    index++;
                }
                if (allEntriesIsValid) {
                    codeSwitchedSentencesWithProbability.put(sentenceID, entries);
                    System.out.println("Successfully add one line!");
                    System.out.println("SentenceID: " + sentenceID);
                    System.out.println("CodeSiwthcedWords: " + codeSwitchedWords.toString());
                    System.out.println("TranslatedWords: " + translatedWords.toString());
                    System.out.println("AverageIndependentProbabilityOfCodeSwitchedWord: " + averageIndependentProbabilityOfCodeSwitchedWord);
                    System.out.println("AverageIndependentProbabilityOfTranslatedWord: " + averageIndependentProbabilityOfTranslatedWord);
                    System.out.println("AverageIndependentProbabilityOfWholeSentence: " + averageIndependentProbabilityOfWholeSentence);
//                    System.out.println("AverageBilingualCorpusProbabilityOfCodeSwitchedWord: " + averageBilingualCorpusProbabilityOfCodeSwitchedWord);
//                    System.out.println("AverageBilingualCorpusProbabilityOfTranslatedWord: " + averageBilingualCorpusProbabilityOfTranslatedWord);
//                    System.out.println("AverageIBilingualCorpusProbabilityOfWholeSentence: " + averageIBilingualCorpusProbabilityOfWholeSentence);
                }

            }

            in.close();

        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveToCSVFile()
            throws FileNotFoundException, UnsupportedEncodingException {

        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header

        final String FILE_HEADER = "SentenceID,"
                + "AverageIndependentProbabilityOfCodeSwitchedWord,"
                + "AverageIndependentProbabilityOfTranslatedWord,"
                + "AverageIndependentProbabilityOfWholeSentence,"
                + "AverageBilingualCorpusProbabilityOfCodeSwitchedWord,"
                + "AverageBilingualCorpusProbabilityOfTranslatedWord,"
                + "AverageIBilingualCorpusProbabilityOfWholeSentence";

        //psucssa
        //cmucssa
        //pittcssa
        try {
            File outputfileName = new File("data/code-switched-sentences-with-probability/"
                    + "09222018-code-switched-sentences-with-probability-psucssa.csv");
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);

            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            codeSwitchedSentencesWithProbability.forEach((id, probabilities) -> {
                if (probabilities.size() != 4) {
                    System.err.println("The probabilities of the sentence is not 4, but :" + probabilities.size());
                }
                try {
                    for (int i = 0; i < 3; i++) {
                        w.append(probabilities.get(i));
                        w.append(COMMA_DELIMITER);
                    }
                    w.append(probabilities.get(3));
                    w.append(NEW_LINE_SEPARATOR);
                } catch (IOException ex) {
                    Logger.getLogger(CodeSwitchedSentencesWithProbabilityCSVFileWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + "data/code-switched-sentences-with-probability/"
                    + "09222018-code-switched-sentences-with-probability-psucssa.csv");
        }

    }

}
