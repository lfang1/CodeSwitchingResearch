/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amtexperiment;

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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Manger hired workers for experiments on Amazon Mechanical Turk.
 * @author Le
 */
public class WorkerManager {

    private LinkedHashMap<String, String> qualifiedWorkerIDToInfo;
    private LinkedHashMap<String, String> blockedWorkerIDToInfo;
    private LinkedHashMap<String, String> lowQualityWorkerIDToInfo;
    private LinkedHashMap<String, String> allWorkerIDToInfo;
    private LinkedList<String> qualifiedWorkerIDList;
    private LinkedList<String> blockedWorkerIDList;
    private LinkedList<String> testWorkerIDList;
    private LinkedHashMap<String, String> testWorkerIDToInfo;
    private String workerInfoHeader = "";
    private int[] setIndices;

    public WorkerManager() {
        //add test workerID to the list;
        this.fillTestWorkerIDList();
        setIndices = new int[26];      
        allWorkerIDToInfo = new LinkedHashMap<>();
        qualifiedWorkerIDToInfo = new LinkedHashMap<>();
        blockedWorkerIDToInfo = new LinkedHashMap<>();
        testWorkerIDToInfo = new LinkedHashMap<>();
        lowQualityWorkerIDToInfo = new LinkedHashMap<>();
        qualifiedWorkerIDList = new LinkedList<>();
        blockedWorkerIDList = new LinkedList<>();
    }

    public static void main(String[] args) {
        WorkerManager manager = new WorkerManager();
        //add qualified workID to the list
        //manager.fillQualifiedWorkerIDList();     
        //manager.fillWorkerInfoList("12092019_all_workers.csv");
        //manager.assignNWorkersToXSets("12092019_v2");
        //manager.resetAllWorkers("12042019");
        //manager.verifyQualifcationAssignment("12052019_qualified_worker_info.csv");
        //manager.saveBlockedWorkerInfo("11292019");     
        //manager.selectControlTestWorker(10, 1);
        //manager.saveTestWorkerInfo("12042019");
        manager.rejectLowQualityWorker("12092019_all_workers.csv");
    }
    
