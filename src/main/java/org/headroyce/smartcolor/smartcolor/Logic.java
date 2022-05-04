package org.headroyce.smartcolor.smartcolor;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Random;

public class Logic {

    private Random rand;
    public Logic(){
        rand = new Random();
    }

    /**
     * Converts image to grayscale
     * @param sourceImage the image to convert
     * @return the new grayscale image
     */
    public Image toGrayScale(Image sourceImage) {
        PixelReader pixelReader = sourceImage.getPixelReader();

        int width = (int)sourceImage.getWidth();
        int height = (int)sourceImage.getHeight();

        WritableImage grayImage = new WritableImage(width, height);

        //Goes through each pixel of the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //Gets specific ARGB values of pixel
                int pixel = pixelReader.getArgb(x, y);

                //Converts ARGB values of pixel to grayscale by shifting special amounts to the right & white
                int alpha = ((pixel >> 24) & 0xff);
                int red = ((pixel >> 16) & 0xff);
                int green = ((pixel >> 8) & 0xff);
                int blue = (pixel & 0xff);

                //Special values for making something grayscale
                int grayLevel = (int)(0.2162 * red + 0.7152 * green + 0.0722 * blue);

                //Using special grayscale value to alter pixel's ARGB values to grayscale of the color
                int gray = (alpha << 24) + (grayLevel << 16) + (grayLevel << 8) + grayLevel;

                //Sets pixel to new grayscale color
                grayImage.getPixelWriter().setArgb(x, y, gray);
            }
        }

        return grayImage;
    }
}
