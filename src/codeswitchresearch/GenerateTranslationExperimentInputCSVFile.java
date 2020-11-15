/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import infodetector.AmericanPhoneNumberDetector;
import amtexperiment.WorkerManager;
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generate input file for Amazon Mechanical Turk translation experiment
 * @author Le
 */
public class GenerateTranslationExperimentInputCSVFile {

    //a linked list stores code-switch sentences
    private static LinkedList<String> codeSwitchSentenceWithNoPersonalIdentifierList;
    //a linked list stores non-code-switch sentences
    private static LinkedList<String> nonCodeSwitchSentenceWithNoPersonalIdentifierList;
    //a linked list store a CS-sent/nonCS-sent pair
    private static LinkedList<String[]> sentencePairWithNoPersonalIdentifierList;
    //a linked list stores task one control sentences
    //private static LinkedList<String> taskOneControlSentenceList;
    //a linked list stores task1 good control sentences
    private static LinkedList<String> taskOneGoodControlSentenceList;
    //a linked list stores task1 bad control sentences
    private static LinkedList<String> taskOneBadControlSentenceList;
    //a linked list store cs-sentence ids used as controls that will be excluded from sampling
    private static LinkedList<String> csSentenceIDOfControlList;
    //a linked list stores task2 good control sentences
    private static LinkedList<String> taskTwoGoodControlSentenceList;
    //a linked list stores task2 bad control sentences
    private static LinkedList<String> taskTwoBadControlSentenceList;
    //a linked list stores n randomly sampled CS-sent/nonCS-sent pair
    private static LinkedList<String[]> nRandomlySampledPairsOfSentenceList;
    //a linked hashmap stores randomly assigned M pairs of CS-sent/nonCS-sent to P participants, with the constraints:
    // -each pair of CS-sent/nonCS-sent has to be seen by R different participants
    private static LinkedHashMap<Integer, LinkedList<String[]>> participantIDToSentencePairs;
    //a linked hash map stores a random sentence list for each participantID
    private static LinkedHashMap<Integer, LinkedList<String>> participantIDToRandomSentenceList;
    //a link stores random sentence lists with control sentences added
    private static LinkedList<LinkedList<String>> taskOneSentenceLists;
    //a link stores random sentence lists with control sentences added
    private static LinkedList<LinkedList<String>> taskTwoSentenceLists;
    //a list to store participant IDs
    private static LinkedList<String> qualifiedWorkerIDList;
    //a link stores random sentence pair lists with no control sentence added yet
    //private static LinkedHashMap<String, String> workerIDToWorkerInfo;
    private static LinkedList<LinkedList<String[]>> sentencePairLists;
    //a map stores a workerID to sentence list ID(s) for task one
    private static LinkedHashMap<String, LinkedList<Integer>> taskOneWorkerIDToSentenceListIDs;
    //a map stores a workerID to sentence list ID(s) for task two
    private static LinkedHashMap<String, LinkedList<Integer>> taskTwoWorkerIDToSentenceListIDs;
    private static String date;
    private static WorkerManager manager;
    private static int numberOfAssignmentPerWorker;
    
    public static void main(String[] args) {
        //initialize data
        date = "12062019";
        manager = new WorkerManager();
        //initialize workerList and initialize variables in manager 
        initializeQualifiedWorkerList();
        //initialize taskOneControlSentenceList and csSentenceIDOfControlList;
        initializeTaskOneControlSentenceList();     
        //initialize taskTwoControlSentenceList and add a new item (if there is any) to csSentenceIDOfControlList;
        initializeTaskTwoControlSentenceList();      
        //initialize both codeSwitchSentenceList and nonCodeSwitchSentenceList;
        initializeSentenceWithNoPersonalIdentifierLists();
        //initialize a list that randomly samples 500 pairs of CS-sent/nonCS-sent
        initializenRandomlySampledNPairsOfSentences(500);
        //initialize a map that randomly assign 20 pairs of CS-sent/nonCS-sent in a way that each pair is seen by 3 different participants
        //initialize senencePairList, taskOneWorkerIDToSentenceListIDs and taskTwoWorkerIDToSentenceListIDs
        initializeSentencePairLists(20, 3);
        //intiailize taskOneSentenceLists by randomly inserting control sentences in second half of each list in taskOneSentencePairLists    
        initializeTaskOneSentenceLists(numberOfAssignmentPerWorker);
        //intiailize taskTwoSentenceLists by randomly inserting control sentences in second half of each list in taskTwoSentencePairLists
        initializeTaskTwoSentenceLists(numberOfAssignmentPerWorker);
        //generate a set of files for task 1
        //saveTaskOneExperimentCSVFile();
        //generate a set of files for task 2
        saveTaskTwoExperimentCSVFile();
        assignQualificationToWorkers();
    }

    private static void assignQualificationToWorkers() {
        manager.saveQualificationAssignment(date, taskOneWorkerIDToSentenceListIDs, taskTwoWorkerIDToSentenceListIDs);
    }
        
    private static void initializeQualifiedWorkerList() {   
        qualifiedWorkerIDList = new LinkedList<>();        
        manager.fillQualifiedWorkerIDList();
        manager.fillWorkerInfoList("12062019_all_workers.csv");
        qualifiedWorkerIDList = manager.getQualifiedWorkerIDList();
    }

