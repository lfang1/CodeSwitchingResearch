/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infodetector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Le
 */
public class AmericanPhoneNumberDetector {

    public static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) {
            return true;
        } //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) {
            return true;
        } //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) {
            return true;
        } //validating phone number where area code is in braces ()
        else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) {
            return true;
        } //return false if nothing matches the input
        else {
            return false;
        }
    }
    
        public static boolean hasPhoneNumber(String s) {
        //phone numbers of format "1234567890" 
        Pattern patternOne = Pattern.compile("\\d{10}");
        Matcher matcherOne = patternOne.matcher(s);
        //phone number with -, . or spaces
        Pattern patternTwo = Pattern.compile("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}");
        Matcher matcherTwo = patternTwo.matcher(s);
        //phone number with extension length from 3 to 5
        Pattern patternThree = Pattern.compile("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}");
        Matcher matcherThree = patternThree.matcher(s);
        //phone number where area code is in braces ()
        Pattern patternFour = Pattern.compile("\\(\\d{3}\\)-\\d{3}-\\d{4}");
        Matcher matcherFour = patternFour.matcher(s);
        //check if phone numbers of format "1234567890" found
        if (matcherOne.find()) {
            return true;
        } //check if phone number with -, . or spaces found
        else if (matcherTwo.find()) {
            return true;
        } //check if phone number with extension length from 3 to 5
        else if (matcherThree.find()) {
            return true;
        } //check if phone number where area code is in braces ()
        else if (matcherFour.find()) {
            return true;
        } //return false if nothing matches the input
        else {
            return false;
        }

    }

    public static String extractAmericanPhoneNumber(String s) {
        Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            System.err.println("The American phone number is not found!");
            return "";
        }
    }
}
