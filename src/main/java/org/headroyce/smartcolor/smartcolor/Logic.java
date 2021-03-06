package org.headroyce.smartcolor.smartcolor;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Logic {

    private Image img;
    private int width;
    private int height;
    private WritableImage wImg;
    private Image originalImg;
    private String saveFileFormat;
    private Color fillColor;
    private Color pixelColor;
    private PixelReader pixelReader;
    private boolean imgNotUploaded;

    /**
     * Creates the logic
     */
    public Logic(){
        fillColor = Color.WHITE;
        imgNotUploaded = true;
    }

    /**
     * Checks if there is no uploaded image
     * @return true if no image is uploaded, false otherwise
     */
    public boolean ImgIsNotUploaded(){
        return imgNotUploaded;
    }

    /**
     * Sets if the image has been uploaded or not
     * @param notUploaded if the image has not been uploaded
     */
    public void setImgNotUploaded( boolean notUploaded ){
        imgNotUploaded = notUploaded;
    }

    /**
     * Sets the image
     * @param img the image to set it to
     */
    public void setImg( Image img ){
        setImg(img, false);
    }

    /**
     * Sets the image and the width and heights of the image
     * Syncs the writable image to the image
     * @param img the image to set it to
     * @param replaceOriginal if this will override the original image
     */
    public void setImg( Image img, boolean replaceOriginal ){
        this.img = img;
        width = (int)img.getWidth();
        height = (int)img.getHeight();
        pixelReader = img.getPixelReader();
        syncWImg();
        if( replaceOriginal){
            Image image = img;
            originalImg = copyImage(image);
        }
    }

    /**
     * Makes a copy of an image
     * @param img the image to copy
     * @return a WritableImage copy of the image
     */
    public Image copyImage( Image img ){
        PixelReader pr = img.getPixelReader();
        WritableImage copy = new WritableImage((int)img.getWidth(), (int)img.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                copy.getPixelWriter().setArgb(x, y, pr.getArgb(x, y));
            }
        }
        return copy;
    }

    /**
     * Gets the width of the image
     * @return the width
     */
    public int getWidth(){ return width; }

    /**
     * Gets the height of the image
     * @return the height
     */
    public int getHeight(){ return height; }

    /**
     * Syncs the writable image to the image
     */
    public void syncWImg(){
        pixelReader = img.getPixelReader();
        wImg = new WritableImage(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                wImg.getPixelWriter().setArgb(x, y, pixelReader.getArgb(x, y));
            }
        }
    }

    /**
     * Syncs the changes of the image (with the writable image)
     */
    public void syncImg(){
        img = copyImage(wImg);
    }

    /**
     * Syncs the image and gets the image
     * @return the image
     */
    public Image getImg(){
        syncImg();
        return img;
    }

    /**
     * Resets the changes; sets the image to the original image
     */
    public void resetImg(){
        img = copyImage(originalImg);
        syncWImg();
    }

    /**
     * Sets the color to fill pixels with
     * @param c the color to set the fill to
     */
    public void setFill( Color c ){
        fillColor = c;
    }

    /**
     * Keeps track of the color of the pixel being changed
     * @param c the color the pixel is
     */
    public void setPixelColor( Color c ){
        pixelColor = c;
    }

    /**
     * Sets the format of the file when saving the image
     * @param s the string to set it to: jpeg, png, pdf
     */
    public void setSaveFile( String s ){
        saveFileFormat = s;
    }

    /**
     * Checks if @saveFileFormat is null
     * @return true if saveFileFormat is null, false otherwise
     */
    public boolean saveFileIsNull(){
        return saveFileFormat == null;
    }

    /**
     * Converts image to grayscale
     * @return the new grayscale image
     */
    public Image toGrayScale() {
        syncImg();
        syncWImg();
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

    /**
     * Recolors a certain colored area of the drawing
     * @param x the x coordinate of the pixel clicked
     * @param y the y coordinate of the pixel clicked
     */
    public void flood( int x, int y, int maxDepth ){
        if( maxDepth >= 0 && x >= 0 && y >= 0
                && x < wImg.getWidth()
                && y < wImg.getHeight() ){

            // color at point x, y
            Color c = wImg.getPixelReader().getColor(x, y);

            // if pixel has not already been filled and the color of the new pixel is similar
            if( !fillColor.equals(c)
                    && Math.abs(c.getGreen() - pixelColor.getGreen()) < .2
                    && Math.abs(c.getRed() - pixelColor.getRed()) < .2
                    && Math.abs(c.getBlue() - pixelColor.getBlue()) < .2 ){

                // change image pixel color
                wImg.getPixelWriter().setColor(x, y, fillColor);

                // floodfill in all possible directions
                if(y <= wImg.getHeight() - 1) flood( x, y + 1,maxDepth-1);
                if(x <= wImg.getWidth() - 1) flood(x + 1, y,maxDepth-1);
                if(x >= 1) flood(x - 1, y, maxDepth-1);
                if(y >= 1) flood( x, y - 1, maxDepth-1);
            }
        }
    }

    /**
     * Saves image to computer
     */
    public void saveImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files","PNG", "JPEG", "JPG", "*.png", "*.jpeg", "*.jpg"));
        fileChooser.setInitialFileName("savedimage." + saveFileFormat.toLowerCase());
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(saveFileFormat, saveFileFormat.toLowerCase()));

        File saveFile = fileChooser.showSaveDialog(null);
        if (saveFile != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(img, null), saveFileFormat.toLowerCase(), saveFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