    public void rejectLowQualityWorker(String inputFilename) {
        LinkedList<String> lowQualityWorkerIDs = new LinkedList<>();
        //task1
        /*
        lowQualityWorkerIDs.add("A3RZXYMT4ZCAS");
        lowQualityWorkerIDs.add("A5WAWW70PYRP");
        lowQualityWorkerIDs.add("ASFBR18FCV8Z1");
        lowQualityWorkerIDs.add("A2AHXGFXPG6ZSR");
        lowQualityWorkerIDs.add("A3CJ3BSN87874X");
        lowQualityWorkerIDs.add("AFUUPNBIKHRFZ");
        lowQualityWorkerIDs.add("A1EK8YNHZHTW1J");
        */
        //task2
        lowQualityWorkerIDs.add("A5WAWW70PYRP");
        lowQualityWorkerIDs.add("ASFBR18FCV8Z1");
        lowQualityWorkerIDs.add("AFUUPNBIKHRFZ");
        BufferedReader br;
        String inputFileFolderPath = "data/get_experiment_sentences/input/task2_result/";
        try {
            List<File> filesInFolder = Files.walk(Paths.get(inputFileFolderPath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            System.out.println("Start reading files in " + inputFileFolderPath);
            for (File filename : filesInFolder) {
                //initialize buffered reader br
                //create an input stream read to read bytes and decode them to characters
                //create a file input stream by opening a connection to an actual file and use decode "UTF-8"
                br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(filename), "UTF8"));

                //initialize a string to store each line in the input file
                String line = "";
                //header
                workerInfoHeader = br.readLine();
                String[] headerColumns = splitIntoColumns(workerInfoHeader);
                //header length
                int headerLength = headerColumns.length;
                //System.out.println("File header length: " + headerLength);

                while ((line = br.readLine()) != null) {

                    String[] columns = splitIntoColumns(line);
                    //true if a different length is found
                    if (columns.length != headerLength && columns.length != headerLength - 2) {
                        System.err.println("The line has " + columns.length + " columns");
                        System.err.println(line);
                    }

                    //substring the leading " and trailing "  columns[x].substring(1, columns[x].length()-1
                    String workerID = columns[15].substring(1, columns[15].length() - 1);
                    //System.out.println(headerColumns[15] + ": " + workerID);
                    if (lowQualityWorkerIDs.contains(workerID)) {
                        System.out.println(workerID + " " + filename + ": " + headerColumns[headerLength-1] + ": x");
                    }
                }

                br.close();
            }
            System.out.println("Finished reading control test results.");
            System.out.println("Size of lowQualityWorkerIDToInfo: " + lowQualityWorkerIDToInfo.size());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public  void assignNWorkersToXSets(String version) {
        /*
            A1WTD5OHCQS3TG
            AW05ZDBCQIU8F
            A3CJ3BSN87874X
            A1EK8YNHZHTW1J
            AZE7YSINS9UNZ
        
            A2BFJUDBW8ZNTQ      
        */
//        LinkedList<String> newWorkerIDs = new LinkedList<>();
//        newWorkerIDs.add("A1WTD5OHCQS3TG");
//        newWorkerIDs.add("AW05ZDBCQIU8F");
//        newWorkerIDs.add("A3CJ3BSN87874X");
//        newWorkerIDs.add("A1EK8YNHZHTW1J");
//        newWorkerIDs.add("AZE7YSINS9UNZ");
//        newWorkerIDs.add("A2BFJUDBW8ZNTQ");
        LinkedList<String> availableWorkerIDs = new LinkedList<>(qualifiedWorkerIDList);
//        for(String id : newWorkerIDs) {
//            availableWorkerIDs.remove(id);
//        }
        LinkedList<Integer> numbers = new LinkedList<>();
        for(int i = 0 ; i < 25; i++) {
            if(i >= 22 || i == 18 || i == 19 || i == 12 || i == 9 || i <= 1) {
                numbers.add(i);
            }
        }
        System.out.println("There are " + numbers.size() + " numbers and " + availableWorkerIDs.size() + " available workers");
        
        int workersPerSet = availableWorkerIDs.size();
        System.out.println("Each set will be assigned to: " + workersPerSet + " workers");
          
        
        LinkedHashMap<String, String> availableWorkerIDToInfo = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedList<Integer>> availableWorkerIDToSetID = new LinkedHashMap<>();
        Random rand = new Random();
        Collections.shuffle(availableWorkerIDs, rand); 
        int lastAssignedIndex = -1;
        for(Integer number : numbers) {
            System.out.println("Set_" + number + ":");
            for(int i = 0; i < workersPerSet; i++) {
                int currentAssignedIndex = lastAssignedIndex + 1;
                lastAssignedIndex = currentAssignedIndex;
                if(lastAssignedIndex == availableWorkerIDs.size() - 1) {
                    lastAssignedIndex = -1;
                }
                String workerID = availableWorkerIDs.get(currentAssignedIndex);
                if(availableWorkerIDToSetID.get(workerID) == null) {
                    availableWorkerIDToSetID.put(workerID, new LinkedList<>());
                }
                availableWorkerIDToSetID.get(workerID).add(number);
                System.out.println("Assigned to " + workerID);
            }
        }
        
        
        for (String workerID : availableWorkerIDs) {
            System.out.println(workerID + ": ");
            String availableWorkerInfo = allWorkerIDToInfo.get(workerID);
            LinkedList<Integer> setIDs = availableWorkerIDToSetID.get(workerID);
            String[] headerColumns = splitIntoColumns(workerInfoHeader);
            String columns[] = splitIntoColumns(availableWorkerInfo);
            for(Integer id : setIDs) {
                
                if(columns[setIndices[id]-1].equals("\"\"")) {
                    columns[setIndices[id]-1] = "1";
                    System.out.println("assign" + headerColumns[setIndices[id]] + " to 1");
                } else {  
                    System.out.println(availableWorkerInfo);
                    System.err.println("At index " + (setIndices[id]-1) + ", " + headerColumns[setIndices[id]-1] + " is already assigned to 1: " + columns[setIndices[id]-1]);    
                }               
            }
            availableWorkerInfo = String.join(",", columns);
            availableWorkerIDToInfo.put(workerID, availableWorkerInfo);
        }
        
        
        
        final String NEW_LINE_SEPARATOR = "\n";
        //final String COMMA_DELIMITER = ",";
        final String FILE_HEADER = workerInfoHeader;
        try {
            String outputFilename = "data/get_experiment_sentences/output/worker_info/"
                    + version
                    + "_available_qualified_worker_info.csv";
            File outputfile = new File(outputFilename);
            System.out.println("The file will be saved in: "
                    + outputfile.getPath());
            FileOutputStream is = new FileOutputStream(outputfile);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);
            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            LinkedList<String> newWorkerInfo = new LinkedList(availableWorkerIDToInfo.values());
           
            for (int i = 0; i < newWorkerInfo.size(); i++) {
                //System.out.println(testWorkerInfo.get(i));
                w.append(newWorkerInfo.get(i));
                if (i != newWorkerInfo.size() - 1) {
                    w.append(NEW_LINE_SEPARATOR);
                }
            }
            System.out.println("CSV file was created successfully !!!");
            w.flush();
            w.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
     /* 
        
      double setsPerWorker = Math.ceil(numbers.size() / (double) newWorkerIDs.size());        
        System.out.println("Each worker will be assigned: " + setsPerWorker + " sets");
        Random rand = new Random();
        Collections.shuffle(numbers, rand);   
        int lastAssignedIndex = -1;
        LinkedHashMap<String, String> newWorkerIDToInfo = new LinkedHashMap<>();
        for (String workerID : newWorkerIDs) {
            System.out.println(workerID + ": ");
            String newWorkerInfo = allWorkerIDToInfo.get(workerID);
            String[] headerColumns = splitIntoColumns(workerInfoHeader);
            String columns[] = splitIntoColumns(newWorkerInfo);
            for(int i = 0; i < 4; i++) {
                if(lastAssignedIndex == numbers.size() - 1) {
                lastAssignedIndex = -1;
                }
                int currentAssignedIndex = lastAssignedIndex + 1;
                lastAssignedIndex = currentAssignedIndex;
                if(columns[setIndices[numbers.get(currentAssignedIndex)]].equals("\"\"")) {
                    columns[setIndices[numbers.get(currentAssignedIndex)]] = "1";
                    System.out.println("assign" + headerColumns[setIndices[numbers.get(currentAssignedIndex)]] + " to 1");
                } else {  
                    System.out.println(newWorkerInfo);
                    System.err.println("At index " + setIndices[numbers.get(currentAssignedIndex)] + ", " + headerColumns[setIndices[numbers.get(currentAssignedIndex)]] + " is not empty, but: " + columns[setIndices[numbers.get(currentAssignedIndex)]]);    
                }               
            }
            newWorkerInfo = String.join(",", columns);
            newWorkerIDToInfo.put(workerID, newWorkerInfo);
        }
        final String NEW_LINE_SEPARATOR = "\n";
        //final String COMMA_DELIMITER = ",";
        final String FILE_HEADER = workerInfoHeader;
        try {
            String outputFilename = "data/get_experiment_sentences/output/worker_info/"
                    + version
                    + "_added_qualified_worker_info.csv";
            File outputfile = new File(outputFilename);
            System.out.println("The file will be saved in: "
                    + outputfile.getPath());
            FileOutputStream is = new FileOutputStream(outputfile);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);
            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            LinkedList<String> newWorkerInfo = new LinkedList(newWorkerIDToInfo.values());
           
            for (int i = 0; i < newWorkerInfo.size(); i++) {
                //System.out.println(testWorkerInfo.get(i));
                w.append(newWorkerInfo.get(i));
                if (i != newWorkerInfo.size() - 1) {
                    w.append(NEW_LINE_SEPARATOR);
                }
            }
            System.out.println("CSV file was created successfully !!!");
            w.flush();
            w.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }

    public void fillControlTestWorkerIDList() {
        BufferedReader br;
        String inputFileFolderPath = "data/get_experiment_sentences/input/control_test/result/";
        try {
            List<File> filesInFolder = Files.walk(Paths.get(inputFileFolderPath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            System.out.println("Start reading files in " + inputFileFolderPath);
            for (File filename : filesInFolder) {
                //initialize buffered reader br
                //create an input stream read to read bytes and decode them to characters
                //create a file input stream by opening a connection to an actual file and use decode "UTF-8"
                br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(filename), "UTF8"));

                //initialize a string to store each line in the input file
                String line = "";
                //header
                workerInfoHeader = br.readLine();
                String[] headerColumns = splitIntoColumns(workerInfoHeader);
                //header length
                int headerLength = headerColumns.length;
                //System.out.println("File header length: " + headerLength);

                while ((line = br.readLine()) != null) {

                    String[] columns = splitIntoColumns(line);
                    //true if a different length is found
                    if (columns.length != headerLength && columns.length != headerLength - 2) {
                        System.err.println("The line has " + columns.length + " columns");
                        System.err.println(line);
                    }

                    //substring the leading " and trailing "  columns[x].substring(1, columns[x].length()-1
                    String workerID = columns[15].substring(1, columns[15].length() - 1);
                    //System.out.println(headerColumns[15] + ": " + workerID);
                    if (!testWorkerIDList.contains(workerID)) {
                        testWorkerIDList.add(workerID);
                    }
                }

                br.close();
            }
            System.out.println("Finished reading control test results.");
            System.out.println("Size of testWorkerIDList: " + testWorkerIDList.size());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fillNaturalSentenceTestRunWorkerIDList() {
        BufferedReader br;
        String inputFileFolderPath = "data/get_experiment_sentences/input/test_run/natural_sentence/";
        try {
            List<File> filesInFolder = Files.walk(Paths.get(inputFileFolderPath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            System.out.println("Start reading files in " + inputFileFolderPath);
            for (File filename : filesInFolder) {
                //initialize buffered reader br
                //create an input stream read to read bytes and decode them to characters
                //create a file input stream by opening a connection to an actual file and use decode "UTF-8"
                br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(filename), "UTF8"));

                //initialize a string to store each line in the input file
                String line = "";
                //header
                workerInfoHeader = br.readLine();
                String[] headerColumns = splitIntoColumns(workerInfoHeader);
                //header length
                int headerLength = headerColumns.length;
                //System.out.println("File header length: " + headerLength);

                while ((line = br.readLine()) != null) {

                    String[] columns = splitIntoColumns(line);
                    //true if a different length is found
                    if (columns.length != headerLength && columns.length != headerLength - 2) {
                        System.err.println("The line has " + columns.length + " columns");
                        System.err.println(line);
                    }

                    //substring the leading " and trailing "  columns[x].substring(1, columns[x].length()-1
                    String workerID = columns[15].substring(1, columns[15].length() - 1);
                    //System.out.println(headerColumns[15] + ": " + workerID);
                    if (!testWorkerIDList.contains(workerID)) {
                        testWorkerIDList.add(workerID);
                    }
                }

                br.close();
            }
            System.out.println("Finished reading natural sentence test run results.");
            System.out.println("Size of testWorkerIDList: " + testWorkerIDList.size());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fillTranslationQualityTestRunWorkerIDList() {
        BufferedReader br;
        String inputFileFolderPath = "data/get_experiment_sentences/input/test_run/translation_quality/";
        try {
            List<File> filesInFolder = Files.walk(Paths.get(inputFileFolderPath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            System.out.println("Start reading files in " + inputFileFolderPath);
            for (File filename : filesInFolder) {
                //initialize buffered reader br
                //create an input stream read to read bytes and decode them to characters
                //create a file input stream by opening a connection to an actual file and use decode "UTF-8"
                br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(filename), "UTF8"));

                //initialize a string to store each line in the input file
                String line = "";
                //header
                workerInfoHeader = br.readLine();
                String[] headerColumns = splitIntoColumns(workerInfoHeader);
                //header length
                int headerLength = headerColumns.length;
                //System.out.println("File header length: " + headerLength);

                while ((line = br.readLine()) != null) {

                    String[] columns = splitIntoColumns(line);
                    //true if a different length is found
                    if (columns.length != headerLength && columns.length != headerLength - 2) {
                        System.err.println("The line has " + columns.length + " columns");
                        System.err.println(line);
                    }

                    //substring the leading " and trailing "  columns[x].substring(1, columns[x].length()-1
                    String workerID = columns[15].substring(1, columns[15].length() - 1);
                    //System.out.println(headerColumns[15] + ": " + workerID);
                    if (!testWorkerIDList.contains(workerID)) {
                        testWorkerIDList.add(workerID);
                    }
                }

                br.close();
            }
            System.out.println("Finished reading natural sentence test run results.");
            System.out.println("Size of testWorkerIDList: " + testWorkerIDList.size());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void verifyQualifcationAssignment(String inputFilename) {
        BufferedReader br;
        try {
            //initialize input file path
            File fileDir = new File("data/get_experiment_sentences/output/worker_info/"
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
            workerInfoHeader = br.readLine();
            String[] headerColumns = splitIntoColumns(workerInfoHeader);
            //header length
            int headerLength = headerColumns.length;
            System.out.println("Worker info update block status: " + headerColumns[headerLength - 2]);
            System.out.println("Worker info block reason: " + headerColumns[headerLength - 1]);
            System.out.println("Worker info first update-set: " + headerColumns[headerLength - 4 - 2 * 24]);
            System.out.println("Worker info last update-set: " + headerColumns[headerLength - 4]);

            System.out.println("Worker info file header length: " + headerLength);

            while ((line = br.readLine()) != null) {

                String[] columns = splitIntoColumns(line);
                //true if a different length is found
                if (columns.length != headerLength) {
                    System.out.println("The line below does not has " + headerLength + " items, but " + columns.length);
                    System.out.println(line);
                }

                //substring the leading " and trailing "  columns[x].substring(1, columns[x].length()-1
                String workerID = columns[0].substring(1, columns[0].length() - 1);
                System.out.println(workerID);

                //revoke all qualification for set_i
                //System.out.println("Worker info first update-set column: " +columns[columns.length-6-2*24]);
                //System.out.println("Worker info last update-set: " + columns[columns.length-4]);
                for (int i = columns.length - 6 - 2 * 24; i <= columns.length - 4; i += 2) {
                    if (columns[i].equals("\"1\"")) {
                        System.out.println(headerColumns[i] + ": " + columns[i]);
                    }
                    //System.out.println(headerColumns[i] + ": " + columns[i]);
                }

            }

            br.close();
            System.out.println("Finished reading " + inputFilename);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void selectTestWorkers(int numberOfTesters) {
        Random rand = new Random();
        testWorkerIDList = new LinkedList<>();
        Collections.shuffle(qualifiedWorkerIDList, rand);
        for (int i = 0; i < numberOfTesters; i++) {
            testWorkerIDList.add(qualifiedWorkerIDList.get(i));
        }
    }

    public void resetAllWorkers(String date) {
        final String NEW_LINE_SEPARATOR = "\n";
        final String FILE_HEADER = workerInfoHeader;
        try {
            String outputFilename = "data/get_experiment_sentences/output/worker_info/"
                    + date
                    + "_reset_all_workers.csv";
            File outputfile = new File(outputFilename);
            System.out.println("The file will be saved in: "
                    + outputfile.getPath());
            FileOutputStream is = new FileOutputStream(outputfile);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);
            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            LinkedList<String> allWorkerInfo = new LinkedList(allWorkerIDToInfo.values());

            for (int i = 0; i < allWorkerInfo.size(); i++) {
                //System.out.println(allWorkerInfo.get(i));
                w.append(allWorkerInfo.get(i));
                if (i != allWorkerInfo.size() - 1) {
                    w.append(NEW_LINE_SEPARATOR);
                }
            }
            System.out.println("CSV file was created successfully !!!");
            w.flush();
            w.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveQualificationAssignment(String date,
            LinkedHashMap<String, LinkedList<Integer>> taskOneWorkIDToSentenceListIDs,
            LinkedHashMap<String, LinkedList<Integer>> taskTwoWorkIDToSentenceListIDs) {
        LinkedList<String> taskOneWorkIDList = new LinkedList<>(taskOneWorkIDToSentenceListIDs.keySet());
        for(String workerID: taskOneWorkIDList) {
            System.out.println("For woker: " + workerID);
            System.out.println("Task one: " + taskOneWorkIDToSentenceListIDs.get(workerID).toString());
            System.out.println("Task two: " + taskOneWorkIDToSentenceListIDs.get(workerID).toString());
        }

        LinkedHashMap<Integer, Integer> numberOfAssignmentOfEachSentenceList = new LinkedHashMap<>();
        qualifiedWorkerIDToInfo.forEach((k, v) -> {
            System.out.println("For workerID: " + k);
            System.out.println("Task one qualifcation assignment:");
            String[] columns = splitIntoColumns(v);
            LinkedList<Integer> taskOneSentenceListIDs = taskOneWorkIDToSentenceListIDs.get(k);
            for (Integer i : taskOneSentenceListIDs) {
                columns[setIndices[i]] = "\"1\"";
                System.out.println("Set_" + i + " assigned at index: " + columns[setIndices[i]]);
                if (numberOfAssignmentOfEachSentenceList.containsKey(i)) {
                    numberOfAssignmentOfEachSentenceList.put(i, numberOfAssignmentOfEachSentenceList.get(i) + 1);
                } else {
                    numberOfAssignmentOfEachSentenceList.put(i, 1);
                }
            }
            String newWorkerInfo = String.join(",", columns);
            qualifiedWorkerIDToInfo.put(k, newWorkerInfo);
        });
        for (int i = 0; i < numberOfAssignmentOfEachSentenceList.size(); i++) {
            System.out.println("Set_" + i + " has been assigned " + numberOfAssignmentOfEachSentenceList.get(i) + " times");
        }

        final String NEW_LINE_SEPARATOR = "\n";
        final String COMMA_DELIMITER = ",";
        final String FILE_HEADER = workerInfoHeader;
        try {
            String outputFilename = "data/get_experiment_sentences/output/worker_info/"
                    + date
                    + "_qualified_worker_info.csv";
            File outputfile = new File(outputFilename);
            System.out.println("The file will be saved in: "
                    + outputfile.getPath());
            FileOutputStream is = new FileOutputStream(outputfile);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);
            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            LinkedList<String> qualifiedWorkerInfo = new LinkedList(qualifiedWorkerIDToInfo.values());

            for (int i = 0; i < qualifiedWorkerInfo.size(); i++) {
                //System.out.println(blockedWorkerInfo.get(i));
                w.append(qualifiedWorkerInfo.get(i));
                if (i != qualifiedWorkerInfo.size() - 1) {
                    w.append(NEW_LINE_SEPARATOR);
                }
            }
            System.out.println("CSV file was created successfully !!!");
            w.flush();
            w.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void fillTestWorkerIDList() {
        testWorkerIDList = new LinkedList<>();
        fillControlTestWorkerIDList();
        fillNaturalSentenceTestRunWorkerIDList();
        fillTranslationQualityTestRunWorkerIDList();
        /*
        testWorkerIDList.add("A11SWVGXLZCTQF");
        testWorkerIDList.add("AHLS6AZ1VTNH7");
        testWorkerIDList.add("A12V2IEWROX7ZQ");
        testWorkerIDList.add("A3VOMP0WOJTB4I");
        testWorkerIDList.add("A31QX0ZOIZVQO9");
        //already submited result before cancellation
        testWorkerIDList.add("A36P1ZQ0GYF567");
        testWorkerIDList.add("A3VMNOYU4TNDNS");
        testWorkerIDList.add("A3DKFBFK3FA3MA");
        //12042019 test run 10 test workers
        testWorkerIDList.add("A24JKHC4HTY6CD");
        testWorkerIDList.add("A2ASBOP6RG352M");
        testWorkerIDList.add("A1OUX27FOYXKZ9");
        testWorkerIDList.add("A1X5U8WTTXMS87");
        testWorkerIDList.add("A9WFXKAF1RF5Q");
        testWorkerIDList.add("A13O7MR3K9JTC4");
        testWorkerIDList.add("A19HCBT1EH8484");
        testWorkerIDList.add("A2Z5Z53HZL34C3");
        testWorkerIDList.add("A2HITRHQ441VHA");
        testWorkerIDList.add("AQ4PB1PVI9U7V");
         */
    }

    public LinkedList<String> getQualifiedWorkerIDList() {
        return qualifiedWorkerIDList;
    }

    public void setQualifiedWorkerIDList(LinkedList<String> qualifiedWorkerIDList) {
        this.qualifiedWorkerIDList = qualifiedWorkerIDList;
    }

    public void saveTestWorkerInfo(String version) {
        final String NEW_LINE_SEPARATOR = "\n";
        //final String COMMA_DELIMITER = ",";
        final String FILE_HEADER = workerInfoHeader;
        try {
            String outputFilename = "data/get_experiment_sentences/output/worker_info/"
                    + version
                    + "_test_worker_info.csv";
            File outputfile = new File(outputFilename);
            System.out.println("The file will be saved in: "
                    + outputfile.getPath());
            FileOutputStream is = new FileOutputStream(outputfile);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);
            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            LinkedList<String> testWorkerInfo = new LinkedList(testWorkerIDToInfo.values());

            for (int i = 0; i < testWorkerInfo.size(); i++) {
                //System.out.println(testWorkerInfo.get(i));
                w.append(testWorkerInfo.get(i));
                if (i != testWorkerInfo.size() - 1) {
                    w.append(NEW_LINE_SEPARATOR);
                }
            }
            System.out.println("CSV file was created successfully !!!");
            w.flush();
            w.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
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

    public LinkedList<String> selectControlTestWorker(int numberOfSelection, int score) {
        LinkedList<String> selectedWorkerIDs = new LinkedList<>();
        String[] headerColumns = splitIntoColumns(workerInfoHeader);
        System.out.println(headerColumns[12]);
        if (!qualifiedWorkerIDList.isEmpty()) {
            System.out.println("Size of qualifiedWorkerIDList: " + qualifiedWorkerIDList.size());
            if (qualifiedWorkerIDList.size() != qualifiedWorkerIDToInfo.size()) {
                System.out.println("qualifiedWorkerIDList and qualifiedWorkerInfoList do not have the same size.");
            }
            Random rand = new Random();
            //shuffle qualified worker id list
            Collections.shuffle(qualifiedWorkerIDList, rand);
            for (int i = 0; i < numberOfSelection; i++) {
                String currentWorkerID = qualifiedWorkerIDList.get(i);
                String currentWorkerInfo = qualifiedWorkerIDToInfo.get(currentWorkerID);
                String[] columns = splitIntoColumns(currentWorkerInfo);
                columns[11] = "\"\"";
                columns[12] = "\"" + score + "\"";
                currentWorkerInfo = String.join(",", columns);
                //Check the the row will have the same amount of items
                if (splitIntoColumns(currentWorkerInfo).length != columns.length) {
                    System.out.println("Not the same length!");
                }
                testWorkerIDToInfo.put(currentWorkerID, currentWorkerInfo);
            }
        }
        return selectedWorkerIDs;
    }

//    public void readQualifiedWorkerInfo(String version, String inputFilename, 
//            LinkedHashMap<String, LinkedList<Integer>> taskOneWorkerIDToSentenceListIDs, 
//            LinkedHashMap<String, LinkedList<Integer>> taskTwoWorkerIDToSentenceListIDs) {
//        
//        final String NEW_LINE_SEPARATOR = "\n";
//        final String COMMA_DELIMITER = ",";
//        final String FILE_HEADER = workerInfoHeader;
//        
//    }
    public void saveBlockedWorkerInfo(String version) {
        final String NEW_LINE_SEPARATOR = "\n";
        final String COMMA_DELIMITER = ",";
        final String FILE_HEADER = workerInfoHeader;
        try {
            String outputFilename = "data/get_experiment_sentences/output/worker_info/"
                    + version
                    + "_blocked_worker_info.csv";
            File outputfile = new File(outputFilename);
            System.out.println("The file will be saved in: "
                    + outputfile.getPath());
            FileOutputStream is = new FileOutputStream(outputfile);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);
            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            LinkedList<String> blockedWorkerInfo = new LinkedList(blockedWorkerIDToInfo.values());

            for (int i = 0; i < blockedWorkerInfo.size(); i++) {
                //System.out.println(blockedWorkerInfo.get(i));
                w.append(blockedWorkerInfo.get(i));
                if (i != blockedWorkerInfo.size() - 1) {
                    w.append(NEW_LINE_SEPARATOR);
                }
            }
            System.out.println("CSV file was created successfully !!!");
            w.flush();
            w.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fillWorkerInfoList(String inputFilename) {
        BufferedReader br;
        try {
            //initialize input file path
            File fileDir = new File("data/get_experiment_sentences/input/worker_info/"
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
            workerInfoHeader = br.readLine();
            String[] headerColumns = splitIntoColumns(workerInfoHeader);
            //header length
            int headerLength = headerColumns.length;
//            System.out.println("Worker info update block status: " + headerColumns[headerLength - 2]);
//            System.out.println("Worker info block reason: " + headerColumns[headerLength - 1]);
//            System.out.println("Worker info first update-set: " + headerColumns[headerLength - 4 - 2 * 24]);
//            System.out.println("Worker info last update-set: " + headerColumns[headerLength - 4]);

            System.out.println("Worker info file header length: " + headerLength);
            for (int i = 0; i < headerColumns.length; i++) {
                if (headerColumns[i].contains("UPDATE-set_")) {
                    String setInfo = headerColumns[i].substring(1, headerColumns[i].length() - 1);
                    String[] tokens = setInfo.split("_");
                    int assignIndex = Integer.parseInt(tokens[tokens.length - 1]);
                    //System.out.println("Assign set index: " + i + " for " + setInfo + " at " + assignIndex);
                    setIndices[assignIndex] = i;
                }
            }

            while ((line = br.readLine()) != null) {

                String[] columns = splitIntoColumns(line);
                //true if a different length is found
                if (columns.length != headerLength) {
                    System.err.println("The line below does not has " + headerLength + " items, but " + columns.length);
                    System.err.println(line);
                }

                //substring the leading " and trailing "  columns[x].substring(1, columns[x].length()-1
                String workerID = columns[0].substring(1, columns[0].length() - 1);
                //System.out.println(workerID);
                //uncomment this block for blocking workers
                /*
                if (blockedWorkerIDList.contains(workerID)) {                    
                    //"UPDATE BlockStatus"
                    columns[headerLength-2] = "\"Block\"";
                    //"BlockReason"
                    columns[headerLength-1] = "\"Do not meet the qualification for follow-up tasks. The worker will be unblocked after the follow-up tasks end.\"";
                    String workerInfo = String.join(",", columns);
                    blockedWorkerIDToInfo.put(workerID, workerInfo);
                }
                
                //unblock previous blocked worker
                if(columns[headerLength-3].equals("\"Blocked\"")) {
                    System.out.println(workerID + " unblocked ");
                    columns[headerLength-2] = "\"Unblock\"";
                }
                 */
                if (qualifiedWorkerIDList.contains(workerID)) {
                    qualifiedWorkerIDToInfo.put(workerID, line);
                }
                //true if the worker qualifcation score "1" is asigned to the given workerID
//                if (columns[columns.length - 7 - 2 * 24].equals("\"1\"")) {
//                    System.out.println(headerColumns[columns.length - 7 - 2 * 24] + ": " + columns[columns.length - 7 - 2 * 24]);
//                }

                //revoke all qualification for set_i
                //System.out.println("Worker info first update column (update_worker): " +columns[columns.length-6-2*24]);
                //System.out.println("Worker info last update column (update_set_0): " + columns[columns.length-4]);
                /*
                for (int i = columns.length - 6 - 2 * 24; i <= columns.length - 4; i += 2) {
                    columns[i] = "\"Revoke\"";
                }
                */
                String workerInfo = String.join(",", columns);
                allWorkerIDToInfo.put(workerID, workerInfo);

            }

            br.close();
            System.out.println("Finished reading " + inputFilename);
            System.out.println("Size of blockedWorkerIDToInfo: " + blockedWorkerIDToInfo.size());
            System.out.println("Size of qualifiedWorkerIDToInfo: " + qualifiedWorkerIDToInfo.size());
            System.out.println("Size of allWorkerIDToInfo: " + allWorkerIDToInfo.size());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //add workerID to blockedWorkerIDList, if the worker's English fluency is equal or less than 8 or Chinese fluency is not 10
    //else add workerID to qualifiedWorkerIDList, if the worker's English fluency is greater than 8 and Chinese fluency is 10
    public void fillQualifiedWorkerIDList() {
        BufferedReader br;
        String inputFileFolderPath = "data/get_experiment_sentences/input/qualification_task_result/";
        try {
            List<File> filesInFolder = Files.walk(Paths.get(inputFileFolderPath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            System.out.println("Start reading files in " + inputFileFolderPath);
            for (File filename : filesInFolder) {
                //initialize buffered reader br
                //create an input stream read to read bytes and decode them to characters
                //create a file input stream by opening a connection to an actual file and use decode "UTF-8"
                br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(filename), "UTF8"));

                //initialize a string to store each line in the input file
                String line = "";
                //header
                String fileHeader = br.readLine();
                String[] headerColumns = splitIntoColumns(fileHeader);
                //header length
                int headerLength = headerColumns.length;
                //System.out.println("File header length: " + headerLength);

                while ((line = br.readLine()) != null) {
                    String[] columns = splitIntoColumns(line);
                    //length should be header length -2 because apporve and reject column is missing
                    if (columns.length != headerLength - 2) {
                        System.out.println("The line below does not has " + (headerLength - 2) + "items, but " + columns.length);
                        System.out.println(line);
                    }

                    //substring the leading " and trailing "
                    int chineseFluency = Integer.parseInt(columns[28].substring(1, columns[28].length() - 1));
                    int englishFluency = Integer.parseInt(columns[34].substring(1, columns[34].length() - 1));
                    //System.out.println("Chinese Fluency: " + chineseFluency);
                    //System.out.println("English Fluency: " + englishFluency);
                    String assignmentStatus = columns[16];

                    String workerID = columns[15].substring(1, columns[15].length() - 1);
                    //System.out.println(workerID);

                    if (chineseFluency == 10 && englishFluency >= 9) {
                        if (!qualifiedWorkerIDList.contains(workerID) && !testWorkerIDList.contains(workerID) && !assignmentStatus.contains("Rejected")) {
                            System.out.println("Add: " + workerID);
                            qualifiedWorkerIDList.add(workerID);
                        } else if (qualifiedWorkerIDList.contains(workerID) && !assignmentStatus.contains("Rejected")) {
                            System.out.println("Repeated submission, need to reject " + workerID + " in qualification test result: " + filename);
                        } else if (testWorkerIDList.contains(workerID)) {
                            //System.out.println("Test worker " + workerID + " found in qualification test result: " + inputFilename);
                        }
                    }
                }
                
                br.close();
                System.out.println("Current size of qualifiedWorkerIDList: " + qualifiedWorkerIDList.size());
            }
            System.out.println("Finished reading qualifcaiton task results.");
            System.out.println("FInal size of qualifiedWorkerIDList: " + qualifiedWorkerIDList.size());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //add workerID to blockedWorkerIDList, if the worker's English fluency is equal or less than 8 or Chinese fluency is not 10
    //else add workerID to qualifiedWorkerIDList, if the worker's English fluency is greater than 8 and Chinese fluency is 10
    public void fillQualifiedWorkerIDList(String inputFilename) {
        BufferedReader br;
        try {
            //initialize input file path
            File fileDir = new File("data/get_experiment_sentences/input/qualification_task_result/"
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
            //System.out.println(headerColumns[16]);
            //System.out.println("qualifcation task result header length: " + headerLength);
            while ((line = br.readLine()) != null) {
                String[] columns = splitIntoColumns(line);
                //length should be header length -2 because apporve and reject column is missing
                if (columns.length != headerLength - 2) {
                    System.out.println("The line below does not has " + (headerLength - 2) + "items, but " + columns.length);
                    System.out.println(line);
                }

                //substring the leading " and trailing "
                int chineseFluency = Integer.parseInt(columns[28].substring(1, columns[28].length() - 1));
                int englishFluency = Integer.parseInt(columns[34].substring(1, columns[34].length() - 1));
                //System.out.println("Chinese Fluency: " + chineseFluency);
                //System.out.println("English Fluency: " + englishFluency);
                String assignmentStatus = columns[16];

                String workerID = columns[15].substring(1, columns[15].length() - 1);
                //System.out.println(workerID);

                if (chineseFluency == 10 && englishFluency >= 9) {
                    if (!qualifiedWorkerIDList.contains(workerID) && !testWorkerIDList.contains(workerID) && !assignmentStatus.contains("Rejected")) {
                        qualifiedWorkerIDList.add(workerID);
                    } else if (qualifiedWorkerIDList.contains(workerID) && !assignmentStatus.contains("Rejected")) {
                        System.out.println("Repeated submission, need to reject " + workerID + " in qualification test result: " + inputFilename);
                    } else if (testWorkerIDList.contains(workerID)) {
                        //System.out.println("Test worker " + workerID + " found in qualification test result: " + inputFilename);
                    }
                }
            }

            br.close();
            System.out.println("Finished reading " + inputFilename);
            System.out.println("Size of qualifiedWorkerIDList: " + qualifiedWorkerIDList.size());
            //System.out.println("Size of blockedWorkerIDList: " + blockedWorkerIDList.size());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
