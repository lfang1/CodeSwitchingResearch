/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import codeswitchedsentenceprocesser.CodeSwitchedSentencesForTranslationCSVFileWriter;
import infodetector.AmericanPhoneNumberDetector;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class EmailAndPhoneCSVFileWriter {

    static HashMap<String, String> emailAddressToId = new HashMap<>();
    static HashMap<String, String> emailIdToAddress = new HashMap<>();
    static HashMap<Integer, String> sentenceIdToEmailId = new HashMap<>();

    static HashMap<String, String> phoneNumberToId = new HashMap<>();
    static HashMap<String, String> phoneIdToNumber = new HashMap<>();
    private static HashMap<Integer, String> sentenceIdToPhoneId = new HashMap<>();

    //Create List for holding CodeSwitchedSentence objects
    private static List<CodeSwitchedSentence> sentenceList = new ArrayList<CodeSwitchedSentence>();
    //psucssa
    //cmucssa
    //pittcssa
    private static String corpusName = "pittcssa";

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        readShortCSVFile();
        saveToCSVFile(corpusName, sentenceList);
        EmailMapCSVFileWriter.saveToCSVFile("10252018-" + corpusName, emailIdToAddress);
        PhoneMapCSVFileWriter.saveToCSVFile("10252018-" + corpusName, phoneIdToNumber);
    }

    public static void readShortCSVFile() {
        BufferedReader br = null;
        //Delimiters used in the CSV file
        final String COMMA_DELIMITER = ",";

        try {
            //Reading the csv file
            File fileDir = new File("data/short-csv/10242018-"
                    + corpusName
                    + "-short.csv");
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
                    System.out.println("SentenceId: " + sentenceDetails[0]);
                    System.out.println("sentenceDetails.length:" + sentenceDetails.length);
                    int sentenceId = Integer.parseInt(sentenceDetails[0]);
                    String words = getEmailAndPhone(sentenceId, sentenceDetails[1]);
                    String translatedSentence = "";
                    if (sentenceDetails.length == 2) {
                        translatedSentence = getEmailAndPhone(sentenceId, "");
                    } else {
                        translatedSentence = getEmailAndPhone(sentenceId, sentenceDetails[2]);
                    }

                    //Save the employee details in Employee object
                    CodeSwitchedSentence s = new CodeSwitchedSentence(sentenceId,
                            words, translatedSentence);
                    sentenceList.add(s);
                }
            }

            //Lets print the CodeSwitchedSentence List
            for (CodeSwitchedSentence s : sentenceList) {
                System.out.println(s.getSentenceId() + "   " + s.getUnmarkedWordsInSentence() + "   "
                        + s.getTranslatedSentence());
            }

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

    public static String getEmailAndPhone(int sentenceId, String text) {
        if (text.isEmpty()) {
            System.out.println("The current text is empty!");
            return "";
        }
        if (text.equals(null)) {
            System.out.println("The current text is null!");
            return "";
        }

        String newText = "";
        for (String next : text.split("\\s")) {
            if (next.contains("@")) {
                System.out.println("An email address found: " + next);
                System.out.println("It is in sentenceId: " + sentenceId);
                String emailId = "";
                if (emailAddressToId.containsKey(next)) {
                    emailId = emailAddressToId.get(next);
                } else {
                    emailId = "em@" + emailAddressToId.size();
                    emailAddressToId.put(next, emailId);
                    emailIdToAddress.put(emailId, next);
                    sentenceIdToEmailId.put(sentenceId, emailId);
                }
                next = next.replace(next, emailId);
                System.out.println("The email addresss is replaced: " + next);
            } else if (AmericanPhoneNumberDetector.validatePhoneNumber(next)) {
                System.out.println("A phone number found: " + next);
                System.out.println("It is in sentenceId: " + sentenceId);
                String phoneId = "";
                if (phoneNumberToId.containsKey(next)) {
                    phoneId = phoneNumberToId.get(next);
                } else {
                    phoneId = "pn-" + phoneNumberToId.size();
                    phoneNumberToId.put(next, phoneId);
                    phoneIdToNumber.put(phoneId, next);
                    sentenceIdToPhoneId.put(sentenceId, phoneId);
                }
                next = next.replace(next, phoneId);
                System.out.println("The phone number is replaced: " + next);
            }
            newText = newText + " " + next;
        }
        return newText;
    }

    public static void saveToCSVFile(String corpusName, List<CodeSwitchedSentence> cssList)
            throws FileNotFoundException, UnsupportedEncodingException {

        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        //CSV file header

        final String FILE_HEADER = "SentenceID,"
                + "CodeSwitchedSentence,"
                + "TranslatedSentence";

        try {
            File outputfileName = new File("data/short-csv/"
                    + corpusName
                    + "-email-phone-replaced.csv");
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
                    w.append(css.getTranslatedSentence());
                    w.append(NEW_LINE_SEPARATOR);

                } catch (IOException ex) {
                    Logger.getLogger(CodeSwitchedSentencesForTranslationCSVFileWriter.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            };

            System.out.println("CSV file was created successfully !!!");

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (IOException e) {
            System.err.println("Problem writing to the "
                    + "data/short-csv/"
                    + corpusName
                    + "-email-phone-replaced.csv");
        }
    }

}
