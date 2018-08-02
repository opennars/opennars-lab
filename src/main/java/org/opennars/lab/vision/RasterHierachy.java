/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.opennars.lab.vision;

import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.alg.filter.derivative.GradientSobel;
import boofcv.gui.image.VisualizeImageData;
import boofcv.io.webcamcapture.UtilWebcamCapture;
import boofcv.alg.misc.ImageMiscOps;
import boofcv.core.image.ConvertImage;
import boofcv.core.image.border.FactoryImageBorderAlgs;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.io.image.*;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import com.github.sarxos.webcam.Webcam;
//import georegression.struct.point.Point2D_I32;
import org.opennars.gui.NARSwing;
import org.opennars.main.Nar;
//import org.infinispan.commons.hash.Hash;

import javax.swing.*;
import java.awt.*;
import static java.awt.Color.gray;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import org.opennars.entity.Task;
import org.opennars.io.events.EventEmitter.EventObserver;
import org.opennars.io.events.OutputHandler.IN;
import org.opennars.language.Inheritance;
import org.opennars.language.Term;
import org.opennars.plugin.perception.VisionChannel;

/**
 *
 * @author Patrick Hammer
 */
public class RasterHierachy extends JPanel {
    /** The number of rasters to calculate. */
    int numberRasters;


    /** The dimensions of the input frame. */
    int frameWidth, frameHeight;

    /** The number of blocks to divide the coarsest raster into. */
    int divisions;

    /** The scaling factor for each raster in the hierarchy. */
    int scalingFactor;

    // The center of the region of focus
    //Point2D_I32 focusPoint = new Point2D_I32();

    /** Image for visualization */
    BufferedImage workImage;

    /** Window for visualization */
    JFrame window;

    int focusX = 0;
    int focusY = 0;

    /**
     * Configure the Raster Hierarchy
     *
     * @param numberRasters The number of rasters to generate
     * @param frameWidth The desired size of the input stream
     * @param frameHeight The desired height of the input stream
     * @param divisions The number of blocks to divide the coarsest grained raster into
     * @param scalingFactor The scaling factor for each raster in the heirarchy.
     */
    public  RasterHierachy(int numberRasters, int frameWidth, int frameHeight, int divisions, int scalingFactor)
    {
        this.numberRasters = numberRasters;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        this.divisions = divisions;
        this.scalingFactor = scalingFactor;

        // Set the default focus to the center
        this.setFocus(frameWidth/2, frameHeight/2);

        window = new JFrame("Hierarchical Raster Vision Representation");
        window.setContentPane(this);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }



    /**
     * Set the focus to the given location.  All rasters (other than the most coarse-grained) are centered on
     * this point.
     *
     * @param x The x-coordinate of the focal point
     * @param y The y-coordinate of the focal point
     */
    public void setFocus(int x, int y) {
        focusX = x;
        focusY = y;
    }

    int updaterate=1;
    int cnt=1;

    HashMap<Integer,Float> lastvalR=new HashMap<>();
    HashMap<Integer,Float> lastvalG=new HashMap<>();
    HashMap<Integer,Float> lastvalB=new HashMap<>();
    HashMap<Integer,Value> voter=new HashMap<>();

    public class Value
    {
        public int x;
        public int y;
        public int r;
        public double value;
        public Value(int r, int x, int y, double value) {
            this.x=x;
            this.y=y;
            this.r=r;
            this.value=value;
        }
    }

    boolean even = false;
    public BufferedImage rasterizeImage(BufferedImage input)
    {
        voter = new HashMap<>();
        boolean putin=false; //vladimir
        cnt--;
        if(cnt==0) {
            putin = true;
            cnt=updaterate;
        }

        int red, green, blue;
        int redSum, greenSum, blueSum;
        int x, y, startX, startY;
        int newX, newY;

        int width = input.getWidth();
        int height = input.getHeight();

        int blockXSize = width/divisions;
        int blockYSize = height/divisions;

        Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input,null,true,GrayU8.class);
        
        GrayU8 blurred = new GrayU8(image.width,image.height);
        GrayS16 derivX = new GrayS16(image.width,image.height);
        GrayS16 derivY = new GrayS16(image.width,image.height);

        GrayU8 unweighted = new GrayU8(image.width,image.height);
        ConvertImage.average(image,unweighted);
        
