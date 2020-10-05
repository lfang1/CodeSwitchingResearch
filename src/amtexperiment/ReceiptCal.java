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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculate cost details of tasks posted on Amazon Mechanical Turk.
 * @author Le
 */
public class ReceiptCal {
    LinkedHashMap<String, LinkedList<String[]>> turkIDToPayment = new LinkedHashMap<>();
    double prePaidSum = 0.0;
    double assignmentPaymentSum = 0.0;
    double feePaymentSum = 0.0;
    double approveRejectedSum = 0.0;
    double realSpend = 0.0;
    public ReceiptCal() {
        
    }
    
    public static void main (String[] args) {
        ReceiptCal rc = new ReceiptCal();
        rc.readTransactionHistory("data/get_experiment_sentences/input/receipt/Transactions_2019-11-01_to_2019-12-12.csv");
        rc.printReceipt("20191212");
        
    }
    
    public void printReceipt(String version) {
        final String NEW_LINE_SEPARATOR = "\n";
        final String COMMA_DELIMITER = ",";
        final String FILE_HEADER = "MTurk IDS"
                + COMMA_DELIMITER
                + "Date"
                + COMMA_DELIMITER
                + "Amount Paid out";
        try {
            String outputFilename = "data/get_experiment_sentences/input/receipt/"
                    + version
                    + "_receipt.csv";
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
            
            LinkedList<String> recipientIDs = new LinkedList<>(turkIDToPayment.keySet());
            Double totalSum = 0.0;
            
            for(String id : recipientIDs) {
                w.append(id);
                w.append(COMMA_DELIMITER);
                w.append(COMMA_DELIMITER);
                w.append(NEW_LINE_SEPARATOR);
                double total = 0.0;
                LinkedList<String[]> transcations = turkIDToPayment.get(id);
                for(String[] details : transcations) {
                    String date = details[0];
                    String amount = details[1];
                    total += Double.parseDouble(amount);
                    w.append(COMMA_DELIMITER);
                    w.append(date);
                    w.append(COMMA_DELIMITER);
                    w.append(amount);
                    w.append(NEW_LINE_SEPARATOR);
                }
                
                w.append("total:");
                w.append(COMMA_DELIMITER);
                w.append(String.valueOf(total));
                w.append(COMMA_DELIMITER);
                w.append(NEW_LINE_SEPARATOR);
                
                totalSum += total;
            }
            
            w.append("Sum of totals: ");
            w.append(COMMA_DELIMITER);
            w.append(String.valueOf(totalSum));
            w.append(COMMA_DELIMITER);
            w.append(NEW_LINE_SEPARATOR);
            
            w.append("Number of MTurkers: ");
            w.append(COMMA_DELIMITER);
            w.append(String.valueOf(recipientIDs.size()));
            w.append(COMMA_DELIMITER);

            System.out.println("CSV file was created successfully !!!");
            w.flush();
            w.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ReceiptCal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReceiptCal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReceiptCal.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void readTransactionHistory(String inputFilename) {
        BufferedReader br;
        
        try {
            br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(inputFilename), "UTF8"));

                //initialize a string to store each line in the input file
                String line = "";
                //header
                String workerInfoHeader = br.readLine();
                String[] headerColumns = workerInfoHeader.split(",");
                //Date Posted
                System.out.println(headerColumns[2]);
                //Transaction Type
                System.out.println(headerColumns[3]);
                //Recipient ID
                System.out.println(headerColumns[5]);
                //Amount
                System.out.println(headerColumns[6]);
                //int lineCounter = 0;
                while((line = br.readLine()) != null) {
                    //lineCounter++;
                    String[] columns = line.split(",");
                    String datePosted = columns[2];
                    String transactionType = columns[3];
                    String recipientID = columns[5];
                    double amount = Double.parseDouble(columns[6]);
                    String[] paymentDetails = new String[2];
                    paymentDetails[0] = datePosted;
                    paymentDetails[1] = String.valueOf(-amount);
                    
                    if(!turkIDToPayment.containsKey(recipientID)) {
                        turkIDToPayment.put(recipientID, new LinkedList<>());
                    }
                    if(!transactionType.equals("Prepayment")) {
                        if(amount - 0.0 > 0.0) {
                            System.err.println("The actual amount paid out is positive: " + amount);
                        }
                        turkIDToPayment.get(recipientID).add(paymentDetails);
                    }          
                    
//                    if(transactionType.equals("Prepayment")) {
//                        prePaidSum += amount;
//                    } else if (transactionType.equals("AssignmentPayment")) {
//                        assignmentPaymentSum += amount;
//                    } else if (transactionType.equals("FeePayment")) {
//                        feePaymentSum += amount;
//                    } else if (transactionType.equals("ApproveRejected")){
//                        approveRejectedSum += amount;
//                    } else {
//                        System.err.println("Unexpected transaction type: " + transactionType);
//                    }
                }
//                realSpend = assignmentPaymentSum + feePaymentSum + approveRejectedSum;
//                System.out.println("prePaidSum: " + prePaidSum);
//                System.out.println("assignmentPaymentSum: " + assignmentPaymentSum);
//                System.out.println("feePaymentSum: " + feePaymentSum);
//                System.out.println("approveRejectedSum: " + approveRejectedSum);
//                System.out.println("Read " + lineCounter + " lines, and real spend is: " + realSpend);
//                System.out.println("Current balance: " + (prePaidSum+realSpend));
            System.out.println("Size of turkIDToPayment: " + turkIDToPayment.size());
                
            br.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ReceiptCal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReceiptCal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReceiptCal.class.getName()).log(Level.SEVERE, null, ex);
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
