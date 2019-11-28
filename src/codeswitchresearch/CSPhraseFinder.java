/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeswitchresearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author Le
 */
public class CSPhraseFinder {

    //default constructor
    public CSPhraseFinder() {

    }

    //find the code-switch phrase by checking if some code-switch indices are continous
    public static void main(String[] args) {
        //test();
        testfindCSPhraseIndices();
    }

    //convert String[] to int[]
    public int[] convertStringArrayToIntArray(String[] stringIndices) {
        int[] indices = new int[stringIndices.length];
        //convert String[] to int[]
        for (int i = 0; i < stringIndices.length; i++) {
            indices[i] = Integer.parseInt(stringIndices[i]);
        }
        return indices;
    }

    //convert String[] to LinkedList<Integer>
    public LinkedList<Integer> convertStringArrayToIntegerLinkedList(String[] stringIndices) {
        LinkedList<Integer> indexList = new LinkedList<>();
        //convert String[] to int[]
        if (stringIndices.length == 1 && stringIndices[0].equals("")) {
            return indexList;
        }
        for (int i = 0; i < stringIndices.length; i++) {
            indexList.add(Integer.parseInt(stringIndices[i]));
        }
        return indexList;
    }
    
    //find indices of cs-segments
    public ArrayList<ArrayList<Integer>> findIndicesOfCSSegment(ArrayList<Integer> indicesOfCodeSwitchedWord, ArrayList<Integer> indicesOfPunctuation) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        int lastCSIndex = -1;
        ArrayList<Integer> indicesOfCSSegment = new ArrayList<>();
        for (int i = 0; i < indicesOfCodeSwitchedWord.size(); i++) {
            int currentCSIndex = indicesOfCodeSwitchedWord.get(i);
            //check if indicesOfCSSegment list is empty
            if (indicesOfCSSegment.isEmpty()) {
                indicesOfCSSegment.add(currentCSIndex);
            } else {
                //check if the currentCSIndex is next to the lastCSIndex
                if (currentCSIndex - lastCSIndex == 1) {
                    //check if the currentCSIndex is the index of a punctuation
                    if (!indicesOfPunctuation.contains(currentCSIndex)) {
                        indicesOfCSSegment.add(currentCSIndex);
                    } else {
                        result.add(indicesOfCSSegment);
                        indicesOfCSSegment = new ArrayList<>();
                    }
                } else {
                    result.add(indicesOfCSSegment);
                    indicesOfCSSegment = new ArrayList<>();
                    indicesOfCSSegment.add(currentCSIndex);
                }
            }
            lastCSIndex = currentCSIndex;
        }
        if (!indicesOfCSSegment.isEmpty()) {
            result.add(indicesOfCSSegment);
        }
        return result;
    }
    
    public static void testfindCSPhraseIndices() {
        String[] csStringIndices = {"0", "1", "2", "3", "4", "5", "6", "7", "9", "10"};
        String[] csPunctStringIndices = {"4", "7", "10"};
        CSPhraseFinder cspfinder = new CSPhraseFinder();
        LinkedList<LinkedList<Integer>> result = cspfinder.findCSPhraseIndices(csStringIndices, csPunctStringIndices);
        System.out.println("Expected result size: " + 3);
        System.out.println("Actual result size:   " + result.size());
        System.out.println("Expected segments: " + "[0, 1, 2, 3], [5, 6], [9]");
        System.out.println("Actual segments  : " + result.toString());

    }

    //Find cs-phrase and trailing punctuation is excluded
    public LinkedList<LinkedList<Integer>> findCSPhraseIndices(String[] csStringIndices, String[] csPunctStringIndices) {
        //initialize int array csIndices to an empty int arry with the same length of String array csIndicesString
        int[] csIndices = convertStringArrayToIntArray(csStringIndices);
        //convert int array to initialize integer linked list
        LinkedList<Integer> csPunctIndexList = convertStringArrayToIntegerLinkedList(csPunctStringIndices);
        //initialize a linked list to store the indices of cs phrase(s) that have been found
        LinkedList<LinkedList<Integer>> result = new LinkedList<>();
        //initialize a linked list to store the indices of one cs phrase
        LinkedList<Integer> csPhrase = new LinkedList<>();
        //initialize the index of the last cs-word
        int lastCSIndex = -1;
        //iterate the int array of cs indices
        for (int currentIndex : csIndices) {
            //check if the csPhrase list is empty
            if (csPhrase.isEmpty()) {
                //add the first cs index of cs-phrase to the csPhrase list
                csPhrase.add(currentIndex);
            } else {
                //check if the index of the current cs-word is next to the index of the last cs-word
                if (currentIndex == lastCSIndex + 1) {
                    //check if the current index is a punctuation index
                    if (!csPunctIndexList.contains(currentIndex)) {
                        //add current index to this cs-phrase index list
                        csPhrase.add(currentIndex);
                    }//else, the index list of a cs-phrase is completed
                    else {
                        //add the index list to the result
                        result.add(csPhrase);
                        //reset the csPhrase list to a new list
                        csPhrase = new LinkedList<>();
                    }
                } //else, the index list of a cs-phrase is completed
                else {
                    //add the index list to the result
                    result.add(csPhrase);
                    //reset the csPhrase list to a new list
                    csPhrase = new LinkedList<>();
                    //add the first cs-word in the new csPhrase
                    csPhrase.add(currentIndex);
                }
            }
            //assign currentIndex to lastCSIndex
            lastCSIndex = currentIndex;
        }
        //check if the csPhrase list is not empty
        if (!csPhrase.isEmpty()) {
            //add the final completed csPhrase to the result
            result.add(csPhrase);
        }
        return result;
    }

    public LinkedList<String> findCSPhrase(LinkedList<LinkedList<Integer>> csPhraseIndices, String[] words) {
        //initialize a linkedlist to store the finding result
        LinkedList<String> result = new LinkedList<>();
        //initialize a String Builder to store a cs-phrase
        StringBuilder csPhraseBuilder = new StringBuilder();
        //iterate the index list of csPhraseIndices list
        for (LinkedList<Integer> indices : csPhraseIndices) {
            for (int index : indices) {
                //append a word
                csPhraseBuilder.append(words[index]);
                //append one whitespace " "
                csPhraseBuilder.append(" ");
            }
            //convert the string builder to string and remove the leading and trailing whitespaces
            String csPhrase = csPhraseBuilder.toString().trim();
            //add the csPhrase string to the result;
            result.add(csPhrase);
            //reset the csPhraseBuilder to an new StringBuilder with no characters in it
            csPhraseBuilder = new StringBuilder();
        }
        return result;
    }

    //convert csPhrase list to a single string, each phrase is split by "_"
    public String listToString(LinkedList<String> csPhraseList) {
        StringBuilder resultStringBuilder = new StringBuilder();
        for (String csPhrase : csPhraseList) {
            resultStringBuilder.append(csPhrase);
            resultStringBuilder.append(",");
        }
        //delete the trailing ","
        resultStringBuilder.deleteCharAt(resultStringBuilder.length() - 1);
        String result = resultStringBuilder.toString();
        return result;
    }

    //test
    private static void test() {
        CSPhraseFinder cspfinder = new CSPhraseFinder();
        String[] csStringIndices = new String[]{"2", "3", "5", "6", "9", "10"};
        String[] csPunctStringIndicesOriginal = new String[]{"3", "10"};
        //String[] csPunctStringIndicesOriginal = new String[]{""};
        LinkedList<LinkedList<Integer>> csIndices = cspfinder.findCSPhraseIndices(csStringIndices, csPunctStringIndicesOriginal);
        String[] words = new String[]{"我", "吃", "apple", "，", "在", "Yummy", "Cafe", "的", "里面", "play", "!"};
        System.out.println("There are " + csIndices.size() + " cs-phrase(s)");
        for (LinkedList<Integer> indices : csIndices) {
            for (int index : indices) {
                System.out.print(index + "_");
            }
            System.out.println();
        }
        LinkedList<String> result = cspfinder.findCSPhrase(csIndices, words);
        System.out.println(cspfinder.listToString(result));
    }
}
