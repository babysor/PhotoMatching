package company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by babys_000 on 12/25/2014.
 */
public class UserInterface extends JPanel  {//


    protected UserInterface(List<String> input) {
        super();
        for(String s:input) {
            add(new JLabel(new ImageIcon(s)));
        }
    }
    protected UserInterface(String bi, List<SearchResult> input) throws IOException{
        super();
        this.setLayout(new GridLayout(0, 2));
        BufferedImage img = (ImageIO.read(new File(bi)));
        JLabel JLabel0 = new JLabel(new ImageIcon(img.getScaledInstance(256, 256,
                Image.SCALE_SMOOTH)));
        JLabel0.setText( "Searched Image");
        add(JLabel0);

        this.add(new JSeparator());
        for(SearchResult s:input) {
            BufferedImage dimg = (ImageIO.read(new File(s.filename)));
            JLabel newJLabel = new JLabel(new ImageIcon(dimg.getScaledInstance(256, 256,
                    Image.SCALE_SMOOTH)));

            //newJLabel.setText( "Match: "+ s.matchRatio + "%\n" );
            JLabel text = new JLabel();
            text.setText( "<html><font color='red'><center>MatchRaito:" + s.matchRatio +"%<br>Location<br>"+s.filename.substring(s.filename.lastIndexOf("\\")) +"<center></font></html>" );
            text.setLocation(20, 20);
            text.setSize(text.getPreferredSize());
            //newJLabel.setPreferredSize(new java.awt.Dimension(256, 256));
            newJLabel.add(text);
            this.add(newJLabel);



        }
    }
}
