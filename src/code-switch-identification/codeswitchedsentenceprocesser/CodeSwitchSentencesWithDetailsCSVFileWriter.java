/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchedsentenceprocesser;

import sentenceprocesser.MarkedSentence;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 *
 * @author Le
 */
public class CodeSwitchSentencesWithDetailsCSVFileWriter {

    public static void saveToCSVFile(String corpusName, HashMap<Integer, MarkedSentence> markedSentencesMap)
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
                + "ChineseWordCount,"
                + "EnglishWordCount,"
                + "PunctuationCount,"
                + "IndicesOfChineseWord,"
                + "IndicesOfCodeSwitchedWord,"
                + "IndicesOfPunctuation";

        //String corpusName value is one of the following:
        //psucssa
        //cmucssa
        //pittcssa
        try {
            File outputfileName = new File("data/code-switched-sentences-with-details/"
                    + corpusName
                    + "-bilingual-corpus.csv");
            System.out.println("The file will be saved in: "
                    + outputfileName.getPath());
            FileOutputStream is = new FileOutputStream(outputfileName);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            //Write the CSV file header
            w.append(FILE_HEADER);

            //Add a new line separator after the header
            w.append(NEW_LINE_SEPARATOR);

            markedSentencesMap.forEach((id, markedSentence) -> {
                try {
                    w.append(String.valueOf(id));
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(markedSentence.getWholeSentence()));
                    w.append(COMMA_DELIMITER);
                    int codeSwitchingOccurTimes = markedSentence.getCodeSwitchedPhrases().size();
                    w.append(String.valueOf(codeSwitchingOccurTimes));
                    w.append(COMMA_DELIMITER);
                    markedSentence.getCodeSwitchedPhrases().forEach(phrase -> {
                        try {
                            w.append(phrase);
                            w.append(COMMA_DELIMITER);
                            w.append("");
                            w.append(COMMA_DELIMITER);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                    for (int i = 0; i < (6 - codeSwitchingOccurTimes); i++) {
                        w.append("");
                        w.append(COMMA_DELIMITER);
                        w.append("");
                        w.append(COMMA_DELIMITER);
                    }
                    w.append(String.valueOf(markedSentence.getChineseWordCount()));
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(markedSentence.getEnglishWordCount()));
                    w.append(COMMA_DELIMITER);
                    w.append(String.valueOf(markedSentence.getPunctuationCount()));
                    w.append(COMMA_DELIMITER);
                    //replace ',' by '_' or ' '  remove " ' space
                    w.append(markedSentence.getIndicesOfChineseWord().toString().replace(",", "_").replace(" ", ""));
                    w.append(COMMA_DELIMITER);
                    w.append(markedSentence.getIndicesOfCodeSwitchedWord().toString().replace(",", "_").replace(" ", ""));
                    w.append(COMMA_DELIMITER);
                    if (markedSentence.getIndicesOfPunctuation().isEmpty()) {
                        w.append("");
                    } else {
                        w.append(markedSentence.getIndicesOfPunctuation().toString().replace(",", "_").replace(" ", ""));
                    }
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
                    + "data/code-switched-sentences-with-details/"
                    + corpusName
                    + "-bilingual-corpus.csv");
        }
    }
}