        boolean sobel = true;
        GrayU8 Result = new GrayU8(image.width,image.height);
        if(sobel) {
            // Gaussian blur: Convolve a Gaussian kernel
            BlurImageOps.gaussian(unweighted,blurred,-1, 5,null);

            // Calculate image's derivative
            GradientSobel.process(blurred, derivX, derivY, FactoryImageBorderAlgs.extend(unweighted));

            // display the results
            for(int i=0; i<image.width; i++) {
                for(int j=0; j<image.height; j++) {
                    Result.set(i, j, Math.max(derivX.get(i, j), derivY.get(i, j)));
                }
            }
        } else {
            CannyEdge<GrayU8,GrayS16> canny = FactoryEdgeDetectors.canny(5,true, true, GrayU8.class, GrayS16.class);

            // The edge image is actually an optional parameter.  If you don't need it just pass in null
            canny.process(unweighted,0.1f,0.2f,Result);
            for(int i=0; i<image.width; i++) {
                for(int j=0; j<image.height; j++) {
                    if(Result.get(i, j) != 0) {
                        Result.set(i, j, 255);
                    }
                }
            }
        }
        image.setBand(0, Result);
        image.setBand(1, Result);
        image.setBand(2, Result);
        
        Planar<GrayU8> output = new Planar<>(GrayU8.class, width, height, 3);

        
        BufferedImage rasterizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Set the initial raster region
        int regionWidth = width;
        int regionHeight = height;
        newX = 0;
        newY = 0;
        startX = 0;
        startY = 0;

        for (int step = 1; step <= numberRasters; step++) {

            // For each step we need to reduce the dimensions of the area that is pixelated and
            // also reduce the block size.

            if (step > 1) {
                newX = startX + (regionWidth - regionWidth / scalingFactor) / scalingFactor;
                newY = startY + (regionHeight - regionHeight / scalingFactor) / scalingFactor;
                if (newX < 0) {newX = 0;}
                if (newY < 0) {newY = 0;}

                regionWidth  = regionWidth/ scalingFactor;
                regionHeight = regionHeight/ scalingFactor;

                blockXSize = blockXSize/ scalingFactor;
                blockYSize = blockYSize/ scalingFactor;
                if (blockXSize < 1) {blockXSize = 1;}
                if (blockYSize < 1) {blockYSize = 1;}
            }

            // Set the starting point for the next step
            startX = focusX - ((regionWidth)/2);
            startY = focusY - ((regionHeight)/2);

            int pixelCount = blockXSize * blockYSize; // Number of pixels per block

            int blockXSizeCur = regionWidth/divisions;
            int blockYSizeCur = regionHeight/divisions;
            
            even = !even;
            int h=0,j=0;
            for (x = newX; x < ((step == 1 ? 0 : startX) + regionWidth); x += blockXSizeCur) {
                h++;
                j = 0;
                for (y = newY; y < ((step == 1 ? 0 : startY) + regionHeight); y += blockYSizeCur) {
                    j++;

                    if(j > resolution || h > resolution) {
                        continue;
                    }
                    redSum = 0;
                    greenSum = 0;
                    blueSum = 0;

                    for (int pixelX = 0; (pixelX < blockXSize) && (x + pixelX < width); pixelX++) {
                        for (int pixelY = 0; (pixelY < blockYSize) && (y + pixelY < height); pixelY++) {
                            redSum += image.getBand(0).get(x + pixelX, y + pixelY);
                            greenSum += image.getBand(1).get(x + pixelX, y + pixelY);
                            blueSum += image.getBand(2).get(x + pixelX, y + pixelY);
                        }
                    }

                    red = redSum / pixelCount;
                    green = greenSum / pixelCount;
                    blue = blueSum / pixelCount;

                    float fred = ((float) red) / 255.0f;
                    float fgreen = ((float) red) / 255.0f;
                    float fblue = ((float) red) / 255.0f;

                    //manage move heuristic
                    int brightness = (red+green+blue)/3; //maybe not needed
                    int key=step+10*x+10000*y;

                    if(lastvalR.containsKey(key) && putin) {

                        double area = blockXSize * blockYSize;
                        double diff = Math.abs(fred - (lastvalR.get(key))) + Math.abs(fgreen - (lastvalG.get(key))) + Math.abs(fblue - (lastvalB.get(key)));
                        double vote = diff * area;// / area;
                        // vote*=step;
                        voter.put(key, new Value(step, x + blockXSize / 2, y + blockYSize / 2, vote));
                    }
                    lastvalR.put(key, fred);
                    lastvalG.put(key, fgreen);
                    lastvalB.put(key, fblue);

                    if(putin && step==numberRasters) { //only most finest raster
                        int used_X = h-1;
                        int used_Y = j-1;
                        float USED_X = 2.0f*(used_X / (float) (res-1))-1.0f;
                        float USED_Y = 2.0f*(used_Y / (float) (res-1))-1.0f;
                        String st="<{M["+ String.valueOf(USED_X)+","+String.valueOf(USED_Y)+"]} --> [WHITE]>. :|: %"+String.valueOf(fred)+"%";
                        nar.addInput(st);
                    }

                    // Here we can generate NAL, since we know all of the required values.
                    if(true /*|| step == numberRasters*/) {
                        ImageMiscOps.fillRectangle(output.getBand(0), red, x, y, blockXSize, blockYSize);
                        ImageMiscOps.fillRectangle(output.getBand(1), green, x, y, blockXSize, blockYSize);
                        ImageMiscOps.fillRectangle(output.getBand(2), blue, x, y, blockXSize, blockYSize);
                    }
                }
            }
        }

