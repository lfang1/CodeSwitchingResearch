/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Le
 */
public class NonCodeSwitchedSentenceFinder {

    static HashMap<String, Double> englishIndependentProbabilities
            = new HashMap<>();
    static HashMap<String, Double> chineseIndependentProbabilities
            = new HashMap<>();
    static BilingualCorpusProbabilityDatabase database;
    static HashMap<Integer, ArrayList<String>> markedSentencesAsStrings
            = new HashMap<>();
    static HashMap<Integer, ArrayList<String>> unmarkedSentencesAsStrings
            = new HashMap<>();
    static HashMap<Integer, MarkedSentence> markedSentencesMap = new HashMap<>();
    static HashMap<Integer, ArrayList<Double>> bilingualCorpusProbabilitiesOfSentences = new HashMap<>();
       
    static ArrayList<Integer> validSentenceID = new ArrayList<>();
    
    static HashMap<String, String> emailAddressToID = new HashMap<>();
    static HashMap<String, String> emailIDToAddress = new HashMap<>();
    static HashMap<Integer, String> sentenceIDToEmailID = new HashMap<>();
    static ArrayList<String> validEmailID = new ArrayList<>();
    
    static HashMap<String, String> phoneNumberToID = new HashMap<>();
    static HashMap<String, String> phoneIDToNumber = new HashMap<>();
    private static HashMap<Integer, String> sentenceIDToPhoneID = new HashMap<>();
    private static ArrayList<String> validPhoneID = new ArrayList<>();
//    static HashMap<String, Integer> bilingualCorpusFrequencies = new HashMap<>();
//    static HashMap<String, Double> bilingualCorpusProbabilities = new HashMap<>();

    static int totalWordCount = 0;
    static final String UNKNOWN_WORD = "UNKNOWN_WORD";
    static HashMap<String, Integer> unknownWordBook = new HashMap<>();
    static String corpusName = "";

    public static void main(String args[])
            throws FileNotFoundException, UnsupportedEncodingException {
        //Note: ● in 55k-english-word-probabilities.txt has been excluded 
        //by changing from ● to "●"
        englishIndependentProbabilities
                = IndependentProbabilityDatabase.getIndependentProbabilities(
                        "55k-english-word-independent-probabilities.txt");
        //Get english independent probability csv file
        //IndependentProbabilityDatabase.saveToCSVFile(1, englishIndependentProbabilities);
        chineseIndependentProbabilities
                = IndependentProbabilityDatabase.getIndependentProbabilities(
                        "55k-chinese-word-independent-probabilities.txt");
        //Get chinese independent probability csv file
        //IndependentProbabilityDatabase.saveToCSVFile(0, chineseIndependentProbabilities);

        //first-line-empty-segmented-psucssa-bilingual-corpus.txt
        //first-line-empty-segmented-cmucssa-bilingual-corpus.txt
        //first-line-empty-segmented-pittcssa-bilingual-corpus.txt
        //System.out.println("Please type the corpus filename: ");
        //Scanner scaner = new Scanner(System.in);
        //String filename = scaner.nextLine();
        //getSentenceMap(filename);
        //psucssa
        //cmucssa
        //pittcssa
        corpusName = "psucssa";
        getSentenceMap("first-line-empty-segmented-" + corpusName + "-bilingual-corpus.txt");
        NonCodeSwitchSentencesWithDetailsCSVFileWriter.saveToCSVFile("01072019-" + corpusName, markedSentencesMap);

        System.out.println("Total Word Count in the corpus: " + totalWordCount);

    }

