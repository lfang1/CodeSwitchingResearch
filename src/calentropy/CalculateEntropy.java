/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calentropy;

import codeswitchresearch.CodeSwitchedSentence;
import codeswitchresearch.SurprisalsOfASentence;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import static java.util.Map.Entry.comparingByKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;

/**
 *
 * @author Le
 */
public class CalculateEntropy {

    private static LinkedList<CodeSwitchedSentence> csSentences = new LinkedList<>();
    private static LinkedList<SurprisalsOfASentence> surprisalList = new LinkedList<>();
    //private static String inputFileFolderPath = "D:/App/Dropbox/Fred/research/short_paper/add_entropy/output/55k_one_word_after_cs_point_surprisal/11152019/";
    private static String inputFileFolderPath = "D:/App/Dropbox/Fred/research/short_paper/add_entropy/output/55k/11152019/";
    private static LinkedHashMap<Integer, Double> lineIDToEntropy = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, Double> sortedLineIDToEntropy = new LinkedHashMap<>();

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        readSurprisalCSVFile();
        //Remember to add entropy input for line_0 csv file before entropy calcuation. line_0 csv file was missed in both 55k and 55k_one_ward_after_cs_point_surprisal
        saveToCSVFile();
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

        try {
            //double maxSurprisal = 0.0;
            
            //Reading all the csv file in inputFileFolderPath
            //Before run this file,  please update the path of the csv file
            List<File> filesInFolder = Files.walk(Paths.get(inputFileFolderPath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            for (File filename : filesInFolder) {
                
                br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(filename), "UTF8"));

                //System.out.println("Start reading file: " + filename.getName());
                String[] pathParts = filename.toString().split("_");
                int lineID = Integer.parseInt(pathParts[pathParts.length - 2]);
                //System.out.println("Reading 55k sentences for lineID: " + lineID);
                int sentenceId = 0;
                ArrayList<String> wordsInSentence = new ArrayList<>();
                ArrayList<Double> surprisalsInSentence = new ArrayList<>();

                String line = "";
                //Reading from the first line

                //Double[] surprisalArray = new Double[55000];
                Double[] entropyArray = new Double[55000];
                double entropySum = 0.0;
                while ((line = br.readLine()) != null) {
                    //System.out.println(line);

                    //Break when the last second row is reached, the summary of the surprisals 
                    if (Pattern.matches("^file\\s.*", line)) {
                        //System.out.println("Reached the second last row in the file");
                        break;
                    }

                    String[] sentenceDetails = line.split(COMMA_DELIMITER);

                    //If the length is 1 when it is empty
                    //Use if(!line.isEmpty()) or sentenceDetails.length != 0
                    if (!line.isEmpty()) {
                    //System.out.println(sentenceDetails.length);
                    //System.out.println(line);
                        switch (sentenceDetails.length) {
                            case 1:
                                //System.out.println("sentenceDetails[0]: " + sentenceDetails[0]);
                                Matcher pm = propPattern.matcher(sentenceDetails[0]);
                                //true, if an indidual word probability is found
                                //false, if it is a sentence
                                if (pm.find()) {
                                    Matcher sm = surprisalPattern.matcher(sentenceDetails[0]);
                                    String wString = pm.group();
                                    String[] ws = wString.split("\\s");
                                    String word = ws[2].trim();
                                    //System.out.println(word);
                                    wordsInSentence.add(word);
                                    //true, if a suprisal is found
                                    if (sentenceDetails[0].contains("[OOV] 0 [ -inf ]") || sentenceDetails[0].contains("0 [ -inf ]")) {
                                        //double surprisal = Double.POSITIVE_INFINITY;
                                        //assign to the highest surprisal 10.16642 in input_R_v2
                                        //lowest prob 6.81679133e-11
                                        //The max surprisal: 11.48991 within 2449 sentences
                                        //double surprisal = 10.16642;
                                        double surprisal = 11.48991;
                                        surprisalsInSentence.add(surprisal);

                                    } else if (sm.find()) {
                                        String sString = sm.group();
                                        sString = sString.substring(1, sString.length() - 2).trim();
                                        double surprisal = -Double.parseDouble(sString);
                                        surprisalsInSentence.add(surprisal);
                                    } else {
                                        System.err.println(sentenceDetails[0]);
                                    }
                                }
                                break;
                            case 2:
                                // true, if it contains information of the zeroprobs, logprob, ppl, ppl1 of the whole sentence
                                //it looks like:
                                //0 zeroprobs	 logprob= -30.82968 ppl= 2663.914 ppl1= 7140.197

                                //Check if wordsInSentence arraylist is empty
                                if (wordsInSentence.isEmpty()) {
                                    System.err.println("SentenceId: " + sentenceId + "wordsInSentence is empty!");
                                    return;
                                }   //Check if the wordsInSentence has the same size with with surprisalInSentence
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
                                
                                double csSurprisal = surprisalsInSentence.get(surprisalsInSentence.size()-2);
                                
//                                if(csSurprisal - maxSurprisal > 0.0) {
//                                    maxSurprisal = csSurprisal;
//                                }
                                //uncomment the following three lines of code after finding the maxing csSurprisal
                                double entropy = csSurprisal * Math.pow(10, -csSurprisal);
                                entropyArray[sentenceId] = entropy;
                                entropySum += entropy;
                                wordsInSentence = new ArrayList<>();
                                surprisalsInSentence = new ArrayList<>();
                                break;
                            case 3:
                                //true if this row contian details of the sentences
                                //It looks like:
                                //1 sentences	 8 words	 0 OOVs
                                break;
                            default:
                                break;
                        }

                    } else {
                        sentenceId++;
                    }
                }
                //System.out.println("There are " + sentenceId + " sentences");
                if(sentenceId < 54999) {
                    int numOfSen = sentenceId + 1;
                    System.err.println("line" + lineID + " has only " + numOfSen + " sentences");
                }
                lineIDToEntropy.put(lineID, entropySum);
            }
            System.out.println("There are " + lineIDToEntropy.size() + " lines before adding missing lines");
            
            
            //Comment this for block when calculating for entropy of one word after cs-point
            //If a lineIDToEntropy does not contain such line id, add the miising line id and assign entropy value of line 0
            //Remember to: add line_0 output csv file that was missed before in the folder 
            //2952 is the amount of sentence in 11152019_location_appended_input_R_v4_new_15_11_2019.csv
            for(int i = 0; i < 2952; i ++) {
                if(!lineIDToEntropy.containsKey(i)) {
                    //the current line has a cs word that is the first word of the sentence, which is same as line 0 sentence
                    lineIDToEntropy.put(i, lineIDToEntropy.get(0));
                }
            }
            
            //FIXME: check if there is no line missing. If no line missing, then it is right.
            System.out.println("check if there is no line missing");
            //check which line is missing
            for(int i = 0; i < 2952; i ++) {
                if(!lineIDToEntropy.containsKey(i)) {
                    System.out.println("line " + i + " is missing.");
                }
            }
            
            sortedLineIDToEntropy = lineIDToEntropy
                    .entrySet()
                    .stream()
                    .sorted(comparingByKey())
                    .collect(
                            toMap(e -> e.getKey(), e -> e.getValue(),
                            (e1, e2) -> e2, LinkedHashMap::new));
            
            System.out.println("There are " + sortedLineIDToEntropy.size() + " sorted lines");           
            //System.out.println("The max surprisal: " + maxSurprisal);
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

    public static void saveToCSVFile()
            throws FileNotFoundException, UnsupportedEncodingException {

        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header
        final String FILE_HEADER = "LineID,"
                + "Entropy";

        System.out.println("FILE_HEADER: " + FILE_HEADER);
        //String outputFilename = "D:/App/Dropbox/Fred/research/short_paper/add_entropy/output/entropy_result/11152019_55k_one_word_after_cs_point_entropy.csv";
        String outputFilename = "D:/App/Dropbox/Fred/research/short_paper/add_entropy/output/entropy_result/11152019_55k_new_phrases_entropy.csv";

        try {
            File outputfileName = new File(outputFilename);
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);

            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            sortedLineIDToEntropy.forEach((k, v) -> {
                try {
                    w.append(String.valueOf(k));
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(v));
                    w.append(NEW_LINE_SEPARATOR);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            });

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + outputFilename);
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
