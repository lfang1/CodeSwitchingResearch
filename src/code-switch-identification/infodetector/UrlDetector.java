/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infodetector;

/**
 *
 * @author Le
 */
public class UrlDetector {
    public static boolean isUrl(String s) {
        String url = s.toLowerCase();
        if(url.contains("http")) {
            return true;
        } else if(url.contains("www")) {
            return true;            
        } else if(url.contains("com")) {
            return true;
        } else if(url.contains("edu")) {
            return true;
        } else if(url.contains("org")) {
            return true;
        } else if(url.contains("cn")) {
            return true;
        } else if(url.contains("net")) {
            return true;
        } else {
            return false;
        }
    }
}