    private static void initializeSentenceWithNoPersonalIdentifierLists() {
        codeSwitchSentenceWithNoPersonalIdentifierList = new LinkedList<>();
        nonCodeSwitchSentenceWithNoPersonalIdentifierList = new LinkedList<>();
        sentencePairWithNoPersonalIdentifierList = new LinkedList<>();

        BufferedReader br;
        String filename = "input_R_v4.csv";
        try {
            //initialize input file path
            File fileDir = new File("data/get_experiment_sentences/input/sample_source/"
                    + filename);
            //initialize buffered reader br
            //create an input stream read to read bytes and decode them to characters
            //create a file input stream by opening a connection to an actual file and use decode "UTF-8"
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF-8"));

            //initialize a string to store each line in the input file
            String line = "";
            //initialize a int to store line id, to track which line has been read, start at 1
            int lineId = 1;
            //read to skip header
            br.readLine();
            //check if a person identifier is in the cs sentence
            boolean isPersonalIdentifierCS = false;
            //check if a person identifier is in the non-cs sentence
            boolean isPersonalIdentifierNonCS = false;

            //check if cs sentence is used as a control
            boolean isControlCS = false;

            boolean isEnglishWordInCSTranslation = false;
            boolean isEnglishWordInNonCS = false;
            Pattern englishWordPattern = Pattern.compile("[\\w&&[^\\d]]+");

            //initialize a string to store information of both original cs-sentence and translated cs-sentence
            //String originalSentAndTranslation = "";
            //initialize a string to store information of cs-sentence
            String csSentence = "";
            //initialize a string to store information of non-cs-sentence
            String nonCSSentence = "";
            //initialize int store last line id
            int lastCSLineId = -2;
            //read until the end of the file
            while ((line = br.readLine()) != null) {
                //initialize a string[] to store items in each line
                String[] tokens = line.split(",");
                //check if the length of columns is 31
                if (tokens.length != 31) {
                    System.err.println("Line_" + lineId + " amount of tokens: " + tokens.length);
                    System.exit(0);
                }

                //String sentenceInfo  tokens[0] sentence type, tokens[1] university, tokens[2] id of the sentence
                String sentenceInfo = tokens[0] + "," + tokens[1] + "_" + tokens[2];

                //tokens[4] original sentence, tokens[5] tranlsation, tokens[6] word_id (starts at 1), tokens[8] first_cs_word_translation
                switch (lineId % 2) {
                    //the current line is a code-switched sentence.
                    case 1:
                        //store information of both original cs-sentence and translated cs-sentence
                        //originalSentAndTranslation = lineId + "," + sentenceInfo + "," + tokens[4] + "," + tokens[5] + "," + tokens[6] + "," + tokens[8];
                        lastCSLineId = lineId;
                        //store cs-sentence
                        csSentence = sentenceInfo + "," + tokens[4] + "," + tokens[5];

                        //true if the sentence contains personal identifier such "em@", "pn-"
                        if (foundPersonalIdentifier(tokens[4]) || foundPersonalIdentifier(tokens[5])) {
                            isPersonalIdentifierCS = true;
                        } else {
                            isPersonalIdentifierCS = false;
                        }

                        Matcher englishWordMatcher = englishWordPattern.matcher(tokens[5]);
                        //true if the translaiton has English characters
                        if (englishWordMatcher.find()) {
                            isEnglishWordInCSTranslation = true;
                        } else {
                            isEnglishWordInCSTranslation = false;
                        }

                        //true if the cs-sentence is used as a control
                        if (csSentenceIDOfControlList.contains(sentenceInfo)) {
                            //System.out.println(sentenceInfo + " is found.");
                            isControlCS = true;
                        } else {
                            isControlCS = false;
                        }

                        //initialize a string array to store words in translated cs-sentence
                        //String[] wordsInTranslation = tokens[5].split(" ");
                        //Check if the cs-word and translated word are at the same position.
                        //After test, cs-word and translated word are at the same position in all the 1476 cs-sentences 
//                        if (!wordsInTranslation[Integer.parseInt(tokens[6]) - 1].equals(tokens[8])) {
//                            System.out.println("Line" + lineId + " has different word.");
//                            System.out.println("first_cs_word_translation: " + tokens[8]);
//                            System.out.println("The word at " + tokens[6] + ": " + wordsInTranslation[Integer.parseInt(tokens[6]) - 1]);
//                        }                       
                        break;
                    //the current line is a non-code-switched sentence.
                    case 0:
                        //store information of non-cs-sentence
                        //String nonCodeSwitchSentence = lineId + "," + sentenceInfo + "," + tokens[4] + "," +tokens[5] + "," + tokens[6] + "," + tokens[8];
                        nonCSSentence = sentenceInfo + "," + tokens[4] + "," + tokens[5];

                        //check if the sentence contains personal identifier such "em@", "pn-"
                        if (foundPersonalIdentifier(tokens[4]) || foundPersonalIdentifier(tokens[5])) {
                            isPersonalIdentifierNonCS = true;
                        } else {
                            isPersonalIdentifierNonCS = false;
                        }

                        Matcher englishWordMatcherNonCS = englishWordPattern.matcher(tokens[5]);
                        //true if the non-cs sentence has English characters
                        if (englishWordMatcherNonCS.find()) {
                            isEnglishWordInNonCS = true;
                        } else {
                            isEnglishWordInNonCS = false;
                        }

                        //initialize a string array to store words in non-cs-sentence
                        //String[] wordsInNonCodeSwitchSentence = tokens[5].split(" ");
                        //Check if the cs-word and translated word are at the same position.
                        //After test, cs-word and translated word are at the same position in all the 1476 non-cs-sentences 
//                        if (!wordsInNonCodeSwitchSentence[Integer.parseInt(tokens[6]) - 1].equals(tokens[8])) {
//                            System.out.println("Line" + lineId + " has different word.");
//                            System.out.println("first_cs_point_word: " + tokens[8]);
//                            System.out.println("The word at " + tokens[6] + ": " + wordsInNonCodeSwitchSentence[Integer.parseInt(tokens[6]) - 1]);
//                        }
                        //true if no personal identifer is found in the pair of cs-sentence and non-cs-sentence
                        if (!isPersonalIdentifierCS && !isPersonalIdentifierNonCS && !isEnglishWordInCSTranslation && !isEnglishWordInNonCS && !isControlCS) {
                            //String[] items = csSentence.split(",");
                            if (lineId - lastCSLineId == 1) {
                                String[] sentencePair = new String[2];
                                sentencePair[0] = csSentence;
                                sentencePair[1] = nonCSSentence;
                                if (csSentence.contains("em@") || nonCSSentence.contains("em@")) {
                                    System.out.println(csSentence + "\n " + nonCSSentence);
                                }
                                sentencePairWithNoPersonalIdentifierList.add(sentencePair);
                                codeSwitchSentenceWithNoPersonalIdentifierList.add(csSentence);
                                //System.out.println(csSentence);
                                nonCodeSwitchSentenceWithNoPersonalIdentifierList.add(nonCSSentence);
                                //System.out.println(nonCSSentence);
                            } else {
                                System.out.println("Two lines are not in a pair " + lineId + "," + lastCSLineId);;
                            }
                        }
                        //reset isPersonalIdentifierCS to false
                        isPersonalIdentifierCS = false;
                        //reset isPersonalIdentifierNonCS to false
                        isPersonalIdentifierNonCS = false;
                        //reset
                        isEnglishWordInCSTranslation = false;
                        isEnglishWordInNonCS = false;
                        //reset isControlCS
                        isControlCS = false;
                        //reset originalSentAndTranslation to empty
                        //originalSentAndTranslation = "";
                        //reset csSentence to empty
                        csSentence = "";
                        //reset nonCSSentence to empty
                        nonCSSentence = "";
                        break;
                    default:
                        System.out.println("unknown case: line_" + lineId);
                        break;
                }
                //increment lineId by 1
                lineId++;
            }

            br.close();
            System.out.println("Finished reading " + filename);
            System.out.println("Size of code-switch sentence list: " + codeSwitchSentenceWithNoPersonalIdentifierList.size());
            System.out.println("Size of non-code-switch sentence list: " + nonCodeSwitchSentenceWithNoPersonalIdentifierList.size());
            System.out.println("Size of sentence pair list: " + sentencePairWithNoPersonalIdentifierList.size());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static boolean foundPersonalIdentifier(String sentence) {
        boolean isPersonalIdentifier = false;
        //initialize American phone number detector
        AmericanPhoneNumberDetector phoneDetector = new AmericanPhoneNumberDetector();
        //check if the sentence contains em@135 id
        if (sentence.contains("em@")) {
            isPersonalIdentifier = true;

            //check if the sentence contains phnumber id such as pn-    
        } else if (sentence.contains("pn-")) {
            isPersonalIdentifier = true;

            //check if original sentence or translation contains american phone number    
        } else if (phoneDetector.hasPhoneNumber(sentence)) {
            isPersonalIdentifier = true;
        } else {
            isPersonalIdentifier = false;
        }

        return isPersonalIdentifier;
    }

    //initialize a list that randomly samples n pairs of CS-sent/nonCS-Sent
    private static void initializenRandomlySampledNPairsOfSentences(int n) {
        //initialize nRandomlySampledPairsOfSentenceList to a new LinkedList class
        nRandomlySampledPairsOfSentenceList = new LinkedList<>();
        //initialize nRandomlySampledPairIDToSentencePairMap to a new LinkedHashMap class
        //nRandomlySampledPairIDToSentencePairMap = new LinkedHashMap<>();
        //initialize a list to store n number of integer, range [0, n-1] (n = nPairs)
        LinkedList<Integer> nNumbers = new LinkedList<>();
        //initialize a random
        Random rand = new Random();
        //add n numbers to nNumbers list from 0 to n-1
        for (int i = 0; i < nRandomlySampledPairsOfSentenceList.size(); i++) {
            nNumbers.add(i);
        }
        //Randomly permute the specified list using the specified source ofrandomness. 
        //All permutations occur with equal likelihoodassuming that the source of randomness is fair.
        Collections.shuffle(nNumbers, rand);

        //fill nRandomlySampledPairsOfSentenceList with random indices
        for (int i = 0; i < n; i++) {
            //get a random index in the suffled nNumbers list
            int id = nNumbers.get(i);
            //add a randomly simpled CS-sent/nonCS-sent pair to list
            nRandomlySampledPairsOfSentenceList.add(sentencePairWithNoPersonalIdentifierList.get(id));
            //store a pairID to a CS-sent/nonCS-sent pair
            //nRandomlySampledPairIDToSentencePairMap.put(i, sentencePairWithNoPersonalIdentifierList.get(id));
        }
        System.out.println(nRandomlySampledPairsOfSentenceList.size() + " pairs of sentences have been randomly sampled.");
    }

    //
    //initialize a list that randomly assign the same number of sentence pair list(s) to each of p workers in a way that each pair is seen by r participants
    // also intialize sentencePairLists
    private static void initializeSentencePairLists(int m, int r) {
        //number of workers
        int p = qualifiedWorkerIDList.size();
        //get the size of sampled pairs
        int sampledPairSize = nRandomlySampledPairsOfSentenceList.size();
        //assign sentencePairListsForWorkers to a new list
        //sentencePairListsForWorkers = new LinkedList<>();
        //assign a sentencePairLists to a new list
        sentencePairLists = new LinkedList<>();
        //assign taskOneWorkerIDToSentenceListIDs to a new map
        taskOneWorkerIDToSentenceListIDs = new LinkedHashMap<>();
        //assign taskTwoWorkerIDToSentenceListIDs to a new map
        taskTwoWorkerIDToSentenceListIDs = new LinkedHashMap<>();

        //initialize a list to store a copy of nRandomlySampledPairsOfSentenceList
        LinkedList<String[]> sentencePairSample = new LinkedList<>();
        for (String[] sentencepair : nRandomlySampledPairsOfSentenceList) {
            sentencePairSample.add(sentencepair);
        }
        Random pairRandom = new Random();
        //shuffle the list of sentencePairSample
        Collections.shuffle(sentencePairSample, pairRandom);

        //number of sentence pair list IDs to be assigned to each worker
        numberOfAssignmentPerWorker = (int) Math.ceil(sampledPairSize * r / ((double) m * p));

        System.out.println("Each of " + p + " workers will be assigned to " + numberOfAssignmentPerWorker + " sentence pair list(s).");

        //add (sampledPairSize / m) sentence pair lists
        for (int i = 0; i < sampledPairSize; i += m) {
            LinkedList<String[]> sentencePairList = new LinkedList<>();
            for (int index = i; index < i + m; index++) {
                sentencePairList.add(sentencePairSample.get(index));
            }
            sentencePairLists.add(sentencePairList);
        }
        System.out.println("Size of sentencePairsLists: " + sentencePairLists.size());
        //store the last assigned sentence pair list ID
        int lastAssignedSentencePairListID = -1;
        //assign each worker the same amount of sentence list ids
        for (int i = 0; i < p; i++) {
            String currentWorkerID = qualifiedWorkerIDList.get(i);
            LinkedList<Integer> sentencePairListIDs = new LinkedList<>();
            //System.out.println("worker" + i);
            for (int counter = 0; counter < numberOfAssignmentPerWorker; counter++) {
                //true if the last assigned sentence pair list ID is the last one in sentencePairLists
                if (lastAssignedSentencePairListID == sentencePairLists.size() - 1) {
                    //reset the lastAssignedSentencePairListID to -1
                    lastAssignedSentencePairListID = -1;
                }
                int currentSentencePairListID = lastAssignedSentencePairListID + 1;
                //System.out.println(currentSentencePairListID);
                sentencePairListIDs.add(currentSentencePairListID);
                lastAssignedSentencePairListID = currentSentencePairListID;
            }
            if (sentencePairListIDs.size() != numberOfAssignmentPerWorker) {
                System.out.println("The size of sentencePairListIDs is not equal to the size of numberOfSentencePairListIDsPerWorker, but is: " + sentencePairListIDs.size());
            }
            //assign the current sentence pair list id to the current workerID for task one
            taskOneWorkerIDToSentenceListIDs.put(currentWorkerID, sentencePairListIDs);
        }
        System.out.println("Size of taskOneWorkIDToSentenceListIDs: " + taskOneWorkerIDToSentenceListIDs.size());

        //add the last pair in taskOneWorkerIDToSentenceListIDs as the first pair in taskTwoWorkerIDToSentenceListIDs
        taskTwoWorkerIDToSentenceListIDs.put(qualifiedWorkerIDList.getFirst(), taskOneWorkerIDToSentenceListIDs.get(qualifiedWorkerIDList.getLast()));
        //fill the taskTwoWorkerIDToSentenceListIDs from the second to the last pair)
        for (int i = 0; i < taskOneWorkerIDToSentenceListIDs.size() - 1; i++) {
            taskTwoWorkerIDToSentenceListIDs.put(qualifiedWorkerIDList.get(i + 1), taskOneWorkerIDToSentenceListIDs.get(qualifiedWorkerIDList.get(i)));
        }
        System.out.println("Size of taskTwoWorkIDToSentenceListIDs: " + taskTwoWorkerIDToSentenceListIDs.size());

        System.out.println("First assigned ID in task two: ");
        for (Integer id : taskTwoWorkerIDToSentenceListIDs.get(qualifiedWorkerIDList.getFirst())) {
            System.out.print(id + " ");
        }
        System.out.println();
        System.out.println("Last assigned ID in task one: ");
        for (Integer id : taskOneWorkerIDToSentenceListIDs.get(qualifiedWorkerIDList.getLast())) {
            System.out.print(id + " ");
        }
        System.out.println();
    }

    //fill control sentence Lists and shuffle them, also add csSentenceID csSentenceIDOfControlList
    private static void initializeTaskOneControlSentenceList() {
        csSentenceIDOfControlList = new LinkedList<>();
        taskOneGoodControlSentenceList = new LinkedList<>();
        taskOneBadControlSentenceList = new LinkedList<>();
        //initialize a buffered reader
        BufferedReader br;
        String filename = "task1_controls.csv";
        try {
            //initialize input file path
            File fileDir = new File("data/get_experiment_sentences/input/controls/"
                    + filename);
            //initialize buffered reader br
            //create an input stream read to read bytes and decode them to characters
            //create a file input stream by opening a connection to an actual file and use decode "UTF-8"
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF-8"));

            //initialize a string to store each line in the input file
            String line = "";
            //read until the end of the file is reached
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                String type = columns[1];
                String controlID = "";
                String sentence = "";
                switch (columns.length) {
                    case 4:
                        controlID = columns[0];
                        sentence = columns[3];
                        break;
                    case 6:
                        controlID = columns[0];
                        sentence = columns[3];
                        String csSentenceInfo = columns[4] + "," + columns[5];
                        if(!csSentenceIDOfControlList.contains(csSentenceInfo)) {
                            csSentenceIDOfControlList.add(csSentenceInfo);
                        }                 
                        break;
                    default:
                        System.out.println("Unexpected length for task1 control: " + columns.length);
                        break;
                }
                String controlSentence = controlID + "," + sentence;
                if(type.equals("good")) {
                    taskOneGoodControlSentenceList.add(controlSentence);
                } else if(type.equals("bad")) {
                    taskOneBadControlSentenceList.add(controlSentence);
                } else {
                    System.out.println("Unknow sentence type in task1 control sentence list:" + type);
                }
                
            }
            br.close();

            //shuffle the two list
            Random rand = new Random();
            Collections.shuffle(taskOneGoodControlSentenceList, rand);
            Collections.shuffle(taskOneBadControlSentenceList, rand);
            
            System.out.println("The size of shuffled task1 good control sentence list: " + taskOneGoodControlSentenceList.size());
            System.out.println("The size of shuffled task1 bad control sentence list: " + taskOneBadControlSentenceList.size());
            System.out.println("The size of csSentenceIDOfControlList: " + csSentenceIDOfControlList.size());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //fill control sentence Lists and shuffle them, also add csSentenceIDs to csSentenceIDOfControlList
    private static void initializeTaskTwoControlSentenceList() {
        taskTwoGoodControlSentenceList = new LinkedList<>();
        taskTwoBadControlSentenceList = new LinkedList<>();
        //initialize a buffered reader
        BufferedReader br;
        String filename = "task2_controls.csv";
        try {
            //initialize input file path
            File fileDir = new File("data/get_experiment_sentences/input/controls/"
                    + filename);
            //initialize buffered reader br
            //create an input stream read to read bytes and decode them to characters
            //create a file input stream by opening a connection to an actual file and use decode "UTF-8"
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF-8"));

            //initialize a string to store each line in the input file
            String line = "";
            //read until the end of the file is reached
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");                
                String controlID = columns[0];
                String type = columns[1];
                String sentence = columns[3] + "," + columns[4];
                
                switch (columns.length) {
                    //code-switch sentence control
                    case 7:
                        String csSentenceInfo = columns[5] + "," + columns[6];
                        if (!csSentenceIDOfControlList.contains(csSentenceInfo)) {
                            //System.out.println(csSentenceInfo);
                            csSentenceIDOfControlList.add(csSentenceInfo);
                        }   break;
                    //chinese textbook control.
                    case 5:
                        break;
                    default:
                        System.out.println("Unexpected length for task2 control: " + columns.length);
                        break;
                }
                String controlSentence = controlID + "," + sentence;
                if(type.equals("good")) {
                    taskTwoGoodControlSentenceList.add(controlSentence);
                } else if (type.equals("bad")) {
                    taskTwoBadControlSentenceList.add(controlSentence);
                } else {
                    System.out.println("Unknow sentence type in task2 control sentence list:" + type);
                }

            }
            br.close();

            //shuffle the two list
            Random rand = new Random();
            Collections.shuffle(taskTwoGoodControlSentenceList, rand);
            Collections.shuffle(taskTwoBadControlSentenceList, rand);
            
            
            System.out.println("The size of shuffled task2 good control sentence list: " + taskTwoGoodControlSentenceList.size());
            System.out.println("The size of shuffled task2 bad control sentence list: " + taskTwoBadControlSentenceList.size());
            System.out.println("The size of csSentenceIDOfControlList: " + csSentenceIDOfControlList.size());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //a method randomly insert controls to the second half of the sentence list for task one
    private static void initializeTaskOneSentenceLists(int numberOfAssignments) {
        taskOneSentenceLists = new LinkedList<>();
        LinkedList<LinkedList<String>> subsets = new LinkedList<>();
        //true if taskOneGoodControlSentenceList and taskOneBadControlSentenceList do not have the same size
        if(taskOneGoodControlSentenceList.size() != taskOneBadControlSentenceList.size()) {
            System.err.println("Task1 good and bad control sentence list do not have the same size!");
        }
        int numberOfGoodConrols = (int) Math.ceil(taskOneGoodControlSentenceList.size() / (double) numberOfAssignments);
        System.out.println("Number of good and bad controls of task1 in subset are both: " + numberOfGoodConrols);
        int lastAddedControlSentenceIndex = -1;
        for(int i = 0; i < numberOfAssignments; i++) {
            LinkedList<String> subsetList = new LinkedList<>();
            //check if the last added index is the last index in the control sentence list
            //when encounter out of bound index error, move this if statement inside the for loop below
            if(lastAddedControlSentenceIndex == taskOneGoodControlSentenceList.size() - 1) {
                lastAddedControlSentenceIndex = -1;
            }
            for(int w = 0; w < (numberOfGoodConrols); w++) {
                int currentAssignedControlSentenceIndex = lastAddedControlSentenceIndex + 1;
                subsetList.add(taskOneGoodControlSentenceList.get(currentAssignedControlSentenceIndex));
                subsetList.add(taskOneBadControlSentenceList.get(currentAssignedControlSentenceIndex));
                lastAddedControlSentenceIndex = currentAssignedControlSentenceIndex;
            }
            if(subsetList.size() != numberOfGoodConrols * 2) {
                System.err.println("The subset size of task1 is not " + numberOfGoodConrols * 2 + ", but " + subsetList.size());
            } else {
                subsets.add(subsetList);
            }
        }
        System.out.println("Size of subsets of task1: " + subsets.size());
        int lastAssignedIndexInSubsets = -1;
        for (LinkedList<String[]> pairList : sentencePairLists) {
            
            LinkedList<String> sentenceList = new LinkedList<>();
            for (String[] pair : pairList) {
                if (pair.length != 2) {
                    System.out.println("Pair length is not 2, but " + pair.length);
                }
                //csItems[0] sentence type, csItems[1] sentence id, csItems[2] original sentence, csItems[3] translation        
                String[] csItems = pair[0].split(",");
                String cs = csItems[0] + "_" + csItems[1] + "," + csItems[3];
                //nonCSItems[0] sentence type, nonCSItems[1] sentence id, nonCSItems[2] empty, nonCSItems[3] nonCS-sentence        
                String[] nonCSItems = pair[1].split(",");
                String nonCS = nonCSItems[0] + "_" + nonCSItems[1] + "," + nonCSItems[3];
                //System.out.println("cs: " + cs);
                //System.out.println("noncs: " + nonCS);
                sentenceList.add(cs);
                sentenceList.add(nonCS);
            }
            //System.out.println("Size of sentenceList:" + sentenceList.size());
            //Generate a random
            Random rand = new Random();
            //shuffle sentenceList before insert controls to the second half
            Collections.shuffle(sentenceList, rand);      
            //inclusive insert start point
            int insertStartPoint = (int) Math.floor(sentenceList.size() / (double) 2);
            //exclusive insert end point
            int insertEndPoint = sentenceList.size();
            //initialize a random integer list for to get index for inserting controls
            LinkedList<Integer> randomNumbers = new LinkedList<>();
            for (int i = insertStartPoint; i < insertEndPoint; i++) {
                randomNumbers.add(i);
            }
            //shuffle random number list
            Collections.shuffle(randomNumbers, rand);
            //System.out.println("start insert point at: " + insertStartPoint + " for a list of size: " + sentenceList.size());
//FIXME loop through subset of the list
            //reset lastAssignedIndexInSubsets if it is the last index of subsets
            if(lastAssignedIndexInSubsets == subsets.size() -1) {
                lastAssignedIndexInSubsets = -1;
            }
            int currentAssignedIndexInSubsets = lastAssignedIndexInSubsets + 1;
            lastAssignedIndexInSubsets = currentAssignedIndexInSubsets;
            LinkedList<String> subset = subsets.get(currentAssignedIndexInSubsets);
            //shuffle subset
            Collections.shuffle(subset, rand);
            //check if there are numberOfGoodConrols * 2 controls
            if(subset.size() != numberOfGoodConrols * 2) {
                System.err.println("The size of subset is not " + numberOfGoodConrols * 2);
            }
            for (int i = 0; i < subset.size(); i++) {
                String[] tokens = subset.get(i).split(",");
                String controlSentence = "";
                if (tokens.length == 2) {
                    //tokens[0] controlID, tokens[1] Chinese sentence
                    //System.out.println(tokens[0]);
                    controlSentence = tokens[0] + "," + tokens[1];
                    //System.out.println("control sentence: " + controlSentence);
                } else {
                    System.out.println("Unexpected length");
                }
                sentenceList.add(randomNumbers.get(i), controlSentence);
            }
            //System.out.println("size of task1 sentenceList after controls added: " + sentenceList.size());
            taskOneSentenceLists.add(sentenceList);
        }
        System.out.println("Size of taskOneSentenceLists: " + taskOneSentenceLists.size());
    }

    //a method randomly insert controls to the second half of the sentence list for task two
    private static void initializeTaskTwoSentenceLists(int numberOfAssignments) {
        taskTwoSentenceLists = new LinkedList<>();
        LinkedList<LinkedList<String>> subsets = new LinkedList<>();
        //true if taskTwoGoodControlSentenceList and taskTwoBadControlSentenceList do not have the same size
        if(taskTwoGoodControlSentenceList.size() != taskTwoBadControlSentenceList.size()) {
            System.err.println("Task2 good and bad control sentence list do not have the same size!");
        }
        int numberOfGoodConrols = (int) Math.ceil(taskTwoGoodControlSentenceList.size() / (double) numberOfAssignments);
        System.out.println("Number of good and bad controls of task2 in subset are both: " + numberOfGoodConrols);
        int lastAddedControlSentenceIndex = -1;
        for(int i = 0; i < numberOfAssignments; i++) {
            LinkedList<String> subsetList = new LinkedList<>();
            //check if the last added index is the last index in the control sentence list
            //when encounter out of bound index error, move this if statement inside the for loop below
            if(lastAddedControlSentenceIndex == taskOneGoodControlSentenceList.size() - 1) {
                lastAddedControlSentenceIndex = -1;
            }
            for(int w = 0; w < (numberOfGoodConrols); w++) {
                int currentAssignedControlSentenceIndex = lastAddedControlSentenceIndex + 1;
                subsetList.add(taskTwoGoodControlSentenceList.get(currentAssignedControlSentenceIndex));
                subsetList.add(taskTwoBadControlSentenceList.get(currentAssignedControlSentenceIndex));
                lastAddedControlSentenceIndex = currentAssignedControlSentenceIndex;
            }
            if(subsetList.size() != numberOfGoodConrols * 2) {
                System.err.println("The subset size in task2 is not " + numberOfGoodConrols * 2 + ", but " + subsetList.size());
            } else {
                subsets.add(subsetList);
            }
        }
        System.out.println("Size of subsets in task2: " + subsets.size());
        int lastAssignedIndexInSubsets = -1; 
        for (LinkedList<String[]> pairList : sentencePairLists) {
            LinkedList<String> sentenceList = new LinkedList<>();
            for (String[] pair : pairList) {
                if (pair.length != 2) {
                    System.out.println("Pair length is not 2, but " + pair.length);
                }
                //csItems[0] sentence type, csItems[1] sentence id, csItems[2] original sentence, csItems[3] translation        
                String[] csItems = pair[0].split(",");
                String csSentence = csItems[0] + "_" + csItems[1] + "," + csItems[2] + "," + csItems[3];
                //System.out.println(csSentence);
                sentenceList.add(csSentence);
            }
            //System.out.println("Size of sentenceList:" + sentenceList.size());
            //Generate a random
            Random rand = new Random();
            //shuffle sentenceList before insert controls to the second half
            Collections.shuffle(sentenceList, rand);
            //inclusive insert start point
            int insertStartPoint = (int) Math.floor(sentenceList.size() / (double) 2);
            //exclusive insert end point
            int insertEndPoint = sentenceList.size();
            //initialize a random integer list for to get index for inserting controls
            LinkedList<Integer> randomNumbers = new LinkedList<>();
            for (int i = insertStartPoint; i < insertEndPoint; i++) {
                randomNumbers.add(i);
            }
            //shuffle random number list
            Collections.shuffle(randomNumbers, rand);
            //System.out.println("start insert point at: " + insertStartPoint + " for a list of size: " + sentenceList.size());

            //reset lastAssignedIndexInSubsets if it is the last index of subsets
            if(lastAssignedIndexInSubsets == subsets.size() -1) {
                lastAssignedIndexInSubsets = -1;
            }
            int currentAssignedIndexInSubsets = lastAssignedIndexInSubsets + 1;
            lastAssignedIndexInSubsets = currentAssignedIndexInSubsets;
            LinkedList<String> subset = subsets.get(currentAssignedIndexInSubsets);
            //shuffle subset
            Collections.shuffle(subset, rand);
            //check if there are numberOfGoodConrols * 2 controls
            if(subset.size() != numberOfGoodConrols * 2) {
                System.err.println("The size of subset is not " + numberOfGoodConrols * 2);
            }
            for (int i = 0; i < subset.size(); i++) {
                String[] tokens = subset.get(i).split(",");
                String controlSentence = "";
                //System.out.println(tokens.length);
                if (tokens.length == 3) {
                    //tokens[0] controlID, tokens[2] englsih sentence, tokens[1] Chinese translation
                    //System.out.println(tokens[0]);
                    controlSentence = tokens[0] + "," + tokens[2] + "," + tokens[1];
                    //System.out.println(controlSentence);
                } else {
                    System.err.println("Unexpected length in task2 control: " + tokens.length);
                }
                sentenceList.add(randomNumbers.get(i), controlSentence);
            }
            //System.out.println("size of task2 sentenceList after controls added: " + sentenceList.size());
            taskTwoSentenceLists.add(sentenceList);

        }
        System.out.println("Size of taskTwoSentenceLists: " + taskTwoSentenceLists.size());
    }

    private static void saveTaskOneExperimentCSVFile() {
        final String NEW_LINE_SEPARATOR = "\n";
        final String COMMA_DELIMITER = ",";
        //check if all the size is the same
        boolean isSameSize = false;
        int lastSize = taskOneSentenceLists.get(0).size();
        for (LinkedList<String> sList : taskOneSentenceLists) {
            int currentSize = sList.size();
            if (lastSize != currentSize) {
                isSameSize = false;
            } else {
                isSameSize = true;
            }
            lastSize = currentSize;
        }
        if (isSameSize) {
            System.out.println(taskOneSentenceLists.size() + " participants have the same size of sentence list: " + lastSize);
        }

        String fileHeader = "";
        for (int i = 0; i < lastSize; i++) {
            fileHeader += "sentence_" + i
                    + COMMA_DELIMITER
                    + "sentence_info_" + i;
            //true if it is the last index
            if (i != lastSize - 1) {
                fileHeader += COMMA_DELIMITER;
            } else {
                fileHeader += NEW_LINE_SEPARATOR;
            }
        }
        //CSV file header
        final String FILE_HEADER = fileHeader;

        System.out.println("FILE_HEADER: " + FILE_HEADER);
        try {
            String outputFilePath = "data/get_experiment_sentences/output/task1/"
                    + date
                    + "/";
            int fileCount = 0;
            for (int i = 0; i < taskOneSentenceLists.size(); i++) {
                fileCount++;
                File outputfileName = new File(outputFilePath
                        + date
                        + "_task1_set_"
                        + i
                        + ".csv");
                FileOutputStream is = new FileOutputStream(outputfileName);
                OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
                BufferedWriter w = new BufferedWriter(osw);

                //Write the CSV file header
                w.append(FILE_HEADER);
                //Add a new line separator after the header
                w.append(NEW_LINE_SEPARATOR);

                LinkedList<String> sentenceList = taskOneSentenceLists.get(i);
                for (int sentenceIndex = 0; sentenceIndex < sentenceList.size(); sentenceIndex++) {
                    String[] items = sentenceList.get(sentenceIndex).split(",");
                    //items[0] sentence info, items[1] chinese sentence   
                    if (items.length == 2) {
                        //System.out.println(sentenceList.get(sentenceIndex));
                        w.append(items[1].replaceAll("\\s+", ""));
                        w.append(COMMA_DELIMITER);
                        w.append(items[0]);
                    } else {
                        System.out.println("Unexpected length for the sentence below: " + items.length);
                        System.out.println(sentenceList.get(sentenceIndex));
                    }
                    //true if the current sIndex is the last sentence in the list
                    if (sentenceIndex == sentenceList.size() - 1) {
                        w.append(NEW_LINE_SEPARATOR);
                    } else {
                        w.append(COMMA_DELIMITER);
                    }
                }

                w.flush();
                w.close();

            }
            System.out.println(fileCount + " files have been saved successfully!");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void saveTaskTwoExperimentCSVFile() {
        final String NEW_LINE_SEPARATOR = "\n";
        final String COMMA_DELIMITER = ",";
        //check if all the size is the same
        boolean isSameSize = false;
        int lastSize = taskTwoSentenceLists.get(0).size();
        for (LinkedList<String> sList : taskTwoSentenceLists) {
            int currentSize = sList.size();
            if (lastSize != currentSize) {
                isSameSize = false;
            } else {
                isSameSize = true;
            }
            lastSize = currentSize;
        }
        if (isSameSize) {
            System.out.println(taskTwoSentenceLists.size() + " participants have the same size of sentence list: " + lastSize);
        }

        String fileHeader = "";
        for (int i = 0; i < lastSize; i++) {
            fileHeader += "sentence_" + i
                    + COMMA_DELIMITER
                    + "translation_" + i
                    + COMMA_DELIMITER
                    + "sentence_info_" + i;
            //true if it is the last index
            if (i != lastSize - 1) {
                fileHeader += COMMA_DELIMITER;
            } else {
                fileHeader += NEW_LINE_SEPARATOR;
            }
        }
        //CSV file header
        final String FILE_HEADER = fileHeader;

        System.out.println("FILE_HEADER: " + FILE_HEADER);
        try {
            String outputFilePath = "data/get_experiment_sentences/output/task2/"
                    + date
                    + "/";
            int fileCount = 0;
            for (int i = 0; i < taskTwoSentenceLists.size(); i++) {
                fileCount++;
                File outputfileName = new File(outputFilePath
                        + date
                        + "_task2_set_"
                        + i
                        + ".csv");
                FileOutputStream is = new FileOutputStream(outputfileName);
                OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
                BufferedWriter w = new BufferedWriter(osw);

                //Write the CSV file header
                w.append(FILE_HEADER);
                //Add a new line separator after the header
                w.append(NEW_LINE_SEPARATOR);

                IdentifyWord identifier = new IdentifyWord();

                LinkedList<String> sentenceList = taskTwoSentenceLists.get(i);
                for (int sentenceIndex = 0; sentenceIndex < sentenceList.size(); sentenceIndex++) {
                    String[] items = sentenceList.get(sentenceIndex).split(",");
                    //items[0] sentence info, items[1] english/cs-sentence, items[2] chinese translation        

                    if (items.length == 3) {
                        String csSentenceSpaceRemoved = "";
                        if (!items[0].contains("CONTROL")) {
                            csSentenceSpaceRemoved = items[1].replaceAll("\\s+", "");
                            String[] wordsInCS = items[1].split(" ");
                            LinkedList<LinkedList<String>> csPhraseList = new LinkedList<>();
                            int lastCSIndex = -2;
                            LinkedList<String> csPhrase = new LinkedList<>();
                            for (int wordIndex = 0; wordIndex < wordsInCS.length; wordIndex++) {
                                String currentWord = wordsInCS[wordIndex];
                                if (identifier.checkTheWord(currentWord.toLowerCase()) == 1) {
                                    if (csPhrase.isEmpty() || wordIndex - lastCSIndex == 1) {
                                        csPhrase.add(currentWord);
                                    } else {
                                        if (csPhrase.size() > 1) {
                                            csPhraseList.add(csPhrase);
                                            //System.out.println(csPhrase);
                                        }
                                        csPhrase = new LinkedList<>();
                                        csPhrase.add(currentWord);
                                    }
                                    lastCSIndex = wordIndex;
                                } else {
                                    if (!csPhrase.isEmpty()) {
                                        csPhraseList.add(csPhrase);
                                        csPhrase = new LinkedList<>();
                                    }
                                }
                            }
                            if (!csPhrase.isEmpty()) {
                                csPhraseList.add(csPhrase);
                                csPhrase = new LinkedList<>();
                            }

                            //String csSentenceSpaceRemoved = items[1].replaceAll("\\s+", "");
                            for (LinkedList<String> onePhrase : csPhraseList) {
                                String csPhraseSpaceRemoved = String.join("", onePhrase);
                                String csPhraseWithSpace = String.join(" ", onePhrase);
                                if (csSentenceSpaceRemoved.contains(csPhraseSpaceRemoved)) {
//                                System.out.println(csSentenceSpaceRemoved);
//                                System.out.println(csPhraseWithSpace);
                                    csSentenceSpaceRemoved = csSentenceSpaceRemoved.replace(csPhraseSpaceRemoved, csPhraseWithSpace);
                                }
                            }
                            //System.out.println("cs: " + csSentenceSpaceRemoved);

                        } else {
                            csSentenceSpaceRemoved = items[1];
                            //System.out.println("Control: " + csSentenceSpaceRemoved);
                        }
                        //System.out.println(csSentenceSpaceRemoved);
                        w.append(csSentenceSpaceRemoved);
                        w.append(COMMA_DELIMITER);
                        w.append(items[2].replaceAll("\\s+", ""));
                        w.append(COMMA_DELIMITER);
                        w.append(items[0]);
                    } else {
                        System.out.println("Unexpected length for the sentence below: " + items.length);
                        System.out.println(sentenceList.get(sentenceIndex));
                    }
                    //true if the current sIndex is the last sentence in the list
                    if (sentenceIndex == sentenceList.size() - 1) {
                        w.append(NEW_LINE_SEPARATOR);
                    } else {
                        w.append(COMMA_DELIMITER);
                    }
                }
                w.flush();
                w.close();
            }
            System.out.println(fileCount + " files have been saved successfully!");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateTranslationExperimentInputCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