    private static void getSentenceMap(String filename)
            throws FileNotFoundException, UnsupportedEncodingException {
        try {
            File fileDir = new File("data/bilingual-corpus/"
                    + filename);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            //Read an empty line in order to 
            //get rid of the "?" mark in the first line
            String newLine = "";
            newLine = in.readLine();

            int sentenceID = 0;
            //remove repeated line
            String lastLine = "";
            while ((newLine = in.readLine()) != null) {
                if (newLine.isEmpty()) {
                    continue;
                }
                if (newLine.equals(lastLine)) {
                    continue;
                } else {
                    lastLine = newLine;
                }

                //Mark sentence boundary
                newLine = SentenceBoundaryMarker.markSentenceBoundary(newLine);

                String lastMarkedSentence = "";
                for (String s : newLine.split("\u2605")) {
                    if (s.isEmpty() || s.length() < 5) {
                        continue;
                    }
                    if (UrlDetector.isUrl(s)) {
                        continue;
                    }
                    MarkedSentence markedSentence = getMarkedSentence(sentenceID, s);

                    ArrayList<String> wordsInSentence = new ArrayList<>();
                    wordsInSentence = markedSentence.getWordsInSentence();
                    boolean unknownWordExist
                            = markedSentence.getUnknownWordCount() > 0;

                    if (unknownWordExist) {
                        continue;
                    }
                    if (wordsInSentence.size() < 3) {
                        continue;
                    }
                    if (markedSentence.getChineseWordCount() == 0) {
                        continue;
                    }
                    if (markedSentence.getEnglishWordCount() > 0) {
                        continue;
                    }
                    if (corpusName.equalsIgnoreCase("psucssa")) {
                        //Ignore quote in post for psucssa corpus
                        if (markedSentence.getWholeSentence().endsWith("说道 ：")) {
                            continue;
                        }
                    } else if (corpusName.equalsIgnoreCase("pittcssa")) {
                        //Ignore ending suffix for pittcssa corpus
                        if (markedSentence.getWholeSentence().endsWith("编辑") && markedSentence.getWholeSentence().startsWith("本 帖")) {
                            continue;
                        }
                    }

                    String newSentence = "";
                    for (String word : wordsInSentence) {
                        newSentence = newSentence + " " + word;
                    }

                    if (lastMarkedSentence.equals(newSentence)) {
                        continue;
                    } else {
                        lastMarkedSentence = newSentence;
                    }

                    //System.out.println("Current sentence: " + sentenceID + " " + newSentence);
                    validSentenceID.add(sentenceID);
                    unmarkedSentencesAsStrings.put(sentenceID, markedSentence.getUnmarkedWordsInSentence());
                    markedSentencesAsStrings.put(sentenceID, wordsInSentence);
                    markedSentencesMap.put(sentenceID, markedSentence);
                    sentenceID++;
                }
            }
            in.close();

        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static HashMap<String, String> getEmailMap() {
        for (int sentenceID : validSentenceID) {
            validEmailID.add(sentenceIDToEmailID.get(sentenceID));
        }
        HashMap<String, String> validEmailIDToAddress = new HashMap<>();
        for (String emailID : validEmailID) {
            if (emailID != null) {
                validEmailIDToAddress.put(emailID, emailIDToAddress.get(emailID));
            }
        }
        return validEmailIDToAddress;
    }
    
    public static HashMap<String, String> getPhoneMap() {
         for (int sentenceID : validSentenceID) {
            validPhoneID.add(sentenceIDToPhoneID.get(sentenceID));
        }
        HashMap<String, String> validPhoneIDToNumber = new HashMap<>();
        for (String phoneID : validPhoneID) {
            if (phoneID != null) {
                validPhoneIDToNumber.put(phoneID, phoneIDToNumber.get(phoneID));
           }
        }
       return validPhoneIDToNumber;
    }
    

    //Mark code-switched word with black dot "\u2022" ●
    public static MarkedSentence getMarkedSentence(int id, String sentence) {
        ArrayList<String> wordsInSentence = new ArrayList<>();
        ArrayList<Integer> indicesOfCodeSwitchedWord = new ArrayList<>();
        ArrayList<Integer> indicesOfPunctuation = new ArrayList<>();
        ArrayList<Integer> indicesOfChineseWord = new ArrayList<>();
        ArrayList<Double> independentProbabilities = new ArrayList<>();
        boolean codeSwitchOccur = false;
        int chineseWordCount = 0;
        int englishWordCount = 0;
        int unknownWordCount = 0;
        int currentPositionIndex = 0;
        for (String next : sentence.split("\\s")) {
            if (next.isEmpty()) {
                continue;
            }
            next = PunctuationDetector.markPunctuation(next);
            if (PunctuationDetector.isPunctuation(next)) {
                next = next.replace("\u2605", "");
                if (next.contains("@")) {
                    String emailID = "";
                    if (emailAddressToID.containsKey(next)) {
                        emailID = emailAddressToID.get(next);
                    } else {
                        emailID = "em" + emailAddressToID.size();
                        emailAddressToID.put(next, emailID);
                        emailIDToAddress.put(emailID, next);
                        sentenceIDToEmailID.put(id, emailID);
                    }
                    wordsInSentence.add(emailID);
                    indicesOfCodeSwitchedWord.add(currentPositionIndex);
                    englishWordCount++;
                } else if (AmericanPhoneNumberDetector.validatePhoneNumber(next)) {
                    String phoneID = "";
                    if (phoneNumberToID.containsKey(next)) {
                        phoneID = phoneNumberToID.get(next);
                    } else {
                        phoneID = "pn" + phoneNumberToID.size();
                        phoneNumberToID.put(next, phoneID);
                        phoneIDToNumber.put(phoneID, next);
                        sentenceIDToPhoneID.put(id, phoneID);
                    }
                    wordsInSentence.add(phoneID);
                    indicesOfCodeSwitchedWord.add(currentPositionIndex);
                    englishWordCount++;
                } else {                   
                    wordsInSentence.add(next);
                    indicesOfPunctuation.add(currentPositionIndex);
                    //No need to have punctuation count because it will be calculated by the list's size later
                }
                currentPositionIndex++;
                //Assign indepentProabilities/ relative freuqency for punctuation
                independentProbabilities.add(0.0);
                continue;
            }

            if (chineseIndependentProbabilities.containsKey(next)) {
                wordsInSentence.add(next);
                indicesOfChineseWord.add(currentPositionIndex);
                currentPositionIndex++;
                chineseWordCount++;
                totalWordCount++;
                independentProbabilities.add(chineseIndependentProbabilities.get(next));
            } else if (englishIndependentProbabilities.containsKey(next)) {
                String newSentence = "";
                newSentence = "\u2022" + next + "\u2022";
                wordsInSentence.add(newSentence);
                indicesOfCodeSwitchedWord.add(currentPositionIndex);
                currentPositionIndex++;
                englishWordCount++;
                totalWordCount++;
                independentProbabilities.add(englishIndependentProbabilities.get(next));
            } else {
                wordsInSentence.add(UNKNOWN_WORD);
                unknownWordCount++;
                currentPositionIndex++;
                independentProbabilities.add(0.0);
            }

        }

        MarkedSentence markedSentence = new MarkedSentence(id, codeSwitchOccur,
                indicesOfCodeSwitchedWord,
                indicesOfPunctuation,
                indicesOfChineseWord,
                wordsInSentence, chineseWordCount, englishWordCount,
                unknownWordCount, independentProbabilities);

        return markedSentence;
    }
}
