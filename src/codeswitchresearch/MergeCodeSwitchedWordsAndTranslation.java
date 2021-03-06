/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import infodetector.PunctuationDetector;
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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class MergeCodeSwitchedWordsAndTranslation {

    private static LinkedList<CodeSwitchedSentence> csSentences = new LinkedList<>();
//FIXME: replace all code switched sentence to chinese to get the translatedSentence;
//    private static LinkedList<String> translatedSentences = new LinkedList<>();
    //psucssa
    //cmucssa
    //pittcssa
    private static String corpusName = "cmucssa";

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        readTranslationCSVFile();
        //saveToCSVFile(corpusName, csSentences);
    }

    public static void readTranslationCSVFile() {
        BufferedReader br = null;
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";
        IdentifyWord identifier = new IdentifyWord();

        try {
            //Reading the csv file
            File fileDir = new File("data/translation-for-merge/10292018-"
                    + corpusName
                    + "-for-merge.csv");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String line = "";
            //Read to skip the header
            br.readLine();
            //Reading from the second line
            while ((line = br.readLine()) != null) {
                String[] sentenceDetails = line.split(COMMA_DELIMITER);

                if (sentenceDetails.length > 0) {
                    if(sentenceDetails.length !=14) {
                        System.err.println("The sentenceDetails length is not 14, but " + sentenceDetails.length);
                        for(String s : sentenceDetails) {
                            System.out.println(s);
                        }
                    }
                    int sentenceId = Integer.parseInt(sentenceDetails[0]);
                    String unmarkedWordsInSentence = sentenceDetails[1];
                    unmarkedWordsInSentence = unmarkedWordsInSentence.trim();
                    ArrayList<String> wordsInSentence = new ArrayList<>();
                    for (String word : unmarkedWordsInSentence.split("\\s+")) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        wordsInSentence.add(word);
                    }
                    int numberOfCodeSwitching = -1;
                    try {
                        numberOfCodeSwitching = Integer.parseInt(sentenceDetails[2]);
                    } catch (NumberFormatException ne) {
                        System.err.println("In sentence " + sentenceId + ": The sentenceDetails[2]:" + sentenceDetails[2]);
                        ne.printStackTrace();
                    }

                                                             
                    ArrayList<Integer> indicesOfCodeSwitchedWord = new ArrayList<>();
                    ArrayList<Integer> indicesOfPunctuation = new ArrayList<>();
                    ArrayList<String> addedWordList = new ArrayList<>();
                    ArrayList<String> newWordsInSentence = new ArrayList<>();
                    int lastCodeSwitchIndex=-1;
                   
                    for (String word : wordsInSentence) {
                        int type = identifier.identifyTheWord(word);
                        int i = newWordsInSentence.size();   
                        //System.out.println(i);
                        switch (type) {
                        	//Chinese word found
                            case 0: newWordsInSentence.add(word);
                                    break;
                            //English word found
                            case 1: indicesOfCodeSwitchedWord.add(i); 
                                    newWordsInSentence.add(word);
                                    lastCodeSwitchIndex=i;
                                    break;
                            //Unknown word with no punctuation inside found;
                            case 2: if(i-lastCodeSwitchIndex==1) {  //If the previous word is CS word, then this is also considered English
                            		indicesOfCodeSwitchedWord.add(i);
                            		lastCodeSwitchIndex=i;
                            		}
                            		newWordsInSentence.add(word);
                            		break;
                            //Single punctuation or email id or phone number id
                            //Note: all ","(English comma) is replaced by "，" (Chinese comma) for using csv format
                            case 3: case 4: if(i-lastCodeSwitchIndex==1) {  //If the previous word is CS word, then this is also considered English
            	            		indicesOfCodeSwitchedWord.add(i);
            	            		lastCodeSwitchIndex=i;
            	            		}
                                    indicesOfPunctuation.add(i);
                                    newWordsInSentence.add(word);
                                    break;
                            //2 chunks: first is Chinese, second is punctuation
                            case 5: String[] twoChunks=PunctuationDetector.splitWordByPunctuation(word);         
                                    addedWordList.add(twoChunks[0]);
                                    newWordsInSentence.add(twoChunks[0]);
                                    newWordsInSentence.add(twoChunks[1]);
                                    indicesOfPunctuation.add(i+1);
                                    break;
                            //2 chunks: first is English, second is punctuation
                            case 6: String[] twoChunksE=PunctuationDetector.splitWordByPunctuation(word);         
                            		addedWordList.add(twoChunksE[0]);
                            		newWordsInSentence.add(twoChunksE[0]);
                            		newWordsInSentence.add(twoChunksE[1]);
                            		indicesOfCodeSwitchedWord.add(i);
                            		indicesOfCodeSwitchedWord.add(i+1);
                                    indicesOfPunctuation.add(i+1);
                                    lastCodeSwitchIndex=i+1;
                                    break;
                            //2 chunks: first is digits or Unknown word, second is punctuation     
                            case 7: case 8: String[] twoChunksD=PunctuationDetector.splitWordByPunctuation(word);   
            	                	addedWordList.add(twoChunksD[0]);
            	            		newWordsInSentence.add(twoChunksD[0]);
            	            		newWordsInSentence.add(twoChunksD[1]);
            	            		indicesOfPunctuation.add(i+1);
                            		if(i-lastCodeSwitchIndex==1) {  //If the previous word is CS word, then this is also considered English
                            			indicesOfCodeSwitchedWord.add(i);
                                		indicesOfCodeSwitchedWord.add(i+1);
                                		lastCodeSwitchIndex=i+1;
                        			}
                                    break;
                            //Unknown word with only hyphens in between
                            case 9: indicesOfCodeSwitchedWord.add(i); 
            		                newWordsInSentence.add(word);
            		                lastCodeSwitchIndex=i;
            		                break;
            		    //At least one punctuation within the unknown word, and the word ends with "."
                            case 10: String unkownWord = word.substring(0, word.length()-1);            
            		            	addedWordList.add(unkownWord);
            		        		newWordsInSentence.add(unkownWord);
            		        		newWordsInSentence.add(".");
            		        		indicesOfPunctuation.add(i+1);
            		        		if(i-lastCodeSwitchIndex==1) {  //If the previous word is CS word, then this is also considered English
            		        			indicesOfCodeSwitchedWord.add(i);
            		            		indicesOfCodeSwitchedWord.add(i+1);
            		            		lastCodeSwitchIndex=i+1;
            		    			}
            		                break;
                            //At least one punctuation within the unknown word,and the word does not end with "."
                            case 11: if(i-lastCodeSwitchIndex==1) {  //If the previous word is CS word, then this is also considered English
                            		indicesOfCodeSwitchedWord.add(i);
                            		lastCodeSwitchIndex=i;
                            		}
                            		newWordsInSentence.add(word);
                            		break;                    
                            default:newWordsInSentence.add(word);
                                    System.err.println("Unknown word in default type: " + word);
                                    break;                          
                        }                                              
                    }
                
 /*               
                if(newWordsInSentence.size() !=  wordsInSentence.size()) {
                    System.out.println("SentenceId: " + sentenceId);
                    System.out.println("old sentence has been updated!");
                    String oldSentence = wordsInSentence.toString();
                    oldSentence = oldSentence.replace(",", "");
                    oldSentence = oldSentence.substring(1, oldSentence.length()-1);
                    System.out.println("old sentence: " + oldSentence);
                    String newSentence = newWordsInSentence.toString();
                    newSentence = newSentence.replace(",", "");
                    newSentence = newSentence.substring(1, newSentence.length()-1);
                    System.out.println("new sentence: " + newSentence);  
                }               
*/
                    ArrayList<CodeSwitchPair> codeSwitchPairsList = new ArrayList<>();
                    for (int i = 3; i < 12; i += 2) {
                        int codeSwitchNumber = i / 2;                       
                        String codeSwitch = sentenceDetails[i];
                        String translation = sentenceDetails[i + 1];
                        CodeSwitchPair pair = new CodeSwitchPair(codeSwitchNumber, codeSwitch, translation);
                        codeSwitchPairsList.add(pair);
                        if(codeSwitchNumber > numberOfCodeSwitching) {
                            break;
                        }
                    }
                    String translatedSentence = sentenceDetails[13].trim();

                    //Save the employee details in Employee object
                    CodeSwitchedSentence s = new CodeSwitchedSentence(sentenceId,
                            newWordsInSentence,
                            numberOfCodeSwitching,
                            indicesOfCodeSwitchedWord,
                            indicesOfPunctuation,
                            codeSwitchPairsList,
                            translatedSentence,
                            addedWordList);
                    csSentences.add(s);
                } else {
                    System.err.println("An error occur at sentence" + sentenceDetails[0] + ", the sentenceDetails length is " + sentenceDetails.length);
                }
            }
/*            
            //Lets print the CodeSwitchedSentence list
            for (CodeSwitchedSentence s : csSentences) {
                System.out.println(s.getSentenceId() + "   " + s.getUnmarkedWordsInSentence());
                for(CodeSwitchPair pair : s.getMergedCodeSwitchPairsList()) {
                    System.out.println("Pair" + pair.getPairId() + ": " + pair.getCodeSwitch() + " -> " + pair.getTranslation());
                }
            }
*/            
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

    public static void saveToCSVFile(String corpusName, List<CodeSwitchedSentence> cssList)
            throws FileNotFoundException, UnsupportedEncodingException {

        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header

        final String FILE_HEADER = "SentenceID,"
                + "CodeSwitchedSentence,"
                + "NumberOfCodeSwitching,"
                + "CodeSwitch1,"
                + "Translation1,"
                + "CodeSwitch2,"
                + "Translation2,"
                + "CodeSwitch3,"
                + "Translation3,"
                + "CodeSwitch4,"
                + "Translation4,"
                + "CodeSwitch5,"
                + "Translation5,"
                + "CodeSwitch6,"
                + "Translation6,"
                + "CodeSwitch7,"
                + "Translation7,"
                + "TranslatedSentence,"
                + "IndicesOfCodeSwitchedWord,"
                + "IndicesOfPunctuation,"
                + "AddedWordList";

        try {
            File outputfileName = new File("data/merged-translation/"
                    + "10302018-"
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

            for (CodeSwitchedSentence css : cssList) {
                try {
                    w.append(String.valueOf(css.getSentenceId()));
                    w.append(COMMA_DELIMITER);
                    w.append(css.getUnmarkedWordsInSentence());
                    w.append(COMMA_DELIMITER);
                    int codeSwitchingOccurTimes = css.getMergedCodeSwitchPairsList().size();
                    w.append(String.valueOf(codeSwitchingOccurTimes));
                    w.append(COMMA_DELIMITER);
                    css.getMergedCodeSwitchPairsList().forEach(codeSwichPair -> {
                        try {
                            w.append(codeSwichPair.getCodeSwitch());
                            w.append(COMMA_DELIMITER);
                            w.append(codeSwichPair.getTranslation());
                            w.append(COMMA_DELIMITER);
                        } catch (IOException ex) {                       
                            Logger.getLogger(MergeCodeSwitchedWordsAndTranslation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    for (int i = 0; i < (7 - codeSwitchingOccurTimes); i++) {
                        w.append("");
                        w.append(COMMA_DELIMITER);
                        w.append("");
                        w.append(COMMA_DELIMITER);
                    }
                    w.append(css.getTranslatedSentence());
                    w.append(COMMA_DELIMITER);
                    w.append(css.getIndicesOfCodeSwitchedWord().toString().replace(",", "_").replace(" ", ""));
                    w.append(COMMA_DELIMITER);
                    if (css.getIndicesOfPunctuation().isEmpty()) {
                        w.append("");
                        w.append(COMMA_DELIMITER);
                    } else {
                        w.append(css.getIndicesOfPunctuation().toString().replace(",", "_").replace(" ", ""));
                        w.append(COMMA_DELIMITER);
                    }
                    if (css.getAddedWordList().isEmpty()) {
                        w.append("");
                    } else {
                        w.append(css.getAddedWordList().toString().replace(",", "_").replace(" ", ""));
                    }
                    w.append(NEW_LINE_SEPARATOR);

                } catch (IOException ex) {
                    Logger.getLogger(MergeCodeSwitchedWordsAndTranslation.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            };

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + "data/merged-translation/"
                    + "10302018-"
                    + corpusName
                    + ".csv");
        }
    }
    
    public static void test() {
    	ArrayList<Integer> indicesOfCodeSwitchedWord = new ArrayList<>();
        ArrayList<Integer> indicesOfPunctuation = new ArrayList<>();
        ArrayList<String> addedWordList = new ArrayList<>();
        ArrayList<String> newWordsInSentence = new ArrayList<>();
        int lastCodeSwitchIndex=-1;
        
        String testString = "this 生活. 不 只是 便利 ， 是 非常 丰富 ~  关于 租约 和  hkjhjk&kj;; 价格   bedroom-something  jklf-ad?ljkdsa . gggkg. hkhjkhj 1$600 ， 555. bedroom 2$540 ， bedroom 3$520 ；";
       
        IdentifyWord identifier = new IdentifyWord();
        for (String word : testString.split("\\s+")) {
            int type = identifier.identifyTheWord(word);
            int i = newWordsInSentence.size();   
            //System.out.println(i);
            switch (type) {
            	//Chinese word found
                case 0: newWordsInSentence.add(word);
                        break;
                //English word found
                case 1: indicesOfCodeSwitchedWord.add(i); 
                        newWordsInSentence.add(word);
                        lastCodeSwitchIndex=i;
                        break;
                //Unknown word with or without punctuation inside found;
                case 2: case 11: if(i-lastCodeSwitchIndex==1) {  //If the previous word is CS word, then this is also considered English
                		indicesOfCodeSwitchedWord.add(i);
                		lastCodeSwitchIndex=i;
                		}
                		newWordsInSentence.add(word);
                		break;
                //Single punctuation or email id or phone number id
                case 3: case 4: if(i-lastCodeSwitchIndex==1) {  //If the previous word is CS word, then this is also considered English
	            		indicesOfCodeSwitchedWord.add(i);
	            		lastCodeSwitchIndex=i;
	            		}
                        indicesOfPunctuation.add(i);
                        newWordsInSentence.add(word);
                        break;
               //2 chunks: first is Chinese, second is punctuation
                case 5: String[] twoChunks=PunctuationDetector.splitWordByPunctuation(word);         
                        addedWordList.add(twoChunks[0]);
                        newWordsInSentence.add(twoChunks[0]);
                        newWordsInSentence.add(twoChunks[1]);
                        indicesOfPunctuation.add(i+1);
                        break;
                //2 chunks: first is English, second is punctuation
                case 6: String[] twoChunksE=PunctuationDetector.splitWordByPunctuation(word);         
                		addedWordList.add(twoChunksE[0]);
                		newWordsInSentence.add(twoChunksE[0]);
                		newWordsInSentence.add(twoChunksE[1]);
                		indicesOfCodeSwitchedWord.add(i);
                		indicesOfCodeSwitchedWord.add(i+1);
                        indicesOfPunctuation.add(i+1);
                        lastCodeSwitchIndex=i+1;
                        break;
                //2 chunks: first is digits or Unknown word, second is punctuation     
                case 7: case 8: String[] twoChunksD=PunctuationDetector.splitWordByPunctuation(word);   
	                	addedWordList.add(twoChunksD[0]);
	            		newWordsInSentence.add(twoChunksD[0]);
	            		newWordsInSentence.add(twoChunksD[1]);
	            		indicesOfPunctuation.add(i+1);
                		if(i-lastCodeSwitchIndex==1) {  //If the previous word is CS word, then this is also considered English
                			indicesOfCodeSwitchedWord.add(i);
                    		indicesOfCodeSwitchedWord.add(i+1);
                    		lastCodeSwitchIndex=i+1;
            			}
                        break;
                //Unknown word with only hyphens in between
                case 9: indicesOfCodeSwitchedWord.add(i); 
		                newWordsInSentence.add(word);
		                lastCodeSwitchIndex=i;
		                break;
		//more than 1 punctuation in unknown word, the word ends with "."
                case 10: String unkownWord = word.substring(0, word.length()-1);            
		            	addedWordList.add(unkownWord);
		        		newWordsInSentence.add(unkownWord);
		        		newWordsInSentence.add(".");
		        		indicesOfPunctuation.add(i+1);
		        		if(i-lastCodeSwitchIndex==1) {  //If the previous word is CS word, then this is also considered English
		        			indicesOfCodeSwitchedWord.add(i);
		            		indicesOfCodeSwitchedWord.add(i+1);
		            		lastCodeSwitchIndex=i+1;
		    			}
		                break;

                default:newWordsInSentence.add(word);
                        System.err.println("Type: " + type + " found, unknown word: " + word);
                        break;                          
            }                                              
        }
        for(int i=0;i<newWordsInSentence.size();i++) {
        	System.out.println(i+": "+newWordsInSentence.get(i));
        }
        System.out.println("codeSwitchIndices: " + indicesOfCodeSwitchedWord.toString());
        System.out.println("punctuationIndices: " + indicesOfPunctuation.toString());
    }
    
}
