package company;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
/*
Version 0.2 update:
    1. Seperate the ImageProcessor. (loosing some access restrictions)
    2. MajorColor: black(0)+red(1): 010101 in hashCode
 */

public class ImagesFinder {
    protected final int precision = 16;
    private ImageProcessor ip;
    public static void main(String[] args) throws IOException{
        JFrame f = new JFrame("ImagesFinder");
        ImagesFinder iFinder = new ImagesFinder();
        iFinder.ip = new ImageProcessor(iFinder);
        iFinder.ip.initImageData();
        iFinder.ip.processImageDirectory(".");

        iFinder.preRecorded.clear();

        String fileToSearch = "./toSearch/IMG_1823.jpg";
        UserInterface id = new UserInterface(fileToSearch,iFinder.findSimilar(fileToSearch,10,10));
        f.add(id);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    ImagesFinder(){
        imagesRecorded = new HashMap<String,String>();
    }
    private String findSimilar(String filename){
        BufferedImage imageBuffer = ip.loadImage(filename);
        if(imageBuffer==null)
            return "Stop!\n";
        imageBuffer = ip.scaleImage(imageBuffer,precision);
        String sourceHashCode = ip.calculateHashcode(imageBuffer);
        String result = searchBest(sourceHashCode);
        if(result.length()==0){
            return "No similar\n";
        }else{
            return result;
        }
    }
    private List<SearchResult> findSimilar(String filename,int threshold,int maximumDisplay){
        BufferedImage imageBuffer = ip.loadImage(filename);
        if(imageBuffer==null)
            return new ArrayList <SearchResult>();
        imageBuffer = ip.scaleImage(imageBuffer,precision);
        String sourceHashCode = ip.calculateHashcode(imageBuffer);
        return searchReturnList(sourceHashCode,threshold,maximumDisplay);

    }

    private String searchBest(String input){
        String result = "";
        int min = Integer.MAX_VALUE;
        int length = input.length();
        for(String filename:imagesRecorded.keySet()) {
            int diff = editDistance(input,imagesRecorded.get(filename));
            System.out.print(filename+" match percentage:  "+(double)(length-diff)/length*100+"%\n");
            if(diff<min) {
                min = diff;
                result= filename;
            }
        }

        return result;
    }

    private List<SearchResult> searchReturnList(String input,int threshold,int maximumCount){
//        List<String> result = new ArrayList <String>();
        List<SearchResult> resultSorted = new ArrayList <SearchResult>();
        int length = input.length();
        for(Map.Entry<String,String> f:imagesRecorded.entrySet()) {
            int similar = (int)((double)(length-editDistance(input,f.getValue()))/length*100);
            if(similar>=threshold) {
                resultSorted.add(new SearchResult(f.getKey(),similar));
            }
        }
        Collections.sort(resultSorted, new Comparator() {
            @Override
            public int compare(Object Inter1, Object Inter2) {     //Not safe enough
                return ((SearchResult)Inter2).matchRatio-(((SearchResult)Inter1).matchRatio); //descending
            }
        });
        if(resultSorted.size()<maximumCount)
            maximumCount = resultSorted.size();
        return resultSorted.subList(0,maximumCount);
    }


    /**
     * Calculate special editDistance (only the character has more 2 difference than another one)
     * @param s1
     * @param s2
     * @return
     */
    public static int editDistance(String s1, String s2) {

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        //if (Math.abs(s1.charAt(i - 1) - s2.charAt(j - 1))>3)
                        if (Math.abs(Character.digit(s1.charAt(i - 1),16) - Character.digit(s2.charAt(j - 1),16) )>1)
//                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    protected HashMap<String,String> imagesRecorded;  //file name -- hash code
    protected HashMap<String,String> preRecorded;
}

class SearchResult {
    String filename;
    int matchRatio;
    SearchResult(String ss, int v){
        filename = ss;
        matchRatio = v;
    }
}