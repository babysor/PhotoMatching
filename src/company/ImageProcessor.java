package company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

/**
 * Created by babys_000 on 12/28/2014.
 */
public class ImageProcessor{
    final ImagesFinder controller;
    ImageProcessor(ImagesFinder control){
        controller = control;
    }

    protected void initImageData(){
        controller.preRecorded = loadData("temp_recordedImage");
    }
    /**
     * To process All Images in a directory
     * @param pathname
     */
    protected void processImageDirectory (String pathname){
        File directory = new File(pathname);
        for(File input:directory.listFiles(new FileFilter() {
            public boolean accept(File f) {
                String filename = f.getName();
                return filename.endsWith("jpg")||filename.endsWith("JPG");
            }
        })
                ){
            String filename = pathname+"\\"+input.getName();
            if(controller.preRecorded.containsKey((filename))) {
                controller.imagesRecorded.put(filename,controller.preRecorded.get(filename));  //add previous data hashcode
                continue;
            }
            BufferedImage imageBuffer = loadImage(input);
            imageBuffer = scaleImage(imageBuffer, controller.precision);
            String sourceHashCode = calculateHashcode(imageBuffer);
            controller.imagesRecorded.put(filename,sourceHashCode);
            System.out.print("file" + input.getName() + " has hashCode:" + sourceHashCode + "\n");
        }
        storeData("temp_recordedImage");
        return;

    }

    /**
     * To process single Image, for test
     * @param filename
     * @return BufferedImage
     */
    protected BufferedImage processImage (String filename){
        BufferedImage imageBuffer = loadImage(filename);
        if(imageBuffer==null)
            return null;
        imageBuffer = scaleImage(imageBuffer,controller.precision);
        String sourceHashCode = calculateHashcode(imageBuffer);
        controller.imagesRecorded.put(filename,sourceHashCode);
        System.out.print("file"+filename+" has hashCode:"+sourceHashCode+"\n");

        return imageBuffer;
    }

    protected static BufferedImage loadImage(String filename){
        try {
            File inputFile = new File(filename);
            return ImageIO.read(inputFile);
        }catch(IOException ioe){
            System.out.print("can't open the image file: "+filename+"\n");
            return null;
        }
    }
    protected static BufferedImage loadImage(File inputFile){
        try {
            return ImageIO.read(inputFile);
        }catch(IOException ioe){
            return new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
        }
    }

    private HashMap<String,String> loadData(String pathname){
        try {
            File inputFile = new File(pathname);
            FileInputStream f = new FileInputStream (inputFile);
            ObjectInputStream s = new ObjectInputStream (f);
            HashMap<String,String> pre = (HashMap<String,String>)s.readObject();
            s.close();
            f.close();
            return pre;

        }catch(IOException ioe){
            System.out.print("No previous data of image processing found");
            return new HashMap<String,String>();
        }catch(ClassNotFoundException cnfe){
            System.out.print("Failed to store previous of image processing, please check the file "+pathname+"\n");
            return new HashMap<String,String>();
        }
    }
    private void storeData(String pathname){
        try {
            File outputFile = new File(pathname);
            if(!outputFile.exists())
                outputFile.createNewFile();
            FileOutputStream f = new FileOutputStream(outputFile);
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(controller.imagesRecorded);
            s.close();
            f.close();
        }catch(IOException ioe){
            System.out.print("Failed to store data of image processing, please check the file "+pathname+"\n");
        }
    }
    protected BufferedImage scaleImage(BufferedImage imageBuffer,int longestEdge){
        int width = imageBuffer.getWidth(null);
        int height = imageBuffer.getHeight(null);
        if(width<height)
            height = width;
        else
            width = height;
        double scaleX= (double) longestEdge / width;
        double scaleY= (double) longestEdge / height;
        width = height= longestEdge;
        BufferedImage thumbImage= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D canvas = thumbImage.createGraphics();
        canvas.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        canvas.drawRenderedImage(imageBuffer, AffineTransform.getScaleInstance(scaleX, scaleY) );//AffineTransform.getScaleInstance(scaleX, scaleY)
        canvas.dispose();
        return thumbImage;
    }

