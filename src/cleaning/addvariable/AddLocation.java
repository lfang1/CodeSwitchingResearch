/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package addvariable;

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
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class AddLocation {

    static LinkedList<String[]> sentenceList = new LinkedList<>();

    public static void main(String[] args) {
        readCSVFile();
        writeCSVFile();
    }

    public static void readCSVFile() {
        BufferedReader br = null;
        String line = "";
        String inputFilename = "11152019_appended_input_R_v4_new_15_11_2019";
        File fileDir = new File("data/add_location/input/"
                + inputFilename
                + ".csv");
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            //Read to skip header
            line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length < 22 || columns.length > 24) {
                    System.out.println("Wrong length: " + columns.length);
                } else {
                    String[] wordsInTranslation = columns[5].split("\\s+");
                    int sentenceLength = wordsInTranslation.length;
                    //Start at 1
                    int csWordID = Integer.valueOf(columns[6]);

                    String oneTwoOne = "";
                    String oneEightOne = "";
                    String threeFourThree = "";
                    String firstMiddleLast = "";
                    double percent = (double) csWordID / sentenceLength;
                    //"1" for first section, beginning of the sentence
                    //"2" for second section, middle of the sentence
                    //"3" for third section, end of the sentence
                    if (percent < 0.25 || percent - 0.25 == 0) {
                        oneTwoOne = "1";
                    } else if (percent > 0.25 && percent < 0.75) {
                        oneTwoOne = "2";
                    } else {
                        oneTwoOne = "3";
                    }

                    if (percent < 0.1 || percent - 0.1 == 0) {
                        oneEightOne = "1";
                    } else if (percent > 0.1 && percent < 0.9) {
                        oneEightOne = "2";
                    } else {
                        oneEightOne = "3";
                    }

                    if (percent < 0.3 || percent - 0.3 == 0) {
                        threeFourThree = "1";
                    } else if (percent > 0.3 && percent < 0.7) {
                        threeFourThree = "2";
                    } else {
                        threeFourThree = "3";
                    }

                    if (csWordID == 1) {
                        firstMiddleLast = "1";
                    } else if (csWordID == sentenceLength) {
                        firstMiddleLast = "3";
                    } else {
                        firstMiddleLast = "2";
                    }

                    String[] appended = new String[29];
                    System.arraycopy(columns, 0, appended, 0, columns.length);
                    if (appended[22] == null) {
                        appended[22] = "";
                    }
                    if (appended[23] == null) {
                        appended[23] = "";
                    }
                    appended[24] = oneTwoOne;
                    appended[25] = oneEightOne;
                    appended[26] = threeFourThree;
                    appended[27] = firstMiddleLast;
                    appended[28] = String.valueOf(sentenceLength);
                    sentenceList.add(appended);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AddLocation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddLocation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddLocation.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
                System.out.println("Read input file successfully.");
                System.out.println(sentenceList.size() + " sentences have been added.");
            } catch (IOException ex) {
                Logger.getLogger(AddLocation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void writeCSVFile() {
        try {
            //Delimiter used in CSV file
            final String COMMA_DELIMITER = ",";
            final String NEW_LINE_SEPARATOR = "\n";
            final String outputFilename = "11152019_location_appended_input_R_v4_new_15_11_2019";
            //CSV file header
            final String FILE_HEADER = "sent_type,"
                    + "university,"
                    + "sent_id,"
                    + "aligned_to,"
                    + "original_sentence,"
                    + "translation,"
                    + "word_id,"
                    + "first_cs_word_form,"
                    + "first_cs_word_translation,"
                    + "frequency_negative_ln_first_cs_word_trans,"
                    + "surprisal_first_cs_word_trans,"
                    + "pos_tag_first_cs_word_trans,"
                    + "governor_first_cs_word_trans,"
                    + "deprel_first_cs_word_trans,"
                    + "surprisal_values,"
                    + "average_surprisal,"
                    + "bilingual_corpus_frequency_negative_log_first_cs_word,"
                    + "bilingual_corpus_frequency_negative_log_first_cs_word_trans,"
                    + "length_first_cs_word_form,"
                    + "length_first_cs_word_trans,"
                    + "dependency_distance,"
                    + "if_it_is_root,"
                    + "if_previous_word_is_punctuation,"
                    + "surprisal_of_previous_word,"
                    + "25_50_25_percent_location,"
                    + "10_80_10_percent_location,"
                    + "30_40_30_percent_location,"
                    + "first_middle_last_location,"
                    + "translation_sentence_length";
            File outputDir = new File("data/add_location/output/"
                    + outputFilename
                    + ".csv");
            System.out.println("The file will be saved in: "
                    + outputDir.getPath());
            FileOutputStream is = new FileOutputStream(outputDir);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            w.append(FILE_HEADER);
            w.append(NEW_LINE_SEPARATOR);
            for (String[] sentenceDetails : sentenceList) {
                for (int i = 0; i < sentenceDetails.length - 1; i++) {
                    w.append(sentenceDetails[i]);
                    w.append(COMMA_DELIMITER);
                }
                w.append(sentenceDetails[sentenceDetails.length - 1]);
                w.append(NEW_LINE_SEPARATOR);
            }

            w.flush();
            w.close();
            System.out.println("The file has been saved.");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddLocation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AddLocation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddLocation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
