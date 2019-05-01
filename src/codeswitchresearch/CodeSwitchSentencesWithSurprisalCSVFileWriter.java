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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Le
 */
public class CodeSwitchSentencesWithSurprisalCSVFileWriter {

    private static LinkedList<CodeSwitchedSentence> csSentences = new LinkedList<>();
    private static LinkedHashMap<Integer, SurprisalsOfASentence> surprisalMap = new LinkedHashMap<>();
    //psucssa
    //cmucssa
    //pittcssa
    private static String corpusName = "pittcssa";

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        readSurprisalCSVFile();
        saveToCSVFile(corpusName, surprisalMap);
        //test();
    }

    public static void readSurprisalCSVFile() {
        BufferedReader br = null;
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";
        final String SENTENCE_START = "<s>";
        final String SENTENCE_END = "</s>";

        //If "" surround the sentenceDetails[0], use this pattern
        Pattern propPattern = Pattern.compile("^\"\\sp\\((.*|.*)\\)");
        Pattern surprisalPattern = Pattern.compile("\\[\\s-?\\d+\\.?\\d*\\s\\]\"$");
        
        //If not, use this pattern
        //Pattern propPattern = Pattern.compile("^\\sp\\((.*|.*)\\)");
        //Pattern surprisalPattern = Pattern.compile("\\[\\s-?\\d+\\.?\\d*\\s\\]$");

        try {
            //Reading the csv file
            //Before run this file,  please update the path of the csv file
            File fileDir = new File("data/surprisal/"
                    + "11112018-"
                    + corpusName
                    + "-surprisal.csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            int sentenceId = 0;
            ArrayList<String> wordsInSentence = new ArrayList<>();
            ArrayList<Double> surprisalsInSentence = new ArrayList<>();
            int numberOfSentences = -1;
            int numberOfWords = -1;
            int numberOfOOVs = -1;
            double zeroprobs = -1.0;
            double logprob = -1.0;
            double ppl = -1.0;
            double ppl1 = -1.0;

            String line = "";
            //Reading from the first line
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                /*
                //Break when the last second row is reached, the summary of the surprisals 
                if(Pattern.matches("^file\\s.*", line)) {
                	System.out.println("Reached the second last row in the file");
                	break;
                }
                 */
                String[] sentenceDetails = line.split(COMMA_DELIMITER);
                //true, if it is an empty row
                if (sentenceDetails.length == 0) {
                    //System.out.println("new sentence below:");
                } else if (sentenceDetails.length == 1) {
                    //System.out.println("sentenceDetails[0]: " + sentenceDetails[0]);
                    Matcher pm = propPattern.matcher(sentenceDetails[0]);

                    //true, if an indidual word probability is found
                    if (pm.find()) {
                        Matcher sm = surprisalPattern.matcher(sentenceDetails[0]);
                        String wString = pm.group();
                        String[] ws = wString.split("\\s");
                        String word = ws[2];
                        wordsInSentence.add(word);
                        //true, if a suprisal is found
                        if (sentenceDetails[0].contains("[OOV] 0 [ -inf ]")) {
                            double surprisal = Double.NEGATIVE_INFINITY;
                            surprisalsInSentence.add(surprisal);

                        } else if (sm.find()) {
                            String sString = sm.group();
                            sString = sString.substring(1, sString.length() - 2).trim();
                            double surprisal = Double.parseDouble(sString);
                            surprisalsInSentence.add(surprisal);
                        } else {
                            System.out.println(sentenceDetails[0]);
                        }
                    }
                } else if (sentenceDetails.length == 2) {
                    // true, if it contains information of the zeroprobs, logprob, ppl, ppl1 of the whole sentence
                    //it looks like:
                    //0 zeroprobs	 logprob= -30.82968 ppl= 2663.914 ppl1= 7140.197

                    //Check if wordsInSentence arraylist is empty
                    if (wordsInSentence.isEmpty()) {
                        System.err.println("SentenceId: " + sentenceId + "wordsInSentence is empty!");
                        return;
                    }
                    //Check if the wordsInSentence has the same size with with surprisalInSentence
                    if (wordsInSentence.size() != surprisalsInSentence.size()) {
                        System.err.println("In sentenceId:" + sentenceId);
                        System.err.println("The two array list is not the same size");
                        System.err.println("wordsInSentence size: " + wordsInSentence.size());
                        int wwCount = 0;
                        for (String ww : wordsInSentence) {
                            wwCount++;
                            System.err.println("word" + wwCount + ": " + ww);
                        }
                        System.err.println("surprisalsInSentence size: " + surprisalsInSentence.size());
                        int ssCount = 0;
                        for (Double ss : surprisalsInSentence) {
                            ssCount++;
                            System.err.println("surprisal" + ssCount + ": " + ss);
                        }
                        return;
                    }
                    SurprisalsOfASentence soas = new SurprisalsOfASentence(
                            sentenceId, wordsInSentence, surprisalsInSentence,
                            numberOfSentences, numberOfWords, numberOfOOVs);
                    surprisalMap.put(sentenceId, soas);
                    sentenceId++;
                    wordsInSentence = new ArrayList<>();
                    surprisalsInSentence = new ArrayList<>();
                    numberOfSentences = -1;
                    numberOfWords = -1;
                    numberOfOOVs = -1;
                } else if (sentenceDetails.length == 3) {
                    //true if this row contian details of the sentences
                    //It looks like:
                    //1 sentences	 8 words	 0 OOVs
                    try {                       
                        numberOfSentences = Integer.parseInt(sentenceDetails[0].replace("sentences", "").trim());
                        numberOfWords = Integer.parseInt(sentenceDetails[1].replace("words", "").trim());
                        numberOfOOVs = Integer.parseInt(sentenceDetails[2].replace("OOVs", "").trim());
                    } catch (NumberFormatException nfe){
                        nfe.printStackTrace();
                        return;
                    }

                }
            }

            //Lets print the SurprisalMap
//            surprisalMap.forEach((k,v) -> {
//                System.out.println("Sentence" + k + ": ");
//                System.out.println("surprisals of words: " + v.getSurprisalsInSentence().toString());
//            });
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ie) {
                System.out.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    }

    public static void saveToCSVFile(String corpusName, LinkedHashMap<Integer, SurprisalsOfASentence> surprisalMap)
            throws FileNotFoundException, UnsupportedEncodingException {

        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header
        String sixtySurprisals = "";
        String sixtyWords = "";
        for (int i = 1; i < 61; i++) {
            sixtySurprisals = sixtySurprisals.concat("Surprisal" + i + ",");
            sixtyWords = sixtyWords.concat("Word" + i + ",");
        }

        final String FILE_HEADER = "SentenceID,"
                + "CodeSwitchStatus,"
                + "NumberOfWords,"
                + sixtySurprisals
                + sixtyWords
                + "CorpusName";

        System.out.println("FILE_HEADER: " + FILE_HEADER);

        try {
            File outputfileName = new File("data/surprisal/"
                    + "11112018-"
                    + "formated-"
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
            surprisalMap.forEach((k, v) -> {
                try {
                    w.append(String.valueOf(k));
                    w.append(COMMA_DELIMITER);
                    w.append("1");
                    w.append(COMMA_DELIMITER);
                    int numberOfWords = v.getNumberOfWords();
                    w.append(String.valueOf(numberOfWords));
                    w.append(COMMA_DELIMITER);
                    ArrayList<Double> surprisalsInSentence = v.getSurprisalsInSentence();
                    if (numberOfWords + 1 != surprisalsInSentence.size()) {
                        System.err.println("The the number of surprisals - 1 is not equal with the number of the words!");
                        System.err.println("The number of words: " + numberOfWords);
                        System.err.println("The number of surprisals: " + surprisalsInSentence.size());
                    }

                    for (Double d : surprisalsInSentence) {
                        w.append(String.valueOf(d));
                        w.append(COMMA_DELIMITER);
                    }
                    for (int i = 0; i < (60 - surprisalsInSentence.size()); i++) {
                        w.append("");
                        w.append(COMMA_DELIMITER);
                    }
                    ArrayList<String> wordsInSentence = v.getWordsInSentence();
                    for (String word : wordsInSentence) {
                        w.append(word);
                        w.append(COMMA_DELIMITER);
                    }
                    for (int i = 0; i < (60 - wordsInSentence.size()); i++) {
                        w.append("");
                        w.append(COMMA_DELIMITER);
                    }
                    w.append(corpusName);

                    w.append(NEW_LINE_SEPARATOR);

                } catch (IOException ex) {
                    Logger.getLogger(CodeSwitchSentencesWithSurprisalCSVFileWriter.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            });

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + "data/surprisal/"
                    + "11112018-formated"
                    + corpusName
                    + ".csv");
        }
    }

    public static void test() {
        //String testString="一 个 较 大号 ， 一 个 标准 大小 。";
        String testString = "p( 比邻 | <s> ) 	= [2gram] 2.970071e-07 [ -6.527233 ]";
        Pattern propPattern = Pattern.compile("^p\\((.*|.*)\\)");
        Matcher pm = propPattern.matcher(testString);

        if (pm.find()) {
            System.out.println(pm.group());
        } else {
            System.out.println("nothing found");
        }

        /*
    	    newSentence=True
    	    while(lines in file):
    	   		if newSentence and newSentence not matches prob patterns:
    	   		     newSentence=False
    	   		     Double[] surprisalValues= new...
    	   		     String[] stringValues = new...
    	   		     continue
    	   		 else if line matches probpattern:
    	   		
    	   		
    	   		while not empty line:
    	   		
    	   
    	   
    	  
         */
    }

}
