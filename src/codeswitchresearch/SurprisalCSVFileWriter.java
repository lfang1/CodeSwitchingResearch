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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Le
 */
public class SurprisalCSVFileWriter {
    private static LinkedList<CodeSwitchedSentence> csSentences = new LinkedList<>();
    private static LinkedList<SurprisalsOfASentence>  surprisalList = new LinkedList<>();
    private static String filename = "non-cs/10312019_noncs_ctb_segmentation_fixed_surprisal";
    
    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        readSurprisalCSVFile();
        saveToCSVFile();
        //test();
    }

    public static void readSurprisalCSVFile() {
        BufferedReader br = null;
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";
        final String SENTENCE_START = "<s>";
        final String SENTENCE_END = "</s>";

        //If "" surround the sentenceDetails[0], use this pattern
        //Pattern propPattern = Pattern.compile("^\"\\sp\\((.*|.*)\\)");
        //Pattern surprisalPattern = Pattern.compile("\\[\\s-?\\d+\\.?\\d*\\s\\]\"$");
        //If not, use this pattern
        Pattern propPattern = Pattern.compile("^\\sp\\((.*|.*)\\)");
        Pattern surprisalPattern = Pattern.compile("\\[\\s-?\\d+\\.?\\d*\\s\\]$");
        int lastSentenceId = -1;
        try {
            //Reading the csv file
            //Before run this file,  please update the path of the csv file
            File fileDir = new File("data/surprisal/input/"
                    + filename
                    + ".csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            int sentenceId = 0;
            String source = "";
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

            boolean isSentence = false;
            boolean isSentenceID = true;
            int sentenceCounter = 0;

            while ((line = br.readLine()) != null) {
                //System.out.println(line);

                //Break when the last second row is reached, the summary of the surprisals 
                if (Pattern.matches("^file\\s.*", line)) {
                    System.out.println("Reached the second last row in the file");
                    break;
                }

                String[] sentenceDetails = line.split(COMMA_DELIMITER);

                //true, if it is an empty row
                //If then length is 1 when it is empty
                //Use if(!line.isEmpty()) or sentenceDetails.length != 0
                if (!line.isEmpty()) {
//                    System.out.println(sentenceDetails.length);
//                    System.out.println(line);
                    if (isSentence) {
                        if (sentenceDetails.length == 1) {
                            //System.out.println("sentenceDetails[0]: " + sentenceDetails[0]);
                            Matcher pm = propPattern.matcher(sentenceDetails[0]);

                            //true, if an indidual word probability is found
                            if (pm.find()) {
                                Matcher sm = surprisalPattern.matcher(sentenceDetails[0]);
                                String wString = pm.group();
                                String[] ws = wString.split("\\s");
                                String word = ws[2].trim();
                                //System.out.println(word);
                                wordsInSentence.add(word);
                                //true, if a suprisal is found
                                if (sentenceDetails[0].contains("[OOV] 0 [ -inf ]")) {
                                    double surprisal = Double.POSITIVE_INFINITY;
                                    surprisalsInSentence.add(surprisal);

                                } else if (sm.find()) {
                                    String sString = sm.group();
                                    sString = sString.substring(1, sString.length() - 2).trim();
                                    double surprisal = -Double.parseDouble(sString);
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
                            SurprisalsOfASentence soas = new SurprisalsOfASentence(source,
                                    sentenceId, wordsInSentence, surprisalsInSentence,
                                    numberOfSentences, numberOfWords, numberOfOOVs);
                            surprisalList.add(soas);
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
                            } catch (NumberFormatException nfe) {
                                nfe.printStackTrace();
                                return;
                            }

                        }
                    } else {
                        if (isSentenceID) {
                            String[] tokens = sentenceDetails[0].split("_");
                            source = tokens[0];
                            sentenceId = Integer.parseInt(tokens[1]);
                            //System.out.println(tokens[0] + ": " + tokens[1]);
                            sentenceCounter++;
                            isSentenceID = false;
                            lastSentenceId = sentenceId;
                        } 
                    }
                } else {
                   
                    if (isSentence) {
                        isSentence = false;

                    } else {
                        isSentence = true;
                        
                        if(!isSentenceID) {
                            isSentenceID = true;
                        }
                    }
                }

            }

            //Lets print the SurprisalMap
//            surprisalMap.forEach((k,v) -> {
//                System.out.println("Sentence" + k + ": ");
//                System.out.println("surprisals of words: " + v.getSurprisalsInSentence().toString());
//            });
        } catch (Exception ee) {
            System.out.println(lastSentenceId);
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

    public static void saveToCSVFile()
            throws FileNotFoundException, UnsupportedEncodingException {

        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header
        final String FILE_HEADER = "Word Index,"
                + "Word Form,"
                + "Surprisal";

        System.out.println("FILE_HEADER: " + FILE_HEADER);

        try {
            File outputfileName = new File("data/surprisal/output/"
                    + filename
                    + "_ngrams"
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
            surprisalList.forEach((v) -> {
                try {
                    w.append(v.getSource());
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(v.getSentenceId()));
                    w.append(NEW_LINE_SEPARATOR);
                    for(int i = 0; i < v.getNumberOfWords(); i++) {
                        w.append(String.valueOf(i));
                        w.append(COMMA_DELIMITER);
                        w.append(v.getWordsInSentence().get(i));
                        w.append(COMMA_DELIMITER);
                        w.append(String.valueOf(v.getSurprisalsInSentence().get(i)));
                        w.append(NEW_LINE_SEPARATOR);
                    }
                    w.append(NEW_LINE_SEPARATOR);
                } catch (IOException ex) {
                    Logger.getLogger(SurprisalCSVFileWriter.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            });

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + "data/surprisal/output/non-cs/"
                    + filename
                    + "_ngrams"
                    + ".csv");
        }
    }

    public static void test() {
        //String testString="一 个 较 大号 ， 一 个 标准 大小 。";
        String testString = "p( 管理 | <s> ) 	= [2gram] 2.970071e-07 [ -6.527233 ]";
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
