/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amtexperiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Analyze Amazon Mechanical Turk task results. 
 * @author Le
 */
public class ResultAnalyzer {
    private LinkedHashMap<String, LinkedList<String[]>> workerIDToTaskOneResult;
    private LinkedHashMap<String, LinkedList<String[]>> workerIDToTaskOneControlResult;    
    private LinkedHashMap<String, LinkedList<String[]>> workerIDToTaskTwoResult;
    private LinkedHashMap<String, LinkedList<String[]>> workerIDToTaskTwoControlResult;
    
    public ResultAnalyzer() {
        workerIDToTaskOneResult = new LinkedHashMap<>();
        workerIDToTaskOneControlResult = new LinkedHashMap<>();
        workerIDToTaskTwoResult = new LinkedHashMap<>();
        workerIDToTaskTwoControlResult = new LinkedHashMap<>();
    }
    
    public static void main(String[] args) {
        ResultAnalyzer analyzer = new ResultAnalyzer();
        analyzer.readTaskOneResult("identify_translation_set_4_results.csv");
        analyzer.readTaskOneResult("identify_translation_set_6_results.csv");
        analyzer.analyzeTaskOneResult();
        //analyzer.readTaskTwoResult("Batch_3853279_batch_results.csv");
        //analyzer.analyzerTaskTwoResult();
    }

    public void analyzeTaskOneResult() {
        LinkedHashMap<String, LinkedList<Integer>> controlIDToResult = new LinkedHashMap<>();
        LinkedHashMap<String, Double> sums = new LinkedHashMap<>();
        System.out.println("Average control response for each worker: ");
        workerIDToTaskOneControlResult.forEach((k,v) -> {
            double sum = 0.0;           
            for(String[] pair : v) {
                sum += Integer.parseInt(pair[1]);
                if(!controlIDToResult.containsKey(pair[0])) {
                    //System.out.println("Adding " + pair[0]);
                    LinkedList<Integer> result = new LinkedList<>();
                    result.add(Integer.parseInt(pair[1]));
                    controlIDToResult.put(pair[0], result);
                } else {
                    controlIDToResult.get(pair[0]).add(Integer.parseInt(pair[1]));
                }
            }
            sums.put(k, sum);
            System.out.println("Worker " + k + ": " + sum/v.size());
        });
//        controlIDToResult.forEach((k,v) -> {          
//            double sum = 0.0;
//            //if(v.size() != 3) {
//            //    System.out.println("Not 3 responses for " + k);
//            //}
//            for(Integer integer : v) {
//                sum += integer;
//            }
//            double average = sum / v.size();
//            System.out.println("Average respone of " + k + ": " + average);        
//        });
        
        LinkedHashMap<String, LinkedList<Integer>> sentenceIDToResult = new LinkedHashMap<>();
        System.out.println("Average sentence response for each worker: ");           
        workerIDToTaskOneResult.forEach((k,v) -> {
            double sum = 0.0;           
            for(String[] pair : v) {
                sum += Integer.parseInt(pair[1]);
                if(!sentenceIDToResult.containsKey(pair[0])) {
                    //System.out.println("Adding " + pair[0]);
                    LinkedList<Integer> result = new LinkedList<>();
                    result.add(Integer.parseInt(pair[1]));
                    sentenceIDToResult.put(pair[0], result);
                } else {
                    sentenceIDToResult.get(pair[0]).add(Integer.parseInt(pair[1]));
                }
            }
            sums.put(k, (sums.get(k)+ sum));
            System.out.println("Worker " + k + ": " + sum/v.size());           
        });
        
        System.out.println("Average global sentence response for each worker:");
        sums.forEach((k,v) -> {
            double average = v / (workerIDToTaskOneResult.get(k).size() + workerIDToTaskOneControlResult.get(k).size());
            System.out.println("Worker " + k + ": " + average);
        });
        
//        sentenceIDToResult.forEach((k,v) -> {          
//            double sum = 0.0;
//            //if(v.size() != 3) {
//            //    System.out.println("Not 3 responses for " + k);
//            //}
//            for(Integer integer : v) {
//                sum += integer;
//            }
//            double average = sum / v.size();
//            System.out.println("Average respone of " + k + ": " + average);        
//        });
    }
    