    protected static String calculateHashcode(BufferedImage imageBuffer) {
        int width = imageBuffer.getWidth(null);
        int height = imageBuffer.getHeight(null);
        int[]pixels = new int[width * height];
        int countRed = 0;
        int countGreen = 0;
        int countBlue = 0;
        int countDark = 0;

        for (int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
//                pixels[i* height + j] = rgbToGray(imageBuffer.getRGB(i, j));
                int RGB = imageBuffer.getRGB(i, j);
                int _red = (RGB >> 16) & 0xFF;
                int _green = (RGB >> 8) & 0xFF;
                int _blue = (RGB) & 0xFF;
                if(_red+_green+_blue<100)
                    countDark ++;
                else if(_red-_green>(_red>>2)&&_red-_blue>(_red>>2))
                    countRed ++;
                else if(_green-_red>(_green>>2)&&_green-_blue>(_green>>2))
                    countGreen ++;
                else if(_blue-_green>(_blue>>3)&&_blue-_red>(_blue>>3))
                    countBlue ++;
                pixels[i* height + j] =  (int) (0.3 * _red + 0.59 * _green + 0.11 * _blue);
            }
        }
        int threshold = pixels.length>>3;

        int max = Math.max(countRed,Math.max(countGreen,Math.max(countBlue,countDark)));
        boolean majorNone = max<threshold;
        boolean majorRed = (!majorNone)&&countRed==max;
        boolean majorGreen = (!majorNone)&&countGreen==max;
        boolean majorBlue = (!majorNone)&&countBlue==max;
        boolean majorDark = (!majorNone)&&countDark==max;

        int avgPixel;
        int m = 0;
        for (int i =0; i < pixels.length; ++i) {
            m +=pixels[i];
        }
        m = m /pixels.length;
        avgPixel = m;

        int[] comps= new int[width * height];
        for (int i = 0; i < comps.length; i++) {
            if(pixels[i] >= avgPixel) {
                comps[i]= 1;
            }else {
                comps[i]= 0;
            }
        }

        StringBuffer hashCode = new StringBuffer();

        for (int i = 0; i < comps.length; i+= 4) {
            int result = (comps[i]<<3) + (comps[i + 1]<<2)+ (comps[i + 2]<<1) + comps[i + 3];//int result = (comps[i] <<3) + (comps[i + 1] <<2)+ (comps[i + 2]<<1) + comps[i + 3];
            hashCode.append(Integer.toHexString(result));//二进制转为16进制
        }
        /*
        version 0.2 Add new features: 8 more bits to record major color
         */
        int marjorColor = 16;
        while(marjorColor>0){
            if(majorNone){
                hashCode.append(0);
                marjorColor--;
                continue;
            }
            if(marjorColor>0&&majorRed){
                hashCode.append(1);
                marjorColor--;
            }
            if(marjorColor>0&&majorGreen){
                hashCode.append(2);
                marjorColor--;
            }
            if(marjorColor>0&&majorBlue){
                hashCode.append(3);
                marjorColor--;
            }
            if(marjorColor>0&&majorDark) {
                hashCode.append(4);
                marjorColor--;
            }

        }
        return hashCode.toString();
    }

    /**
     * RGB to Gray calcuation
     * @param pixels Red-Green-Blue values
     * @return int gray value
     */

    private static int rgbToGray(int pixels) {
        // int _alpha =(pixels >> 24) & 0xFF;
        int _red = (pixels >> 16) & 0xFF;
        int _green = (pixels >> 8) & 0xFF;
        int _blue = (pixels) & 0xFF;
        return (int) (0.3 * _red + 0.59 * _green + 0.11 * _blue);
    }
}