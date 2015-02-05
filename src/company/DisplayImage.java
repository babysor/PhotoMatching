package company;/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class demonstrates how to load an Image from an external file
 */
public class DisplayImage extends Component {

    BufferedImage img;
    int longestEdge = 256;
    double scaleX,scaleY;
    int width,height;

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(img, 0,0,width ,height, null);//AffineTransform.getScaleInstance(scaleX, scaleY),

    }

    public DisplayImage() {
        try {
            //Scale the photo
            img = ImageIO.read(new File("sample.jpg"));
            scale();
        } catch (IOException e) {
        }

    }
    public DisplayImage(BufferedImage input) {
        img = input;
        scale();
    }
    private final void scale(){
        width = img.getWidth(null);
        height = img.getHeight(null);
        scaleX= (double) longestEdge / width;
        scaleY= (double) longestEdge / height;
        if(scaleX > scaleY) {
            scaleX= scaleY;
        }else {
            scaleY= scaleX;
        }
        width = (int) (scaleX * width);
        height= (int) (scaleY * height);
    }

    public Dimension getPreferredSize() {
        if (img == null) {
            return new Dimension(256,256);
        } else {
            return new Dimension(width, height);
        }
    }

}