    public void analyzerTaskTwoResult() {
        LinkedHashMap<String, LinkedList<Integer>> controlIDToAdequacyResult = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedList<Integer>> controlIDToFluencyResult = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedList<Integer>> sentenceIDToAdequacyResult = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedList<Integer>> sentenceIDToFluencyResult = new LinkedHashMap<>();
        LinkedHashMap<String, Double> adequacySums = new LinkedHashMap<>();
        LinkedHashMap<String, Double> fluencySums = new LinkedHashMap<>();
        workerIDToTaskTwoControlResult.forEach((k,v) -> {
            double adequacySum = 0.0;  
            double fluencySum = 0.0;
            System.out.println("Worker: " + k);
            for(String[] pair : v) {
                adequacySum += Integer.parseInt(pair[1]);
                fluencySum += Integer.parseInt(pair[2]);
                if(!controlIDToAdequacyResult.containsKey(pair[0])) {
                    //System.out.println("Adding " + pair[0]);
                    LinkedList<Integer> result = new LinkedList<>();
                    result.add(Integer.parseInt(pair[1]));
                    controlIDToAdequacyResult.put(pair[0], result);
                } else {
                    controlIDToAdequacyResult.get(pair[0]).add(Integer.parseInt(pair[1]));
                }               
                if(!controlIDToFluencyResult.containsKey(pair[0])) {
                    //System.out.println("Adding " + pair[0]);
                    LinkedList<Integer> result = new LinkedList<>();
                    result.add(Integer.parseInt(pair[2]));
                    controlIDToFluencyResult.put(pair[0], result);
                } else {
                    controlIDToFluencyResult.get(pair[0]).add(Integer.parseInt(pair[2]));
                }
            }
            adequacySums.put(k, adequacySum);
            fluencySums.put(k, fluencySum);
            System.out.println("Average control adequacy response: " + adequacySum/v.size());
            System.out.println("Average control fluency response: " + fluencySum/v.size());
        });
        workerIDToTaskTwoResult.forEach((k,v) -> {
            double adequacySum = 0.0;  
            double fluencySum = 0.0;
            System.out.println("Worker: " + k);
            for(String[] pair : v) {
                adequacySum += Integer.parseInt(pair[1]);
                fluencySum += Integer.parseInt(pair[2]);
                if(!sentenceIDToAdequacyResult.containsKey(pair[0])) {
                    //System.out.println("Adding " + pair[0]);
                    LinkedList<Integer> result = new LinkedList<>();
                    result.add(Integer.parseInt(pair[1]));
                    sentenceIDToAdequacyResult.put(pair[0], result);
                } else {
                    sentenceIDToAdequacyResult.get(pair[0]).add(Integer.parseInt(pair[1]));
                }               
                if(!sentenceIDToFluencyResult.containsKey(pair[0])) {
                    //System.out.println("Adding " + pair[0]);
                    LinkedList<Integer> result = new LinkedList<>();
                    result.add(Integer.parseInt(pair[2]));
                    sentenceIDToFluencyResult.put(pair[0], result);
                } else {
                    sentenceIDToFluencyResult.get(pair[0]).add(Integer.parseInt(pair[2]));
                }
            }
            adequacySums.put(k, (adequacySums.get(k) + adequacySum));
            fluencySums.put(k, (fluencySums.get(k) + fluencySum));
            System.out.println("Average sentence adequacy response: " + adequacySum/v.size());
            System.out.println("Average sentence fluency response: " + fluencySum/v.size());
        });
        
        adequacySums.forEach((k,v) -> {
            System.out.println("Worker: " + k);
            double average = v / (workerIDToTaskTwoResult.get(k).size() + workerIDToTaskTwoControlResult.get(k).size());
            System.out.println("Average global sentence adequacy response: " + average);
        });
        fluencySums.forEach((k,v) -> {
            System.out.println("Worker: " + k);
            double average = v / (workerIDToTaskTwoResult.get(k).size() + workerIDToTaskTwoControlResult.get(k).size());
            System.out.println("Average global sentence fluency response: " + average);
        });
        
        
        /*
        System.out.println("Average adequacy respone of: ");
        controlIDToAdequacyResult.forEach((k,v) -> {          
            double sum = 0.0;
            //if(v.size() != 3) {
            //    System.out.println("Not 3 responses for " + k);
            //}
            for(Integer integer : v) {
                sum += integer;
            }
            double average = sum / v.size();
            System.out.println(k + ": " + average);        
        });
        System.out.println("Average fluency respone of: ");
        controlIDToFluencyResult.forEach((k,v) -> {          
            double sum = 0.0;
            //if(v.size() != 3) {
            //    System.out.println("Not 3 responses for " + k);
            //}
            for(Integer integer : v) {
                sum += integer;
            }
            double average = sum / v.size();
            System.out.println(k + ": " + average);        
        });
        */
    }
    
    
    //fill the workerIDToTaskOneResult and workerIDToTaskOneControlResult
    public void readTaskOneResult(String inputFilename) {
        BufferedReader br;
        try {
            //initialize input file path
            File fileDir = new File("data/get_experiment_sentences/input/test_run/"
                    + inputFilename);
            
            //initialize buffered reader br
            //create an input stream read to read bytes and decode them to characters
            //create a file input stream by opening a connection to an actual file and use decode "UTF-8"
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF-8"));

            //initialize a string to store each line in the input file
            String line = "";
            
            
            //header
            String header = br.readLine();
            String[] headerColumns = splitIntoColumns(header);
            
            //header length
            int headerLength = headerColumns.length;
            System.out.println("Identify Translation task result header length: " + headerLength);
            //first sentence headerColumns[27], first sentence info headerColumns[28]
            //System.out.println("first sentence :" + headerColumns[27]);
            //System.out.println("first sentence info :" + headerColumns[28]);
            //last sentence sentence headerColumns [27+47*2]=[121], last sentence info headerColumns [28+47*2]=[122]
            //System.out.println("last sentence sentence :" + headerColumns[121]);
            //System.out.println("last sentence info :" + headerColumns[122]);
            //first opinion headerColumns[123], last opinion headerColumns[123+47]=headerColumns[170]
            //System.out.println("first opinion :" + headerColumns[123]);
            //System.out.println("last opinion :" + headerColumns[(123+47)]);
    
            //store sentence_info index by increment order
            int[] sentenceInfoIndices = new int[48];
            //store opinion index by increment order
            int[] opinionIndices = new int[48];
            for(int i =0; i < headerColumns.length; i++) {
                if(headerColumns[i].equals("\"WorkerId\"")) {
                    System.out.println("WorkerID is at index " + i);
                } 
                if(headerColumns[i].contains("Input.sentence_info_")) {
                    //System.out.println(headerColumns[i]);
                    //substring the leading " and trailing "     
                    String sentenceInfo = headerColumns[i].substring(1, headerColumns[i].length() - 1);
                    String[] tokens = sentenceInfo.split("_");
                    int assignIndex = Integer.parseInt(tokens[tokens.length-1]);
                    //System.out.println("Assign sentence info index: " + i + " at " + insertIndex);
                    sentenceInfoIndices[assignIndex] = i;                  
                } else if (headerColumns[i].contains("Answer.opinion_")) {
                    //System.out.println(headerColumns[i]);
                    //substring the leading " and trailing "     
                    String opinion = headerColumns[i].substring(1, headerColumns[i].length() - 1);
                    String[] tokens = opinion.split("_");
                    int assignIndex = Integer.parseInt(tokens[tokens.length-1]);
                    //System.out.println("Assign opinion index: " + i + " at " + insertIndex);
                    opinionIndices[assignIndex] = i;
                }
            }
            
            LinkedList<String[]> result = new LinkedList<>();
            LinkedList<String[]> controlResult = new LinkedList<>();
            while ((line = br.readLine()) != null) {
                String[] columns = splitIntoColumns(line);
                //length should be header length -2 because apporve and reject column is missing
                if (columns.length != headerLength - 2) {
                    System.out.println("The line below does not has " + (headerLength - 2) + "items, but " + columns.length);
                    System.out.println(line);
                }
            
                //substring the leading " and trailing "              
                String workerID = columns[15].substring(1, columns[15].length() - 1);
                if(workerID.equals("A11SWVGXLZCTQF")) {
                    continue;
                }
                //System.out.println(workerID);               
                for(int i = 0; i < sentenceInfoIndices.length; i++) {
                    String sentenceInfo = columns[sentenceInfoIndices[i]];
                    sentenceInfo = sentenceInfo.substring(1, sentenceInfo.length()-1);
                    String opinion = columns[opinionIndices[i]];
                    opinion = opinion.substring(1, opinion.length()-1);
                    String[] pair = new String[2];
                    pair[0] = sentenceInfo;
                    pair[1] = opinion;                    
                    //System.out.println(sentenceInfo);
                    //System.out.println(opinion);
                    if(sentenceInfo.contains("CONTROL_")) {
                        controlResult.add(pair);
                    } else {
                        result.add(pair);
                    }
                }
                workerIDToTaskOneResult.put(workerID, result);
                workerIDToTaskOneControlResult.put(workerID, controlResult);
                //System.out.println("Size of result: " + result.size());
                //System.out.println("Size of control result: " + controlResult.size());
                result = new LinkedList<>();
                controlResult = new LinkedList<>();         
            }
   
            br.close();
            System.out.println("Finished reading " + inputFilename);
            System.out.println("Size of workerIDToTaskOneResult: " + workerIDToTaskOneResult.size());
            System.out.println("Size of workerIDToTaskOneControlResult: " + workerIDToTaskOneControlResult.size());     
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResultAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ResultAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResultAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void readTaskTwoResult(String inputFilename) {
        BufferedReader br;
        try {
            //initialize input file path
            File fileDir = new File("data/get_experiment_sentences/input/test_run/"
                    + inputFilename);
            
            //initialize buffered reader br
            //create an input stream read to read bytes and decode them to characters
            //create a file input stream by opening a connection to an actual file and use decode "UTF-8"
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF-8"));

            //initialize a string to store each line in the input file
            String line = "";
         
            //header
            String header = br.readLine();
            String[] headerColumns = splitIntoColumns(header);
            
            //header length
            int headerLength = headerColumns.length;
            System.out.println("Rate Translation Quality task result header length: " + headerLength);
            //first sentence headerColumns[27], first sentence info headerColumns[28]
            //System.out.println("first sentence :" + headerColumns[27]);
            //System.out.println("first sentence info :" + headerColumns[29]);
            //last sentence sentence headerColumns [27+27*3]=[108], last sentence info headerColumns [29+27*3]=[110]
            //System.out.println("last sentence sentence :" + headerColumns[108]);
            //System.out.println("last sentence info :" + headerColumns[110]);
            //first adequacy headerColumns[111], last fluency headerColumns[138]
            //System.out.println("first adequacy :" + headerColumns[111]);
            //System.out.println("last adequacy :" + headerColumns[138]);
            //first fluency headerColumns[139], last fluency headerColumns[139+27]=headerColumns[166]
            //System.out.println("first fluency :" + headerColumns[139]);
            //System.out.println("last fluency :" + headerColumns[166]);
    
            //store sentence_info index by increment order
            int[] sentenceInfoIndices = new int[28];
            //store adequacy index by increment order
            int[] adequacyIndices = new int[28];
            //store fluency index by increment order
            int[] fluencyIndices = new int[28];
            for(int i =0; i < headerColumns.length; i++) {
                //if(headerColumns[i].equals("\"WorkerId\"")) {
                //    System.out.println("WorkerID is at index " + i);
                //} 

                if(headerColumns[i].contains("Input.sentence_info_")) {
                    //System.out.println(headerColumns[i]);
                    //substring the leading " and trailing "     
                    String sentenceInfo = headerColumns[i].substring(1, headerColumns[i].length() - 1);
                    String[] tokens = sentenceInfo.split("_");
                    int assignIndex = Integer.parseInt(tokens[tokens.length-1]);
                    //System.out.println("Assign sentence info index: " + i + " at " + insertIndex);
                    sentenceInfoIndices[assignIndex] = i;                  
                } else if (headerColumns[i].contains("Answer.adequacy_opinion_")) {
                    //System.out.println(headerColumns[i]);
                    //substring the leading " and trailing "     
                    String adequacy = headerColumns[i].substring(1, headerColumns[i].length() - 1);
                    String[] tokens = adequacy.split("_");
                    int assignIndex = Integer.parseInt(tokens[tokens.length-1]);
                    //System.out.println("Assign adequacy opinion index: " + i + " at " + insertIndex);
                    adequacyIndices[assignIndex] = i;
                } else if (headerColumns[i].contains("Answer.fluency_opinion_")) {
                    //System.out.println(headerColumns[i]);
                    //substring the leading " and trailing "     
                    String fluency = headerColumns[i].substring(1, headerColumns[i].length() - 1);
                    String[] tokens = fluency.split("_");
                    int assignIndex = Integer.parseInt(tokens[tokens.length-1]);
                    //System.out.println("Assign fluency opinion index: " + i + " at " + insertIndex);
                    fluencyIndices[assignIndex] = i;
                }

            }
            
            LinkedList<String[]> result = new LinkedList<>();
            LinkedList<String[]> controlResult = new LinkedList<>();
            while ((line = br.readLine()) != null) {
                String[] columns = splitIntoColumns(line);
                //length should be header length -2 because apporve and reject column is missing
                if (columns.length != headerLength - 2) {
                    System.out.println("The line below does not has " + (headerLength - 2) + "items, but " + columns.length);
                    System.out.println(line);
                }
            
                //substring the leading " and trailing "              
                String workerID = columns[15].substring(1, columns[15].length() - 1);
                //System.out.println(workerID);               
                for(int i = 0; i < sentenceInfoIndices.length; i++) {
                    String sentenceInfo = columns[sentenceInfoIndices[i]];
                    sentenceInfo = sentenceInfo.substring(1, sentenceInfo.length()-1);
                    String adequacy = columns[adequacyIndices[i]];
                    adequacy = adequacy.substring(1, adequacy.length()-1);
                    String fluency = columns[fluencyIndices[i]];
                    fluency = fluency.substring(1, fluency.length()-1);
                    String[] pair = new String[3];
                    pair[0] = sentenceInfo;
                    pair[1] = adequacy;   
                    pair[2] = fluency;
                    //System.out.println(sentenceInfo);
                    //System.out.println(opinion);
                    if(sentenceInfo.contains("CONTROL_")) {
                        controlResult.add(pair);
                    } else {
                        result.add(pair);
                    }
                }
                workerIDToTaskTwoResult.put(workerID, result);
                workerIDToTaskTwoControlResult.put(workerID, controlResult);
                //System.out.println("Size of result: " + result.size());
                //System.out.println("Size of control result: " + controlResult.size());
                result = new LinkedList<>();
                controlResult = new LinkedList<>();         
            }
  
            br.close();
            System.out.println("Finished reading " + inputFilename);
            System.out.println("Size of workerIDToTaskTwoResult: " + workerIDToTaskTwoResult.size());
            System.out.println("Size of workerIDToTaskTwoControlResult: " + workerIDToTaskTwoControlResult.size());     
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResultAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ResultAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResultAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public String[] splitIntoColumns(String line) {
            String otherThanQuote = " [^\"] ";
            String quotedString = String.format(" \" %s* \" ", otherThanQuote);
            String regex = String.format("(?x) "
                    + // enable comments, ignore white spaces
                    ",                         "
                    + // match a comma
                    "(?=                       "
                    + // start positive look ahead
                    "  (?:                     "
                    + //   start non-capturing group 1
                    "    %s*                   "
                    + //     match 'otherThanQuote' zero or more times
                    "    %s                    "
                    + //     match 'quotedString'
                    "  )*                      "
                    + //   end group 1 and repeat it zero or more times
                    "  %s*                     "
                    + //   match 'otherThanQuote'
                    "  $                       "
                    + // match the end of the string
                    ")                         ", // stop positive look ahead
                    otherThanQuote, quotedString, otherThanQuote);
            //String regex = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

            String[] columns = line.split(regex, -1);
            
            return columns;
    }
}