        // search for maximum vote to move heuristic
        if(putin) {
            Value maxvalue = null;
            float threshold = 0.05f;
            for (Integer key : voter.keySet()) {
                Value value = voter.get(key);
                if (maxvalue == null || value.value > maxvalue.value) {
                    if (value.value > threshold)
                        maxvalue = value;
                }
            }

            if (maxvalue != null && maxvalue.x!=0 && maxvalue.y!=0) {
                this.setFocus((this.focusX+(maxvalue.x+regionWidth/2))/2, (this.focusY+(maxvalue.y+regionHeight/2))/2);
                chan.setFocus(this.focusX, this.focusY);
                // this.setFocus(maxvalue.x, maxvalue.y);
            }
        }

        ConvertBufferedImage.convertTo(output, rasterizedImage, true);
        return rasterizedImage;
    }

    /**
     * Invoke to start the main processing loop.
     */
    public void process() {
        Webcam webcam = UtilWebcamCapture.openDefault(frameWidth, frameHeight);

        // adjust the window size and let the GUI know it has changed
        Dimension actualSize = webcam.getViewSize();
        setPreferredSize(actualSize);
        setMinimumSize(actualSize);
        window.setMinimumSize(actualSize);
        window.setPreferredSize(actualSize);
        window.setVisible(true);

        BufferedImage input, buffered;

        workImage = new BufferedImage(actualSize.width, actualSize.height, BufferedImage.TYPE_INT_RGB);

        //int counter = 0;

        while( true ) {
            /*
             * Uncomment this section to scan the focal point across the frame
             * automatically - just for demo purposes.
             */
                /*
                int xx = this.focusPoint.getX();
                int yy = this.focusPoint.getY();
                xx += 1;
                if(xx > frameWidth)
                {
                    xx = 0;
                    yy += 1;
                    if (yy > frameHeight)
                        yy = 0;
                }
                this.setFocus(xx, yy);
                */
            input = webcam.getImage();

            synchronized( workImage ) {
                // copy the latest image into the work buffer
                Graphics2D g2 = workImage.createGraphics();

                buffered = this.rasterizeImage(input);
                g2.drawImage(buffered,0,0,null);
            }

            repaint();
        }
    }

    @Override
    public void paint (Graphics g) {
        if( workImage != null ) {
            // draw the work image and be careful to make sure it isn't being manipulated at the same time
            synchronized (workImage) {
                g.drawImage(workImage, 0, 0, null);
            }
        }
    }

    static int resolution = 20; //on change re-set res!!
    static int res = 0;
    static Nar nar;
    static VisionChannel chan = null;
    public static void main(String[] args) throws Exception {

        //RasterHierarchy rh = new RasterHierarchy(8, 640, 480, 12, 2);
        // RasterHierarchy rh = new RasterHierarchy(3, 640, 480, 5, 2);
        nar = new Nar();
        res = resolution; // determined according to resolution, don't change, but needs to be changed if resolution changes
        chan = new VisionChannel("WHITE", nar, nar, res, res, res*res, 0.5f, 12);
        nar.addPlugin(chan);
        nar.narParameters.VOLUME = 0;
        NARSwing.themeInvert();
        NARSwing swing = new NARSwing(nar);
        
        nar.event(new EventObserver() {
            @Override
            public void event(Class event, Object[] args) {
                if(event == IN.class) {
                    Task task = (Task) args[0];
                    Inheritance inh = (Inheritance) task.getTerm();
                    Term subj = inh.getSubject();
                    //TODO visualize re-detected prototype for the user
                }
            }
        }, true, IN.class);
        // nar.start(0);

        RasterHierachy rh = new RasterHierachy(2, 640, 480, resolution, 3); //new RasterHierachy(3, 640, 480, 8, 3);

        rh.process();
    }

    public int getNumberRasters() {
        return numberRasters;
    }

    public void setNumberRasters(int numberRasters) {
        this.numberRasters = numberRasters;
    }

    public int getDivisions() {
        return divisions;
    }

    public void setDivisions(int divisions) {
        this.divisions = divisions;
    }

    public int getScalingFactor() {
        return scalingFactor;
    }

    public void setScalingFactor(int scalingFactor) {
        this.scalingFactor = scalingFactor;
    }
}
