/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le
 */
public class GenerateHTMLTable {

    public static void main(String[] args) {
        saveToTaskOneHTMLFile(48);
        saveToTaskTwoHTMLFile(28);
    }

    public static void saveToTaskOneHTMLFile(int numberOfTables) {
        try {
            String outputFilename = "data/get_experiment_sentences/output/11272019/11272019_task1_html_code" + ".txt";
            File outputfile = new File(outputFilename);
            System.out.println("The file will be saved in: "
                    + outputfile.getPath());
            FileOutputStream is = new FileOutputStream(outputfile);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);

            /*
            <p align="center"><font size="5">${sentence_0}</font></p>
 
            <table align="center" width="50%">
            <tr>
                <td align="center" width="40%">translation <br>翻译</td>
                <td algin="center" width="20%"><crowd-slider name="opinion_0" min="1" max="5" step="1" required></crowd-slider></td> 
                <td align="center" width="40%">not a translation <br>不是翻译</td>
            </tr>
            </table>
  
            <p></p>
            <hr align="center">
            <p></p>
            
             */
            String html = "";
            for (int i = 0; i < numberOfTables; i++) {
                html += "<p align=\"center\"><font size=\"5\">${sentence_" + i + "}</font></p>"
                        + "<table align=\"center\" width=\"50%\">"
                        + "<tr>"
                        + "<td align=\"center\" width=\"40%\">翻译</td>"
                        + "<td algin=\"center\" width=\"20%\"><crowd-slider name=\"opinion_" + i + "\" min=\"1\" max=\"5\" step=\"1\" value=\"3\" required></crowd-slider></td> "
                        + "<td align=\"center\" width=\"40%\">不是翻译</td>"
                        + "</tr>"
                        + "</table>";
                if (i != numberOfTables - 1) {
                    html += "<p><br></p>"
                            + "<hr align=\"center\">"
                            + "<p><br></p>";
                }
            }
            w.append(html);
            //System.out.println("CSV file was created successfully !!!");
            w.flush();
            w.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GenerateHTMLTable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateHTMLTable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateHTMLTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void saveToTaskTwoHTMLFile(int numberOfTables) {
        /*
        <!-- The original sentence will be substituted for the "sentence_0" variables when you publish a CSV batch with multiple translations -->
        <p align="center">Original sentence 原本的句子： <font size="5">${sentence_0}</font></p>
        <!-- The translated sentence will be substituted for the "translation_0"
               and "translated_text" variables here and below when you publish a CSV batch with multiple translations -->
        <p align="center">Translated sentence 翻译的句子： <font size="5">${translation_0}</font></p>

        <p><br></p>

        <div>
          <p align="center">The <u><strong>translated</strong></u> sentence <strong>adequately expresses the meaning</strong> of the <u><strong>original</strong></u> sentence.
              <br><u><strong>翻译的</strong></u>句子<strong>充分地</strong>表达了<u><strong>原本的</strong></u>句子的含义。</p>
          <table align="center" width="50%">
              <tr>
              <td align="center" width="40%">Strongly disagree<br>强烈反对</td>
              <td algin="center" width="20%"><crowd-slider name="adequacy_opinion_0" min="1" max="5" step="1" value="3" required></crowd-slider></td>
              <td align="center" width="40%">Strongly agree<br>完全同意</td>
              </tr>
          </table>

            <p><br></p>

            <p align="center">The <u><strong>translated</strong></u> sentence is <strong>fluent</strong>.
                <br><u><strong>翻译的</strong></u>句子十分<strong>流利</strong>。</p>
            <table align="center" width="50%">
                <tr>
                <td align="center" width="40%">Strongly disagree<br>强烈反对</td>
                <td algin="center" width="20%"><crowd-slider name="fluency_opinion_0" min="1" max="5" step="1" value="3" required></crowd-slider></td>
                <td align="center" width="40%">Strongly agree<br>完全同意</td>
                </tr>
            </table>
        </div>
        
        <p><br></p><hr align="center"><p><br></p>
         */
        try {
            String outputFilename = "data/get_experiment_sentences/output/11272019/11272019_task2_html_code" + ".txt";
            File outputfile = new File(outputFilename);
            System.out.println("The file will be saved in: "
                    + outputfile.getPath());
            FileOutputStream is = new FileOutputStream(outputfile);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter w = new BufferedWriter(osw);
            String html = "";

            for (int i = 0; i < numberOfTables; i++) {
                html += "<p align=\"center\">Original sentence 原本的句子： <font size=\"5\">${sentence_" + i + "}</font></p>"
                        + "<p align=\"center\">Translated sentence 翻译的句子： <font size=\"5\">${translation_" + i + "}</font></p>"                  
                        + "<p><br></p>"
                        + "<div>"
                        + "<p align=\"center\">The translated sentence adequately expresses the meaning of the original sentence." 
                        + "<br>翻译的句子充分地表达了原本的句子的含义。</p>" 
                        + "<table align=\"center\" width=\"50%\">" 
                        + "<tr>" 
                        + "<td align=\"center\" width=\"40%\">Strongly disagree<br>强烈反对</td>" 
                        + "<td algin=\"center\" width=\"20%\"><crowd-slider name=\"adequacy_opinion_" + i + "\" min=\"1\" max=\"5\" step=\"1\" value=\"3\" required></crowd-slider></td>" 
                        + "<td align=\"center\" width=\"40%\">Strongly agree<br>完全同意</td>" 
                        + "</tr>" 
                        + "</table>" 
                        + "<p><br></p>"
                        + "<p align=\"center\">The translated sentence is fluent." 
                        + "<br>翻译的句子十分流利。</p>" 
                        + "<table align=\"center\" width=\"50%\">" 
                        + "<tr>" 
                        + "<td align=\"center\" width=\"40%\">Strongly disagree<br>强烈反对</td>" 
                        + "<td algin=\"center\" width=\"20%\"><crowd-slider name=\"fluency_opinion_" + i + "\" min=\"1\" max=\"5\" step=\"1\" value=\"3\" required></crowd-slider></td>" 
                        + "<td align=\"center\" width=\"40%\">Strongly agree<br>完全同意</td>" 
                        + "</tr>" 
                        + "</table>"
                        + "</div>";
                if (i != numberOfTables - 1) {
                    html += "<p><br></p>"
                            + "<hr align=\"center\">"
                            + "<p><br></p>";
                }
            }
            w.append(html);
            //System.out.println("CSV file was created successfully !!!");
            w.flush();
            w.close();

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GenerateHTMLTable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateHTMLTable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateHTMLTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
